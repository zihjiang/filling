package com.filling.calculation.plugin.base.flink.sink;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.types.Row;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class ClickHouseOutputFormat implements OutputFormat<Row> {


    JSONObject config;
    PreparedStatement ps;
    private Connection connection;

    private static String driverName;
    private static String dbUrl;
    private static String username;
    private static String password;
    private static String query;
    private int batchSize = 5000;
    private int batchIntervalMs = 200;
    private int maxRetries = 5;
    private JSONArray params;

    private Integer _count = 0;

    public ClickHouseOutputFormat(String driverName, String dbUrl, String username, String password, String query, Integer batchSize, Integer batchIntervalMs, Integer maxRetries, JSONArray params) {

        ClickHouseOutputFormat.driverName = driverName;
        ClickHouseOutputFormat.dbUrl = dbUrl;
        ClickHouseOutputFormat.username = username;
        ClickHouseOutputFormat.password = password;
        ClickHouseOutputFormat.query = query;
        this.batchSize = batchSize;
        this.batchIntervalMs = batchIntervalMs;
        this.maxRetries = maxRetries;
        this.params = params;

    }


    @Override
    public void configure(Configuration configuration) {


    }

    @Override
    public void open(int i, int i1) throws IOException {

        try {
            connection = getConnection();
            ps = this.connection.prepareStatement(query);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void writeRecord(Row row) {
        //组装数据，执行插入操作
        try {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, row.getField(params.getString(i)));
                _count++;
                if (_count >= batchSize) {
                    ps.executeBatch();
                    _count = 0;
                }
            }
            ps.addBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            // 关闭前提交
            ps.executeBatch();
            //关闭连接和释放资源
            if (connection != null) {
                connection.close();
            }
            if (ps != null) {
                ps.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName(driverName);
            con = DriverManager.getConnection(dbUrl, username, password);
        } catch (Exception e) {
            System.out.println("-----------jdbc get connection has exception , msg = " + e.getMessage());
        }
        return con;
    }
}