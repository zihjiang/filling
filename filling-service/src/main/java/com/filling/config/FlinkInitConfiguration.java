package com.filling.config;

import com.filling.client.ClusterClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class FlinkInitConfiguration{
    private final Logger log = LoggerFactory.getLogger(FlinkInitConfiguration.class);
    @Autowired
    ApplicationProperties flink;

    @Autowired
    ClusterClient client;
    @PostConstruct
    public void pingStart() throws IOException {
//        ClusterClient client = new StandaloneClusterClient();
        client.init();
    }
}
