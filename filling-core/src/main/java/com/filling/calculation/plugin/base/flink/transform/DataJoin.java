package com.filling.calculation.plugin.base.flink.transform;


import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamTransform;
import com.filling.calculation.flink.util.TableUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
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
public class DataJoin implements FlinkStreamTransform<Row, Row> {


    //    {
//        "source_table_name": "sql_table29",
//        "result_table_name": "sql_ta9le30",
//        "plugin_name": "DataJoin",
//        "join.source_table_name": ["sql_table28"],
//        "join.sql_table28.type": "left",
//        "join.sql_table28.where": "sql_table29.hostid = sql_table28.host"
//    }
    private JSONObject config;

    private static String JOIN_SOURCE_TABLE_NAME_NAME = "join.source_table_name";
    private static List<String> JOIN_SOURCE_TABLE_NAME = null;

    private static Map<String, String> TABLE_AND_WHERE = new HashMap<>();

    private static Map<String, String> TABLE_AND_TYPE = new HashMap<>();

    String PRE = "join.";
    String WHERE_SUFFIX = ".where";
    String TYPE_SUFFIX = ".type";
    // 默认的需要join配置
    String SECONDARY = "secondary";

    @Override
    public DataStream<Row> processStream(FlinkEnvironment env, DataStream<Row> dataStream) {

        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();

        return (DataStream<Row>) process(tableEnvironment, dataStream, "stream");
    }

    private Object process(TableEnvironment tableEnvironment, Object data, String type) {

        // 主表
        Table mainTable = tableEnvironment.from(config.getString(SOURCE_TABLE_NAME));
        for (String table_name : JOIN_SOURCE_TABLE_NAME) {

            String where = config.getString(TABLE_AND_WHERE.get(table_name));
            String joinType = config.getString(TABLE_AND_TYPE.get(table_name));
            String sql = "select * from {main} {joinType} join {secondary} on {where}"
                    .replaceAll("\\{where\\}", where)
                    .replaceAll("\\{joinType\\}", joinType)
                    .replaceAll("\\{main\\}", config.getString(SOURCE_TABLE_NAME))
                    .replaceAll("\\{secondary\\}", table_name);

            mainTable = tableEnvironment.sqlQuery(sql);
        }

        return "batch".equals(type) ? TableUtil.tableToDataSet((BatchTableEnvironment) tableEnvironment, mainTable) : TableUtil.tableToDataStream((StreamTableEnvironment) tableEnvironment, mainTable, false);
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
        for (String table: JOIN_SOURCE_TABLE_NAME) {
            if(!CheckConfigUtil.check(config, PRE + table + WHERE_SUFFIX).isSuccess() || CheckConfigUtil.check(config, PRE + SECONDARY + WHERE_SUFFIX).isSuccess()) {
                return CheckConfigUtil.check(config, PRE + table + WHERE_SUFFIX);
            }
        }
        return CheckConfigUtil.check(config, JOIN_SOURCE_TABLE_NAME_NAME);
    }

    @Override
    public void prepare(FlinkEnvironment env) {
        JOIN_SOURCE_TABLE_NAME = config.getObject(JOIN_SOURCE_TABLE_NAME_NAME, List.class);

        for (String table_name : JOIN_SOURCE_TABLE_NAME) {
            String where = PRE + table_name + WHERE_SUFFIX;
            String type = PRE + table_name + TYPE_SUFFIX;
            if (StringUtils.isEmpty(config.getString(where))) {

                String swhere = PRE + SECONDARY + WHERE_SUFFIX;
                String stype = PRE + SECONDARY + TYPE_SUFFIX;
                System.out.println("config " + where + "not found, use secondary");
                System.out.println("config " + type + "not found, use secondary");
                config.put(where, config.getString(swhere));
                config.put(type, config.getString(stype));
            }
            TABLE_AND_WHERE.put(table_name, where);
            TABLE_AND_TYPE.put(table_name, type);

        }
    }
}
