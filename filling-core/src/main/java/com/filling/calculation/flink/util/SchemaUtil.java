package com.filling.calculation.flink.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.DtStringUtil;
import com.google.common.collect.Maps;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ObjectArrayTypeInfo;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.scala.typeutils.Types;
import org.apache.flink.table.utils.TypeStringUtils;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author zihjiang
 * @date 2020/10/22 - 15:10
 */
public class SchemaUtil {


    public static TypeInformation[] getCsvType(List<Map<String, String>> schemaList) {
        TypeInformation[] typeInformation = new TypeInformation[schemaList.size()];
        int i = 0;
        for (Map<String, String> map : schemaList) {
            String type = map.get("type").toUpperCase();
            typeInformation[i++] = TypeStringUtils.readTypeInfo(type);
        }
        return typeInformation;
    }

    /**
     * 根据JSON, 获取RowTypeInfo
     * @param json json格式的数据
     * @return RowTypeInfo
     */
    public static RowTypeInfo getTypeInformation(JSONObject json) {
        int size = json.size();
        String[] fields = new String[size];
        TypeInformation[] informations = new TypeInformation[size];
        int i = 0;
        for (Map.Entry<String, Object> entry : json.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            fields[i] = key;
            if (value instanceof String) {
                informations[i] = Types.STRING();
            } else if (value instanceof Integer) {
                informations[i] = Types.INT();
            } else if (value instanceof Long) {
                informations[i] = Types.LONG();
            } else if (value instanceof BigDecimal) {
                informations[i] = Types.DOUBLE();
            } else if (value instanceof JSONObject) {
                informations[i] = getTypeInformation((JSONObject) value);
            }else if (value instanceof JSONArray) {
                Object object = ((JSONArray) value).getObject(0, Object.class);
                // 判断, 如果是json
                if(object instanceof JSONObject) {
                    JSONObject demo = ((JSONArray) value).getJSONObject(0);
                    informations[i] = ObjectArrayTypeInfo.getInfoFor(Row[].class, getTypeInformation(demo));
                } else {
                    // 数组形式, 默认都是striing
                    informations[i] = ObjectArrayTypeInfo.getInfoFor(Types.STRING());
                }
            } else {
                // 数组形式, 默认都是striing
                informations[i] = ObjectArrayTypeInfo.getInfoFor(Types.STRING());
            }
            i++;
        }
        return new RowTypeInfo(informations, fields);
    }

    /**
     * json转row
     * @param json
     * @return
     */
    public static Row getRow(JSONObject json) {

        return null;
    }

    /**
     * row数据转json map
     * @param row row为具有field名称的row
     * @param fields 字段名
     * @param fieldTypes 字段类型
     * @return
     */
    public static Map<String, Object> rowToJsonMap(Row row, List<String> fields, List<TypeInformation> fieldTypes) {

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
                if (fieldTypes.get(i).isBasicType()) {
                    Object value = DtStringUtil.col2string(col, fieldTypes.get(i).toString());
                    currMap.put(key, value);
                } else {
                    // 判断类型,
                    switch (fieldTypes.get(i).getClass().getTypeName()) {
                        case "org.apache.flink.api.java.typeutils.RowTypeInfo":
                            currMap.put(key, rowObjectToJsonMap(col));
                            break;
                        case "org.apache.flink.api.java.typeutils.ObjectArrayTypeInfo":
                            currMap.put(key, rowArrayToJsonMap(col));
                            break;
                        case "org.apache.flink.api.common.typeinfo.SqlTimeTypeInfo":
                            currMap.put(key, DtStringUtil.col2string(col, fieldTypes.get(i).toString()));
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
