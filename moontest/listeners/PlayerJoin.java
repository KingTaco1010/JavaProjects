package org.moonstudio.moontest.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.moonstudio.moontest.Main;
import org.moonstudio.moontest.posts.VkPostLoader;

public class PlayerJoin implements Listener {
    @EventHandler
    public void execute(PlayerJoinEvent e) {
        VkPostLoader vkPostLoader = Main.vkPostLoader;

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
            Main.database.runActionOnExistsUser(
                    e.getPlayer().getUniqueId(),
                    () -> Main.playerManager.getRegisteredOnJoinPlayers().add(e.getPlayer().getUniqueId()),
                    () -> vkPostLoader.openBook(e.getPlayer(), vkPostLoader.getOldPostText())
            );
        });
    }
}
