package com.filling.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.filling.client.ClusterClient;
import com.filling.domain.FillingJobs;
import com.filling.repository.FillingJobsRepository;
import com.filling.utils.DebugUtils;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public FillingJobsService(FillingJobsRepository fillingJobsRepository, ClusterClient clusterClient) {
        this.fillingJobsRepository = fillingJobsRepository;
        this.clusterClient = clusterClient;
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
     * @param fillingJobs job信息
     * @param debugId id
     */
    @Async
    public void debugFillingJob(FillingJobs fillingJobs, String debugId) {
        try {
            File result = new File(TemplateDir + File.separator + debugId + ".log");
            DebugUtils debugUtils = new DebugUtils();
            debugUtils.flinkDebug(result, fillingJobs.getJobText());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            return fileName;
        }
    }

    /**
     * 根据任务返回的debug名称, 获取日志
     * @param fileName
     * @return
     */
    public List<String> debugFillingJobByName(String fileName) {
        List<String> strings = new ArrayList<>();
        try {
            Path path = Paths.get(TemplateDir + File.separator + fileName + ".log");
            strings = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return strings;
        }
    }

    public List<String> detailResultTable(String resultName) {

        List<String> strings;

        Path path = Paths.get("/tmp/flink_" + resultName + ".json");
        try {
            strings = JSONArray.parseArray(Files.readString(path), String.class);
        } catch (IOException e) {
            e.printStackTrace();
            strings = new ArrayList<>();
        }

        return strings;
    }
}
