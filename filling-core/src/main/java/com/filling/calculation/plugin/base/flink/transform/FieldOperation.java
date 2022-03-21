package com.filling.calculation.plugin.base.flink.transform;


import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamTransform;
import com.filling.calculation.flink.util.TableUtil;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.table.expressions.ExpressionParser;
import org.apache.flink.types.Row;

import java.util.List;

public class FieldOperation implements FlinkStreamTransform<Row, Row> {


    private JSONObject config;

    private static final String SCRIPT_NAME = "script";
    private static final String TARGET_FIELD_NAME = "target_field";

    @Override
    public void processStream(FlinkEnvironment env, DataStream<Row> dataStream) {

        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();

        process(tableEnvironment);
    }

    private void process(TableEnvironment tableEnvironment) {

        String sql = "select *,(" +config.getString(SCRIPT_NAME)+ ") as {target_field_name} from {table_name}"
            .replaceAll("\\{target_field_name}", config.getString(TARGET_FIELD_NAME))
            .replaceAll("\\{table_name}", config.getString(SOURCE_TABLE_NAME));

        tableEnvironment.createTemporaryView(config.getString(RESULT_TABLE_NAME), tableEnvironment.sqlQuery(sql));
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
       return CheckConfigUtil.check(config,SCRIPT_NAME );
    }

    @Override
    public void prepare(FlinkEnvironment env) {
        String SCRIPT = config.getString(SCRIPT_NAME);
    }

    /**
     * 防止idea提示废弃
     * @param expr 表达式
     * @return
     */
    private Expression[] _(String expr) {

        List<Expression> expressions = ExpressionParser.parseExpressionList(expr);
        return expressions.toArray(new Expression[0]);
    }
}
