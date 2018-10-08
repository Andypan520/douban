import meta.Book2;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;
import org.junit.jupiter.api.Test;
import db.C3P0Util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by pandechuan on 2018/09/21.
 */
public class DBTest {

    @Test
    // ArrayHandler, 将查询结果的第一条记录封装成数组,返回
    public void arrayHandlerTest() throws SQLException {
        QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
        String sql = "select * from BookList";
        Object[] query = qr.query(sql, new ArrayHandler());
        System.out.println(Arrays.toString(query));
    }

    @Test
    // ArrayListHandler, 将查询结果的每一条记录封装成数组,将每一个数组放入list中返回
    public void arrayListHandlerTest() throws SQLException {
        QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
        String sql = "select * from BookList";
        List<Object[]> query = qr.query(sql, new ArrayListHandler());
        for (Object[] row : query) {
            System.out.println(Arrays.toString(row));
        }
    }

    @Test
    // BeanHandler, 将查询结果的第一条记录封装成指定的bean对象,返回
    public void beanHandlerTest() throws SQLException {
        QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());

        String sql = "select * from BookList";

        Book2 bean = qr.query(sql, new BeanHandler<>(Book2.class));
        System.out.println(bean);

    }

    @Test
    // BeanListHandler, 将查询结果的每一条记录封装成指定的bean对象,将每一个bean对象放入list中 返回.
    public void beanListHandlerTest() throws SQLException {
        QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());

        String sql = "select * from BookList";

//        List<Book> beanList = qr.query(sql, new BeanListHandler<>(Book.class));
//        for (Book bean : beanList) {
//            System.out.println(bean);
//        }
        List<Book2> beanList = qr.query(sql, new BeanListHandler<>(Book2.class));
        for (Book2 bean : beanList) {
            System.out.println(bean);
        }
    }

    @Test
    // MapHandler, 将查询结果的第一条记录封装成map,字段名作为key,值为value 返回
    public void mapHandlerTest() throws SQLException {
        QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());

        String sql = "select * from BookList";

        Map<String, Object> map = qr.query(sql, new MapHandler());
        System.out.println(map);
    }

    @Test
    // MapListHandler, 将查询结果的每一条记录封装map集合,将每一个map集合放入list中返回
    public void mapListHandlerTest() throws SQLException {
        QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());

        String sql = "select * from BookList";

        List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler());
        for (Map<String, Object> map : mapList) {
            System.out.println(map);
        }
    }


    @Test
    // ScalarHandler,针对于聚合函数 例如:count(*) 返回的是一个Long值
    public void ScalarHandlerTest() throws SQLException {
        QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());

        String sql = "select count(*) from BookList";

        Object obj = qr.query(sql, new ScalarHandler());
        System.out.println(obj); // obj是一个Long值
    }


    @Test
    public void testQueryRunnerUpdate() throws SQLException {
        //1. 创建 QueryRunner 的实现类
        QueryRunner queryRunner = new QueryRunner();
        Connection connection = C3P0Util.getConnection();
        String sql = "insert into BookList(title,author) values(?,?)";
        queryRunner.update(connection, sql, "aaaa", "bbbab");
        DbUtils.close(connection);
    }

    @Test
    public void testBatchUpdate() throws SQLException {
        //1. 创建 QueryRunner 的实现类
        QueryRunner queryRunner = new QueryRunner();
        Connection connection = C3P0Util.getConnection();
        String sql = "insert into BookList(title,author) values(?,?)";
        queryRunner.update(connection, sql, "aaaa", "bbbab");
        DbUtils.close(connection);
    }

}
