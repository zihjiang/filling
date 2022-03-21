package com.filling.calculation.plugin.base.flink.transform;


import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamTransform;
import com.filling.calculation.flink.util.TableUtil;
import com.filling.calculation.plugin.base.flink.transform.scalar.ScalarParsing;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.scala.typeutils.Types;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.List;


public class DataParsing implements FlinkStreamTransform<Row, Row> {

    private JSONObject config;

    private static final String source_field = "source_field";
    private List<JSONObject> fields;

    private RowTypeInfo rowTypeInfo;

    private static String SOURCE_FIELD_NAME = "source_field";

    private static final String FIELD_NAMES = "fields";


    @Override
    public void processStream(FlinkEnvironment env, DataStream<Row> dataStream) {
        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();
        process(tableEnvironment, dataStream, "stream");
    }

    private void process(TableEnvironment tableEnvironment, Object data, String type) {

        String FUNCTION_NAME = "dataParsing";
        String sql = "select * from (select *,{function_name}(`{source_field}`) as info_row  from {source_table_name}) t1"
            .replaceAll("\\{source_table_name}", config.getString(SOURCE_TABLE_NAME))
            .replaceAll("\\{function_name}", FUNCTION_NAME)
            .replaceAll("\\{source_field}", config.getString(SOURCE_FIELD_NAME));
        Table table = tableEnvironment.sqlQuery(sql);

        tableEnvironment.createTemporaryView(config.getString(RESULT_TABLE_NAME), table);
    }

    @Override
    public void registerFunction(FlinkEnvironment flinkEnvironment) {
        if (flinkEnvironment.isStreaming()){
            flinkEnvironment
                    .getStreamTableEnvironment()
                    .registerFunction("dataParsing",new ScalarParsing(rowTypeInfo,fields));
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
        return CheckConfigUtil.check(config, SOURCE_FIELD_NAME);
    }

    @Override
    public void prepare(FlinkEnvironment prepareEnv) {

        fields = config.getObject(FIELD_NAMES, List.class);
        TypeInformation[] types = new  TypeInformation[fields.size()];
        for (int i = 0; i< types.length; i++){
            switch (fields.get(i).getString("type")) {
                case "string":
                    types[i] = Types.STRING();
                    break;
                case "datetime":
                    types[i] = Types.LOCAL_DATE_TIME();
                    break;
                case "number":
                    types[i] = Types.INT();
                    break;
            }
        }

        String[] _field = new String[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            _field[i] = fields.get(i).getString("name");
        }
        rowTypeInfo = new RowTypeInfo(types, _field);
    }


}

