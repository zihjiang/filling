package com.filling.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Filling.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application.flink", ignoreUnknownFields = true)
public class ApplicationProperties {
    String model;
    String url;
    String jar;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getJar() {
        return jar;
    }

    public void setJar(String jar) {
        this.jar = jar;
    }

}
