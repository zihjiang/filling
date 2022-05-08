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

import AceEditor from "react-ace";
import 'ace-builds/src-noconflict/mode-json';
import "ace-builds/src-noconflict/theme-terminal";

const PreviewConfiguration = (e) => {

    const deCodeDataMap = e.deCodeDataMap;

    const [form] = Form.useForm();

    let sourceOptions = [];

    // 更新testOrigin的数据
    const changeTestOrigin = (values) => {

        const data = deCodeDataMap(window.canvas.getDataMap());
        const selectSource = data.nodes.filter(_d => _d.id == values);

        form.setFieldsValue({
            schema: selectSource[0].data.schema
        });
        // console.log("previewData: ", previewData);

        // $("#EditorDebug span").click();
    }

    // 设置下拉框数据
    const setSourceOptions = () => {

        const data = deCodeDataMap(window.canvas.getDataMap());
        sourceOptions = data.nodes.filter(_d => _d.PluginType == 'source').map(c => { return { 'label': c.data.name, 'value': c.id } });

        console.log(form.getFieldValue);
        console.log(form.getFieldValue('testOrigin'));

    }


    let initialValues = {};

    console.log(initialValues);



    return (
        <ModalForm
            title="调试配置"
            form={form}
            trigger={
                <BugFilled title="调试" onClick={setSourceOptions} />
            }
            initialValues={initialValues}
            modalProps={{
                onCancel: () => console.log('run'),
            }}
            onFinish={async (values) => {
                // console.log(values);
                const data = window.canvas.getDataMap();
                console.log(data);
                // console.log(this.deCodeDataMap(data));
                console.log(this.updateData);
                await this.updateData(values);
                message.success('提交成功');
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
                    name="testOrigin"
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
                    label={'示例数据'}
                    tooltip={'item.paramsDesc'}
                    placeholder={'item.paramsDesc'}
                    shouldUpdate={(prevValues, currentValues) => console.log(prevValues, currentValues)}
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