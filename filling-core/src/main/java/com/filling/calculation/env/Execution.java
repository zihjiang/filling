package com.filling.calculation.env;


import com.filling.calculation.apis.BaseSink;
import com.filling.calculation.apis.BaseSource;
import com.filling.calculation.apis.BaseTransform;
import com.filling.calculation.plugin.Plugin;

import java.util.List;

public interface Execution<SR extends BaseSource, TF extends BaseTransform, SK extends BaseSink> extends Plugin<Void> {
    void start(List<SR> sources, List<TF> transforms, List<SK> sinks) throws Exception;
}
