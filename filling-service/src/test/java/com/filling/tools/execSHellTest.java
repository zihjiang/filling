package com.filling.tools;

import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.LogOutputStream;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class execSHellTest {

    public static Integer exec(String sid, String... command) throws InterruptedException, TimeoutException, IOException {
        int exit = new ProcessExecutor().command(command).destroyOnExit().redirectOutput(new LogOutputStream() {
            protected void processLine(String s) {
                System.out.println("成功日志：" + s);
                System.out.println(s);

            }
        }).redirectError(new LogOutputStream() {
            protected void processLine(String s) {
                System.out.println("错误日志：" + s);
            }
        }).execute().getExitValue();
        return exit;
    }

    public static void main(String[] args) throws InterruptedException, IOException, TimeoutException {
        exec(UUID.randomUUID().toString(), "java", "-cp",  "/Users/jiangzihan/filling/filling-service/flink-jars/flink-table-runtime_2.12-1.14.3.jar:/Users/jiangzihan/filling/filling-service/flink-jars/flink-scala_2.12-1.14.3.jar:/Users/jiangzihan/filling/filling-service/flink-jars/scala-library-2.12.15.jar:/Users/jiangzihan/filling/filling-service/flink-jars/flink-table-planner_2.12-1.14.3.jar:/Users/jiangzihan/filling/filling-service/flink-jars/flink-streaming-java_2.12-1.14.3.jar:/Users/jiangzihan/filling/filling-core/target/filling-core-1.0-SNAPSHOT.jar", "com.filling.calculation.Filling",  "/tmp/1.json",  "debug");
    }
}
