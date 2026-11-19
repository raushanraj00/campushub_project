package com.campushub.gui.theme;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

/**
 * Manages active application visual themes and coordinates live theme toggles.
 * Provides modern UI settings including rounded components, clean paddings, and font metrics.
 */
public class ThemeManager {

    public enum Theme {
        COOL_WHITE,
        DARK_SLATE
    }

    private static Theme currentTheme = Theme.COOL_WHITE;

    public static void initializeTheme() {
        applyTheme(Theme.COOL_WHITE);
    }

    public static void applyTheme(Theme theme) {
        currentTheme = theme;
        try {
            // Configure modern FlatLaf roundness and metrics before setup
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.width", 8);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.intercellSpacing", new Dimension(0, 1));

            if (theme == Theme.DARK_SLATE) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }

            // Refresh all open top-level windows
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
        } catch (Exception e) {
            System.err.println("[ThemeManager] Error setting theme: " + e.getMessage());
        }
    }

    public static void toggleTheme() {
        if (currentTheme == Theme.COOL_WHITE) {
            applyTheme(Theme.DARK_SLATE);
        } else {
            applyTheme(Theme.COOL_WHITE);
        }
    }

    public static boolean isDarkMode() {
        return currentTheme == Theme.DARK_SLATE;
    }

    public static Theme getCurrentTheme() {
        return currentTheme;
    }
}
