package com.filling.calculation.plugin.base.flink.transform.scalar;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.scala.typeutils.Types;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.types.Row;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;

public class FunctionJavascript extends TableFunction<Row> {

    private  String script = "";
    private  ScriptEngine engine;

    public FunctionJavascript(String script ) {
        this.script = script;
    }

    public void eval(String event) throws Exception {

        if (!(engine instanceof Invocable)) {
            engine =  new ScriptEngineManager().getEngineByName("javascript");
            System.out.println(" init ScriptEngineManager");
            System.out.println("script: " + script);
        }


        engine.eval(script + "function _process(d){ return JSON.stringify(process(JSON.parse(d)))}");
        Invocable inv2 = (Invocable) engine;
        JSONObject jsonObject = JSONObject.parseObject(inv2.invokeFunction("_process", event).toString());
        Row row = new Row(jsonObject.size());

        int num = 0;
        for(Map.Entry<String,Object> entry : jsonObject.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            row.setField(num, value.toString());
            num ++;
        }
        collect(row);
    }

    @Override
    public TypeInformation<Row> getResultType() {
        String[] fields = "hostid,metric,value,auth".split(",");

        TypeInformation[] types = new  TypeInformation[fields.length];
        for (int i = 0; i< types.length; i++){
            types[i] = Types.STRING();
        }
        new RowTypeInfo(types,fields);
        System.out.println("init TypeInformation ... ");


        return Types.ROW(fields, types);
    }

}
