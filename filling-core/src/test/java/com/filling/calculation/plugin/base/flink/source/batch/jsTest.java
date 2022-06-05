package com.filling.calculation.plugin.base.flink.source.batch;

import javax.script.*;
import java.util.*;

public class jsTest {
    public static void main(String[] args) throws Exception {
//        org.objectweb.asm.FieldVisitor
        ScriptEngineManager m = new ScriptEngineManager();
        ScriptEngine e = m.getEngineByName("nashorn");

        Object obj1 = e.eval(
                "JSON.parse('{ \"x\": 343, \"y\": \"hello\", \"z\": [2,4,5] }');");
        Map<String, Object> map1 = (Map<String, Object>)obj1;
        System.out.println(map1.get("x"));
        System.out.println(map1.get("y"));
        System.out.println(map1.get("z"));
        Map<Object, Object> array1 = (Map<Object, Object>)map1.get("z");
        array1.forEach((a, b) -> System.out.println("z[" + a + "] = " + b));

        System.out.println();

        Object obj2 = e.eval(
                "Java.asJSONCompatible({ \"x\": 343, \"y\": \"hello\", \"z\": [2,4,5] })");
        Map<String, Object> map2 = (Map<String, Object>)obj2;
        System.out.println(map2.get("x"));
        System.out.println(map2.get("y"));
        System.out.println(map2.get("z"));
        List<Object> array2 = (List<Object>)map2.get("z");
        array2.forEach(a -> System.out.println(a));
    }
}