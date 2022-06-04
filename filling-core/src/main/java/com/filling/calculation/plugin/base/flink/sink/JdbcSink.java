package com.filling.calculation.plugin.base.flink.sink;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamSink;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.types.Row;

import java.util.Arrays;
import java.util.List;


/**
 * @program: calculation-core
 * @description:
 * @author: zihjiang
 * @create: 2021-06-26 16:10
 **/
public class JdbcSink implements FlinkStreamSink<Row, Row> {

    JSONObject config;

    public static final String DRIVER = "driver";
    public static final String URL = "url";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String QUERY = "query";
    public static final String BATCHSIZE = "batch_size";
    public static final String INTERVERTEBRAL = "batch_interval_ms";
    public static final String MAXRETRIES = "max_retries";
    public static final String PARAMS = "params";

    private String driverName;
    private String dbUrl;
    private String username;
    private String password;
    private String query;
    private int batchSize = 5000;
    private int batchIntervalMs = 200;
    private int maxRetries = 5;
    private List params;


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
        return CheckConfigUtil.check(config, "driver", "url", "query", "params");
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

    @Override
    public void prepare(FlinkEnvironment env) {

        driverName = config.getString(DRIVER);
        dbUrl = config.getString(URL);
        query = config.getString(QUERY);

        if (config.containsKey(USERNAME) && !StringUtils.isEmpty(config.getString(USERNAME))) {
            username = config.getString(USERNAME);
        }
        if (config.containsKey(PASSWORD) && !StringUtils.isEmpty(config.getString(PASSWORD))) {
            password = config.getString(PASSWORD);
        }
        if (config.containsKey(BATCHSIZE)) {
            batchSize = config.getInteger(BATCHSIZE);
        }
        if (config.containsKey(INTERVERTEBRAL)) {
            batchIntervalMs = config.getInteger(INTERVERTEBRAL);
        }
        if (config.containsKey(MAXRETRIES)) {
            maxRetries = config.getInteger(MAXRETRIES);
        }
        if (config.containsKey(PARAMS)) {
            params = config.getJSONArray(PARAMS);
        }
        String columns = String.join(",", params);
        String[] str = new String[params.size()];
        Arrays.fill(str, "?");
        String questionMark = String.join(",", str);

        query = query.replaceAll("\\{columns}", columns);
        query = query.replaceAll("\\{questionMark}", questionMark);
    }


    @Override
    public DataStreamSink<Row> outputStream(FlinkEnvironment env, DataStream<Row> dataStream) {

        return dataStream.addSink(
                org.apache.flink.connector.jdbc.JdbcSink.sink(
                        query
                        ,
                        (statement, row) -> {
                            for (int i = 0; i < params.size(); i++) {
                                statement.setObject(i + 1, row.getField(params.get(i).toString()));
                            }
                        },
                        JdbcExecutionOptions.builder()
                                .withBatchSize(batchSize)
                                .withBatchIntervalMs(batchIntervalMs)
                                .withMaxRetries(maxRetries)
                                .build(),
                        new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                                .withUrl(dbUrl)
                                .withDriverName(driverName)
                                .withUsername(username)
                                .withPassword(password)
                                .build())
        ).setParallelism(getParallelism()).name(getName());

    }
}
