package com.filling.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.filling.client.ClusterClient;
import com.filling.config.ApplicationProperties;
import com.filling.domain.FillingJobs;
import com.filling.repository.FillingJobsRepository;
import com.filling.utils.Base64Utils;
import com.filling.utils.DebugUtils;
import com.filling.utils.KafkaUtil;
import com.filling.web.rest.vm.FillingDebugVM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.loader.tools.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.LogOutputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Service Implementation for managing {@link FillingJobs}.
 */
@Service
@Transactional
public class FillingJobsService {

    private final Logger log = LoggerFactory.getLogger(FillingJobsService.class);

    private final FillingJobsRepository fillingJobsRepository;

    private final ClusterClient clusterClient;

    private final String TemplateDir = System.getProperty("java.io.tmpdir");

    private ApplicationProperties flink;

    public FillingJobsService(FillingJobsRepository fillingJobsRepository, ClusterClient clusterClient, ApplicationProperties flink) {
        this.fillingJobsRepository = fillingJobsRepository;
        this.clusterClient = clusterClient;
        this.flink = flink;
    }

    /**
     * Save a fillingJobs.
     *
     * @param fillingJobs the entity to save.
     * @return the persisted entity.
     */
    public FillingJobs save(FillingJobs fillingJobs) {
        log.debug("Request to save FillingJobs : {}", fillingJobs);
        return fillingJobsRepository.save(fillingJobs);
    }

    /**
     * Partially update a fillingJobs.
     *
     * @param fillingJobs the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FillingJobs> partialUpdate(FillingJobs fillingJobs) {
        log.debug("Request to partially update FillingJobs : {}", fillingJobs);

        return fillingJobsRepository
            .findById(fillingJobs.getId())
            .map(
                existingFillingJobs -> {
                    if (fillingJobs.getName() != null) {
                        existingFillingJobs.setName(fillingJobs.getName());
                    }
                    if (fillingJobs.getApplicationId() != null) {
                        existingFillingJobs.setApplicationId(fillingJobs.getApplicationId());
                    }
                    if (!"{}".equals(fillingJobs.getJobText())) {
                        existingFillingJobs.setJobText(fillingJobs.getJobText());
                    }
                    if (fillingJobs.getType() != null) {
                        existingFillingJobs.setType(fillingJobs.getType());
                    }
                    if (fillingJobs.getConfProp() != null) {
                        existingFillingJobs.setConfProp(fillingJobs.getConfProp());
                    }
                    if (!"1".equals(fillingJobs.getStatus())) {
                        existingFillingJobs.setStatus(fillingJobs.getStatus());
                    }
                    if (fillingJobs.getCreatetime() != null) {
                        existingFillingJobs.setCreatetime(fillingJobs.getCreatetime());
                    }
                    if (fillingJobs.getCreatedBy() != null) {
                        existingFillingJobs.setCreatedBy(fillingJobs.getCreatedBy());
                    }
                    if (fillingJobs.getAddjar() != null) {
                        existingFillingJobs.setAddjar(fillingJobs.getAddjar());
                    }
                    if (fillingJobs.getDescription() != null) {
                        existingFillingJobs.setDescription(fillingJobs.getDescription());
                    }

                    existingFillingJobs.setUpdatetime(Instant.now());

                    return existingFillingJobs;
                }
            )
            .map(fillingJobsRepository::save);
    }

    /**
     * Get all the fillingJobs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FillingJobs> findAll(Pageable pageable) {
        log.debug("Request to get all FillingJobs");
        return fillingJobsRepository.findAll(pageable);
    }

    /**
     * Get one fillingJobs by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FillingJobs> findOne(Long id) {
        log.debug("Request to get FillingJobs : {}", id);
        return fillingJobsRepository.findById(id);
    }

    /**
     * Delete the fillingJobs by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete FillingJobs : {}", id);
        fillingJobsRepository.deleteById(id);
    }

    /**
     * start thes filling job
     *
     * @param fillingJobs
     * @return
     */
    public FillingJobs start(FillingJobs fillingJobs) {
        Optional<String> jobId = clusterClient.submit(fillingJobs.toJobString());
        if (jobId.isPresent()) {
            fillingJobs.setStatus("2");
            fillingJobs.setApplicationId(jobId.get());
            save(fillingJobs);
        } else {
            fillingJobs.setStatus("6");
            save(fillingJobs);
        }
        return fillingJobs;
    }

    /**
     * stop this job
     *
     * @param fillingJobs
     * @return
     */
    public FillingJobs stop(FillingJobs fillingJobs) {
        if (StringUtils.isEmpty(fillingJobs.getApplicationId())) {
            log.warn("jobid is null {}", fillingJobs);
            // TODO jobid is null 的逻辑
            return fillingJobs;
        }

        Boolean result = clusterClient.cancel(fillingJobs.getApplicationId());
        if (result) {
            fillingJobs.setStatus("5");
        } else {
            // TODO 停止失败的逻辑
        }
        return save(fillingJobs);
    }

