package com.filling.utils;

import com.alibaba.fastjson.JSONObject;
import com.filling.web.rest.FillingEdgeJobsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.okhttp3.*;

import java.io.IOException;
import java.time.Duration;


/**
 * @author zihjiang
 * edge操作相关工具类
 */
public class EdgeUtils {

    private final static Logger log = LoggerFactory.getLogger(EdgeUtils.class);

    private static Integer TIMEOUT = 60;

    /**
     * 新增edge任务
     *
     * @param edgeHttpUrl edge地址. 不带/
     * @param pipelineId  自定义的pipelineid
     * @param bodyStr     任务参数
     * @return uuid
     * @throws IOException
     */
    public static String save(String edgeHttpUrl, String pipelineId, String bodyStr) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(TIMEOUT))
            .callTimeout(Duration.ofSeconds(TIMEOUT))
            .readTimeout(Duration.ofSeconds(TIMEOUT))
            .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, bodyStr);
        Request request = new Request.Builder()
            .url(edgeHttpUrl + "/rest/v1/pipeline/" + pipelineId)
            .method("PUT", body)
            .addHeader("Content-Type", "application/json")
            .build();
        Response response = client.newCall(request).execute();
        String responseStr = response.body().string();
        log.debug("create edge job");
        log.debug(responseStr);
        if (response.isSuccessful()) {
            return JSONObject.parseObject(responseStr).getString("uuid");
        }
        return null;
    }

    /**
     * 修改edge任务
     *
     * @param edgeHttpUrl edge地址. 不带/
     * @param pipelineId  自定义的pipelineid
     * @param bodyStr     任务参数
     * @return uuid
     * @throws IOException
     */
    public static String update(String edgeHttpUrl, String pipelineId, String bodyStr) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(TIMEOUT))
            .callTimeout(Duration.ofSeconds(TIMEOUT))
            .readTimeout(Duration.ofSeconds(TIMEOUT))
            .build();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, bodyStr);
        log.debug("update edge job body: {}", JSONObject.parseObject(bodyStr).toJSONString());
        Request request = new Request.Builder()
            .url(edgeHttpUrl + "/rest/v1/pipeline/" + pipelineId)
            .method("POST", body)
            .addHeader("Content-Type", "application/json")
            .build();
        Response response = client.newCall(request).execute();
        log.debug("update edge job");
        String responseStr = response.body().string();
        log.debug(responseStr);
        if (response.isSuccessful()) {
            return JSONObject.parseObject(responseStr).getString("uuid");
        }
        return null;
    }

    /**
     * 预览edge任务的结果
     *
     * @param edgeHttpUrl edge地址. 不带/
     * @param pipelineId  pipelineid
     * @return
     * @throws IOException
     */
    public static JSONObject preview(String edgeHttpUrl, String pipelineId) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(TIMEOUT))
            .callTimeout(Duration.ofSeconds(TIMEOUT))
            .readTimeout(Duration.ofSeconds(TIMEOUT))
            .build();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
            .url(edgeHttpUrl + "/rest/v1/pipeline/" + pipelineId + "/preview")
            .method("POST", body)
            .build();
        Response response = client.newCall(request).execute();
        log.debug("preview edge job");
        String responseStr = response.body().string();
        log.debug(responseStr);
        if (response.isSuccessful()) {
            String previewId = JSONObject.parseObject(responseStr).getString("previewerId");

            return getPreviewData(edgeHttpUrl, pipelineId, previewId);
        } else {
            return null;
        }
    }

    private static JSONObject getPreviewData(String edgeHttpUrl, String pipelineId, String previewerId) throws IOException {

        OkHttpClient client = new OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(TIMEOUT))
            .callTimeout(Duration.ofSeconds(TIMEOUT))
            .readTimeout(Duration.ofSeconds(TIMEOUT))
            .build();

        Request request = new Request.Builder()
            .url(edgeHttpUrl + "/rest/v1/pipeline/" + pipelineId + "/preview/" + previewerId + "?edge=true")
            .method("GET", null)
            .build();
        Response response = client.newCall(request).execute();
        log.debug("getPreviewData edge job");
        String responseStr = response.body().string();
        log.debug(responseStr);
        if (response.isSuccessful()) {
            return JSONObject.parseObject(responseStr);
        } else {
            return null;
        }
    }

    public static JSONObject start(String edgeHttpUrl, String pipelineId) throws IOException {

        OkHttpClient client = new OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(TIMEOUT))
            .callTimeout(Duration.ofSeconds(TIMEOUT))
            .readTimeout(Duration.ofSeconds(TIMEOUT))
            .build();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
            .url(edgeHttpUrl + "/rest/v1/pipeline/" + pipelineId + "/start")
            .method("POST", body)
            .build();
        Response response = client.newCall(request).execute();
        log.debug("start edge job");
        String responseStr = response.body().string();
        log.debug(responseStr);
        if (response.isSuccessful()) {
            return JSONObject.parseObject(responseStr);
        } else {
            return null;
        }
    }
}
