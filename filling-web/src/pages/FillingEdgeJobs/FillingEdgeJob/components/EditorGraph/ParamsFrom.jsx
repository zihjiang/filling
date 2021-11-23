import React, { useRef, Component, useState } from 'react';
import { Button, message, Select, Switch } from 'antd';
import {
  DrawerForm,
  ProFormText,
  ProFormDigit,
  ProFormRadio,
  ProFormSelect,
  ProFormSwitch,
  ProFormList

} from '@ant-design/pro-form';
import { PlusOutlined } from '@ant-design/icons';
import _ from 'lodash';
import AceEditor from "react-ace";

import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/mode-javascript';
import "ace-builds/src-noconflict/theme-terminal";
import ProForm from '@ant-design/pro-form';

import { Tabs, Collapse, Form } from 'antd';

import { EditableProTable } from '@ant-design/pro-table';

const { Panel } = Collapse;

const { TabPane } = Tabs;
class ParamsFrom extends Component {
  constructor(props) {
    super(props);

    this.state = {
      dataId: "",
      data: {},
      pluginName: "",
      pluginOptions: [],
      initialValues: {},
      editModel: '配置'
    }

  }

  componentDidMount() {
  }

  handleUpdate = () => {

    let configGroupDefinition = window.selectNode.options.configGroupDefinition ? JSON.parse(window.selectNode.options.configGroupDefinition) : { groupNameToLabelMapList: [] };
    let configDefinitions = window.selectNode.options.configDefinitions ? JSON.parse(window.selectNode.options.configDefinitions) : {};
    // 初始化通用配置
    configGroupDefinition.groupNameToLabelMapList.unshift(
      {
        "name": "General",
        "label": "基本信息"
      }
    );

    configDefinitions.push(
      {
        "name": "name",
        "type": "STRING",
        "defaultValue": null,
        "dependsOnMap": {},
        "dependsOn": "",
        "group": "General",
        "upload": "NO",
        "lines": 0,
        "triggeredByValues": null,
        "displayPosition": 1000,
        "connectionType": "",
        "displayMode": "BASIC",
        "label": "算子名称",
        "fieldName": "instanceName",
        "model": null,
        "required": true,
        "description": "",
        "mode": "text/plain",
        "elDefs": null,
        "evaluation": "IMPLICIT"
      },
      {
        "name": "description",
        "type": "TEXT",
        "defaultValue": null,
        "dependsOnMap": {},
        "dependsOn": "",
        "group": "General",
        "upload": "NO",
        "lines": 0,
        "triggeredByValues": null,
        "displayPosition": 1000,
        "connectionType": "",
        "displayMode": "BASIC",
        "label": "说明",
        "fieldName": "description",
        "model": null,
        "required": true,
        "description": "",
        "mode": "text/plain",
        "elDefs": null,
        "evaluation": "IMPLICIT"
      }
    );
    this.setState({
      data: window.selectNode.options.data,
      pluginName: window.selectNode.options.pluginName,
      configGroupDefinition: configGroupDefinition,
      configDefinitions: configDefinitions
    });

    // this._forceUpdate({1: 1});
  }
  // 更新node的数据
  updateData = (values, initialValues) => {

    console.log("values", values);

    switch (this.state.editModel) {
      case "json":
        _.map(window.canvas.nodes, (d) => { if (d.id == window.selectNode.id) d.options.data = this.state.data });
        break;
      default:
        _.map(window.canvas.nodes, (d) => { if (d.id == window.selectNode.id) d.options.data = _.merge(initialValues, values) });
        break;
    }
  }

  _forceUpdate = (values) => {

    let initialValues01 = this.state.initialValues;
    let configDefinitions = this.state.configDefinitions;

    initialValues01[Object.keys(values)[0]] = Object.values(values)[0];

    configDefinitions.map((item) => {
      if (item.name == Object.keys(values)[0]) {
        console.log('-');
        item.defaultValue = Object.values(values)[0];
      }
    })
    this.setState({
      initialValues: initialValues01,
      configDefinitions: configDefinitions
    })

    console.log('configDefinitions', configDefinitions, values);
  }

  changeType = (value) => {
    this.setState(
      {
        editModel: value
      }
    )
  }

