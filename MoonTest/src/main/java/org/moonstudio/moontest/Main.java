package org.moonstudio.moontest;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.moonstudio.moontest.commands.CommandManager;
import org.moonstudio.moontest.database.SQLiteDatabase;
import org.moonstudio.moontest.listeners.ListenerManager;
import org.moonstudio.moontest.listeners.PlayerJoin;
import org.moonstudio.moontest.managers.PlayerManager;
import org.moonstudio.moontest.posts.PostParser;
import org.moonstudio.moontest.posts.VkPostLoader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public final class Main extends JavaPlugin {
    public static Main instance;
    public static SQLiteDatabase database;
    public static ListenerManager listenerManager;
    public static PlayerManager playerManager;
    public static VkPostLoader vkPostLoader;
    public static CommandManager commandManager;

    @Override
    public void onEnable() {
        try {
            instance = this;
            initializeDatabase();
            listenerManager = new ListenerManager();
            playerManager = new PlayerManager();
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                try {
                    vkPostLoader = new VkPostLoader(new PostParser(Config.config.getString("VK_settings.url")));
                    registerListeners();
                } catch (Exception e) {
                    Bukkit.getPluginManager().disablePlugin(this);
                    throw new RuntimeException(e);
                }
            });
            commandManager = new CommandManager();
            commandManager.initializeCommands();
        } catch (Exception e) {
            Bukkit.getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }
    }

    private void initializeDatabase() {
        FileConfiguration config = Config.config;
        database = new SQLiteDatabase(
            config.getString("SQLite_database.file_name", "database.db"),
            config.getString("SQLite_database.nameTable", "registrations"),
            config.getInt("SQLite_database.connection_pool_size", 5)
        );
    }

    private void registerListeners() {
        listenerManager.register(new PlayerJoin());
    }

    @Override
    public void onDisable() {
        try {
            database.shutDown();
            Bukkit.getScheduler().getActiveWorkers().forEach(w -> w.getThread().interrupt());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void runTimerCheck() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                vkPostLoader.refreshPosts();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String newPost = vkPostLoader.checkNewPost();
            if (vkPostLoader.saveIfNewPost(newPost)) {
                Set<UUID> set = playerManager.getRegisteredOnJoinPlayers();
                Iterator<UUID> iterator = set.iterator();

                while (iterator.hasNext() && !Thread.currentThread().isInterrupted()) {
                    Player player = Bukkit.getPlayer(iterator.next());
                    if (player == null) {
                        iterator.remove();
                        continue;
                    }
                    if (player.isOnline()) {
                        vkPostLoader.openBook(player, vkPostLoader.getNewPostText());
                        iterator.remove();
                    }
                }
            }
        }, 0L, 20L * Config.config.getLong("VK_settings.interval_check", 60L));
    }
}
