package com.filling.calculation.plugin.base.flink.sink;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.common.EsUtil;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.batch.FlinkBatchSink;
import com.filling.calculation.flink.stream.FlinkStreamSink;
import com.filling.calculation.utils.StringTemplate;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.operators.DataSink;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink;
import org.apache.flink.types.Row;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestClientBuilder;

import java.util.*;


public class Elasticsearch implements FlinkStreamSink<Row, Row>, FlinkBatchSink<Row, Row> {

    private JSONObject config;
    private String indexName;
    private String INDEXIDFIELD;

    private final static String PREFIX = "es.";

    private String USERNAME;
    private String PASSWORD;

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
        if (config.containsKey("hosts")) {
            return new CheckResult(true, "");
        } else {
            return new CheckResult(false, "please specify [hosts] as a non-empty string list");
        }
    }


    @Override
    public void prepare(FlinkEnvironment env) {
        config.putIfAbsent("index", "filling");
        config.putIfAbsent("index_type", "_doc");
        config.putIfAbsent("index_time_format", "yyyy.MM.dd");
        INDEXIDFIELD = config.getString("index_id_field");

        config.putIfAbsent(PREFIX + "bulk.flush.max.actions", 1000);
        config.putIfAbsent(PREFIX + "bulk.flush.max.size.mb", 2);
        config.putIfAbsent(PREFIX + "bulk.flush.interval.ms", 1000);
        config.putIfAbsent(PREFIX + "bulk.flush.backoff.enable", true);
        config.putIfAbsent(PREFIX + "bulk.flush.backoff.delay", 50);
        config.putIfAbsent(PREFIX + "bulk.flush.backoff.retries", 8);

        USERNAME = config.getString(PREFIX + "username");
        PASSWORD = config.getString(PREFIX + "password");

    }

    @Override
    public DataStreamSink<Row> outputStream(FlinkEnvironment env, DataStream<Row> dataStream) throws Exception {

        List<HttpHost> httpHosts = new ArrayList<>();
        List<String> hosts = config.getObject("hosts", List.class);
        for (String host : hosts) {
            httpHosts.add(new HttpHost(host.split(":")[0], Integer.parseInt(host.split(":")[1]), "http"));
        }

        RowTypeInfo rowTypeInfo = (RowTypeInfo) dataStream.getType();

        List<String> fieldNames = Arrays.asList(rowTypeInfo.getFieldNames());


        List<TypeInformation> fieldTypes = new ArrayList<>();
        for (int i = 0; i < rowTypeInfo.getFieldTypes().length; i++) {
            fieldTypes.add(rowTypeInfo.getFieldTypes()[i]);
        }


        indexName = StringTemplate.substitute(config.getString("index"), config.getString("index_time_format"));

        ElasticsearchSink.Builder<Row> esSinkBuilder = new ElasticsearchSink.Builder<>(httpHosts, new ElasticsearchSinkFunction<Row>() {
            public IndexRequest createIndexRequest(Row element) {
                Map<String, Object> dataMap = EsUtil.rowToJsonMap(element, fieldNames, fieldTypes);

                IndexRequest indexRequest = Requests.indexRequest()
                        .index(indexName)
                        .type(config.getString("index_type"))
                        .source(dataMap);
                // 判断id_field是否为空, 如果不为空, 则加上id
                if(StringUtils.isNotEmpty(INDEXIDFIELD) && dataMap.get(INDEXIDFIELD) != null) {
                    indexRequest.id(dataMap.get(INDEXIDFIELD).toString());
                }
                return indexRequest;
            }

            @Override
            public void process(Row element, RuntimeContext ctx, RequestIndexer indexer) {
                indexer.add(createIndexRequest(element));
            }
        });


        // configuration for the bulk requests; this instructs the sink to emit after every element, otherwise they would be buffered
        esSinkBuilder.setBulkFlushMaxActions(config.getInteger(PREFIX + "bulk.flush.max.actions"));
        esSinkBuilder.setBulkFlushMaxSizeMb(config.getInteger(PREFIX + "bulk.flush.max.size.mb"));
        esSinkBuilder.setBulkFlushInterval(config.getInteger(PREFIX + "bulk.flush.interval.ms"));
        esSinkBuilder.setBulkFlushBackoff(config.getBoolean(PREFIX + "bulk.flush.backoff.enable"));
        esSinkBuilder.setBulkFlushBackoffDelay(config.getLong(PREFIX + "bulk.flush.backoff.delay"));
        esSinkBuilder.setBulkFlushBackoffRetries(config.getInteger(PREFIX + "bulk.flush.backoff.retries"));

        esSinkBuilder.setRestClientFactory(
                restClientBuilder -> {
            restClientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {

                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    if(!StringUtils.isEmpty(USERNAME) || !StringUtils.isEmpty(PASSWORD)) {
                        // elasticsearch username and password
                        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(USERNAME,  PASSWORD));
                    }
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            });
        });

        // finally, build and add the sink to the job's pipeline
        return dataStream.addSink(esSinkBuilder.build()).setParallelism(getParallelism()).name(getName());
    }

    @Override
    public DataSink<Row> outputBatch(FlinkEnvironment env, DataSet<Row> dataSet) {

        RowTypeInfo rowTypeInfo = (RowTypeInfo) dataSet.getType();
        String[] fieldNames = rowTypeInfo.getFieldNames();

        indexName = StringTemplate.substitute(config.getString("index"), config.getString("index_time_format"));
        return dataSet.output(new ElasticsearchOutputFormat<>(config, new ElasticsearchSinkFunction<Row>() {
            @Override
            public void process(Row element, RuntimeContext ctx, RequestIndexer indexer) {
                indexer.add(createIndexRequest(element));
            }

            private IndexRequest createIndexRequest(Row element) {
                Map<String, Object> json = new HashMap<>(100);
                int elementLen = element.getArity();
                for (int i = 0; i < elementLen; i++) {
                    json.put(fieldNames[i], element.getField(i));
                }
                return Requests.indexRequest()
                        .index(indexName)
                        .type(config.getString("index_type"))
                        .source(json);
            }
        }));
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
