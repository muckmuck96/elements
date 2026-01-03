package de.muckmuck96.elements.element.menu.container;

import de.muckmuck96.elements.element.button.ItemBuilder;
import de.muckmuck96.elements.element.menu.BaseMenu;
import de.muckmuck96.elements.element.menu.InventoryIncompleteException;
import de.muckmuck96.elements.element.menu.MenuClickEvent;
import de.muckmuck96.elements.element.menu.container.utils.ScrollUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Menu with vertical scrolling for long item lists.
 */
public class ScrollableMenu extends BaseMenu {

    private int rowCount;
    private int currentScroll;

    private ItemStack[] content;
    private ItemStack scrollDownButton;
    private ItemStack scrollUpButton;
    private ScrollUtils.ScrollbarPosition scrollbarPosition = ScrollUtils.ScrollbarPosition.RIGHT;
    
    public ScrollableMenu(String name, int rows, Plugin plugin) {
        super(name, rows, plugin);
    }

    public ScrollableMenu setContent(List<ItemStack> items) {
        this.content = new ItemStack[9 * rowCount];
        for (int i = 0; i < items.size(); i++)
            this.content[i] = items.get(i);
        return this;
    }

    public ScrollableMenu setScrollDownButton(ItemStack itemStack) {
        this.scrollDownButton = itemStack;
        return this;
    }

    public ScrollableMenu setScrollUpButton(ItemStack itemStack) {
        this.scrollUpButton = itemStack;
        return this;
    }

    public ScrollableMenu setScrollbarPosition(ScrollUtils.ScrollbarPosition scrollbarPosition) {
        this.scrollbarPosition = scrollbarPosition;
        return this;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getCurrentScroll() {
        return currentScroll;
    }

    public void setCurrentScroll(int currentScroll) {
        this.currentScroll = currentScroll;
    }

    public ItemStack[] getContent() {
        return content;
    }

    public void setContent(ItemStack[] content) {
        this.content = content;
    }

    public ItemStack getScrollDownButton() {
        return scrollDownButton;
    }

    public ItemStack getScrollUpButton() {
        return scrollUpButton;
    }

    public ScrollUtils.ScrollbarPosition getScrollbarPosition() {
        return scrollbarPosition;
    }

    public void update(int lastScroll) {
        init();
    }

    private void init() {
        for (int i = 0; i < getEvents().length; i++)
            addItem(getFillPattern(), i);
        boolean scrollbar = false;

        //Add Buttons
        if (getRowCount() > 6) {
            scrollbar = true;
            if (getScrollbarPosition() == ScrollUtils.ScrollbarPosition.RIGHT) {
                addItem(new ItemStack(getScrollUpButton()), 8);
                addItem(new ItemStack(getScrollDownButton()), 53);
            } else {
                addItem(new ItemStack(getScrollUpButton()), 0);
                addItem(new ItemStack(getScrollDownButton()), 45);
            }

            int scrollbarStart = ScrollUtils.getScrollbarStart(ScrollUtils.getScrollbarLength(getRowCount()), getRowCount(), getCurrentScroll()) - 1;

            //Prevent scrollbar changes
            int start = getScrollbarPosition() == ScrollUtils.ScrollbarPosition.LEFT ? 9 : 8;
            int curr = 0;
            for (int i = start; i < getEvents().length; i++) {
                if ((i + (getScrollbarPosition() == ScrollUtils.ScrollbarPosition.RIGHT ? 1 : 0)) % 9 == 0
                        && (getInventory().getItem(i) == null || getInventory().getItem(i).getType() == Material.AIR)) {
                    if (curr >= scrollbarStart && curr < (scrollbarStart + ScrollUtils.getScrollbarLength(getRowCount())))
                        addItem(new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).name(" ").build(), i);
                    else
                        addItem(new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name(" ").build(), i);
                    curr++;
                }
            }
        }

        //Add Content
        int startIndex = scrollbar && getScrollbarPosition() == ScrollUtils.ScrollbarPosition.LEFT ? 1 : 0;
        boolean left = scrollbar && getScrollbarPosition() == ScrollUtils.ScrollbarPosition.LEFT;
        int itemIndex = getCurrentScroll() * (scrollbar ? 8 : 9);
        for (int i = startIndex; i < getEvents().length; i++) {
            if (scrollbar && i > 0 && (i + (left ? 0 : 1)) % 9 == 0)
                continue;
            if (getContent().length > itemIndex) {
                if (getContent()[itemIndex] != null)
                    addItem(getContent()[itemIndex], i);
                else
                    addItem(new ItemStack(getFillPattern()), i);
                itemIndex++;
            } else
                addItem(new ItemStack(getFillPattern()), i);
        }
    }

    @Override
    public void open(Player player) {
        if (scrollUpButton == null)
            throw new InventoryIncompleteException("setScrollUpButton() must be called before opening!");
        if (scrollDownButton == null)
            throw new InventoryIncompleteException("setScrollDownButton() must be called before opening!");
        init();
        super.open(player);
    }

    @Override
    public void dispose() {
        setAllowIO(false);
        scrollUpButton = null;
        scrollDownButton = null;
        scrollbarPosition = null;
        content = null;
        super.dispose();
    }



    @EventHandler
    public void onMenuClick(MenuClickEvent event) {
        if (!event.getInventory().equals(getInventory())) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;

        ItemStack down = getScrollDownButton();
        ItemStack up = getScrollUpButton();

        if (down != null && down.isSimilar(clicked)) {
            int lastScroll = getCurrentScroll();
            setCurrentScroll(Math.min(getCurrentScroll() + 1, getRowCount() - 6));
            update(lastScroll);
        } else if (up != null && up.isSimilar(clicked)) {
            int lastScroll = getCurrentScroll();
            setCurrentScroll(Math.max(getCurrentScroll() - 1, 0));
            update(lastScroll);
        }
    }
}
