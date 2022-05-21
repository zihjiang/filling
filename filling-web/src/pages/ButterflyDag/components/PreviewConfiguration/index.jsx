import { React, Component, useState } from 'react';
import { Button, message, Form, Input, Row, Col } from 'antd';
import ProForm, {
    ModalForm,
    ProFormSelect,
    ProFormDigit,
    ProFormSwitch
} from '@ant-design/pro-form';
import { BugFilled } from '@ant-design/icons';
import $ from 'jquery';
import { debugFillingJob } from '@/pages/FillingJobs/service';


import AceEditor from "react-ace";
import 'ace-builds/src-noconflict/mode-json';
import "ace-builds/src-noconflict/theme-terminal";

const PreviewConfiguration = (e) => {

    const [modalVisit, setModalVisit] = useState(false);

    const deCodeDataMap = e.deCodeDataMap;

    const [form] = Form.useForm();

    let sourceOptions = [];

    let fillingJob = e.data;

    // 选中的源
    let selectSource;

    // 更新testOrigin的数据
    const changeTestOrigin = (values) => {

        const data = deCodeDataMap(window.canvas.getDataMap());
        selectSource = data.nodes.filter(_d => _d.id == values);

        form.setFieldsValue({
            schema: selectSource[0].data.schema
        });
        console.log("selectSource: ", selectSource);

        // $("#EditorDebug span").click();
    }

    const  submit = async (changeData) => {

        const data = window.canvas.getDataMap();
        const jobText = JSON.parse(JSON.stringify(deCodeDataMap(data)));



        jobText.nodes.map(d => {
            if (d.data.result_table_name == changeData.result_table_name.replaceAll('-', '_')) {
                d.data.schema = changeData.schema;
                d.data['plugin_name'] = 'CustomDataSource';
            }
        });
        fillingJob.jobText = JSON.stringify(jobText);

        console.log("fillingJob", fillingJob);

        const job = await debugFillingJob({ data: fillingJob});

        console.log("job", job);
        

    }

    let initialValues = {};
    return (
        <ModalForm
            title="调试配置"
            visible={modalVisit}
            form={form}
            trigger={
                <BugFilled title="调试" onClick={setModalVisit} />
            }
            initialValues={initialValues}
            modalProps={{
                onCancel: () => { setModalVisit(false) },
            }}
            onFinish={async (values) => {
                console.log(values);
                submit(values);
                console.log("submit");
                message.success('提交成功');
                setModalVisit(false)
                return true;
            }}

            width='40%'
            submitter={{
                // 配置按钮文本
                searchConfig: {
                    resetText: '取消',
                    submitText: '运行',
                }
            }}
            drawerprops={{
                forceRender: true,
                destroyOnClose: true
            }}
        >
            <ProForm.Group>
                <ProFormSelect
                    width="md"
                    name="result_table_name"
                    label="Preview Source"
                    tooltip="Preview Source"
                    placeholder="Preview Source"
                    // options={sourceOptions}
                    options={
                        window.canvas == undefined ? sourceOptions : deCodeDataMap(window.canvas.getDataMap()).nodes.filter(_d => _d.PluginType == 'source').map(c => { return { 'label': c.data.name, 'value': c.id } })
                    }
                    onChange={changeTestOrigin}
                    addonAfter={<a>尝试获取样例数据</a>}
                />

            </ProForm.Group>

            <ProForm.Group>
                <Form.Item
                    name='schema'
                    label={'样例数据'}
                    tooltip={'item.paramsDesc'}
                    placeholder={'item.paramsDesc'}
                    valuePropName="value">
                    <AceEditor
                        placeholder={'item.description'}
                        mode={'json'}
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

        </ModalForm>
    );
}


export { PreviewConfiguration };