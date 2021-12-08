package com.filling.calculation.flink.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ObjectArrayTypeInfo;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.scala.typeutils.Types;
import org.apache.flink.formats.avro.typeutils.AvroSchemaConverter;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.table.utils.TypeStringUtils;
import org.apache.flink.types.Row;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SchemaUtil {


    public static void setSchema(Schema schema, Object info, String format) {

        switch (format.toLowerCase()) {
            case "json":
                getJsonSchema(schema, (JSONObject) info);
                break;
            case "csv":
                getCsvSchema(schema, (List<Map<String, String>>) info);
                break;
            case "orc":
                getOrcSchema(schema, (JSONObject) info);
                break;
            case "avro":
                getAvroSchema(schema, (JSONObject) info);
                break;
            case "parquet":
                getParquetSchema(schema, (JSONObject) info);
            default:
                break;
        }
    }

    private static void getJsonSchema(Schema schema, JSONObject json) {

        for (Map.Entry<String, Object> entry : json.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Long) {
                schema.field(key, "LONG");
            } else if (value instanceof Integer) {
                schema.field(key, "INT");
            } else if (value instanceof BigDecimal) {
                schema.field(key, "FLOAT");
            } else if (value instanceof JSONObject) {
                schema.field(key, getTypeInformation((JSONObject) value));
            } else if (value instanceof String) {
                schema.field(key, "STRING");
            } else if (value instanceof JSONArray) {
                Object obj = ((JSONArray) value).get(0);
                if (obj instanceof JSONObject) {
                    schema.field(key, ObjectArrayTypeInfo.getInfoFor(Row[].class,getTypeInformation((JSONObject) obj)));
                }else {
                    schema.field(key, ObjectArrayTypeInfo.getInfoFor(Object[].class,TypeInformation.of(Object.class)));
                }
            }
        }
    }

    private static void getCsvSchema(Schema schema, List<Map<String, String>> schemaList) {

        for (Map<String, String> map : schemaList) {
            String field = map.get("field");
            String type = map.get("type").toUpperCase();
            schema.field(field, type);
        }
    }

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
     * todo
     *
     * @param schema
     * @param json
     */
    private static void getOrcSchema(Schema schema, JSONObject json) {

    }


    /**
     * todo
     *
     * @param schema
     * @param json
     */
    private static void getParquetSchema(Schema schema, JSONObject json) {

    }


    private static void getAvroSchema(Schema schema, JSONObject json) {
        RowTypeInfo typeInfo = (RowTypeInfo) AvroSchemaConverter.<Row>convertToTypeInfo(json.toString());
        String[] fieldNames = typeInfo.getFieldNames();
        for(String name : fieldNames){
            schema.field(name,typeInfo.getTypeAt(name));
        }
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
            }
            i++;
        }
        return new RowTypeInfo(informations, fields);
    }



    public static String getUniqueTableName() {
        return "_tmp_" + UUID.randomUUID().toString().replaceAll("-", "_");
    }
}
