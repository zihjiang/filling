import React from 'react';
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

const waitTime = (time = 100) => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve(true);
        }, time);
    });
};

export default () => {
    return (
        <ModalForm
            title="调试配置"
            trigger={
                <BugFilled title="调试" />

            }
            modalProps={{
                onCancel: () => console.log('run'),
            }}
            onFinish={async (values) => {
                await waitTime(2000);
                console.log(values.name);
                message.success('提交成功');
                return true;
            }}
            width='35%'
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
                <ProFormSwitch width="xl" name="skipTargets" label="Write to Destinations and Executors" />
            </ProForm.Group>


            <ProForm.Group>
                <ProFormSwitch width="xl" name="rememberConfig" label="Remember the Configuration" />
            </ProForm.Group>
            <ProFormSwitch width="xl" name="skipLifecycleEvents" label="Execute Pipeline Lifecycle Events" />

        </ModalForm>
    );
};