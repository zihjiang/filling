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
     *
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
            informations[i] = getTypeInformation(value);
            i++;
        }
        return new RowTypeInfo(informations, fields);
    }

    /**
     * 根据传入值, 判断值的TypeInformation
     * @param value 传入的值
     * @return 值的类型
     */
    private static TypeInformation getTypeInformation(Object value) {
        TypeInformation result;
        if (value instanceof String) {
            result = Types.STRING();
        } else if (value instanceof Integer) {
            result = Types.INT();
        } else if (value instanceof Long) {
            result = Types.LONG();
        } else if (value instanceof BigDecimal) {
            result = Types.JAVA_BIG_DEC();
        } else if (value instanceof JSONObject) {
            result = getTypeInformation((JSONObject) value);
        } else if (value instanceof JSONArray) {
            Object object = ((JSONArray) value).getObject(0, Object.class);
            // 判断, 如果是json
            if (object instanceof JSONObject) {
                JSONObject demo = ((JSONArray) value).getJSONObject(0);
                result = ObjectArrayTypeInfo.getInfoFor(Row[].class, getTypeInformation(demo));
            } else {

                result = Types.OBJECT_ARRAY(getTypeInformation(object));
            }
        } else {
            System.out.println("值无法解析: "+ value +" 默认为string");
            result = ObjectArrayTypeInfo.getInfoFor(Types.STRING());
        }

//         return TypeExtractor.getForObject(value);
        return result;
    }

    /**
     * map转row
     *
     * @param map
     * @return
     */
    public static Row JsonMapToRow(Map<String, Object> map) {
        Row row = Row.withNames();
        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
            String key = stringObjectEntry.getKey();
            Object value = stringObjectEntry.getValue();
            if(value instanceof HashMap) {
                row.setField(key, JsonMapToRow((HashMap<String, Object>) map.get(key)));
            } else if(value instanceof JSONObject) {

                JSONObject jsonObject = new JSONObject(map);
                row.setField(key, JsonMapToRow(jsonObject.getJSONObject(key)));
            } else if(value instanceof ArrayList) {
                ArrayList arrayList = (ArrayList) value;
                row.setField(key, JsonMapArrayToRow(arrayList));
            }  else if(value instanceof JSONArray) {
                JSONArray jsonArray = JSONArray.parseArray(value.toString());
                ArrayList arrayList = jsonArray.toJavaObject(ArrayList.class);
                row.setField(key, JsonMapArrayToRow(arrayList));
            } else {
                row.setField(key, value);
            }
        }
        return row;
    }

    private static Object JsonMapArrayToRow(ArrayList arrayList) {
        Row row = Row.withNames();
        for (Object o : arrayList) {
            if(o instanceof HashMap) {
                HashMap<String, Object> map = (HashMap<String, Object>) o;
                for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
                    row.setField(stringObjectEntry.getKey(), map.get(stringObjectEntry.getKey()));
                }
            } else {
                // TODO 解析数组类型
                return o.toString();
//                row.setField();
            }
        }

        return row;
    }

    /**
     * row数据转json map
     *
     * @param row        row为具有field名称的row
     * @param fields     字段名
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
//                            currMap.put(key, rowObjectToJsonMap(col));
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


    public static Map<String, Object> rowToJsonMap(Row row) {

        Map<String, Object> jsonMap = Maps.newHashMap();
        for (String field : row.getFieldNames(true)) {
            String key = field.split("\\.")[field.split("\\.").length - 1];
            Object col = row.getField(field);
            if (col != null) {
                // 判断类型,
                if (col instanceof Row) {

                    jsonMap.put(key, rowToJsonMap((Row) col));
                } else if (col instanceof Row[]) {
                    jsonMap.put(key, rowArrayToJsonMap(col));
                } else {
                    jsonMap.put(key, col);
                }
            } else {
                jsonMap.put(key, null);
            }
        }

        return jsonMap;
    }

    private Map<String, Object> rowObjectToJsonMap(Object col) {
        Row row = (Row) col;
        Map result = new HashMap(row.getArity());
        for (String fieldName : row.getFieldNames(true)) {
            if ("org.apache.flink.api.java.typeutils.RowTypeInfo".equals(row.getFieldAs(fieldName).getClass().getTypeName())) {
                result.put(fieldName, rowObjectToJsonMap(row.getFieldAs(fieldName)));
            } else if ("org.apache.flink.api.java.typeutils.ObjectArrayTypeInfo".equals(row.getFieldAs(fieldName).getClass().getTypeName())) {
                result.put(fieldName, rowArrayToJsonMap(row.getFieldAs(fieldName)));
            } else {
                result.put(fieldName, row.getFieldAs(fieldName).toString());
            }
        }
        return result;

    }

    static List rowArrayToJsonMap(Object col) {
        List result = new ArrayList();
        if (col instanceof String[]) {
            String[] strings = (String[]) col;
            result = Arrays.asList(strings);
        } else {
            Row[] rows = (Row[]) col;
            for (Row row : rows) {
                result.add(rowToJsonMap(row));
            }
        }


        return result;
    }


    /**
     * 判断object是否为基本类型
     * @param object
     * @return
     */
    public static boolean isBaseType(Object object) {
        Class className = object.getClass();
        if (className.equals(java.lang.Integer.class) ||
                className.equals(java.lang.Byte.class) ||
                className.equals(java.lang.Long.class) ||
                className.equals(java.lang.Double.class) ||
                className.equals(java.lang.Float.class) ||
                className.equals(java.lang.Character.class) ||
                className.equals(java.lang.Short.class) ||
                className.equals(java.lang.String.class) ||
                className.equals(java.lang.Boolean.class)) {
            return true;
        }
        return false;
    }
}
