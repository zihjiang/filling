package com.filling.calculation.plugin.base.flink.source;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.filling.calculation.flink.util.SchemaUtil;
import org.apache.flink.api.common.io.DelimitedInputFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ObjectArrayTypeInfo;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.types.Row;

import java.beans.beancontext.BeanContext;
import java.io.IOException;
import java.util.Map;

public class JsonRowInputFormat extends DelimitedInputFormat<Row> implements ResultTypeQueryable<Row> {


    private RowTypeInfo rowTypeInfo;

    private static final byte CARRIAGE_RETURN = (byte) '\r';

    private static final byte NEW_LINE = (byte) '\n';

    private String charsetName = "UTF-8";

    public JsonRowInputFormat(Path filePath, Configuration configuration, RowTypeInfo rowTypeInfo) {
        super(filePath, configuration);
        this.rowTypeInfo = rowTypeInfo;
    }

    @Override
    public Row readRecord(Row reuse, byte[] bytes, int offset, int numBytes) throws IOException {

        String str = new String(bytes, offset, numBytes, this.charsetName);
        JSONObject json = JSONObject.parseObject(str);
        Row reuseRow;
        if (reuse == null) {
            reuseRow = new Row(rowTypeInfo.getArity());
        } else {
            reuseRow = reuse;
        }
        reuseRow = setJsonRow(json);
        return reuseRow;
    }


    private Row setJsonRow(JSONObject json) {
        Map<String, Object> map = json.getInnerMap();
        return SchemaUtil.JsonMapToRow(map);
    }

    @Override
    public TypeInformation<Row> getProducedType() {
        return rowTypeInfo;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

}
