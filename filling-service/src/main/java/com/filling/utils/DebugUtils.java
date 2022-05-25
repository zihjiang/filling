package com.filling.utils;

import com.filling.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.LogOutputStream;

import java.io.*;

public class DebugUtils {
    private static final Logger log = LoggerFactory.getLogger(DebugUtils.class);


    static String JARSLIB_STATIC;

    static String MAINJAR_STATIC;

    @Value("${application.flink.debug-lib-dir}")
    public void setJarslibStatic(String name) {
        DebugUtils.JARSLIB_STATIC = name;
    }

    @Value("${application.flink.jar}")
    public void setMainjarStatic(String name) {
        DebugUtils.MAINJAR_STATIC = name;
    }

//    public static void main(String[] args) throws IOException {
//        flinkDebug();
//    }
}
