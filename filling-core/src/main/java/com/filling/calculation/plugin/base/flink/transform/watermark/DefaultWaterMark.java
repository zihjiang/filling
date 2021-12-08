package com.filling.calculation.plugin.base.flink.transform.watermark;

import com.sun.istack.Nullable;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.types.Row;

public class DefaultWaterMark implements AssignerWithPeriodicWatermarks<Row> {
    String fieldName;
    Long delayTime;
    public DefaultWaterMark(String fieldName, Long delayTime) {

        this.fieldName = fieldName;
        this.delayTime = delayTime;
    }

    Long currentMaxTimestamp = 0L;
    Long lastEmittedWatermark = Long.MIN_VALUE;

    @Override
    public long extractTimestamp(Row row, long previousElementTimestamp) {
        // 将元素的时间字段值作为该数据的timestamp
        Long time = row.getFieldAs(fieldName);
        if (time > currentMaxTimestamp) {
            currentMaxTimestamp = time;
        }
        return time;
    }

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {
        // 允许延迟三秒
        Long potentialWM = currentMaxTimestamp - delayTime;
        // 保证水印能依次递增
        if (potentialWM >= lastEmittedWatermark) {
            lastEmittedWatermark = potentialWM;
        }
        return new Watermark(lastEmittedWatermark);
    }

}
