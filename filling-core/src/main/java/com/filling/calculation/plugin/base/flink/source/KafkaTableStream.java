package com.filling.calculation.plugin.base.flink.source;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.common.PropertiesUtil;
import com.filling.calculation.common.TypesafeConfigUtils;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamSource;
import com.filling.calculation.flink.util.SchemaUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.scala.typeutils.Types;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.csv.CsvRowDeserializationSchema;
import org.apache.flink.formats.json.JsonRowDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class KafkaTableStream implements FlinkStreamSource<Row> {

    private JSONObject config;

    private Properties kafkaParams = new Properties();
    private List<String> topics = new ArrayList<>();
    private Object schemaInfo;
    private final String consumerPrefix = "consumer.";
    private String format;
    private String format_field_delimiter;
    private String bootstrap_servers;
    private String group_id;

    private static final String TOPICS = "topics";
    private static final String SCHEMA = "schema";
    private static final String SOURCE_FORMAT = "format.type";
    private static final String GROUP_ID = "group.id";
    private static final String BOOTSTRAP_SERVERS = "bootstrap.servers";
    private static final String OFFSET_RESET = "offset.reset";
    private static final String FORMAT_FIELD_DELIMITER = "format.field_delimiter";

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

        CheckResult result = CheckConfigUtil.check(config, TOPICS, SCHEMA, SOURCE_FORMAT, RESULT_TABLE_NAME);

        if (result.isSuccess()) {
            JSONObject consumerConfig = TypesafeConfigUtils.extractSubConfig(config, consumerPrefix, false);
            return CheckConfigUtil.check(consumerConfig, BOOTSTRAP_SERVERS, GROUP_ID);
        }

        return result;
    }

    @Override
    public void prepare(FlinkEnvironment env) {
        String startsWith = "[";
        if (config.getString(TOPICS) != null) {
            // 查看是否以[开头
            if (config.getString(TOPICS).startsWith(startsWith)) {
                for (int i = 0; i < config.getJSONArray(TOPICS).size(); i++) {
                    topics.add(config.getJSONArray(TOPICS).getString(i));
                }
                topics = config.getJSONArray(TOPICS).toJavaList(String.class);
            } else {
                topics.add(config.getString(TOPICS));
            }
        }
        PropertiesUtil.setProperties(config, kafkaParams, consumerPrefix, false);
        format = config.getString(SOURCE_FORMAT);
        config.putIfAbsent(FORMAT_FIELD_DELIMITER, ",");
        bootstrap_servers = kafkaParams.getProperty(BOOTSTRAP_SERVERS);
        group_id = kafkaParams.getProperty(GROUP_ID);
    }

    @Override
    public DataStream<Row> getStreamData(FlinkEnvironment env) {

        KafkaSource<Row> source = KafkaSource.<Row>builder()
                .setProperties(kafkaParams)
                .setBootstrapServers(bootstrap_servers)
                .setTopics(topics)
                .setGroupId(group_id)
                .setStartingOffsets(getOffsetType())
                .setValueOnlyDeserializer(getSchema())
                .build();

        return env.getStreamExecutionEnvironment().fromSource(source, WatermarkStrategy.noWatermarks(), getName()).setParallelism(getParallelism());
    }

    private DeserializationSchema getSchema() {
        DeserializationSchema result = null;
        String schemaContent = config.getString(SCHEMA);
        switch (format) {
            case "csv":
                // TODO
//                char delimiter = config.getString(FORMAT_FIELD_DELIMITER).charAt(0);
//                result = new CsvRowDeserializationSchema.Builder(typeInfo).setFieldDelimiter(delimiter).setIgnoreParseErrors(true).build();
                break;
            case "json":
                schemaInfo = JSONObject.parse(schemaContent, Feature.OrderedField);

                TypeInformation<Row> typeInfo = SchemaUtil.getTypeInformation((JSONObject) schemaInfo);
                // 忽略转换错误引发的退出任务, 提升健壮性,
                result = new JsonRowDeserializationSchema.Builder(typeInfo).ignoreParseErrors().build();
                break;
            case "text":
            case "string":
                TypeInformation[] info = {Types.STRING()};
                String[] name = {"message"};
                result = new JsonRowDeserializationSchema.Builder(new RowTypeInfo(info, name)).ignoreParseErrors().build();
                break;
            default:
                break;
        }
        return result;
    }

    private OffsetsInitializer getOffsetType() {
        if (config.containsKey(OFFSET_RESET)) {
            String reset = config.getString(OFFSET_RESET);
            switch (reset) {
                case "latest":
                    return OffsetsInitializer.latest();
                case "earliest":
                    return OffsetsInitializer.earliest();
                case "fromTimestamp":
                    return OffsetsInitializer.timestamp(1);
                case "fromGroupOffsets":
                    return OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST);
                default:
                    System.out.println("不识别参数offset.reset=" + reset + "参数应该为: latest, earliest, fromTimestamp, fromGroupOffsets, 默认为 fromGroupOffsets");
                    return OffsetsInitializer.committedOffsets();
            }
        }

        System.out.println(" 默认为 fromGroupOffsets");
        return OffsetsInitializer.committedOffsets();
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
