package com.platform.kudu;

import java.util.ArrayList;
import java.util.List;

import org.apache.kudu.ColumnSchema.ColumnSchemaBuilder;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.CreateTableOptions;
import org.apache.kudu.client.Delete;
import org.apache.kudu.client.Insert;
import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.KuduException;
import org.apache.kudu.client.KuduScanner;
import org.apache.kudu.client.KuduSession;
import org.apache.kudu.client.KuduTable;
import org.apache.kudu.client.ListTablesResponse;
import org.apache.kudu.client.PartialRow;
import org.apache.kudu.client.RowResult;
import org.apache.kudu.client.Update;

public class KuduTest {
	private static final String KUDU_MASTER = System.getProperty("kuduMaster","172.10.4.218:5071");
	private KuduClient client;
	private KuduTable table;
	private ListTablesResponse listTablesResponse;
	private KuduTableCol kuduTableCol = new KuduTableCol();

	public static void main(String[] args) {

		KuduTest kuduTest = new KuduTest();

		// kuduTest.createTestTableWay();

		// kuduTest.createAiji66_TableWay();

		// 建立连接

		kuduTest.getConnection();

		// System.out.println("开始建表");

		// //创建kudu表

		// //kuduTest.createTable(kuduTest.kuduTableCol);

		// System.out.println("建表结束");

		kuduTest.showTables();

		// System.out.println("开始添加数据");

		// //添加单条数据测试

		// kuduTest.insertTimeColumn();

		// System.out.println("添加数据结束");

		// kuduTest.showTables();

		// System.out.println("批量添加数据");

		// //添加多条数据测试

		// kuduTest.inserColumns();

		// System.out.println("批量添加数据结束");

		// kuduTest.showTables();

		// //阅读列数据

		// kuduTest.scanRows();

		// System.out.println("执行结束");

		// //删除表

		// System.out.println("开始删除数据表");

		// kuduTest.deleteTable("aiji66_log");

	}

	// 0.和master主机端建立连接

	public void getConnection() {

		// Creates a new client that connects to the masters.

		this.client = new KuduClient.KuduClientBuilder(KUDU_MASTER).build();

	}

	// 1.show tables;

	public void showTables() {

		try {

			listTablesResponse = this.client.getTablesList();

			for (String tableString : listTablesResponse.getTablesList()) {

				System.out.println(tableString);

			}

		} catch (KuduException e) {

			e.printStackTrace();

		}

	}

	// 2.1设置元素--这里是我们以后建表需要经常修改的地方

	public void createTableWay() {

		// 2.1.1 设置table表名

		this.kuduTableCol.setTableName("user_test");

		// 2.2.2 设置colmun表字段

		/**
		 * 
		 * ("字段名",字段类型).key(boolean 是否为行键).encoding(字符编码).nullable(true
		 * 表是否设计为空).build() --构建
		 */

		List cols = new ArrayList<>();

		// cols.add(new ColumnSchemaBuilder("id",
		// Type.INT32).key(true).nullable(true).build());

		cols.add(new ColumnSchemaBuilder("id", Type.INT32).key(true).build());

		cols.add(new ColumnSchemaBuilder("name", Type.STRING).key(true).build());

		cols.add(new ColumnSchemaBuilder("ip", Type.INT64).key(true).build());

		cols.add(new ColumnSchemaBuilder("value", Type.STRING).build());

		this.kuduTableCol.setTableSchema(cols);

		// 2.2.3 设置分区

		List rangeKeys = new ArrayList();

		rangeKeys.add("id");

		rangeKeys.add("name");

		rangeKeys.add("ip");

		this.kuduTableCol.setTableBuilder(rangeKeys);

	}

	// 2. create table 创建表

	public void createTable(KuduTableCol kuduTableCol) {

		try {

			if (client.tableExists(kuduTableCol.getTableName())) {

				return;

			}

			// 这里是建表语句，注意，第三个分区选项有时候也需要修改

			table = this.client.createTable(kuduTableCol.getTableName(),
					kuduTableCol.getTableSchema(), new CreateTableOptions()
							.setRangePartitionColumns(kuduTableCol
									.getTableBuilder()));

		} catch (KuduException e) {

			e.printStackTrace();

		}

	}

