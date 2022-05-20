package com.filling.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.LogOutputStream;

import java.io.*;
import java.util.concurrent.TimeoutException;

public class DebugUtils {
    private static final Logger log = LoggerFactory.getLogger(DebugUtils.class);

    public File flinkDebug(File file, String jobString) throws IOException {
        log.info("create tempFile: {}", file.getAbsolutePath());
        try {
            exec(file, "java", "-cp",  "/Users/jiangzihan/filling/filling-service/flink-jars/flink-table-runtime_2.12-1.14.3.jar:/Users/jiangzihan/filling/filling-service/flink-jars/flink-scala_2.12-1.14.3.jar:/Users/jiangzihan/filling/filling-service/flink-jars/scala-library-2.12.15.jar:/Users/jiangzihan/filling/filling-service/flink-jars/flink-table-planner_2.12-1.14.3.jar:/Users/jiangzihan/filling/filling-service/flink-jars/flink-streaming-java_2.12-1.14.3.jar:/Users/jiangzihan/filling/filling-core/target/filling-core-1.0-SNAPSHOT.jar", "com.filling.calculation.Filling",  Base64Utils.encode(jobString),  "debug");
        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }

    private static Integer exec(File file, String... command) throws Exception {
        FileWriter fw = new FileWriter(file, true);
        int exit = new ProcessExecutor().command(command).destroyOnExit().redirectOutput(new LogOutputStream() {
            @Override
            protected void processLine(String s) {
                try {
                    fw.append(s);
                    fw.append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).redirectError(new LogOutputStream() {
            @Override
            protected void processLine(String s) {
                try {
                    fw.append(s);
                    fw.append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).execute().getExitValue();
        fw.close();
        return exit;
    }

//    public static void main(String[] args) throws IOException {
//        flinkDebug();
//    }
}
