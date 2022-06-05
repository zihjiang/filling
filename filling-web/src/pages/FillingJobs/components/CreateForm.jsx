import React from 'react';
import ProForm, {
  ModalForm,
  ProFormText,
  ProFormTextArea,
  ProFormSelect,
  ProFormDigit,
  ProFormGroup
} from '@ant-design/pro-form';
import { addFillingJobs, importFillingJob } from '../service';
import { Upload, message, Button, Tabs } from 'antd';
import { InboxOutlined } from '@ant-design/icons';
import '../index.less';

const { Dragger } = Upload;
const { TabPane } = Tabs;

const CreateForm = (props) => {
  console.log(props);
  let model = 'blank';  // 哪种模式新建 blank 空白, 

  let fromFile = {};
  let type = "";
  let initialValues = {};
  if (!initialValues.confProp) {
    initialValues.confProp = '{"execution.parallelism": 1, "execution.checkpoint.interval": 1000}';
  }
  return (
    <>
      <ModalForm
        title="新建任务"
        trigger={
          <Button type="primary"> 新建任务</Button>
        }
        modalProps={{
          onCancel: () => console.log('run'),
        }}
        onFinish={async (values) => {
          // await this.save(values);
          // const job = await addFillingJobs({ data: entity });
          switch (model) {
            case "blank":
              const job = addFillingJobs({ data: values });
              job.then(d => {
                window.location.href = '/butterfly-dag/' + d.id;
              })
              break;

            case "fromFile":
              if (fromFile.fileNames) {
                await importFillingJob({ "data": fromFile.fileNames });
                props.refresh();
              } else {
                message.info('请选择文件');
                return false;
              }
              break;
            default:
              break;
          }
          console.log(values);
          message.success('提交成功');
          return true;
        }}
        width="40%"
        initialValues={initialValues}
      >
        <Tabs
          defaultActiveKey="blank"
          destroyInactiveTabPane={'true'}
          onChange={(d) => {
            model = d;
          }
          }>
          <TabPane tab="新建空白任务" key="blank">
            <ProFormText
              width="xl"
              name="name"
              label="任务名称"
              tooltip="最长为 24 位"
              placeholder="请输入名称"
              rules={[{ required: true }]}
            />

            <ProFormGroup label="任务参数">
              <ProFormTextArea width="xl" name="confProp" label="" />
            </ProFormGroup>
            <ProFormTextArea width="xl" name="description" label="说明" />
          </TabPane>
          <TabPane tab="从模版选择" key="fromTemplate">
            Content of Tab Pane 2
          </TabPane>
          <TabPane tab="从文件导入" key="fromFile">
            <Dragger
              accept={'.json'}
              multiple={true}
              action={'/api/filling-job/upload-file'}
              withCredentials={true}
              onChange={
                (info) => {
                  fromFile.fileNames = info.fileList.map(d => d.response);
                  console.log("onchage: {}", info);
                  console.log("fromFile: {}", fromFile);
                }
              }
            >
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">单击或拖动文件到该区域进行上传</p>
              <p className="ant-upload-hint">
                支持单次或批量上传, 上传后, 点击确定才能导入成功
              </p>
            </Dragger>
          </TabPane>
        </Tabs>
      </ModalForm>
    </>
  );
};

export default CreateForm;
