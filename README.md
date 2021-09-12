# Filling 

Filling, 如其名, 致力于填充你的各种存储,  是一个`非常易用`，`高性能`、支持`实时流式`和`离线批处理`的`海量数据`处理产品，架构于 `Apache Flink`之上。

---


### 如果您没时间看下面内容，请直接进入正题:



<img src="./docs/zh-cn/images/wechat-qrcode/garyelephant.jpeg" height="240" width="240">

想了解Filling的设计与实现原理，请查看视频：[https://time.geekbang.org/dailylesson/detail/100028486](https://time.geekbang.org/dailylesson/detail/100028486)


---

## 为什么我们需要 Filling

Databricks 开源的 Apache Spark 对于分布式数据处理来说是一个伟大的进步。我们在使用 Spark 时发现了很多可圈可点之处，同时我们也发现了我们的机会 —— 通过我们的努力让Spark的使用更简单，更高效，并将业界和我们使用Spark的优质经验固化到Filling这个产品中，明显减少学习成本，加快分布式数据处理能力在生产环境落地。

除了大大简化分布式数据处理难度外，Filling尽所能为您解决可能遇到的问题：

* 数据丢失与重复
* 任务堆积与延迟
* 吞吐量低
* 应用到生产环境周期长
* 缺少应用运行状态监控

"Filling" 的中文是“水滴”，来自中国当代科幻小说作家刘慈欣的《三体》系列，它是三体人制造的宇宙探测器，会反射几乎全部的电磁波，表面绝对光滑，温度处于绝对零度，全部由被强互作用力紧密锁死的质子与中子构成，无坚不摧。在末日之战中，仅一个水滴就摧毁了人类太空武装力量近2千艘战舰。

## Filling 使用场景

* 海量数据ETL
* 海量数据聚合
* 多源数据处理

## Filling 的特性

*   简单易用，灵活配置，无需开发
*   实时流式处理
*   离线多源数据分析
*   高性能
*   海量数据处理能力
*   模块化和插件化，易于扩展
*   支持利用SQL做数据处理和聚合
*   支持flink
*   目前支持flink1.31.1

## Filling 的工作流程



<p align="center">
    <img src="./docs/zh-cn/images/wd-workflow.png" height="460" width="280" >
</p>


```
Input[数据源输入] -> Filter[数据处理]-> Filter[数据处理]-> Filter[数据处理]-> Filter[数据处理] -> Output[结果输出]
```

多个Filter构建了数据处理的Pipeline，满足各种各样的数据处理需求，如果您熟悉SQL，也可以直接通过SQL构建数据处理的Pipeline，简单高效。目前Filling支持的[Filter列表](zh-cn/configuration/filter-plugin), 仍然在不断扩充中。您也可以开发自己的数据处理插件，整个系统是易于扩展的。

## Filling 支持的插件

* Input plugin

kafka, file, jdbc , 自行开发的Input plugin

* Filter plugin

DataAggregates,DecodeBase64,FieldOperation,FieldRename,FieldTypeConver, DataJoin, EncodeBase64,FieldOrder.,FieldSelect,Sql, DataSelector, FieldJavaScript, FieldRemove, FieldSplit, 自行开发的Filter plugin

* Output plugin

Elasticsearch, File, Jdbc, Kafka, Mysql, Stdout, 自行开发的Output plugin

## 环境依赖

1. java运行环境，java >= 11

2. 如果您要在集群环境中运行Filling，那么需要以下flink集群环境的任意一种：

* Flink on Yarn
* Flink Standalone
* Flink on Mesos

如果您的数据量较小或者只是做功能验证，也可以仅使用`local`模式启动，无需集群环境，Filling支持单机运行。

## 文档

暂无 // TODO



## 贡献观点和代码

提交问题和建议：https://github.com/zihjiang/Filling/issues

贡献代码：https://github.com/zihjiang/Filling/pulls

## 开发者


## 联系项目负责人

zihjaing : zihjiang@gmail.com, 微信: 943673887
