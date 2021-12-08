package com.filling.calculation.plugin.base.flink.sink;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.common.PropertiesUtil;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.batch.FlinkBatchSink;
import com.filling.calculation.flink.stream.FlinkStreamSink;
import com.filling.calculation.flink.util.SchemaUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.operators.DataSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.BatchTableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.FormatDescriptor;
import org.apache.flink.table.descriptors.Json;
import org.apache.flink.table.descriptors.Kafka;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
public class KafkaTable implements FlinkStreamSink<Row, Row>, FlinkBatchSink<Row, Row> {

    private JSONObject config;
    private Properties kafkaParams = new Properties();
    private String topic;


    @Override
    public DataStreamSink<Row> outputStream(FlinkEnvironment env, DataStream<Row> dataStream) {
        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();
        Table table = tableEnvironment.fromDataStream(dataStream);
//        insert(tableEnvironment,table);

        List<String> columes = table.getResolvedSchema().getColumnNames();

        FlinkKafkaProducer<Row> myProducer = new FlinkKafkaProducer<>(
                topic,
                (KafkaSerializationSchema<Row>) (element, timestamp) -> {
                    JSONObject jsonObject = new JSONObject();
                    for (String colume : columes) {
                        jsonObject.put(colume, element.getField(colume));
                    }
                    return new ProducerRecord<>(topic, "key".getBytes(StandardCharsets.UTF_8), jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
                },
                kafkaParams,
                FlinkKafkaProducer.Semantic.EXACTLY_ONCE);


        return dataStream.addSink(myProducer);
    }


    @Override
    public DataSink<Row> outputBatch(FlinkEnvironment env, DataSet<Row> rowDataSet){

        BatchTableEnvironment tableEnvironment = env.getBatchTableEnvironment();
        Table table = tableEnvironment.fromDataSet(rowDataSet);
        insert(tableEnvironment,table);
        return null;
    }


    private void insert(TableEnvironment tableEnvironment,Table table){
        TypeInformation<?>[] types = table.getSchema().getFieldTypes();
        String[] fieldNames = table.getSchema().getFieldNames();
        Schema schema = getSchema(types, fieldNames);
        String uniqueTableName = SchemaUtil.getUniqueTableName();
        tableEnvironment.connect(getKafkaConnect())
                .withSchema(schema)
                .withFormat(setFormat())
                .inAppendMode()
                .createTemporaryTable(uniqueTableName);

//        table.insertInto(uniqueTableName);

//        table.executeInsert()
//        table.intersect(tableEnvironment.from(uniqueTableName));

//        tableEnvironment.from(uniqueTableName).intersect(table);
        table.executeInsert(uniqueTableName);
    }


    private Schema getSchema(TypeInformation<?>[] informations, String[] fieldNames) {
        Schema schema = new Schema();
        for (int i = 0; i < informations.length; i++) {
            schema.field(fieldNames[i], informations[i]);
        }
        return schema;
    }

    private Kafka getKafkaConnect() {

        Kafka kafka = new Kafka().version("universal");
        kafka.topic(topic);
        kafka.properties(kafkaParams);
        return kafka;
    }

    private FormatDescriptor setFormat() {
        return new Json().failOnMissingField(false).deriveSchema();
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
        return CheckConfigUtil.check(config,"topics");
    }

    @Override
    public void prepare(FlinkEnvironment env) {
        topic = config.getString("topics");
        String producerPrefix = "producer.";
        PropertiesUtil.setProperties(config, kafkaParams, producerPrefix, false);
        kafkaParams.put("key.serializer","org.apache.kafka.common.serialization.ByteArraySerializer");
        kafkaParams.put("value.serializer","org.apache.kafka.common.serialization.ByteArraySerializer");
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
