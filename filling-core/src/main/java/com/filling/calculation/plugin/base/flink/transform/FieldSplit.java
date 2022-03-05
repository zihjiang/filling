package com.filling.calculation.plugin.base.flink.transform;


import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamTransform;
import com.filling.calculation.flink.util.TableUtil;
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


public class FieldSplit implements FlinkStreamTransform<Row, Row> {

    private JSONObject config;

    private static final String SEPARATOR = "separator";
    private static final String FIELDS = "fields";

    private String separator = ",";

    private int num;

    private List<String> fields;

    private RowTypeInfo rowTypeInfo;

    private static String SOURCE_FIELD_NAME = "source_field";


    @Override
    public DataStream<Row> processStream(FlinkEnvironment env, DataStream<Row> dataStream) {
        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();
        return (DataStream<Row>) process(tableEnvironment, dataStream, "stream");
    }

    private Object process(TableEnvironment tableEnvironment, Object data, String type) {

        String FUNCTION_NAME = "split";
        String sql = "select info_row.*, * from (select *,{function_name}(`{source_field}`) as info_row  from {source_table_name}) t1"
            .replaceAll("\\{source_table_name}", config.getString(SOURCE_TABLE_NAME))
            .replaceAll("\\{function_name}", FUNCTION_NAME)
            .replaceAll("\\{source_field}", config.getString(SOURCE_FIELD_NAME));
        Table table = tableEnvironment.sqlQuery(sql);
        return TableUtil.tableToDataStream((StreamTableEnvironment) tableEnvironment, table, false);
    }

    @Override
    public void registerFunction(FlinkEnvironment flinkEnvironment) {
        if (flinkEnvironment.isStreaming()){
            flinkEnvironment
                    .getStreamTableEnvironment()
                    .registerFunction("split",new ScalarSplit(rowTypeInfo,num,separator));
        }else {
            System.out.println("no support batch");
        }
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
        return CheckConfigUtil.check(config,FIELDS, SOURCE_FIELD_NAME);
    }

    @Override
    public void prepare(FlinkEnvironment prepareEnv) {
        fields = config.getObject(FIELDS, List.class);
        num = fields.size();
        if (config.containsKey(SEPARATOR)){
            separator = config.getString(SEPARATOR);
        }
        TypeInformation[] types = new  TypeInformation[fields.size()];
        for (int i = 0; i< types.length; i++){
            types[i] = Types.STRING();
        }
        rowTypeInfo = new RowTypeInfo(types,fields.toArray(new String[]{}));
    }


}

