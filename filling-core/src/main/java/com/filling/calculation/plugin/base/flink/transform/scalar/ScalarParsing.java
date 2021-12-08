package com.filling.calculation.plugin.base.flink.transform.scalar;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.types.Row;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScalarParsing extends ScalarFunction {

    public ScalarParsing() {
        super();
    }

    private RowTypeInfo rowTypeInfo;
    private List<JSONObject> fields;

    public ScalarParsing(RowTypeInfo rowTypeInfo, List<JSONObject> fields) {
        this.rowTypeInfo = rowTypeInfo;
        this.fields = fields;
    }

    public Row eval(String str) {
        Row row = Row.withNames();

        for (int i = 0; i < fields.size(); i++) {
            JSONObject field = fields.get(i);
            Pattern p = Pattern.compile(field.getString("format"));
            Matcher m = p.matcher(str);

            if(m.find()) {
                switch (field.getString("type")) {
                    case "string":
                    case "number":
                        row.setField(field.getString("name"), m.group(0));
                        break;
                    case "datetime":
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(field.getString("pattern"));
                            LocalDateTime timestamp = LocalDateTime.parse(m.group(0), formatter);
                            row.setField(field.getString("name"), timestamp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }
        return row;
    }

    @Override
    public TypeInformation<?> getResultType(Class<?>[] signature) {
        return rowTypeInfo;
    }
}