  // 检测依赖
  dependsShow = (item, items) => {
    let dependsOnMap = item.dependsOnMap;
    let returnValue = true;

    if (JSON.stringify(dependsOnMap) == "{}") {
      return true;
    }
    Object.keys(dependsOnMap).forEach(d => {
      if (dependsOnMap[d] != undefined) {
        if (dependsOnMap[d].indexOf(items[d]) < 0) {
          returnValue = false;
        }
      } else {
        returnValue = false;
      }
    })
    return returnValue;
  }


  // 生成from表单的元素
  generationFromItem = (_item, _idx) => {
    const conver_options = (options) => {
      let _options = [];
      if (options.labels != null && options.labels) {
        for (let i = 0; i < options.labels.length; i++) {
          _options.push({ value: options.values[i], label: options.labels[i] });
        }
      }
      return _options;
    }
    switch (_item.type) {
      case "TEXT":
        return (
          <Form.Item
            key={_idx}
            name={_item.name}
            label={_item.label}
            defaultValue={_item.defaultValue}
            valuePropName="value">

            <AceEditor
              placeholder={_item.description}
              mode={_item.mode == 'text/javascript' ? 'javascript' : 'json'}
              name="data"
              theme="terminal"
              fontSize={12}
              showPrintMargin={true}
              showGutter={true}
              highlightActiveLine={true}
              setOptions={{
                enableBasicAutocompletion: true,
                enableLiveAutocompletion: true,
                enableSnippets: true,
                showLineNumbers: true,
                tabSize: 2,
              }} />
          </Form.Item>);
      case "STRING":
        return (
          <ProFormText
            key={_idx}
            name={_item.name}
            label={_item.label}
            tooltip={_item.description}
            placeholder={_item.description}
            style={{ _item: _item.display }}
            defaultValue={_item.defaultValue}
            formItemProps={
              {
                rules: [
                  {
                    required: _item.required,
                    message: `${_item.label}是必须的`,
                  },
                ],
              }
            }
          />
        )
      case "MODEL":

        // 如果是LIST_BEAN
        if (_item.model && _item.model.modelType == 'LIST_BEAN') {

          console.log("_item: ", _item);
          return (
            <ProFormList
              key={_idx}
              name={_item.name}
              title={_item.label}
              creatorButtonProps={{
                creatorButtonText: '增加',
              }}
              placeholder={_item.description}
            >
              {_item.model.configDefinitions.map((__item, i) => {
                return this.generationFromItem(__item, _idx + i);
              })}

            </ProFormList>
          );
          // let element = [];
          // for (let i = 0; i < _item.model.configDefinitions.length; i++) {
          //   element.push(this.generationFromItem(_item.model.configDefinitions[i], _idx + i));
          // }
          // return element;
        } else {
          console.log("_item: ", _item);
          return (
            <ProFormSelect
              key={_idx}
              name={_item.name}
              label={_item.label}
              tooltip={_item.description}
              placeholder={_item.description}
              style={{ _item: _item.display }}
              defaultValue={_item.defaultValue}
              options={conver_options(_item.model)}
              formItemProps={
                {
                  rules: [
                    {
                      required: _item.required,
                      message: `${_item.label}是必须的`,
                    },
                  ],
                }
              }
            />
          )
        }
      case "NUMBER":
        return (
          <ProFormDigit
            key={_idx}
            name={_item.name}
            label={_item.label}
            tooltip={_item.description}
            placeholder={_item.description}
            style={{ _item: _item.display }}
            defaultValue={_item.defaultValue}
            min={_item.min}
            max={_item.max}
            formItemProps={
              {
                rules: [
                  {
                    required: _item.required,
                    message: `${_item.label}是必须的`,
                  },
                ],
              }
            }
          />
        )
      case "BOOLEAN":
        return (
          <ProFormSwitch
            key={_idx}
            name={_item.name}
            label={_item.label}
            tooltip={_item.description}
            placeholder={_item.description}
            style={{ _item: _item.display }}
            defaultValue={_item.defaultValue}
            checked={_item.defaultValue}
            defaultChecked={_item.defaultValue}
          />
        )
      case "MAP":
        const columns = [
          {
            title: 'key',
            key: 'key',
            dataIndex: 'key',
            formItemProps: (form, { rowIndex }) => {
              return {
                rules: rowIndex > 2 ? [{ required: true, message: '此项为必填项' }] : [],
              };
            },
            width: '35%',
          },
          {
            title: 'value',
            key: 'value',
            dataIndex: 'value',
            width: '35%',
          },
          {
            title: '操作',
            valueType: 'option',
            width: "30%",
            render: (text, record, _, action) => [
              <a
                key="editable"
                onClick={() => {
                  action?.startEditable?.(record.id);
                }}
              >
                编辑
              </a>,
              <a
                key="delete"
                onClick={() => { }}
              >
                删除
              </a>,
            ],
          },
        ];
        return (
          <Form.Item
            key={_idx}
            name={_item.name}
            label={_item.label}
            defaultValue={_item.defaultValue}
            valuePropName="ss"
          >
            <EditableProTable
              key={_idx}
              rowKey={_idx}
              headerTitle={_item.label}
              maxLength={10}
              columns={columns}
              editable={{
                type: 'multiple',
                onSave: async (rowKey, data, row) => {
                  console.log(rowKey, data, row);
                }
              }}
            />
          </Form.Item>
        );
      default:
        console.log("default: ", _item);

        return (<a key={_idx}> {_item.type} </a>);
    }
  }



