package com.filling.calculation.domain;

import com.filling.calculation.enums.GenDataKind;
import com.filling.calculation.enums.GenDataType;

/**
 * datagen参数实体类
 * @author zihjiang
 */
public class DataGenField {

    private GenDataKind kind;
    private GenDataType type;
    private Integer min;
    private Integer max;
    private Integer length;
    private Integer start;
    private Integer end;

    public GenDataKind getKind() {
        return GenDataKind.SEQUENCE.equals(kind) ? GenDataKind.SEQUENCE : GenDataKind.RANDOM;
    }

    public void setKind(GenDataKind kind) {
        this.kind = kind;
    }

    public Integer getMin() {
        return (min == null || min == 0) ? 0 : min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return (max == null || max == 0) ? Integer.MIN_VALUE - 1 : max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getLength() {
        return (length == null || length == 0) ? 100 : length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getStart() {
        return (start == null || start == 0) ? 0 : start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return (end == null || end == 0) ? 100 : end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public GenDataType getType() {
        return type;
    }

    public void setType(GenDataType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DataGenField{" +
                "kind='" + kind + '\'' +
                ", type='" + type + '\'' +
                ", min=" + min +
                ", max=" + max +
                ", length=" + length +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
