package com.filling.config;

import com.filling.client.ClusterClient;
import com.filling.client.standalone.StandaloneClusterClient;
import com.filling.web.rest.AccountResource;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.deployment.application.ApplicationRunner;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.dispatcher.DispatcherGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class FlinkInitConfiguration{
    private final Logger log = LoggerFactory.getLogger(FlinkInitConfiguration.class);
    @Autowired
    ApplicationProperties flink;

    @Autowired
    ClusterClient client;
    @PostConstruct
    public void pingStart() {
//        ClusterClient client = new StandaloneClusterClient();
        client.init();
    }
}
