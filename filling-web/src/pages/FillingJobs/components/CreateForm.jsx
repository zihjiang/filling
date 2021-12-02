import React from 'react';
import { ProFormText, ProFormTextArea, ProFormGroup } from '@ant-design/pro-form';
import { previewFillingEdgeJob, patchFillingEdgeJob, addFillingEdgeJob, startFillingEdgeJob, stopFillingEdgeJob } from '@/pages/FillingEdgeJobs/FillingEdgeJob/service';
const CreateForm = (props) => {
  return (
    <>
      <ProFormText
        width="xl"
        name="name"
        label="任务名称"
        tooltip="最长为 24 位"
        placeholder="请输入名称"
      />
      <ProFormGroup label="任务参数">
        <ProFormTextArea width="xl" name="confProp" label="" />
      </ProFormGroup>
      <ProFormTextArea width="xl" name="description" label="说明" />
    </>
  );
};

export default CreateForm;
