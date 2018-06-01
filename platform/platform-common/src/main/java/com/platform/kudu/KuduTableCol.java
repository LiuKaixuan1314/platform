package com.platform.kudu;

import java.util.List;

import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;

public class KuduTableCol {
	private String tableName;
    private Schema tableSchema;
    private List tableBuilder;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Schema getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(List<ColumnSchema> cols) {
        this.tableSchema = new Schema(cols);
    }

    public List getTableBuilder() {
        return tableBuilder;
    }

    public void setTableBuilder(List tableBuilder) {
        this.tableBuilder = tableBuilder;
    }
}
