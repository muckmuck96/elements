package de.muckmuck96.elements.element.menu.container;

import de.muckmuck96.elements.element.menu.BaseMenu;
import de.muckmuck96.elements.element.menu.InventoryIncompleteException;
import de.muckmuck96.elements.element.menu.MenuClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Menu with pagination for displaying many items across multiple pages.
 */
public class PageableMenu extends BaseMenu {

    private ItemStack nextButton;
    private ItemStack previousButton;
    private int currentPage = 0;
    private List<ItemStack> items;
    private Map<Integer, ItemStack> navigationbarItems = new HashMap<>();
    private Map<Integer, ItemStack> secondNavigationbarItems = new HashMap<>();
    private NavigationPosition navigationPosition = NavigationPosition.BOTTOM;

    public PageableMenu(String name, int rows, ItemStack fillPattern, Plugin plugin) {
        super(name, rows, fillPattern, plugin);
    }

    public PageableMenu(String name, int rows, Plugin plugin) {
        super(name, rows, plugin);
    }

    public PageableMenu setNextButton(ItemStack itemStack) {
        this.nextButton = itemStack;
        return this;
    }

    public PageableMenu setPreviousButton(ItemStack itemStack) {
        this.previousButton = itemStack;
        return this;
    }

    public PageableMenu setItems(List<ItemStack> items) {
        this.items = items;
        return this;
    }

    public PageableMenu setNavigationPosition(NavigationPosition navigationPosition) {
        this.navigationPosition = navigationPosition;
        return this;
    }

    public ItemStack getNextButton() {
        return nextButton;
    }

    public ItemStack getPreviousButton() {
        return previousButton;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public Map<Integer, ItemStack> getNavigationbarItems() {
        return navigationbarItems;
    }

    public Map<Integer, ItemStack> getSecondNavigationbarItems() {
        return secondNavigationbarItems;
    }

    public NavigationPosition getNavigationPosition() {
        return navigationPosition;
    }

    public PageableMenu addNavigationItem(int relPos, ItemStack item) {
        this.navigationbarItems.put(relPos, item);
        return this;
    }

    public PageableMenu addSecondNavigationItem(int relPos, ItemStack item) {
        this.secondNavigationbarItems.put(relPos, item);
        return this;
    }

    private void resetItems() {
        int itemsPerPage = !secondNavigationbarItems.isEmpty() ? getEvents().length - 18 : getEvents().length - 9;
        for (int i = ((navigationPosition == NavigationPosition.TOP || !secondNavigationbarItems.isEmpty()) ? 9 : 0);
             i < ((navigationPosition == NavigationPosition.TOP || !secondNavigationbarItems.isEmpty()) ? itemsPerPage + 9 : itemsPerPage); i++)
            addItem(getFillPattern(), i);
        addItem(new ItemStack(getPreviousButton()),
                getNavigationPosition() == NavigationPosition.TOP ? 0 : getEvents().length - 9);
        addItem(new ItemStack(getNextButton()),
                getNavigationPosition() == NavigationPosition.TOP ? 8 : getEvents().length - 1);
        getNavigationbarItems().forEach((pos, item) -> {
            addItem(item, getNavigationPosition() == NavigationPosition.TOP
                    ? 1 + Math.min(pos, 6)
                    : getEvents().length - 8 + Math.min(pos, 6));
        });
        getSecondNavigationbarItems().forEach((pos, item) -> {
            addItem(item, getNavigationPosition() == NavigationPosition.BOTTOM
                    ? 1 + Math.min(pos, 6)
                    : getEvents().length - 8 + Math.min(pos, 6));
        });
    }

    public int getMaxPageCount() {
        double itemsPerPage = !secondNavigationbarItems.isEmpty() ? getEvents().length - 18 : getEvents().length - 9;
        return (int) Math.floor((double) getItems().size() / itemsPerPage);
    }

    public PageableMenu loadPage(int page) {
        resetItems();
        this.currentPage = page;

        int itemsPerPage = !secondNavigationbarItems.isEmpty() ? getEvents().length - 18 : getEvents().length - 9;
        int startIndex = page * itemsPerPage;
        for (int i = ((navigationPosition == NavigationPosition.TOP || !secondNavigationbarItems.isEmpty()) ? 9 : 0);
             i < ((navigationPosition == NavigationPosition.TOP || !secondNavigationbarItems.isEmpty()) ? itemsPerPage + 9 : itemsPerPage); i++) {
            if (getItems().size() <= (startIndex + ((navigationPosition == NavigationPosition.TOP || !secondNavigationbarItems.isEmpty()) ? i - 9 : i)) || startIndex < 0)
                continue;
            ItemStack item = getItems().get(startIndex + ((navigationPosition == NavigationPosition.TOP || !secondNavigationbarItems.isEmpty()) ? i - 9 : i));
            addItem(item, i);
        }
        return this;
    }

    @Override
    public void open(Player player) {
        if (nextButton == null)
            throw new InventoryIncompleteException("setNextButton() must be called before opening!");
        if (previousButton == null)
            throw new InventoryIncompleteException("setPreviousButton() must be called before opening!");
        if (items == null)
            throw new InventoryIncompleteException("setItems() must be called before opening!");
        loadPage(currentPage);
        super.open(player);
    }

    @Override
    public void dispose() {
        nextButton = null;
        previousButton = null;
        navigationPosition = null;
        if (navigationbarItems != null)
            navigationbarItems.clear();
        navigationbarItems = null;
        if (items != null)
            items.clear();
        items = null;
        super.dispose();
    }

    @EventHandler
    public void onMenuClick(MenuClickEvent event) {
        if (!event.getInventory().equals(getInventory())) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;

        ItemStack prev = getPreviousButton();
        ItemStack next = getNextButton();

        if (prev != null && prev.isSimilar(clicked)) {
            loadPage(Math.max(getCurrentPage() - 1, 0));
        } else if (next != null && next.isSimilar(clicked)) {
            loadPage(Math.min(getCurrentPage() + 1, getMaxPageCount()));
        }
    }

    public enum NavigationPosition {
        TOP,
        BOTTOM
    }

}