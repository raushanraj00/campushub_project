package com.campushub.gui.theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

/**
 * AnimationUtils – Provides animated Swing components for CampusHub.
 * Includes FadeInPanel, AnimatedButton (with ripple), ShadowPanel, and AnimatedSidebarButton.
 */
public class AnimationUtils {

    // =========================================================================
    // FADE-IN PANEL
    // =========================================================================
    /**
     * A JPanel that fades in from alpha=0 to alpha=1 using a Swing Timer.
     */
    public static class FadeInPanel extends JPanel {
        private float alpha = 0.0f;
        private Timer fadeTimer;

        public FadeInPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            startFade();
        }

        public FadeInPanel() {
            this(new BorderLayout());
        }

        private void startFade() {
            alpha = 0.0f;
            fadeTimer = new Timer(25, e -> {
                alpha += 0.08f;
                if (alpha >= 1.0f) {
                    alpha = 1.0f;
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            });
            fadeTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paint(g2);
            g2.dispose();
        }
    }

    // =========================================================================
    // ANIMATED BUTTON (hover lerp + ripple click effect)
    // =========================================================================
    /**
     * A JButton with smooth hover color interpolation and a ripple click effect.
     */
    public static class AnimatedButton extends JButton {
        private Color normalBg;
        private Color hoverBg;
        private Color currentBg;

        // Ripple state
        private int rippleX = -1;
        private int rippleY = -1;
        private float rippleAlpha = 0f;
        private float rippleRadius = 0f;
        private float rippleMaxRadius = 0f;
        private Timer rippleTimer;

        // Hover lerp state
        private Timer hoverTimer;
        private float hoverProgress = 0f;
        private boolean hovering = false;

        public AnimatedButton(String text, Color normalBg, Color hoverBg) {
            super(text);
            this.normalBg = normalBg;
            this.hoverBg = hoverBg;
            this.currentBg = normalBg;
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            setupHoverAnimation();
            setupRipple();
        }

        private void setupHoverAnimation() {
            hoverTimer = new Timer(20, e -> {
                if (hovering) {
                    hoverProgress = Math.min(1f, hoverProgress + 0.125f); // ~8 steps
                } else {
                    hoverProgress = Math.max(0f, hoverProgress - 0.125f);
                }
                currentBg = interpolateColor(normalBg, hoverBg, hoverProgress);
                repaint();
                if ((hovering && hoverProgress >= 1f) || (!hovering && hoverProgress <= 0f)) {
                    hoverTimer.stop();
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) {
                        hovering = true;
                        if (!hoverTimer.isRunning()) hoverTimer.start();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovering = false;
                    if (!hoverTimer.isRunning()) hoverTimer.start();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    startRipple(e.getX(), e.getY());
                }
            });
        }

        private void setupRipple() {
            rippleTimer = new Timer(16, e -> {
                rippleRadius += rippleMaxRadius / 15f;
                rippleAlpha = Math.max(0f, rippleAlpha - 0.05f);
                repaint();
                if (rippleAlpha <= 0f) {
                    rippleTimer.stop();
                    rippleX = -1;
                }
            });
        }

        private void startRipple(int x, int y) {
            rippleX = x;
            rippleY = y;
            rippleRadius = 0f;
            rippleAlpha = 0.35f;
            rippleMaxRadius = (float) Math.sqrt(getWidth() * getWidth() + getHeight() * getHeight());
            if (rippleTimer.isRunning()) rippleTimer.stop();
            rippleTimer.start();
        }

