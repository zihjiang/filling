package com.filling.calculation;


import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.apis.BaseSink;
import com.filling.calculation.apis.BaseSource;
import com.filling.calculation.apis.BaseTransform;
import com.filling.calculation.config.ConfigBuilder;
import com.filling.calculation.domain.PreviewResult;
import com.filling.calculation.domain.RunModel;
import com.filling.calculation.env.Execution;
import com.filling.calculation.env.RuntimeEnv;
import com.filling.calculation.flink.util.Engine;
import com.filling.calculation.flink.util.PluginType;
import com.filling.calculation.plugin.Plugin;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


public class Filling {

    public static List<PreviewResult> entryPoint(String jsonObject, Engine engine, RunModel runModel) throws Exception {

        ConfigBuilder configBuilder = new ConfigBuilder(jsonObject, engine);
        List<BaseSource> sources = configBuilder.createPlugins(PluginType.SOURCE);
        List<BaseTransform> transforms = configBuilder.createPlugins(PluginType.TRANSFORM);
        List<BaseSink> sinks = configBuilder.createPlugins(PluginType.SINK);
        Execution execution = configBuilder.createExecution();
        return execution.start(sources, transforms, sinks, runModel);
    }


    private static void showFatalError(Throwable throwable) {
        System.out.println(
                "\n\n===============================================================================\n\n");
        String errorMsg = throwable.getMessage();
        System.out.println("Fatal Error, \n");
        System.out.println("Reason: " + errorMsg + "\n");
        System.out.println("Exception StackTrace: " + ExceptionUtils.getStackTrace(throwable));
        System.out.println(
                "\n===============================================================================\n\n\n");
    }

    private static void prepare(RuntimeEnv env, List<? extends Plugin>... plugins) {
        for (List<? extends Plugin> pluginList : plugins) {
            pluginList.forEach(plugin -> plugin.prepare(env));
        }

    }

    public static void main(String[] args) throws Exception {


        if (args.length < 1) {
            System.out.println("文件路径是必须的");
        } else {
            RunModel runModel;

            if (args.length == 2) {
                runModel = RunModel.valueOf(args[1]);
            } else {
                runModel = RunModel.DEV;
            }

            String jsonStr;
            if (args[0].startsWith("/")) {
                jsonStr = Files.lines(Paths.get(args[0]), StandardCharsets.UTF_8).collect(Collectors.joining());
            } else {
                final Base64.Decoder decoder = Base64.getDecoder();
                jsonStr = new String(decoder.decode(args[0]), "UTF-8");
            }


            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            System.out.println("json: " + jsonObject.toJSONString());
            System.out.println("model: " + runModel);
            entryPoint(jsonStr, Engine.FLINK, runModel);
        }
    }
}
