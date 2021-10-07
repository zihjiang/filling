// import * as panelPluginsInstance from 'butterfly-dag/plugins/panel/dist/index.js';

// import * as panelPluginsInstance from 'butterfly-dag/plugins/panel/dist/index.js';
// import { panelPlugins } from './panelPlugins';
import { panelPluginsInstance } from './panelPlugins';
import './index.less';
import { Component } from 'react';
import LightFilter, { ProFormSelect, ProFormText } from '@ant-design/pro-form';
import data from './data';
import { Affix } from 'antd';
class EditorPanel extends Component {

    constructor(props) {
        super(props);

    }

    componentDidMount() {

        this.canvas = window.canvas;
        // panelPluginsInstance.PanelNode = BaseNode;
        panelPluginsInstance.register(
            [
                {
                    root: document.getElementById('dnd'),
                    canvas: this.canvas,
                    // type: 'basic',
                    height: 90,
                    data: data,
                },
            ], () => {
                console.log('finish')
            }
        );
    }

    filterFrom(values) {

        document.getElementById('dnd').innerHTML = "";
        this.canvas = window.canvas;
        // panelPluginsInstance.PanelNode = BaseNode;

        panelPluginsInstance.register(
            [
                {
                    root: document.getElementById('dnd'),
                    canvas: this.canvas,
                    // type: 'basic',
                    height: 90,
                    data: _.filter(data, (d) => { return (d.text.indexOf(values["filterString"]) >= 0 || (d.pluginType == values["selectMode"] || values["selectMode"] == "all")) }),
                },
            ], () => {
                console.log('finish')
            }
        );
    }

    render() {
        let initialValues = {
            selectMode: "all"
        };
        return (
            <div className={"filter"}>
                <LightFilter
                    initialValues={initialValues}
                    submitter={false}
                    onValuesChange={(values) => this.filterFrom(values)}
                >
                    <Affix offsetTop={10}>
                        <ProFormSelect
                            valueEnum={{
                                all: '全部',
                                source: '源',
                                transform: '算子',
                                sink: '目标'
                            }}
                            style={{
                                margin: 16,
                            }}
                            name="selectMode"
                        />
                        <ProFormText name="filterString" placeholder="过滤" />

                    </Affix>


                </LightFilter>

                <div className="dnd" id="dnd"></div>
            </div>
        );
    }
}
export { EditorPanel };