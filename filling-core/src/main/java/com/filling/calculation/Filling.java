package com.filling.calculation;


import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.apis.BaseSink;
import com.filling.calculation.apis.BaseSource;
import com.filling.calculation.apis.BaseTransform;
import com.filling.calculation.config.ConfigBuilder;
import com.filling.calculation.enums.RunModel;
import com.filling.calculation.env.Execution;
import com.filling.calculation.env.Engine;
import com.filling.calculation.flink.util.PluginType;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: calculation-core
 * @description:
 * @author: zihjiang
 * @create: 2021-12-19 16:10
 **/
public class Filling {

    public static void entryPoint(String jsonObject, Engine engine, RunModel runModel) throws Exception {

        ConfigBuilder configBuilder = new ConfigBuilder(jsonObject, engine);
        List<BaseSource> sources = configBuilder.createPlugins(PluginType.SOURCE);
        List<BaseTransform> transforms = configBuilder.createPlugins(PluginType.TRANSFORM);
        List<BaseSink> sinks = configBuilder.createPlugins(PluginType.SINK);
        Execution execution = configBuilder.createExecution();
        execution.start(sources, transforms, sinks, runModel);
    }

    public static void entryPoint(String jsonObject, Engine engine) throws Exception {

        entryPoint(jsonObject, engine, RunModel.RUN);
    }

    public static void main(String[] args) throws Exception {


        if (args.length < 1) {
            System.out.println("文件路径是必须的");
        } else {

            String jsonStr;
            if (args[0].startsWith(File.separator)) {
                jsonStr = Files.lines(Paths.get(args[0]), StandardCharsets.UTF_8).collect(Collectors.joining());
            } else {
                final Base64.Decoder decoder = Base64.getDecoder();
                jsonStr = new String(decoder.decode(args[0]), StandardCharsets.UTF_8);
            }
            RunModel runModel = RunModel.RUN;
            if (args.length >= 2) {
                runModel = args[1].equalsIgnoreCase("debug") ? RunModel.DEBUG : RunModel.RUN;
            }


            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            System.out.println("json: " + jsonObject.toJSONString());
            System.out.println("runModel: " + runModel);
            entryPoint(jsonStr, Engine.FLINK, runModel);
        }
    }
}
