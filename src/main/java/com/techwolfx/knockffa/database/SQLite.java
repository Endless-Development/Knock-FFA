package com.techwolfx.knockffa.database;

import com.techwolfx.knockffa.Knockffa;
import com.techwolfx.knockffa.utils.ConsoleUtils;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLite extends Database {

    public SQLite(Knockffa plugin) {
        super(plugin);
        ConsoleUtils.coloredLog("Initialized SQLite Database", ChatColor.GREEN);
    }

    /**
     * Establish a connection with SQLite database.
     *
     * @return connection to database
     */
    @Override
    protected Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()) {
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    @Override
    protected void Initialize() {
        createTable();
    }

}
