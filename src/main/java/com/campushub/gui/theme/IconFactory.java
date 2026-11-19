package com.campushub.gui.theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * High-performance vector icon renderer for CampusHub.
 * Draws mathematical vector icons directly with Java2D anti-aliasing.
 * 
 * Benefits over unicode emojis or image files:
 * 1. Zero missing glyphs / tofu boxes ([]) on any operating system or JDK font.
 * 2. Razor-sharp rendering on all DPI scaling factors (100%, 125%, 150%, 200%).
 * 3. Dynamically adapts to active theme colors (light, dark, sidebar blue, etc.).
 */
public class IconFactory {

    public enum IconType {
        DASHBOARD,
        COURSES,
        MARKS,
        TIMETABLE,
        STUDENTS,
        FACULTY,
        LIBRARY,
        BOOKING,
        SETTINGS,
        KEY,
        LOGOUT,
        EYE_OPEN,
        EYE_CLOSED,
        SEARCH,
        PLUS,
        EDIT,
        DELETE,
        CHECK,
        USER,
        LOCK,
        DATABASE,
        SUN,
        MOON,
        LOGO,
        EXPORT,
        INFO,
        ALERT
    }

    public static Icon getIcon(IconType type) {
        return getIcon(type, 18, null);
    }

    public static Icon getIcon(IconType type, int size) {
        return getIcon(type, size, null);
    }

    public static Icon getIcon(IconType type, int size, Color color) {
        return new VectorIcon(type, size, color);
    }

    /**
     * Custom implementation of javax.swing.Icon using Java2D vector paths.
     */
    public static class VectorIcon implements Icon {
        private final IconType type;
        private final int size;
        private final Color fixedColor;

        public VectorIcon(IconType type, int size, Color fixedColor) {
            this.type = type;
            this.size = size;
            this.fixedColor = fixedColor;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            // Determine drawing color
            Color drawColor;
            if (fixedColor != null) {
                drawColor = fixedColor;
            } else if (c != null && c.getForeground() != null) {
                drawColor = c.getForeground();
            } else {
                drawColor = new Color(71, 85, 105);
            }
            g2.setColor(drawColor);

            // Scale to standard 24x24 coordinate box
            double scale = (double) size / 24.0;
            g2.translate(x, y);
            g2.scale(scale, scale);

            // Set default stroke
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            drawVectorShape(g2, type, drawColor);

            g2.dispose();
        }

