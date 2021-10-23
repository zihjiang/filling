import { PlusOutlined } from '@ant-design/icons';
import { Button, message, Input, Drawer } from 'antd';
import React, { useState, useRef } from 'react';
import { PageContainer, FooterToolbar } from '@ant-design/pro-layout';
import ProTable, { TableDropdown } from '@ant-design/pro-table';
import { ModalForm, ProFormText, ProFormTextArea } from '@ant-design/pro-form';
import ProDescriptions from '@ant-design/pro-descriptions';
import UpdateForm from './components/UpdateForm';
import { fillingJobs, addFillingJobs, updateFillingJobs, removeFillingJobs } from './service';
import { Link } from 'react-router-dom';
import { startFillingJobs, stopFillingJobs, patchFillingJobs } from '@/pages/FillingJobs/service';


const handleRemove = async (selectedRow) => {
  const hide = message.loading('正在删除');
  if (!selectedRow) return true;

  try {

    console.log(selectedRow);
    await removeFillingJobs(selectedRow.id);
    hide();
    message.success('删除成功，即将刷新');
    return true;
  } catch (error) {
    console.log(error);
    hide();
    message.error('删除失败，请重试');
    return false;
  }
};

const handleMonitor = async (selectedRow) => {

  window.open(`/api/filling-jobs/overview/${selectedRow.id}`);
}

// 启动任务
const start = async (record) => {
  if (record.name == undefined) {
    message.info('任务名称不能为空');
    return;
  }
  if (record.id) {
    const hide = message.loading('启动中');
    const job = await startFillingJobs(record.id);
    console.log("job", job.status);
    switch (job.status) {
      case "2":
        hide();
        message.success('启动成功');
        break;
      default:
        hide();
        message.error('启动失败, 请查看flink端日志');
        window.jobRunStatus = false;
        break;
    };
  } else {
    message.warning('请先保存');
  }
}
// 停止任务
const stop = async (record) => {
  if (record.id) {
    const hide = message.loading('停止中');
    const job = await stopFillingJobs(record.id);
    hide();
    message.success('停止成功');
  } else {
    message.warning('未知');
  }
}

const TableList = () => {
  /** 新建窗口的弹窗 */
  const [createModalVisible, handleModalVisible] = useState(false);
  /** 分布更新窗口的弹窗 */

  const [showDetail, setShowDetail] = useState(false);
  const actionRef = useRef();
  const [currentRow, setCurrentRow] = useState();
  const [selectedRowsState, setSelectedRows] = useState([]);
  /** 国际化配置 */

  const columns = [
    {
      title: '任务名称',
      dataIndex: 'name',
      ellipsis: true,
      tip: '也是flink的任务名称',
      render: (dom, entity) => {
        return (
          <Link to={"/butterfly-dag/" + entity.id} >
            {dom}
          </Link>
        );
      },
    },
    {
      title: '任务类型',
      dataIndex: 'type'
    },
    {
      title: '状态',
      dataIndex: 'status',
      hideInForm: true,
      valueEnum: {
        1: {
          text: '未运行',
          status: 'Created',
        },
        2: {
          text: '运行中',
          status: 'Processing',
        },
        3: {
          text: '完成',
          status: 'Success',
        },
        4: {
          text: '失败',
          status: 'Error',
        },
        5: {
          text: '停止',
          status: 'normal',
        },
        6: {
          text: '失败',
          status: 'Error',
        },
        7: {
          text: '取消中',
          status: 'Canceling',
        },
        8: {
          text: '重启中',
          status: 'Restarting',
        }
      },
    },
    {
      title: '最后修改时间',
      dataIndex: 'updatetime',
      valueType: "dateTime"
    },
    {
      title: 'description',
      sorter: true,
      dataIndex: 'description',
      valueType: 'textarea',
      ellipsis: true
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => {
        let result = [];
        // 修改
        result.push(
          <Link key="id" to={"/butterfly-dag/" + record.id} > 修改 </Link>
        );
        // 删除
        if (record.status != 2) {

          result.push(
            <a key="id" onClick={() => {
              handleRemove(record);
              setSelectedRows([]);
              actionRef.current?.reloadAndRest?.();
            }}>
              删除
            </a>
          );
          result.push(
            <a key="id" onClick={() => {
              start(record).then(() => {
                setSelectedRows([]);
                actionRef.current?.reloadAndRest?.();
              })
            }}>
              启动
            </a>
          );
        }
        // 监控
        if (record.status == 2) {
          result.push(
            <a key="id" onClick={() => {
              handleMonitor(record);
              setSelectedRows([]);
            }}>
              监控
            </a>
          );
          result.push(
            <a key="id" onClick={() => {
              stop(record).then(() => {
                setSelectedRows([]);
                actionRef.current?.reloadAndRest?.();
              });

            }}>
              停止
            </a>
          )
        }

        result.push(
          <TableDropdown
            key="actionGroup"
            onSelect={() => action?.reload()}
            menus={[
              { key: 'copy', name: '复制' },
              { key: 'delete', name: '删除' },
            ]}
          />
        )
        return result;
      }
    },
  ];
  return (
    <PageContainer>
      <ProTable
        headerTitle="任务列表"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Link to={"/butterfly-dag/"} >
            新建任务
          </Link>,
        ]}
        request={fillingJobs}
        columns={columns}
        rowSelection={{
          onChange: (_, selectedRows) => {
            setSelectedRows(selectedRows);
          },
        }}
      />
      {selectedRowsState?.length > 0 && (
        <FooterToolbar
          extra={
            <div>
              已选择{' '}
              <a
                style={{
                  fontWeight: 600,
                }}
              >
                {selectedRowsState.length}
              </a>{' '}
              项 &nbsp;&nbsp;
              <span>
                服务调用次数总计 {selectedRowsState.reduce((pre, item) => pre + item.callNo, 0)} 万
              </span>
            </div>
          }
        >
          <Button
            onClick={async () => {
              await handleRemove(selectedRowsState);
              setSelectedRows([]);
              actionRef.current?.reloadAndRest?.();
            }}
          >
            批量删除
          </Button>
          <Button type="primary">批量审批</Button>
        </FooterToolbar>
      )}


      <Drawer
        width={600}
        visible={showDetail}
        onClose={() => {
          setCurrentRow(undefined);
          setShowDetail(false);
        }}
        closable={false}
      >
        {currentRow?.name && (
          <ProDescriptions
            column={2}
            title={currentRow?.name}
            request={async () => ({
              data: currentRow || {},
            })}
            params={{
              id: currentRow?.name,
            }}
            columns={columns}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default TableList;
