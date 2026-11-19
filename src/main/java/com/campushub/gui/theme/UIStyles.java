package com.campushub.gui.theme;

import com.campushub.gui.theme.AnimationUtils.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern CSS-like design system and style tokens for CampusHub.
 * Provides consistent typography, colors, cards, tables, inputs, and button themes.
 */
public class UIStyles {

    // =========================================================================
    // COLOR PALETTE (Modern SaaS / Cool White & Slate Design System)
    // =========================================================================
    public static final Color BG_PAGE = new Color(248, 250, 252);        // #F8FAFC Ultra-light slate
    public static final Color BG_CARD = Color.WHITE;                      // #FFFFFF Pure crisp white
    public static final Color BG_SIDEBAR = new Color(15, 23, 42);         // #0F172A Deep slate navy
    public static final Color BG_SIDEBAR_BTN = new Color(30, 41, 59);     // #1E293B Sidebar button base
    public static final Color BG_SIDEBAR_HOVER = new Color(51, 65, 85);   // #334155 Sidebar hover
    public static final Color BG_SIDEBAR_ACTIVE = new Color(37, 99, 235); // #2563EB Primary accent

    public static final Color BORDER_LIGHT = new Color(226, 232, 240);    // #E2E8F0 Clean border line
    public static final Color BORDER_FOCUS = new Color(59, 130, 246);    // #3B82F6 Focus ring
    public static final Color BORDER_INPUT = new Color(203, 213, 225);    // #CBD5E1 Input border

    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);       // #0F172A Deep dark text
    public static final Color TEXT_SECONDARY = new Color(71, 85, 105);   // #475569 Slate muted text
    public static final Color TEXT_MUTED = new Color(148, 163, 184);      // #94A3B8 Subtle hint text

    public static final Color PRIMARY = new Color(37, 99, 235);          // #2563EB Royal Blue
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);    // #1D4ED8
    public static final Color SUCCESS = new Color(16, 185, 129);          // #10B981 Emerald
    public static final Color SUCCESS_HOVER = new Color(5, 150, 105);
    public static final Color WARNING = new Color(245, 158, 11);          // #F59E0B Amber
    public static final Color DANGER = new Color(239, 68, 68);            // #EF4444 Rose Red
    public static final Color DANGER_HOVER = new Color(220, 38, 38);

    public static final Color BADGE_GREEN_BG = new Color(220, 252, 231); // #DCFCE7
    public static final Color BADGE_GREEN_FG = new Color(22, 101, 52);   // #166534
    public static final Color BADGE_BLUE_BG = new Color(219, 234, 254);  // #DBEAFE
    public static final Color BADGE_BLUE_FG = new Color(30, 64, 175);    // #1E40AF
    public static final Color BADGE_AMBER_BG = new Color(254, 243, 199); // #FEF3C7
    public static final Color BADGE_AMBER_FG = new Color(146, 64, 14);   // #92400E
    public static final Color BADGE_RED_BG = new Color(254, 226, 226);   // #FEE2E2
    public static final Color BADGE_RED_FG = new Color(153, 27, 27);     // #991B1B

    // Extra semantic color tokens
    public static final Color SIDEBAR_ACCENT = new Color(37, 99, 235);   // Same as BG_SIDEBAR_ACTIVE
    public static final Color CARD_SHADOW = new Color(0, 0, 0, 20);

    // =========================================================================
    // TYPOGRAPHY
    // =========================================================================
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);

    // =========================================================================
    // BORDER FACTORIES
    // =========================================================================
    public static Border createCardBorder() {
        return new CompoundBorder(
                new LineBorder(BORDER_LIGHT, 1, true),
                new EmptyBorder(16, 20, 16, 20)
        );
    }

    public static Border createSectionBorder() {
        return new CompoundBorder(
                new LineBorder(BORDER_LIGHT, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        );
    }

    public static Border createFieldBorder() {
        return new CompoundBorder(
                new LineBorder(BORDER_INPUT, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        );
    }

    // =========================================================================
    // BUTTON FACTORIES (Legacy plain JButton - kept for compatibility)
    // =========================================================================
    public static JButton createPrimaryButton(String text, IconFactory.IconType iconType) {
        JButton btn = new JButton(text);
        if (iconType != null) {
            btn.setIcon(IconFactory.getIcon(iconType, 16, Color.WHITE));
            btn.setIconTextGap(8);
        }
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(PRIMARY_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(PRIMARY);
            }
        });
        return btn;
    }

    public static JButton createSecondaryButton(String text, IconFactory.IconType iconType) {
        JButton btn = new JButton(text);
        if (iconType != null) {
            btn.setIcon(IconFactory.getIcon(iconType, 16, TEXT_SECONDARY));
            btn.setIconTextGap(8);
        }
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(Color.WHITE);
        btn.setForeground(TEXT_SECONDARY);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(new LineBorder(BORDER_LIGHT, 1, true), new EmptyBorder(7, 14, 7, 14)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(241, 245, 249));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    public static JButton createDangerButton(String text, IconFactory.IconType iconType) {
        JButton btn = new JButton(text);
        if (iconType != null) {
            btn.setIcon(IconFactory.getIcon(iconType, 16, Color.WHITE));
            btn.setIconTextGap(8);
        }
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(DANGER_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(DANGER);
            }
        });
        return btn;
    }

    // =========================================================================
    // ANIMATED BUTTON FACTORIES
    // =========================================================================
    public static AnimatedButton createAnimatedPrimaryButton(String text, IconFactory.IconType iconType) {
        AnimatedButton btn = new AnimatedButton(text, PRIMARY, PRIMARY_HOVER);
        if (iconType != null) {
            btn.setIcon(IconFactory.getIcon(iconType, 16, Color.WHITE));
            btn.setIconTextGap(8);
        }
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        return btn;
    }

    public static AnimatedButton createAnimatedSecondaryButton(String text, IconFactory.IconType iconType) {
        AnimatedButton btn = new AnimatedButton(text, new Color(241, 245, 249), new Color(226, 232, 240));
        if (iconType != null) {
            btn.setIcon(IconFactory.getIcon(iconType, 16, TEXT_SECONDARY));
            btn.setIconTextGap(8);
        }
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(TEXT_SECONDARY);
        btn.setBorder(new CompoundBorder(new LineBorder(BORDER_LIGHT, 1, true), new EmptyBorder(9, 16, 9, 16)));
        return btn;
    }

    public static AnimatedButton createAnimatedDangerButton(String text, IconFactory.IconType iconType) {
        AnimatedButton btn = new AnimatedButton(text, DANGER, DANGER_HOVER);
        if (iconType != null) {
            btn.setIcon(IconFactory.getIcon(iconType, 16, Color.WHITE));
            btn.setIconTextGap(8);
        }
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        return btn;
    }

    // =========================================================================
    // SHADOW CARD FACTORY
    // =========================================================================
    public static ShadowPanel createShadowCard() {
        ShadowPanel panel = new ShadowPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        // Inner content padding via a wrapper
        return panel;
    }

    // =========================================================================
    // INPUT FIELD STYLING
    // =========================================================================
    public static void styleInputField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER_INPUT, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new CompoundBorder(
                        new LineBorder(BORDER_FOCUS, 2, true),
                        new EmptyBorder(7, 9, 7, 9)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new CompoundBorder(
                        new LineBorder(BORDER_INPUT, 1, true),
                        new EmptyBorder(8, 10, 8, 10)
                ));
            }
        });
    }

    public static void styleInputField(JPasswordField field) {
        field.setFont(FONT_BODY);
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER_INPUT, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new CompoundBorder(
                        new LineBorder(BORDER_FOCUS, 2, true),
                        new EmptyBorder(7, 9, 7, 9)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new CompoundBorder(
                        new LineBorder(BORDER_INPUT, 1, true),
                        new EmptyBorder(8, 10, 8, 10)
                ));
            }
        });
    }

    // =========================================================================
    // SCROLL PANE STYLING
    // =========================================================================
    public static JScrollPane styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
        sp.getHorizontalScrollBar().setPreferredSize(new Dimension(Integer.MAX_VALUE, 8));
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    // =========================================================================
    // MODERN TABLE STYLING
    // =========================================================================
    public static void styleModernTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_LIGHT);
        table.setSelectionBackground(new Color(224, 231, 255)); // Soft indigo highlight
        table.setSelectionForeground(new Color(30, 27, 75));
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_SMALL_BOLD);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(new Color(71, 85, 105));
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT));
        header.setReorderingAllowed(false);

        // Default Alternating Row Renderer
        DefaultTableCellRenderer alternatingRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                if (isSelected) {
                    c.setBackground(tbl.getSelectionBackground());
                    c.setForeground(tbl.getSelectionForeground());
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(TEXT_PRIMARY);
                }
                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 10));
                }
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(alternatingRenderer);
        }
    }

    // =========================================================================
    // PILL BADGE COMPONENT
    // =========================================================================
    public static JLabel createPillBadge(String text, Color bg, Color fg) {
        JLabel badge = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        badge.setFont(FONT_SMALL_BOLD);
        badge.setBackground(bg);
        badge.setForeground(fg);
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(3, 10, 3, 10));
        return badge;
    }
}
