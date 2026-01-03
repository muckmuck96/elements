package de.muckmuck96.elements.element.menu.container;

import de.muckmuck96.elements.element.menu.container.utils.ScrollUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Extended ScrollableMenu that saves changes back to the original list.
 */
public class ScrollableContainer extends ScrollableMenu {

    private Consumer<ScrollableContainer> onClose;

    public ScrollableContainer(String name, int rows, Plugin plugin) {
        super(name, rows, plugin);
    }

    public Consumer<ScrollableContainer> getOnClose() {
        return onClose;
    }

    public ScrollableContainer setContainerItems(List<ItemStack> items) {
        List<ItemStack> eItems = new ArrayList<>();
        items.forEach(itemStack -> {
            if (itemStack == null)
                eItems.add(null);
            else
                eItems.add(new ItemStack(itemStack));
        });
        setContent(eItems);
        return this;
    }

    private void saveItems(int lastScroll) {
        if (getInventory() == null || getContent() == null) return;

        boolean scrollbar = getRowCount() > 6;
        int startIndex = scrollbar && getScrollbarPosition() == ScrollUtils.ScrollbarPosition.LEFT ? 1 : 0;
        boolean left = scrollbar && getScrollbarPosition() == ScrollUtils.ScrollbarPosition.LEFT;
        int itemIndex = Math.max((8 * lastScroll), 0);
        for (int i = startIndex; i < getEvents().length; i++) {
            if (scrollbar && i > 0 && (i + (left ? 0 : 1)) % 9 == 0)
                continue;
            if (itemIndex >= getContent().length) break;
            ItemStack item = getInventory().getItem(i);
            this.getContent()[itemIndex] = item != null ? new ItemStack(item) : null;
            itemIndex++;
        }
    }

    @Override
    public void update(int lastScroll) {
        setAllowIO(false);
        saveItems(lastScroll);
        super.update(lastScroll);
        setAllowIO(true);
    }

    public ScrollableContainer onClose(Consumer<ScrollableContainer> callBack) {
        this.onClose = callBack;
        return this;
    }

    @Override
    public void dispose() {
        saveItems(getCurrentScroll());
        setAllowIO(false);
        if (onClose != null)
            onClose.accept(this);
        onClose = null;
        super.dispose();
    }
}
