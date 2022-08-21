package com.filling.calculation.plugin.base.flink.transform;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamTransform;
import com.filling.calculation.flink.util.SchemaUtil;
import com.filling.calculation.plugin.base.flink.transform.scalar.ScalarFlattener;
import org.apache.commons.beanutils.BeanMap;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import java.util.Map;

import static org.apache.flink.table.api.Expressions.concat;


public class DataJavascript implements FlinkStreamTransform<Row, Row> {

    private JSONObject config;

    static final String SCRIPT = "script";

    @Override
    public DataStream<Row> processStream(FlinkEnvironment env, DataStream<Row> dataStream) {
        System.out.println("[DEBUG] current stage: " + config.getString("name"));

        Table inputTable = env.getStreamTableEnvironment().fromDataStream(dataStream);
        DataStream<Row> resultStream = env.getStreamTableEnvironment().toDataStream(inputTable);

        return resultStream.flatMap(new Javascript(config.getString(SCRIPT)));
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
        return CheckConfigUtil.check(config);
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

    static class Javascript implements FlatMapFunction<Row, Row> {

        private Invocable inv2;
        static ScriptEngine scriptEngine;
        private String script = null;
        Javascript(String script) {
            System.setProperty("nashorn.args", "--language=es6");
            this.script = script;
        }

        @Override
        public void flatMap(Row row, Collector<Row> collector) throws Exception {
            if (scriptEngine == null) {
                scriptEngine = new ScriptEngineManager().getEngineByName("Nashorn");
                scriptEngine.eval(script);
                inv2 = (Invocable) scriptEngine;
            }
            Map<String, Object> stringObjectMap = SchemaUtil.rowToJsonMap(row);

            Object result = inv2.invokeFunction("process", stringObjectMap);

            if(result.getClass().isArray()) {
                for (Object o : (Object[]) result) {
                    collector.collect(SchemaUtil.JsonMapToRow((Map) o));
                }
            } else {
                collector.collect(SchemaUtil.JsonMapToRow((Map) result));
            }
        }
    }

}

