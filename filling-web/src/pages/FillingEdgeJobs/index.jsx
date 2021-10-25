import React, { useState, useRef } from 'react';
import styles from './index.less';
import { Button, message, Input, Drawer } from 'antd';
import { Link } from 'react-router-dom'
import { PageContainer, FooterToolbar } from '@ant-design/pro-layout';
import ProTable, { TableDropdown } from '@ant-design/pro-table';


import { fillingEdgeNodes } from '@/pages/FillingEdgeJobs/service';

const columns = [
  {
    title: '任务名称',
    dataIndex: 'name',
    ellipsis: true,
    tip: '也是flink的任务名称',
    render: (dom, entity) => {
      return (
        <Link to={"/FillingEdgeJobs/FillingEdgeJob/" + entity.id} >
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
        <Link key="id" to={"/FillingEdgeJobs/FillingEdgeJob/" + record.id} > 修改 </Link>
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
const data = [
  {
    key: '1',
    name: 'John Brown',
    age: 32,
    address: 'New York No. 1 Lake Park',
    tags: ['nice', 'developer'],
  },
  {
    key: '2',
    name: 'Jim Green',
    age: 42,
    address: 'London No. 1 Lake Park',
    tags: ['loser'],
  },
  {
    key: '3',
    name: 'Joe Black',
    age: 32,
    address: 'Sidney No. 1 Lake Park',
    tags: ['cool', 'teacher'],
  },
];
export default () => {

  const [showDetail, setShowDetail] = useState(false);
  const actionRef = useRef();
  const [currentRow, setCurrentRow] = useState();
  const [selectedRowsState, setSelectedRows] = useState([]);
  return (
    <PageContainer>
      <ProTable
        headerTitle="节点列表"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Link to={"/FillingEdgeJobs/FillingEdgeJob/"} >
            新建任务
          </Link>,
        ]}
        request={fillingEdgeNodes}
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
  )
};
