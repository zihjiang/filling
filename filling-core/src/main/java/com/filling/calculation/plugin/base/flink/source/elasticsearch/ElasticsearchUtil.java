package com.filling.calculation.plugin.base.flink.source.elasticsearch;

import org.apache.flink.table.api.ValidationException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.slice.SliceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.flink.streaming.connectors.elasticsearch.table.ElasticsearchConnectorOptions.HOSTS_OPTION;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

/**
 * @author zihjiang
 */
public class ElasticsearchUtil {

    /**
     * @param elasticsearchConf
     * @return RestHighLevelClient
     */
    public static RestHighLevelClient createClient(ElasticsearchConf elasticsearchConf) throws IOException {
        List<HttpHost> httpAddresses = getHosts(elasticsearchConf.getHosts());
        RestClientBuilder restClientBuilder = RestClient.builder(
                httpAddresses.
                        toArray(new HttpHost[httpAddresses.size()]));
        if (elasticsearchConf.isAuthMesh()) {
            // 进行用户和密码认证
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(
                            elasticsearchConf.getUsername(),
                            elasticsearchConf.getPassword()));
            restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder ->
                            httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                                    .setKeepAliveStrategy((response, context) -> elasticsearchConf.getKeepAliveTime())
                                    .setMaxConnPerRoute(elasticsearchConf.getMaxConnPerRoute()))
                    .setRequestConfigCallback(requestConfigBuilder
                            -> requestConfigBuilder.setConnectTimeout(elasticsearchConf.getConnectTimeout())
                            .setConnectionRequestTimeout(elasticsearchConf.getRequestTimeout())
                            .setSocketTimeout(elasticsearchConf.getSocketTimeout()));
        } else {
            restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder ->
                            httpAsyncClientBuilder
                                    .setKeepAliveStrategy((response, context) -> elasticsearchConf.getKeepAliveTime())
                                    .setMaxConnPerRoute(elasticsearchConf.getMaxConnPerRoute()))
                    .setRequestConfigCallback(requestConfigBuilder
                            -> requestConfigBuilder.setConnectTimeout(elasticsearchConf.getConnectTimeout())
                            .setConnectionRequestTimeout(elasticsearchConf.getRequestTimeout())
                            .setSocketTimeout(elasticsearchConf.getSocketTimeout()));
        }

        RestHighLevelClient rhlClient = new RestHighLevelClient(restClientBuilder);
        return rhlClient;
    }

    /**
     * generate doc id by id fields.
     *
     * @param
     * @return
     */
    public static String generateDocId(List<String> idFieldNames, Map<String, Object> dataMap, String keyDelimiter) {
        String doc_id = "";
        if (null != idFieldNames) {
            doc_id = idFieldNames.stream()
                    .map(idFiledName -> dataMap.get(idFiledName).toString())
                    .collect(Collectors.joining(keyDelimiter));
        }
        return doc_id;
    }

    public static List<HttpHost> getHosts(List<String> hosts) {
        return hosts.stream()
                .map(host -> validateAndParseHostsString(host))
                .collect(Collectors.toList());
    }

    /**
     * Parse Hosts String to list.
     *
     * <p>Hosts String format was given as following:
     *
     * <pre>
     *     connector.hosts = http://host_name:9092;http://host_name:9093
     * </pre>
     */
    private static HttpHost validateAndParseHostsString(String host) {
        try {
            HttpHost httpHost = HttpHost.create(host);
            if (httpHost.getPort() < 0) {
                throw new ValidationException(
                        String.format(
                                "Could not parse host '%s' in option '%s'. It should follow the format 'http://host_name:port'. Missing port.",
                                host, HOSTS_OPTION.key()));
            }

            if (httpHost.getSchemeName() == null) {
                throw new ValidationException(
                        String.format(
                                "Could not parse host '%s' in option '%s'. It should follow the format 'http://host_name:port'. Missing scheme.",
                                host, HOSTS_OPTION.key()));
            }
            return httpHost;
        } catch (Exception e) {
            throw new ValidationException(
                    String.format(
                            "Could not parse host '%s' in option '%s'. It should follow the format 'http://host_name:port'.",
                            host, HOSTS_OPTION.key()),
                    e);
        }
    }


    /**
     * 使用scroll进行搜索
     * @param client
     * @param elasticsearchConf
     * @param sliceId
     * @param sliceMax
     * @return
     * @throws IOException
     */
    public static SearchResponse searchByScroll(RestHighLevelClient client, ElasticsearchConf elasticsearchConf, Integer sliceId, Integer sliceMax) throws IOException {

        //初始化scroll
        //设定滚动时间间隔
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(elasticsearchConf.getKeepAliveTime()));
        SearchRequest searchRequest = new SearchRequest();
        SliceBuilder sliceBuilder = new SliceBuilder(sliceId, sliceMax);

        searchRequest.indices(elasticsearchConf.getIndex());
        searchRequest.scroll(scroll);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().slice(sliceBuilder);
        searchSourceBuilder.query(matchAllQuery());
        //设定每次返回多少条数据
        searchSourceBuilder.size(elasticsearchConf.getBulkSize());
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return searchResponse;
    }

    public static SearchResponse searchByScroll(RestHighLevelClient client, ElasticsearchConf elasticsearchConf) throws IOException {

        //初始化scroll
        //设定滚动时间间隔
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(elasticsearchConf.getKeepAliveTime()));
        SearchRequest searchRequest = new SearchRequest();

        searchRequest.indices(elasticsearchConf.getIndex());
        searchRequest.scroll(scroll);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchAllQuery());
        //设定每次返回多少条数据
        searchSourceBuilder.size(elasticsearchConf.getBulkSize());
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return searchResponse;
    }

    /**
     * 通过滚动ID获取文档
     *
     * @param client
     * @param scrollId
     */
    public static List<Map<String, Object>> searchByScrollId(RestHighLevelClient client, String scrollId) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        TimeValue timeValue = new TimeValue(30000);
        SearchResponse response;
        // 结果
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        // 重新设定滚动时间
        scrollRequest.scroll(timeValue);
        // 请求
        response = client.scroll(scrollRequest, RequestOptions.DEFAULT);
        // 这一批次结果
        SearchHit[] searchHits = response.getHits().getHits();
        for (SearchHit searchHit : searchHits) {
            result.add(searchHit.getSourceAsMap());
        }
        return result;
    }

    /**
     * 清除滚动ID
     *
     * @param client
     * @param scrollId
     * @return
     */
    public static boolean clearScroll(RestHighLevelClient client, String scrollId) throws IOException {
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        RequestOptions options = RequestOptions.DEFAULT;
        ClearScrollResponse response = client.clearScroll(clearScrollRequest, options);
        return response.isSucceeded();
    }

    /**
     * 获取第一条数据
     * @param elasticsearchConf
     * @return
     * @throws IOException
     */
    public static String getHeadData(ElasticsearchConf elasticsearchConf) throws IOException {
        RestHighLevelClient client = createClient(elasticsearchConf);
        SearchRequest searchRequest = new SearchRequest();

        searchRequest.indices(elasticsearchConf.getIndex());

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchAllQuery());
        //设定只返回第一条数据,
        searchSourceBuilder.size(1);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResponse.getHits().getHits()[0].getSourceAsString();
    }
}
