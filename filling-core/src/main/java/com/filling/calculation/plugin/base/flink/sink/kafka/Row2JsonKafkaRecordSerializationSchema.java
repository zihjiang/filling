package com.filling.calculation.plugin.base.flink.sink.kafka;


import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.types.Row;

import java.nio.charset.StandardCharsets;

/**
 * @program: filling
 * @description:
 * @author: zihjiang
 * @create: 2022-03-16 22:53
 **/
public class Row2JsonKafkaRecordSerializationSchema implements SerializationSchema<Row> {
    @Override
    public void open(InitializationContext context) throws Exception {
        SerializationSchema.super.open(context);
        // TODO monitor
    }

    @Override
    public byte[] serialize(Row row) {
        JSONObject jsonObject = new JSONObject();
        for (String fieldName : row.getFieldNames(true)) {
            jsonObject.put(fieldName, row.getField(fieldName));
        }
        return jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8);
    }
}
