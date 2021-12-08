package com.filling.calculation.common;


import com.alibaba.fastjson.JSONObject;

public class CheckConfigUtil {

    public static CheckResult check(JSONObject config, String... params) {
        for (String param : params) {
            if (!config.containsKey(param)) {
                return new CheckResult(false, "please specify [" + param + "] as non-empty");
            }
        }
        return new CheckResult(true,"");
    }
}
