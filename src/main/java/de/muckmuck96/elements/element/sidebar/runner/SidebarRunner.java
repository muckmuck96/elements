package de.muckmuck96.elements.element.sidebar.runner;

import de.muckmuck96.elements.element.sidebar.BoardHolder;
import de.muckmuck96.elements.element.sidebar.BoardPage;
import de.muckmuck96.elements.element.sidebar.SidebarHolder;
import de.muckmuck96.elements.element.sidebar.listener.PlayerListener;
import de.muckmuck96.elements.registry.element.PlaceholderRegistry;
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
    private PlaceholderRegistry placeholderRegistry;

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

        // First line is the title
        if (!lines.isEmpty()) {
            this.boardPage = new BoardPage(List.of(lines.get(0)), interval);

            // Remaining lines are the content
            for (int i = 1; i < lines.size(); i++) {
                BoardPage page = new BoardPage(List.of(lines.get(i)), interval);
                this.boardPages.add(page);
            }
        } else {
            this.boardPage = new BoardPage(List.of(""), interval);
        }

        // Also add any nested sidebar holders (for animated content)
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

    public PlaceholderRegistry getPlaceholderRegistry() {
        return placeholderRegistry;
    }

    public void setPlaceholderRegistry(PlaceholderRegistry placeholderRegistry) {
        this.placeholderRegistry = placeholderRegistry;
    }

    public void destroy() {
        for (BoardHolder holder : holders) {
            holder.destroy();
        }
        holders.clear();
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
