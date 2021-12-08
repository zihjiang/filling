package com.filling.calculation.flink.util;

/**
 * @program: calculation-core
 * @description:
 * @author: zihjiang
 * @create: 2021-06-26 16:10
 **/
public enum Engine {
    /**
     * spark 引擎
     */
    SPARK("spark"),
    /**
     * flink引擎
     */
    FLINK("flink"),

    /**
     * null引擎
     */
    NULL("");

    private String engine;
    Engine(String engine) {
        this.engine = engine;
    }

    public String getEngine() {
        return engine;
    }
}
