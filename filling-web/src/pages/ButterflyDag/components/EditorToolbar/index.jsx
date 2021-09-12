import {
    DeleteFilled,
    UndoOutlined,
    RedoOutlined,
    LayoutFilled,
    PlayCircleFilled,
    BugFilled,
    CheckCircleFilled,
    ZoomOutOutlined,
    ZoomInOutlined,
    SaveFilled,
    DownloadOutlined,
    SelectOutlined,
    FormOutlined,
    PauseCircleFilled,
    Icon
} from '@ant-design/icons';
import React, { Component, useState } from 'react';
import './index.less';
import { Button, message } from 'antd';
import ProForm, {
    ModalForm,
    ProFormText,
    ProFormTextArea,
    ProFormSelect,
} from '@ant-design/pro-form';
import { PlusOutlined } from '@ant-design/icons';
import { startFillingJobs, stopFillingJobs, addFillingJobs, updateFillingJobs, patchFillingJobs } from '@/pages/FillingJobs/service';
import { history } from 'umi';

class EditorToolbar extends Component {
    constructor(props) {
        super(props);
        this.state = {
            jobId: props.data.id,
            data: props.data,
            status: (props.data.status == undefined) ? 1 : props.data.status
        }
        console.log(props);
    }

    componentDidMount() {

    }

    // 删除节点或线条
    deleteNodeAndEdge = () => {
        let selectNodeOrEdge = window.selectNodeOrEdge;
        if (selectNodeOrEdge != undefined && selectNodeOrEdge['id']) {
            if (selectNodeOrEdge.type == 'endpoint') {
                window.canvas.removeEdge(selectNodeOrEdge['id']);
            } else {
                window.canvas.removeNode(selectNodeOrEdge['id']);
            }
        }
    }

    // 恢复
    undo = () => {
        window.canvas.undo();
    }
    // 还原
    redo = () => {
        window.canvas.undo();
    }
    // 自动对齐
    autoLayout = () => {
        window.canvas.autoLayout('dagreLayout', {
            'rankdir': 'LR',
            'nodesep': 50,
            'ranksep': 50,
            'controlPoints': false,
        });
    }
    zoomIn = () => {
        window.canvas.zoom(canvas.getZoom() + 0.1);
    }
    zoomInOut = () => {
        window.canvas.zoom(canvas.getZoom() - 0.1);
    }

    debugMode = () => {
        const data = window.canvas.getDataMap();
        console.log(this.deCodeDataMap(data));
        console.log(JSON.stringify(this.deCodeDataMap(data)));
    }

    save = async (entity) => {
        const hide = message.loading('正在保存');
        if (entity) {
            // 修改基本信息
            console.log(entity);
            delete entity.jobText;
        } else {
            // 只保存图表
            const data = window.canvas.getDataMap();
            const jobText = JSON.stringify(this.deCodeDataMap(data));
            entity = { jobText };
        }
        try {
            if (this.state.jobId) {
                await patchFillingJobs(this.state.jobId, { data: entity });
            } else {
                const job = await addFillingJobs({ data: entity });
                this.state.jobId = job.id;
                history.push('/butterfly-dag/' + job.id);
            }
            hide();
            message.success('保存成功');
            return true;
        } catch (error) {
            hide();
            message.error('保存失败请重试！');
            return false;
        }
    }

    start = async () => {
        if (this.state.jobId) {
            const hide = message.loading('启动中');
            const job = await startFillingJobs(this.state.jobId);
            console.log("job", job.status);
            switch (job.status) {
                case "2":
                    hide();
                    message.success('启动成功');
                    break;
                default:
                    hide();
                    message.error('启动失败, 请查看flink端日志');
                    break;
            };

            this.setState(
                {
                    status: job.status
                }
            );
        } else {
            message.warning('请先保存');
        }
    }

    stop = async () => {
        if (this.state.jobId) {
            const hide = message.loading('停止中');
            const job = await stopFillingJobs(this.state.jobId);
            this.setState(
                {
                    status: job.status
                }
            );
            hide();
            message.success('停止成功');
        } else {
            message.warning('请先保存');
        }
    }

    // 把canvas对象data换成能序列化的对象
    deCodeDataMap = (dataMap) => {
        let result = {
            nodes: [],
            edges: []
        };
        const { nodes, edges } = dataMap;
        // node
        // const nodeClass = Class;
        for (let i = 0; i < nodes.length; i++) {
            const node = nodes[i];
            let _endpoints = [];
            const { id, data, top, width, left, height, options, endpoints } = node;
            for (let j = 0; j < endpoints.length; j++) {
                const endpoint = endpoints[j];
                const { id, orientation, pos } = endpoint;
                _endpoints.push({ id, orientation, pos });
            }
            const _node = { id, data, top, width, left, height };
            _node['endpoints'] = _endpoints;

            _node['pluginOptions'] = options.pluginOptions;
            _node['PluginType'] = options.PluginType;
            _node['pluginName'] = options.pluginName;
            _node['data'] = options.data;
            _node['text'] = options.text;
            // _node['data'] = options.data;
            result.nodes.push(_node);
        }
        // edges
        for (let i = 0; i < edges.length; i++) {
            const edge = edges[i];
            const { id, source, target, sourceEndpoint, targetEndpoint, sourceNode, targetNode, type } = edge;
            const _edge = { id, source, target, type };
            _edge.source = sourceEndpoint.id;
            _edge.target = targetEndpoint.id;

            _edge.sourceNode = sourceNode.id;
            _edge.targetNode = targetNode.id;
            result.edges.push(_edge);
        }

        return result;
    }

    render() {
        let initialValues = this.state.data;
        const appEdit = (<>
            <ModalForm
                title="编辑基础信息"
                trigger={
                    <FormOutlined title="编辑基础信息" />
                }
                modalProps={{
                    onCancel: () => console.log('run'),
                }}
                onFinish={async (values) => {
                    await this.save(values);
                    message.success('提交成功');
                    return true;
                }}
                initialValues={initialValues}
                width="40%"
            >
                <ProFormText
                    width="xl"
                    name="name"
                    label="任务名称"
                    tooltip="最长为 24 位"
                    placeholder="请输入名称"
                />

                <ProFormTextArea width="xl" name="description" label="说明" />
            </ModalForm>
        </>);

        return (
            <div className="main">
                <DeleteFilled onClick={this.deleteNodeAndEdge} title="删除" />
                <UndoOutlined onClick={this.undo} title="撤销" />
                <RedoOutlined onClick={this.redo} title="恢复" />
                <LayoutFilled onClick={this.autoLayout} title="自动对齐" />
                <ZoomInOutlined onClick={this.zoomIn} title="放大" />
                <ZoomOutOutlined onClick={this.zoomInOut} title="缩小" />
                <BugFilled title="调试" onClick={this.debugMode} />
                <SaveFilled title="保存" onClick={() => this.save()} />
                <CheckCircleFilled title="检查" />

                <PlayCircleFilled title="启动" style={{ display: (this.state.status == 2) ? 'none' : '' }} onClick={() => this.start()} />
                <PauseCircleFilled title="停止" style={{ display: (this.state.status != 2) ? 'none' : '' }} onClick={() => this.stop()} />

                <DownloadOutlined title="下载" />
                <SelectOutlined title="另存为" />

                {appEdit}
            </div>
        );
    }
}
export default EditorToolbar;