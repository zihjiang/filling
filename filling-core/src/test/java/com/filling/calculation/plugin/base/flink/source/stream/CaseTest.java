package com.filling.calculation.plugin.base.flink.source.stream;

import com.filling.calculation.Filling;
import com.filling.calculation.domain.RunModel;
import com.filling.calculation.flink.util.Engine;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class CaseTest {

    private String configPath;

    private String rootPath;

    @Before
    public void setup() {

        rootPath = this.getClass().getResource("/").getPath();
    }

    @Test
    public void testCase01() throws Exception {
        configPath = "flink/case01.json";
        String str = Files.lines(Paths.get(rootPath + configPath), StandardCharsets.UTF_8).collect(Collectors.joining());

        Filling.entryPoint(str, Engine.FLINK, RunModel.PROD);
    }

    @Test
    public void testCase02() throws Exception {
        configPath = "flink/case02.json";
        String str = Files.lines(Paths.get(rootPath + configPath), StandardCharsets.UTF_8).collect(Collectors.joining());

        Filling.entryPoint(str, Engine.FLINK, RunModel.PROD);
    }

    @Test
    public void testCase03() throws Exception {
        configPath = "flink/case03.json";
        String str = Files.lines(Paths.get(rootPath + configPath), StandardCharsets.UTF_8).collect(Collectors.joining());

        Filling.entryPoint(str, Engine.FLINK, RunModel.PROD);
    }

    @Test
    public void testCase04() throws Exception {
        configPath = "flink/case04.json";
        String str = Files.lines(Paths.get(rootPath + configPath), StandardCharsets.UTF_8).collect(Collectors.joining());

        Filling.entryPoint(str, Engine.FLINK, RunModel.PROD);
    }
}
