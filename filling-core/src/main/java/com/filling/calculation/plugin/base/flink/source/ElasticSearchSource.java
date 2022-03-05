package com.filling.calculation.plugin.base.flink.source;


import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.common.CheckConfigUtil;
import com.filling.calculation.common.CheckResult;
import com.filling.calculation.flink.FlinkEnvironment;
import com.filling.calculation.flink.stream.FlinkStreamSource;
import com.filling.calculation.flink.util.SchemaUtil;
import com.filling.calculation.plugin.base.flink.source.elasticsearch.ElasticSearchFactory;
import com.filling.calculation.plugin.base.flink.source.elasticsearch.ElasticsearchConf;
import com.filling.calculation.plugin.base.flink.source.elasticsearch.ElasticsearchUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author cclient
 */
public class ElasticSearchSource implements FlinkStreamSource<Row> {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchSource.class);

    private JSONObject config;

    private ElasticsearchConf elasticsearchConf;

    private JSONObject schemaInfo;

    private TypeInformation<Row> typeInfo;

    private static final String SCHEMA = "schema";

    @Override
    public Integer getParallelism() {
        return 1;
    }

    @Override
    public String getName() {
        return StringUtils.isEmpty(config.getString("name")) ? config.getString("plugin_name") : config.getString("name");
    }

    @Override
    public DataStream<Row> getStreamData(FlinkEnvironment env) throws NoSuchFieldException {

        ElasticSearchFactory elasticSearchFactory = new ElasticSearchFactory(elasticsearchConf, typeInfo);

        DataStream dataStream = env.getStreamExecutionEnvironment().addSource(elasticSearchFactory).returns(typeInfo).name(getName()).setParallelism(getParallelism());
        return dataStream;
    }

    @Override
    public void setConfig(JSONObject config) {

        elasticsearchConf = JSONObject.parseObject(String.valueOf(config), ElasticsearchConf.class);
        this.config = config;
    }

    @Override
    public JSONObject getConfig() {
        return config;
    }

    @Override
    public CheckResult checkConfig() {
        if(!elasticsearchConf.getAutoSchema()) {
            return CheckConfigUtil.check(config);
        }
        CheckResult result = new CheckResult(true, "");
        return result;
    }

    @Override
    public void prepare(FlinkEnvironment prepareEnv) {
        try {
            if(elasticsearchConf.getAutoSchema()) {
                String schemaInfoStr = ElasticsearchUtil.getHeadData(elasticsearchConf);
                logger.info("auto schemaInfo: {}", schemaInfoStr);
                schemaInfo = JSONObject.parseObject(schemaInfoStr);
                typeInfo = SchemaUtil.getTypeInformation(schemaInfo);
            } else {
                schemaInfo = JSONObject.parseObject(config.getString(SCHEMA));
                typeInfo = SchemaUtil.getTypeInformation(schemaInfo);
            }
            elasticsearchConf.setFieldNames(schemaInfo.getInnerMap().keySet().toArray(new String[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
