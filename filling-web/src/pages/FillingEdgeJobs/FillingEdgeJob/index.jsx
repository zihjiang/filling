import { Col, Row } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import styles from './index.less';
import { EditorGraph } from './components/EditorGraph/EditorGraph';

import EditorPanel from './components/EditorPanel/index';
import EditorToolbar from './components/EditorToolbar';
import { Component } from 'react';
import BaseNode from './components/EditorGraph/node';
import { fillingEdgeJobById, findByFillingEdgeNodesId } from './service';
import { Spin } from 'antd';
import rightTools from './components/EditorPanel/data';
import ProForm, {
  ProFormSelect
} from '@ant-design/pro-form';
class EditorFlow extends Component {
  constructor(props) {
    super(props);
    this.state = {
      id: this.props.match.params.id,
      nodeId: this.props.match.params.nodeId,
      data: {},
      jobs: []
    };

  }

  getJob = () => {
    return fillingEdgeJobById(this.state.id);
  }

  getjobs = async (id) => {

    return findByFillingEdgeNodesId(id);
  }

  componentDidMount() {
    if (this.state.jobs.length <= 0) {
      console.log("加载jobs");
      this.getjobs(this.state.nodeId).then((jobs) => {
        this.setState({
          jobs
        })
      })
    }
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
    if (_.isEqual(this.state.data, {})) return (<Spin />);
    const data = JSON.parse(this.state.data.jobText);
    // 更改全局状态
    window.jobRunStatus = this.state.data.status == 2 ? true : false;
    if (data.nodes) {
      data.nodes.map(d => {
        const node = rightTools.find(_d => _d.name == d.pluginName);

        d.configDefinitions = JSON.stringify(node.configDefinitions);
        d.configGroupDefinition = JSON.stringify(node.configGroupDefinition);
        d.icon = node.icon;
        d.label = node.label;
        if (!d.Class) {
          console.log('no class');
          d.Class = BaseNode;
        }
      })
    }

    let selectJob = () => {
      let options = [];
      for (let i = 0; i < this.state.jobs.length; i++) {
        const job = this.state.jobs[i];
        options[i] = {};
        options[i].value = job.id;
        options[i].label = job.name || job.pipelineId;

      }

      return (
        <>
          <ProForm
            submitter={false}
            onValuesChange={(changeValues) => {

              location.href = `/FillingEdgeJobs/${this.state.nodeId}/FillingEdgeJob/${changeValues.jobId}`;
              // history.push("/FillingEdgeJobs/" + this.state.nodeId + "/FillingEdgeJob/" + changeValues.jobId)
            }}
          >
            <ProFormSelect
              options={options}
              width="xl"
              name="jobId"
              initialValue={ this.state.id ? this.state.id/1 : null }
            />
          </ProForm>
        </>
      );
    }

    return (
      <PageContainer
        header={{
          title: selectJob()
        }}
        content={this.state.data.description} className={styles.main}
      >
        <div className={styles.editor}>
          <Row className={styles.editorHd}>
            <Col lg={20} xxl={22}>
              {/* FlowToolbar  */}
              <EditorToolbar data={this.state.data} forceJobUpdate={this.forceJobUpdate} nodeId={this.state.nodeId} />
            </Col>
          </Row>
          <Row className={styles.editorBd}>
            <Col lg={20} xxl={22} className={styles.editorContent}>
              <EditorGraph data={data} />
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