        private void drawVectorShape(Graphics2D g, IconType t, Color col) {
            switch (t) {
                case DASHBOARD:
                    // 4 neat modern rounded tiles
                    g.fillRoundRect(3, 3, 8, 8, 3, 3);
                    g.fillRoundRect(13, 3, 8, 8, 3, 3);
                    g.fillRoundRect(3, 13, 8, 8, 3, 3);
                    g.drawRoundRect(13, 13, 8, 8, 3, 3);
                    break;

                case COURSES:
                    // Stacked books with academic spine
                    g.drawRoundRect(4, 5, 16, 4, 2, 2);
                    g.drawRoundRect(4, 10, 16, 4, 2, 2);
                    g.drawRoundRect(4, 15, 16, 4, 2, 2);
                    g.drawLine(7, 5, 7, 19);
                    break;

                case MARKS:
                    // Assessment Clipboard with checklist & pen
                    g.drawRoundRect(5, 4, 14, 17, 3, 3);
                    g.drawRoundRect(9, 2, 6, 4, 2, 2);
                    g.drawLine(8, 10, 11, 10);
                    g.drawLine(13, 10, 16, 10);
                    g.drawLine(8, 14, 11, 14);
                    g.drawLine(13, 14, 16, 14);
                    break;

                case TIMETABLE:
                    // Calendar grid
                    g.drawRoundRect(3, 4, 18, 17, 3, 3);
                    g.drawLine(3, 9, 21, 9);
                    g.drawLine(7, 2, 7, 5);
                    g.drawLine(17, 2, 17, 5);
                    // Dots inside calendar
                    g.fillRect(7, 12, 2, 2);
                    g.fillRect(11, 12, 2, 2);
                    g.fillRect(15, 12, 2, 2);
                    g.fillRect(7, 16, 2, 2);
                    g.fillRect(11, 16, 2, 2);
                    g.fillRect(15, 16, 2, 2);
                    break;

                case STUDENTS:
                    // Student silhouette with graduation mortarboard
                    // Cap
                    Polygon cap = new Polygon();
                    cap.addPoint(12, 3);
                    cap.addPoint(20, 7);
                    cap.addPoint(12, 11);
                    cap.addPoint(4, 7);
                    g.fillPolygon(cap);
                    g.drawLine(20, 7, 20, 12);
                    // Body
                    g.drawArc(6, 13, 12, 10, 0, 180);
                    break;

                case FACULTY:
                    // Teacher / Professor silhouette
                    g.drawOval(9, 3, 6, 6);
                    g.drawArc(5, 11, 14, 10, 0, 180);
                    // Podium line / desk
                    g.drawLine(3, 21, 21, 21);
                    break;

                case LIBRARY:
                    // Open book
                    Path2D book = new Path2D.Double();
                    book.moveTo(12, 6);
                    book.curveTo(8, 4, 4, 4, 3, 6);
                    book.lineTo(3, 18);
                    book.curveTo(4, 16, 8, 16, 12, 18);
                    book.curveTo(16, 16, 20, 16, 21, 18);
                    book.lineTo(21, 6);
                    book.curveTo(20, 4, 16, 4, 12, 6);
                    book.closePath();
                    g.draw(book);
                    g.drawLine(12, 6, 12, 18);
                    break;

                case BOOKING:
                    // Building / Room door with handle
                    g.drawRoundRect(4, 3, 16, 18, 2, 2);
                    g.drawLine(4, 21, 20, 21);
                    g.drawLine(14, 3, 14, 21);
                    g.fillOval(11, 11, 2, 2);
                    break;

                case SETTINGS:
                    // Modern Gear
                    g.drawOval(8, 8, 8, 8);
                    for (int i = 0; i < 8; i++) {
                        double angle = i * Math.PI / 4.0;
                        int x1 = (int) (12 + 6 * Math.cos(angle));
                        int y1 = (int) (12 + 6 * Math.sin(angle));
                        int x2 = (int) (12 + 9 * Math.cos(angle));
                        int y2 = (int) (12 + 9 * Math.sin(angle));
                        g.drawLine(x1, y1, x2, y2);
                    }
                    break;

                case KEY:
                    // Password Key
                    g.drawOval(5, 6, 8, 8);
                    g.drawLine(12, 13, 19, 19);
                    g.drawLine(16, 16, 18, 14);
                    g.drawLine(18, 18, 20, 16);
                    break;

                case LOGOUT:
                    // Doorway + Exit arrow
                    g.drawRoundRect(3, 3, 10, 18, 2, 2);
                    g.drawLine(10, 12, 20, 12);
                    g.drawLine(16, 8, 20, 12);
                    g.drawLine(16, 16, 20, 12);
                    break;

                case EYE_OPEN:
                    // Eye outline + Pupil
                    Path2D eye = new Path2D.Double();
                    eye.moveTo(3, 12);
                    eye.curveTo(7, 6, 17, 6, 21, 12);
                    eye.curveTo(17, 18, 7, 18, 3, 12);
                    eye.closePath();
                    g.draw(eye);
                    g.fillOval(10, 10, 4, 4);
                    break;

                case EYE_CLOSED:
                    // Eye closed with diagonal slash
                    Path2D eyeC = new Path2D.Double();
                    eyeC.moveTo(3, 12);
                    eyeC.curveTo(7, 6, 17, 6, 21, 12);
                    eyeC.curveTo(17, 18, 7, 18, 3, 12);
                    g.draw(eyeC);
                    g.drawLine(4, 20, 20, 4);
                    break;

                case SEARCH:
                    // Magnifying glass
                    g.drawOval(5, 5, 10, 10);
                    g.drawLine(13, 13, 19, 19);
                    break;

                case PLUS:
                    // Clean plus sign
                    g.drawLine(12, 5, 12, 19);
                    g.drawLine(5, 12, 19, 12);
                    break;

                case EDIT:
                    // Pencil
                    g.drawLine(5, 19, 7, 15);
                    g.drawLine(7, 15, 17, 5);
                    g.drawLine(17, 5, 19, 7);
                    g.drawLine(19, 7, 9, 17);
                    g.drawLine(9, 17, 5, 19);
                    break;

                case DELETE:
                    // Trash can
                    g.drawLine(4, 6, 20, 6);
                    g.drawRoundRect(6, 6, 12, 14, 2, 2);
                    g.drawLine(9, 9, 9, 16);
                    g.drawLine(15, 9, 15, 16);
                    g.drawRoundRect(9, 3, 6, 3, 1, 1);
                    break;

                case CHECK:
                    // Checkmark
                    g.drawLine(4, 12, 9, 17);
                    g.drawLine(9, 17, 20, 6);
                    break;

                case USER:
                    // Single profile user
                    g.drawOval(8, 4, 8, 8);
                    g.drawArc(4, 13, 16, 11, 0, 180);
                    break;

                case LOCK:
                    // Padlock
                    g.drawRoundRect(5, 10, 14, 11, 3, 3);
                    g.drawArc(8, 4, 8, 10, 0, 180);
                    g.fillOval(11, 14, 2, 3);
                    break;

                case DATABASE:
                    // Cylinder database disks
                    g.drawOval(5, 4, 14, 4);
                    g.drawLine(5, 6, 5, 18);
                    g.drawLine(19, 6, 19, 18);
                    g.drawArc(5, 9, 14, 4, 180, 180);
                    g.drawArc(5, 14, 14, 4, 180, 180);
                    g.drawArc(5, 16, 14, 4, 180, 180);
                    break;

                case SUN:
                    // Sun icon for Light Theme
                    g.drawOval(8, 8, 8, 8);
                    for (int i = 0; i < 8; i++) {
                        double a = i * Math.PI / 4.0;
                        int x1 = (int) (12 + 6 * Math.cos(a));
                        int y1 = (int) (12 + 6 * Math.sin(a));
                        int x2 = (int) (12 + 8 * Math.cos(a));
                        int y2 = (int) (12 + 8 * Math.sin(a));
                        g.drawLine(x1, y1, x2, y2);
                    }
                    break;

                case MOON:
                    // Crescent Moon icon for Dark Theme
                    Path2D moon = new Path2D.Double();
                    moon.moveTo(14, 4);
                    moon.curveTo(9, 4, 5, 8, 5, 13);
                    moon.curveTo(5, 18, 9, 21, 14, 21);
                    moon.curveTo(11, 19, 10, 16, 10, 12);
                    moon.curveTo(10, 8, 12, 5, 14, 4);
                    moon.closePath();
                    g.fill(moon);
                    break;

                case LOGO:
                    // Modern Shield Crest with Graduation Cap & Star
                    Path2D shield = new Path2D.Double();
                    shield.moveTo(12, 2);
                    shield.lineTo(21, 5);
                    shield.curveTo(21, 15, 12, 21, 12, 22);
                    shield.curveTo(12, 21, 3, 15, 3, 5);
                    shield.closePath();
                    g.fill(shield);
                    g.setColor(Color.WHITE);
                    // Center Star
                    g.drawLine(12, 8, 12, 16);
                    g.drawLine(8, 12, 16, 12);
                    break;

                case EXPORT:
                    // Export / Download tray
                    g.drawLine(4, 16, 4, 20);
                    g.drawLine(4, 20, 20, 20);
                    g.drawLine(20, 20, 20, 16);
                    g.drawLine(12, 4, 12, 14);
                    g.drawLine(8, 10, 12, 14);
                    g.drawLine(16, 10, 12, 14);
                    break;

                case INFO:
                    g.drawOval(4, 4, 16, 16);
                    g.fillOval(11, 7, 2, 2);
                    g.drawLine(12, 11, 12, 16);
                    break;

                case ALERT:
                    Polygon tri = new Polygon();
                    tri.addPoint(12, 3);
                    tri.addPoint(21, 20);
                    tri.addPoint(3, 20);
                    g.drawPolygon(tri);
                    g.drawLine(12, 9, 12, 14);
                    g.fillRect(11, 16, 2, 2);
                    break;
            }
        }
    }
}

