let editor = ace.edit();
let langTools = ace.require("ace/ext/language_tools");
// 根据sourceNode名称, 获取关键词
const getKey = () => {

    const targetNode = window.selectNode.options.id;

    let result = [];
    function getDepSourceNode(targetNode) {
        let current = window.canvas.edges.filter(_d => _d.targetNode.id == targetNode);
        if (current.lehgth = 0) {
            return;
        }

        if (current[0]) {
            getDepSourceNode(current[0].sourceNode.id, canvas);
            const sourceNode = current[0].sourceNode.id;
            const node_data = canvas.getNode(sourceNode).options.data;

            result.push(node_data);
        }
    }

    function getMap(re) {
        let _result = [];
        re.forEach(d => {
            if (d['target_field']) {
                _result.push({ 'word': d['target_field'], 'meta': d['name'] });
            } else if (d['schema']) {
                let json = JSON.parse(d['schema']);
                Object.keys(json).forEach(key => {
                    _result.push({ 'word': key, 'meta': d['name'] });
                }
                )
            }

        }
        )
        return _result;
    }
    getDepSourceNode(targetNode, result);
    return getMap(result);
}

const targetAutocomplete = () => {


    let wordList = [];

    try {
        wordList = getKey();
    } catch (error) {
        console.error(error);
        // expected output: ReferenceError: nonExistentFunction is not defined
        // Note - error messages will vary depending on browser
    }

    const rhymeCompleter = {
        getCompletions: (editor, session, pos, prefix, callback) => {
            if (prefix.length === 0) {
                callback(null, []);
                return
            }
            console.log(pos, session.getTokenAt(pos.row, pos.column));
            callback(null, wordList.map(function (ea) {
                return {
                    name: ea.word,
                    value: ea.word,
                    score: ea.score,
                    meta: ea.meta
                };
            }))
        }
    }

    langTools.addCompleter(rhymeCompleter);
}
export default targetAutocomplete;