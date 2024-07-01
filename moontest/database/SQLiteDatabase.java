package org.moonstudio.moontest.database;

import org.bukkit.Bukkit;
import org.moonstudio.moontest.Main;

import java.io.File;
import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SQLiteDatabase {
    private final String nameTable;
    private final LinkedBlockingQueue<Connection> connectionPool;
    private final LinkedBlockingQueue<Connection> usedConnections = new LinkedBlockingQueue<>();
    private final Set<String> cacheUsers = Collections.synchronizedSet(new HashSet<>());
    public SQLiteDatabase(String nameFile, String nameTable, int pool_size) {
        File file = new File(Main.instance.getDataFolder(), nameFile);
        String url = "jdbc:sqlite:" + file.getAbsolutePath();
        this.nameTable = nameTable;

        if (!file.exists()) {
            try {
                Class.forName("org.sqlite.JDBC").getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        connectionPool = new LinkedBlockingQueue<>(pool_size);

        for (int i = 1; i < pool_size; i++) {
            try {
                connectionPool.add(DriverManager.getConnection(url));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        createTable();
    }

    private void createTable() {
        try (PooledConnection c = getConnection();
             Statement s = c.getConnection().createStatement()) {
            s.executeUpdate( "CREATE TABLE IF NOT EXISTS `" + nameTable + "` (" +
                    "uuid VARCHAR(200) NOT NULL," +
                    "PRIMARY KEY('uuid'));");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void runActionOnExistsUser(UUID player, Runnable onExists, Runnable noExists) {
        String queueINSERT = "INSERT OR IGNORE INTO `" + nameTable + "` (`uuid`) VALUES (?)";
        try (PooledConnection c = getConnection();
             PreparedStatement s = c.getConnection().prepareStatement(queueINSERT)) {
            s.setString(1, player.toString());
            int affectsRows = s.executeUpdate();

            Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
                if (affectsRows == 0) {
                    onExists.run();
                } else {
                    noExists.run();
                }
            });

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public PooledConnection getConnection() {
        try {
            Connection connection = connectionPool.poll(10L, TimeUnit.SECONDS);
            if (connection == null) {
                throw new SQLException("Время ожидания нового соединения закончилось!");
            }
            return new PooledConnection(connection, this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Получение соединения было прервано!");
        }
    }

    void releaseConnection(Connection connection) {
        usedConnections.remove(connection);
        connectionPool.offer(connection);
    }

    public void shutDown() throws SQLException {
        while (!usedConnections.isEmpty()) {
            Connection connection = usedConnections.poll();
            if (connection != null) {
                connection.close();
            }
        }
        while (!connectionPool.isEmpty()) {
            Connection connection = connectionPool.poll();
            if (connection != null) {
                connection.close();
            }
        }
    }
}
