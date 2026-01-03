package de.muckmuck96.elements.element.sidebar;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Scoreboard-based sidebar display for a player.
 */
public class Sidebar {
    private static final int MAX_LINES = 15;

    private final Player player;
    private final Scoreboard board;
    private final Objective objective;
    private final int lineCount;
    private final boolean longLine;
    private final Map<Integer, String> cache;
    private final String[] entries;

    public Sidebar(Plugin plugin, Player player, int lineCount) {
        this(plugin, player, lineCount, true);
    }

    public Sidebar(Plugin plugin, Player player, int lineCount, boolean longLine) {
        if (lineCount < 0) {
            throw new IllegalArgumentException("Line count cannot be negative");
        }
        if (lineCount > MAX_LINES) {
            throw new IllegalArgumentException("Line count cannot exceed " + MAX_LINES);
        }
        this.cache = new ConcurrentHashMap<>();
        this.player = player;
        this.lineCount = lineCount;
        this.longLine = longLine;
        this.entries = new String[lineCount];
        this.board = plugin.getServer().getScoreboardManager().getNewScoreboard();
        this.objective = this.board.registerNewObjective("elements_sb", Criteria.DUMMY, Component.text("..."));
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int score = lineCount;
        for (int i = 0; i < lineCount; ++i) {
            Team t = this.board.registerNewTeam("line_" + i);
            String entry = "§" + Integer.toHexString(i) + "§r";
            entries[i] = entry;
            t.addEntry(entry);
            this.objective.getScore(entry).setScore(score);
            --score;
        }
        this.player.setScoreboard(this.board);
    }

    public Scoreboard getBoard() {
        return board;
    }

    public void setTitle(String string) {
        if (string == null) {
            string = "";
        }
        if (this.cache.containsKey(-1) && this.cache.get(-1).equals(string)) {
            return;
        }
        this.cache.put(-1, string);
        this.objective.displayName(LegacyComponentSerializer.legacySection().deserialize(string));
    }

    public void setTitle(Component component) {
        this.objective.displayName(component);
    }

    public void setLine(int line, String string) {
        Team t = this.board.getTeam("line_" + line);
        if (string == null) {
            string = "";
        }
        if (this.cache.containsKey(line) && this.cache.get(line).equals(string)) {
            return;
        }
        this.cache.put(line, string);

        if (longLine) {
            string = this.prep(string);
        } else {
            string = this.prepForShortline(string);
        }

        ArrayList<String> parts;
        if (longLine) {
            parts = this.convertIntoPieces(string, 64);
        } else {
            parts = this.convertIntoPieces(string, 16);
        }

        if (t != null) {
            t.prefix(LegacyComponentSerializer.legacySection().deserialize(this.fixAnyIssues(parts.get(0))));
            t.suffix(LegacyComponentSerializer.legacySection().deserialize(this.fixAnyIssues(parts.get(1))));
        }
    }

    public void setLine(int line, Component component) {
        Team t = this.board.getTeam("line_" + line);
        if (t != null) {
            t.prefix(component);
            t.suffix(Component.empty());
        }
    }

    private String fixAnyIssues(String part) {
        if (longLine) {
            return part;
        }
        if (part.length() > 16) {
            return part.substring(0, 16);
        }
        return part;
    }

    private String prep(String color) {
        ArrayList<String> parts;
        if (longLine) {
            parts = this.convertIntoPieces(color, 64);
        } else {
            parts = this.convertIntoPieces(color, 15);
        }
        return parts.get(0) + "§f" + this.getLastColor(parts.get(0)) + parts.get(1);
    }

    private String prepForShortline(final String color) {
        if (color.length() > 16) {
            final ArrayList<String> pieces = this.convertIntoPieces(color, 16);
            return pieces.get(0) + "§f" + this.getLastColor(pieces.get(0)) + pieces.get(1);
        }
        return color;
    }

    private String getLastColor(String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == '§') {
                char code = s.charAt(i + 1);
                if ("0123456789abcdefklmnorABCDEFKLMNOR".indexOf(code) != -1) {
                    result.append('§').append(code);
                }
            }
        }
        return result.toString();
    }

    private String stripColorCodes(String s) {
        return s.replaceAll("§[0-9a-fk-orA-FK-OR]", "");
    }

    private ArrayList<String> convertIntoPieces(String s, int allowedLineSize) {
        ArrayList<String> parts = new ArrayList<>();
        String stripped = stripColorCodes(s);

        if (stripped.length() > allowedLineSize) {
            // Find split point that accounts for color codes
            int splitIndex = findSplitIndex(s, allowedLineSize);
            parts.add(s.substring(0, splitIndex));
            String s2 = s.substring(splitIndex);
            // Apply same logic to second part
            if (stripColorCodes(s2).length() > allowedLineSize) {
                int secondSplit = findSplitIndex(s2, allowedLineSize);
                s2 = s2.substring(0, secondSplit);
            }
            parts.add(s2);
        } else {
            parts.add(s);
            parts.add("");
        }
        return parts;
    }

    private int findSplitIndex(String s, int visibleLength) {
        int visible = 0;
        int i = 0;
        while (i < s.length() && visible < visibleLength) {
            if (s.charAt(i) == '§' && i + 1 < s.length()) {
                // Skip color code
                i += 2;
            } else {
                visible++;
                i++;
            }
        }
        // Don't split in the middle of a color code
        if (i > 0 && i < s.length() && s.charAt(i - 1) == '§') {
            i--;
        }
        return Math.min(i, s.length());
    }
}
