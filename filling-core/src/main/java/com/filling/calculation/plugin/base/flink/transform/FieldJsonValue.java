package com.filling.calculation.plugin.base.flink.transform;


import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamTransform;
import com.filling.calculation.flink.util.TableUtil;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.JsonValueOnEmptyOrError;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.types.DataType;
import org.apache.flink.types.Row;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @program: calculation-core
 * @description:
 * @author: zihjiang
 * @create: 2022-03-22 18:36
 **/
public class FieldJsonValue implements FlinkStreamTransform<Row, Row> {


    private JSONObject config;

    private static String SOURCE_FIELD_NAME = "source_field";
    private static String FIELD_PATH = "path";
    private static String TARGET_FIELD = "target_field";
    private static String RETURN_TYPE = "return_type";
    private static String source_field_name = null;
    private static String path = null;
    private static String target_field_name = null;
    private static String return_type = null;

    @Override
    public DataStream<Row> processStream(FlinkEnvironment env, DataStream<Row> dataStream) {
        System.out.println("[DEBUG] current stage: " + config.getString("name"));

        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();

        return tableEnvironment.toChangelogStream(tableEnvironment.from(config.getString(SOURCE_TABLE_NAME))
                .addColumns($(source_field_name)
                        .jsonValue(path, getDataTypes(return_type), JsonValueOnEmptyOrError.NULL).as(target_field_name)));
    }

    @Override
    public void setConfig(JSONObject config) {
        this.config = config;
    }

    private DataType getDataTypes(String str) {

        switch (str) {
            case "INT":
                return DataTypes.INT();
            case "DOUBLE":
                return DataTypes.DOUBLE();
            case "FLOAT":
                return DataTypes.FLOAT();
            case "DATE":
                return DataTypes.DATE();
            case "BIGINT":
                return DataTypes.BIGINT();
            case "BOOLEAN":
                return DataTypes.BOOLEAN();
            default:
                return DataTypes.STRING();
        }
    }

    @Override
    public JSONObject getConfig() {
        return config;
    }


    @Override
    public CheckResult checkConfig() {
        return CheckConfigUtil.check(config, SOURCE_FIELD_NAME, FIELD_PATH, TARGET_FIELD);
    }

    @Override
    public void prepare(FlinkEnvironment env) {
        source_field_name = config.getString(SOURCE_FIELD_NAME);
        path = config.getString(FIELD_PATH);
        target_field_name = config.getString(TARGET_FIELD);
        return_type = config.getOrDefault(RETURN_TYPE, "STRING").toString();
    }
}
