package com.filling.calculation.plugin.base.flink.source;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.batch.FlinkBatchSource;
import com.filling.calculation.flink.stream.FlinkStreamSource;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.typeinfo.SqlTimeTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.connector.jdbc.JdbcInputFormat;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.flink.api.common.typeinfo.BasicTypeInfo.*;
import static org.apache.flink.api.common.typeinfo.PrimitiveArrayTypeInfo.BYTE_PRIMITIVE_ARRAY_TYPE_INFO;

public class JdbcSource implements  FlinkStreamSource<Row>, FlinkBatchSource<Row> {

    private JSONObject config;
    private String tableName;
    private String driverName;
    private String dbUrl;
    private String username;
    private String password;
    private int fetchSize = Integer.MIN_VALUE;
    private Set<String> fields;

    private static final Pattern COMPILE = Pattern.compile("select (.+) from (.+).*");


    private HashMap<String, TypeInformation> informationMapping = new HashMap<>();

    private JdbcInputFormat jdbcInputFormat;

    {
        informationMapping.put("VARCHAR", STRING_TYPE_INFO);
        informationMapping.put("String", STRING_TYPE_INFO);
        informationMapping.put("BOOLEAN", BOOLEAN_TYPE_INFO);
        informationMapping.put("TINYINT", BYTE_TYPE_INFO);
        informationMapping.put("SMALLINT", SHORT_TYPE_INFO);
        informationMapping.put("INTEGER", INT_TYPE_INFO);
        informationMapping.put("MEDIUMINT", INT_TYPE_INFO);
        informationMapping.put("Int32", INT_TYPE_INFO);
        informationMapping.put("INT", INT_TYPE_INFO);
        informationMapping.put("BIGINT", LONG_TYPE_INFO);
        informationMapping.put("Float64", DOUBLE_TYPE_INFO);
        informationMapping.put("FLOAT", FLOAT_TYPE_INFO);
        informationMapping.put("DOUBLE", DOUBLE_TYPE_INFO);
        informationMapping.put("CHAR", STRING_TYPE_INFO);
        informationMapping.put("TEXT", STRING_TYPE_INFO);
        informationMapping.put("LONGTEXT", STRING_TYPE_INFO);
        informationMapping.put("DateTime", SqlTimeTypeInfo.TIMESTAMP);
        informationMapping.put("DATE", SqlTimeTypeInfo.DATE);
        informationMapping.put("TIME", SqlTimeTypeInfo.TIME);
        informationMapping.put("TIMESTAMP", SqlTimeTypeInfo.TIMESTAMP);
        informationMapping.put("DECIMAL", BIG_DEC_TYPE_INFO);
        informationMapping.put("BINARY", BYTE_PRIMITIVE_ARRAY_TYPE_INFO);

    }

    @Override
    public DataStream<Row> getStreamData(FlinkEnvironment env) {

        return env.getStreamExecutionEnvironment().createInput(jdbcInputFormat).setParallelism(getParallelism()).name(getName());
    }

    @Override
    public DataSet<Row> getBatchData(FlinkEnvironment env) {
        return env.getBatchEnvironment().createInput(jdbcInputFormat);
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
        driverName = config.getString("driver");
        dbUrl = config.getString("url");
        username = config.getString("username");
        String query = config.getString("query");
        Matcher matcher = COMPILE.matcher(query);
        if (matcher.find()) {
            String var = matcher.group(1);
            tableName = matcher.group(2);
            if ("*".equals(var.trim())) {
                //do nothing
            } else {
                LinkedHashSet<String> vars = new LinkedHashSet<>();
                String[] split = var.split(",");
                for (String s : split) {
                    vars.add(s.trim());
                }
                fields = vars;
            }
        }
        if (config.containsKey("password")) {
            password = config.getString("password");
        }
        if (config.containsKey("fetch_size")) {
            fetchSize = config.getInteger("fetch_size");
        }

        jdbcInputFormat = JdbcInputFormat.buildJdbcInputFormat()
                .setDrivername(driverName)
                .setDBUrl(dbUrl)
                .setUsername(username)
                .setPassword(password)
                .setQuery(query)
                .setFetchSize(fetchSize)
                .setRowTypeInfo(getRowTypeInfo())
                .finish();
    }

    private RowTypeInfo getRowTypeInfo() {
        HashMap<String, TypeInformation> map = new LinkedHashMap<>();

        try {
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(dbUrl, username, password);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(connection.getCatalog(), connection.getSchema(), tableName, "%");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataTypeName = columns.getString("TYPE_NAME");
                if (fields == null || fields.contains(columnName)) {
                    map.put(columnName, informationMapping.get(dataTypeName));
                    System.out.println( columnName +":  " + dataTypeName);
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int size = map.size();
        if (fields != null && fields.size() > 0) {
            size = fields.size();
        } else {
            fields = map.keySet();
        }

        TypeInformation<?>[] typeInformation = new TypeInformation<?>[size];
        String[] names = new String[size];
        int i = 0;

        for (String field : fields) {
            typeInformation[i] = map.get(field);
            names[i] = field;
            i++;
        }
        return new RowTypeInfo(typeInformation, names);
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
