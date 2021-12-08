package com.filling.calculation.domain;



import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PreviewResult {
    String TableName;
    String Type;
    List Data;
    List<Map<String, String>> Column;
    String Status;
    String Error;

    public String getTableName() {
        return TableName;
    }

    public void setTableName(String tableName) {
        TableName = tableName;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public List getData() {
        return Data;
    }

    public void setData(List data) {
        Data = data;
    }

    public List<Map<String, String>> getColumn() {
        return Column;
    }

    public void setColumn(List<Map<String, String>> column) {
        Column = column;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getError() {
        return Error;
    }

    public void setError(String error) {
        Error = error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PreviewResult that = (PreviewResult) o;
        return Objects.equals(TableName, that.TableName) && Objects.equals(Type, that.Type) && Objects.equals(Data, that.Data) && Objects.equals(Column, that.Column) && Objects.equals(Status, that.Status) && Objects.equals(Error, that.Error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(TableName, Type, Data, Column, Status, Error);
    }

    @Override
    public String toString() {
        return "PreviewResult{" +
                "TableName='" + TableName + '\'' +
                ", Type='" + Type + '\'' +
                ", Data=" + Data +
                ", Column=" + Column +
                ", Status='" + Status + '\'' +
                ", Error='" + Error + '\'' +
                '}';
    }
}
