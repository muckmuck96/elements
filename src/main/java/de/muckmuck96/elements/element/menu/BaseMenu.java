package de.muckmuck96.elements.element.menu;

import de.muckmuck96.elements.Elements;
import de.muckmuck96.elements.registry.element.MenuRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public abstract class BaseMenu implements Listener {
    private static final int HOPPER_SIZE = 5;

    private ItemStack[] events;
    private ItemStack fillPattern;
    private Inventory inventory;
    private MenuHolder holder;
    private String name;
    private boolean disposeOnClose = true;
    private boolean allowIO;
    private Plugin plugin;
    private boolean autoRegistered = false;

    public BaseMenu(String name, Plugin plugin) {
        this(name, new ItemStack(Material.AIR), plugin);
    }

    public BaseMenu(String name, ItemStack fillPattern, Plugin plugin) {
        this.plugin = plugin;
        this.fillPattern = fillPattern;
        this.name = name;
        this.events = new ItemStack[HOPPER_SIZE];
        this.holder = new MenuHolder(this);

        this.createInventory();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        autoRegister();
    }

    public BaseMenu(String name, int rows, Plugin plugin) {
        this(name, rows, new ItemStack(Material.AIR), plugin);
    }

    public BaseMenu(String name, int rows, ItemStack fillPattern, Plugin plugin) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Rows must be between 1 and 6, got: " + rows);
        }
        this.plugin = plugin;
        this.fillPattern = fillPattern;
        this.name = name;
        this.events = new ItemStack[9 * rows];
        this.holder = new MenuHolder(this);

        this.createInventory();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        autoRegister();
    }

    private void autoRegister() {
        if (plugin == null) return;

        Elements.getRegistry(plugin).ifPresent(registry -> {
            registry.getElementRegistry(MenuRegistry.class).ifPresent(menuRegistry -> {
                menuRegistry.registerMenu(this);
                autoRegistered = true;
            });
        });
    }

    public void resize(int rows) {
        if (rows == 0) {
            this.events = new ItemStack[HOPPER_SIZE];
        } else {
            if (rows < 1 || rows > 6) {
                throw new IllegalArgumentException("Rows must be between 1 and 6, got: " + rows);
            }
            this.events = new ItemStack[rows * 9];
        }

        this.createInventory();
    }

    private void createInventory() {
        Component title = LegacyComponentSerializer.legacyAmpersand().deserialize(name);
        if (events.length == HOPPER_SIZE) {
            this.inventory = Bukkit.createInventory(holder, InventoryType.HOPPER, title);
        } else {
            this.inventory = Bukkit.createInventory(holder, events.length, title);
        }
        holder.setInventory(this.inventory);

        for (int i = 0; i < inventory.getSize(); i++) {
            this.addItem(fillPattern, i);
        }
    }

    public BaseMenu addItem(ItemStack itemStack, int x, int y) {
        return addItem(itemStack, x + y * 9);
    }

    public BaseMenu addItem(ItemStack itemStack, int position) {
        inventory.setItem(position, itemStack);
        events[position] = itemStack;
        return this;
    }

    public BaseMenu setItems(ItemStack[] items) {
        inventory.setContents(items);
        return this;
    }

    public void clearInventory() {
        Arrays.fill(events, null);

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }
    }

    public void open(Player player) {
        player.closeInventory();
        player.openInventory(inventory);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent e) {
        if (this.inventory == null)
            return;
        if (!e.getInventory().equals(this.inventory))
            return;
        if (!disposeOnClose)
            return;
        dispose();
    }

    public void dispose() {
        // Unregister from MenuRegistry if auto-registered
        if (autoRegistered && plugin != null) {
            Elements.getRegistry(plugin).ifPresent(registry -> {
                registry.getElementRegistry(MenuRegistry.class).ifPresent(menuRegistry -> {
                    menuRegistry.unregisterMenu(this);
                });
            });
            autoRegistered = false;
        }

        HandlerList.unregisterAll(this);
        this.events = null;
        this.fillPattern = null;
        if (this.inventory != null)
            this.inventory.clear();
        this.inventory = null;
        this.name = null;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public boolean isAutoRegistered() {
        return autoRegistered;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (this.inventory == null)
            return;
        Player player = (Player) e.getWhoClicked();

        if (e.getClickedInventory() == null)
            return;
        boolean watching = this.inventory.getViewers().stream()
                .map(Entity::getUniqueId)
                .anyMatch(uuid -> uuid.equals(e.getWhoClicked().getUniqueId()));

        if (e.getClickedInventory().equals(this.inventory)) {
            if (e.getSlot() == e.getRawSlot() && e.getSlot() < this.events.length && events[e.getSlot()] == null)
                return;
            e.setResult(Event.Result.DENY);

            if (e.getCurrentItem() != null && e.getSlot() == e.getRawSlot() && e.getSlot() < this.events.length) {
                ItemStack be = this.events[e.getSlot()];
                if (be != null) {
                    MenuClickEvent mce = new MenuClickEvent(e.getView(), e.getSlotType(), e.getSlot(), e.getClick(), e.getAction());
                    try {
                        Bukkit.getPluginManager().callEvent(mce);
                    } catch (IllegalStateException ex) {
                        Bukkit.getLogger().warning("Failed to dispatch MenuClickEvent: " + ex.getMessage());
                    }
                }
            }
            player.updateInventory();
        }
        if (watching && e.getResult() != Event.Result.DENY) {
            if (isAllowIO())
                return;
            if (e.isShiftClick() || e.getClick() == ClickType.DOUBLE_CLICK) {
                e.setResult(Event.Result.DENY);
                player.updateInventory();
            }
        }
    }

    public ItemStack[] getEvents() {
        return this.events;
    }

    public ItemStack getFillPattern() {
        return this.fillPattern;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public String getName() {
        return this.name;
    }

    public void setDisposeOnClose(boolean disposeOnClose) {
        this.disposeOnClose = disposeOnClose;
    }

    public boolean isDisposingOnClose() {
        return disposeOnClose;
    }

    public boolean isAllowIO() {
        return allowIO;
    }

    public void setAllowIO(boolean allowIO) {
        this.allowIO = allowIO;
    }
}
