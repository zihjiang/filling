package com.filling.calculation.plugin.base.flink.source;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamSource;
import com.filling.calculation.plugin.base.flink.source.mySqlCdc.RowDebeziumDeserializationSchema;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.scala.typeutils.Types;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.types.logical.IntType;
import org.apache.flink.table.types.logical.RowType;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class MysqlCdcSource implements FlinkStreamSource<Row> {

    private JSONObject config;
    private String tableName;
    private String driverName;
    private String dbUrl;
    private String username;
    private String password;
    private int fetchSize = Integer.MIN_VALUE;
    private Set<String> fields;


    private TypeInformation<Row> typeInfo;

    @Override
    public DataStream<Row> getStreamData(FlinkEnvironment env) {

        List<RowType.RowField> fields = new ArrayList<>();
//        fields.add(new RowType.RowField("name", new VarCharType()));
//        fields.add(new RowType.RowField("description", new VarCharType()));
        fields.add(new RowType.RowField("id", new IntType()));


//        typeInfo = SchemaUtil.getTypeInformation(JSON.parseObject("{'id': 1, 'name': '1', 'description': '231'}"));

        TypeInformation[] informations = new TypeInformation[1];
        informations[0] = Types.INT();
//        informations[1] = StringDataTypeInfo.INSTANCE;
//        informations[2] = StringDataTypeInfo.INSTANCE;
        typeInfo = new RowTypeInfo(informations, new String[]{"id"});

        Properties debeziumProperties = new Properties();
        debeziumProperties.put("snapshot.locking.mode", "none");
        MySqlSource<Row> mySqlSource = MySqlSource.<Row>builder()
                .hostname("192.168.100.177")
                .port(3306)
                .databaseList("mydb")
                .tableList("mydb.products")
                .username("root")
                .password("123456")
                .debeziumProperties(debeziumProperties)
                .deserializer(RowDebeziumDeserializationSchema.newBuilder().setPhysicalRowType(new RowType(fields)).setResultTypeInfo(typeInfo).build())
                .build();


        // enable checkpoint
        env.getStreamExecutionEnvironment().enableCheckpointing(3000);

        TableResult inputTable = env.getStreamTableEnvironment().executeSql("CREATE TABLE products (\n" +
                "    id INT,\n" +
                "    name STRING,\n" +
                "    description STRING,\n" +
                "    PRIMARY KEY (id) NOT ENFORCED\n" +
                "  ) WITH (\n" +
                "    'connector' = 'mysql-cdc',\n" +
                "    'hostname' = '192.168.100.177',\n" +
                "    'port' = '3306',\n" +
                "    'username' = 'root',\n" +
                "    'password' = '123456',\n" +
                "    'database-name' = 'mydb',\n" +
                "    'table-name' = 'products'\n" +
                "  )\n");

        Table table = env.getStreamTableEnvironment().from("products");
        DataStream<Tuple2<Boolean, Row>> retractStream = env.getStreamTableEnvironment().toRetractStream(table, Row.class);
//        retractStream.map( d -> {
//            d.g
//        })
        return env.getStreamTableEnvironment().toChangelogStream(table);
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
        return CheckConfigUtil.check(config, "driver", "url", "query");
    }

    @Override
    public void prepare(FlinkEnvironment env) {
//        driverName = config.getString("driver");
//        dbUrl = config.getString("url");
//        username = config.getString("username");
//        String query = config.getString("query");
//        Matcher matcher = COMPILE.matcher(query);
//        if (matcher.find()) {
//            String var = matcher.group(1);
//            tableName = matcher.group(2);
//            if ("*".equals(var.trim())) {
//                //do nothing
//            } else {
//                LinkedHashSet<String> vars = new LinkedHashSet<>();
//                String[] split = var.split(",");
//                for (String s : split) {
//                    vars.add(s.trim());
//                }
//                fields = vars;
//            }
//        }
//        if (config.containsKey("password")) {
//            password = config.getString("password");
//        }
//        if (config.containsKey("fetch_size")) {
//            fetchSize = config.getInteger("fetch_size");
//        }

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
}
