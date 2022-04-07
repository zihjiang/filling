package com.filling.calculation.plugin.base.flink.source.stream;

import com.filling.calculation.Filling;
import com.filling.calculation.env.Engine;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class CustomDataTest {

    private String configPath;

    private String rootPath;

    @Before
    public void setup() {

        rootPath = this.getClass().getResource("/").getPath();
    }

    @Test
    public void testCustomdata2Console() throws Exception {
        configPath = "flink/Customdata2Console.json";

        String str = Files.lines(Paths.get(rootPath + configPath), StandardCharsets.UTF_8).collect(Collectors.joining());

        Filling.entryPoint(str, Engine.FLINK);
    }
}
