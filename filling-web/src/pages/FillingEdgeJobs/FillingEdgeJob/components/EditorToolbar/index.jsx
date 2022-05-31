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
    PauseCircleFilled
} from '@ant-design/icons';
import React, { Component, useState } from 'react';
import './index.less';
import { message } from 'antd';
import { previewFillingEdgeJob, patchFillingEdgeJob, addFillingEdgeJob, startFillingEdgeJob, stopFillingEdgeJob } from '@/pages/FillingEdgeJobs/FillingEdgeJob/service';
import { history } from 'umi';
import { PreviewConfiguration } from '../PreviewConfiguration';
class EditorToolbar extends Component {
    constructor(props) {
        super(props);
        this.state = {
            jobId: props.data.id,
            nodeId: props.nodeId,
            data: props.data,
            status: (props.data.status == undefined) ? 'STOP' : props.data.status
        }
    }

    forceJobUpdate = (job) => {
        this.props.forceJobUpdate(job);
    };

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

        previewFillingEdgeJob(this.state.jobId, { data: { jobText: JSON.stringify(this.deCodeDataMap(data)), id: this.state.jobId } });
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

        entity.fillingEdgeNodes = { id: this.state.nodeId };
        try {
            if (this.state.jobId) {
                await patchFillingEdgeJob(this.state.jobId, { data: entity });
            } else {
                const job = await addFillingEdgeJob({ data: entity });

                console.log('job: {}', job);
                this.state.jobId = job.id;
                history.push("/FillingEdgeJobs/" + this.state.nodeId + "/FillingEdgeJob/" + job.id);
            }
            hide();
            message.success('保存成功');
            // 通知父组件
            this.forceJobUpdate(entity);
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
            const job = await startFillingEdgeJob(this.state.jobId);
            console.log("job", job.status);
            switch (job.status) {
                case "RUNNING":
                    hide();
                    message.success('启动成功');
                    window.jobRunStatus = true;
                    break;
                default:
                    hide();
                    message.error('启动失败, 请查看日志');
                    window.jobRunStatus = false;
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
            const job = await stopFillingEdgeJob(this.state.jobId);
            this.setState(
                {
                    status: job.status
                }
            );
            hide();
            message.success('停止成功');
            window.jobRunStatus = false;
        } else {
            message.warning('请先保存');
        }
    }

    plan = async () => {
        if (this.state.jobId) {
            const hide = message.loading('检查中');
            const status = await planFillingJobs(this.state.jobId);
            hide();
            console.log('status', status);
            if (status.errors) {
                message.error(status.errors[1]);
            } else {
                message.success('检查成功');
            }
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

            // _node['pluginOptions'] = options.pluginOptions;
            _node['PluginType'] = options.PluginType;
            _node['pluginName'] = options.pluginName;
            _node['inputLanes'] = options.inputLanes;
            _node['outputLanes'] = options.outputLanes;
            _node['data'] = options.data;
            _node['description'] = options.data['description'];
            _node['instanceName'] = options.data['name'];
            _node['stageName'] = options.stageName;
            _node['library'] = options.library;
            _node['stageVersion'] = options.stageVersion;
            // _node['data'] = options.data;
            result.nodes.push(_node);
        }
        // 排序
        result.nodes = _.orderBy(result.nodes, ['left', 'top']);
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
        if (!initialValues.confProp) {
            initialValues.confProp = '{"execution.parallelism": 1, "execution.checkpoint.interval": 1000}';
        }

        let uiInfo = this.state.data.uiInfo || {};
        console.log("this.state.data", this.state.data);

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
                <CheckCircleFilled title="检查" onClick={() => this.plan()} />

                <PlayCircleFilled title="启动" style={{ display: (this.state.status == 'RUNNING') ? 'none' : '' }} onClick={() => this.start()} />
                <PauseCircleFilled title="停止" style={{ display: (this.state.status != 'RUNNING') ? 'none' : '' }} onClick={() => this.stop()} />

                <DownloadOutlined title="下载" />
                <SelectOutlined title="另存为" />
                <PreviewConfiguration deCodeDataMap={this.deCodeDataMap} uiInfo={uiInfo} jobId={this.state.jobId} data={this.state.data} />
            </div>
        );
    }
}
export default EditorToolbar;