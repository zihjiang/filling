import kafkaIcon from './images/kafka.svg';
import jdbsIcon from './images/jdbc.svg';
import dataGenIcon from './images/dataGen.svg';
import DataAggregatesIcon from './images/DataAggregates.svg';
import enbase64Icon from './images/enbase64.svg';
import debase64Icon from './images/debase64.svg';
import dataJoinIcon from './images/dataJoin.svg';
import dataSelectorIcon from './images/dataSelector.svg';
import devIcon from './images/dev.svg';
// import elasticsearchIcon from './images/Elasticsearch.png';
import elasticsearchIcon from './images/Elasticsearch.svg';
import converIcon from './images/convert.svg';
import fieldIcon from './images/field.svg';
import sqlIcon from './images/sql.svg';
import selectIocn from './images/selected.svg';

import BaseEndpoint from '../EditorGraph/endpoint';
import ClickHouseIcon from './images/clickhouse.svg';
import MysqlIcon from './images/mysql.svg';
import jsonIcon from './images/json.svg';

import _ from 'lodash';
const source = [
    {
        id: 'kafka',
        text: 'kafka consumer',
        type: 'png',
        content: kafkaIcon,
        height: 90,
        width: "100%",
        pluginType: 'source',

        pluginName: "KafkaTableStream",
        pluginOptions: [
            {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "KafkaTableStream",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "consumer.group.id",
                "text": "消费组",
                "defaultValue": "filling-group01",
                "required": true,
                "paramsDesc": "kafka里的group.id参数",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "topics",
                "text": "订阅组",
                "defaultValue": "filling-topic",
                "required": true,
                "paramsDesc": "kakfa的topic参数, 可以数多个, 用`,`分割",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "schema",
                "text": "简单数据",
                "defaultValue": "{\"host\":\"f0e31d4cd63b\",\"@timestamp\":\"2021-08-12T03:27:57.316Z\",\"path\":\"/sample_data/apache_access_logs_1000w_02\",\"message\":\"127.0.0.1 - - [19/Jun/1998:23:12:52 +0000] \\\"GET /images/logo_cfo.gif HTTP/1.0\\\" 304 0\",\"@version\":\"1\"}",
                "required": true,
                "paramsDesc": "数据样本, 用来解析数据格式",
                "desc": " ",

                "readOnly": false,
                "type": "textArea"
            }, {
                "name": "format.type",
                "text": "数据类型",
                "defaultValue": "json",
                "required": true,
                "paramsDesc": "kafka消息的数据格式",
                "desc": " ",

                "readOnly": false,
                "type": "select",
                "selectOptions": [
                    {
                        "value": "json",
                        "label": "json"
                    }, {
                        "value": "csv",
                        "label": "csv"
                    }, {
                        "value": "string",
                        "label": "string"
                    }
                ]
            }, {
                "name": "offset.reset",
                "text": "消费模式",
                "defaultValue": "earliest",
                "required": true,
                "paramsDesc": "earliest: 尽可能从最早消费数据,latest: 从最新处消费数据,fromTimestamp: 指定时间戳消费, fromGroupOffsets: 从当前的offset消费, 若果不存在offset, 则和latest一致",
                "desc": " ",

                "readOnly": false,
                "type": "select",
                "selectOptions": [
                    {
                        "value": "earliest",
                        "label": "最早处消费数据"
                    }, {
                        "value": "latest",
                        "label": "最新处消费数据"
                    }, {
                        "value": "fromGroupOffsets",
                        "label": "当前消费位置消费数据"
                    }
                ]
            }, {
                "name": "consumer.bootstrap.servers",
                "text": "kakfa地址",
                "defaultValue": "192.168.1.70:9092",
                "required": true,
                "paramsDesc": "kakfa地址, 例如: 127.0.0.1:9092",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": true,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit",
                "digitMin": 1,
                "digitMax": 20,
            }, {
                "name": "name",
                "text": "名称",
                "defaultValue": "kafka",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }
        ],
        endpoints: [{
            id: 'kafka_result_table_name',
            orientation: [1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }]
    },
    {
        id: 'jdbc',
        text: 'jdbc源',
        type: 'png',
        Data: {},
        pluginType: 'source',
        pluginName: "JdbcSource",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "jdbc",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "JdbcSource",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "driver",
                "text": "驱动",
                "defaultValue": "com.mysql.jdbc.Driver",
                "required": true,
                "paramsDesc": "jdbc驱动类",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "url",
                "text": "链接字符串",
                "defaultValue": "jdbc:mysql://127.0.0.1:3306/test_demo?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                "required": true,
                "paramsDesc": "链接字符串, 例如: jdbc:mysql://127.0.0.1:3306/test_demo",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "username",
                "text": "用户名",
                "defaultValue": "root",
                "required": false,
                "paramsDesc": "连接数据库用户名(选填)",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "password",
                "text": "密码",
                "defaultValue": "root",
                "required": false,
                "paramsDesc": "连接数据库密码(选填)",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "query",
                "text": "查询语句",
                "defaultValue": "select * from table1",
                "required": true,
                "paramsDesc": "查询的数据库语句, 例如: select * from table1",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "fetch_size",
                "text": "拉取数量",
                "defaultValue": "1000",
                "required": true,
                "paramsDesc": "每次拉取的数量",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": true,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
        ],
        endpoints: [{
            id: 'jdbc_result_table_name',
            orientation: [1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }],
        content: jdbsIcon,
        height: 90,
        width: "100%"
    },
    {
        id: 'dataGen',
        text: 'dataGen源',
        type: 'png',
        Data: {
            "plugin_name": "DataGenTableStream",
            "result_table_name": "dataGenTableStreamTable",
            "schema": "{\"id\":1, \"host\":\"192.168.1.103\",\"source\":\"datasource\",\"MetricsName\":\"cpu\",\"value\":49}",
            "rows-per-second": 1000000,
            "number-of-rows": 100000000,
            "fields": [
                {
                    "id": {
                        "kind": "SEQUENCE",
                        "type": "Int",
                        "start": 0,
                        "end": 10000000
                    }
                },
                {
                    "host": {
                        "type": "String",
                        "length": 1
                    }
                },
                {
                    "source": {
                        "type": "String",
                        "length": 10
                    }
                },
                {
                    "MetricsName": {
                        "type": "String",
                        "length": 10
                    }
                },
                {
                    "value": {
                        "type": "Int",
                        "min": 1,
                        "max": 2
                    }
                }
            ],
            "parallelism": 5,
            "name": "my-datagen"
        },
        pluginType: 'source',
        pluginName: "dataGenSource",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "dataGen",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "dataGenSource",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "schema",
                "text": "简单数据",
                "defaultValue": "{\"id\":1, \"host\":\"192.168.1.103\",\"source\":\"datasource\",\"MetricsName\":\"cpu\",\"value\":49}",
                "required": true,
                "paramsDesc": "数据样本, 用来解析数据格式",
                "desc": " ",

                "readOnly": false,
                "type": "textArea"
            }, {
                "name": "rows-per-second",
                "text": "每秒钟生成行数",
                "defaultValue": "10000",
                "required": true,
                "paramsDesc": "一秒之内, 生成多少条数据",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "number-of-rows",
                "text": "一共生成行数",
                "defaultValue": "10000000",
                "required": true,
                "paramsDesc": "本次任务一共要生成多少条数据",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": true,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
        ],
        endpoints: [{
            id: 'dataGen_result_table_name',
            orientation: [1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }],
        content: dataGenIcon,
        height: 90,
        width: "100%"
    },


    {
        id: 'MysqlCdc',
        text: 'Mysql cdc',
        type: 'png',
        Data: {},
        pluginType: 'source',
        pluginName: "MysqlCdcSource",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "Mysql-cdc",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "MysqlCdcSource",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": true,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            },
            {
                "name": "sql",
                "mode": "sql",
                "label": "sql",
                "text": "cdc的建表语句, 参考https://ververica.github.io/flink-cdc-connectors/master/content/%E5%BF%AB%E9%80%9F%E4%B8%8A%E6%89%8B/mysql-postgres-tutorial-zh.html",
                "paramsDesc": "cdc的建表语句, 参考https://ververica.github.io/flink-cdc-connectors/master/content/%E5%BF%AB%E9%80%9F%E4%B8%8A%E6%89%8B/mysql-postgres-tutorial-zh.html",
                "defaultValue": "CREATE TABLE {table} (\n    id INT,\n    name STRING,\n    description STRING,\n    PRIMARY KEY (id) NOT ENFORCED\n  ) WITH (\n    'connector' = 'mysql-cdc',\n    'hostname' = '192.168.100.177',\n    'port' = '3306',\n    'username' = 'root',\n    'password' = '123456',\n    'database-name' = 'mydb',\n    'table-name' = 'products'\n  )",
                "required": true,
                "desc": " ",

                "readOnly": false,
                "type": "text"
            }
        ],
        endpoints: [{
            id: 'mysqlCdc_source_table_name',
            orientation: [1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }],
        content: MysqlIcon,
        height: 90,
        width: "100%"
    },
    {
        id: 'ElasticSearch',
        text: 'ElasticSearch',
        type: 'png',
        Data: {},
        pluginType: 'source',
        pluginName: "ElasticSearchSource",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "ElasticSearch",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "ElasticSearchSource",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": true,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            },
            {
                "name": "hosts",
                "label": "hosts",
                "text": "hosts",
                "defaultValue": ["127.0.0.1:9200"],
                "required": true,
                "paramsDesc": "elasticsearch 的主机地址, 格式为 ip:port",
                "desc": " ",

                "readOnly": false,
                "type": "array"
            },
            {
                "name": "index",
                "label": "index",
                "text": "index",
                "defaultValue": "",
                "required": true,
                "paramsDesc": "elasticsearch 的索引名称",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }
        ],
        endpoints: [{
            id: 'elasticsearch_source_table_name',
            orientation: [1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }],
        content: elasticsearchIcon,
        height: 90,
        width: "100%"
    }
];
const transform = [
    {
        id: 'DataAggregates',
        text: 'Data Aggregates',
        type: 'png',
        Data: {},
        pluginType: 'transform',
        pluginName: "DataAggregates",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "Data Aggregates",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "DataAggregates",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "rowtime.watermark.field",
                "text": "时间字段",
                "defaultValue": "_time",
                "required": true,
                "paramsDesc": "时间字段, 必须是13位时间戳类型",
                "desc": " ",
                "readOnly": false,
                "type": "field_string"
            }, {
                "name": "rowtime.watermark.tumble.ms",
                "text": "翻滚窗口的大小(毫秒)",
                "defaultValue": "60000",
                "required": true,
                "paramsDesc": "翻滚窗口的大小, 单位是毫秒",
                "desc": " ",
                "readOnly": false,
                "type": "digit",
                "digitMin": "10",
                "digitMax": "9999999999"
            }, {
                "name": "rowtime.watermark.tumble.delay.ms",
                "text": "允许数据迟到时间",
                "defaultValue": "60000",
                "required": true,
                "paramsDesc": "允许数据迟到时间, 单位是毫秒",
                "desc": " ",
                "readOnly": false,
                "type": "string"
            }, {
                "name": "group.fields",
                "text": "分组的字段, 回车选择",
                "defaultValue": [],
                "required": true,
                "paramsDesc": "分组的字段, 回车选择",
                "desc": " ",
                "readOnly": false,
                "type": "field_array"
            }, {
                "name": "custom.fields",
                "text": "自定义聚合字段",
                "defaultValue": [],
                "required": true,
                "paramsDesc": "除了对group.fields字段聚合, 还可以自定义聚合字段, 这里设置的是字段名称",
                "desc": " ",
                "readOnly": false,
                "type": "array"
            }, {
                "name": "custom.field.{field}.script",
                "text": "{field}字段表达式",
                "defaultValue": "",
                "required": true,
                "paramsDesc": "{field}字段表达式",
                "desc": " ",
                "readOnly": false,
                "type": "child",
                "father": "custom.fields"
            }, {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": true,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
        ],
        endpoints: [{
            id: 'DataAggregates_result_table_name',
            orientation: [1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }, {
            id: 'DataAggregates_source_table_name',
            orientation: [-1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }],
        content: DataAggregatesIcon,
        height: 90,
        width: "100%"
    },
    {
        id: 'DecodeBase64',
        text: 'Decode base64',
        type: 'png',
        Data: {},
        pluginType: 'transform',
        pluginName: "DecodeBase64",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "DecodeBase64",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "DecodeBase64",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "source_field",
                "text": "源字段名",
                "defaultValue": "",
                "required": true,
                "paramsDesc": "源字段名",
                "desc": " ",
                "display": "none",
                "readOnly": false,
                "type": "field_string"
            }, {
                "name": "target_field",
                "text": "目标字段名",
                "defaultValue": "",
                "required": true,
                "paramsDesc": "目标字段名",
                "desc": " ",
                "readOnly": false,
                "type": "string",
                "ruleRegexp": "(^_([a-zA-Z0-9]_?)*$)|(^[a-zA-Z](_?[a-zA-Z0-9])*_?$)",
                "ruleMessage": "字段名称只能包含字母、数字、下划线，且以字母开头"
            }, {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": true,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
        ],
        endpoints: [{
            id: 'DecodeBase64_result_table_name',
            orientation: [1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }, {
            id: 'DecodeBase64_source_table_name',
            orientation: [-1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }],
        content: debase64Icon,
        height: 90,
        width: "100%"
    },
    {
        id: 'EncodeBase64',
        text: 'Encode base64',
        type: 'png',
        Data: {},
        pluginType: 'transform',
        pluginName: "EncodeBase64",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "EncodeBase64",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "EncodeBase64",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "source_field",
                "text": "源字段名",
                "defaultValue": "",
                "required": true,
                "paramsDesc": "源字段名",
                "desc": " ",
                "display": "none",
                "readOnly": false,
                "type": "field_string"
            }, {
                "name": "target_field",
                "text": "目标字段名",
                "defaultValue": "",
                "required": true,
                "paramsDesc": "目标字段名",
                "desc": " ",
                "readOnly": false,
                "type": "string",
                "ruleRegexp": "(^_([a-zA-Z0-9]_?)*$)|(^[a-zA-Z](_?[a-zA-Z0-9])*_?$)",
                "ruleMessage": "字段名称只能包含字母、数字、下划线，且以字母开头"
            }, {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": true,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
        ],
        endpoints: [{
            id: 'Enbase64_result_table_name',
            orientation: [1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }, {
            id: 'Enbase64_source_table_name',
            orientation: [-1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }],
        content: enbase64Icon,
        height: 90,
        width: "100%"
    },
    {
        id: 'DataJoin',
        text: 'DataJoin',
        type: 'png',
        Data: {},
        pluginType: 'transform',
        pluginName: "DataJoin",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "DataJoin",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "DataJoin",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "join.source_table_name",
                "text": "需要join的表",
                "defaultValue": "",
                "required": true,
                "paramsDesc": "需要join的表",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "join.secondary.where",
                "text": "join的条件",
                "defaultValue": "{main}.field = {secondary}.field",
                "required": true,
                "paramsDesc": "join的条件,{main}和{secondary}代表主副表",
                "desc": " ",
                "display": "none",
                "readOnly": false,
                "type": "ace-auto-complete"
            }, {
                "name": "join.secondary.type",
                "text": "关联类型",
                "defaultValue": "left",
                "required": true,
                "paramsDesc": "关联类型",
                "desc": " ",
                "display": "none",
                "readOnly": false,
                "type": "select",
                "selectOptions": [
                    {
                        "value": "left",
                        "label": "左关联"
                    }, {
                        "value": "right",
                        "label": "右关联"
                    }
                ]
            }, {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": true,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
        ],
        endpoints: [{
            id: 'DataJoin_source_table_name',
            orientation: [-1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }, {
            id: 'DataJoin_join_source_table_name',
            orientation: [-1, 0],
            pos: [0, 0.8],
            Class: BaseEndpoint,
            color: 'system-green'
        }, {
            id: 'DataJoin_result_table_name',
            orientation: [1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }],
        content: dataJoinIcon,
        height: 90,
        width: "100%"
    },
    {
        id: 'dataSelector',
        text: 'DataSelector',
        type: 'png',
        Data: {},
        pluginType: 'transform',
        pluginName: "DataSelector",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "DataSelector",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "DataSelector",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "select.result_table_name",
                "text": "生成的流",
                "defaultValue": [],
                "required": true,
                "paramsDesc": "生成的流",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "array"
            }, {
                "name": "select.{id}_t1.where",
                "text": "数据{id}_t1, 的条件",
                "defaultValue": "",
                "required": true,
                "paramsDesc": "数据{id}_t1, 的条件",
                "desc": " ",
                "readOnly": false,
                "type": "text_rex_id"
            }, {
                "name": "select.{id}_t2.where",
                "text": "数据{id}_t2, 的条件",
                "defaultValue": "",
                "required": true,
                "paramsDesc": "数据{id}_t2, 的条件",
                "desc": " ",
                "display": "none",
                "readOnly": false,
                "type": "text_rex_id"
            }, {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": true,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
        ],
        endpoints: [{
            id: 'DataSelector_result_table_name',
            orientation: [-1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }, {
            id: 'DataSelector_t1_result_table_name',
            orientation: [1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }, {
            id: 'DataSelector_t2_result_table_name',
            orientation: [1, 0],
            pos: [0, 0.8],
            Class: BaseEndpoint,
            color: 'system-green'
        }],
        content: dataSelectorIcon,
        height: 90,
        width: "100%"
    },
    {
        id: 'fieldTypeConver',
        text: 'FieldTypeConver',
        type: 'png',
        Data: {},
        pluginType: 'transform',
        pluginName: "FieldTypeConver",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "FieldTypeConver",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "FieldTypeConver",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "target_field_type",
                "text": "目标字段类型",
                "defaultValue": "int",
                "required": true,
                "paramsDesc": "目标字段类型",
                "desc": " ",
                "readOnly": false,
                "type": "select",
                "selectOptions": [
                    {
                        "value": "int",
                        "label": "int"
                    }, {
                        "value": "String",
                        "label": "String"
                    }, {
                        "value": "Long",
                        "label": "Long"
                    }
                ],
                "ruleRegexp": "(^_([a-zA-Z0-9]_?)*$)|(^[a-zA-Z](_?[a-zA-Z0-9])*_?$)",
                "ruleMessage": "字段名称只能包含字母、数字、下划线，且以字母开头"
            }, {
                "name": "source_field",
                "text": "源字段名称",
                "defaultValue": [],
                "required": true,
                "paramsDesc": "源字段名称",
                "desc": " ",
                "readOnly": false,
                "type": "field_array"
            },
            {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": false,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
        ],
        endpoints: [
            {
                id: 'fieldTypeConver_source_table_name',
                orientation: [1, 0],
                pos: [0, 0.5],
                Class: BaseEndpoint,
                color: 'system-green'
            }, {
                id: 'fieldTypeConver_result_table_name',
                orientation: [-1, 0],
                pos: [0, 0.5],
                Class: BaseEndpoint,
                color: 'system-green'
            }],
        content: converIcon,
        height: 90,
        width: "100%"
    },
    {
        id: 'FieldOperation',
        text: 'FieldOperation',
        type: 'png',
        Data: {},
        pluginType: 'transform',
        pluginName: "FieldOperation",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "FieldOperation",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",
                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "FieldOperation",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "target_field",
                "text": "目标字段",
                "defaultValue": "",
                "required": true,
                "paramsDesc": "目标字段",
                "desc": " ",
                "readOnly": false,
                "type": "string",
                "ruleRegexp": "(^_([a-zA-Z0-9]_?)*$)|(^[a-zA-Z](_?[a-zA-Z0-9])*_?$)",
                "ruleMessage": "字段名称只能包含字母、数字、下划线，且以字母开头"
            }, {
                "name": "script",
                "text": "表达式(支持sql函数)",
                "defaultValue": "",
                "required": true,
                "paramsDesc": "表达式(支持sql函数)",
                "desc": " ",
                "readOnly": false,
                "type": "ace-auto-complete",
            },
            {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": false,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
        ],
        endpoints: [
            {
                id: 'fieldOperation_source_table_name',
                orientation: [1, 0],
                pos: [0, 0.5],
                Class: BaseEndpoint,
                color: 'system-green'
            }, {
                id: 'fieldOperation_result_table_name',
                orientation: [-1, 0],
                pos: [0, 0.5],
                Class: BaseEndpoint,
                color: 'system-green'
            }],
        content: fieldIcon,
        height: 90,
        width: "100%"
    },
    {
        id: 'FieldJsonValue',
        text: 'FieldJsonValue',
        type: 'png',
        Data: {},
        pluginType: 'transform',
        pluginName: "FieldJsonValue",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "FieldJsonValue",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "FieldJsonValue",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "source_field",
                "text": "源字段",
                "defaultValue": "",
                "required": true,
                "paramsDesc": "json字符串的字段",
                "desc": " ",
                "display": "none",
                "readOnly": false,
                "type": "field_string"
            }, {
                "name": "path",
                "text": "表达式(xpath语法)",
                "defaultValue": "",
                "required": true,
                "paramsDesc": "表达式(xpath语法)",
                "desc": " ",
                "display": "none",
                "readOnly": false,
                "type": "string"
            },
            {
                "name": "target_field",
                "text": "目标字段名称",
                "defaultValue": "",
                "required": true,
                "paramsDesc": "目标字段名称",
                "desc": " ",
                "readOnly": false,
                "type": "string",
                "ruleRegexp": "(^_([a-zA-Z0-9]_?)*$)|(^[a-zA-Z](_?[a-zA-Z0-9])*_?$)",
                "ruleMessage": "字段名称只能包含字母、数字、下划线，且以字母开头"
            },
            {
                "name": "return_type",
                "text": "返回类型",
                "defaultValue": "STRING",
                "required": true,
                "paramsDesc": "表达式(xpath语法)",
                "desc": " ",
                "display": "none",
                "readOnly": false,
                "type": "select",
                "selectOptions": [
                    {
                        "value": "STRING",
                        "label": "STRING"
                    },
                    {
                        "value": "INT",
                        "label": "INT"
                    }, {
                        "value": "DOUBLE",
                        "label": "DOUBLE"
                    }, {
                        "value": "FLOAT",
                        "label": "FLOAT"
                    }, {
                        "value": "DATE",
                        "label": "DATE"
                    }, {
                        "value": "BIGINT",
                        "label": "BIGINT"
                    }, {
                        "value": "BOOLEAN",
                        "label": "BOOLEAN"
                    }
                ]
            },
            {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": false,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
        ],
        endpoints: [
            {
                id: 'fieldOperation_source_table_name',
                orientation: [1, 0],
                pos: [0, 0.5],
                Class: BaseEndpoint,
                color: 'system-green'
            }, {
                id: 'fieldOperation_result_table_name',
                orientation: [-1, 0],
                pos: [0, 0.5],
                Class: BaseEndpoint,
                color: 'system-green'
            }],
        content: jsonIcon,
        height: 90,
        width: "100%"
    },
    {
        id: 'Sql',
        text: 'Sql',
        type: 'png',
        Data: {},
        pluginType: 'transform',
        pluginName: "Sql",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "Sql",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "Sql",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "sql",
                "text": "sql语句(用{source_table_name}表示上个算子的表名) ",
                "defaultValue": "select * from {source_table_name}",
                "required": true,
                "paramsDesc": "sql语句(用{source_table_name}表示上个算子的表名)",
                "desc": " ",
                "readOnly": false,
                "type": "text",
                "mode": "sql"
            },
            {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": false,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
        ],
        endpoints: [
            {
                id: 'sql_source_table_name',
                orientation: [1, 0],
                pos: [0, 0.5],
                Class: BaseEndpoint,
                color: 'system-green'
            }, {
                id: 'sql_result_table_name',
                orientation: [-1, 0],
                pos: [0, 0.5],
                Class: BaseEndpoint,
                color: 'system-green'
            }],
        content: sqlIcon,
        height: 90,
        width: "100%"
    },
    {
        id: 'FieldSelect',
        text: 'FieldSelect',
        type: 'png',
        Data: {},
        pluginType: 'transform',
        pluginName: "FieldSelect",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "FieldSelect",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "FieldSelect",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "field",
                "text": "需要保留的字段",
                "defaultValue": [],
                "required": true,
                "paramsDesc": "需要保留的字段, 回车选择",
                "desc": " ",
                "readOnly": false,
                "type": "field_array"
            },
            {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": false,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
        ],
        endpoints: [
            {
                id: 'fieldSelect_source_table_name',
                orientation: [1, 0],
                pos: [0, 0.5],
                Class: BaseEndpoint,
                color: 'system-green'
            }, {
                id: 'fieldSelect_result_table_name',
                orientation: [-1, 0],
                pos: [0, 0.5],
                Class: BaseEndpoint,
                color: 'system-green'
            }],
        content: selectIocn,
        height: 90,
        width: "100%"
    }

];
const sink = [
    {
        id: 'ConsoleSink',
        text: 'ConsoleSink',
        type: 'png',
        Data: {},
        pluginType: 'sink',
        pluginName: "ConsoleSink",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "Console",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "ConsoleSink",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": true,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
        ],
        endpoints: [{
            id: 'Console_source_table_name',
            orientation: [-1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }],
        content: devIcon,
        height: 90,
        width: "100%"
    },
    {
        id: 'Elasticsearch',
        text: 'Elasticsearch',
        type: 'png',
        Data: {},
        pluginType: 'sink',
        pluginName: "Elasticsearch",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "Elasticsearch",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "Elasticsearch",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": true,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            },
            {
                "name": "hosts",
                "text": "hosts",
                "defaultValue": [],
                "required": true,
                "paramsDesc": "es的host地址, 格式为host:端口, 例如: 10.11.12.1:9200",
                "desc": " ",

                "readOnly": false,
                "type": "array"
            },
            {
                "name": "index",
                "text": "索引",
                "defaultValue": "filling",
                "required": true,
                "paramsDesc": "es的索引名称",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            },
            {
                "name": "index_id_field",
                "text": "主键",
                "defaultValue": "",
                "required": false,
                "paramsDesc": "es中的_id字段",
                "desc": " ",

                "readOnly": false,
                "type": "field_string"
            },
            {
                "name": "es.bulk.flush.max.actions",
                "text": "刷新前要缓冲的最大文档数",
                "defaultValue": 1000,
                "required": true,
                "paramsDesc": "刷新前要缓冲的最大文档数",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            },
            {
                "name": "es.bulk.flush.max.size.mb",
                "text": "刷新前要缓冲的最大数据大小( 单位是M)",
                "defaultValue": 2,
                "required": true,
                "paramsDesc": "刷新前要缓冲的最大文档数",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            },
            {
                "name": "es.bulk.flush.interval.ms",
                "text": "刷新的时间间隔",
                "defaultValue": 1000,
                "required": true,
                "paramsDesc": "无论缓冲操作的数量或大小如何，刷新的时间间隔。",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            },
            {
                "name": "es.bulk.flush.backoff.enable",
                "text": "背压",
                "defaultValue": "true",
                "required": true,
                "paramsDesc": "背压",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            },
            {
                "name": "es.bulk.flush.backoff.delay",
                "text": "背压延迟",
                "defaultValue": 50,
                "required": true,
                "paramsDesc": "背压延迟",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
            ,
            {
                "name": "es.bulk.flush.backoff.retries",
                "text": "背压重试最大次数",
                "defaultValue": "8",
                "required": true,
                "paramsDesc": "背压重试最大次数",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
        ],
        endpoints: [{
            id: 'elasticsearch_source_table_name',
            orientation: [-1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }],
        content: elasticsearchIcon,
        height: 90,
        width: "100%"
    },


    {
        id: 'KafkaTable',
        text: 'Kafka producer',
        type: 'png',
        Data: {},
        pluginType: 'sink',
        pluginName: "KafkaSink",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "Kafka",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "KafkaSink",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": true,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            },
            {
                "name": "producer.bootstrap.servers",
                "text": "producer.bootstrap.servers",
                "defaultValue": "",
                "required": true,
                "paramsDesc": "kafka地址, 例如: 10.11.12.1:9092",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            },
            {
                "name": "topics",
                "text": "topics",
                "defaultValue": "filling",
                "required": true,
                "paramsDesc": "topics",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }
        ],
        endpoints: [{
            id: 'kafkasink_source_table_name',
            orientation: [-1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }],
        content: kafkaIcon,
        height: 90,
        width: "100%"
    },



    {
        id: 'ClickHouseSink',
        text: 'ClickHouseSink',
        type: 'png',
        Data: {},
        pluginType: 'sink',
        pluginName: "ClickHouseSink",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "ClickHouse",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "ClickHouseSink",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": true,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            },
            {
                "name": "driver",
                "text": "driver",
                "defaultValue": "ru.yandex.clickhouse.ClickHouseDriver",
                "required": true,
                "paramsDesc": "ck驱动地址",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            },
            {
                "name": "url",
                "text": "url地址, jdbc链接字符串",
                "defaultValue": "jdbc:clickhouse://127.0.0.1:8123/aiops",
                "required": true,
                "paramsDesc": "url",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            },
            {
                "name": "username",
                "text": "用户名称",
                "defaultValue": "",
                "required": false,
                "paramsDesc": "用户名称, 选填",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            },
            {
                "name": "password",
                "text": "用户密码",
                "defaultValue": "",
                "required": false,
                "paramsDesc": "用户密码, 选填",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            },
            {
                "name": "query",
                "text": "insert语句, 用?表示占位符",
                "defaultValue": "insert into user_table({columns}) values({questionMark})",
                "required": true,
                "paramsDesc": "query",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            },
            {
                "name": "params",
                "text": "insert语句的参数, 与query数量必须保持一致",
                "defaultValue": [],
                "required": true,
                "paramsDesc": "params",
                "desc": " ",

                "readOnly": false,
                "type": "field_array"
            },
            {
                "name": "batch_size",
                "text": "每个批次多少条数据",
                "defaultValue": "20000",
                "required": true,
                "paramsDesc": "params",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
        ],
        endpoints: [{
            id: 'clickHouseSink_source_table_name',
            orientation: [-1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }],
        content: ClickHouseIcon,
        height: 90,
        width: "100%"
    },

    {
        id: 'MysqlSink',
        text: 'MysqlSink',
        type: 'png',
        Data: {},
        pluginType: 'sink',
        pluginName: "MysqlSink",
        pluginOptions: [
            {
                "name": "name",
                "text": "名称",
                "defaultValue": "Mysql",
                "required": true,
                "paramsDesc": "自定义名称, 显示用",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            }, {
                "name": "plugin_name",
                "text": "插件名称",
                "defaultValue": "JdbcSink",
                "required": true,
                "paramsDesc": "插件名称, 系统自带, 无需更改",
                "desc": " ",
                "display": "none",
                "readOnly": true,
                "type": "string"
            }, {
                "name": "parallelism",
                "text": "并行度",
                "defaultValue": "1",
                "required": true,
                "paramsDesc": "flink并行度设置, 请谨慎设置",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            },
            {
                "name": "driver",
                "text": "driver",
                "defaultValue": "com.mysql.jdbc.Driver",
                "required": true,
                "paramsDesc": "Mysql驱动地址",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            },
            {
                "name": "url",
                "text": "url地址, jdbc链接字符串",
                "defaultValue": "jdbc:mysql://127.0.0.1:3306/cmdb?useSSL=false&rewriteBatchedStatements=true",
                "required": true,
                "paramsDesc": "url",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            },
            {
                "name": "username",
                "text": "用户名称",
                "defaultValue": "",
                "required": false,
                "paramsDesc": "用户名称, 选填",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            },
            {
                "name": "password",
                "text": "用户密码",
                "defaultValue": "",
                "required": false,
                "paramsDesc": "用户密码, 选填",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            },
            {
                "name": "query",
                "text": "insert语句, 用?表示占位符",
                "defaultValue": "insert into user_table({columns}) values({questionMark})",
                "required": true,
                "paramsDesc": "query",
                "desc": " ",

                "readOnly": false,
                "type": "string"
            },
            {
                "name": "params",
                "text": "insert语句, 的参数, 与query数量必须保持一致",
                "defaultValue": [],
                "required": true,
                "paramsDesc": "params",
                "desc": " ",

                "readOnly": false,
                "type": "array"
            },
            {
                "name": "batch_size",
                "text": "每个批次多少条数据",
                "defaultValue": "20000",
                "required": true,
                "paramsDesc": "params",
                "desc": " ",

                "readOnly": false,
                "type": "digit"
            }
        ],
        endpoints: [{
            id: 'mysqlSink_source_table_name',
            orientation: [-1, 0],
            pos: [0, 0.5],
            Class: BaseEndpoint,
            color: 'system-green'
        }],
        content: MysqlIcon,
        height: 90,
        width: "100%"
    }

];

const data = _.concat(source, transform, sink);
export default data;
