package de.muckmuck96.elements.element.sidebar;

import java.util.List;

/**
 * Animated line content that cycles through frames at a set interval.
 */
public class BoardPage {
    private int interval;
    private List<String> lines;
    private String line;
    private int current;
    public boolean staticLine;
    public boolean active;
    private int count;

    public BoardPage(final List<String> lines, final int interval) {
        this.current = 0;
        this.staticLine = false;
        this.active = false;
        this.count = 0;
        this.lines = lines;
        this.interval = interval;
        if(lines.size() == 1) {
            this.staticLine = true;
        }
        if(lines.size() > 0) {
            this.line = lines.get(0).replaceAll("&", "§");
        } else {
            this.line = "";
        }
    }

    public void update() {
        if(this.staticLine) return;
        this.active = true;
        if(this.count >= this.interval) {
            this.count = 0;
            this.current = (this.current + 1) % this.lines.size();
            this.line = this.lines.get(current).replaceAll("&", "§");
        } else {
            ++this.count;
        }
    }

    public String getLine() {
        return line;
    }
}
