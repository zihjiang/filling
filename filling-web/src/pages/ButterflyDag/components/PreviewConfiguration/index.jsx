import { React, Component } from 'react';
import { Button, message, Form, Input, Row, Col } from 'antd';
import ProForm, {
    ModalForm,
    ProFormSelect,
    ProFormDigit,
    ProFormSwitch
} from '@ant-design/pro-form';
import { BugFilled } from '@ant-design/icons';
import $ from 'jquery';
import { previewFillingEdgeJob, patchFillingEdgeJob, addFillingEdgeJob } from '@/pages/FillingEdgeJobs/FillingEdgeJob/service';

import AceEditor from "react-ace";
import 'ace-builds/src-noconflict/mode-json';
import "ace-builds/src-noconflict/theme-terminal";

class PreviewConfiguration extends Component {
    constructor(props) {
        super(props);
        this.state = {
            initialValues: props.uiInfo.previewConfig,
            jobId: props.jobId,
            job: props.data
        };
        this.uiInfo = props.uiInfo ? {} : JSON.parse(props.uiInfo);
        this.state.deCodeDataMap = props.deCodeDataMap;
        console.log("prop: ", props);

    }

    // 更新node的数据
    updateData = async (values) => {
        console.log(this.uiInfo);

        this.uiInfo.previewConfig = values;
        this.uiInfo.displayMode = "ADVANCED";
        const data = window.canvas.getDataMap();
        this.state.job.uiInfo = JSON.stringify(this.uiInfo);
        this.state.job.jobText = JSON.stringify(this.deCodeDataMap(data));
        const previewData = await previewFillingEdgeJob(this.state.jobId, {
            data: this.state.job
        });
        window.previewData = previewData;
        console.log("previewData: ", previewData);

        $("#EditorDebug span").click();
    }

    render() {
        let initialValues = this.state.initialValues == undefined ? { testOrigin: true, batchSize: 10, skipTargets: true, rememberConfig: true, skipLifecycleEvents: true, timeout: 30000 } : this.state.initialValues;

        const waitTime = (time = 100) => {
            return new Promise((resolve) => {
                setTimeout(() => {
                    resolve(true);
                }, time);
            });
        };
        return (
            <ModalForm
                title="调试配置"
                trigger={
                    <BugFilled title="调试" />
                }
                initialValues={initialValues}
                modalProps={{
                    onCancel: () => console.log('run'),
                }}
                onFinish={async (values) => {
                    await waitTime(10);
                    console.log(values);
                    const data = window.canvas.getDataMap();
                    console.log(this.deCodeDataMap(data));
                    await this.updateData(values);
                    message.success('提交成功');
                    return true;
                }}
                width='40%'
                onVisibleChange={function (e) {
                    const data = window.canvas.getDataMap();
                    // this.state.job.jobText = JSON.stringify(this.deCodeDataMap(data));
                    // console.log(this.state.deCodeDataMap);
                    // this.state.sourceOptions = this.state.deCodeDataMap(data).jobText.nodes.filter(_d => _d).map(c => {return {'name': c.data.plugin_name}})
                    console.log('---------------', this.state);
                }}
                submitter={{
                    // 配置按钮文本
                    searchConfig: {
                        resetText: '取消',
                        submitText: '运行',
                    }
                }}
            >
                <ProForm.Group>
                    <ProFormSelect
                        width="xl"
                        name="testOrigin"
                        label="Preview Source"
                        tooltip="Preview Source"
                        placeholder="Preview Source"
                        options={this.state.sourceOptions}
                    />

                </ProForm.Group>

                <ProForm.Group>

                    <Form.Item
                        key={'idx'}
                        name={'item.name'}
                        label={'示例数据'}
                        tooltip={'item.paramsDesc'}
                        placeholder={'item.paramsDesc'}
                        defaultValue={'item.defaultValue'}
                        valuePropName="value">
                        <AceEditor
                            placeholder={'item.description'}
                            mode={'json'}
                            name="data"
                            theme="terminal"
                            fontSize={12}
                            height={'200px'}
                            showPrintMargin={true}
                            showGutter={true}
                            highlightActiveLine={true}
                            editorProps={{ $blockScrolling: false }}
                            setOptions={{
                                enableBasicAutocompletion: true,
                                enableLiveAutocompletion: true,
                                enableSnippets: true,
                                showLineNumbers: true,
                                tabSize: 2,
                            }} />
                    </Form.Item>
                </ProForm.Group>

                <div>
                    <Row>
                        <Col span={8} offset={16}>
                            <Button type="link" block>
                                尝试获取数据
                            </Button>
                        </Col>
                    </Row>
                </div>

                <ProForm.Group>
                    <ProFormSelect
                        width="xl"
                        name="testOrigin"
                        label="Preview Source"
                        tooltip="Preview Source"
                        placeholder="Preview Source"
                        options={[
                            { label: 'kafka-source', value: 'kafka-source' }
                        ]}
                    />

                </ProForm.Group>
            </ModalForm>
        );
    }
}


export { PreviewConfiguration };