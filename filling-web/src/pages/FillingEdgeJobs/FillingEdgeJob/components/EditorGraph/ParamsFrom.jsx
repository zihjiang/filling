import React, { useRef, Component, useState } from 'react';
import { Button, message, Select, Switch } from 'antd';
import {
  DrawerForm,
  ProFormText,
  ProFormTextArea,
  ProFormDigit,
  ProFormRadio,
  ProFormSelect

} from '@ant-design/pro-form';
import { PlusOutlined } from '@ant-design/icons';
import _ from 'lodash';
import AceEditor from "react-ace";

import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/mode-javascript';
import "ace-builds/src-noconflict/theme-terminal";

import { Tabs, Radio, Space, Collapse, Form } from 'antd';

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

    this.setState({
      data: window.selectNode.options.data,
      pluginName: window.selectNode.options.pluginName,
      configGroupDefinition: window.selectNode.options.configGroupDefinition ? JSON.parse(window.selectNode.options.configGroupDefinition) : { groupNameToLabelMapList: [] },
      configDefinitions: window.selectNode.options.configDefinitions ? JSON.parse(window.selectNode.options.configDefinitions) : {}
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
        item.defaultValue = Object.values(values)[0];
      }
    })

    console.log("initialValues", initialValues01);

    this.setState({
      initialValues: initialValues01,
      configDefinitions: configDefinitions
    })
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
      if (options.labels) {
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
            name={_item.name}
            label={_item.label}
            defaultValue={_item.defaultValue}
            valuePropName="value">

            <AceEditor
              placeholder="Placeholder Text"
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

          <Form.Item
            name={_item.name}
            label={_item.label}
            defaultValue={_item.defaultValue}
            valuePropName="checked">
            <Switch />
          </Form.Item>

        )
      default:
        console.log("default: ", _item);

        return (<a> {_item.type} </a>);
    }
  }



  render() {
    let initialValues = this.state.initialValues;
    let configDefinitions = this.state.configDefinitions;
    let data = this.state.data;

    // if (pluginOptions) {
    if (this.state.data != undefined) {
      // 编辑
      initialValues = data;
      console.log('编辑', initialValues);
    } else {
      // 新建
      console.log('configDefinitions', configDefinitions);
      configDefinitions.forEach((options) => {
        initialValues[options.name] = options['defaultValue'];
      })

      console.log('新建');
    }
    console.log('window.jobRunStatus', window.jobRunStatus);

    console.log('initialValues', initialValues);

    console.log('configGroupDefinition', this.state.configGroupDefinition);
    // }
    let Universal = () => {
      if (this.state.configGroupDefinition)
        return (this.state.configGroupDefinition.groupNameToLabelMapList).map((item, idx) => {
          return (<TabPane tab={item.label} key={idx}>
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
          </TabPane>)
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