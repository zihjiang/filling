package com.filling.calculation.plugin.base.flink.source;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamSource;
import com.filling.calculation.flink.util.SchemaUtil;
import com.filling.calculation.flink.util.TableUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.io.InputFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.io.RowCsvInputFormat;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.functions.BuiltInFunctionDefinitions;
import org.apache.flink.table.functions.FunctionDefinition;
import org.apache.flink.table.types.utils.LegacyTypeInfoDataTypeConverter;
import org.apache.flink.table.types.utils.TypeConversions;
import org.apache.flink.types.Row;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.apache.flink.table.api.Expressions.*;


public class CustomDataSource implements FlinkStreamSource<Row> {

    private JSONObject config;

    private static Path PATH = null;

    private final static String SCHEMA = "schema";
    private TypeInformation<Row> typeInfo;


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

        return CheckConfigUtil.check(config);
    }

    @Override
    public void prepare(FlinkEnvironment env) {
        typeInfo = SchemaUtil.getTypeInformation(config.getJSONObject(SCHEMA));
    }

    @Override
    public Integer getParallelism() {

        // 默认为1,
        return config.getInteger("parallelism") == null ? 1 : config.getInteger("parallelism");
    }

    @Override
    public String getName() {

        return StringUtils.isEmpty(config.getString("name")) ? config.getString("plugin_name") : config.getString("name");
    }

    @Override
    public DataStream<Row> getStreamData(FlinkEnvironment env) throws NoSuchFieldException {


        List<String> strs = new ArrayList();
        for (String s :  config.getJSONObject(SCHEMA).keySet()) {
            strs.add(config.getJSONObject(SCHEMA).getString(s));
        }
        Table table = env.getStreamTableEnvironment().fromValues(
                TypeConversions.fromLegacyInfoToDataType(typeInfo),
                call(String.valueOf(BuiltInFunctionDefinitions.ROW), strs.toArray(new String[strs.size()]))
        );
        System.out.println("开始执行");

        return TableUtil.tableToDataStream(env.getStreamTableEnvironment(), table, false);
    }


}
