package com.filling.calculation.flink.stream;

import com.filling.calculation.flink.BaseFlinkTransform;
import com.filling.calculation.flink.FlinkEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;

public interface FlinkStreamTransform<IN, OUT> extends BaseFlinkTransform {

    DataStream<OUT> processStream(FlinkEnvironment env, DataStream<IN> dataStream);
}
