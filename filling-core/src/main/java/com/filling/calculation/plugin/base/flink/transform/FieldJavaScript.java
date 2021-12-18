package com.filling.calculation.plugin.base.flink.transform;


import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamTransform;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.List;


public class FieldJavaScript implements FlinkStreamTransform<Row, Row> {

    private JSONObject config;

    private static final String SCRIPT = "script";
    private static String SOURCE_FIELD_NAME = "source_field";

    private List<String> fields;

    private RowTypeInfo rowTypeInfo;

    private ScriptEngine scriptEngine;


    @Override
    public DataStream<Row> processStream(FlinkEnvironment env, DataStream<Row> dataStream) {
        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();
        return (DataStream<Row>) process(tableEnvironment, dataStream, "stream");
    }

    private Object process(TableEnvironment tableEnvironment, Object data, String type) {

//        DataSet<String> words = ((DataSet) data).flatMap(new FunctionJavscript02(config.getString(SCRIPT)));
//        try {
//            words.print();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        String FUNCTION_NAME = "javascript";
//        String sql = "select * from {source_table_name} LEFT JOIN LATERAL TABLE({function_name}({source_field})) ON TRUE"
//            .replaceAll("\\{source_table_name}", config.getString(SOURCE_TABLE_NAME))
//            .replaceAll("\\{function_name}", FUNCTION_NAME)
//            .replaceAll("\\{source_field}", config.getString(SOURCE_FIELD_NAME));
//        Table table = tableEnvironment.sqlQuery(sql);


//        return "batch".equals(type) ? TableUtil.tableToDataSet((BatchTableEnvironment) tableEnvironment, table) : TableUtil.tableToDataStream((StreamTableEnvironment) tableEnvironment, table, false);
        return null;
    }

//    @Override
//    public void registerFunction(FlinkEnvironment flinkEnvironment) {
//        if (flinkEnvironment.isStreaming()){
//            flinkEnvironment
//                    .getStreamTableEnvironment()
//                    .registerFunction("javascript",new FunctionJavascript(config.getString(SCRIPT) ));
//        }else {
//            flinkEnvironment
//                    .getBatchTableEnvironment()
//                    .registerFunction("javascript",new FunctionJavascript(config.getString(SCRIPT) ));
//        }
//    }

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
        return CheckConfigUtil.check(config, SOURCE_FIELD_NAME, SCRIPT);
    }

    @Override
    public void prepare(FlinkEnvironment prepareEnv) {
    }


}

class FunctionJavscript02 implements FlatMapFunction<Row,Row> {

    String script = "";
    FunctionJavscript02(String script) {
        this.script = script;
    }

    @Override
    public void flatMap(Row value, Collector collector) throws Exception {

        System.out.println("value: " + value);
        System.out.println("value.toString(): " + value.toString());

        ScriptEngine engine =  new ScriptEngineManager().getEngineByName("javascript");
        engine.eval(script + "function _process(d){ return JSON.stringify(process(JSON.parse(d)))}");
        Invocable inv2 = (Invocable) engine;
        JSONObject jsonObject = JSONObject.parseObject(inv2.invokeFunction("_process", value.toString()).toString());

        collector.collect(value);
    }
}

