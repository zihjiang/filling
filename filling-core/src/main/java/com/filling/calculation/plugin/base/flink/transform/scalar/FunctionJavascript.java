package com.filling.calculation.plugin.base.flink.transform.scalar;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.flink.util.SchemaUtil;
import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.annotation.InputGroup;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.types.Row;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class FunctionJavascript extends ScalarFunction {
    private static Invocable inv2;
    static ScriptEngine scriptEngine;

    String script;

    public FunctionJavascript(String script) {
        this.script = script;
        scriptEngine = new ScriptEngineManager().getEngineByName("Nashorn");
        System.setProperty("nashorn.args", "--language=es6");
    }

    public String eval(@DataTypeHint(inputGroup = InputGroup.ANY) Object... o) throws Exception {
        if (scriptEngine == null) {
            scriptEngine = new ScriptEngineManager().getEngineByName("Nashorn");
            scriptEngine.eval(script);
            inv2 = (Invocable) scriptEngine;
        }
        String[] column = (String[]) o[o.length - 1];
        JSONObject row = new JSONObject();
        for (int i = 0; i < o.length - 1; i++) {
            if (!SchemaUtil.isBaseType(o[i])) {
                if (o[i] instanceof org.apache.flink.types.Row) {
                    row.put(column[i], new JSONObject(SchemaUtil.rowToJsonMap((Row) o[i])));
                } else {
                    row.put(column[i], o[i]);
                }
            } else {
                row.put(column[i], o[i]);
            }

        }
        return inv2.invokeFunction("process", row).toString();
    }
}