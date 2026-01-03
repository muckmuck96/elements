package de.muckmuck96.elements.registry.element;

import de.muckmuck96.elements.element.sidebar.SidebarHolder;
import de.muckmuck96.elements.element.sidebar.runner.SidebarRunner;
import de.muckmuck96.elements.registry.ElementRegistry;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SidebarRegistry extends ElementRegistry {
    private final List<SidebarRunner> sidebarRunners = new CopyOnWriteArrayList<>();
    private final Plugin plugin;

    public SidebarRegistry(Plugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public SidebarRunner newRunner(SidebarHolder sidebarHolder, boolean isDefault) {
        SidebarRunner sidebarRunner = new SidebarRunner(plugin, sidebarHolder);
        sidebarRunner.runTaskTimerAsynchronously(plugin, 1L, 1L);
        sidebarRunners.add(sidebarRunner);
        sidebarRunner.setDefault(isDefault);
        return sidebarRunner;
    }

    public void removeRunner(SidebarRunner runner) {
        sidebarRunners.remove(runner);
        runner.cancel();
    }

    public List<SidebarRunner> getSidebarRunners() {
        return Collections.unmodifiableList(sidebarRunners);
    }

    public void clearRunners() {
        for (SidebarRunner runner : sidebarRunners) {
            runner.cancel();
        }
        sidebarRunners.clear();
    }
}
