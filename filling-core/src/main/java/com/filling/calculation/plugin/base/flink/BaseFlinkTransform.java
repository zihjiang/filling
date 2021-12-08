package com.filling.calculation.plugin.base.flink;

import com.filling.calculation.apis.BaseTransform;
import com.filling.calculation.flink.FlinkEnvironment;


public interface BaseFlinkTransform extends BaseTransform<FlinkEnvironment> {

    default void registerFunction(FlinkEnvironment flinkEnvironment){

    }

}
