import React, { useRef, Component, useState } from 'react';
import { Button, message, Select, AutoComplete } from 'antd';
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
import 'ace-builds/src-noconflict/mode-sql';
import "ace-builds/src-noconflict/theme-terminal";
import 'ace-builds/src-noconflict/theme-github';
import "ace-builds/src-noconflict/ext-language_tools"
import { targetAutocomplete, getKey } from "@/utils/autocomplete";

import { Form } from 'antd';


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
    // 更改节点的值
    if (values.name) {
      document.getElementById(selectNode.id).innerText = values.name;
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

    // console.log("editor", editor);

    // if (pluginOptions) {
    if (this.state.data != undefined) {
      // 编辑
      initialValues = data;
    } else {
      // 新建
      pluginOptions.forEach((pluginOption) => {
        initialValues[pluginOption.name] = pluginOption['defaultValue'];
      })

      console.log('新建');
    }

    targetAutocomplete();

    // }

    let Universal = () => (
      this.state.pluginOptions.map((item, idx) => {
        switch (item.type) {
          case "string":
            return <Form.Item
              style={{ display: item.display }}
              key={idx}
            > <ProFormText
                name={item.name}
                label={item.text}
                tooltip={item.paramsDesc}
                placeholder={item.paramsDesc}
                style={{ display: item.display }}
                disabled={item.readOnly || window.jobRunStatus}
                formItemProps={
                  {
                    rules: [
                      {
                        required: item.required,
                        message: `${item.text}是必须的`,
                      },
                      {
                        pattern: new RegExp(item.ruleRegexp),
                        message: item.ruleMessage
                      }
                    ],
                  }
                }
              />
            </Form.Item>
          case "ace-auto-complete":
            return <Form.Item
              key={idx}
              name={item.name}
              label={item.text}
              tooltip={item.paramsDesc}
              placeholder={item.paramsDesc}
              // style={{ display: item.display }}
              disabled={item.readOnly || window.jobRunStatus}
              valuePropName="value">
              <AceEditor
                mode={'sql'}
                theme="github"
                fontSize={12}
                height={'200px'}
                width={window.screen.width * 0.17 + 'px'}
                showPrintMargin={true}
                showGutter={true}
                highlightActiveLine={true}
                editorProps={{ $blockScrolling: true }}
                setOptions={{
                  enableBasicAutocompletion: true,
                  enableLiveAutocompletion: true,
                  enableSnippets: true,
                  showLineNumbers: true,
                  tabSize: 2
                }} />
            </Form.Item>
          case "text":
            return (
              <Form.Item
                key={idx}
                name={item.name}
                label={item.label}
                tooltip={item.paramsDesc}
                placeholder={item.paramsDesc}
                defaultValue={item.defaultValue}
                valuePropName="value">
                <AceEditor
                  placeholder={item.description}
                  mode={item.mode}
                  name="data"
                  theme="github"
                  fontSize={12}
                  showPrintMargin={true}
                  showGutter={true}
                  highlightActiveLine={true}
                  width={window.screen.width * 0.17 + 'px'}
                  editorProps={{ $blockScrolling: false }}
                  setOptions={{
                    enableBasicAutocompletion: true,
                    enableLiveAutocompletion: true,
                    enableSnippets: true,
                    showLineNumbers: true,
                    tabSize: 2,
                  }} />
              </Form.Item>);
          case "textArea":
            return <ProFormTextArea
              key={idx}
              name={item.name}
              label={item.text}
              tooltip={item.paramsDesc}
              placeholder={item.paramsDesc}
              style={{ display: item.display }}
              disabled={item.readOnly || window.jobRunStatus}
            />
          case "digit":
            return <ProFormDigit
              key={idx}
              name={item.name}
              label={item.text}
              tooltip={item.paramsDesc}
              placeholder={item.paramsDesc}
              style={{ display: item.display }}
              min={item.digitMin}
              max={item.digitMax}
              disabled={item.readOnly || window.jobRunStatus}
            />

          case "select":
            return <ProFormSelect
              key={idx}
              name={item.name}
              label={item.text}
              tooltip={item.paramsDesc}
              placeholder={item.paramsDesc}
              style={{ display: item.display }}
              disabled={item.readOnly || window.jobRunStatus}
              options={item.selectOptions}>
            </ProFormSelect>

          case "array":
            return <Form.Item
              style={{ display: item.display }}
            >
              <ProFormSelect
                key={idx}
                mode="tags"
                name={item.name}
                label={item.text}
                tooltip={item.paramsDesc}
                placeholder={item.paramsDesc}
                style={{ display: item.display }}
                disabled={item.readOnly || window.jobRunStatus}
                options={item.selectOptions}>
              </ProFormSelect>
            </Form.Item>

          case "field_array":
            let options = getKey().map(d => {
              d['value'] = d.word;
              d['label'] = d.word;
              return d;
            });

            return <Form.Item
              style={{ display: item.display }}
            >
              <ProFormSelect
                key={idx}
                mode="tags"
                name={item.name}
                label={item.text}
                tooltip={item.paramsDesc}
                placeholder={item.paramsDesc}
                style={{ display: item.display }}
                disabled={item.readOnly || window.jobRunStatus}
                options={options}>
              </ProFormSelect>
            </Form.Item>

          case "field_string":
            options = getKey().map(d => {
              d['value'] = d.word;
              d['label'] = d.word;
              return d;
            });

            return <Form.Item
              key={idx}
              name={item.name}
              label={item.text}
              tooltip={item.paramsDesc}
              placeholder={item.paramsDesc}
              // style={{ display: item.display }}
              disabled={item.readOnly || window.jobRunStatus}
              valuePropName="value">
              <AutoComplete
                options={options}>
              </AutoComplete>
            </Form.Item>

          // case "text_rex_id":
          //   return <ProFormText
          //     key={idx}
          //     tooltip={item.paramsDesc.replace("{id}", window.selectNode.id).replaceAll("-", "_")}
          //     name={item.name.replace("{id}", window.selectNode.id).replaceAll("-", "_")}
          //     label={item.text.replace("{id}", window.selectNode.id).replaceAll("-", "_")}
          //     placeholder={item.paramsDesc.replace("{id}", window.selectNode.id).replaceAll("-", "_")}
          //     style={{ display: item.display }}
          //     disabled={item.readOnly || window.jobRunStatus}
          //     options={item.selectOptions}>
          //   </ProFormText>

          case "text_rex_id":
            return <Form.Item
              key={idx}
              tooltip={item.paramsDesc.replace("{id}", window.selectNode.id).replaceAll("-", "_")}
              name={item.name.replace("{id}", window.selectNode.id).replaceAll("-", "_")}
              label={item.text.replace("{id}", window.selectNode.id).replaceAll("-", "_")}
              placeholder={item.paramsDesc.replace("{id}", window.selectNode.id).replaceAll("-", "_")}
              disabled={item.readOnly || window.jobRunStatus}
              valuePropName="value">
              <AceEditor
                mode={'sql'}
                theme="github"
                fontSize={12}
                height={'20px'}
                width={window.screen.width * 0.17 + 'px'}
                showPrintMargin={true}
                showGutter={true}
                highlightActiveLine={true}
                editorProps={{ $blockScrolling: true }}
                setOptions={{
                  enableBasicAutocompletion: true,
                  enableLiveAutocompletion: true,
                  enableSnippets: true,
                  showLineNumbers: true,
                  tabSize: 2
                }} />
            </Form.Item>


          case "child":
            return (_.find(this.state.pluginOptions, (d) => { return d.name == item.father }) || []).defaultValue.map((_item, _idx) => {
              return <ProFormText
                key={idx + _idx}
                name={item.name.replace("{field}", _item)}
                label={item.text.replace("{field}", _item)}
                placeholder={item.paramsDesc.replace("{field}", _item)}
                style={{ display: item.display }}
                disabled={item.readOnly || window.jobRunStatus}
                options={item.selectOptions}>
              </ProFormText>
            })

        }
      })
    );

    let Configuration = () => {
      return <AceEditor
        placeholder="Placeholder Text"
        mode="json"
        name="data"
        theme="terminal"
        fontSize={12}
        width={window.screen.width * 0.17 + 'px'}
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
          width='20%'
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