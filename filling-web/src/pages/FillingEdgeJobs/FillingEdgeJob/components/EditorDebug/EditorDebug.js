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

  getFieldPaths(record, fieldPaths, nonListAndMap, fieldPathsType, dFieldPaths, cvalue) {
    var keys;
    if (record.type === 'LIST') {
      _.forEach(record.value, function (value) {
        if (value.type === 'MAP' || value.type === 'LIST' || value.type === 'LIST_MAP') {
          if (!nonListAndMap && value.sqpath) {
            fieldPaths.push(value.sqpath);
            dFieldPaths.push(value.dqpath);
            cvalue.push(value.value);

            if (fieldPathsType) {
              fieldPathsType.push(value.type);
            }
          }
          (value, fieldPaths, nonListAndMap, fieldPathsType, dFieldPaths, cvalue) => getFieldPaths(value, fieldPaths, nonListAndMap, fieldPathsType, dFieldPaths, cvalue);
        } else if (value.sqpath) {
          fieldPaths.push(value.sqpath);
          dFieldPaths.push(value.dqpath);
          cvalue.push(value.value);

          if (fieldPathsType) {
            fieldPathsType.push(value.type);
          }
        }
      });
    } else if (record.type === 'MAP') {
      if (record.value) {
        keys = Object.keys(record.value).sort();
        _.forEach(keys, function (key) {
          var value = record.value[key];
          if (value.type === 'MAP' || value.type === 'LIST' || value.type === 'LIST_MAP') {
            if (!nonListAndMap && value.sqpath) {
              fieldPaths.push(value.sqpath);
              dFieldPaths.push(value.dqpath);
            }

            var d = {};
            d[key] = value.value;
            cvalue.push(d);
            console.log("record.type: ", value.value);

            (value, fieldPaths, nonListAndMap, fieldPathsType, dFieldPaths, cvalue) => getFieldPaths(value, fieldPaths, nonListAndMap, fieldPathsType, dFieldPaths, cvalue);

            if (fieldPathsType) {
              fieldPathsType.push(value.type);
            }
          } else if (value.sqpath) {
            fieldPaths.push(value.sqpath);
            dFieldPaths.push(value.dqpath);

            var d = {};
            d[key] = value.value;
            cvalue.push(d);

            if (fieldPathsType) {
              fieldPathsType.push(value.type);
            }
          }
        });
      }
    } else if (record.type === 'LIST_MAP') {
      _.forEach(record.value, function (value, index) {
        if (value.type === 'MAP' || value.type === 'LIST' || value.type === 'LIST_MAP') {
          if (!nonListAndMap && value.sqpath) {
            fieldPaths.push(value.sqpath);
            dFieldPaths.push(value.dqpath);


            if (fieldPathsType) {
              fieldPathsType.push(value.type);
            }
          }

          if (value.type === 'MAP') {
            var d = {};
            d[key] = value.value;
            cvalue.push(d);
          }
          (value, fieldPaths, nonListAndMap, fieldPathsType, dFieldPaths, cvalue) => getFieldPaths(value, fieldPaths, nonListAndMap, fieldPathsType, dFieldPaths, cvalue);
        } else if (value.sqpath) {
          fieldPaths.push(value.sqpath);
          dFieldPaths.push(value.dqpath);
          var d = {};
          d[key] = value.value;
          cvalue.push(d);

          if (!nonListAndMap) {
            fieldPaths.push('[' + index + ']');
            dFieldPaths.push('[' + index + ']');
          }

          if (fieldPathsType) {
            fieldPathsType.push(value.type);
          }
        }
      });
    } else {
      // fieldPaths.push(pipelineConstant.NON_LIST_MAP_ROOT);
      // dFieldPaths.push(pipelineConstant.NON_LIST_MAP_ROOT);
      console.log("record: ", record);

      if (fieldPathsType) {
        fieldPathsType.push(record.type);
      }
    }

    console.log("record: ", record);
  }

  // 点击Collapse事件
  getpreviewData = () => {
    let previewData = window.previewData || {};
    var stage = [];
    previewData.batchesOutput[0].forEach(element => {
      let obj = {};
      obj.label = element.instanceName;
      obj.value = element.instanceName;
      stage.push(obj);
    });
    this.setState({
      stage: stage
    });
  }

  getSelectContent = (selectStage) => {

    var preViewData = (currentData) => {
      let cvalue = [];
      for (let index = 0; index < currentData.length; index++) {
        let fieldPaths = [], fieldPathsType = [], dFieldPaths = [];
        const element = currentData[index].value;

        this.getFieldPaths(element, fieldPaths, false, fieldPathsType, dFieldPaths, cvalue);
      }

      return { fieldPaths, fieldPathsType, dFieldPaths, cvalue };
    }
    let batchesOutput = window.previewData.batchesOutput[0] || { instanceName: null };
    let currentIndex = _.findIndex(batchesOutput, d => d.instanceName == selectStage);

    let beforeData = this.getPreviewDataForStage(batchesOutput, batchesOutput[currentIndex - 1]).output || [];
    let currentData = this.getPreviewDataForStage(batchesOutput, batchesOutput[currentIndex]).output || [];


    let fieldPaths = [], fieldPathsType = [], dFieldPaths = [], cvalue = [];

    // preViewData(beforeData);
    // preViewData(currentData);

    this.setState({
      beforeData: preViewData(beforeData).cvalue,
      currentData: preViewData(currentData).cvalue
    });

  }

  preData = (data) => {
    let result = [];
    for (let i = 0; i < data.output[Object.keys(data.output)].length; i++) {
      const _current = data.output[Object.keys(data.output)][i];
      for (let index = 0; index < Object.keys(_current.value.value).length; index++) {
        const element = Object.keys(_current.value.value)[index];

        result.push(element);

      }
    }

    console.log(result);
  }



  getPreviewDataForStage = (batchData, stageInstance) => {
    if (!stageInstance) return { output: undefined };
    let stagePreviewData = {
      input: [],
      output: [],
      errorRecords: [],
      stageErrors: [],
      newRecords: []
    };

    _.forEach(batchData, function (stageOutput) {
      if (stageOutput.instanceName === stageInstance.instanceName) {
        _.forEach(stageOutput.output, function (outputs, laneName) {
          _.forEach(outputs, function (output, index) {
            output.laneName = laneName;
            if (index < 2) {
              output.expand = true;
            }
            stagePreviewData.output.push(output);
            if (output.header && !output.header.previousTrackingId) {
              stagePreviewData.newRecords.push(output);
            }
          });
        });
        stagePreviewData.errorRecords = stageOutput.errorRecords;
        stagePreviewData.stageErrors = stageOutput.stageErrors;
        stagePreviewData.eventRecords = [];
        if (stageOutput.eventRecords && stageOutput.eventRecords.length) {
          var eventLane = stageOutput.instanceName + '_EventLane';
          _.forEach(stageOutput.eventRecords, function (eventRecord) {
            eventRecord.laneName = eventLane;
            stagePreviewData.eventRecords.push(eventRecord);
          });
        }
      }

      if (stageOutput.output && stageInstance.inputLanes && stageInstance.inputLanes.length) {
        _.forEach(stageInstance.inputLanes, function (inputLane) {
          if (stageOutput.output[inputLane]) {
            _.forEach(stageOutput.output[inputLane], function (input) {
              input.laneName = inputLane;
              stagePreviewData.input.push(input);
            });
          } else if (inputLane === stageOutput.instanceName + '_EventLane') {
            _.forEach(stageOutput.eventRecords, function (input) {
              input.laneName = inputLane;
              stagePreviewData.input.push(input);
            });
          }
        });
      }

    });

    return stagePreviewData;
  };


  render() {
    const { Panel } = Collapse;

    return (
      <div id="EditorDebug">
        <Collapse
          defaultActiveKey={['0']}
          expandIconPosition={'right'}
          onChange={this.getpreviewData}
        >
          <Panel header="Preview data" key="1">
            <ProFormSelect
              label="stage"
              showSearch
              allowClear={false}
              fieldProps={{
                labelInValue: true,
              }}
              options={this.state.stage}
              onChange={(changeValue) => { this.getSelectContent(changeValue.value) }}
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
