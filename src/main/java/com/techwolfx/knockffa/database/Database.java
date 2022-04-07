package com.techwolfx.knockffa.database;

import com.techwolfx.knockffa.Knockffa;
import com.techwolfx.knockffa.utils.ConsoleUtils;
import org.bukkit.entity.Player;

import java.sql.*;

public abstract class Database {

    Knockffa plugin;
    Connection connection;
    String dataTable;
    String dbname;

    public Database(Knockffa plugin) {
        this.plugin = plugin;
        this.dataTable = "playerData";
        this.dbname = "knockffa"; //plugin.getConfig().getString("mysql.dbname");
        this.connection = getSQLConnection();

        Initialize();
    }

    abstract Connection getSQLConnection();

    protected void Initialize() {
        createTable();
    }

    protected void createTable() {
        final String dataQuery = "CREATE TABLE IF NOT EXISTS " + dataTable + " (" +
                "uuid           VARCHAR(64) PRIMARY KEY, " +
                "username       VARCHAR(32) NOT NULL, " +
                "kills          INT(12)     NOT NULL, " +
                "deaths         INT(12)     NOT NULL, " +
                "streak         INT(12)     NOT NULL " +
                ");";

        exeUpdate(dataQuery, "Created table named '" + dataTable + "'");
    }

    public User getUser(String uuid) {
        checkConnection();
        try {
            final String query = "SELECT * FROM `"+ dataTable +"` where `uuid` = '" + uuid + "' LIMIT 1;";

            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            User user = null;

            if (resultSet.next()) {
                do {
                    user = new User(
                            resultSet.getString("uuid"),
                            resultSet.getString("username"),
                            resultSet.getInt("kills"),
                            resultSet.getInt("deaths"),
                            resultSet.getInt("streak"));
                } while (resultSet.next());
            }

            preparedStatement.close();
            resultSet.close();
            return user;
        } catch (SQLException ex) {
            ex.printStackTrace();
            //plugin.getLogger().log(Level.SEVERE, ex.getMessage());
        }
        return null;
    }

    public User registerUser(Player p) {
        checkConnection();
        try {
            Statement stmt = connection.createStatement();

            stmt.executeUpdate("INSERT INTO playerData VALUES ('"+p.getUniqueId()+"', '"+p.getName()+"', '0', '0', '0');");

            stmt.close();
            return new User(p.getUniqueId().toString(), p.getName(), 0, 0, 0);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
            //plugin.getLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public void updateUser(User user) {
        checkConnection();
        try {
            Statement stmt = connection.createStatement();

            stmt.executeUpdate("UPDATE playerData SET " +
                    "kills='"+user.getKills()+"', " +
                    "deaths='"+user.getDeaths()+"', " +
                    "streak='"+user.getStreak()+"' " +
                    "WHERE uuid='"+user.getUUID()+"';");

            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            //plugin.getLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    protected void exeUpdate(String query, String msg) {
        checkConnection();
        try {
            Statement stmt = connection.createStatement();

            stmt.executeUpdate(query);

            if (msg != null)
                ConsoleUtils.logInfo(msg);

            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            //plugin.getLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * Check if the database connection is null,
     * if yes, try initializing it with {@link #getSQLConnection()}
     */
    private void checkConnection() {
        if (this.connection == null)
            this.connection = getSQLConnection();
    }

}
