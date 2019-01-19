package com.yyok.hadoop.util;

import java.io.IOException;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.BufferedMutatorParams;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.log4j.Logger;

import com.yyok.hbase.rowkey.HashChoreWoker;
import com.yyok.kafka.util.KafkaUtils;

/**
 * @author linqinghong by 2019-01-08
 */
public class HBaseUtils {

	private static Logger logger = Logger.getLogger(HBaseUtils.class);
	ThreadLocal<List<Put>> threadLocal = new ThreadLocal<List<Put>>();
	Configuration configuration = null;

	// 连接集群
	public static Connection initHbase() throws IOException {
		Configuration configuration = HBaseConfiguration.create();
		configuration.set("hbase.zookeeper.property.clientPort", "2181");
		configuration.set("hbase.zookeeper.quorum", KafkaUtils.initZKIPGaiaProd);
		configuration.set("hbase.master", "gaiaa:60000");
		//configuration.set("hbase.rootdir", "hdfs://jianbing/data/hbase");
		Connection connection = ConnectionFactory.createConnection(configuration);
		if (connection == null || connection.isClosed()) {
			connection = ConnectionFactory.createConnection(configuration);
		}
		return connection;
	}

	public HBaseUtils() {
		try {
			initHbase();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 插入记录（单行单列族-单列单值）
	 *
	 * @param tableName    表名
	 * @param row          行名
	 * @param columnFamily 列族名
	 * @param column       列名
	 * @param value        值
	 * @throws IOException
	 */
	public static void putTable(String tableName, String rowkey, String columnFamily, String column, String data)
			throws IOException {
		Connection conn = initHbase();
		Table table = conn.getTable(TableName.valueOf(tableName));
		try {
			Put put = new Put(Bytes.toBytes(rowkey));
			put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(data));
			table.put(put);

		} finally {
			if (table != null)
				table.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 插入记录（单行单列族-单列单值）
	 *
	 * @param tableName    表名
	 * @param row          行名
	 * @param columnFamily 列族名
	 * @param column       列名
	 * @param value        值
	 * @throws IOException
	 */
	public void putHTable(String tableName, String rowKey, String family, String column, String value) {
		Configuration conf = null;
		try {
			conf = HBaseUtils.initHbase().getConfiguration();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			HTable table = new HTable(conf, TableName.valueOf(tableName));
			HBaseAdmin admin = new HBaseAdmin(conf);
			// 判断表是否存在，如果不存在进行创建
			if (!admin.tableExists(Bytes.toBytes(tableName))) {
				HTableDescriptor tableDescriptor = new HTableDescriptor(Bytes.toBytes(tableName));
				HColumnDescriptor columnDescriptor = new HColumnDescriptor(Bytes.toBytes(family));
				tableDescriptor.addFamily(columnDescriptor);
				admin.createTable(tableDescriptor);
			}
			table.setAutoFlush(true);
			// 进行数据插入
			Put put = new Put(Bytes.toBytes(rowKey));
			put.add(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(value));
		
			table.put(put);
			if (conf != null)
			table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * } 插入记录（单行单列族-多列多值）
	 *
	 * @param tableName     表名
	 * @param row           行名
	 * @param columnFamilys 列族名
	 * @param columns       列名（数组）
	 * @param values        值（数组）（且需要和列一一对应）
	 */
	public static void putList(String tableName, String row, String columnFamilys, String[] columns, String[] values) {
		TableName name = TableName.valueOf(tableName);
		Table table = null;
		try {
			table = initHbase().getTable(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Put put = new Put(Bytes.toBytes(row));
		for (int i = 0; i < columns.length; i++) {
			put.addColumn(Bytes.toBytes(columnFamilys), Bytes.toBytes(columns[i]), Bytes.toBytes(values[i]));
			try {
				table.put(put);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void putList(String tableName, String[] rowKeys, String[] families, String[] columns, String[] values) {
		Configuration conf = null;
		try {
			conf = HBaseUtils.initHbase().getConfiguration();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			HBaseAdmin admin = new HBaseAdmin(conf);
			HTable table = new HTable(conf, tableName.valueOf(tableName));
			int length = rowKeys.length;
			List<Put> putList = new ArrayList<>();
			if (!admin.tableExists(Bytes.toBytes(tableName))) {
				System.err.println("the " + tableName + " is not exist");
				System.exit(1);
			}
			for (int i = 0; i < length; i++) {
				Put put = new Put(Bytes.toBytes(rowKeys[i]));
				put.add(Bytes.toBytes(families[i]), Bytes.toBytes(columns[i]), Bytes.toBytes(values[i]));
				putList.add(put);
			}
			table.put(putList);
			table.close();
		} catch (Exception e) {
		}
	}

	/**
	 * 异步往指定表添加数据
	 * 
	 * @param tablename 表名
	 * @param puts      需要添加的数据
	 * @return long 返回执行时间SocPut
	 * @throws IOException
	 */
	public static long put(String tablename, List<Put> puts) throws Exception {
		long currentTime = System.currentTimeMillis();
		Connection conn = initHbase();
		final BufferedMutator.ExceptionListener listener = new BufferedMutator.ExceptionListener() {
			@Override
			public void onException(RetriesExhaustedWithDetailsException e, BufferedMutator mutator) {
				for (int i = 0; i < e.getNumExceptions(); i++) {
					System.out.println("Failed to sent put " + e.getRow(i) + ".");
					logger.error("Failed to sent put " + e.getRow(i) + ".");
				}
			}
		};
		BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf(tablename)).listener(listener);
		params.writeBufferSize(5 * 1024 * 1024);

		final BufferedMutator mutator = conn.getBufferedMutator(params);
		try {
			if (mutator != null) {
				mutator.mutate(puts);
				mutator.flush();
			}
		} finally {

			if (mutator != null)
				mutator.close();
			if (conn != null)
				conn.close();
		}
		return System.currentTimeMillis() - currentTime;
	}

	/**
	 * 异步往指定表添加数据
	 * 
	 * @param tablename 表名
	 * @param put       需要添加的数据
	 * @return long 返回执行时间SocPut
	 * @throws IOException
	 */
	public static long put(String tablename, Put put) throws Exception {
		return put(tablename, Arrays.asList(put));
	}

	/**
	 * 往指定表添加数据
	 * 
	 * @param tablename 表名
	 * @param puts      需要添加的数据
	 * @return long 返回执行时间
	 * @throws IOException
	 */
	public static long putByHTable(String tablename, List<Put> puts) throws Exception {
		long currentTime = System.currentTimeMillis();
		Connection conn = initHbase();
		HTable htable = (HTable) conn.getTable(TableName.valueOf(tablename));
		htable.setAutoFlushTo(false);
		htable.setWriteBufferSize(5 * 1024 * 1024);
		try {
			htable.put((List<Put>) puts);
			htable.flushCommits();
		} finally {
			if (htable != null)
				htable.close();
			if (conn != null)
				conn.close();
		}
		return System.currentTimeMillis() - currentTime;
	}

	/**
	 * 根据表名获取到HTable实例
	 */
	public HTable doGetTable(String tableName) {
		Connection conn = null;
		HTable table = null;
		try {
			conn = initHbase();
//            table = new HTable(configuration, tableName);
			final TableName tname = TableName.valueOf(tableName);
			table = (HTable) conn.getTable(tname);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return table;
	}

	/**
	 * 批量添加记录到HBase表，同一线程要保证对相同表进行添加操作！
	 *
	 * @param tableName HBase表名
	 * @param rowkey    HBase表的rowkey
	 * @param cf        HBase表的columnfamily
	 * @param column    HBase表的列key
	 * @param value     写入HBase表的值value
	 * @param value     批量提交数据条数
	 */
	public void bulkput(String tableName, String rowkey, String cf, String column, String value, int bulcount) {
		try {
			List<Put> list = threadLocal.get();
			if (list == null) {
				list = new ArrayList<Put>();
			}
			Put put = new Put(Bytes.toBytes(rowkey));
			put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(column), Bytes.toBytes(value));
			list.add(put);
			if (list.size() >= bulcount) {
				HTable table = doGetTable(tableName);
				table.put(list);
				list.clear();
			} else {
				threadLocal.set(list);
			}
//            table.flushCommits();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doGet(String tableName) throws IOException {
		Connection conn = initHbase();
		long begin = System.currentTimeMillis();
		HTable table = (HTable) conn.getTable(TableName.valueOf(tableName));
		TableName name = table.getName();
		System.out.println("name : " + name);
		/*
		 * Get get = new Get(Bytes.toBytes("91244740570")); Result result =
		 * table.get(get); String str =
		 * Bytes.toString(result.getValue(Bytes.toBytes("mediaCodes"),
		 * Bytes.toBytes("Userid"))); System.out.println(str); long end =
		 * System.currentTimeMillis(); System.out.println("used time : " + (end -
		 * begin));
		 */
		table.close();
	}

	/**
	 * 创建表
	 *
	 * @param tableName    表名
	 * @param columnFamily 列族（数组）
	 * @param delflag      default value true,fix valus false plz.
	 */
	public void createTable(String tableName, String[] columnFamily, boolean delflag) throws IOException {
		TableName name = TableName.valueOf(tableName);
		// 如果存在则删除
		Admin admin = initHbase().getAdmin();
		if (admin.tableExists(name) && delflag == true) {
			//admin.disableTable(name);
			//admin.deleteTable(name);
			logger.error("create htable error! this table {} already exists!" + name);
		} else {
			HTableDescriptor desc = new HTableDescriptor(name);
			for (String cf : columnFamily) {
				desc.addFamily(new HColumnDescriptor(cf));
			}
			admin.createTable(desc);
		}
	}

	/**
	 * @function 创建表 逗号分隔，可以有多个columnFamily
	 * @param tableName
	 * @param columnFamily
	 * @return
	 */
	public static boolean create(String tableName, String columnFamily) {
		HBaseAdmin admin = null;
		try {
			admin = null;// new HBaseAdmin(configuration);
			if (admin.tableExists(tableName)) {
				System.out.println(tableName + " exists!");
				return false;
			} else {
				// 逗号分隔，可以有多个columnFamily
				String[] cfArr = columnFamily.split(",");
				HColumnDescriptor[] hcDes = new HColumnDescriptor[cfArr.length];
				for (int i = 0; i < cfArr.length; i++) {
					hcDes[i] = new HColumnDescriptor(cfArr[i]);
				}
				HTableDescriptor tblDes = new HTableDescriptor(TableName.valueOf(tableName));
				for (HColumnDescriptor hc : hcDes) {
					tblDes.addFamily(hc);
				}
				admin.createTable(tblDes);
				System.out.println(tableName + " create successfully！");
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @function 默认二个columnFamily为info,data
	 * @param tableName
	 * @throws IOException
	 */
	public void createTable(String tableName) throws IOException {
		Connection conn = initHbase();
		HBaseAdmin admin = (HBaseAdmin) conn.getAdmin();
		HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(tableName));
		HColumnDescriptor htd_info = new HColumnDescriptor("info");
		htd.addFamily(htd_info);
		htd.addFamily(new HColumnDescriptor("data"));
		htd_info.setMaxVersions(3);

		admin.createTable(htd);
		admin.close();

	}

	/**
	 * @function 预分区创建表
	 * @param tableNameStr namespace:tableName
	 * @param columnFamily Array[String]
	 * @param delflag      default value true,fix valus false plz.
	 */
	public static void createTableInHash(String tableNameStr, String[] columnFamily, boolean delflag) {
		HashChoreWoker worker = new HashChoreWoker(1000000, 15);
		byte[][] splitKeys = worker.calcSplitKeys();

		HBaseAdmin admin = null;
		TableName tableName = null;

		try {
			admin = (HBaseAdmin) initHbase().getAdmin();
			tableName = TableName.valueOf(tableNameStr);

			if (admin.tableExists(tableName) && delflag == true) {
					/*try {
						admin.disableTable(tableName);
						admin.deleteTable(tableName);
					} catch (Exception e) {
					}*/
					
				} else {
					HTableDescriptor tableDesc = new HTableDescriptor(tableName);
					for (String cf : columnFamily) {
						HColumnDescriptor columnDesc = new HColumnDescriptor(Bytes.toBytes(cf));
						columnDesc.setMaxVersions(3);
						tableDesc.addFamily(columnDesc);
						tableDesc.setConfiguration("hbase.hregion.scan.loadColumnFamiliesOnDemand", "true");
						tableDesc.setConfiguration("COMPRESSION", "SNAPPY");
					}
					admin.createTable(tableDesc, splitKeys);
				
				}
		} catch (MasterNotRunningException e1) {
			e1.printStackTrace();
		} catch (ZooKeeperConnectionException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (admin != null)
					admin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 自定义获取分区splitKeys
	 */
	public byte[][] getSplitKeys(String[] keys) {
		if (keys == null) {
			// 默认为15个分区
			keys = new String[] { "1|", "2|", "3|", "4|", "5|", "6|", "7|", "8|", "9|", "10|", "11|", "12|", "13|",
					"14|", "15|" };
		}
		byte[][] splitKeys = new byte[keys.length][];
		// 升序排序
		TreeSet<byte[]> rows = new TreeSet<byte[]>(Bytes.BYTES_COMPARATOR);
		for (String key : keys) {
			rows.add(Bytes.toBytes(key));
		}

		Iterator<byte[]> rowKeyIter = rows.iterator();
		int i = 0;
		while (rowKeyIter.hasNext()) {
			byte[] tempRow = rowKeyIter.next();
			rowKeyIter.remove();
			splitKeys[i] = tempRow;
			i++;
		}
		return splitKeys;
	}

	/**
	 * 按startKey和endKey，分区数获取分区
	 */
	public static byte[][] getHexSplits(String startKey, String endKey, int numRegions) {
		byte[][] splits = new byte[numRegions - 1][];
		BigInteger lowestKey = new BigInteger(startKey, 16);
		BigInteger highestKey = new BigInteger(endKey, 16);
		BigInteger range = highestKey.subtract(lowestKey);
		BigInteger regionIncrement = range.divide(BigInteger.valueOf(numRegions));
		lowestKey = lowestKey.add(regionIncrement);
		for (int i = 0; i < numRegions - 1; i++) {
			BigInteger key = lowestKey.add(regionIncrement.multiply(BigInteger.valueOf(i)));
			byte[] b = String.format("%016x", key).getBytes();
			splits[i] = b;
		}
		return splits;
	}

	// 行计数
	public static long rowCount(String tableName) {
		long rowCount = 0;
		@SuppressWarnings("resource")
		AggregationClient aggregationClient = null;// new AggregationClient(conf);
		Scan scan = new Scan();
		try {
			rowCount = aggregationClient.rowCount(TableName.valueOf(tableName), new LongColumnInterpreter(), scan);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return rowCount;
	}

	/**
	 * 删除一行记录
	 *
	 * @param tablename 表名
	 * @param rowkey    行名
	 */
	public void deleteRow(String tablename, String rowkey) throws IOException {
		TableName name = TableName.valueOf(tablename);
		Table table = initHbase().getTable(name);
		Delete d = new Delete(rowkey.getBytes());
		table.delete(d);
	}

	/**
	 * 删除单行单列族记录
	 * 
	 * @param tablename    表名
	 * @param rowkey       行名
	 * @param columnFamily 列族名
	 */
	public void deleteColumnFamily(String tablename, String rowkey, String columnFamily) throws IOException {
		TableName name = TableName.valueOf(tablename);
		Table table = initHbase().getTable(name);
		Delete d = new Delete(rowkey.getBytes()).deleteFamily(Bytes.toBytes(columnFamily));
		table.delete(d);
	}

	/**
	 * 删除单行单列族单列记录
	 *
	 * @param tablename    表名
	 * @param rowkey       行名
	 * @param columnFamily 列族名
	 * @param column       列名
	 */
	public void deleteColumn(String tablename, String rowkey, String columnFamily, String column) throws IOException {
		TableName name = TableName.valueOf(tablename);
		Table table = initHbase().getTable(name);
		Delete d = new Delete(rowkey.getBytes()).deleteColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
		table.delete(d);
	}

	/**
	 * 查找一行记录
	 *
	 * @param tablename 表名
	 * @param rowKey    行名
	 */
	public static String selectRow(String tablename, String rowKey) throws IOException {
		String record = "";
		TableName name = TableName.valueOf(tablename);
		Table table = initHbase().getTable(name);
		Get g = new Get(rowKey.getBytes());
		Result rs = table.get(g);
		NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = rs.getMap();
		for (Cell cell : rs.rawCells()) {
			StringBuffer stringBuffer = new StringBuffer().append(Bytes.toString(cell.getRow())).append("\t")
					.append(Bytes.toString(cell.getFamily())).append("\t").append(Bytes.toString(cell.getQualifier()))
					.append("\t").append(Bytes.toString(cell.getValue())).append("\n");
			String str = stringBuffer.toString();
			record += str;
		}
		return record;
	}

	/**
	 * 查找一行记录
	 * @param tablename
	 * @param rowKey
	 * @return
	 * @throws IOException
	 */
	public static Result selectByRowkey(String tablename, String rowKey) throws IOException {
		TableName name = TableName.valueOf(tablename);
		Table table = initHbase().getTable(name);
		Get g = new Get(rowKey.getBytes());
		return table.get(g);
	}
	
	/**
	 * 查找单行单列族单列记录
	 *
	 * @param tablename    表名
	 * @param rowKey       行名
	 * @param columnFamily 列族名
	 * @param column       列名
	 * @return
	 */
	public static String selectValue(String tablename, String rowKey, String columnFamily, String column)
			throws IOException {
		TableName name = TableName.valueOf(tablename);
		Table table = initHbase().getTable(name);
		Get g = new Get(rowKey.getBytes());
		g.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
		Result rs = table.get(g);
		return Bytes.toString(rs.value());
	}

	/**
	 * 查询表中所有行（Scan方式）
	 *
	 * @param tablename
	 * @return
	 */
	public String scanAllRecord(String tablename) throws IOException {
		String record = "";
		TableName name = TableName.valueOf(tablename);
		Table table = initHbase().getTable(name);
		Scan scan = new Scan();
		ResultScanner scanner = table.getScanner(scan);
		try {
			for (Result result : scanner) {
				for (Cell cell : result.rawCells()) {
					StringBuffer stringBuffer = new StringBuffer().append(Bytes.toString(cell.getRow())).append("\t")
							.append(Bytes.toString(cell.getFamily())).append("\t")
							.append(Bytes.toString(cell.getQualifier())).append("\t")
							.append(Bytes.toString(cell.getValue())).append("\n");
					String str = stringBuffer.toString();
					record += str;
				}
			}
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return record;
	}

	/**
	 * 根据rowkey关键字查询报告记录
	 *
	 * @param tablename
	 * @param rowKeyword
	 * @return
	 */
	public static List scanReportDataByRowKeyword(String tablename, String rowKeyword) throws IOException {
		ArrayList list = new ArrayList<>();
		Table table = initHbase().getTable(TableName.valueOf(tablename));
		Scan scan = new Scan();
		// 添加行键过滤器，根据关键字匹配
		RowFilter rowFilter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator(rowKeyword));
		scan.setFilter(rowFilter);
		ResultScanner scanner = table.getScanner(scan);
		try {
			for (Result result : scanner) {
				// TODO 此处根据业务来自定义实现
				list.add(null);
			}
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return list;
	}

	/**
	 * } } 根据rowkey关键字和时间戳范围查询报告记录
	 *
	 * @param tablename
	 * @param rowKeyword
	 * @return
	 */

	public static List scanReportDataByRowKeywordTimestamp(String tablename, String rowKeyword, Long minStamp,
			Long maxStamp) throws IOException {
		ArrayList list = new ArrayList<>();
		Table table = initHbase().getTable(TableName.valueOf(tablename));
		Scan scan = new Scan();
		// 添加scan的时间范围
		scan.setTimeRange(minStamp, maxStamp);
		RowFilter rowFilter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator(rowKeyword));
		scan.setFilter(rowFilter);
		ResultScanner scanner = table.getScanner(scan);
		try {
			for (Result result : scanner) {
				// TODO 此处根据业务来自定义实现
				list.add(null);
			}
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return list;
	}

	/**
	 * 利用协处理器进行全表count统计
	 *
	 * @param tablename
	 */
	public Long countRowsWithCoprocessor(String tablename) throws Throwable {
		TableName name = TableName.valueOf(tablename);
		Admin admin = initHbase().getAdmin();
		HTableDescriptor descriptor = admin.getTableDescriptor(name);
		String coprocessorClass = "org.apache.hadoop.hbase.coprocessor.AggregateImplementation";
		if (!descriptor.hasCoprocessor(coprocessorClass)) {
			admin.disableTable(name);
			descriptor.addCoprocessor(coprocessorClass);
			admin.modifyTable(name, descriptor);
			admin.enableTable(name);
		}
		// 计时
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Scan scan = new Scan();
		AggregationClient aggregationClient = new AggregationClient(configuration);
		Long count = aggregationClient.rowCount(name, new LongColumnInterpreter(), scan);
		stopWatch.stop();
		System.out.println("RowCount：" + count + "，全表count统计耗时：" + stopWatch.getSplitNanoTime());// getTotalTimeMillis());
		return count;
	}

}
