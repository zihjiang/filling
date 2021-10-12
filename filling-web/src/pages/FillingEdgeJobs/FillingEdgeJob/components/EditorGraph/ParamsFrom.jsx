import React, { useRef, Component, useState } from 'react';
import { Button, message, Select } from 'antd';
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
import "ace-builds/src-noconflict/theme-terminal";

import { Tabs, Radio, Space } from 'antd';

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
      // data: window.canvas.getNode(window.selectNode.id).data == undefined ? window.canvas.getNode(window.selectNode.id).options.data : window.canvas.getNode(window.selectNode.id).data,
      data: window.selectNode.options.data,
      pluginName: window.selectNode.options.pluginName,
      pluginOptions: window.selectNode.options.pluginOptions ? JSON.parse(window.selectNode.options.pluginOptions) : {}
    });
    console.log('pluginOptions', this.state.pluginOptions);

    // this._forceUpdate({1: 1});
  }
  // 更新node的数据
  updateData = (values) => {

    switch (this.state.editModel) {
      case "json":
        _.map(window.canvas.nodes, (d) => { if (d.id == window.selectNode.id) d.options.data = this.state.data });
        break;
      default:
        _.map(window.canvas.nodes, (d) => { if (d.id == window.selectNode.id) d.options.data = _.merge(d.options.data, values) });
        break;
    }
  }

  _forceUpdate = (values) => {


    let initialValues01 = this.state.initialValues;
    let pluginOptions01 = this.state.pluginOptions;

    initialValues01[Object.keys(values)[0]] = Object.values(values)[0];

    pluginOptions01.map((item) => {

      if (item.name == Object.keys(values)[0]) {
        item.defaultValue = Object.values(values)[0];
      }
    })

    this.setState({
      initialValues: initialValues01,
      pluginOptions: pluginOptions01
    })



  }

  changeType = (value) => {
    this.setState(
      {
        editModel: value
      }
    )
  }


  render() {
    let initialValues = this.state.initialValues;
    const pluginOptions = this.state.pluginOptions;
    console.log('pluginOptions', pluginOptions);
    let data = this.state.data;

    // if (pluginOptions) {
    if (this.state.data != undefined) {
      // 编辑
      initialValues = data;
      console.log('编辑', initialValues);
    } else {
      // 新建
      pluginOptions.forEach((pluginOption) => {
        initialValues[pluginOption.name] = pluginOption['defaultValue'];
      })

      console.log('新建');
    }
    console.log('pluginOptions', pluginOptions);
    console.log('window.jobRunStatus', window.jobRunStatus);
    // }

    let Universal = () => (
      <Tabs tabPosition={'left'}>
        <TabPane tab="Tab 1" key="1">
          Content of Tab 1
        </TabPane>
        <TabPane tab="Tab 2" key="2">
          Content of Tab 2
        </TabPane>
        <TabPane tab="Tab 3" key="3">
          Content of Tab 3
        </TabPane>
      </Tabs>
    );

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
            this.updateData(values);
            message.success('提交成功');
            return true;

          }}
          width='30%'
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
          {getFrom(this.state.editModel)}
        </DrawerForm>
      </>
    );
  }
}

export { ParamsFrom };