package com.filling.calculation.flink.util;

/**
 * @program: calculation-core
 * @description:
 * @author: zihjiang
 * @create: 2021-06-26 16:10
 **/
public enum PluginType {
    /**
     * 算子, source
     */
    SOURCE("source"),
    /**
     * 算子. transform
     */
    TRANSFORM("transform"),
    /**
     * 算子, sink
     */
    SINK("sink");

    private String type;
    private PluginType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
