package com.filling.calculation.plugin.base.flink.source;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamSource;
import com.filling.calculation.flink.util.SchemaUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.io.InputFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.io.RowCsvInputFormat;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.List;
import java.util.Map;


public class FileStreamSource implements FlinkStreamSource<Row> {

    private JSONObject config;

    private InputFormat inputFormat;

    private final static String PATH = "path";
    private final static String SOURCE_FORMAT = "format.type";
    private final static String SCHEMA = "schema";


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
        return CheckConfigUtil.check(config,PATH,SOURCE_FORMAT,SCHEMA);
    }

    @Override
    public void prepare(FlinkEnvironment env) {
        String path = config.getString(PATH);
        String format = config.getString(SOURCE_FORMAT);
        String schemaContent = config.getString(SCHEMA);
        Path filePath = new Path(path);
        switch (format) {
            case "json":
                Object jsonSchemaInfo = JSONObject.parse(schemaContent);
                RowTypeInfo jsonInfo = SchemaUtil.getTypeInformation((JSONObject) jsonSchemaInfo);
                JsonRowInputFormat jsonInputFormat = new JsonRowInputFormat(filePath, null, jsonInfo);
                inputFormat = jsonInputFormat;
                break;
            case "parquet":
//                final Schema parse = new Schema.Parser().parse(schemaContent);
//                final MessageType messageType = new AvroSchemaConverter().convert(parse);
//                inputFormat = new ParquetRowInputFormat(filePath, messageType);
                break;
            case "orc":
                System.out.println("no support orc");
                break;
            case "csv":
                Object csvSchemaInfo = JSONObject.parse(schemaContent);
                TypeInformation[] csvType = SchemaUtil.getCsvType((List<Map<String, String>>) csvSchemaInfo);
                RowCsvInputFormat rowCsvInputFormat = new RowCsvInputFormat(filePath, csvType, true);
                this.inputFormat = rowCsvInputFormat;
                break;
            case "text":
                TextRowInputFormat textInputFormat = new TextRowInputFormat(filePath);
                inputFormat = textInputFormat;
                break;
            default:
                break;
        }

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
    public DataStream<Row> getStreamData(FlinkEnvironment env)  {
        return env.getStreamExecutionEnvironment().createInput(inputFormat).setParallelism(getParallelism()).name(getName());
    }
}
