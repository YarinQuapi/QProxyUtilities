package me.yarinlevi.qproxyutilities;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author YarinQuapi
 */
public class MySQLHandler {
    private Connection connection;

    public MySQLHandler(Configuration config) {
        String hostName = config.getString("mysql.host");
        String database = config.getString("mysql.database");
        int port = config.getInt("mysql.port");
        String user = config.getString("mysql.user");
        String pass = config.getString("mysql.pass");

        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        dataSource.addDataSourceProperty("serverName", hostName);
        dataSource.addDataSourceProperty("port", port);
        dataSource.addDataSourceProperty("databaseName", database);
        dataSource.addDataSourceProperty("user", user);
        dataSource.addDataSourceProperty("password", pass);
        dataSource.addDataSourceProperty("useSSL", config.getBoolean("mysql.ssl"));
        dataSource.addDataSourceProperty("autoReconnect", true);
        dataSource.addDataSourceProperty("useUnicode", true);
        dataSource.addDataSourceProperty("characterEncoding", "UTF-8");

        String reportSQL = "CREATE TABLE IF NOT EXISTS `reports` ("
                + "`id` INT NOT NULL AUTO_INCREMENT,"
                + "`reported_uuid` VARCHAR(40) NOT NULL,"
                + "`reporter_uuid` varchar(40) NOT NULL,"
                + "`reported_name` varchar(16) NOT NULL,"
                + "`reporter_name` varchar(16) NOT NULL,"
                + "`reporter_server` TEXT NOT NULL,"
                + "`reported_server` TEXT NOT NULL,"
                + "`date_added` TEXT NOT NULL,"
                + "PRIMARY KEY (`id`)"
                + ") DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;";

        System.out.println("Please await mysql hook...");
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            {
                statement.executeUpdate(reportSQL);
                System.out.println("Successfully connected to MySQL database!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("Something went horribly wrong while connecting to database!");
        }
    }

    @Nullable
    public ResultSet get(String query) {
        try {
            return connection.prepareStatement(query).executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public int update(String query) {
        try {
            return connection.prepareStatement(query).executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    public boolean insert(String query) {
        try {
            connection.prepareStatement(query).execute();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}