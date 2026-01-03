package de.muckmuck96.elements.element.sidebar.runner;

import de.muckmuck96.elements.element.sidebar.BoardHolder;
import de.muckmuck96.elements.element.sidebar.BoardPage;
import de.muckmuck96.elements.element.sidebar.SidebarHolder;
import de.muckmuck96.elements.element.sidebar.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Async task that manages sidebar updates for all players.
 */
public class SidebarRunner extends BukkitRunnable {
    private final BoardPage boardPage;
    private final List<BoardPage> boardPages;
    private final List<BoardHolder> holders;
    private final SidebarHolder sidebarHolder;
    private final boolean longLine;
    private boolean isDefault;

    public SidebarRunner(Plugin plugin, SidebarHolder sidebarHolder) {
        this(plugin, sidebarHolder, true);
    }

    public SidebarRunner(Plugin plugin, SidebarHolder sidebarHolder, boolean longLine) {
        this.boardPages = new ArrayList<>();
        this.holders = new CopyOnWriteArrayList<>();
        this.isDefault = true;
        this.longLine = longLine;
        this.sidebarHolder = sidebarHolder;

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(plugin, this), plugin);

        List<String> lines = sidebarHolder.lines();
        int interval = sidebarHolder.interval();
        this.boardPage = new BoardPage(lines, interval);

        for (SidebarHolder subSidebarHolder : sidebarHolder.sidebarHolders()) {
            BoardPage page = new BoardPage(subSidebarHolder.lines(), subSidebarHolder.interval());
            this.boardPages.add(page);
        }

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            new BoardHolder(plugin, this, player);
        }
    }
    public List<BoardPage> getBoardPages() {
        return boardPages;
    }

    public BoardPage getTitle() {
        return boardPage;
    }

    public boolean isLongLine() {
        return longLine;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public SidebarHolder getSidebarHolder() {
        return sidebarHolder;
    }

    public List<BoardHolder> getHolders() {
        return holders;
    }

    public void registerHolder(BoardHolder holder) {
        this.holders.add(holder);
    }

    public void unregisterHolder(BoardHolder holder) {
        this.holders.remove(holder);
    }

    public void unregisterHolder(Player player) {
        holders.removeIf(holder -> holder.getPlayer().equals(player));
    }

    @Override
    public void run() {
        boardPage.update();
        for (BoardPage row : this.boardPages) {
            row.update();
        }
        for (BoardHolder holder : this.holders) {
            holder.update();
        }
    }
}
