package org.moonstudio.moontest.listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.jetbrains.annotations.NotNull;
import org.moonstudio.moontest.Config;
import org.moonstudio.moontest.Main;
import org.moonstudio.moontest.Messages;

import java.util.List;

import static org.moonstudio.moontest.Main.vkPostLoader;
import static org.moonstudio.moontest.Main.commandManager;

public class PluginLoaded implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void execute(PluginEnableEvent e) {
        if (e.getPlugin() == Main.instance)
            initializeCommands();
    }

    @EventHandler
    public void execute2(ServerLoadEvent e) {
        initializeCommands();
    }

    private void initializeCommands() {
        FileConfiguration config = Config.config;
        String command = config.getString("custom_command.command_name", "news");
        String permission = config.getString("custom_command.permission", "");
        List<String> aliases = config.getStringList("custom_command.aliases");

        commandManager.registerCommands(command, new Command(command) {
            @Override
            public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
                if (!commandSender.hasPermission(permission)) {
                    commandSender.sendMessage(Messages.no_permission.getMessage());
                    return true;
                }

                if (!vkPostLoader.isLoaded()) {
                    commandSender.sendMessage(Messages.not_loaded.getMessage());
                    return true;
                }

                commandSender.sendMessage(vkPostLoader.getNewPostText());
                return true;
            }
        }, permission, aliases);
    }
}
