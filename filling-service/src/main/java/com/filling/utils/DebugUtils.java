package com.filling.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;

public class DebugUtils {
    private static final Logger log = LoggerFactory.getLogger(DebugUtils.class);
    public File flinkDebug(File file, String jobText) throws IOException {
        log.info("create tempFile: {}", file.getAbsolutePath());
        Runtime runtime = Runtime.getRuntime();
        Process process;
        BufferedReader br = null;
        BufferedWriter wr = null;
        try {
            process = runtime.exec("java -cp  /Users/jiangzihan/filling/filling-service/flink-jars/flink-table-runtime_2.12-1.14.3.jar:/Users/jiangzihan/filling/filling-service/flink-jars/flink-scala_2.12-1.14.3.jar:/Users/jiangzihan/filling/filling-service/flink-jars/scala-library-2.12.15.jar:/Users/jiangzihan/filling/filling-service/flink-jars/flink-table-planner_2.12-1.14.3.jar:/Users/jiangzihan/filling/filling-service/flink-jars/flink-streaming-java_2.12-1.14.3.jar:/Users/jiangzihan/filling/filling-core/target/filling-core-1.0-SNAPSHOT.jar com.filling.calculation.Filling /Users/jiangzihan/filling/filling-core/src/test/resources/flink/Customdata2Console.json debug");

            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            wr = new BufferedWriter(new FileWriter(file));

            String inline;
            while ((inline = br.readLine()) != null) {
                System.out.println(inline);
                wr.write(inline);
                wr.write("\n");
//                    session.getBasicRemote().sendText("\n");    //换行
            }
            br.close();
            wr.close();

            System.out.println("flink debug");
        } finally {
            if (br != null) {
                br.close();
            }
            if (wr != null) {
                wr.close();
            }
        }
        return null;
    }

//    public static void main(String[] args) throws IOException {
//        flinkDebug();
//    }
}
