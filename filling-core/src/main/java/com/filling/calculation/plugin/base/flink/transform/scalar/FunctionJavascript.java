package com.filling.calculation.plugin.base.flink.transform.scalar;

import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.flink.util.SchemaUtil;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.scala.typeutils.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.annotation.FunctionHint;
import org.apache.flink.table.annotation.InputGroup;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.catalog.DataTypeFactory;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.util.RowDataUtil;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.table.types.inference.TypeInference;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class FunctionJavascript extends ScalarFunction {

    private static final ScriptEngine SCRIPT_ENGINE;
    static Invocable inv2;

    public FunctionJavascript(String script) {

        try {
            SCRIPT_ENGINE.eval(script);
        } catch (ScriptException e) {
            System.out.println("[ERROR] script error: " + e.getMessage());
        }
    }

    static {
        SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("javascript");
        inv2 = (Invocable) SCRIPT_ENGINE;
    }

    public String eval(@DataTypeHint(inputGroup = InputGroup.ANY) Object... o) throws Exception {
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