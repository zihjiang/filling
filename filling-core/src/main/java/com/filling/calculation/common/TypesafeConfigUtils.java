package com.filling.calculation.common;


import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class TypesafeConfigUtils {

    /**
     * Extract sub config with fixed prefix
     *
     * @param source config source
     * @param prefix config prefix
     * @param keepPrefix true if keep prefix
     */
    public static JSONObject extractSubConfig(JSONObject source, String prefix, boolean keepPrefix) {

        JSONObject result = new JSONObject();

        for (Map.Entry<String, Object> entry : source.entrySet()) {
            final String key = entry.getKey();
            final String value = String.valueOf(entry.getValue());

            if (key.startsWith(prefix)) {

                if (keepPrefix) {
                    result.put(key, value);
                } else {
                    result.put(key.substring(prefix.length()), value);
                }
            }
        }

        return result;

    }

    /**
     * Check if config with specific prefix exists
     * @param source config source
     * @param prefix config prefix
     * @return true if it has sub config
     */
    public static boolean hasSubConfig(JSONObject source, String prefix) {

        boolean hasConfig = false;

        for (Map.Entry<String, Object> entry : source.entrySet()) {
            final String key = entry.getKey();

            if (key.startsWith(prefix)) {
                hasConfig = true;
                break;
            }
        }

        return hasConfig;
    }

    public static JSONObject extractSubConfigThrowable(JSONObject source, String prefix, boolean keepPrefix) {

        JSONObject config = extractSubConfig(source, prefix, keepPrefix);

        if (config.isEmpty()) {
            throw new ConfigRuntimeException("config is empty");
        }

        return config;
    }
}
