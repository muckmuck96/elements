package de.muckmuck96.elements.element.sidebar.listener;

import de.muckmuck96.elements.element.sidebar.BoardHolder;
import de.muckmuck96.elements.element.sidebar.runner.SidebarRunner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PlayerListener implements Listener {
    private SidebarRunner sidebarRunner;
    private Plugin plugin;


    public PlayerListener(Plugin plugin, SidebarRunner sidebarRunner) {
        this.sidebarRunner = sidebarRunner;
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if(sidebarRunner != null && sidebarRunner.isDefault()) {
            new BoardHolder(plugin, sidebarRunner, player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        if(sidebarRunner == null) return;
        sidebarRunner.unregisterHolder(e.getPlayer());
        e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
