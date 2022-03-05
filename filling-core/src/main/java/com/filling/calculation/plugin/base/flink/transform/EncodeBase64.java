package com.filling.calculation.plugin.base.flink.transform;


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

public class EncodeBase64 implements FlinkStreamTransform<Row, Row> {


    private JSONObject config;

    private static String SOURCE_FIELD_NAME = "source_field";

    private static String TARGET_FIELD_NAME = "target_field";

    @Override
    public DataStream<Row> processStream(FlinkEnvironment env, DataStream<Row> dataStream) {

        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();

        return (DataStream<Row>) process(tableEnvironment, dataStream, "stream");
    }

    private Object process(TableEnvironment tableEnvironment, Object data, String type) {

        String FUNCTION_NAME = "TO_BASE64";
        String sql = "select *,{function_name}(`{source_field}`) as `{target_field}` from {source_table_name}"
            .replaceAll("\\{source_table_name}", config.getString(SOURCE_TABLE_NAME))
            .replaceAll("\\{function_name}", FUNCTION_NAME)
            .replaceAll("\\{source_field}", config.getString(SOURCE_FIELD_NAME))
            .replaceAll("\\{target_field}", config.getString(TARGET_FIELD_NAME));

        Table table = tableEnvironment.sqlQuery(sql).dropColumns(config.getString(SOURCE_FIELD_NAME));
        return TableUtil.tableToDataStream((StreamTableEnvironment) tableEnvironment, table, false);
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
       return CheckConfigUtil.check(config,SOURCE_FIELD_NAME, TARGET_FIELD_NAME);
    }

    @Override
    public void prepare(FlinkEnvironment env) {
//        SOURCE_FIELD = config.getString("source_field");
//        TARGET_FIELD = config.getString("target_field");
    }
}
