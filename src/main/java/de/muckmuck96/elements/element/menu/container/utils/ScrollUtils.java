package de.muckmuck96.elements.element.menu.container.utils;

public class ScrollUtils {

    public static int getScrollbarLength(int rowCount) {
        if (rowCount <= 6)
            return -1;
        switch (rowCount) {
            case 7: return 3;
            case 8: return 2;
            default: return 1;
        }
    }

    public static int getScrollbarStart(int scrollbarLength, int rowCount, int currScroll) {
        if (rowCount <= 6) {
            return 0;
        }
        double scrolls = rowCount - 6;
        double progress = (double) currScroll / scrolls;
        switch (scrollbarLength) {
            case 3: {
                if (progress >= 0.5)
                    return 2;
                return 1;
            }
            case 2: {
                if (progress >= 0.333 && progress < 0.666)
                    return 2;
                if (progress >= 0.666)
                    return 3;
                return 1;
            }
            case 1: {
                if (progress >= 0.25 && progress < 0.5)
                    return 2;
                if (progress >= 0.5 && progress < 0.75)
                    return 3;
                if (progress >= 0.75)
                    return 4;
                return 1;
            }
        }
        return 0;
    }

    public enum ScrollbarPosition {
        LEFT,
        RIGHT
    }
}