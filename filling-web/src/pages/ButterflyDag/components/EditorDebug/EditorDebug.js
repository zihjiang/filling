import React, { Component } from 'react';

import { Collapse } from 'antd';
import _ from 'lodash';

import { diff as DiffEditor } from 'react-ace';
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/mode-javascript';
import "ace-builds/src-noconflict/theme-xcode";

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
      currentData: {}
    };
  }

  componentDidMount() {

  }

  render() {
    const { Panel } = Collapse;

    return (
      <div id="EditorDebug">
        <Collapse
          defaultActiveKey={['0']}
          expandIconPosition={'right'}
          destroyInactivePanel={true}
        >
          <Panel header="Preview data" key="1">
            <ProFormSelect
              label="stage"
              showSearch
              allowClear={false}
              fieldProps={{
                labelInValue: true,
              }}
            />
            <div>
              <DiffEditor
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
                theme="xcode"
                setOptions={{
                  enableBasicAutocompletion: true,
                  enableLiveAutocompletion: true,
                  enableSnippets: true,
                  showLineNumbers: true,
                  showConnectors: true,
                  readOnly: true,
                  tabSize: 2,
                }}
              />

            </div>
          </Panel>
        </Collapse>
      </ div>
    );
  }
}

export { EditorDebug };
