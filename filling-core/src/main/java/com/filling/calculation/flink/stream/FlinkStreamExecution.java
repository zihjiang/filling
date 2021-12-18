package com.filling.calculation.flink.stream;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.domain.PreviewResult;
import com.filling.calculation.domain.RunModel;
import com.filling.calculation.env.Execution;
import com.filling.calculation.env.RuntimeEnv;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.util.TableUtil;
import com.filling.calculation.plugin.Plugin;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.*;

public class FlinkStreamExecution implements Execution<FlinkStreamSource, FlinkStreamTransform, FlinkStreamSink> {

    private JSONObject config;

    private FlinkEnvironment flinkEnvironment;


    public FlinkStreamExecution(FlinkEnvironment streamEnvironment) {
        this.flinkEnvironment = streamEnvironment;
    }

    @Override
    public void start(List<FlinkStreamSource> sources, List<FlinkStreamTransform> transforms, List<FlinkStreamSink> sinks, RunModel runModel) throws Exception {

        List<DataStream> data = new ArrayList<>();
        for (FlinkStreamSource source : sources) {
            DataStream dataStream;
            try {
                baseCheckConfig(source);
                prepare(flinkEnvironment, source);
                dataStream = source.getStreamData(flinkEnvironment);
                data.add(dataStream);
                registerResultTable(source, dataStream);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        DataStream input = data.get(0);
        for (FlinkStreamTransform transform : transforms) {
            try {
                prepare(flinkEnvironment, transform);
                baseCheckConfig(transform);
                DataStream stream = fromSourceTable(transform);
                if (Objects.isNull(stream)) {
                    stream = input;
                }
                transform.registerFunction(flinkEnvironment);
                input = transform.processStream(flinkEnvironment, stream);
                registerResultTable(transform, input);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        for (FlinkStreamSink sink : sinks) {
            baseCheckConfig(sink);
            prepare(flinkEnvironment, sink);
            DataStream stream = fromSourceTable(sink);
            if (Objects.isNull(stream)) {
                stream = input;
            }
            sink.outputStream(flinkEnvironment, stream);
        }

        flinkEnvironment.getStreamExecutionEnvironment().execute(flinkEnvironment.getJobName());
    }

    private void registerResultTable(Plugin plugin, DataStream dataStream) {
        JSONObject config = plugin.getConfig();
        if (config.containsKey(RESULT_TABLE_NAME)) {
            String name = config.getString(RESULT_TABLE_NAME);
            StreamTableEnvironment tableEnvironment = flinkEnvironment.getStreamTableEnvironment();
            if (!TableUtil.tableExists(tableEnvironment, name)) {
                if (config.containsKey("field_name")) {
                    String fieldName = config.getString("field_name");
                    tableEnvironment.createTemporaryView(name, dataStream, fieldName);
                } else {
                    tableEnvironment.createTemporaryView(name, dataStream);
                }
            }
        }
    }

    private DataStream fromSourceTable(Plugin plugin) {
        JSONObject config = plugin.getConfig();
        if (config.containsKey(SOURCE_TABLE_NAME)) {
            StreamTableEnvironment tableEnvironment = flinkEnvironment.getStreamTableEnvironment();
            Table table = tableEnvironment.scan(config.getString(SOURCE_TABLE_NAME));
            return TableUtil.tableToDataStream(tableEnvironment, table, true);
        }
        return null;
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
        return new CheckResult(true, "");
    }

    @Override
    public void prepare(Void prepareEnv) {
    }

    private static void baseCheckConfig(Plugin plugin) throws Exception {
        CheckResult checkResult = plugin.checkConfig();
        if (!checkResult.isSuccess()) {
            throw new Exception(String.format("Plugin[%s] contains invalid config, error: %s\n"
                    , plugin.getClass().getName(), checkResult.getMsg()));
        }
    }

    private static void prepare(RuntimeEnv env, Plugin plugin) {
        plugin.prepare(env);
    }

    public List<PreviewResult> debugPreview(Plugin plugin, DataStream dataStream, Exception exception) {

        // 表名
        List<String> tables = getTableByPlugins(plugin);
        // 结果
        List<PreviewResult> result = new ArrayList();

        for (String table : tables) {
            PreviewResult _result = new PreviewResult();
            _result.setTableName(table);
            _result.setType(getType(plugin.getClass().toString()));

            try {
                Table table1 = flinkEnvironment.getStreamTableEnvironment().from(table);


                DataStream<Row> dsRow = flinkEnvironment.getStreamTableEnvironment().toDataStream(table1);

                _result.setColumn(getColumn(table1));
                _result.setData(getData(dsRow, getColumn(table1)));

            } catch (Exception e) {
                if (Objects.isNull(exception)) {
                    _result.setError(e.getLocalizedMessage());
                } else {
                    _result.setError(exception.getLocalizedMessage());
                }
            } finally {
                result.add(_result);
            }

            if (Objects.isNull(_result.getError())) {
                _result.setStatus("0");
            } else {
                _result.setStatus("1");
            }

        }

        return result;
    }

    /**
     * 根据类名, 获取类型
     *
     * @param className
     * @return
     */
    public String getType(String className) {
        if (className != null && className.indexOf(".source.") >= 0) {
            return "source";
        } else if (className != null && className.indexOf(".transform.") >= 0) {
            return "transform";
        } else if (className != null && className.indexOf(".sink.") >= 0) {
            return "sink";
        } else {
            return "unknown";
        }
    }


    /**
     * 根据表名, 获取列集合
     *
     * @param table
     * @return
     */
    private List<Map<String, String>> getColumn(Table table) {
        List<Map<String, String>> columns = new ArrayList();
        for (String fieldName : table.getResolvedSchema().getColumnNames()) {
            Map<String, String> map = new HashMap();
            map.put(fieldName, table.getResolvedSchema().getColumn(fieldName).get().getDataType().toString());
            columns.add(map);
        }
        return columns;
    }

    private List<String[]> getData(DataStream<Row> rowDataStream, List<Map<String, String>> columes) throws Exception {
        List<String[]> data = new ArrayList();

//        rowDataStream.map(new MapFunction<Row, Object>() {
//            List<String> _data = new ArrayList();
//            @Override
//            public Object map(Row row) {
//                for (Map<String, String> column : columes) {
//                    for (String map : column.keySet()) {
//                        _data.add(row.getField(map).toString());
//                    }
//                }
//
//                data.add(_data.toArray(new String[0]));
//                System.out.println("data.add -------");
//                return null;
//            }
//        });

//        rowDataStream.executeAndCollect();
//        Iterator<Row> rowIterator = rowDataStream.executeAndCollect();
//        while (rowIterator.hasNext()) {
//            Row row = Row.withNames();
//            System.out.println(rowIterator.next().getField("metric"));
//
//            List<String> _data = new ArrayList();
//            for (Map<String, String> column : columes) {
//                for (String map : column.keySet()) {
//                    _data.add(row.getField(map).toString());
//                }
//            }
//            data.add(_data.toArray(new String[0]));
//
//        }
        return data;
    }

    /**
     * g
     *
     * @param plugin
     * @return
     */
    private List<String> getTableByPlugins(Plugin plugin) {

        List<String> result = new ArrayList<>();
        JSONObject config = plugin.getConfig();

        result.add(config.getString("result_table_name"));
        if (config.containsKey("plugin_name")) {
            String pluginName = config.getString("plugin_name");
            switch (pluginName) {
                case "DataSelector":
                    if (config.containsKey("select.result_table_name")) {
                        result = config.getObject("select.result_table_name", List.class);
                    }
                    break;
                default:
                    break;
            }
        }

        return result;
    }
}
