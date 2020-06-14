package org.advancedban.provider;

import org.advancedban.extension.BanEntry;
import org.advancedban.extension.DeleteEntry;
import org.advancedban.extension.Entry;
import org.advancedban.extension.MuteEntry;
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

    @Override
    public void addEntry(Entry entry) {
        try {
            if(isClosed()) connectAndCreateDatabase(data, false);

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + entry.getTableName() + " (username, createdAt, finishAt, author, reason) VALUES (?, ?, ?, ?, ?)");

            preparedStatement.setString(1, entry.getName());

            preparedStatement.setString(2, entry.getCreatedAt());

            preparedStatement.setString(3, entry.getFinishAt());

            preparedStatement.setString(4, entry.getAuthor());

            preparedStatement.setString(5, entry.getReason());

            preparedStatement.execute();

        } catch(SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public BanEntry getBanActiveByUsername(String name) {
        return null;
    }

    @Override
    public MuteEntry getMuteActiveByUsername(String name) {
        return null;
    }

    @Override
    public BanEntry[] getAllBanByUsername(String name) {
        return new BanEntry[0];
    }

    @Override
    public BanEntry[] getAllBanActive() {
        return new BanEntry[0];
    }

    @Override
    public MuteEntry[] getAllMuteByUsername(String name) {
        return new MuteEntry[0];
    }

    @Override
    public MuteEntry[] getAllMuteActive() {
        return new MuteEntry[0];
    }

    @Override
    public DeleteEntry[] getAllDeleteByUsername(String name) {
        return new DeleteEntry[0];
    }

    @Override
    public Entry[] getAllActiveByUsername(String name) {
        return new Entry[0];
    }

    @Override
    public Entry[] getAllActive() {
        return new Entry[0];
    }

    @Override
    public void deleteEntry(Entry entry) {

    }

    @Override
    public void updateEntry(Entry entry) {

    }

    /**
     * @return bool
     */
    private boolean isClosed() throws SQLException {
        return connection != null && connection.isClosed();
    }
}
