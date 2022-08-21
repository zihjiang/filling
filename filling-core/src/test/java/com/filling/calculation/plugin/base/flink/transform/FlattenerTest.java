package com.filling.calculation.plugin.base.flink.transform;

import com.filling.calculation.Filling;
import com.filling.calculation.env.Engine;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class FlattenerTest {

    private String configPath;

    private String rootPath;

    @Before
    public void setup() {

        rootPath = "/Users/jiangzihan/filling/filling-core/src/test/resources/";
    }

    @Test
    public void DataFlattener2Console() throws Exception {
        configPath = "flink/transform/DataFlattener/DataFlattener2Console.json";
        String str = Files.lines(Paths.get(rootPath + configPath), StandardCharsets.UTF_8).collect(Collectors.joining());

        Filling.entryPoint(str, Engine.FLINK);
    }

    @Test
    public void KafkaDataFlattener2Console() throws Exception {
        configPath = "flink/transform/DataFlattener/KafkaDataFlattener2Console.json";
        String str = Files.lines(Paths.get(rootPath + configPath), StandardCharsets.UTF_8).collect(Collectors.joining());

        Filling.entryPoint(str, Engine.FLINK);
    }
}
