import React, { Component } from 'react';

import { Collapse, Tabs } from 'antd';
import _ from 'lodash';

import { diff as DiffEditor } from 'react-ace';
import AceEditor from "react-ace";

import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/mode-javascript';
import "ace-builds/src-noconflict/theme-github";

import 'ace-builds/src-noconflict/ext-searchbox';

import styles from './index.less';



import {
  ProFormSelect,
} from '@ant-design/pro-form';

class EditorDebug extends Component {
  constructor(props) {
    super(props);
    this.state = {
      stage: [],
      previewData: {},
      beforeData: {},
      currentData: {},
      selectData: [],
      selectTabKey: "log",
      log: ""
    };
  }

  componentDidMount() {

  }

  /**
   * 点击按钮触发
   */
  changeData = () => {
    const debugInfo = window.debugInfo;
    if (debugInfo) {
      const canvas = window.deCodeDataMap(window.canvas);

      canvas.nodes.forEach(node => {
        node.data.id = node.id;
      });


      let selectData = [];
      canvas.nodes.filter(d => d.PluginType != 'sink').forEach(node => {
        this.state.previewData[node.data.name] = debugInfo.previewData[node.data['result_table_name']];
        selectData.push({
          label: node.data.name,
          value: node.data['result_table_name']
        });
      });

      console.log(this.state.previewData);


      this.setState({
        log: debugInfo.log,
        selectData: selectData,
        selectTabKey: debugInfo.status ? 'preview' : 'log'
      })
    }
  }
  /**
   * 
   * @param {改变的数据} d 
   */
  changeSelect = (d) => {

    const canvas = window.deCodeDataMap(window.canvas);
    const node = canvas.edges.filter(_d => _d.targetNode == d.value.replaceAll('_', '-'))[0];

    console.log('node', node);
    if (node) {
      // previewDataNode.

      console.log('debugInfo.previewData', debugInfo.previewData);

      console.log(node.sourceNode);
      console.log('debugInfo.previewData[node.sourceNode]', debugInfo.previewData[node.sourceNode.replaceAll('-', '_')]);

      const beforeData = debugInfo.previewData[node.sourceNode.replaceAll('-', '_')];
      const currentData = debugInfo.previewData[d.value.replaceAll('-', '_')];

      this.setState({
        beforeData: beforeData,
        currentData: currentData
      })

      console.log('beforeData', beforeData);
      console.log('currentData', currentData);
    } else {
      this.setState({
        beforeData: [],
        currentData: debugInfo.previewData[d.value]
      })
    }
  }

  render() {

    const { Panel } = Collapse;
    const { TabPane } = Tabs;
    return (
      <>

        <div id="EditorDebug">
          <Collapse
            defaultActiveKey={['0']}
            expandIconPosition={'right'}
            destroyInactivePanel={true}
            onChange={() => {
              this.changeData();

            }}
          >
            <Panel header="预览数据" key="1">

              <Tabs tabPosition={'left'} defaultActiveKey={this.state.selectTabKey} destroyInactiveTabPane={true}>
                <TabPane tab="预览" key="preview">
                  <ProFormSelect
                    label="算子"
                    showSearch
                    allowClear={false}
                    fieldProps={{
                      labelInValue: true,
                    }}
                    options={this.state.selectData}
                    onChange={(d) => this.changeSelect(d)}
                  />
                  <div>
                    <DiffEditor
                      value={[JSON.stringify(this.state.beforeData, null, 2), JSON.stringify(this.state.currentData, null, 2)]}
                      mode="json"
                      enableBasicAutocompletion
                      enableLiveAutocompletion
                      highlightActiveLine
                      showGutter
                      showPrintMargin
                      wrapEnabled
                      // readOnly
                      width="100%"
                      height="300px"
                      theme="github"
                      setOptions={{
                        enableBasicAutocompletion: true,
                        enableLiveAutocompletion: true,
                        enableSnippets: true,
                        showLineNumbers: true,
                        showConnectors: true,
                        useWorker: false,
                        readOnly: true,
                        tabSize: 2,
                      }}
                    />

                  </div>
                </TabPane>
                <TabPane tab="日志" key="log">
                  <div>
                    <AceEditor
                      mode="json"
                      enableBasicAutocompletion
                      enableLiveAutocompletion
                      highlightActiveLine
                      showGutter
                      showPrintMargin
                      value={this.state.log}
                      // readOnly
                      width="100%"
                      height="350px"
                      theme="github"
                      setOptions={{
                        enableBasicAutocompletion: true,
                        enableLiveAutocompletion: true,
                        enableSnippets: true,
                        showLineNumbers: true,
                        showConnectors: true,
                        readOnly: true
                      }}
                    />

                  </div>
                </TabPane>
              </Tabs>
            </Panel>
          </Collapse>
        </ div>
      </>
    );
  }
}

export { EditorDebug };
