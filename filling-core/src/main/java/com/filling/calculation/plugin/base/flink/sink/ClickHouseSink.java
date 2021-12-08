package com.filling.calculation.plugin.base.flink.sink;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.batch.FlinkBatchSink;
import com.filling.calculation.flink.stream.FlinkStreamSink;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.operators.DataSink;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.types.Row;


/**
 * @program: calculation-core
 * @description:
 * @author: zihjiang
 * @create: 2021-06-26 16:10
 **/
public class ClickHouseSink implements FlinkStreamSink<Row, Row>, FlinkBatchSink<Row, Row> {

    JSONObject config;

    private String driverName;
    private String dbUrl;
    private String username;
    private String password;
    private String query;
    private int batchSize = 5000;
    private int batchIntervalMs = 200;
    private int maxRetries = 5;
    private JSONArray params;


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

        driverName = config.getString("driver");
        dbUrl = config.getString("url");
        username = config.getString("username");
        query = config.getString("query");
        if (config.containsKey("password")) {
            password = config.getString("password");
        }
        if (config.containsKey("batch_size")) {
            batchSize = config.getInteger("batch_size");
        }
        if (config.containsKey("batch_interval_ms")) {
            batchIntervalMs = config.getInteger("batch_interval_ms");
        }
        if (config.containsKey("max_retries")) {
            maxRetries = config.getInteger("max_retries");
        }
        if (config.containsKey("params")) {
            params = config.getJSONArray("params");
        }
    }


    @Override
    public DataStreamSink<Row> outputStream(FlinkEnvironment env, DataStream<Row> dataStream) {

        return dataStream.addSink(
                JdbcSink.sink(
                        query
                        ,
                        (statement, row) -> {
//                    statement.setObject(1, row.getField("_time"));
                            for (int i = 0; i < params.size(); i++) {
                                statement.setObject(i + 1, row.getField(params.getString(i)));
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

    @Override
    public DataSink<Row> outputBatch(FlinkEnvironment env, DataSet<Row> dataSet) {
        dataSet.output(new ClickHouseOutputFormat( driverName,  dbUrl,  username,  password,  query,  batchSize,  batchIntervalMs,  maxRetries,  params));

//        dataSet.output(JdbcOutputFormat.buildJdbcOutputFormat()
//                .setDrivername(driverName)
//                .setDBUrl(dbUrl)
//                .setUsername(username)
//                .setPassword(password)
//                .setBatchSize(batchSize)
//                .setQuery(query)
//                .finish()
//        );
        return null;
    }

}