        private Color interpolateColor(Color c1, Color c2, float t) {
            int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * t);
            int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t);
            int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * t);
            return new Color(
                Math.max(0, Math.min(255, r)),
                Math.max(0, Math.min(255, g)),
                Math.max(0, Math.min(255, b))
            );
        }

        public void setNormalBackground(Color c) {
            this.normalBg = c;
            this.currentBg = c;
            repaint();
        }

        public void setHoverBackground(Color c) {
            this.hoverBg = c;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Background rounded rect
            g2.setColor(isEnabled() ? currentBg : currentBg.darker());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            // Ripple overlay
            if (rippleX >= 0 && rippleAlpha > 0) {
                g2.setColor(new Color(1f, 1f, 1f, rippleAlpha));
                float d = rippleRadius * 2;
                g2.fill(new Ellipse2D.Float(rippleX - rippleRadius, rippleY - rippleRadius, d, d));
            }

            g2.dispose();

            // Paint text and icon
            super.paintComponent(g);
        }
    }

    // =========================================================================
    // SHADOW PANEL
    // =========================================================================
    /**
     * A JPanel that paints a soft multi-layer drop shadow beneath its content.
     */
    public static class ShadowPanel extends JPanel {
        private static final int SHADOW_SIZE = 6;
        private int arc = 12;

        public ShadowPanel() {
            super();
            setOpaque(false);
            setBorder(new EmptyBorder(SHADOW_SIZE, SHADOW_SIZE, SHADOW_SIZE + 4, SHADOW_SIZE + 4));
        }

        public ShadowPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            setBorder(new EmptyBorder(SHADOW_SIZE, SHADOW_SIZE, SHADOW_SIZE + 4, SHADOW_SIZE + 4));
        }

        public void setArc(int arc) {
            this.arc = arc;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = SHADOW_SIZE;
            int y = SHADOW_SIZE;
            int w = getWidth() - SHADOW_SIZE * 2 - 4;
            int h = getHeight() - SHADOW_SIZE * 2 - 4;

            // Multi-layer shadow (decreasing alpha)
            int[] alphas = {15, 10, 6, 3};
            int[] offsets = {4, 3, 2, 1};
            for (int i = 0; i < alphas.length; i++) {
                g2.setColor(new Color(0, 0, 0, alphas[i]));
                g2.fillRoundRect(x - offsets[i] + offsets[i], y + offsets[i], w, h, arc, arc);
            }

            // White card background
            g2.setColor(getBackground() != null ? getBackground() : Color.WHITE);
            g2.fillRoundRect(x, y, w, h, arc, arc);

            g2.dispose();
        }
    }

    // =========================================================================
    // ANIMATED SIDEBAR BUTTON
    // =========================================================================
    /**
     * A sidebar navigation JButton with smooth hover transitions and an active accent bar.
     */
    public static class AnimatedSidebarButton extends JButton {
        private static final Color INACTIVE_BG = new Color(30, 41, 59);
        private static final Color HOVER_BG = new Color(51, 65, 85);
        private static final Color ACTIVE_BG = new Color(37, 99, 235);
        private static final Color ACCENT_BAR = new Color(37, 99, 235);

        private boolean active = false;
        private Color currentBg = INACTIVE_BG;
        private Color targetBg = INACTIVE_BG;
        private Timer hoverTimer;

        public AnimatedSidebarButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(UIStyles.FONT_BODY);
            setForeground(new Color(226, 232, 240));
            setIconTextGap(10);

            hoverTimer = new Timer(15, e -> {
                currentBg = interpolateColor(currentBg, targetBg, 0.2f);
                repaint();
                // Check convergence
                if (Math.abs(currentBg.getRed() - targetBg.getRed()) < 2
                        && Math.abs(currentBg.getGreen() - targetBg.getGreen()) < 2
                        && Math.abs(currentBg.getBlue() - targetBg.getBlue()) < 2) {
                    currentBg = targetBg;
                    hoverTimer.stop();
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!active) {
                        targetBg = HOVER_BG;
                        if (!hoverTimer.isRunning()) hoverTimer.start();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!active) {
                        targetBg = INACTIVE_BG;
                        if (!hoverTimer.isRunning()) hoverTimer.start();
                    }
                }
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            if (active) {
                targetBg = ACTIVE_BG;
                currentBg = ACTIVE_BG;
                setForeground(Color.WHITE);
            } else {
                targetBg = INACTIVE_BG;
                currentBg = INACTIVE_BG;
                setForeground(new Color(226, 232, 240));
            }
            repaint();
        }

        public boolean isActive() {
            return active;
        }

        private Color interpolateColor(Color c1, Color c2, float t) {
            int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * t);
            int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t);
            int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * t);
            return new Color(
                Math.max(0, Math.min(255, r)),
                Math.max(0, Math.min(255, g)),
                Math.max(0, Math.min(255, b))
            );
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Rounded background
            g2.setColor(currentBg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

            // Left accent bar when active
            if (active) {
                g2.setColor(ACCENT_BAR);
                g2.fillRoundRect(0, 4, 3, getHeight() - 8, 3, 3);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
