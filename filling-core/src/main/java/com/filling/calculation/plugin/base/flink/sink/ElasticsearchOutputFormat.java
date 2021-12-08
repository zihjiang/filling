package com.filling.calculation.plugin.base.flink.sink;


import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.util.List;


public class ElasticsearchOutputFormat<T> extends RichOutputFormat<T> {

    private JSONObject config;

    private final static String PREFIX = "es.";

    private final ElasticsearchSinkFunction<T> elasticsearchSinkFunction;

    private transient RequestIndexer requestIndexer;

    private transient BulkProcessor bulkProcessor;

    public ElasticsearchOutputFormat(JSONObject userConfig, ElasticsearchSinkFunction<T> elasticsearchSinkFunction) {
        this.config = userConfig;
        this.elasticsearchSinkFunction = elasticsearchSinkFunction;
    }

    @Override
    public void configure(Configuration configuration) {
        List<String> hosts = config.getObject("hosts", List.class);
        Settings.Builder settings = Settings.builder();

        config.entrySet().forEach(entry -> {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.startsWith(PREFIX)) {
                settings.put(key.substring(PREFIX.length()), value.toString());
            }
        });

        TransportClient transportClient = new PreBuiltTransportClient(settings.build());

        for (String host : hosts) {
            try {
                transportClient.addTransportAddresses(new TransportAddress(InetAddress.getByName(host.split(":")[0]), Integer.parseInt(host.split(":")[1])));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        BulkProcessor.Builder bulkProcessorBuilder = BulkProcessor.builder(transportClient, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
            }
        });

        // This makes flush() blocking
        //  bulkProcessorBuilder.setConcurrentRequests(0);

        bulkProcessor = bulkProcessorBuilder.build();
        requestIndexer = new RequestIndexer() {
            @Override
            public void add(DeleteRequest... deleteRequests) {

            }

            @Override
            public void add(IndexRequest... indexRequests) {
                for (IndexRequest indexRequest : indexRequests) {

                    bulkProcessor.add(indexRequest);
                }
            }

            @Override
            public void add(UpdateRequest... updateRequests) {

            }
        };
    }

    @Override
    public void open(int i, int i1) {

    }

    @Override
    public void writeRecord(T t) {
        elasticsearchSinkFunction.process(t, getRuntimeContext(), requestIndexer);
    }

    @Override
    public void close() {
        this.bulkProcessor.flush();
    }

}
