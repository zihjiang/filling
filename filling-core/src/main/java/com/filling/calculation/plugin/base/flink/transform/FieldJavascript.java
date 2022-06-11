package com.filling.calculation.plugin.base.flink.transform;


import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamTransform;
import com.filling.calculation.flink.util.TableUtil;
import com.filling.calculation.plugin.base.flink.transform.scalar.FunctionJavascript;
import com.filling.calculation.plugin.base.flink.transform.scalar.ScalarSplit;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.scala.typeutils.Types;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.List;

import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.call;


public class FieldJavascript implements FlinkStreamTransform<Row, Row> {

    private JSONObject config;

    static final String  TARGET_FIELD_NAME = "target_field";
    static final String SCRIPT = "script";


    @Override
    public DataStream<Row> processStream(FlinkEnvironment env, DataStream<Row> dataStream) {
        System.out.println("[DEBUG] current stage: " + config.getString("name"));

        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();
        Table table = tableEnvironment.fromDataStream(dataStream).addColumns(
                call(
                        new FunctionJavascript(config.getString(SCRIPT)),
                        $("*"),
                        tableEnvironment.fromDataStream(dataStream).getResolvedSchema().getColumnNames()).as(config.getString(TARGET_FIELD_NAME))
                );


        return tableEnvironment.toChangelogStream(table);
    }
    @Override
    public void registerFunction(FlinkEnvironment flinkEnvironment) {

    }

    @Override
    public void setConfig(JSONObject config) {
        this.config = config;
    }

    @Override
    public JSONObject getConfig() {
        return config;
    }

    @Override
    public CheckResult checkConfig() {
        return CheckConfigUtil.check(config,TARGET_FIELD_NAME, SCRIPT);
    }

    @Override
    public void prepare(FlinkEnvironment prepareEnv) {
    }


}

