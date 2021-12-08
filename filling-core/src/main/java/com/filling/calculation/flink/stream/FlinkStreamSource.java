package com.filling.calculation.flink.stream;

import com.filling.calculation.flink.BaseFlinkSource;
import com.filling.calculation.flink.FlinkEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;

public interface FlinkStreamSource<T> extends BaseFlinkSource {

    DataStream<T> getStreamData(FlinkEnvironment env) throws NoSuchFieldException;


}
