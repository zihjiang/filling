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

    // 点击Collapse事件
    getpreviewData = () => {
        let previewData = window.previewData || {};
        var stage = [];
        previewData.batchesOutput[0].forEach(element => {
            let obj = {};
            obj.label = element.instanceName;
            obj.value = element.instanceName;
            stage.unshift(obj);
        });
        this.setState({
            stage: stage
        });
    }

    getSelectContent = (selectStage) => {
        console.log(selectStage);
        let batchesOutput = window.previewData.batchesOutput[0] || {};
        let currentIndex = _.findIndex(batchesOutput, d => d.instanceName == selectStage);

        this.setState({
            beforeData: this.getPreviewDataForStage(batchesOutput, batchesOutput[currentIndex - 1]).output || {},
            currentData: this.getPreviewDataForStage(batchesOutput, batchesOutput[currentIndex]).output || {}
        });
        // console.log("current: ", current);
        console.log("this.preData(batchesOutput[currentIndex]): ", this.preData(batchesOutput[currentIndex]));

        console.log("getPreviewDataForStage: ", this.getPreviewDataForStage(batchesOutput, batchesOutput[currentIndex]));
        return [{}, {}];
    }

    preData = (data) => {
        let result = [];
        for(let i = 0; i< data.output[Object.keys(data.output)].length; i++) {
            const _current = data.output[Object.keys(data.output)][i];
            for (let index = 0; index < Object.keys(_current.value.value).length; index++) {
                const element = Object.keys(_current.value.value)[index];

                result.push(element);
                
            }
        }

        console.log(result);
    }



    getPreviewDataForStage = (batchData, stageInstance) => {
      let stagePreviewData = {
        input: [],
        output: [],
        errorRecords: [],
        stageErrors: [],
        newRecords: []
      };

      _.forEach(batchData, function (stageOutput) {
        if(stageOutput.instanceName === stageInstance.instanceName) {
          _.forEach(stageOutput.output, function(outputs, laneName) {
            _.forEach(outputs, function(output, index) {
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
            _.forEach(stageOutput.eventRecords, function(eventRecord) {
              eventRecord.laneName = eventLane;
              stagePreviewData.eventRecords.push(eventRecord);
            });
          }
        }

        if(stageOutput.output && stageInstance.inputLanes && stageInstance.inputLanes.length) {
          _.forEach(stageInstance.inputLanes, function(inputLane) {
            if(stageOutput.output[inputLane]) {
              _.forEach(stageOutput.output[inputLane], function(input) {
                input.laneName = inputLane;
                stagePreviewData.input.push(input);
              });
            } else if (inputLane === stageOutput.instanceName + '_EventLane')  {
              _.forEach(stageOutput.eventRecords, function(input) {
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
            <>
                <Collapse
                    defaultActiveKey={['0']}
                    expandIconPosition={'right'}
                    onChange={this.getpreviewData}
                >
                    <Panel header="Preview data" key="1">
                            <ProFormSelect
                                name="sex"
                                label="算子"
                                showSearch
                                allowClear={false}
                                fieldProps={{
                                    labelInValue: true,
                                }}
                                options={this.state.stage}
                                onChange={(changeValue) => {this.getSelectContent(changeValue.value)}}
                            />
                        <div>
                            <DiffEditor
                                value={[JSON.stringify(this.state.beforeData,null, 2), JSON.stringify(this.state.currentData,null, 2)]}
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
            </>
        );
    }
}

export { EditorDebug };