    /**
     * plan this job
     *
     * @param fillingJobs
     * @return
     */
    public JSONObject plan(FillingJobs fillingJobs) {
        return clusterClient.plan(fillingJobs.toJobString());
    }

    /**
     * 上传文件
     *
     * @param multipartFile
     * @return 返回文件
     */
    public File uploadFile(MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        File file = new File(TemplateDir + File.separator + fileName);
        multipartFile.transferTo(file);
        return file;
    }

    public void importFilling(List<String> fileNames) throws IOException {
        FillingJobs fillingJobs;
        for (String fileName : fileNames) {
            fillingJobs = JSONObject.parseObject(new String(Files.readAllBytes(Paths.get(TemplateDir + File.separator + fileName))), FillingJobs.class);
            if (fillingJobs != null) {
                save(fillingJobs);
            }
        }
    }

    /**
     * 异步方法, 执行debug信息
     *
     * @param fillingJobs job信息
     * @param debugId     id
     */
    public FillingDebugVM debugFillingJob(FillingJobs fillingJobs, String debugId) {
        FillingDebugVM fillingDebugVM = new FillingDebugVM();
        try {
            File result = new File(TemplateDir + File.separator + debugId + ".log");

            fillingDebugVM.setStatus(flinkDebug(result, fillingJobs.toJobString()));
            fillingDebugVM.setLog(debugFillingJobByName(debugId));
            fillingDebugVM.setPreviewData(detailResultTable(fillingJobs.getResultTableNameSourceAndTransform()));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return fillingDebugVM;
        }
    }

    /**
     * 根据任务返回的debug名称, 获取日志
     *
     * @param fileName
     * @return
     */
    public String debugFillingJobByName(String fileName) {
        String log = null;
        try {
            Path path = Paths.get(TemplateDir + File.separator + fileName + ".log");
            log = Files.readString(path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return log;
        }
    }


    public Map<String, JSONArray> detailResultTable(List<String> resultNames) {

        Map<String, JSONArray> result = new HashMap<>();
        for (String resultName : resultNames) {
            result.put(resultName, detailResultTable(resultName));
        }
        return result;
    }

    public JSONArray detailResultTable(String resultName) {

        JSONArray strings;

        Path path = Paths.get("/tmp/flink_" + resultName + ".json");
        try {
            strings = JSONArray.parseArray(Files.readString(path));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            strings = new JSONArray();
        }

        return strings;
    }

    public List getSourceData(JSONObject params) throws Exception {
        String pluginName = params.getString("plugin_name");
        List result = new ArrayList();
        switch (pluginName) {
            case "KafkaTableStream":
                Map<String, Object> map;
                if (params.containsKey("topics")) {
                    map = extractSubConfig(params, "consumer.", false);
                } else {
                    throw new Exception("must is topics");
                }
                String topic = params.getString("topics");
                List<String> _result = KafkaUtil.consumeMessage(topic, map);

                _result.stream().forEach(s -> {
                    if (s.startsWith("{")) {
                        result.add(JSONObject.parseObject(s));
                    } else {
                        result.add(s);
                    }
                });

                break;
            case "dataGenSource":
                for (int i = 0; i < 10; i++) {
                    result.add(params.getJSONObject("schema"));
                }
                break;

            default:
                throw new Exception("pluginName not support preview ");
        }

        return result;

    }


    /**
     * 剔除prefix后, 保留参数
     *
     * @param source
     * @param prefix
     * @param keepPrefix
     * @return
     */
    public static Map<String, Object> extractSubConfig(JSONObject source, String prefix, boolean keepPrefix) {

        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : source.entrySet()) {
            final String key = entry.getKey();
            final String value = String.valueOf(entry.getValue());
            if (key.startsWith(prefix)) {

                if (keepPrefix) {
                    result.put(key, value);
                } else {
                    result.put(key.substring(prefix.length()), value);
                }
            }
        }
        return result;
    }


    /**
     * 执行java -jar脚本
     *
     * @param file
     * @param jobString
     * @return
     */
    private Boolean flinkDebug(File file, String jobString) {
        log.info("create tempFile: {}", file.getAbsolutePath());
        Boolean result = true;
        try {
            result = exec(file, "java", "-cp", flink.getDebugLibDir() + File.separator + "*:" + flink.getJar(), "com.filling.calculation.Filling", Base64Utils.encode(jobString), "debug");
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            return result;
        }
    }

    private Boolean exec(File file, String... command) throws Exception {
        final Boolean[] status = {true};
        FileWriter fw = new FileWriter(file, true);
        new ProcessExecutor().command(command).timeout(30, TimeUnit.SECONDS).destroyOnExit().redirectOutput(new LogOutputStream() {
            @Override
            protected void processLine(String s) {
                try {
                    fw.append(s);
                    fw.append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).redirectError(new LogOutputStream() {
            @Override
            protected void processLine(String s) {
                try {
                    status[0] = false;
                    System.out.println(s);
                    System.out.println("\n");
                    fw.append(s);
                    fw.append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).execute().getExitValue();
        fw.close();
        return status[0];
    }

}
