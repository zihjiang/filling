import React, { useState, useRef } from 'react';
import styles from './index.less';
import { Button, message, Input, Drawer } from 'antd';
import { Link } from 'react-router-dom'
import { PageContainer, FooterToolbar } from '@ant-design/pro-layout';
import ProTable, { TableDropdown } from '@ant-design/pro-table';


import { fillingEdgeNodes, removeFillingEdgeNode } from '@/pages/FillingEdgeJobs/service';

const handleRemove = async (selectedRow) => {
  const hide = message.loading('正在删除');
  if (!selectedRow) return true;

  try {

    console.log(selectedRow);
    await removeFillingEdgeNode(selectedRow.id);
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

export default () => {

  const [showDetail, setShowDetail] = useState(false);
  const actionRef = useRef();
  const [currentRow, setCurrentRow] = useState();
  const [selectedRowsState, setSelectedRows] = useState([]);


  const columns = [
    {
      title: '节点名称',
      dataIndex: 'name',
      ellipsis: true,
      tip: '所在节点',
      render: (dom, entity) => {
        return (
          <Link to={"/FillingEdgeJobs/FillingEdgeJob/" + entity.id} >
            {dom}
          </Link>
        );
      },
    },
    {
      title: '操作类型',
      dataIndex: 'goGoOS'
    },
    {
      title: '架构',
      dataIndex: 'goGoArch'
    },
    {
      title: '编译版本',
      dataIndex: 'goGoVersion'
    },
    {
      title: '最后修改时间',
      dataIndex: 'lastModified',
      valueType: "dateTime"
    },
    {
      title: '说明',
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
          <Link key="id" to={"/FillingEdgeJobs/" +record.id+ "/FillingEdgeJob/"} > 管理 </Link>
        );
        // 删除
        if (record.status != 2) {
  
          result.push(
            <a key="id" onClick={() => {
              handleRemove(record);
              actionRef.current?.reloadAndRest?.();
            }}>
              删除
            </a>
          );
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
