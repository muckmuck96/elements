package de.muckmuck96.elements.element.sidebar;

import de.muckmuck96.elements.element.sidebar.runner.SidebarRunner;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Per-player sidebar instance managed by a SidebarRunner.
 */
public class BoardHolder {
    private final SidebarRunner sidebarRunner;
    private final Player player;
    private final Sidebar sideBoard;

    public BoardHolder(Plugin plugin, SidebarRunner sidebarRunner, Player player) {
        this.sidebarRunner = sidebarRunner;
        this.player = player;
        this.sideBoard = new Sidebar(plugin, player, sidebarRunner.getBoardPages().size(), sidebarRunner.isLongLine());
        sidebarRunner.registerHolder(this);
    }

    public Player getPlayer() {
        return player;
    }

    public void update() {
        this.sideBoard.setTitle(this.sidebarRunner.getTitle().getLine());
        int count = 0;
        for (BoardPage row : this.sidebarRunner.getBoardPages()) {
            String line = row.getLine();
            this.sideBoard.setLine(count, line.replace("&", "§"));
            ++count;
        }
    }
}
