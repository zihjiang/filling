package com.filling.client;

import java.util.Optional;

public interface ClusterClient {

    /**
     * 初始化集群
     */
    void init();

    /**
     * 提交任务
     * @return 返回任务id, 提交失败, 则为空
     */
    Optional<String> submit(String jobText);

    /**
     * 停止正在运行的任务
     * @param jobId
     * @return
     */
    Boolean cancel(String jobId);
}
