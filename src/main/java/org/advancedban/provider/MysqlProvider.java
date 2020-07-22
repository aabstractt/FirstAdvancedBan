package org.advancedban.provider;

import org.advancedban.extension.BanEntry;
import org.advancedban.utils.TargetOffline;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MysqlProvider implements Provider {

    private final HashMap<String, Object> data;

    private Connection connection;

    public MysqlProvider(HashMap<String, Object> data) throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            intentConnect(data);

            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS players (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(70), address VARCHAR(60), lastAddress VARCHAR(60))");

            preparedStatement.executeUpdate();

            preparedStatement.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Can't load JDBC Driver", e);
        }

        this.data = data;
    }

    public void intentConnect(HashMap<String, Object> data) throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + data.get("host") + ":" + data.get("port") + "/" + data.get("dbname") + "?serverTimezone=UTC", (String) data.get("username"), (String) data.get("password"));
    }

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public TargetOffline getTargetOffline(String username) {
        TargetOffline targetOffline = null;

        try {
            if(isClosed()) intentConnect(data);

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE username = ?");

            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                targetOffline = new TargetOffline(rs.getString("username"), rs.getString("address"), rs.getString("lastAddress"));
            }

            rs.close();

            preparedStatement.close();

        } catch(SQLException e) {
            e.printStackTrace();
        }

        return targetOffline;
    }

    @Override
    public void setTargetOffline(TargetOffline targetOffline) {
        try {
            if(isClosed()) intentConnect(data);

            boolean isTargetOffline = getTargetOffline(targetOffline.getName()) != null;

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

            preparedStatement.close();

        } catch(SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void addBan(BanEntry entry) {
        try {
            if(this.isClosed()) this.intentConnect(this.data);

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ban (type, username, createdAt, finishAt, author, reason, address, finished) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

            preparedStatement.setInt(1, entry.getType());

            preparedStatement.setString(2, entry.getName());

            preparedStatement.setString(3, entry.getCreatedAt());

            preparedStatement.setInt(4, (int) TimeUnit.MILLISECONDS.toMinutes(entry.getFinishAt()));

            preparedStatement.setString(5, entry.getAuthor());

            preparedStatement.setString(6, entry.getReason());

            preparedStatement.setString(7, entry.getAddress());

            preparedStatement.setBoolean(8, false);

            preparedStatement.execute();

            preparedStatement.close();

        } catch(SQLException exception) {
            exception.printStackTrace();
        }
    }

    public BanEntry getActiveById(Integer id) {
        BanEntry entry = null;

        try {
            if(this.isClosed()) this.intentConnect(this.data);

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ban WHERE id = ?");

            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                entry = new BanEntry(rs.getInt("type"), rs.getString("username"), rs.getString("createdAt"), TimeUnit.MINUTES.toMillis(rs.getInt("finishAt")), rs.getString("author"), rs.getString("reason"), rs.getString("address"), rs.getBoolean("finished"), rs.getInt("id"));
            }

            rs.close();

            preparedStatement.close();

        } catch(SQLException exception) {
            exception.printStackTrace();
        }

        return entry;
    }

    public BanEntry getBanActiveByUsername(String username) {
        BanEntry entry = this.getBanActiveByUsername(username, false);

        if(entry == null) {
            return this.getBanActiveByUsername(username, true);
        }

        return entry;
    }

    @Override
    public BanEntry getBanActiveByUsername(String username, boolean permanent) {
        BanEntry entry = null;

        try {
            if(this.isClosed()) this.intentConnect(this.data);

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ban WHERE type = ? AND username = ? AND finished = ?");

            preparedStatement.setInt(1, permanent ? 1 : 2);

            preparedStatement.setString(2, username);

            preparedStatement.setBoolean(3, false);

            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                entry = new BanEntry(rs.getInt("type"), rs.getString("username"), rs.getString("createdAt"), TimeUnit.MINUTES.toMillis(rs.getInt("finishAt")), rs.getString("author"), rs.getString("reason"), rs.getString("address"), rs.getBoolean("finished"), rs.getInt("id"));
            }

            rs.close();

            preparedStatement.close();

        } catch(SQLException exception) {
            exception.printStackTrace();
        }

        return entry;
    }

    @Override
    public BanEntry getMuteActiveByUsername(String username) {
        BanEntry entry = this.getMuteActiveByUsername(username, false);

        if(entry == null) {
            return this.getMuteActiveByUsername(username, true);
        }

        return entry;
    }

    @Override
    public BanEntry getMuteActiveByUsername(String username, boolean permanent) {
        BanEntry entry = null;

        try {
            if(this.isClosed()) this.intentConnect(this.data);

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ban WHERE type = ? AND username = ? AND finished = ?");

            preparedStatement.setInt(1, permanent ? 3 : 4);

            preparedStatement.setString(2, username);

            preparedStatement.setBoolean(3, false);

            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                entry = new BanEntry(rs.getInt("type"), rs.getString("username"), rs.getString("createdAt"), TimeUnit.MINUTES.toMillis(rs.getInt("finishAt")), rs.getString("author"), rs.getString("reason"), rs.getString("address"), rs.getBoolean("finished"), rs.getInt("id"));
            }

            rs.close();

            preparedStatement.close();

        } catch(SQLException exception) {
            exception.printStackTrace();
        }

        return entry;
    }

    @Override
    public List<BanEntry> getAllDeleteByUsername(String username) {
        ArrayList<BanEntry> entrys = new ArrayList<>();

        try {
            if(this.isClosed()) this.intentConnect(this.data);

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ban WHERE (type > 4 AND type < 7) AND username = ?");

            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                entrys.add(new BanEntry(rs.getInt("type"), rs.getString("username"), rs.getString("createdAt"), TimeUnit.MINUTES.toMillis(rs.getInt("finishAt")), rs.getString("author"), rs.getString("reason"), rs.getString("address"), rs.getBoolean("finished"), rs.getInt("id")));
            }

            rs.close();

            preparedStatement.close();

        } catch(SQLException exception) {
            exception.printStackTrace();
        }

        return entrys;
    }

    @Override
    public List<BanEntry> getAllActiveByUsername(String username) {
        List<BanEntry> entrys = new ArrayList<>();

        try {
            if(this.isClosed()) this.intentConnect(this.data);

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ban WHERE username = ?");

            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                entrys.add(new BanEntry(rs.getInt("type"), rs.getString("username"), rs.getString("createdAt"), TimeUnit.MINUTES.toMillis(rs.getInt("finishAt")), rs.getString("author"), rs.getString("reason"), rs.getString("address"), rs.getBoolean("finished"), rs.getInt("id")));
            }

            rs.close();

            preparedStatement.close();

        } catch(SQLException exception) {
            exception.printStackTrace();
        }

        return entrys.size() <= 0 ? null : entrys;
    }

    @Override
    public void deleteEntry(BanEntry entry) {
        try {
            if(this.isClosed()) this.intentConnect(this.data);

            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM ban WHERE id = ?");

            preparedStatement.setInt(1, entry.getId());

            preparedStatement.execute();

            preparedStatement.close();

        } catch(SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateEntry(BanEntry entry) {
        try {
            if(this.isClosed()) this.intentConnect(this.data);

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE ban SET type = ?, createdAt = ?, finishAt = ?, author = ?, reason = ?, address = ?, finished = ? WHERE id = ?");

            preparedStatement.setInt(1, entry.getId());

            preparedStatement.setString(2, entry.getCreatedAt());

            preparedStatement.setInt(3, (int) TimeUnit.MILLISECONDS.toMinutes(entry.getFinishAt()));

            preparedStatement.setString(4, entry.getAuthor());

            preparedStatement.setString(5, entry.getReason());

            preparedStatement.setString(6, entry.getAddress());

            preparedStatement.setBoolean(7, entry.isFinished());

            preparedStatement.setString(8, entry.getName());

            preparedStatement.executeUpdate();

            preparedStatement.close();

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