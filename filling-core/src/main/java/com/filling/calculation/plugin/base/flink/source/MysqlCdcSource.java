package com.filling.calculation.plugin.base.flink.source;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamSource;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

public class MysqlCdcSource implements FlinkStreamSource<Row> {

    private JSONObject config;
    private String sql;

    @Override
    public DataStream<Row> getStreamData(FlinkEnvironment env) {

        // enable checkpoint
        // env.getStreamExecutionEnvironment().enableCheckpointing(3000);

        env.getStreamTableEnvironment().executeSql(sql);

        Table table = env.getStreamTableEnvironment().from("mysqlCdc_tmp");

        DataStream<Tuple2<Boolean, Row>> retractStream = env.getStreamTableEnvironment().toRetractStream(table, Row.class);

        return retractStream.flatMap(new FlatMapFunction<Tuple2<Boolean, Row>, Row>() {
            @Override
            public void flatMap(Tuple2<Boolean, Row> value, Collector<Row> out) throws Exception {
                System.out.println(value.f0);
                out.collect(value.f1);
            }
        }).shuffle();
//        retractStream.print();
//
//        return env.getStreamTableEnvironment().toChangelogStream(table);
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
        return CheckConfigUtil.check(config, "sql");
    }

    @Override
    public void prepare(FlinkEnvironment env) {
        sql = config.getString("sql")
                .replaceAll("\\{table}", "mysqlCdc_tmp");
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
