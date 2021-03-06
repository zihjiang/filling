import { React, Component } from 'react';
import { Button, message } from 'antd';
import ProForm, {
    ModalForm,
    ProFormSelect,
    ProFormDigit,
    ProFormSwitch
} from '@ant-design/pro-form';
import { BugFilled } from '@ant-design/icons';
import $ from 'jquery';
import { previewFillingEdgeJob, patchFillingEdgeJob, addFillingEdgeJob } from '@/pages/FillingEdgeJobs/FillingEdgeJob/service';
class PreviewConfiguration extends Component {
    constructor(props) {
        super(props);
        this.state = {
            initialValues: props.uiInfo.previewConfig,
            jobId: props.jobId,
            job: props.data
        };
        this.uiInfo = props.uiInfo ? {} : JSON.parse(props.uiInfo);
        this.deCodeDataMap = props.deCodeDataMap;
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