package com.filling.calculation.flink.batch;

import com.filling.calculation.flink.BaseFlinkSink;
import com.filling.calculation.flink.FlinkEnvironment;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.operators.DataSink;

public interface FlinkBatchSink<IN, OUT> extends BaseFlinkSink {

    DataSink<OUT> outputBatch(FlinkEnvironment env, DataSet<IN> inDataSet) throws Exception;
}
