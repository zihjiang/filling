import { Col, Row } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import styles from './index.less';
import { EditorGraph } from './components/EditorGraph/EditorGraph';

import EditorPanel from './components/EditorPanel/index';
import EditorToolbar from './components/EditorToolbar';
import { Component } from 'react';
import BaseNode from './components/EditorGraph/node';
import { fillingJob } from '../FillingJobs/service';
import { Spin } from 'antd';
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

  render() {
    if (_.isEqual(this.state.data, {})) return (<Spin />);
    const data = JSON.parse(this.state.data.jobText);

    if (data.nodes) {
      data.nodes.map(d => {
        if (!d.Class) {
          console.log('no class');
          d.Class = BaseNode;
        }
      })
    }

    return (
      <PageContainer content="这是一个新页面，从这里进行开发！" className={styles.main}>
        <div className={styles.editor}>
          <Row className={styles.editorHd}>
            <Col span={20}>
              {/* FlowToolbar  */}
              <EditorToolbar data={this.state.data} />
            </Col>
          </Row>
          <Row className={styles.editorBd}>
            <Col span={20} className={styles.editorContent}>
              <EditorGraph data={data} />
            </Col>
            <Col span={4} className={styles.editorSidebar}>
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