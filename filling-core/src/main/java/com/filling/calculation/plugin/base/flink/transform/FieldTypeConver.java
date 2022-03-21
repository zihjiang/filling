package com.filling.calculation.plugin.base.flink.transform;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamTransform;
import com.filling.calculation.flink.util.TableUtil;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class FieldTypeConver implements FlinkStreamTransform<Row, Row> {


    private JSONObject config;

    private static String SOURCE_FIELD_NAME = "source_field";

    private static String SOURCE_FIELD = null;

    private static String TARGET_FIELD_TYPE_NAME = "target_field_type";

    private static String TARGET_FIELD_TYPE = null;

    @Override
    public void processStream(FlinkEnvironment env, DataStream<Row> dataStream) {

        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();

        process(tableEnvironment);
    }

    private void process(TableEnvironment tableEnvironment) {

        String sql;
        // 判断参数是否为数组
        if (SOURCE_FIELD.startsWith("[")) {
            JSONArray _field = JSONArray.parseArray(SOURCE_FIELD);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < _field.size(); i++) {
                sb.append("CAST(");
                sb.append("`" + _field.getString(i) + "`");
                sb.append(" as ");
                sb.append(TARGET_FIELD_TYPE);
                sb.append(") as ");
                sb.append("`" + _field.getString(i) + "`");

                if (i >= (_field.size() - 1)) {
                    sb.append(" ");
                } else {
                    sb.append(", ");
                }
            }

            sql = "select  " + sb + ",* from {table_name}"
                    .replaceAll("\\{table_name}", config.getString(SOURCE_TABLE_NAME));
        } else {
            sql = "select " + "CAST(" + SOURCE_FIELD + " as " + TARGET_FIELD_TYPE + ")" + ", * from {table_name}"
                    .replaceAll("\\{table_name}", config.getString(SOURCE_TABLE_NAME));
        }

        tableEnvironment.createTemporaryView(config.getString(RESULT_TABLE_NAME), tableEnvironment.sqlQuery(sql));
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
        return CheckConfigUtil.check(config, SOURCE_FIELD_NAME, SOURCE_FIELD_NAME);
    }

    @Override
    public void prepare(FlinkEnvironment env) {
        TARGET_FIELD_TYPE = config.getString(TARGET_FIELD_TYPE_NAME);
        SOURCE_FIELD = config.getString(SOURCE_FIELD_NAME);
    }
}
