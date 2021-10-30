import { React, Component } from 'react';
import { Button, message } from 'antd';
import ProForm, {
    ModalForm,
    ProFormText,
    ProFormDateRangePicker,
    ProFormSelect,
    ProFormDigit,
    ProFormSwitch
} from '@ant-design/pro-form';
import { BugFilled } from '@ant-design/icons';
import { previewFillingEdgeJob, patchFillingEdgeJob, addFillingEdgeJob } from '@/pages/FillingEdgeJobs/FillingEdgeJob/service';
class PreviewConfiguration extends Component {
    constructor(props) {
        super(props);
        this.state = {
            initialValues: props.uiInfo.previewConfig
        };
        this.uiInfo = props.uiInfo;
        this.deCodeDataMap = props.deCodeDataMap;
    }

    // 更新node的数据
    updateData = async (values) => {
        console.log(123123);

        this.uiInfo.previewConfig = values;
        this.uiInfo.displayMode = "ADVANCED";
        const data = window.canvas.getDataMap();
        await previewFillingEdgeJob(1, {
            data: {
                uiInfo: JSON.stringify(this.uiInfo),
                jobText: JSON.stringify(this.deCodeDataMap(data)),
                id: 1
            }
        });
        console.log(this.uiInfo);
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
                width='35%'
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
                        options={[
                            { label: 'Configured Origin', value: true },
                            { label: 'Test Origin', value: false }
                        ]}
                    />
                </ProForm.Group>

                <ProForm.Group>
                    <ProFormDigit width="xl" name="batchSize" label="Preview Batch Size" placeholder="Preview Batch Size" />
                </ProForm.Group>

                <ProForm.Group>
                    <ProFormDigit width="xl" name="timeout" label="预览超时" placeholder="预览超时" />
                </ProForm.Group>

                <ProForm.Group>
                    <ProFormSwitch width="xl" name="skipTargets" label="Write to Destinations and Executors" />
                </ProForm.Group>


                <ProForm.Group>
                    <ProFormSwitch width="xl" name="rememberConfig" label="Remember the Configuration" />
                </ProForm.Group>
                <ProFormSwitch width="xl" name="skipLifecycleEvents" label="Execute Pipeline Lifecycle Events" />

            </ModalForm>
        );
    }
}


export { PreviewConfiguration };