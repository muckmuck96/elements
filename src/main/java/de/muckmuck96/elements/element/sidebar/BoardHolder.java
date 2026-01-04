package de.muckmuck96.elements.element.sidebar;

import de.muckmuck96.elements.element.placeholder.classes.Bundle;
import de.muckmuck96.elements.element.sidebar.runner.SidebarRunner;
import de.muckmuck96.elements.registry.element.PlaceholderRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Per-player sidebar instance managed by a SidebarRunner.
 */
public class BoardHolder {
    private final SidebarRunner sidebarRunner;
    private final Player player;
    private final Sidebar sideBoard;
    private final Plugin plugin;

    public BoardHolder(Plugin plugin, SidebarRunner sidebarRunner, Player player) {
        this.plugin = plugin;
        this.sidebarRunner = sidebarRunner;
        this.player = player;
        this.sideBoard = new Sidebar(plugin, player, sidebarRunner.getBoardPages().size(), sidebarRunner.isLongLine());
        sidebarRunner.registerHolder(this);
    }

    public Player getPlayer() {
        return player;
    }

    public Sidebar getSidebar() {
        return sideBoard;
    }

    public void update() {
        String title = this.sidebarRunner.getTitle().getLine();
        title = replacePlaceholders(title);
        this.sideBoard.setTitle(title.replace("&", "§"));
        int count = 0;
        for (BoardPage row : this.sidebarRunner.getBoardPages()) {
            String line = row.getLine();
            line = replacePlaceholders(line);
            this.sideBoard.setLine(count, line.replace("&", "§"));
            ++count;
        }
    }

    private String replacePlaceholders(String text) {
        PlaceholderRegistry placeholderRegistry = sidebarRunner.getPlaceholderRegistry();
        if (placeholderRegistry == null) {
            return text;
        }
        Bundle bundle = new Bundle();
        bundle.put(Player.class, player);
        text = placeholderRegistry.replaceAll(text, bundle);
        return text;
    }

    public void destroy() {
        if (player != null && player.isOnline()) {
            if (Bukkit.isPrimaryThread()) {
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            } else {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (player.isOnline()) {
                        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                    }
                });
            }
        }
    }
}
