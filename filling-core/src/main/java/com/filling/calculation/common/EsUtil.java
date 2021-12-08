package com.filling.calculation.common;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.guava18.com.google.common.collect.Maps;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;

import java.util.*;

/**
 * @author zihjiang
 * @date 2020/10/22 - 15:10
 */
public class EsUtil {

    /**
     * row数据转json map
     * @param row row为具有field名称的row
     * @param fields 字段名称
     * @param types 字段类型, 和字段名称对应
     * @return
     */
    public static Map<String, Object> rowToJsonMap(Row row, List<String> fields, List<TypeInformation> types) {
        Preconditions.checkArgument(row.getArity() == fields.size());
        Map<String, Object> jsonMap = Maps.newHashMap();
        int i = 0;
        for (; i < fields.size(); ++i) {
            String field = fields.get(i);
            String[] parts = field.split("\\.");
            Map<String, Object> currMap = jsonMap;
            for (int j = 0; j < parts.length - 1; ++j) {
                String key = parts[j];
                if (currMap.get(key) == null) {
                    HashMap<String, Object> hashMap = Maps.newHashMap();
                    currMap.put(key, hashMap);
                }
                currMap = (Map<String, Object>) currMap.get(key);
            }
            String key = parts[parts.length - 1];
            Object col = row.getField(i);
            if (col != null) {
                if (types.get(i).isBasicType()) {
                    Object value = DtStringUtil.col2string(col, types.get(i).toString());
                    currMap.put(key, value);
                } else {
                    // 判断类型,
                    switch (types.get(i).getClass().getTypeName()) {
                        case "org.apache.flink.api.java.typeutils.RowTypeInfo":
                            currMap.put(key, rowObjectToJsonMap(col));
                            break;
                        case "org.apache.flink.api.java.typeutils.ObjectArrayTypeInfo":
                            currMap.put(key, rowArrayToJsonMap(col));
                            break;
                        case "org.apache.flink.api.common.typeinfo.SqlTimeTypeInfo":
                            currMap.put(key, DtStringUtil.col2string(col, types.get(i).toString()));
                            break;
                        default:
                            currMap.put(key, col);
                            break;
                    }


                }
            } else {

            }

        }

        return jsonMap;
    }

    static Map<String, Object> rowObjectToJsonMap(Object col) {
        Row row = (Row) col;
        Map result = new HashMap(row.getArity());
        for (String fieldName : row.getFieldNames(true)) {
            if( "org.apache.flink.api.java.typeutils.RowTypeInfo".equals(row.getFieldAs(fieldName).getClass().getTypeName()) ) {
                result.put(fieldName, rowObjectToJsonMap(row.getFieldAs(fieldName)));
            } else if("org.apache.flink.api.java.typeutils.ObjectArrayTypeInfo".equals(row.getFieldAs(fieldName).getClass().getTypeName())) {
                result.put(fieldName, rowArrayToJsonMap(row.getFieldAs(fieldName)));
            } else {
                result.put(fieldName, row.getFieldAs(fieldName).toString());
            }
        }
        return result;

    }

    static List rowArrayToJsonMap(Object col) {
        List result = new ArrayList();
        if(col instanceof String[]) {
            String[] strings = (String[]) col;
            result = Arrays.asList(strings);
        } else {
            Row[] rows = (Row[]) col;
            for (Row row : rows) {
                for (String fieldName : row.getFieldNames(true)) {
                    System.out.println("row.getFieldAs(fieldName).getClass().getTypeName()): " + row.getFieldAs(fieldName).getClass().getTypeName());
                    if( "org.apache.flink.api.java.typeutils.RowTypeInfo".equals(row.getFieldAs(fieldName).getClass().getTypeName()) ) {
                        Map<String, Object> _result = new HashMap<>();
                        _result.put(fieldName, rowObjectToJsonMap(row.getFieldAs(fieldName)));
                        result.add(_result);
                    } else if("org.apache.flink.api.java.typeutils.ObjectArrayTypeInfo".equals(row.getFieldAs(fieldName).getClass().getTypeName())) {
                        List<Map<String, Object>> _result = rowArrayToJsonMap(row.getFieldAs(fieldName));
                        result.addAll(_result);
                    } else {
                        Map<String, Object> _result = new HashMap<>();
                        _result.put(fieldName, row.getField(fieldName));
                        result.add(_result);
                    }
                }
            }
        }


        return result;
    }


}
