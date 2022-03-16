package com.filling.calculation.flink.stream;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.env.Execution;
import com.filling.calculation.env.RuntimeEnv;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.util.TableUtil;
import com.filling.calculation.plugin.Plugin;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @program: calculation-core
 * @description:
 * @author: zihjiang
 * @create: 2021-12-19 15:10
 **/
public class FlinkStreamExecution implements Execution<FlinkStreamSource, FlinkStreamTransform, FlinkStreamSink> {

    private JSONObject config;

    private FlinkEnvironment flinkEnvironment;


    public FlinkStreamExecution(FlinkEnvironment streamEnvironment) {
        this.flinkEnvironment = streamEnvironment;
    }

    @Override
    public void start(List<FlinkStreamSource> sources, List<FlinkStreamTransform> transforms, List<FlinkStreamSink> sinks) throws Exception {

        List<DataStream> data = new ArrayList<>();
        for (FlinkStreamSource source : sources) {
            DataStream dataStream;
            try {
                baseCheckConfig(source);
                prepare(flinkEnvironment, source);
                dataStream = source.getStreamData(flinkEnvironment);
                data.add(dataStream);
                registerResultTable(source, dataStream);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        for (FlinkStreamTransform transform : transforms) {
            try {
                prepare(flinkEnvironment, transform);
                baseCheckConfig(transform);
                DataStream stream = fromSourceTable(transform);
                transform.registerFunction(flinkEnvironment);
                transform.processStream(flinkEnvironment, stream);
                // registerResultTable(transform, input);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        for (FlinkStreamSink sink : sinks) {
            baseCheckConfig(sink);
            prepare(flinkEnvironment, sink);
            DataStream stream = fromSourceTable(sink);
            sink.outputStream(flinkEnvironment, stream);
        }

        flinkEnvironment.getStreamExecutionEnvironment().execute(flinkEnvironment.getJobName());
    }

    private void registerResultTable(Plugin plugin, DataStream dataStream) {
        JSONObject config = plugin.getConfig();
        if (config.containsKey(RESULT_TABLE_NAME)) {
            String name = config.getString(RESULT_TABLE_NAME);
            StreamTableEnvironment tableEnvironment = flinkEnvironment.getStreamTableEnvironment();
            if (!TableUtil.tableExists(tableEnvironment, name)) {
                tableEnvironment.createTemporaryView(name, dataStream);
            }
        }
    }

    private DataStream<Row> fromSourceTable(Plugin plugin) {
        JSONObject config = plugin.getConfig();
        if (config.containsKey(SOURCE_TABLE_NAME)) {
            StreamTableEnvironment tableEnvironment = flinkEnvironment.getStreamTableEnvironment();
            Table table = tableEnvironment.from(config.getString(SOURCE_TABLE_NAME));
//            tableEnvironment.toChangelogStream(table).shuffle();
            return tableEnvironment.toChangelogStream(table);
//            return TableUtil.tableToDataStream(tableEnvironment, table, true);
        }
        return null;
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
        return new CheckResult(true, "");
    }

    @Override
    public void prepare(Void prepareEnv) {
    }

    private static void baseCheckConfig(Plugin plugin) throws Exception {
        CheckResult checkResult = plugin.checkConfig();
        if (!checkResult.isSuccess()) {
            throw new Exception(String.format("Plugin[%s] contains invalid config, error: %s\n"
                    , plugin.getClass().getName(), checkResult.getMsg()));
        }
    }

    private static void prepare(RuntimeEnv env, Plugin plugin) {
        plugin.prepare(env);
    }
}
