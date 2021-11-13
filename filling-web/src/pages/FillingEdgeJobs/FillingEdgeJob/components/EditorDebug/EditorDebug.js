import React, { Component } from 'react';

import { Collapse } from 'antd';
import _ from 'lodash';

import { diff as DiffEditor } from 'react-ace';
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/mode-javascript';
import "ace-builds/src-noconflict/theme-xcode";


class EditorDebug extends Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {

        console.log('previewData: ', window.previewData);

    }

    getpreviewData = () => {
        console.log("window.previewData: ", window.previewData);
    }
    
    render() {
        const { Panel } = Collapse;
        return (
            <Collapse
                defaultActiveKey={['0']}
                expandIconPosition={'right'}
                onChange={this.getpreviewData}
            >
                <Panel header="Preview data" key="1">
                    <div>
                        <DiffEditor
                            value={['{"name": 123}', '{"name": 4526}']}
                            // mode="json"
                            enableBasicAutocompletion
                            enableLiveAutocompletion
                            highlightActiveLine
                            showGutter
                            showPrintMargin
                            wrapEnabled
                            // readOnly
                            width="100%"
                            height="300px"
                            theme="xcode"
                            setOptions={{
                                enableBasicAutocompletion: true,
                                enableLiveAutocompletion: true,
                                enableSnippets: true,
                                showLineNumbers: true,
                                showConnectors: true,
                                readOnly: true,
                                tabSize: 2,
                            }}
                        />

                    </div>
                </Panel>
            </Collapse>
        );
    }
}

export { EditorDebug };
