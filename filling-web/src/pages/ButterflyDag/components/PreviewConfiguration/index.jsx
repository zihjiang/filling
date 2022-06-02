import { React, Component, useState } from 'react';
import { message, Form, Spin, notification } from 'antd';
import ProForm, {
    ModalForm,
    ProFormSelect,
    ProFormDigit,
    ProFormSwitch
} from '@ant-design/pro-form';
import { BugFilled, SmileOutlined, WarningOutlined } from '@ant-design/icons';
import $ from 'jquery';
import { debugFillingJob, debugFillingSourceData } from '@/pages/FillingJobs/service';


import AceEditor from "react-ace";
import 'ace-builds/src-noconflict/mode-json';
import "ace-builds/src-noconflict/theme-terminal";

const PreviewConfiguration = (e) => {

    const [modalVisit, setModalVisit] = useState(false);
    const [spinVisit, setSpinVisit] = useState(false);

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

        if (selectSource[0].data.schema && selectSource[0].data.schema.startsWith("{")) {
            form.setFieldsValue({
                schema: JSON.stringify(JSON.parse(selectSource[0].data.schema), null, 2)
            });
        }
        console.log("selectSource: ", selectSource);

    }

    // 从数据源获取数据
    const getSourceData = async () => {

        const values = form.getFieldValue("result_table_name");
        const data = deCodeDataMap(window.canvas.getDataMap());
        selectSource = data.nodes.filter(_d => _d.id == values);

        if (selectSource && selectSource[0]) {
            setSpinVisit(true);
            const data = selectSource[0].data;
            const sourceData = await debugFillingSourceData({ "data": data });
            setSpinVisit(false);

            form.setFieldsValue({
                schema: JSON.stringify(sourceData, null, 2)
            });

        }
    }

    const submit = async (changeData) => {

        const data = window.canvas.getDataMap();
        const jobText = JSON.parse(JSON.stringify(deCodeDataMap(data)));



        jobText.nodes.map(d => {
            if (['KafkaTableStream', 'dataGenSource'].includes(d.data['plugin_name']) && d.data.result_table_name == changeData.result_table_name.replaceAll('-', '_')) {
                d.data.schema = changeData.schema;
                d.data['plugin_name'] = 'CustomDataSource';
            }
        });
        fillingJob.jobText = JSON.stringify(jobText);

        console.log("fillingJob", fillingJob);

        const debugInfo = await debugFillingJob({ data: fillingJob });

        console.log("job", debugInfo);
        setSpinVisit(false);
        setModalVisit(false);

        window.debugInfo = debugInfo;
        window.deCodeDataMap = deCodeDataMap;
        if (debugInfo.status) {
            notification.open({
                message: '调试成功',
                description:
                    '可以正常运行, 并预览数据',
                icon: <SmileOutlined style={{ color: '#108ee9' }} />,
                onClick: () => {
                    console.log('Notification Clicked!');
                },
            });
        } else {
            notification.open({
                message: '调试失败',
                description:
                    '调试失败, 具体请查看日志',
                icon: <WarningOutlined style={{ color: '#108ee9' }} />,
                onClick: () => {
                    console.log('Notification Clicked!');
                },
            });
        }
        $("#EditorDebug span").trigger("click");


    }

    let initialValues = {};
    return (
        <>
            <ModalForm
                title="调试配置"
                visible={modalVisit}
                form={form}
                trigger={
                    <BugFilled title="调试" onClick={setModalVisit} />
                }
                initialValues={initialValues}
                modalProps={{
                    onCancel: () => { setModalVisit(false); },
                }}
                onFinish={async (values) => {
                    submit(values);
                    setSpinVisit(true);
                    return true;
                }}

                width='60%'
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
                <Spin spinning={spinVisit} delay={100}>
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
                            addonAfter={<a onClick={getSourceData}>尝试获取样例数据</a>}
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
                                width={window.screen.width * 0.55 + 'px'}
                                showPrintMargin={true}
                                showGutter={true}
                                highlightActiveLine={true}
                                editorProps={{ $blockScrolling: true }}
                                setOptions={{
                                    tabSize: 2
                                }} />
                        </Form.Item>
                    </ProForm.Group>
                </Spin>
            </ModalForm>
        </>
    );
}


export { PreviewConfiguration };