package org.the.force.jdbc.partition.driver.jdbc;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcBase;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by xuji on 2017/7/1.
 */
@Test(priority = 20)
public class TestMetaData extends TestJdbcBase {


    public void test1() throws Exception {
        Connection connection = getConnection();
        DatabaseMetaData dsMetaData = connection.getMetaData();
        ResultSet resultSet = dsMetaData.getTables("test", null, null, new String[] {"TABLE"});
        logger.info("表格信息");
        printResultSet(resultSet);
        resultSet = dsMetaData.getColumns("test", "test", "t_user", null);
        logger.info("column信息");
        printResultSet(resultSet);
        resultSet = dsMetaData.getPrimaryKeys("test", "test", "t_user");
        logger.info("primaryKeys信息");
        printResultSet(resultSet);

        resultSet = dsMetaData.getIndexInfo("test", "test", "t_user", false, false);
        logger.info("index信息");
        printResultSet(resultSet);

        Statement statement = connection.createStatement();
        resultSet = statement.executeQuery("DESC t_user");
        logger.info("desc 信息");
        printResultSet(resultSet);
        connection.close();
    }

    public void test2() throws Exception {
        Connection connection = getConnection();
        DatabaseMetaData dsMetaData = connection.getMetaData();
        ResultSet resultSet = dsMetaData.getSchemas(null, null);
        logger.info("schemas 信息1");
        printResultSet(resultSet);
        resultSet = dsMetaData.getSchemas();
        logger.info("schemas 信息2");
        printResultSet(resultSet);
        connection.close();
    }

    public void test3() throws Exception {
        Connection connection = getConnection();
        DatabaseMetaData dsMetaData = connection.getMetaData();
        ResultSet resultSet = dsMetaData.getCatalogs();
        logger.info("catalogs 信息");
        printResultSet(resultSet);
        connection.close();
    }
    public void test4() throws Exception {
        Connection connection = getConnection();
        DatabaseMetaData dsMetaData = connection.getMetaData();
        ResultSet resultSet = dsMetaData.getTableTypes();
        logger.info("tableTypes 信息");
        printResultSet(resultSet);
        connection.close();
    }
    public void test5() throws Exception {
        Connection connection = getConnection();
        DatabaseMetaData dsMetaData = connection.getMetaData();
        ResultSet resultSet = dsMetaData.getColumns("test", null, null,null);
        logger.info("全库column信息");
        printResultSet(resultSet);
        connection.close();
    }
}
