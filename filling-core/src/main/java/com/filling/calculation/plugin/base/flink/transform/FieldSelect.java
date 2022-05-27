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

public class FieldSelect implements FlinkStreamTransform<Row, Row> {


    private JSONObject config;

    private static String FIELD_NAME = "field";
    private static String FIELD = null;

    @Override
    public DataStream<Row> processStream(FlinkEnvironment env, DataStream<Row> dataStream) {
        System.out.println("[DEBUG] current stage: " + config.getString("name"));

        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();

        return (DataStream<Row>) process(tableEnvironment, dataStream, "stream");
    }

    private Object process(TableEnvironment tableEnvironment, Object data, String type) {

        Table table = null;
        String sql = null;

        // 判断参数是否为数组
        if(FIELD.startsWith("[")) {
            JSONArray _field = JSONArray.parseArray(FIELD);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < _field.size(); i++) {
                sb.append(_field.getString(i));

                if (i >= (_field.size() - 1)) {
                    sb.append(" ");
                } else {
                    sb.append(", ");
                }
            }

            sql = "select  " + sb.toString() + " from {table_name}"
                .replaceAll("\\{table_name}", config.getString(SOURCE_TABLE_NAME));
        } else {
            sql = "select " + FIELD + " from {table_name}"
                .replaceAll("\\{table_name}", config.getString(SOURCE_TABLE_NAME));
        }

        table = tableEnvironment.sqlQuery(sql);

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
       return CheckConfigUtil.check(config,FIELD_NAME );
    }

    @Override
    public void prepare(FlinkEnvironment env) {
        FIELD = config.getString(FIELD_NAME);
    }
}
