package com.filling.calculation.plugin.base.flink.sink;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.common.PropertiesUtil;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamSink;
import io.pravega.connectors.flink.FlinkPravegaWriter;
import io.pravega.connectors.flink.PravegaConfig;
import io.pravega.connectors.flink.table.catalog.pravega.util.PravegaSchemaUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamUtils;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.flink.table.api.Table;
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
public class PravegaSink implements FlinkStreamSink<String, String> {

    private final Logger log = LoggerFactory.getLogger(PravegaSink.class);

    private JSONObject config;
    private Properties kafkaParams = new Properties();
    private String topic;

    String producerPrefix = "producer.";

    @Override
    public DataStreamSink<String> outputStream(FlinkEnvironment env, DataStream<String> dataStream) {
        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();

        ParameterTool params = ParameterTool.fromArgs(new String[]{"--controller", "tcp://10.10.14.210:9090"});
        PravegaConfig pravegaConfig = PravegaConfig
                .fromParams(params)
                .withDefaultScope("random");
        FlinkPravegaWriter<String> writer = FlinkPravegaWriter.<String>builder()
                .withPravegaConfig(pravegaConfig)
                .forStream("random/mystream")
                .withSerializationSchema(new SimpleStringSchema())
                .build();
        dataStream.print();

        return dataStream.addSink(writer).setParallelism(getParallelism()).name(getName());
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
