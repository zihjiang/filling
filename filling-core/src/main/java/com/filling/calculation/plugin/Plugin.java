package com.filling.calculation.plugin;


import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckResult;

import java.io.Serializable;

public interface Plugin<T> extends Serializable {
    String RESULT_TABLE_NAME = "result_table_name";
    String SOURCE_TABLE_NAME = "source_table_name";

    void setConfig(JSONObject config);

    JSONObject getConfig();

    CheckResult checkConfig();

    void prepare(T prepareEnv);

}
