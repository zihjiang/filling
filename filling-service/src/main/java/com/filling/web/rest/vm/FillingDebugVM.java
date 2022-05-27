package com.filling.web.rest.vm;

import com.alibaba.fastjson.JSONArray;
import com.filling.domain.FillingEdgeNodes;

import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * debug的数据
 * @author jiangzihan
 */
public class FillingDebugVM {

    /**
     * 日志
     */
    private String log;

    /**
     * 运行状态
     */
    private Boolean status = true;

    /**
     * 预览的数据
     */
    private Map<String, JSONArray> previewData;

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Map<String, JSONArray> getPreviewData() {
        return previewData;
    }

    public void setPreviewData(Map<String, JSONArray> previewData) {
        this.previewData = previewData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FillingDebugVM that = (FillingDebugVM) o;
        return Objects.equals(log, that.log) && Objects.equals(status, that.status) && Objects.equals(previewData, that.previewData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(log, status, previewData);
    }
}
