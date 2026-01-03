package de.muckmuck96.elements.element.timings;

import de.muckmuck96.elements.Elements;
import de.muckmuck96.elements.registry.element.CountdownRegistry;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.Callable;
import java.util.logging.Level;

public class Countdown {
    private final Plugin plugin;
    private final CountdownRegistry registry;
    private final int countFrom;
    private final int steps;
    private final Callable<Void> executePerStep;
    private final Callable<Void> executeAfter;
    private BukkitTask task;
    private boolean cancelled = false;
    private boolean autoRegistered = false;

    /**
     * Creates a countdown without auto-registration.
     * You must manually register this countdown with CountdownRegistry.
     */
    public Countdown(Plugin plugin, int countFrom, int steps, Callable<Void> executePerStep, Callable<Void> executeAfter) {
        this(plugin, null, countFrom, steps, executePerStep, executeAfter);
        autoRegister();
    }

    /**
     * Creates a countdown managed by a CountdownRegistry.
     * Used internally by CountdownRegistry.createCountdown().
     */
    public Countdown(Plugin plugin, CountdownRegistry registry, int countFrom, int steps,
                     Callable<Void> executePerStep, Callable<Void> executeAfter) {
        if (steps <= 0) {
            throw new IllegalArgumentException("Steps must be positive");
        }
        if (countFrom < 0) {
            throw new IllegalArgumentException("countFrom must be non-negative");
        }
        this.plugin = plugin;
        this.registry = registry;
        this.countFrom = countFrom;
        this.steps = steps;
        this.executePerStep = executePerStep;
        this.executeAfter = executeAfter;
    }

    private void autoRegister() {
        if (plugin == null) return;

        Elements.getRegistry(plugin).ifPresent(reg -> {
            reg.getElementRegistry(CountdownRegistry.class).ifPresent(countdownRegistry -> {
                countdownRegistry.registerCountdown(this);
                autoRegistered = true;
            });
        });
    }

    public void start() {
        if (cancelled) return;

        final int[] cursor = {countFrom};
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (cancelled) {
                    cancel();
                    return;
                }
                try {
                    if (executePerStep != null) {
                        executePerStep.call();
                    }
                    cursor[0] -= steps;
                    if (cursor[0] <= 0) {
                        if (executeAfter != null) {
                            executeAfter.call();
                        }
                        onComplete();
                        cancel();
                    }
                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Error in countdown task", e);
                    onComplete();
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L, 20L);
    }

    /**
     * Cancels this countdown.
     */
    public void cancel() {
        if (cancelled) return;
        cancelled = true;

        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
        onComplete();
    }

    private void onComplete() {
        if (registry != null) {
            registry.unregisterCountdown(this);
        } else if (autoRegistered && plugin != null) {
            Elements.getRegistry(plugin).ifPresent(reg -> {
                reg.getElementRegistry(CountdownRegistry.class).ifPresent(countdownRegistry -> {
                    countdownRegistry.unregisterCountdown(this);
                });
            });
        }
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public int getCountFrom() {
        return countFrom;
    }

    public int getSteps() {
        return steps;
    }
}
