package com.filling.calculation.plugin.base.flink.transform;


import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.batch.FlinkBatchTransform;
import com.filling.calculation.flink.stream.FlinkStreamTransform;
import com.filling.calculation.flink.util.TableUtil;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.BatchTableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: calculation-core
 * @description:
 * @author: zihjiang
 * @create: 2021-06-26 16:10
 **/
public class DataSelector implements FlinkBatchTransform<Row, Row>, FlinkStreamTransform<Row, Row> {


//        "source_table_name": "sql_table29",
//            "result_table_name": "sql_ta9le30",
//            "plugin_name": "DataSelector",
//            "select.result_table_name": ["sql_ta9le31", "sql_table32"]
//            "select.sql_ta9le31.where": "hostid in ('filling01', 'filling02')",
//            "select.sql_ta9le32.where": "hostid not in ('filling01', 'filling02')"
    private JSONObject config;

    private static String SELECT_RESULT_TABLE_NAME_NAME = "select.result_table_name";
    private static List<String> SELECT_RESULT_TABLE_NAME= null;

    private static Map<String, String> TABLE_AND_WHERE = new HashMap<>();

    String PRE = "select.";
    String SUFFIX= ".where";

    @Override
    public DataStream<Row> processStream(FlinkEnvironment env, DataStream<Row> dataStream) {

        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();

        return (DataStream<Row>) process(tableEnvironment, dataStream, "stream");
    }

    @Override
    public DataSet<Row> processBatch(FlinkEnvironment env, DataSet<Row> data) {
        BatchTableEnvironment tableEnvironment = env.getBatchTableEnvironment();

        return (DataSet<Row>) process(tableEnvironment, data, "batch");
    }

    private Object process(TableEnvironment tableEnvironment, Object data, String type) {


        for (String table_name: SELECT_RESULT_TABLE_NAME) {

            String sql = "select * from {source_table_name} where 1=1 and {where}"
                    .replace("{source_table_name}", config.getString(SOURCE_TABLE_NAME))
                    .replace("{where}",config.getString(TABLE_AND_WHERE.get(table_name)));

            tableEnvironment.createTemporaryView(table_name, tableEnvironment.sqlQuery(sql));
//            Table table = tableEnvironment.from(config.getString(SOURCE_TABLE_NAME)).where(config.getString(TABLE_AND_WHERE.get(table_name)));
//            tableEnvironment.createTemporaryView(table_name, table);
        }

        return "batch".equals(type) ? TableUtil.tableToDataSet((BatchTableEnvironment) tableEnvironment, tableEnvironment.from(config.getString(SOURCE_TABLE_NAME))) : TableUtil.tableToDataStream((StreamTableEnvironment) tableEnvironment, tableEnvironment.from(config.getString(SOURCE_TABLE_NAME)), false);
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
        for (String table: SELECT_RESULT_TABLE_NAME) {
            if(!CheckConfigUtil.check(config, PRE + table + SUFFIX).isSuccess()) {
                return CheckConfigUtil.check(config, PRE + table + SUFFIX);
            }
        }
        return CheckConfigUtil.check(config,SELECT_RESULT_TABLE_NAME_NAME);
    }

    @Override
    public void prepare(FlinkEnvironment env) {
        SELECT_RESULT_TABLE_NAME = config.getObject(SELECT_RESULT_TABLE_NAME_NAME, List.class);

        for (String table_name: SELECT_RESULT_TABLE_NAME) {
            String where = PRE + table_name + SUFFIX;
            TABLE_AND_WHERE.put(table_name, where );
        }
    }
}
