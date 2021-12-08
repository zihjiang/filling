package com.filling.calculation.common;

import com.alibaba.fastjson.JSONObject;

import java.util.Properties;

public class PropertiesUtil {

    public static void setProperties(JSONObject config, Properties properties, String prefix, boolean keepPrefix){
        config.entrySet().forEach(entry -> {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.startsWith(prefix)) {
                if (keepPrefix){
                    properties.put(key,value);
                }else {
                    properties.put(key.substring(prefix.length()), value);
                }
            }
        });
    }
}
