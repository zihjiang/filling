package com.filling.calculation.plugin.base.flink.sink;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.common.PropertiesUtil;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.batch.FlinkBatchSink;
import com.filling.calculation.flink.stream.FlinkStreamSink;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.operators.DataSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.BatchTableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;


/**
 * @author zihjiang
 */
public class KafkaSink implements FlinkStreamSink<Row, Row>, FlinkBatchSink<Row, Row> {

    private final Logger log = LoggerFactory.getLogger(KafkaSink.class);

    private JSONObject config;
    private Properties kafkaParams = new Properties();
    private String topic;

    String producerPrefix = "producer.";

    @Override
    public DataStreamSink<Row> outputStream(FlinkEnvironment env, DataStream<Row> dataStream) {
        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();
        Table table = tableEnvironment.fromDataStream(dataStream);

        List<String> columns = table.getResolvedSchema().getColumnNames();
        JSONObject jsonObject = new JSONObject();
        FlinkKafkaProducer<Row> myProducer = new FlinkKafkaProducer<>(
                topic,
                (KafkaSerializationSchema<Row>) (element, timestamp) -> {
                    for (String column : columns) {
                        jsonObject.put(column, element.getField(column));
                    }
                    return new ProducerRecord<>(topic, jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
                },
                kafkaParams,
                FlinkKafkaProducer.Semantic.AT_LEAST_ONCE);


        return dataStream.addSink(myProducer).setParallelism(getParallelism()).name(getName());
    }


    @Override
    public DataSink<Row> outputBatch(FlinkEnvironment env, DataSet<Row> rowDataSet){

        BatchTableEnvironment tableEnvironment = env.getBatchTableEnvironment();
//        Table table = tableEnvironment.fromDataSet(rowDataSet);
//        insert(tableEnvironment,table);
        return null;
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
        return CheckConfigUtil.check(config, producerPrefix + "bootstrap.servers","topics");
    }

    @Override
    public void prepare(FlinkEnvironment env) {
        topic = config.getString("topics")
        ;
        PropertiesUtil.setProperties(config, kafkaParams, producerPrefix, false);
//        kafkaParams.put("key.serializer","org.apache.kafka.common.serialization.ByteArraySerializer");
//        kafkaParams.put("value.serializer","org.apache.kafka.common.serialization.ByteArraySerializer");
    }

    @Override
    public Integer getParallelism() {

        // 默认为1,
        return config.getInteger("parallelism") == null ? 1 : config.getInteger("parallelism");
    }

    @Override
    public String getName() {

        return StringUtils.isEmpty(config.getString("name")) ? config.getString("plugin_name") : config.getString("name");
    }
}
