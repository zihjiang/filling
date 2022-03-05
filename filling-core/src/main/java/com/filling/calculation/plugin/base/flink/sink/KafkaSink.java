package com.filling.calculation.plugin.base.flink.sink;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.common.PropertiesUtil;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamSink;
import com.filling.calculation.flink.util.SchemaUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * @author zihjiang
 */
public class KafkaSink implements FlinkStreamSink<Row, Row> {

    private final Logger log = LoggerFactory.getLogger(KafkaSink.class);

    private JSONObject config;
    private Properties kafkaParams = new Properties();
    private String topic;

    String producerPrefix = "producer.";

    @Override
    public DataStreamSink<Row> outputStream(FlinkEnvironment env, DataStream<Row> dataStream) {

        RowTypeInfo rowTypeInfo = (RowTypeInfo) dataStream.getType();
        List<TypeInformation> fieldTypes = new ArrayList<>();
        for (int i = 0; i < rowTypeInfo.getFieldTypes().length; i++) {
            fieldTypes.add(rowTypeInfo.getFieldTypes()[i]);
        }

        FlinkKafkaProducer<Row> myProducer = new FlinkKafkaProducer<>(
                topic,
                (KafkaSerializationSchema<Row>) (element, timestamp) -> {
                    Map<String, Object> stringObjectMap = SchemaUtil.rowToJsonMap(element, Arrays.asList(rowTypeInfo.getFieldNames()), fieldTypes);
                    JSONObject jsonObject = new JSONObject(stringObjectMap);

                    return new ProducerRecord<>(topic, jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
                },
                kafkaParams,
                FlinkKafkaProducer.Semantic.AT_LEAST_ONCE);


        return dataStream.addSink(myProducer).setParallelism(getParallelism()).name(getName());
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
