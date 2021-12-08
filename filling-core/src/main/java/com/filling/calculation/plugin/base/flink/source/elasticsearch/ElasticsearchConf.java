package com.filling.calculation.plugin.base.flink.source.elasticsearch;

import java.util.List;

public class ElasticsearchConf {

    private static final long serialVersionUID = 2L;

    /**
     * elasticsearch address -> ip:port
     * localhost:9200
     */
    private List<String> hosts;

    /**
     * es index name
     */
    private String index;

    /**
     * es type name
     */
    private String type;

    /**
     * es doc id
     */
    private List<String> ids;

    /**
     * is open basic auth.
     */
    private boolean authMesh = false;

    /**
     * basic auth : username
     */
    private String username;

    /**
     * basic auth : password
     */
    private String password;

    /**
     * client socket timeout
     */
    private int socketTimeout;

    /**
     * client keepAlive time
     */
    private int keepAliveTime;

    /**
     * client connect timeout
     */
    private int connectTimeout;

    /**
     * client request timeout
     */
    private int requestTimeout;

    /**
     * Assigns maximum connection per route value.
     */
    private int maxConnPerRoute;

    private Integer bulkSize = 2000;

    /** table field names */
    private String[] fieldNames;

    private Boolean autoSchema = false;

    public int getMaxConnPerRoute() {
        return maxConnPerRoute;
    }

    public void setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public boolean isAuthMesh() {
        return authMesh;
    }

    public void setAuthMesh(boolean authMesh) {
        this.authMesh = authMesh;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Integer getBulkSize() {
        return bulkSize;
    }

    public void setBulkSize(Integer bulkSize) {
        this.bulkSize = bulkSize;
    }

    public String[] getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
    }

    public Boolean getAutoSchema() {
        return autoSchema;
    }

    public void setAutoSchema(Boolean autoSchema) {
        this.autoSchema = autoSchema;
    }
}
