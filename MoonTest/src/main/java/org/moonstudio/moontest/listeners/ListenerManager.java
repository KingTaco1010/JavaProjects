package org.moonstudio.moontest.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.moonstudio.moontest.Main;

public class ListenerManager {
    public void register(Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, Main.instance);
    }
}
