import { Col, Row, Alert } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import styles from './index.less';
import { EditorGraph } from './components/EditorGraph/EditorGraph';

import EditorPanel from './components/EditorPanel/index';
import EditorToolbar from './components/EditorToolbar';
import { Component } from 'react';
import BaseNode from './components/EditorGraph/node';
import { fillingJob } from '../FillingJobs/service';
import { Spin } from 'antd';
import rightTools from './components/EditorPanel/data';
import { EditorDebug } from './components/EditorDebug/index';
import $ from 'jquery';
import {
  CheckCircleOutlined,
  InfoCircleOutlined
} from '@ant-design/icons';
class EditorFlow extends Component {
  constructor(props) {
    super(props);
    this.state = {
      id: this.props.match.params.id,
      data: {}
    };
  }

  getJob = () => {
    return fillingJob(this.state.id);
  }

  componentDidMount() {
    if (this.state.id) {
      console.log("编辑任务");
      this.getJob().then((job) => {
        this.setState({
          data: job
        })
      })
    } else {
      console.log("新建任务");
      const jobText = "{}";
      this.setState({
        data: { jobText }
      })
    }
  }

  forceJobUpdate(job) {
    console.log('forceJobUpdate,', job);
    if (job.description) {
      console.log(job.description)
      // this.setState({
      //   data: {  }
      // })
      // TODO 暂时不能做到实时更新任务名称和说明
      console.log(this.state);
    }

  }

  render() {
    // 右上角状态
    if (window.intervalDagId) {
      clearInterval(window.intervalDagId);
    }

    window.intervalDagId = setInterval(() => {
      try {
        const nodes = canvas.unknownNode();
        if (/d+/.test(document.location.href)) {
          if (canvas.unknownNode().length > 0) {

            document.getElementById('dagStatusErrur').style.display = "";
            document.getElementById('dagStatusNormal').style.display = "none";

            document.getElementById('dagStatusErrorlist').innerHTML = nodes.map(d => d.options.data?.name || d.options.text).join(',');
            console.log(nodes);
          } else {
            document.getElementById('dagStatusErrur').style.display = "none";
            document.getElementById('dagStatusNormal').style.display = "";
          }
        } else {
          clearInterval(window.intervalDagId);
          console.log("取消定时任务");
        }
      } catch (error) {
        console.error(error);
        clearInterval(window.intervalDagId);
      }
    }, 2000);


    if (_.isEqual(this.state.data, {})) return (<Spin />);
    const data = JSON.parse(this.state.data.jobText);
    // 更改全局状态
    window.jobRunStatus = this.state.data.status == 2 ? true : false;
    if (data.nodes) {
      data.nodes.map(d => {
        const node = rightTools.find(_d => _d.pluginName == d.pluginName) || { pluginOptions: "[]" };
        d.pluginOptions = JSON.stringify(node.pluginOptions);
        d.content = node.content;
        if (!d.Class) {
          console.log('no class');
          d.Class = BaseNode;
        }
      })
    }

    return (
      <PageContainer header={{
        title: this.state.data.name || '未命名'
      }} content={this.state.data.description} className={styles.main}>
        <div className={styles.editor}>
          <Row className={styles.editorHd}>
            <Col lg={16} xxl={16}>
              {/* FlowToolbar  */}
              <EditorToolbar data={this.state.data} forceJobUpdate={this.forceJobUpdate} />
            </Col>

            <Col lg={8} xxl={8} >
              <div id="dagStatusNormal" >
                <CheckCircleOutlined twoToneColor="#52c41a" /> 所有节点都已经连接正常
              </div>
              <div id="dagStatusErrur" style={{ display: 'none' }}>
                <InfoCircleOutlined twoToneColor="#52c41a" /> 节点[<b id="dagStatusErrorlist"></b>]还没接入任何数据
              </div>
            </Col>
          </Row>
          <Row className={styles.editorBd}>
            <Col lg={20} xxl={22} className={styles.editorContent}>
              <EditorGraph data={data} />
              <EditorDebug />
            </Col>
            <Col lg={4} xxl={2} className={styles.editorSidebar}>
              {/* FlowItemPanel */}
              <EditorPanel />
            </Col>

          </Row>
        </div>

      </PageContainer>
    );
  }
}

export default EditorFlow;