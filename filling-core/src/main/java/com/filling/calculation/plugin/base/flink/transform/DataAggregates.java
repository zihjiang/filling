package com.filling.calculation.plugin.base.flink.transform;


import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamTransform;
import com.filling.calculation.flink.util.TableUtil;
import com.filling.calculation.plugin.base.flink.transform.watermark.DefaultWaterMark;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.Tumble;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.table.expressions.ExpressionParser;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.List;

import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.lit;

/**
 * @author zihjiang
 */
public class DataAggregates implements FlinkStreamTransform<Row, Row> {

    private static final String CUSTOM_FIELD = "custom.fields";
    private static final String GROUP_FIELDS = "group.fields";
    private static final String ROWTIME_SUFFIX = ".rowtime";
    private static final String WATERMARK_SUFFIX = "_watermark";

    private static String ROWTIME_WATERMARK_FIELD = "rowtime.watermark.field";

    private static String ROWTIME_WATERMARK_FIELD_MS = "rowtime.watermark.tumble.ms";

    private static String ROWTIME_WATERMARK_FIELD_DELAY_MS = "rowtime.watermark.tumble.delay.ms";

    private JSONObject config;

    private static List<String> SELECT_FIELDS= new ArrayList<>();

    private static List<String> CUSTOM_FIELDS= new ArrayList<>();

    String rowtimeWatermarkField = "";
    Long rowtimeWatermarkFieldMs;
    Long rowtimeWatermarkFieldDelayMs;

    @Override
    public void processStream(FlinkEnvironment env, DataStream<Row> dataStream) {
        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();
        DataStream<Row> dataStreamForWT = dataStream.assignTimestampsAndWatermarks(new DefaultWaterMark(rowtimeWatermarkField, rowtimeWatermarkFieldDelayMs));
         Table table = tableEnvironment.fromDataStream(
                 dataStreamForWT,
                 _(
                         getGroupField(rowtimeWatermarkField + ROWTIME_SUFFIX, getColumnByTable(tableEnvironment.from(config.getString(SOURCE_TABLE_NAME)), rowtimeWatermarkField))
                 )
         );
        Table result = table.filter(
                        $(rowtimeWatermarkField).isNotNull()
                        // define window
        ).window(Tumble.over(lit(rowtimeWatermarkFieldMs).millis()).on($(rowtimeWatermarkField)).as(rowtimeWatermarkField + WATERMARK_SUFFIX))
                // group by key and window
                .groupBy( _(getGroupField(rowtimeWatermarkField + WATERMARK_SUFFIX, SELECT_FIELDS)))
                .select(
                        _(getSelectField(rowtimeWatermarkField + WATERMARK_SUFFIX, SELECT_FIELDS))
                );

        tableEnvironment.createTemporaryView(config.getString(RESULT_TABLE_NAME), result);
    }

    @Override
    public void setConfig(JSONObject config) {
        this.config = config;
    }

    @Override
    public JSONObject getConfig() {
        return config;
    }


    @Override
    public CheckResult checkConfig() {
        return CheckConfigUtil.check(config,SOURCE_TABLE_NAME);
    }

    @Override
    public void prepare(FlinkEnvironment env) {

        SELECT_FIELDS = config.getObject(GROUP_FIELDS, List.class);

        if(config.getString(CUSTOM_FIELD) != null ) {
            CUSTOM_FIELDS = config.getObject(CUSTOM_FIELD, List.class);
        }

        rowtimeWatermarkField = config.getString(ROWTIME_WATERMARK_FIELD);
        rowtimeWatermarkFieldMs = config.getLong(ROWTIME_WATERMARK_FIELD_MS);
        rowtimeWatermarkFieldDelayMs = config.getLong(ROWTIME_WATERMARK_FIELD_DELAY_MS);
    }

    /**
     * 防止idea提示废弃
     * @param expr
     * @return
     */
    private Expression[] _(String expr) {

        List<Expression> expressions = ExpressionParser.parseExpressionList(expr);
        return expressions.toArray(new Expression[0]);
    }

    /**
     * 返回包括watermark在内的字段(适用于group后)
     * @param watermarkFieldName
     * @param fieldNames
     * @return
     */
    private String getGroupField(String watermarkFieldName,  List<String> fieldNames) {
        List<String> _fieldNames = new ArrayList<>(fieldNames);
        _fieldNames.add(watermarkFieldName);

        return String.join(",", _fieldNames);
    }

    /**
     * 返回包括watermark在内的字段(适用于select后)
     * @param watermarkFieldName
     * @param fieldName
     * @return
     */
    private String getSelectField(String watermarkFieldName,  List<String> fieldName) {
        StringBuffer sb = new StringBuffer();


        for (int i = 0; i < SELECT_FIELDS.size(); i++) {
            String field_name = SELECT_FIELDS.get(i);
            List funcs = config.getObject("group." + field_name + ".function", List.class);
            sb.append(field_name);
            sb.append(", ");

            // 如果没有group.*.function参数,
            if(funcs != null) {
                for(int i1=0; i1 < funcs.size(); i1++) {
                    String fun = funcs.get(i1).toString();
                    sb.append(field_name);
                    sb.append(".");
                    sb.append(fun);
                    sb.append(" as ");
                    sb.append(field_name);
                    sb.append("_");
                    sb.append(fun);
                    sb.append(",");
                }
            } else {
                String fun = "count";
                sb.append(field_name);
                sb.append(".");
                sb.append(fun);
                sb.append(" as ");
                sb.append(field_name);
                sb.append("_");
                sb.append(fun);
                sb.append(",");
            }

        }

        for (Object obj: CUSTOM_FIELDS) {
            String str = obj.toString();
            sb.append(config.getString("custom.field."+ str +".script"));
            sb.append(" as ");
            sb.append(str);
            sb.append(",");
        }

        sb.append(watermarkFieldName + ".start as " + watermarkFieldName + "_start, ");
        sb.append(watermarkFieldName + ".end as " + watermarkFieldName + "_end");

        System.out.println("getSelectField: " + sb);
        return sb.toString();
    }

    private List<String> getColumnByTable(Table table, String excludeFiledName) {
        List <String> columns = table.getResolvedSchema().getColumnNames();
        // 删除watermark字段
        columns.removeIf(s -> s.contains(excludeFiledName));
        //
        return columns;
    }
}


