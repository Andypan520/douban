package db;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbutils.DbUtils;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Created by pandechuan on 2018/09/20.
 */
public class C3P0Util {
    private static ComboPooledDataSource comboPooledDataSource;

    private static void init() {
        // 创建C3P0的连接池，注意与DBCP的区别
        comboPooledDataSource = new ComboPooledDataSource();

        InputStream inputStream = C3P0Util.class.getClassLoader().getResourceAsStream("dbconfig.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);

            comboPooledDataSource.setUser(properties.getProperty("db.username"));
            comboPooledDataSource.setPassword(properties.getProperty("db.password"));
            comboPooledDataSource.setJdbcUrl(properties.getProperty("db.url"));
            comboPooledDataSource.setDriverClass(properties.getProperty("db.driverClassName"));

            comboPooledDataSource.setInitialPoolSize(Integer.parseInt(properties.getProperty("dataSource.initialSize")));
            comboPooledDataSource.setMaxIdleTime(Integer.parseInt(properties.getProperty("dataSource.maxIdle")));
            comboPooledDataSource.setMaxPoolSize(Integer.parseInt(properties.getProperty("dataSource.maxActive")));
            comboPooledDataSource.setMaxIdleTimeExcessConnections(Integer.parseInt(properties.getProperty("dataSource.maxWait")));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public synchronized static ComboPooledDataSource getDataSource() {
        if (comboPooledDataSource == null) {
            init();
        }
        return comboPooledDataSource;
    }

    public synchronized static Connection getConnection() {
        if (comboPooledDataSource == null) {
            init();
        }
        try {
            return comboPooledDataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void close(ResultSet resultSet, Statement statement, Connection connection) {
        if (resultSet != null) {
            try {
                DbUtils.close(resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                DbUtils.close(statement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                // 数据库连接池的 Connection 对象进行 close 时
                // 并不是真的进行关闭, 而是把该数据库连接会归还到数据库连接池中.
                DbUtils.close(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

