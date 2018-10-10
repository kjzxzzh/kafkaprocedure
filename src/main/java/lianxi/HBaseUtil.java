package lianxi;//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//
//import org.apache.commons.lang.StringUtils;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.hbase.Cell;
//import org.apache.hadoop.hbase.CellUtil;
//import org.apache.hadoop.hbase.HBaseConfiguration;
//import org.apache.hadoop.hbase.HColumnDescriptor;
//import org.apache.hadoop.hbase.HTableDescriptor;
//import org.apache.hadoop.hbase.KeyValue;
//import org.apache.hadoop.hbase.TableName;
//import org.apache.hadoop.hbase.client.Connection;
//import org.apache.hadoop.hbase.client.ConnectionFactory;
//import org.apache.hadoop.hbase.client.Delete;
//import org.apache.hadoop.hbase.client.Get;
//import org.apache.hadoop.hbase.client.HBaseAdmin;
//import org.apache.hadoop.hbase.client.HTable;
//import org.apache.hadoop.hbase.client.Put;
//import org.apache.hadoop.hbase.client.Result;
//import org.apache.hadoop.hbase.client.ResultScanner;
//import org.apache.hadoop.hbase.client.Scan;
//import org.apache.hadoop.hbase.filter.ColumnPrefixFilter;
//import org.apache.hadoop.hbase.filter.PrefixFilter;
//import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
//import org.apache.hadoop.hbase.util.Bytes;
//
//
//public class HBaseUtil {
//	public static Configuration configuration;
//
//	public static Connection conn = null;
//	static {
//		System.setProperty("hadoop.home.dir", "C://hadoop//");
//		configuration = HBaseConfiguration.create();
//		configuration.set("hbase.zookeeper.property.clientPort", "2181");
//		/**
//		 * 这里hbase.zookeeper.quorum的属性与hbase-site.xml中相对应的设置有关
//		 * 1.在hbase-site.xml中
//		 * ，设置hbase.zookeeper.quorum为本地地址时，在填写以下hbase.zookeeper
//		 * .quorum参数值时，请加上端口。 ex: hbase-site.xml: <property>
//		 * <name>hbase.zookeeper.quorum</name> <value>172.16.43.10</value>
//		 * </property>
//		 * configuration.set("hbase.zookeeper.quorum","172.16.43.10:2181");
//		 * 注：2181端口是根据zookeeper中zoo.cfg设置的clientPort=2181值，也可通过登录Hbase
//		 * Master管理页面，在最底下可以看到 zookeeper的信息，可以直接把那边的拷贝过来。
//		 * 2.在hbase-site.xml中，填写的是几个zookeeper地址，则将其以分号为分隔填入 ex: hbase-site.xml:
//		 * <property> <name>hbase.zookeeper.quorum</name>
//		 * <value>172.16.43.10:2181,172.16.43.10:2182,172.16.43.10:2183</value>
//		 * </property> configuration.set("hbase.zookeeper.quorum",
//		 * "172.16.43.10:2181,172.16.43.10:2182,172.16.43.10:2183");
//		 */
//		configuration.set("hbase.zookeeper.quorum",
//				"192.168.71.144:2181");
//		configuration.set("hbase.client.scanner.timeout.period", "120000");
//		System.out.println("init");
////		configuration.set("hbase.master", "192.168.71.144:16201");
//
//		try {
//			conn = ConnectionFactory.createConnection(configuration);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * 创建表
//	 *
//	 * @throws Exception
//	 */
//
//	public static void createTable(String tableName, String[] column_family) {
//		try {
//			HBaseAdmin hBaseAdmin = (HBaseAdmin) conn.getAdmin();
//			HTableDescriptor desc = new HTableDescriptor(
//					TableName.valueOf(tableName));
//			// 添加列簇
//
//			if (column_family != null && column_family.length > 0) {
//				for (String column : column_family) {
//					if(StringUtils.isNotEmpty(column))
//					desc.addFamily(new HColumnDescriptor(column));
//				}
//			}
//			if (hBaseAdmin.tableExists(tableName)) {
//				System.out.println("table is exists !");
//				// System.exit(0);
//			} else {
//				hBaseAdmin.createTable(desc);
//				System.out.println("create table success!");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			// if (null != conn) {
//			// try {
//			// conn.close();
//			// } catch (IOException e) {
//			// e.printStackTrace();
//			// }
//			// }
//		}
//
//	}
//
//	/**
//	 * 批量导入数据
//	 *
//	 * @param pathToHFile
//	 * @param tableName
//	 */
//	public static void doBulkLoad(String pathToHFile, String tableName) {
//		HTable hTable = null;
//		ResultScanner scann = null;
//		try {
//			hTable = (HTable) conn.getTable(TableName.valueOf(tableName));
//			LoadIncrementalHFiles loadFfiles = new LoadIncrementalHFiles(
//					configuration);
//			loadFfiles.doBulkLoad(new Path(pathToHFile), hTable);// 导入数据
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != scann) {
//				scann.close();
//			}
//			if (null != hTable) {
//				try {
//					hTable.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			// if (null != conn) {
//			// try {
//			// conn.close();
//			// } catch (IOException e) {
//			// e.printStackTrace();
//			// }
//			// }
//		}
//
//	}
//
//	/**
//	 * 查询时，会每个cell代表一个列簇中的一个区域， 例如：有一个列簇为 test_1
//	 * 1.如果存储数据时没有存储列修饰符，则cell代表整个列簇的内容，查询出的就是该行下整个列簇的内容
//	 * 2.如果存储数据时有存储列修饰符，则每个列簇下的列修饰符各有一个cell
//	 *
//	 */
//
//	public static void query(String table) {
//		HTable htable = null;
//		ResultScanner scann = null;
//		try {
//			htable = (HTable) conn.getTable(TableName.valueOf(table));
//			scann = htable.getScanner(new Scan());
//			/**
//			 * 循环读取按行区分： 读取结果为： 该表RowKey为：1445320222118 列簇为：test_1
//			 * 值为：这是第一行第一列的数据 列簇为：test_2 值为：这是第一行第二列的数据 列簇为：test_3
//			 * 值为：这是第一行第三列的数据 ==========================================
//			 * 该表RowKey为：1445320222120 列簇为：test_1 值为：这是第二行第一列的数据 列簇为：test_2
//			 * 值为：这是第二行第二列的数据 列簇为：test_3 值为：这是第二行第三列的数据
//			 * ==========================================
//			 */
//			for (Result rs : scann) {
//				System.out.println("该表RowKey为：" + new String(rs.getRow()));
//				/**
//				 * 这边循环是按cell进行循环
//				 */
//				for (Cell cell : rs.rawCells()) {
//					System.out.println("列簇为："
//							+ new String(CellUtil.cloneFamily(cell)));
//					System.out.println("列修饰符为："
//							+ new String(CellUtil.cloneQualifier(cell)));
//					System.out.println("值为："
//							+ new String(CellUtil.cloneValue(cell)));
//				}
//				System.out
//						.println("==========================================");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != scann) {
//				scann.close();
//			}
//			if (null != htable) {
//				try {
//					htable.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	/**
//	 * 根据RowKey查询单行
//	 */
//
//	public static String queryByRowKey(String table, String rowkey) {
//		String val = "";
//		HTable htable = null;
//		try {
//			htable = (HTable) conn.getTable(TableName.valueOf(table));
//			Result rs = htable.get(new Get(rowkey.getBytes()));
//			for (Cell cell : rs.rawCells()) {
//				// 疑问：同个行，一个列簇里具有多列的查询？
////				System.out.println("列簇为："
////						+ new String(CellUtil.cloneFamily(cell)));
//				System.out.println("值为："
//						+ new String(CellUtil.cloneValue(cell)));
//				val += new String(CellUtil.cloneValue(cell))+",";
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != htable) {
//				try {
//					htable.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		if(val.endsWith(",")){
//			val=val.substring(0, val.length()-1);
//		}
//		return val;
//	}
//
//	/**
//	 * 根据RowKey查询单行
//	 */
//
//	public static ArrayList<String> rangeQueryByRowKey(String table, String startRowkey,String endRowkey) {
//		ArrayList<String> hosts = new ArrayList<String>();
//		HTable htable = null;
//		ResultScanner result = null;
//		try {
//			htable = (HTable) conn.getTable(TableName.valueOf(table));
//			Scan scan = new Scan();
////			  Scan scan = new Scan(Bytes.toBytes("0"), Bytes.toBytes("z"));
//			scan.setStartRow(Bytes.toBytes(startRowkey));
//			scan.setStopRow(Bytes.toBytes(endRowkey));
//			result = htable.getScanner(scan);
//			for (Result rs : result) {
////				 System.out.println(Bytes.toString(rs.getValue(Bytes.toBytes("info"), Bytes.toBytes("tags"))));
//				for (Cell cell : rs.rawCells()) {
//					// 疑问：同个行，一个列簇里具有多列的查询？
////					System.out.println("列簇为："
////							+ new String(CellUtil.cloneFamily(cell)));
////					System.out.println("值为："
////							+ new String(CellUtil.cloneValue(cell)));
////					val += new String(CellUtil.cloneValue(cell))+",";
//					hosts.add(CellUtil.cloneValue(cell).toString());
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != htable) {
//				try {
//					htable.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//			}
//			if(null !=result){
//				try {
//					result.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			// if (null != conn) {
//						// try {
//						// conn.close();
//						// } catch (IOException e) {
//						// e.printStackTrace();
//						// }
//						// }
//		}
////		if(val.endsWith(",")){
////			val=val.substring(0, val.length()-1);
////		}
//		return hosts;
//	}
//	/**
//	 * 根据RowKey查询单行
//	 */
//
//	public static void hbaseHostQuery(String table, String startRowkey,String endRowkey,String filename) {
//		File file = new File(filename);
//		OutputStreamWriter os = null;
//		BufferedWriter writer = null;
//        HTable htable = null;
//		ResultScanner result = null;
//		try {
//			os = new OutputStreamWriter(new FileOutputStream(file,true));
//			writer = new BufferedWriter(os);
//			htable = (HTable) conn.getTable(TableName.valueOf(table));
//			Scan scan = new Scan();
//			scan.setStartRow(Bytes.toBytes(startRowkey));
//			scan.setStopRow(Bytes.toBytes(endRowkey));
//			result = htable.getScanner(scan);
//			for (Result rs : result) {
//				for (Cell cell : rs.rawCells()) {
//					writer.write(new String(CellUtil.cloneValue(cell)));
//					writer.newLine();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != htable) {
//				try {
//					htable.close();
//					writer.flush();
//			        os.close();
//			        writer.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//			}
//			if(null !=result){
//				try {
//					result.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//	public static String queryByFilter(String table, String rowPrifix) {
//		String val = "";
//		HTable htable = null;
//		ResultScanner result = null;
//		try {
//			htable = (HTable) conn.getTable(TableName.valueOf(table));
//			Scan scan = new Scan();
//			scan.setFilter(new ColumnPrefixFilter(Bytes.toBytes(rowPrifix)));
//			result = htable.getScanner(scan);
//			for (Result rs : result) {
////				KeyValue[] kv = rs.raw();
//
//				for (Cell cell : rs.rawCells()) {
//					// 疑问：同个行，一个列簇里具有多列的查询？
////					System.out.println("列簇为："
////							+ new String(CellUtil.cloneFamily(cell)));
////					System.out.println("值为："
////							+ new String(CellUtil.cloneValue(cell)));
//					val += new String(CellUtil.cloneValue(cell))+",";
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != htable) {
//				try {
//					htable.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//			}
//			if(null !=result){
//				try {
//					result.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			// if (null != conn) {
//						// try {
//						// conn.close();
//						// } catch (IOException e) {
//						// e.printStackTrace();
//						// }
//						// }
//		}
//		if(val.endsWith(",")){
//			val=val.substring(0, val.length()-1);
//		}
//		return val;
//	}
//
//	/**
//	 * 插入数据
//	 *
//	 * @throws Exception
//	 */
//
//	public static void insertData(String table, String rowkey, String family,HashMap map) {
//		HTable hTable = null;
//		try {
//			hTable = (HTable) conn.getTable(TableName.valueOf(table));
//			// 一个PUT代表一行，构造函数传入的是RowKey
////			Put put = new Put((String.valueOf(System.currentTimeMillis())).getBytes());
//			Put put = new Put(rowkey.getBytes());
//			if(map!=null && map.size()>0){
//				Iterator<String> iterator = map.keySet().iterator();
//				while(iterator.hasNext()){
//					String key = iterator.next();
//					if("".equals(key)){
//						continue;
//					}else{
//						String val=(String) map.get(key);
//						put.addColumn(family.getBytes(), key.getBytes(), val.getBytes());
//					}
//				}
//			}
//
////			put.addColumn("info".getBytes(), "tel".getBytes(), bean.getUid()
////					.getBytes());
////			put.addColumn("info".getBytes(), "host".getBytes(), bean.getTags()
////					.getBytes());
////			// put.addColumn("test_2".getBytes(), null,
////			// "这是第一行第二列的数据".getBytes());
////			// put.addColumn("test_3".getBytes(), null,
////			// "这是第一行第三列的数据".getBytes());
//			List<Put> puts = new ArrayList<Put>();
//			puts.add(put);
//			// 添加进表中
//			hTable.put(puts);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != hTable) {
//				try {
//					hTable.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
////	/**
////	 * 向一个列簇中插入多个值
////	 */
////
////	public void insertColumsValue() {
////		HTable table = null;
////		try {
////			table = (HTable) conn.getTable(TableName.valueOf("test"));
////			Put put = new Put("1445320222118".getBytes());
////			// 1.如果没有指定列修饰符，而在这之下已经有内容，则覆盖原先内容
////			// 2.如果有指定列修饰符，而在该列修饰符下如果存在内容则覆盖
////			put.addColumn("test_1".getBytes(), null,
////					"这是第一行第一列的第二个数值".getBytes());
////			table.put(put);
////		} catch (Exception e) {
////			e.printStackTrace();
////		} finally {
////			if (null != table) {
////				try {
////					table.close();
////				} catch (IOException e) {
////					e.printStackTrace();
////				}
////			}
////
////			// if (null != conn) {
////			// try {
////			// conn.close();
////			// } catch (IOException e) {
////			// e.printStackTrace();
////			// }
////			// }
////		}
////
////	}
//	/**
//	 * 向一个列簇中插入多个值
//	 */
//
//	public static void insertColumsValue(String table,String rowkey,String family, String qualifier,String value) {
//		HTable htable = null;
//		try {
//			htable = (HTable) conn.getTable(TableName.valueOf(table));
//			Put put = new Put(rowkey.getBytes());
//			// 1.如果没有指定列修饰符，而在这之下已经有内容，则覆盖原先内容
//			// 2.如果有指定列修饰符，而在该列修饰符下如果存在内容则覆盖
//			if(StringUtils.isEmpty(qualifier)){
//				put.addColumn(family.getBytes(), null,value.getBytes());
//			}else{
//				put.addColumn(family.getBytes(), qualifier.getBytes(),value.getBytes());
//			}
//			htable.put(put);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != htable) {
//				try {
//					htable.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//
//			// if (null != conn) {
//			// try {
//			// conn.close();
//			// } catch (IOException e) {
//			// e.printStackTrace();
//			// }
//			// }
//		}
//
//	}
//	/**
//	 * 添加数据时，添加列修饰符 列修饰符：相当于在一个列簇中，根据列修饰符分隔成不同区域存储内容。（HBase的特性） 插入后，查询到的数值：
//	 * 该表RowKey为：1445320222118 列簇为：test_1 列修饰符为： 值为：这是第一行第一列的第二个数值 列簇为：test_1
//	 * 列修饰符为：1 值为：test_1_1 列簇为：test_1 列修饰符为：2 值为：test_1_2 列簇为：test_2 列修饰符为：
//	 * 值为：这是第一行第二列的数据 列簇为：test_3 列修饰符为： 值为：这是第一行第三列的数据
//	 * ========================================== 该表RowKey为：1445320222120
//	 * 列簇为：test_1 列修饰符为： 值为：这是第二行第一列的数据 列簇为：test_2 列修饰符为： 值为：这是第二行第二列的数据
//	 * 列簇为：test_3 列修饰符为： 值为：这是第二行第三列的数据
//	 * ==========================================
//	 */
//
//	public static void insertrAddColumnQualifier() {
//		HTable table = null;
//		try {
//			table = (HTable) conn.getTable(TableName.valueOf("test"));
//			Put put = new Put("1445320222118".getBytes());
//			put.addColumn("test_1".getBytes(), "1".getBytes(),
//					"test_1_1".getBytes());
//			put.addColumn("test_1".getBytes(), "2".getBytes(),
//					"test_1_2".getBytes());
//
//			table.put(put);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				table.close();
//				// conn.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//	}
//
//	/**
//	 * 删除指定名称的列簇
//	 */
//
//	public static void deleteFamily(String table,String family) {
//		HBaseAdmin admin = null;
//		try {
//			admin = (HBaseAdmin) conn.getAdmin();
//			admin.deleteColumn(table.getBytes(), family);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			// try {
//			// if (null != conn) {
//			// conn.close();
//			// }
//			// } catch (Exception e) {
//			// e.printStackTrace();
//			// }
//		}
//	}
//
//	/**
//	 * 删除指定行
//	 */
//
//	public static void deleteRow(String table,String rowkey) {
//		HTable htable = null;
//		try {
//			htable = (HTable) conn.getTable(TableName.valueOf(table));
//			htable.delete(new Delete(rowkey.getBytes()));
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (null != htable) {
//					htable.close();
//				}
//				// if (null != conn) {
//				// conn.close();
//				// }
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//	}
//
//	/**
//	 * 删除指定表名
//	 */
//	public static void deleteTable(String table) {
//		HBaseAdmin admin = null;
//		try {
//			admin = (HBaseAdmin) conn.getAdmin();
//			// 在删除一张表前，要使其失效
//			admin.disableTable(table);
//			admin.deleteTable(table);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static void main(String[] args) {
//		HBaseUtil test = new HBaseUtil();
//		String columns = "info1,info2";
//		test.createTable("hostdata_20170321", columns.split(","));
////		System.out.println(test.queryByRowKey("mmdata_uids_tags_20160302", "1064614000534"));
//
////		System.out.println(test.queryByRowKey("mmdata_tags_uids_data_20160309", "Powered by hs633.com!_0"));
////		System.out.println(new Date());
////		System.out.println(rangeQueryByRowKey("mmdata_tags_uids_20160302", "游戏_0","游戏_9"));
////		System.out.println(new Date());
////		System.out.println(queryByFilter("mmdata_uids_tags_20160302", "134"));
//
////
////		test.deleteFamily("hostdata_20160330","info1");
//
////		test.deleteTable("hostdata_20160330");
////		test.deleteRow("hostdata_20160309", "z11.cnzz.com");
//
////		HashMap map =new HashMap();
////		map.put("tags1", "test1");
////		map.put("tags2", "test2");
////		test.insertData("hostdata_20160309", "z11.cnzz.com", "info",map);
//
//
//		// String rowkey="";
//		//
//		// for(int i=1;i<100001;i++){
//		// String tel ="1325826938"+i;
//		//
//		// rowkey=tel;
//		// test.queryByRowKey(rowkey);
//		// }
//
//	}
//
//}