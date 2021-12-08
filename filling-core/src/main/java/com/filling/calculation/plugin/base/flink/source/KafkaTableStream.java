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
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.formats.csv.CsvRowDeserializationSchema;
import org.apache.flink.formats.json.JsonRowDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class KafkaTableStream implements FlinkStreamSource<Row> {

    private JSONObject config;

    private Properties kafkaParams = new Properties();
    private List<String> topics = new ArrayList<>();
    private Object schemaInfo;
    private String tableName;
    private final String consumerPrefix = "consumer.";
    private String format;
    private String format_field_delimiter;

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
        if(config.getString(TOPICS) != null) {
            // 查看是否以[开头
            if(config.getString(TOPICS).startsWith(startsWith)) {
                for (int i = 0; i < config.getJSONArray(TOPICS).size(); i++) {
                    topics.add(config.getJSONArray(TOPICS).getString(i));
                }
                topics = config.getJSONArray(TOPICS).toJavaList(String.class);
            } else {
                topics.add(config.getString(TOPICS));
            }
        }

        PropertiesUtil.setProperties(config, kafkaParams, consumerPrefix, false);
        tableName = config.getString(RESULT_TABLE_NAME);
        format = config.getString(SOURCE_FORMAT);
        config.putIfAbsent(FORMAT_FIELD_DELIMITER, ",");
    }

    @Override
    public DataStream<Row> getStreamData(FlinkEnvironment env) {


        FlinkKafkaConsumer<Row> kafkaConsumer = new FlinkKafkaConsumer<>(
                topics,
                getSchema(),
                kafkaParams);

        if (config.containsKey(OFFSET_RESET)) {
            String reset = config.getString(OFFSET_RESET);
            switch (reset) {
                case "latest":
                    kafkaConsumer.setStartFromLatest();
                    break;
                case "earliest":
                    kafkaConsumer.setStartFromEarliest();
                    break;
                case "fromTimestamp":
                    kafkaConsumer.setStartFromTimestamp(1);
                    break;
                case "fromGroupOffsets":
                    kafkaConsumer.setStartFromGroupOffsets();
                    break;
                default:
                    System.out.println("不识别参数offset.reset=" + reset + "参数应该为: latest, earliest, fromTimestamp, fromGroupOffsets, 默认为 fromGroupOffsets");
                    kafkaConsumer.setStartFromGroupOffsets();
                    break;
            }
        }
        kafkaConsumer.setCommitOffsetsOnCheckpoints(true);

        DataStream<Row> stream = env.getStreamExecutionEnvironment()
                .addSource(kafkaConsumer).setParallelism(getParallelism()).name(getName());

        return stream;
    }

    private DeserializationSchema getSchema() {
        DeserializationSchema result = null;
        String schemaContent = config.getString(SCHEMA);
        schemaInfo = JSONObject.parse(schemaContent, Feature.OrderedField);

        TypeInformation<Row> typeInfo = SchemaUtil.getTypeInformation((JSONObject) schemaInfo);
        switch (format) {
            case "csv":
                char delimiter =config.getString(FORMAT_FIELD_DELIMITER).charAt(0);
                result = new CsvRowDeserializationSchema.Builder(typeInfo).setFieldDelimiter(delimiter).setIgnoreParseErrors(true).build();
                break;
            case "json":
                // 忽略转换错误引发的退出任务, 提升健壮性,
                result = new JsonRowDeserializationSchema.Builder(typeInfo).ignoreParseErrors().build();
                break;
            case "text":
                result = new SimpleStringSchema();
                break;
            default:
                break;
        }
        return result;
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
