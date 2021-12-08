package com.filling.client.standalone;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.filling.client.ClusterClient;
import com.filling.config.ApplicationProperties;
import com.filling.utils.Base64Utils;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Configuration
public class StandaloneClusterClient implements ClusterClient {

    private final Logger log = LoggerFactory.getLogger(StandaloneClusterClient.class);


    @Autowired
    ApplicationProperties flink;

    static JSONObject jarInfo;

    static String jarName;


    @Override
    public void init() throws IOException {
        File file = new File(flink.getJar());
        if(file.exists()) {
            jarName = file.getName();
            if (getLastFile() == null) {
                log.info("uploading filling core.... ");
                uploadJar();
                log.info("uploadJar success");
            }
            jarInfo = getLastFile();
            System.out.println("getLastFile: " + jarInfo.toJSONString());
        } else {
            throw  new IOException("file " + flink.getJar() + "not exists");
        }
    }

    @Override
    public Optional<String> submit(String jobText) {
        Optional<String> jobId = Optional.empty();
        String url = flink.getUrl() + "/jars/{id}/run";

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("programArgs", Base64Utils.encode(jobText));
        bodyJson.put("entryClass", jarInfo.getJSONArray("entry").getJSONObject(0).getString("name"));
        url = url.replace("{id}", jarInfo.getString("id"));

        log.info("submit job url: {}", url);
        log.info("args is: {}", Base64Utils.encode(jobText));
        OkHttpClient client = new OkHttpClient().newBuilder()
            .callTimeout(Duration.ofSeconds(60))
            .readTimeout(Duration.ofSeconds(60))
            .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
            .url(url)
            .method("POST", body)
            .build();
        try(Response response = client.newCall(request).execute()) {
            String respStr = response.body().string();
            JSONObject resp = JSONObject.parseObject(respStr);
            if(StringUtils.isEmpty(resp.getString("errors"))) {
                log.info("submit success");
                log.info("submit result: {}",respStr);
                jobId = Optional.ofNullable(resp.getString("jobid"));
            } else {
                log.info("submit failed");
                new Exception(resp.getString("errors"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return jobId;
        }

    }

    @Override
    public Boolean cancel(String jobId) {
        Boolean result = true;
        String url = flink.getUrl() + "/jobs/{id}/yarn-cancel";
        url = url.replace("{id}", jobId);
        OkHttpClient client = new OkHttpClient().newBuilder()
            .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
            .url(url)
            .get()
            .build();
        try(Response response = client.newCall(request).execute()) {
            String respStr = response.body().string();
            log.info("Cancel success");
            log.info("Cancel result: {}",respStr);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            return result;
        }
    }

    @Override
    public JSONObject plan(String jobText) {
        JSONObject result = new JSONObject();
        Optional<String> jobId = Optional.empty();
        String url = flink.getUrl() + "/jars/{id}/plan";

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("programArgs", Base64Utils.encode(jobText));
        bodyJson.put("entryClass", jarInfo.getJSONArray("entry").getJSONObject(0).getString("name"));
        url = url.replace("{id}", jarInfo.getString("id"));

        log.info("plan job url: {}", url);
        log.info("args is: {}", Base64Utils.encode(jobText));
        OkHttpClient client = new OkHttpClient().newBuilder()
            .callTimeout(1, TimeUnit.MINUTES)
            .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
            .url(url)
            .method("POST", body)
            .build();
        try(Response response = client.newCall(request).execute()) {
            String respStr = response.body().string();
            JSONObject resp = JSONObject.parseObject(respStr);
            if(StringUtils.isEmpty(resp.getString("errors"))) {
                log.info("plan success");
                log.info("plan result: {}",respStr);
                result = resp;
            } else {
                log.info("plan failed");
                log.info("respStr: {}", resp);
                result = resp;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    /**
     * 上传文件到集群
     *
     * @return
     */
    private String uploadJar() {
        String url = flink.getUrl() + "/jars/upload";
        File jarfile = new File(flink.getJar());

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                .callTimeout(Duration.ofSeconds(120))
                .readTimeout(Duration.ofSeconds(120))
                .build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("jarfile", jarfile.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream"),
                        jarfile))
                .build();
            Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();
            Response response = client.newCall(request).execute();
            JSONObject jsonObject = JSONObject.parseObject(response.body().string());
            log.debug("response.body().string() {}", jsonObject.toJSONString());
            return jsonObject.getString("filename");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return 返回最新的一个jar包
     */
    private JSONObject getLastFile() {
        String url = flink.getUrl() + "/jars";
        OkHttpClient client = new OkHttpClient().newBuilder()
            .build();
        Request request = new Request.Builder()
            .url(url)
            .method("GET", null)
            .build();
        try (Response response = client.newCall(request).execute()) {

            JSONArray jsonArray = JSONObject.parseObject(response.body().string()).getJSONArray("files");

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                System.out.println(jsonObject.toJSONString());
                if (jarName.equals(jsonObject.getString("name"))) {
                    return jsonObject;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
