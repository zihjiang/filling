package com.filling.calculation.plugin.base.flink.transform.scalar;

import org.apache.flink.table.annotation.FunctionHint;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.types.Row;

public  class ScalarFlattener extends TableFunction<Row> {

    public ScalarFlattener() {
        super();
    }

    @FunctionHint
    public void eval(Row fields) {

        String hostName = fields.getFieldAs ("hostName");

        Row row = Row.withNames();
        System.out.println("eval"+ fields);
        row.setField("word", "1");
        row.setField("length", 1);
//        int i = 0;
//        for (String s : str.split(separator, num)) {
//            row.setField(i++, s);
//        }
        collect(fields);
    }

    @FunctionHint
    public void eval(String fields) {

        Row row = Row.withNames();
        System.out.println("eval"+ fields);
        row.setField("word", "1");
        row.setField("length", 1);
        collect(row);
    }

//    @Override
//    public TypeInference getTypeInference(DataTypeFactory typeFactory) {
//
//    }



}
