package com.filling.calculation.plugin.base.flink.source.stream;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.junit.Test;

import java.util.Properties;

/**
 * @Author zihjiang
 * @Date 2021/12/3 21:14
 * @Version 1.0
 */
public class CdcTest {

    @Test
    public void mysqlTest() throws Exception {
        Properties debeziumProperties = new Properties();
        debeziumProperties.put("snapshot.locking.mode", "none");// do not use lock

        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("192.168.100.177")
                .port(3306)
                .databaseList("mydb") // set captured database
                .tableList("mydb.products") // set captured table
                .username("root")
                .password("123456")
                .debeziumProperties(debeziumProperties)
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        // enable checkpoint
        env.enableCheckpointing(3000);

        env
                .fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL Source")
                // set 4 parallel source tasks
                .setParallelism(4)
                .print().setParallelism(1); // use parallelism 1 for sink to keep message ordering

        env.execute("Print MySQL Snapshot + Binlog");
    }
}
