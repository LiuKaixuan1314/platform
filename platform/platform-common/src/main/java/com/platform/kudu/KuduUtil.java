package com.platform.kudu;

import java.util.ArrayList;
import java.util.List;

import org.apache.kudu.ColumnSchema;
import org.apache.kudu.ColumnSchema.ColumnSchemaBuilder;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.CreateTableOptions;
import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.KuduException;
import org.junit.Test;
import java.util.LinkedList;

/**
 * kudu操作实例
 * @author Administrator
 *
 */
public class KuduUtil {
	/**
	 * IP地址
	 */
	private static final String KUDU_MASTER ="172.10.4.218:7051";
	
	private static String tableName="TestKudu";
	/**
	 * 创建表
	 */
	@Test
    public void kuduCreateTableTest(){
        KuduClient client = new KuduClient.KuduClientBuilder(KUDU_MASTER).build();
        try {
            List<ColumnSchema> columns = new ArrayList(2);
            columns.add(new ColumnSchema.ColumnSchemaBuilder("key", Type.STRING)
                    .key(true)
                    .build());
            columns.add(new ColumnSchema.ColumnSchemaBuilder("value", Type.STRING)
                    .build());
            List<String> rangeKeys = new ArrayList<>();
            rangeKeys.add("key");
            Schema schema = new Schema(columns);
            client.createTable(tableName, schema,
                    new CreateTableOptions().setRangePartitionColumns(rangeKeys));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                client.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//	@Test
	public void cc() {
		KuduClient kuduClient = new KuduClient.KuduClientBuilder(
				"172.10.4.218:7051").build();
		ColumnSchema usernameCol = new ColumnSchemaBuilder("username",
				Type.STRING).key(true).build();
		ColumnSchema ageCol = new ColumnSchemaBuilder("age", Type.INT8).build();

		List<ColumnSchema> columns = new ArrayList<ColumnSchema>();
		columns.add(usernameCol);
		columns.add(ageCol);
		List<String> rangeKeys = new ArrayList<>();
		rangeKeys.add("age");
		Schema schema = new Schema(columns);
		String tableName = "users";
		try {
			if (!kuduClient.tableExists(tableName)) {
				kuduClient.createTable(tableName, schema,
						new CreateTableOptions()
								.setRangePartitionColumns(rangeKeys));
			}
		} catch (KuduException e) {
			e.printStackTrace();
		}

	}
	private static ColumnSchema newColumn(String name, Type type, boolean iskey) {
        ColumnSchemaBuilder column = new ColumnSchema.ColumnSchemaBuilder(name, type);
        column.key(iskey);
        return column.build();
    }

    public static void main(String[] args) throws KuduException {
        // master地址
        final String masteraddr = "172.10.4.218";
        // 创建kudu的数据库链接
        KuduClient client = new KuduClient.KuduClientBuilder(masteraddr).defaultSocketReadTimeoutMs(6000).build();

        // 设置表的schema
        List<ColumnSchema> columns = new LinkedList<>();
        columns.add(newColumn("CompanyId", Type.INT16, true));
        columns.add(newColumn("WorkId", Type.INT16, false));
        columns.add(newColumn("Name", Type.STRING, true));
        columns.add(newColumn("Gender", Type.STRING, false));
        columns.add(newColumn("Desc", Type.STRING, false));
        columns.add(newColumn("Photo", Type.BINARY, false));
        Schema schema = new Schema(columns);
        //创建表时提供的所有选项
        CreateTableOptions options = new CreateTableOptions();
        // 设置表的replica备份和分区规则
        List<String> parcols = new LinkedList<>();
        parcols.add("CompanyId");

        // 一个replica
        options.setNumReplicas(1);
        // 用列companyid做为分区的参照
        options.setRangePartitionColumns(parcols);
        // 添加key的hash分区
        //options.addHashPartitions(parcols, 3);
        try {
            client.createTable("TESTPERSON", schema, options);
        } catch (KuduException e) {
            e.printStackTrace();
        }
        client.close();
    }
}
