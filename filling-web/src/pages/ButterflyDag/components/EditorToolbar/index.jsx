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
    BarChartOutlined
} from '@ant-design/icons';
import React, { Component, useState } from 'react';
import './index.less';
import { Button, message } from 'antd';
import ProForm, {
    ModalForm,
    ProFormText,
    ProFormTextArea,
    ProFormSelect,
    ProFormDigit,
    ProFormGroup
} from '@ant-design/pro-form';
import { planFillingJobs, startFillingJobs, stopFillingJobs, addFillingJobs, updateFillingJobs, patchFillingJobs, exportFillingJob } from '@/pages/FillingJobs/service';
import { PreviewConfiguration } from '../PreviewConfiguration';
import { history } from 'umi';
class EditorToolbar extends Component {
    constructor(props) {
        super(props);
        this.state = {
            jobId: props.data.id,
            data: props.data,
            status: (props.data.status == undefined) ? 1 : props.data.status
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
    }

    save = async (entity) => {
        const hide = message.loading('正在保存');
        if (entity) {
            // 修改基本信息
            this.setState({ data: entity });
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
        if (this.state.data.name == undefined) {
            message.info('任务名称不能为空');
            return;
        }
        if (this.state.jobId) {
            const hide = message.loading('启动中');
            const job = await startFillingJobs(this.state.jobId);
            console.log("job", job.status);
            switch (job.status) {
                case "2":
                    hide();
                    message.success('启动成功');
                    window.jobRunStatus = true;
                    break;
                default:
                    hide();
                    message.error('启动失败, 请查看flink端日志');
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
            const job = await stopFillingJobs(this.state.jobId);
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

    overview = async () => {
        if (this.state.jobId) {
            window.open(`/api/filling-jobs/overview/${this.state.jobId}`);
        }
    }



    download = (data, strFileName, strMimeType) =>  {
	
        let self = window, // this script is only for browsers anyway...
            u = "application/octet-stream", // this default mime also triggers iframe downloads
            m = strMimeType || u, 
            x = data,
            D = document,
            a = D.createElement("a"),
            z = function(a){return String(a);},
            
            
            B = self.Blob || self.MozBlob || self.WebKitBlob || z,
            BB = self.MSBlobBuilder || self.WebKitBlobBuilder || self.BlobBuilder,
            fn = strFileName || "download",
            blob, 
            b,
            ua,
            fr;
    
        //if(typeof B.bind === 'function' ){ B=B.bind(self); }
        
        if(String(this)==="true"){ //reverse arguments, allowing download.bind(true, "text/xml", "export.xml") to act as a callback
            x=[x, m];
            m=x[0];
            x=x[1]; 
        }
        
        
        
        //go ahead and download dataURLs right away
        if(String(x).match(/^data\:[\w+\-]+\/[\w+\-]+[,;]/)){
            return navigator.msSaveBlob ?  // IE10 can't do a[download], only Blobs:
                navigator.msSaveBlob(d2b(x), fn) : 
                saver(x) ; // everyone else can save dataURLs un-processed
        }//end if dataURL passed?
        
        try{
        
            blob = x instanceof B ? 
                x : 
                new B([x], {type: m}) ;
        }catch(y){
            if(BB){
                b = new BB();
                b.append([x]);
                blob = b.getBlob(m); // the blob
            }
            
        }
        
        
        
        function d2b(u) {
            var p= u.split(/[:;,]/),
            t= p[1],
            dec= p[2] == "base64" ? atob : decodeURIComponent,
            bin= dec(p.pop()),
            mx= bin.length,
            i= 0,
            uia= new Uint8Array(mx);
    
            for(i;i<mx;++i) uia[i]= bin.charCodeAt(i);
    
            return new B([uia], {type: t});
         }
          
        function saver(url, winMode){
            
            
            if ('download' in a) { //html5 A[download] 			
                a.href = url;
                a.setAttribute("download", fn);
                a.innerHTML = "downloading...";
                D.body.appendChild(a);
                setTimeout(function() {
                    a.click();
                    D.body.removeChild(a);
                    if(winMode===true){setTimeout(function(){ self.URL.revokeObjectURL(a.href);}, 250 );}
                }, 66);
                return true;
            }
            
            //do iframe dataURL download (old ch+FF):
            var f = D.createElement("iframe");
            D.body.appendChild(f);
            if(!winMode){ // force a mime that will download:
                url="data:"+url.replace(/^data:([\w\/\-\+]+)/, u);
            }
             
        
            f.src = url;
            setTimeout(function(){ D.body.removeChild(f); }, 333);
            
        }//end saver 
            
    
        if (navigator.msSaveBlob) { // IE10+ : (has Blob, but not a[download] or URL)
            return navigator.msSaveBlob(blob, fn);
        } 	
        
        if(self.URL){ // simple fast and modern way using Blob and URL:
            saver(self.URL.createObjectURL(blob), true);
        }else{
            // handle non-Blob()+non-URL browsers:
            if(typeof blob === "string" || blob.constructor===z ){
                try{
                    return saver( "data:" +  m   + ";base64,"  +  self.btoa(blob)  ); 
                }catch(y){
                    return saver( "data:" +  m   + "," + encodeURIComponent(blob)  ); 
                }
            }
            
            // Blob but not URL:
            fr=new FileReader();
            fr.onload=function(e){
                saver(this.result); 
            };
            fr.readAsDataURL(blob);
        }	
        return true;
    }

    export = async () => {
        if (this.state.jobId) {
            exportFillingJob(this.state.jobId).then((d) => {
                this.download(JSON.stringify(d), d.name + ".json");
            })
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
            _node['data'] = options.data;
            _node['text'] = options.text;
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
            initialValues.confProp = '{"execution.parallelism": 2}';
        }
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

                <ProFormGroup label="任务参数">
                    <ProFormTextArea width="xl" name="confProp" label="" />
                </ProFormGroup>



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
                <PreviewConfiguration  data={this.state.data} deCodeDataMap={this.deCodeDataMap} />
                <SaveFilled title="保存" onClick={() => this.save()} />
                <CheckCircleFilled title="检查" onClick={() => this.plan()} />

                <PlayCircleFilled title="启动" style={{ display: (this.state.status == 2) ? 'none' : '' }} onClick={() => this.start()} />
                <PauseCircleFilled title="停止" style={{ display: (this.state.status != 2) ? 'none' : '' }} onClick={() => this.stop()} />
                <BarChartOutlined title="监控" style={{ display: (this.state.status != 2) ? 'none' : '' }} onClick={() => this.overview()} />
                <DownloadOutlined title="下载" onClick={() => this.export()} />
                <SelectOutlined title="另存为" />
                {appEdit} 
            </div>
        );
    }
}
export default EditorToolbar;