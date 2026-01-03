package de.muckmuck96.elements.registry.element;

import de.muckmuck96.elements.element.timings.Countdown;
import de.muckmuck96.elements.registry.ElementRegistry;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry for managing Countdown instances with automatic lifecycle management.
 * <p>
 * Provides centralized tracking of all active countdowns and automatic
 * cancellation on plugin disable.
 * </p>
 *
 * <pre>
 * // Enable the registry
 * CountdownRegistry countdowns = registry.enable(CountdownRegistry.class);
 *
 * // Create a countdown (auto-registered)
 * Countdown countdown = countdowns.createCountdown(10, 1,
 *     () -&gt; { System.out.println("Tick!"); return null; },
 *     () -&gt; { System.out.println("Done!"); return null; }
 * );
 *
 * // Cancel all on plugin disable
 * countdowns.cancelAllCountdowns();
 * </pre>
 */
public class CountdownRegistry extends ElementRegistry {
    private final List<Countdown> activeCountdowns = new CopyOnWriteArrayList<>();
    private final Plugin plugin;

    public CountdownRegistry(Plugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    /**
     * Creates and starts a new countdown that is automatically tracked.
     *
     * @param countFrom      the starting count value
     * @param steps          the decrement per tick
     * @param executePerStep callback executed each tick (nullable)
     * @param executeAfter   callback executed on completion (nullable)
     * @return the created Countdown instance
     */
    public Countdown createCountdown(int countFrom, int steps,
                                     Callable<Void> executePerStep,
                                     Callable<Void> executeAfter) {
        Countdown countdown = new Countdown(plugin, this, countFrom, steps, executePerStep, executeAfter);
        activeCountdowns.add(countdown);
        countdown.start();
        return countdown;
    }

    /**
     * Registers an existing countdown with this registry.
     *
     * @param countdown the countdown to register
     */
    public void registerCountdown(Countdown countdown) {
        if (countdown != null && !activeCountdowns.contains(countdown)) {
            activeCountdowns.add(countdown);
        }
    }

    /**
     * Unregisters a countdown from this registry.
     * Called automatically when a countdown completes or is cancelled.
     *
     * @param countdown the countdown to unregister
     */
    public void unregisterCountdown(Countdown countdown) {
        activeCountdowns.remove(countdown);
    }

    /**
     * Cancels a specific countdown.
     *
     * @param countdown the countdown to cancel
     */
    public void cancelCountdown(Countdown countdown) {
        if (countdown != null) {
            countdown.cancel();
            activeCountdowns.remove(countdown);
        }
    }

    /**
     * Gets all active countdowns.
     *
     * @return unmodifiable list of active countdowns
     */
    public List<Countdown> getActiveCountdowns() {
        return Collections.unmodifiableList(activeCountdowns);
    }

    /**
     * Gets the number of active countdowns.
     *
     * @return count of active countdowns
     */
    public int getCountdownCount() {
        return activeCountdowns.size();
    }

    /**
     * Checks if there are any active countdowns.
     *
     * @return true if there are active countdowns
     */
    public boolean hasActiveCountdowns() {
        return !activeCountdowns.isEmpty();
    }

    /**
     * Cancels all active countdowns.
     * Call this in your plugin's onDisable() method.
     */
    public void cancelAllCountdowns() {
        List<Countdown> countdownsCopy = List.copyOf(activeCountdowns);
        for (Countdown countdown : countdownsCopy) {
            try {
                countdown.cancel();
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to cancel countdown: " + e.getMessage());
            }
        }
        activeCountdowns.clear();
    }
}
