package org.moonstudio.moontest.commands;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.moonstudio.moontest.Config;
import org.moonstudio.moontest.Messages;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.moonstudio.moontest.Main.commandManager;
import static org.moonstudio.moontest.Main.vkPostLoader;

public class CommandManager {
    private final Map<String, Command> knownCommands;

    public CommandManager() throws NoSuchFieldException, IllegalAccessException {
        knownCommands = getKnownCommands();
    }

    public void registerCommands(String nameCommand, Command command) {
        knownCommands.put(nameCommand, command);
    }

    public void registerCommands(String nameCommand, Command command, String perm) {
        if (perm != null && !perm.isEmpty())
            command.setPermission(perm);
        registerCommands(nameCommand, command);
    }

    public void registerCommands(String nameCommand, Command command, String perm, List<String> aliases) {
        if (!aliases.isEmpty())
            command.setAliases(aliases);
        registerCommands(nameCommand, command, perm);
    }

    public Map<String, Command> getKnownCommands() throws NoSuchFieldException, IllegalAccessException {
        if (knownCommands == null) {
            Field knownCommands = null;
            try {
                Server server = Bukkit.getServer();
                Field bukkitCommandMap = server.getClass().getDeclaredField("commandMap");
                bukkitCommandMap.setAccessible(true);

                SimpleCommandMap commandMap = (SimpleCommandMap) bukkitCommandMap.get(server);

                knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommands.setAccessible(true);
                return (Map<String, Command>) knownCommands.get(commandMap);
            } finally {
                if (knownCommands != null)
                    knownCommands.setAccessible(false);
            }
        }

        return knownCommands;
    }

    public void initializeCommands() {
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
