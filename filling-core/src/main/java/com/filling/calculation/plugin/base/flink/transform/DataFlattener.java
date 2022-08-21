package com.filling.calculation.plugin.base.flink.transform;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamTransform;
import com.filling.calculation.flink.util.TableUtil;
import com.filling.calculation.plugin.base.flink.transform.scalar.ScalarFlattener;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.apache.flink.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.concat;


public class DataFlattener implements FlinkStreamTransform<Row, Row> {

    private JSONObject config;


    private static final String SOURCE_FIELD_NAME = "source_field";
    private static final String TARGET_FIELD_NAME = "target_field";

    @Override
    public DataStream<Row> processStream(FlinkEnvironment env, DataStream<Row> dataStream) {
        System.out.println("[DEBUG] current stage: " + config.getString("name"));

        Table inputTable = env.getStreamTableEnvironment().fromDataStream(dataStream).addColumns(concat("1", "1").as("attr"));
        DataStream<Row> resultStream = env.getStreamTableEnvironment().toDataStream(inputTable);

        return resultStream.flatMap(new Flattener(config.getString(SOURCE_FIELD_NAME), config.getString(TARGET_FIELD_NAME))).returns(Row.class);
    }

    @Override
    public void registerFunction(FlinkEnvironment flinkEnvironment) {
        if (flinkEnvironment.isStreaming()) {
            flinkEnvironment
                    .getStreamTableEnvironment()
                    .registerFunction("flattener", new ScalarFlattener());
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

    class Flattener implements FlatMapFunction<Row, Row> {

        String sourceFieldName;
        String targetFieldName;

        Flattener(String sourceFieldName, String targetFieldName) {
            this.sourceFieldName = sourceFieldName;
            this.targetFieldName = targetFieldName;
        }

        Flattener() {
            this.sourceFieldName = config.getString(SOURCE_FIELD_NAME);
        }

        @Override
        public void flatMap(Row row, Collector<Row> collector) throws Exception {
            Object sourceField = row.getFieldAs(sourceFieldName);
            // 如果是数组, 则直接转换为新行
            if (sourceField.getClass().isArray()) {
                Object[] sourceFieldArray = (Object[]) sourceField;
                for (Object sourceFieldValue : sourceFieldArray) {
                    row.setField(targetFieldName, sourceFieldValue);
                    collector.collect(row);
                }
            } else {
                collector.collect(row);
            }
        }
    }

}

