package com.filling.calculation.flink;


import com.filling.calculation.apis.BaseTransform;

public interface BaseFlinkTransform extends BaseTransform<FlinkEnvironment> {

    default void registerFunction(FlinkEnvironment flinkEnvironment){

    }

}
