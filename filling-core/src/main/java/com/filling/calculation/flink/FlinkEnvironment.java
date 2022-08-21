package com.filling.calculation.flink;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.env.RuntimeEnv;
import com.filling.calculation.flink.util.ConfigKeyName;
import com.filling.calculation.flink.util.EnvironmentUtil;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: calculation-core
 * @description:
 * @author: zihjiang
 * @create: 2021-06-26 16:10
 **/
public class FlinkEnvironment implements RuntimeEnv {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkEnvironment.class);

    private JSONObject config;

    private StreamExecutionEnvironment environment;

    private StreamTableEnvironment tableEnvironment;

    private ExecutionEnvironment batchEnvironment;

    private boolean isStreaming;

    private String jobName = "filling";


    @Override
    public void setConfig(JSONObject config) {
        this.config = config;
    }

    @Override
    public JSONObject getConfig() {
        return config;
    }

    @Override
    public CheckResult checkConfig() {
        return EnvironmentUtil.checkRestartStrategy(config);
    }

    @Override
    public void prepare(Boolean isStreaming) {
        this.isStreaming = isStreaming;
        if (isStreaming) {
            createStreamEnvironment();
            createStreamTableEnvironment();
        } else {
            createExecutionEnvironment();
        }
        if (config.containsKey("job.name")){
            jobName = config.getString("job.name");
        }
    }

    public String getJobName() {
        return jobName;
    }

    public boolean isStreaming() {
        return isStreaming;
    }

    public StreamExecutionEnvironment getStreamExecutionEnvironment() {
        return environment;
    }

    public StreamTableEnvironment getStreamTableEnvironment() {
        return tableEnvironment;
    }

    private void createStreamTableEnvironment() {
        tableEnvironment = StreamTableEnvironment.create(getStreamExecutionEnvironment());
        TableConfig config = tableEnvironment.getConfig();
        if (this.config.containsKey(ConfigKeyName.MAX_STATE_RETENTION_TIME) && this.config.containsKey(ConfigKeyName.MIN_STATE_RETENTION_TIME)){
            long max = this.config.getLong(ConfigKeyName.MAX_STATE_RETENTION_TIME);
            long min = this.config.getLong(ConfigKeyName.MIN_STATE_RETENTION_TIME);
            config.setIdleStateRetentionTime(Time.seconds(min),Time.seconds(max));
        }
    }

    private void createStreamEnvironment() {
        environment = StreamExecutionEnvironment.getExecutionEnvironment();
        setCheckpoint();

        EnvironmentUtil.setRestartStrategy(config,environment.getConfig());

        if (config.containsKey(ConfigKeyName.BUFFER_TIMEOUT_MILLIS)) {
            long timeout = config.getLong(ConfigKeyName.BUFFER_TIMEOUT_MILLIS);
            environment.setBufferTimeout(timeout);
        }

        if (config.containsKey(ConfigKeyName.PARALLELISM)) {
            int parallelism = config.getInteger(ConfigKeyName.PARALLELISM);
            environment.setParallelism(parallelism);
        }

        if (config.containsKey(ConfigKeyName.MAX_PARALLELISM)) {
            int max = config.getInteger(ConfigKeyName.MAX_PARALLELISM);
            environment.setMaxParallelism(max);
        }
    }

    public ExecutionEnvironment getBatchEnvironment() {
        return batchEnvironment;
    }

    private void createExecutionEnvironment() {
//         batchEnvironment = ExecutionEnvironment.createRemoteEnvironment("10.10.14.117", 9091, "/Users/jiangzihan/hesheng/aiops/aiops-calculation-service/target/aiops-calculation-service-0.0.1-SNAPSHOT.war");
        batchEnvironment = ExecutionEnvironment.getExecutionEnvironment();
        if (config.containsKey(ConfigKeyName.PARALLELISM)) {
            int parallelism = config.getInteger(ConfigKeyName.PARALLELISM);
            batchEnvironment.setParallelism(parallelism);
        }
        EnvironmentUtil.setRestartStrategy(config, batchEnvironment.getConfig());
    }


    private void setCheckpoint() {
        if (config.containsKey(ConfigKeyName.CHECKPOINT_INTERVAL)) {
            CheckpointConfig checkpointConfig = environment.getCheckpointConfig();
            long interval = config.getLong(ConfigKeyName.CHECKPOINT_INTERVAL);
            environment.enableCheckpointing(interval);

            if (config.containsKey(ConfigKeyName.CHECKPOINT_MODE)) {
                String mode = config.getString(ConfigKeyName.CHECKPOINT_MODE);
                switch (mode.toLowerCase()) {
                    case "exactly-once":
                        checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
                        break;
                    case "at-least-once":
                        checkpointConfig.setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
                        break;
                    default:
                        LOG.warn("set checkpoint.mode failed, unknown checkpoint.mode [{}],only support exactly-once,at-least-once", mode);
                        break;
                }
            }

            if (config.containsKey(ConfigKeyName.CHECKPOINT_TIMEOUT)) {
                long timeout = config.getLong(ConfigKeyName.CHECKPOINT_TIMEOUT);
                checkpointConfig.setCheckpointTimeout(timeout);
            }

            if (config.containsKey(ConfigKeyName.CHECKPOINT_DATA_URI)) {
                String uri = config.getString(ConfigKeyName.CHECKPOINT_DATA_URI);
                StateBackend fsStateBackend = new FsStateBackend(uri);
                if (config.containsKey(ConfigKeyName.STATE_BACKEND)){
                    String stateBackend = config.getString(ConfigKeyName.STATE_BACKEND);
                    if ("rocksdb".equals(stateBackend.toLowerCase())){
//                        StateBackend rocksDBStateBackend = new RocksDBStateBackend(fsStateBackend, TernaryBoolean.TRUE);
//                        environment.setStateBackend(rocksDBStateBackend);
                        new Exception("rocksdb");
                    }
                }else {
                    environment.setStateBackend(fsStateBackend);
                }
            }

            if (config.containsKey(ConfigKeyName.MAX_CONCURRENT_CHECKPOINTS)) {
                int max = config.getInteger(ConfigKeyName.MAX_CONCURRENT_CHECKPOINTS);
                checkpointConfig.setMaxConcurrentCheckpoints(max);
            }

            if (config.containsKey(ConfigKeyName.CHECKPOINT_CLEANUP_MODE)) {
                boolean cleanup = config.getBoolean(ConfigKeyName.CHECKPOINT_CLEANUP_MODE);
                if (cleanup) {
                    checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION);
                } else {
                    checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

                }
            }

            if (config.containsKey(ConfigKeyName.MIN_PAUSE_BETWEEN_CHECKPOINTS)) {
                long minPause = config.getLong(ConfigKeyName.MIN_PAUSE_BETWEEN_CHECKPOINTS);
                checkpointConfig.setMinPauseBetweenCheckpoints(minPause);
            }

            if (config.containsKey(ConfigKeyName.FAIL_ON_CHECKPOINTING_ERRORS)) {
                int failNum = config.getInteger(ConfigKeyName.FAIL_ON_CHECKPOINTING_ERRORS);
                checkpointConfig.setTolerableCheckpointFailureNumber(failNum);
            }
        }
    }



}
