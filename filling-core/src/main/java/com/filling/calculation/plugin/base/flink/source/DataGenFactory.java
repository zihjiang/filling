package com.filling.calculation.plugin.base.flink.source;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.domain.DataGenField;
import com.filling.calculation.enums.GenDataKind;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.streaming.api.functions.source.datagen.DataGenerator;
import org.apache.flink.types.Row;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataGenFactory implements DataGenerator<Row> {

    private List<Map<String, DataGenField>> fields;

    public DataGenFactory(List<Map<String, DataGenField>> fields) {

        this.fields = fields;
    }
    // 随机数据生成器对象
    RandomDataGenerator randomDataGenerator;

    Map<String, Number> counter;

    Boolean isNext;

    @Override
    public void open(String s, FunctionInitializationContext functionInitializationContext, RuntimeContext runtimeContext) throws Exception {
        // 实例化生成器对象
        randomDataGenerator = new RandomDataGenerator();
        counter = new ConcurrentHashMap<>();
        isNext = true;
    }

    @Override
    public boolean hasNext() {
        return isNext;
    }

    @Override
    public Row next() {
        Row row = Row.withNames();
        for (int i = 0; i < fields.size(); i++) {
            Map field = fields.get(i);
            field.keySet().stream().forEach(
                    key -> {
                        DataGenField dataGenField = JSONObject.parseObject(field.get(key).toString(), DataGenField.class);
                        switch (dataGenField.getType()){
                            case STRING:
                                row.setField(key.toString(), randomDataGenerator.nextHexString(dataGenField.getLength()));
                                break;
                            case INT:
                                // 当kind等于RANDOM时, 获取随机数
                                if(dataGenField.getKind().equals(GenDataKind.RANDOM)) {
                                    row.setField(key.toString(), randomDataGenerator.nextInt(dataGenField.getMin(), dataGenField.getMax()));
                                } else {
                                    // 当第一次时
                                    if(counter.get(key.toString()) == null) {
                                        counter.put(key.toString(), dataGenField.getStart());
                                    }
                                    Integer num = (Integer) counter.get(key.toString());
                                    num++;
                                    counter.put(key.toString(), num);
                                    row.setField(key.toString(), num);

                                    isNext =  num >= dataGenField.getEnd() ? false : true;
                                }
                                break;
                            case LONG:
                                row.setField(key.toString(), randomDataGenerator.nextLong(dataGenField.getMin(), dataGenField.getMax()));
                                break;
                            default:
                                break;
                        }
                    }
            );
        }

        return row;
    }
}
