package com.filling.calculation.flink.batch;


import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.domain.PreviewResult;
import com.filling.calculation.domain.RunModel;
import com.filling.calculation.env.Execution;
import com.filling.calculation.env.RuntimeEnv;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.util.TableUtil;
import com.filling.calculation.plugin.Plugin;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.BatchTableEnvironment;
import org.apache.flink.types.Row;

import java.util.*;

public class FlinkBatchExecution implements Execution<FlinkBatchSource, FlinkBatchTransform, FlinkBatchSink> {

    private JSONObject config;

    private FlinkEnvironment flinkEnvironment;

    public FlinkBatchExecution(FlinkEnvironment flinkEnvironment) {
        this.flinkEnvironment = flinkEnvironment;
    }

    @Override
    public List<PreviewResult> start(List<FlinkBatchSource> sources, List<FlinkBatchTransform> transforms, List<FlinkBatchSink> sinks, RunModel runModel) throws Exception {

        List<PreviewResult> result = new ArrayList();

        List<DataSet> data = new ArrayList<>();
        for (FlinkBatchSource source : sources) {
            Exception exception = new Exception();
            DataSet dataSet = null;
            try {
                baseCheckConfig(source);
                prepare(flinkEnvironment, source);
                dataSet = source.getBatchData(flinkEnvironment);
                data.add(dataSet);
                registerResultTable(source, dataSet);
            } catch (Exception e) {
                exception = e;
                e.printStackTrace();
            } finally {
                // debugPreview
                switch (runModel){
                    case DEV:
                        result.addAll(debugPreview(source, dataSet, exception));
                        break;
                    case PROD:
                        // Do nothing
                        break;
                    default:
                        break;
                }


            }
        }


        for (FlinkBatchTransform transform : transforms) {
            Exception exception = new Exception();
            DataSet input = null;
            DataSet dataSet = null;
            try {
                prepare(flinkEnvironment, transform);
                baseCheckConfig(transform);
                transform.registerFunction(flinkEnvironment);
                dataSet = fromSourceTable(transform);
                input = transform.processBatch(flinkEnvironment, dataSet);
                registerResultTable(transform, input);
            } catch (Exception e) {
                exception = e;
                e.printStackTrace();
            } finally {
                // debugPreview
                switch (runModel){
                    case DEV:
                        result.addAll(debugPreview(transform, input, exception));
                        break;
                    case PROD:
                        // Do nothing
                        break;
                    default:

                        break;
                }
            }
        }

        for (FlinkBatchSink sink : sinks) {
//            Exception exception = new Exception();
            try {
                baseCheckConfig(sink);
                prepare(flinkEnvironment, sink);
                DataSet dataSet = fromSourceTable(sink);
                sink.outputBatch(flinkEnvironment, dataSet);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//                result.add(debugPreview(sink, dataSet, exception));
            }

        }
        try {
            flinkEnvironment.getBatchEnvironment().execute(flinkEnvironment.getJobName());
//            System.out.println("System.out.println: " + flinkEnvironment.getBatchEnvironment().getExecutionPlan());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private  void registerResultTable(Plugin plugin, DataSet dataSet) {
        JSONObject config = plugin.getConfig();
        if (config.containsKey(Plugin.RESULT_TABLE_NAME)) {
            String name = config.getString(Plugin.RESULT_TABLE_NAME);
            BatchTableEnvironment tableEnvironment = flinkEnvironment.getBatchTableEnvironment();
            if (!TableUtil.tableExists(tableEnvironment, name)) {
                if (config.containsKey("field_name")) {
                    String fieldName = config.getString("field_name");
                    tableEnvironment.createTemporaryView(name, dataSet, fieldName);
                } else {
                    if(!Objects.isNull(dataSet)) {
                        tableEnvironment.createTemporaryView(name, dataSet);
                    }
                }
            }
        }

    }


    private DataSet fromSourceTable(Plugin plugin) {
        JSONObject config = plugin.getConfig();
        if (config.containsKey(Plugin.SOURCE_TABLE_NAME)) {
            BatchTableEnvironment tableEnvironment = flinkEnvironment.getBatchTableEnvironment();
            Table table = tableEnvironment.from(config.getString(Plugin.SOURCE_TABLE_NAME));
            return TableUtil.tableToDataSet(tableEnvironment, table);
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

    public List<PreviewResult> debugPreview(Plugin plugin, DataSet dataSet, Exception exception) {

        // 表名
        List<String> tables = getTableByPlugins(plugin);
        // 结果
        List<PreviewResult> result = new ArrayList();

            for (String table: tables) {
                PreviewResult _result = new PreviewResult();
                _result.setTableName(table);
                _result.setType(getType(plugin.getClass().toString()));

                try {
                    Table table1 = flinkEnvironment.getBatchTableEnvironment().from(table);

                    DataSet<Row> dsRow = flinkEnvironment.getBatchTableEnvironment().toDataSet(table1, Row.class);


                    _result.setColumn(getColumn(table1));
                    _result.setData(getData(dsRow, getColumn(table1)));



                } catch (Exception e) {
                    if(Objects.isNull(exception)) {
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

    private static void baseCheckConfig(Plugin plugin) throws Exception {
        CheckResult checkResult = plugin.checkConfig();
                if (!checkResult.isSuccess()) {
                    throw  new Exception(String.format("Plugin[%s] contains invalid config, error: %s\n"
                        , plugin.getClass().getName(), checkResult.getMsg()));
                }
            }
    private static void prepare(RuntimeEnv env, Plugin plugin) {
//        System.out.println(plugin.getConfig().toJSONString());
            plugin.prepare(env);
    }

    /**
     * 根据类名, 获取类型
     * @param className
     * @return
     */
    public String getType(String className) {
        if(className !=null && className.indexOf(".source.")>=0) {
            return "source";
        } else if( className !=null && className.indexOf(".transform.")>=0) {
            return "transform";
        } else if(className !=null && className.indexOf(".sink.")>=0 ) {
            return "sink";
        } else {
            return "unknown";
        }
    }


    /**
     * 根据表名, 获取列集合
     * @param table
     * @return
     */
    private List<Map<String, String>> getColumn(Table table) {
        List<Map<String, String>> columns = new ArrayList();
        for (String fieldName: table.getResolvedSchema().getColumnNames()) {
            Map<String, String> map = new HashMap();
            map.put(fieldName,table.getResolvedSchema().getColumn(fieldName).get().getDataType().toString());
            columns.add(map);
        }
        return columns;
    }

    private List<String[]> getData(DataSet<Row> rowDataSet, List<Map<String, String>> columes) throws Exception {
        List<String[]> data = new ArrayList();

        for (Object o : rowDataSet.collect().toArray()) {
            Row row = (Row)o;
            List<String> _data = new ArrayList();
            for (Map<String, String> column: columes) {
                for(String map: column.keySet()) {
                    _data.add(row.getField(map).toString() );
                }
            }
            data.add(_data.toArray(new String[0]));
        }
        return data;
    }

    /**
     * g
     * @param plugin
     * @return
     */
    private List<String> getTableByPlugins(Plugin plugin) {

        List<String> result = new ArrayList<>();
        JSONObject config = plugin.getConfig();

        result.add(config.getString("result_table_name"));
        if(config.containsKey("plugin_name")) {
            String pluginName = config.getString("plugin_name");
            switch (pluginName) {
                case "DataSelector":
                    if(config.containsKey("select.result_table_name")) {
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

