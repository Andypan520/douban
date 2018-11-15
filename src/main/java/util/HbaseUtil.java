package util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.*;

/**
 * Created by pandechuan on 2018/11/15.
 */
public class HbaseUtil {

    private static Configuration conf = null;

    static {
       /* conf = HBaseConfiguration.create();
        //使用eclipse时必须添加这个，否则无法定位
        conf.set("hbase.rootdir", "hdfs://sniper5:9000/hbase");
        conf.set("hbase.zookeeper.quorum", "sniper5,sniper6,sniper7");
        //conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        */
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "10.242.15.197");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("hbase.master", "127.0.0.1:60010");
    }

    /**
     * 创建表（单个列族）
     *
     * @param tableName
     * @param columnFamily
     * @throws IOException
     */
    public static void create(String tableName, String columnFamily) throws IOException {
        try (HBaseAdmin admin = new HBaseAdmin(conf)) {
            if (admin.tableExists(tableName)) {
                System.out.println("table exists...");
            } else {
                HTableDescriptor tableDesc = new HTableDescriptor(tableName);
                tableDesc.addFamily(new HColumnDescriptor(columnFamily));
                admin.createTable(tableDesc);
                System.out.println("table create successful... ");
            }
        }
    }

    /**
     * 创建表（多个列族）
     *
     * @param tableName
     * @param columnFamily
     * @throws IOException
     */
    public static void create(String tableName, String[] columnFamily) throws IOException {
        try (HBaseAdmin admin = new HBaseAdmin(conf)) {
            if (admin.tableExists(tableName)) {
                System.out.println("table exists...");
            } else {
                HTableDescriptor tableDesc = new HTableDescriptor(tableName);
                for (String family : columnFamily) {
                    tableDesc.addFamily(new HColumnDescriptor(family));
                }
                admin.createTable(tableDesc);
                System.out.println("table create successful... ");
            }
        }
    }

    /**
     * 添加一条记录
     *
     * @param tableName
     * @param rowKey
     * @param columnFamily
     * @param column
     * @param value
     * @throws IOException
     */
    public static void put(String tableName, String rowKey, String columnFamily, String column, String value) throws IOException {
        Table table;
        try (Connection connection = ConnectionFactory.createConnection(conf)) {

            table = connection.getTable(TableName.valueOf(tableName));
            Put p1 = new Put(Bytes.toBytes(rowKey));
            p1.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));

            table.put(p1);
        }
    }

    /**
     * 添多条记录
     *
     * @param tableName
     * @param rowKey
     * @param columnFamily
     * @param colValueMap
     * @throws IOException
     */
    public static void put(String tableName, String rowKey, String columnFamily, Map<String, String> colValueMap) throws IOException {
//        try (HTable table = new HTable(conf, tableName)) {
//
//            List<Put> putList = new ArrayList<Put>();
//
//            Set<String> set = colValueMap.keySet();
//
//            for (String column : set) {
//                String value = colValueMap.get(column);
//
//                Put p = new Put(Bytes.toBytes(rowKey));
//                p.add(columnFamily.getBytes(), column.getBytes(), value.getBytes());
//
//                putList.add(p);
//            }
//
//            table.put(putList);
//        }

        Table table;
        try (Connection connection = ConnectionFactory.createConnection(conf)) {

            table = connection.getTable(TableName.valueOf(tableName));
            List<Put> putList = Lists.newArrayList();
            Set<String> set = colValueMap.keySet();

            for (String column : set) {
                String value = colValueMap.get(column);
                Put p = new Put(Bytes.toBytes(rowKey));
                p.addColumn(columnFamily.getBytes(), column.getBytes(), value.getBytes());
                putList.add(p);
            }

            table.put(putList);
        }

        System.out.println("put " + "table:" + tableName + " rowKey:" + rowKey + " columnFamily:" + columnFamily + " colValueMap:" + colValueMap);
    }

    /**
     * 读取一条记录
     *
     * @param tableName
     * @param rowKey
     * @param columnFamily
     * @param column
     * @return
     * @throws IOException
     */
    public static String get(String tableName, String rowKey, String columnFamily, String column) throws IOException {
        Result result;
        String value;

        Table table;
        try (Connection connection = ConnectionFactory.createConnection(conf)) {

            table = connection.getTable(TableName.valueOf(tableName));
            //拿到rowKey对应的所有的列，result0.list().get(0),result0.list().get(1)...
//            Get get0 = new Get(Bytes.toBytes(rowKey));
//            Result result0 = table.get(get0);
//
//            //根据rowkey取得记录，打印记录的字段名，字段值
//            List<Cell> cells = result0.listCells();
//            cells.forEach(e -> {
//                System.out.println("FamilyArray:" + Bytes.toString(e.getFamilyArray())
//                        + " QualifierArray:" + Bytes.toString(e.getQualifierArray())
//                        + " ValueArray:" + Bytes.toString(e.getValueArray()));
//            });
//
//            List<KeyValue> keyValueList = result0.list();
//            for (KeyValue keyValue : keyValueList) {
//                System.out.println("key:" + Bytes.toString(keyValue.getKey()) + " value:" + Bytes.toString(keyValue.getValue()));
//            }

            //System.out.println("get:" + result0.size() + "  " + result0.list() + "  " + Bytes.toString(result0.list().get(0).getValue()));

            Get get = new Get(Bytes.toBytes(rowKey));
            get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
            result = table.get(get);
            System.out.println("size:" + result.size() + "   value:" + Bytes.toString(result.list().get(0).getValue()));

        }
        byte[] resultByte = result.getValue(columnFamily.getBytes(), column.getBytes());
        value = new String(resultByte);
        return value;
    }

    /**
     * 显示所有数据
     *
     * @param tableName
     * @throws IOException
     */
    public static void scan(String tableName) throws IOException {
        ResultScanner scanner;
        Table table;
        try (Connection connection = ConnectionFactory.createConnection(conf)) {

            table = connection.getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            scanner = table.getScanner(scan);
        }
        for (Result result : scanner) {
            System.out.println("scan:" + result);
        }
    }

    /**
     * 显示所有数据
     *
     * @param tableName
     * @param columnFamily
     * @throws IOException
     */
    public static void scan(String tableName, String columnFamily) throws IOException {
        ResultScanner scanner;
        Table table;
        try (Connection connection = ConnectionFactory.createConnection(conf)) {

            table = connection.getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            //扫描列族
            scan.addFamily(Bytes.toBytes(columnFamily));

            scanner = table.getScanner(scan);
        }
        for (Result result : scanner) {
            System.out.println("scan:" + result);
            List<KeyValue> keyValueList = result.list();
            for (KeyValue keyValue : keyValueList) {
                System.out.println("key:" + Bytes.toString(keyValue.getKey()) + " value:" + Bytes.toString(keyValue.getValue()));
            }
        }
        scanner.close();
    }

    /**
     * 显示所有数据
     *
     * @param tableName
     * @param columnFamily
     * @param column
     * @throws IOException
     */
    public static void scan(String tableName, String columnFamily, String column) throws IOException {
        ResultScanner scanner;
        Table table;
        try (Connection connection = ConnectionFactory.createConnection(conf)) {

            table = connection.getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();

            if (null != column && !column.trim().equals("")) {
                //扫描列
                scan.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
            } else {
                //扫描列族
                scan.addFamily(Bytes.toBytes(columnFamily));
            }

            scanner = table.getScanner(scan);
        }
        for (Result result : scanner) {
            System.out.println("scan:" + result);
            List<KeyValue> keyValueList = result.list();
            for (KeyValue keyValue : keyValueList) {
                System.out.println("key:" + Bytes.toString(keyValue.getKey()) + " value:" + Bytes.toString(keyValue.getValue()));
            }
        }
        scanner.close();
    }

    /**
     * select 。。。 from table where id between ... and
     * 显示所有数据
     *
     * @param tableName
     * @param columnFamily
     * @param rowKeyBegin
     * @param rowKeyEnd
     * @throws IOException
     */
    public static void scan(String tableName, String columnFamily, List<String> columns, String rowKeyBegin, String rowKeyEnd) throws IOException {
        ResultScanner scanner;
        Table table;
        try (Connection connection = ConnectionFactory.createConnection(conf)) {

            table = connection.getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            scan.setStartRow(rowKeyBegin.getBytes());//开始位置
            scan.setStopRow(rowKeyEnd.getBytes());//结束位置

            for (String column : columns) {
                //扫描列
                scan.addColumn(columnFamily.getBytes(), column.getBytes());
            }

            scanner = table.getScanner(scan);
        }
        for (Result result : scanner) {
            String rowKey = new String(result.getRow());

            for (KeyValue keyValue : result.raw()) {
                System.out.println(rowKey + ":" + new String(keyValue.getFamily()) + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()));
            }
        }
    }

    /**
     * 删除表
     *
     * @param tableName
     * @throws IOException
     */
    public static void drop(String tableName) throws IOException {
        try (HBaseAdmin admin = new HBaseAdmin(conf)) {
            if (admin.tableExists(tableName)) {
                admin.disableTable(tableName);
                admin.deleteTable(tableName);
            }
        }
    }

    /**
     * 删除多条记录
     *
     * @param tableName
     * @param rowKeys
     * @throws IOException
     */
    public static void delete(String tableName, String... rowKeys) throws IOException {
        Table table;
        try (Connection connection = ConnectionFactory.createConnection(conf)) {

            table = connection.getTable(TableName.valueOf(tableName));
            List<Delete> list = Lists.newArrayList();

            for (String rowKey : rowKeys) {
                Delete delete = new Delete(rowKey.getBytes());
                list.add(delete);
            }

            table.delete(list);
        }
    }

    /**
     * select 。。。 from table where id c_column = ?
     * 条件过滤
     *
     * @param tableName
     * @param columnFamily
     * @param column
     * @throws IOException
     */
    public static void query(String tableName, String columnFamily, String column, String value) throws IOException {
        ResultScanner scanner;
        Table table;
        try (Connection connection = ConnectionFactory.createConnection(conf)) {

            table = connection.getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();

            Filter filter = new SingleColumnValueFilter(columnFamily.getBytes(), column.getBytes(), CompareFilter.CompareOp.EQUAL, value.getBytes());
            scan.setFilter(filter);

            scanner = table.getScanner(scan);
        }
        for (Result result : scanner) {
            String rowKey = new String(result.getRow());

            for (KeyValue keyValue : result.raw()) {
                System.out.println(rowKey + ":" + new String(keyValue.getFamily()) + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()));
            }
        }
    }

    /**
     * 条件过滤        rowkey模糊查询
     *
     * @param tableName
     * @param value
     * @throws IOException
     */
    public static void queryByRowKey(String tableName, String value) throws IOException {
        ResultScanner scanner;
        Table table;
        try (Connection connection = ConnectionFactory.createConnection(conf)) {

            table = connection.getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            RowFilter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(value));
            scan.setFilter(filter);

            scanner = table.getScanner(scan);
        }
        for (Result result : scanner) {
            String rowKey = new String(result.getRow());

            for (KeyValue keyValue : result.raw()) {
                System.out.println(rowKey + ":" + new String(keyValue.getFamily()) + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()));
            }
        }
    }

    /**
     * select * from table where c1 = 111 and c2 in (1, 2)
     *
     * @param tableName
     * @param columnFamily
     * @throws IOException
     */
    public static void query(String tableName, String columnFamily) throws IOException {
        ResultScanner scanner;
        Table table;
        try (Connection connection = ConnectionFactory.createConnection(conf)) {

            table = connection.getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();

            SingleColumnValueFilter filter1 = new SingleColumnValueFilter(columnFamily.getBytes(), "c1".getBytes(), CompareFilter.CompareOp.EQUAL, "aaa".getBytes());

            FilterList filterAll = new FilterList();
            filterAll.addFilter(filter1);

            SingleColumnValueFilter filter2 = new SingleColumnValueFilter(columnFamily.getBytes(), "c4".getBytes(), CompareFilter.CompareOp.EQUAL, "101".getBytes());
            SingleColumnValueFilter filter3 = new SingleColumnValueFilter(columnFamily.getBytes(), "c4".getBytes(), CompareFilter.CompareOp.EQUAL, "102".getBytes());

            FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);
            filterList.addFilter(filter2);
            filterList.addFilter(filter3);

            filterAll.addFilter(filterList);

            scan.setFilter(filterAll);

            scanner = table.getScanner(scan);
        }

        for (Result result : scanner) {
            String rowKey = new String(result.getRow());

            for (KeyValue keyValue : result.raw()) {
                System.out.println(rowKey + ":" + new String(keyValue.getFamily()) + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()));
            }
        }
    }

    /**
     * select * from table where rowkey like 'aaaa%'
     * 查询rowkey以xxx开头的
     *
     * @param tableName
     * @param columnFamily
     * @throws IOException
     */
    public static void query(String tableName, String columnFamily, String value) throws IOException {
        ResultScanner scanner;
        Table table;
        try (Connection connection = ConnectionFactory.createConnection(conf)) {

            table = connection.getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();

            PrefixFilter filter = new PrefixFilter(value.getBytes());
            scan.setFilter(filter);

            scanner = table.getScanner(scan);
        }

        for (Result result : scanner) {
            String rowKey = new String(result.getRow());

            for (KeyValue keyValue : result.raw()) {
                System.out.println(rowKey + ":" + new String(keyValue.getFamily()) + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()));
            }
        }
    }

    /**
     * 清空所有数据
     *
     * @param tableName
     * @throws IOException
     */
    public static void truncate(String tableName) throws IOException {
        try (HBaseAdmin admin = new HBaseAdmin(conf)) {

            admin.disableTable(tableName);
            admin.truncateTable(TableName.valueOf(tableName), false);
        }
    }

    public static void main(String[] args) throws IOException {

//        System.out.println(conf);
        String andypan = "andypan";
        String andypan2 = "andypan2";
        String t5 = "t5";

        //HbaseUtil.create("andypan", "info");
        //HbaseUtil.create("andypan2", new String[]{"cf1","cf2"});

        //  HbaseUtil.create("t5", new String[]{"f1", "f2"});


//        for (int i = 0; i < 5; i++) {
//            Map<String, String> map = Maps.newHashMap();
//            map.put("c1", "aaa" + i);
//            map.put("c2", "bbb" + i);
//
//            HbaseUtil.put(t5, "r" + i, "f1", map);
//        }
//        for (int i = 0; i < 5; i++) {
//            Map<String, String> map = Maps.newHashMap();
//            map.put("c1", "ccc" + i);
//            map.put("c2", "ddd" + i);
//
//            HbaseUtil.put(t5, "r" + i, "f2", map);
//        }
//        for (int i = 5; i < 15; i++) {
//            Map<String, String> map = Maps.newHashMap();
//            map.put("c1", "eee" + i);
//
//            HbaseUtil.put(t5, "r" + i, "f2", map);
//        }


        // System.err.println(HbaseUtil.get("t5", "r1", "f1", "c1"));

        //  HbaseUtil.put("t5", "r99", "f1", "c1", "99999");
        /*
        HbaseUtil.put("t5", "r1", "f1", "c1", "aaaaaa");
        HbaseUtil.put("t5", "r1", "f1", "c2", "bbbbbb");
        HbaseUtil.put("t5", "r1", "f2", "c1", "cccccc");
        HbaseUtil.put("t5", "r2", "f2", "c1", "cccccc");

        HbaseUtil.get("t5", "r1", "f1", "c1");
  */
        //  HbaseUtil.get("t5", "r1", "f1", "c1");


        //HbaseUtil.scan("t5");
        // HbaseUtil.scan("t5", "f1", "c1");

        // HbaseUtil.get("t5", "r1", "f1", "c1");
  /*
        HbaseUtil.scan("t5", "f1", "");

        HbaseUtil.scan("t5", "f1", "c1");
  */
        //HbaseUtil.scan("t5", "f1");

        //System.out.println(conf);
//
//        String[] rowKeys = new String[]{"r1", "r3"};
//
//        HbaseUtil22.delete("t5", rowKeys);
//
//        HbaseUtil22.scan("t5", "f1");
//
//        HbaseUtil22.delete("t5", "r2");
//
//        List<String> columns = new ArrayList<String>();
//        columns.add("c1");
//
//        HbaseUtil22.scan("t5", "f1", columns, "r2", "r4");
//
//        HbaseUtil22.query("t5", "f1", "c1", "aaa1");
//
//        HbaseUtil22.queryByRowKey("t5", "[a-z][0-1]");

        //创建订单表
        //HbaseUtil.create("t_order", "info");

        //创建订单明细项表
        //HbaseUtil.create("t_item", "info");

        /*for(int i=100; i<110; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("c_userid", "userid"+i);
            map.put("c2", "bbb"+i);
            map.put("c3", "ccc"+i);
            map.put("c4", ""+i);
            map.put("c5", "eee"+i);

            HbaseUtil.put("t_order", "rowkey"+i, "info", map);
        }

        for(int i=100; i<110; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("c1", "aaa");
            map.put("c2", "bbb"+i);
            map.put("c3", "ccc"+i);
            map.put("c4", i+"");
            map.put("c5", "eee"+i);

            HbaseUtil.put("t_item", i+"_"+"item", "info", map);
        }*/

        //query("t_item", "info");

        //query("t_item", "info", "105_");
//        HbaseUtil.truncate("t_order");
//
//        HbaseUtil.drop("t5");
    }
}
