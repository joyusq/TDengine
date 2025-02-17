package com.taosdata.jdbc.cases;

import com.taosdata.jdbc.TSDBDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class QueryDataTest {

    static Connection connection;
    static Statement statement;
    static String dbName = "test";
    static String stbName = "meters";
    static String host = "127.0.0.1";

    @Before
    public void createDatabase() {
        try {
            Properties properties = new Properties();
            properties.setProperty(TSDBDriver.PROPERTY_KEY_USER, "root");
            properties.setProperty(TSDBDriver.PROPERTY_KEY_PASSWORD, "taosdata");
            properties.setProperty(TSDBDriver.PROPERTY_KEY_CHARSET, "UTF-8");
            properties.setProperty(TSDBDriver.PROPERTY_KEY_LOCALE, "en_US.UTF-8");
            properties.setProperty(TSDBDriver.PROPERTY_KEY_TIME_ZONE, "UTC-8");
            connection = DriverManager.getConnection("jdbc:TAOS://" + host + ":0/", properties);

            statement = connection.createStatement();
            statement.executeUpdate("drop database if exists " + dbName);
            statement.executeUpdate("create database if not exists " + dbName);
            statement.executeUpdate("use " + dbName);

            String createTableSql = "create table " + stbName + "(ts timestamp, name binary(64))";
            statement.executeUpdate(createTableSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQueryBinaryData() throws SQLException {
        String insertSql = "insert into " + stbName + " values(now, 'taosdata')";
        statement.executeUpdate(insertSql);

        String querySql = "select * from " + stbName;
        ResultSet rs = statement.executeQuery(querySql);

        while (rs.next()) {
            String name = rs.getString(2);
            assertEquals("taosdata", name);
        }
        rs.close();
    }

    @After
    public void close() {
        try {
            if (statement != null)
                statement.close();
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}