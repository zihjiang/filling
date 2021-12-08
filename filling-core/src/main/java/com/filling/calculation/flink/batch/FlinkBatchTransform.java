package com.filling.calculation.flink.batch;

import com.filling.calculation.flink.BaseFlinkTransform;
import com.filling.calculation.flink.FlinkEnvironment;
import org.apache.flink.api.java.DataSet;

public interface FlinkBatchTransform<IN,OUT> extends BaseFlinkTransform {

    DataSet<OUT> processBatch(FlinkEnvironment env, DataSet<IN> data);

}
