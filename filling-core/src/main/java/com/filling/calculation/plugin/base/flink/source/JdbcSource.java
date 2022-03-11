package com.filling.calculation.plugin.base.flink.source;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamSource;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.typeinfo.SqlTimeTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.connector.jdbc.JdbcInputFormat;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

import static org.apache.flink.api.common.typeinfo.BasicTypeInfo.*;
import static org.apache.flink.api.common.typeinfo.PrimitiveArrayTypeInfo.BYTE_PRIMITIVE_ARRAY_TYPE_INFO;

public class JdbcSource implements FlinkStreamSource<Row> {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcSource.class);

    public static final String DRIVER = "driver";
    public static final String URL = "url";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String QUERY = "query";
    public static final String FETCH_SIZE = "fetch_size";

    private JSONObject config;
    private String driverName;
    private String dbUrl;
    private String username;
    private String password;
    private String query;
    private String getFastRow;
    private int fetchSize = Integer.MIN_VALUE;


    private HashMap<String, TypeInformation> informationMapping = new HashMap<>();

    private HashMap<Integer, String> informationMappingNum = new HashMap<>();

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

    {
        informationMappingNum.put(2003, "Array");
        informationMappingNum.put(-5, "BIGINT");
        informationMappingNum.put(-2, "BINARY");
        informationMappingNum.put(-7, "Bit");
        informationMappingNum.put(2004, "Blob");
        informationMappingNum.put(16, "BOOLEAN");
        informationMappingNum.put(1, "CHAR");
        informationMappingNum.put(2005, "Clob");
        informationMappingNum.put(91, "DATE");
        informationMappingNum.put(70, "DATALINK");
        informationMappingNum.put(3, "DECIMAL");
        informationMappingNum.put(2001, "Distinct");
        informationMappingNum.put(8, "DOUBLE");
        informationMappingNum.put(6, "FLOAT");
        informationMappingNum.put(4, "INTEGER");
        informationMappingNum.put(2000, "JAVAOBJECT");
        informationMappingNum.put(-16, "LONG VAR CHAR");
        informationMappingNum.put(-15, "NCHAR");
        informationMappingNum.put(2011, "NCLOB");
        informationMappingNum.put(12, "VARCHAR");
        informationMappingNum.put(-3, "VARBINARY");
        informationMappingNum.put(-6, "TINY INT");
        informationMappingNum.put(2014, "TIME STAMT WITH TIME ZONE");
        informationMappingNum.put(93, "TIMESTAMP");
        informationMappingNum.put(92, "TIME");
        informationMappingNum.put(2002, "STRUCT");
        informationMappingNum.put(2009, "SQLXML");
        informationMappingNum.put(5, "SMALLINT");
        informationMappingNum.put(-8, "ROWID");
        informationMappingNum.put(2012, "REFCURSOR");
        informationMappingNum.put(2006, "REF");
        informationMappingNum.put(7, "REAL");
        informationMappingNum.put(-9, "NVARCHAR");
        informationMappingNum.put(2, "NUMERIC");
        informationMappingNum.put(0, "NULL");
        informationMappingNum.put(5, "SMALLINT");
    }

    @Override
    public DataStream<Row> getStreamData(FlinkEnvironment env) {

        return env.getStreamExecutionEnvironment().createInput(jdbcInputFormat).setParallelism(getParallelism()).name(getName());
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
        return CheckConfigUtil.check(config, DRIVER, URL, QUERY);
    }

    @Override
    public void prepare(FlinkEnvironment env) {
        driverName = config.getString(DRIVER);
        dbUrl = config.getString(URL);
        username = config.getString(USERNAME);
        query = config.getString(QUERY);
        getFastRow = config.getString("getFastRow");

        if (config.containsKey(PASSWORD)) {
            password = config.getString(PASSWORD);
        }
        if (config.containsKey(FETCH_SIZE)) {
            fetchSize = config.getInteger(FETCH_SIZE);
        }

        jdbcInputFormat = JdbcInputFormat
                .buildJdbcInputFormat()
                .setDrivername(driverName)
                .setDBUrl(dbUrl)
                .setUsername(username)
                .setPassword(password)
                .setQuery(query)
                .setFetchSize(fetchSize)
                .setRowTypeInfo(getRowTypeInfo()).finish();
    }

    private RowTypeInfo getRowTypeInfo() {
        String[] columnNames = new String[0];
        TypeInformation[] columnTypes = new TypeInformation[0];
        try {
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(dbUrl, username, password);
            try (Statement stmt = connection.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(getFastRow(query))) {
                    int columnCount = rs.getMetaData().getColumnCount();
                    columnNames = new String[columnCount];
                    columnTypes = new TypeInformation[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        int columnTypeNum = rs.getMetaData().getColumnType(i + 1);
                        String columnName = rs.getMetaData().getColumnName(i + 1);
                        TypeInformation columnType = informationMapping.get(informationMappingNum.get(columnTypeNum));
                        if (columnType == null) {
                            LOG.error("columnName: {} columnTypeNum: {} not support, use String", columnName, informationMappingNum.get(columnTypeNum));
                            columnType = informationMapping.get("VARCHAR");
                        }
                        columnNames[i] = columnName;
                        columnTypes[i] = columnType;
                    }
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RowTypeInfo(columnTypes, columnNames);
    }

    private String getFastRow(String query) {
        String limitString = "select * from ({query}) t1 limit 1".replaceAll("\\{query}", query);
        if (getFastRow == null) {
            LOG.warn("getFastRow not set, use default: {}", limitString);
            return limitString;
        }
        return getFastRow;
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