  render() {
    let initialValues = this.state.initialValues;
    let configDefinitions = this.state.configDefinitions;
    let data = this.state.data;

    if (this.state.data != undefined) {
      // 编辑
      initialValues = data;
      console.log('编辑', initialValues);
    } else {
      // 新建
      configDefinitions.forEach((options) => {
        initialValues[options.name] = options['defaultValue'];
      })

    }
    // }
    let Universal = () => {
      if (this.state.configGroupDefinition)
        return (this.state.configGroupDefinition.groupNameToLabelMapList).map((item, idx) => {
          return (
              <TabPane tab={item.label} key={idx}>
                {this.state.configDefinitions.filter(d => (d.group == item.name)).map((_item, _idx) => {
                  if (_item.displayMode == "BASIC" && this.dependsShow(_item, initialValues)) {
                    // this.isDepends(_item, initialValues);
                    return this.generationFromItem(_item, _idx);
                  }
                })}

                <Collapse >
                  <Panel header="高级选项" key="1">
                    {this.state.configDefinitions.filter(d => (d.group == item.name)).map((_item, _idx) => {
                      if (_item.displayMode != "BASIC" && this.dependsShow(_item, initialValues)) {
                        // this.isDepends(_item, initialValues);
                        return this.generationFromItem(_item, _idx);
                      }
                    })}
                  </Panel>
                </Collapse>
              </TabPane>
          )
        })
    };

    let Configuration = () => {
      return <AceEditor
        placeholder="Placeholder Text"
        mode="json"
        name="data"
        theme="terminal"
        fontSize={12}
        showPrintMargin={true}
        showGutter={true}
        highlightActiveLine={true}
        value={JSON.stringify(initialValues, null, 2)}
        // onChange={(d) => this.setState({ data: d })}
        onChange={(d) => this.state.data = JSON.parse(d)}
        setOptions={{
          enableBasicAutocompletion: true,
          enableLiveAutocompletion: true,
          enableSnippets: true,
          showLineNumbers: true,
          tabSize: 2,
        }} />
    }

    let getFrom = (type) => {
      return type == 'json' ? Configuration() : Universal();
    }
    return (
      <>
        <DrawerForm
          title={this.state.pluginName}
          trigger={
            <div onClick={this.handleUpdate}>
              <PlusOutlined />
            </div>
          }
          drawerProps={{
            forceRender: true,
            destroyOnClose: true
          }}
          onFinish={async (values) => {
            // 不返回不会关闭弹框
            this.updateData(values, initialValues);
            message.success('提交成功');
            return true;

          }}
          width='40%'
          initialValues={initialValues}
          onValuesChange={(value) => this._forceUpdate(value)}
        >

          <ProFormRadio.Group
            style={{
              margin: 16,
            }}
            radioType="button"
            fieldProps={{
              value: this.state.editModel,
              onChange: (e) => this.changeType(e.target.value),
            }}
            options={[
              '配置',
              'json'

            ]}
          />
          <Tabs tabPosition={'left'}>
            {getFrom(this.state.editModel)}
          </Tabs>
        </DrawerForm>
      </>
    );
  }
}

export { ParamsFrom };