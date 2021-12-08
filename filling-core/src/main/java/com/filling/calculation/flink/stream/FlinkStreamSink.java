package com.filling.calculation.flink.stream;

import com.filling.calculation.flink.BaseFlinkSink;
import com.filling.calculation.flink.FlinkEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;

public interface FlinkStreamSink<IN, OUT> extends BaseFlinkSink {

    DataStreamSink<OUT> outputStream(FlinkEnvironment env, DataStream<IN> dataStream) throws Exception;

}