	// 3.insert column 添加数据

	public void insertColumn() {

		try {

			KuduSession session = this.client.newSession();

			table = this.client.openTable("user_test");

			Insert insert = table.newInsert();

			insert.getRow().addInt("id", 1);

			insert.getRow().addString("name", "张三");

			insert.getRow().addLong("ip", 2017121801);

			insert.getRow().addString("value",
					"这是添加的第一个角色，就像一般的剧情一样，可能有一些不平凡的主角光环在里面");

			session.apply(insert);

		} catch (KuduException e) {

			e.printStackTrace();

		}

	}

	// 3.1批量插入数据,待改进

	public void inserColumns() {

		try {

			KuduSession session = this.client.newSession();

			table = this.client.openTable("user_test");

			GetWay getWay = new GetWay();

			long j = 2017122001;

			for (int i = 1; i < 500; i++) {

				Insert insert = table.newInsert();

				insert.getRow().addInt("id", i);

				insert.getRow().addString("name", getWay.getName());

				insert.getRow().addLong("ip", j);

				insert.getRow().addString("value", "批量插入的数据");

				session.apply(insert);

				j += 1;

			}

		} catch (KuduException e) {

			e.printStackTrace();

		}

	}

	// 4.update column 更新数据

	public void updateColumn() {

		try {

			KuduSession session = this.client.newSession();

			table = this.client.openTable("kevin_table");

			Update update = table.newUpdate();

			update.getRow().addInt("id", 1);

			update.getRow().addString("name", "张三大大");

			session.apply(update);

		} catch (KuduException e) {

			e.printStackTrace();

		}

	}

	// 5.delete column 删除数据

	public void deleteColumn() {

		try {

			KuduSession session = this.client.newSession();

			table = this.client.openTable("kevin_table");

			Delete delete = table.newDelete();

			delete.getRow().addInt("id", 1);

			session.apply(delete);

		} catch (KuduException e) {

			e.printStackTrace();

		}

	}

	// 6.KuduScanner 阅读列数据

	public void scanRows() {

		try {

			List columnNames = new ArrayList();

			columnNames.add("name");

			table = this.client.openTable("user_test");

			KuduScanner scanner = this.client.newScannerBuilder(table)
					.setProjectedColumnNames(columnNames).build();

			while (scanner.hasMoreRows()) {

				for (RowResult row : scanner.nextRows()) {

					System.out.println(row.getString("name"));

				}

			}

		} catch (KuduException e) {

			e.printStackTrace();

		}

	}

	// 6.1 KuduScanner阅读列数据-指定上下限

	public void scanRow() {

		List projectColumns = new ArrayList(1);

		projectColumns.add("id");

		projectColumns.add("name");

		projectColumns.add("ip");

		try {

			table = this.client.openTable("user_test");

			Schema schema = table.getSchema();

			PartialRow partialRow = schema.newPartialRow();

			partialRow.addInt("id", 1);

			PartialRow partialRow1 = schema.newPartialRow();

			partialRow1.addInt("id", 2);

			KuduScanner scanner = this.client.newScannerBuilder(table)

			.setProjectedColumnNames(projectColumns)// 指定输出列

					.lowerBound(partialRow)// 指定下限（包含）

					.exclusiveUpperBound(partialRow1)// 指定上限（不包含）

					.build();

			while (scanner.hasMoreRows()) {

				for (RowResult row : scanner.nextRows()) {

					System.out.println(row.getString("name"));

					System.out
							.println(row.getInt("id") + " "
									+ row.getString("name") + " "
									+ row.getString("ip"));

				}

			}

		} catch (KuduException e) {

			e.printStackTrace();

		}

	}

	// 7.Kudu deleteTable

	public void deleteTable(String tableName) {

		try {

			if (!client.tableExists(tableName)) {

				return;

			}

			this.client.deleteTable(tableName);

			System.out.println("该表已删除");

		} catch (KuduException e) {

			e.printStackTrace();

		}
	}

}
