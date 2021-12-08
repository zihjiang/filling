package com.filling.calculation.apis;

import com.filling.calculation.env.RuntimeEnv;
import com.filling.calculation.plugin.Plugin;

public interface BaseSink<T extends RuntimeEnv> extends Plugin<T> {

    Integer getParallelism();

    String getName();
}
