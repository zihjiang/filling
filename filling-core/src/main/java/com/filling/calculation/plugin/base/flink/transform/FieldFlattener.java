package com.filling.calculation.plugin.base.flink.transform;


import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamTransform;
import com.filling.calculation.flink.util.TableUtil;
import com.filling.calculation.plugin.base.flink.transform.scalar.ScalarFlattener;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FieldFlattener implements FlinkStreamTransform<Row, Row> {

    private JSONObject config;


    private static String SOURCE_FIELD_NAME = "source_field";

    @Override
    public void processStream(FlinkEnvironment env, DataStream<Row> dataStream) {
        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();
        process(tableEnvironment);
    }

    private void process(TableEnvironment tableEnvironment) {

        String FUNCTION_NAME = "flattener";
        String sql = "select * FROM {source_table_name} LEFT JOIN LATERAL  TABLE({function_name}(`_source`))  ON TRUE"
            .replaceAll("\\{source_table_name}", config.getString(SOURCE_TABLE_NAME))
            .replaceAll("\\{function_name}", FUNCTION_NAME)
            .replaceAll("\\{source_field}", config.getString(SOURCE_FIELD_NAME));

//        Table table = tableEnvironment.from(config.getString(SOURCE_TABLE_NAME))
//                .joinLateral(call("flattener", $("_id")));
//                );
        tableEnvironment.createTemporaryView(config.getString(RESULT_TABLE_NAME), tableEnvironment.sqlQuery(sql));
    }

    @Override
    public void registerFunction(FlinkEnvironment flinkEnvironment) {
        if (flinkEnvironment.isStreaming()){
            flinkEnvironment
                    .getStreamTableEnvironment()
                    .registerFunction("flattener",new ScalarFlattener());
        }
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
        return CheckConfigUtil.check(config, SOURCE_FIELD_NAME);
    }

    @Override
    public void prepare(FlinkEnvironment prepareEnv) {
//        fields = config.getObject(FIELDS, List.class);
//
//        TypeInformation[] types = new  TypeInformation[fields.size()];
//        for (int i = 0; i< types.length; i++){
//            types[i] = Types.STRING();
//        }
//        rowTypeInfo = new RowTypeInfo(types,fields.toArray(new String[]{}));
    }

    /**
     * 根据表名, 获取列集合
     *
     * @param table
     * @return
     */
    private List<Map<String, String>> getColumn(Table table) {

        List<Map<String, String>> columns = new ArrayList();
        for (String fieldName : table.getResolvedSchema().getColumnNames()) {
            Map<String, String> map = new HashMap();
            map.put(fieldName, table.getResolvedSchema().getColumn(fieldName).get().getDataType().toString());
            columns.add(map);
        }
        return columns;
    }


}

