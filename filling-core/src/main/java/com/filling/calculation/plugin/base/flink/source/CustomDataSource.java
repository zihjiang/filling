package com.filling.calculation.plugin.base.flink.source;

import com.alibaba.fastjson.JSONArray;
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class CustomDataSource implements FlinkStreamSource<Row> {

    private JSONObject config;

    private InputFormat inputFormat;

    private static Path PATH = null;

    private final static String SOURCE_FORMAT = "format.type";
    private final static String SCHEMA = "schema";
    private final static String SIMPLE_DATA = "simple_data";


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
        // 默认为json
        config.putIfAbsent(SOURCE_FORMAT, "json");
        // 如果schema为json, 并且没有设置schema, 则用simple_data第一条作为schema
        String simpleData = config.getString(SCHEMA).replaceAll("\n|\r", "");
        config.put(SIMPLE_DATA, simpleData);
        if ("json".equals(config.getString(SOURCE_FORMAT))) {
            if (simpleData.startsWith("[")) {
                // 如果是数组, 取第一条当做schema
                config.put(SCHEMA, JSONArray.parseArray(simpleData).getJSONObject(0).toJSONString());
            } else {
                config.put(SCHEMA, simpleData);
            }

        } else if ("string".equals(config.getString(SOURCE_FORMAT))) {
            return CheckConfigUtil.check(config, SOURCE_FORMAT, SCHEMA);
        }
        return CheckConfigUtil.check(config, SOURCE_FORMAT, SCHEMA, SCHEMA);

    }

    @Override
    public void prepare(FlinkEnvironment env) {
        // 先写入临时文件, 其他的处理流程和fileSource基本一致
        try {
            PATH = getTempFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String format = config.getString(SOURCE_FORMAT);
        String schemaContent = config.getString(SCHEMA);
        switch (format) {
            case "json":
                Object jsonSchemaInfo = JSONObject.parse(schemaContent);
                RowTypeInfo jsonInfo = SchemaUtil.getTypeInformation((JSONObject) jsonSchemaInfo);
                JsonRowInputFormat jsonInputFormat = new JsonRowInputFormat(PATH, null, jsonInfo);
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
                RowCsvInputFormat rowCsvInputFormat = new RowCsvInputFormat(PATH, csvType, true);
                this.inputFormat = rowCsvInputFormat;
                break;
            case "text":
            case "string":
                TextRowInputFormat textInputFormat = new TextRowInputFormat(PATH);
                inputFormat = textInputFormat;
                break;
            default:
                break;
        }

    }

    /**
     * 获取临时文件目录
     *
     * @return
     */
    private Path getTempFile() throws IOException {


        String tempPath = System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID();

        Files.createFile(Paths.get(tempPath));

        String simpleData = config.getString(SIMPLE_DATA);
        if (simpleData.startsWith("[")) {
            JSONArray jsonArray = JSONArray.parseArray(simpleData);
            for (Object o : jsonArray) {
                byte data[] = o.toString().getBytes(StandardCharsets.UTF_8);
                Files.write(Paths.get(tempPath), data, new StandardOpenOption[]{StandardOpenOption.APPEND});
                Files.write(Paths.get(tempPath), "\n".getBytes(StandardCharsets.UTF_8), new StandardOpenOption[]{StandardOpenOption.APPEND});
            }
        } else {

            byte data[] = simpleData.getBytes();
            Files.write(Paths.get(tempPath), data, new StandardOpenOption[]{StandardOpenOption.APPEND});
        }
        return new Path(tempPath);
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
        return env.getStreamExecutionEnvironment().createInput(inputFormat).setParallelism(getParallelism()).name(getName());
    }
}
