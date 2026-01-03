package de.muckmuck96.elements.element.sidebar;

import java.util.List;

/**
 * Configuration holder for sidebar content and update interval.
 *
 * @param lines          the sidebar lines to display
 * @param interval       update interval in ticks
 * @param sidebarHolders optional nested holders for animated content
 */
public record SidebarHolder(List<String> lines, int interval, SidebarHolder ...sidebarHolders) {}
