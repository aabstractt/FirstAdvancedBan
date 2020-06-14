package org.advancedban.provider;

import org.advancedban.utils.TargetOffline;

import java.sql.*;
import java.util.HashMap;

public class MysqlProvider implements Provider {

    private final HashMap<String, Object> data;

    private Connection connection;

    public MysqlProvider(HashMap<String, Object> data) throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connectAndCreateDatabase(data);
        } catch (SQLException e) {
            throw new SQLException(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Can't load JDBC Driver", e);
        }

        this.data = data;
    }

    public void connectAndCreateDatabase(HashMap<String, Object> data) throws SQLException {
        connectAndCreateDatabase(data, true);
    }

    public void connectAndCreateDatabase(HashMap<String, Object> data, boolean value) throws SQLException {
        if(value) {
            connection = DriverManager.getConnection("jdbc:mysql://" + data.get("host") + ":" + data.get("port"), (String) data.get("username"), (String) data.get("password"));

            Statement statement = connection.createStatement();

            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + data.get("dbname"));

            connection.close();

            connection = null;
        }

        connection = DriverManager.getConnection("jdbc:mysql://" + data.get("host") + ":" + data.get("port") + "/" + data.get("dbname"), (String) data.get("username"), (String) data.get("password"));
    }

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public TargetOffline getTargetOffline(String username) throws SQLException {
        if(isClosed()) connectAndCreateDatabase(data, false);

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE username = ?");

        preparedStatement.setString(1, username);

        ResultSet rs = preparedStatement.executeQuery();

        TargetOffline targetOffline = null;

        while(rs.next()) {
             targetOffline = new TargetOffline(rs.getString("username"), rs.getString("address"), rs.getString("lastAddress"));
        }

        return targetOffline;
    }

    @Override
    public void setTargetOffline(TargetOffline targetOffline) {
        try {
            if(isClosed()) connectAndCreateDatabase(data, false);

            boolean isTargetOffline = this.getTargetOffline(targetOffline.getName()) != null;

            PreparedStatement preparedStatement;

            if(!isTargetOffline) {
                preparedStatement = connection.prepareStatement("INSERT INTO players(username, address, lastAddress) VALUES (?, ?, ?)");
            } else {
                preparedStatement = connection.prepareStatement("UPDATE players SET username = ?, address = ?, lastAddress = ? WHERE username = ?");
            }

            preparedStatement.setString(1, targetOffline.getName());

            preparedStatement.setString(2, targetOffline.getAddress());

            preparedStatement.setString(3, targetOffline.getLastAddress());

            if(!isTargetOffline) {
                preparedStatement.execute();
            } else {
                preparedStatement.setString(4, targetOffline.getName());

                preparedStatement.executeUpdate();
            }

        } catch(SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @return bool
     */
    private boolean isClosed() throws SQLException {
        return connection != null && connection.isClosed();
    }
}
