package com.filling.calculation.plugin.base.flink.sink;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamSink;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.io.FileOutputFormat;
import org.apache.flink.api.common.serialization.Encoder;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;

public class FileSink implements FlinkStreamSink<Row, Row> {

    private static final Logger LOG = LoggerFactory.getLogger(FileSink.class);

    private final static String PATH = "path";
    private final static String FORMAT = "format";
    private final static String WRITE_MODE = "write_mode";

    private JSONObject config;

    private FileOutputFormat outputFormat;

    private Path filePath;

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
        return CheckConfigUtil.check(config,PATH,FORMAT);
    }

    @Override
    public void prepare(FlinkEnvironment env) {
        String path = config.getString(PATH);
        filePath = new Path(path);
    }

    @Override
    public DataStreamSink<Row> outputStream(FlinkEnvironment env, DataStream<Row> dataStream) {
        final StreamingFileSink<Row> sink = StreamingFileSink
                .forRowFormat(filePath, (Encoder<Row>) (element, stream) -> {
                    PrintStream out = new PrintStream(stream);
                    out.println(element);
                })
                .build();



        return dataStream.addSink(sink).setParallelism(getParallelism()).name(getName());
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
