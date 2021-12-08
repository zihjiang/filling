package com.filling.calculation.flink.batch;

import com.filling.calculation.flink.BaseFlinkSource;
import com.filling.calculation.flink.FlinkEnvironment;
import org.apache.flink.api.java.DataSet;

public interface FlinkBatchSource<T> extends BaseFlinkSource {

    DataSet<T> getBatchData(FlinkEnvironment env);
}
