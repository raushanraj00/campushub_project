package com.campushub.gui;

import com.campushub.database.DBConnection;
import com.campushub.database.DatabaseInitializer;
import com.campushub.exceptions.InvalidUserException;
import com.campushub.gui.theme.AnimationUtils.AnimatedButton;
import com.campushub.gui.theme.AnimationUtils.FadeInPanel;
import com.campushub.gui.theme.IconFactory;
import com.campushub.gui.theme.IconFactory.IconType;
import com.campushub.gui.theme.UIStyles;
import com.campushub.models.User;
import com.campushub.services.AuthService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * UNIT 3: SWING GUI - AUTHENTICATION & LOGIN PORTAL
 * Professional SaaS-style split-panel login interface with:
 * - Left branding panel (dark navy) with feature pills
 * - Right form panel with FadeInPanel entry animation
 * - Anti-aliased Java2D vector icons (zero missing font square boxes)
 * - Interactive eye password reveal toggle
 * - Fast one-click demo credentials
 * - Dynamic MySQL configuration dialog
 */
public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnEye;
    private JLabel lblStatus;
    private AnimatedButton btnLogin;
    private JButton btnDbConfig;
    private boolean isPasswordVisible = false;

    public LoginFrame() {
        setTitle("CampusHub - Smart Campus Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        checkDatabaseConnectivity();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIStyles.BG_PAGE);

        // ----------------------------------------------------------------
        // LEFT BRANDING PANEL (40%)
        // ----------------------------------------------------------------
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(UIStyles.BG_SIDEBAR);
        leftPanel.setPreferredSize(new Dimension(360, 600));

        JPanel brandContent = new JPanel();
        brandContent.setLayout(new BoxLayout(brandContent, BoxLayout.Y_AXIS));
        brandContent.setOpaque(false);
        brandContent.setBorder(new EmptyBorder(0, 32, 0, 32));

        // Large shield logo
        JLabel logoLbl = new JLabel(IconFactory.getIcon(IconType.LOGO, 60, Color.WHITE));
        logoLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandContent.add(logoLbl);
        brandContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // App name
        JLabel appName = new JLabel("CampusHub ERP");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 28));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandContent.add(appName);
        brandContent.add(Box.createRigidArea(new Dimension(0, 8)));

        // Tagline
        JLabel tagline = new JLabel("Smart Campus Management System");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tagline.setForeground(new Color(148, 163, 184));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandContent.add(tagline);
        brandContent.add(Box.createRigidArea(new Dimension(0, 32)));

        // Feature pills
        String[] features = {"OOP Concepts Demonstrated", "JDBC Database Ready", "Role-Based Access"};
        for (String feature : features) {
            JLabel pill = createFeaturePill(feature);
            pill.setAlignmentX(Component.CENTER_ALIGNMENT);
            brandContent.add(pill);
            brandContent.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        brandContent.add(Box.createRigidArea(new Dimension(0, 12)));

        // Project Owner & Lead Architect Card
        JPanel creatorCard = createCreatorCard();
        brandContent.add(creatorCard);

        brandContent.add(Box.createVerticalGlue());

        // Version tag at bottom
        JLabel version = new JLabel("v2.0 | Java OOP End-Semester Project");
        version.setFont(UIStyles.FONT_SMALL);
        version.setForeground(new Color(100, 116, 139));
        version.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandContent.add(version);

        leftPanel.add(brandContent);
        root.add(leftPanel, BorderLayout.WEST);

        // ----------------------------------------------------------------
        // RIGHT FORM PANEL (60%) wrapped in FadeInPanel
        // ----------------------------------------------------------------
        FadeInPanel rightFade = new FadeInPanel(new GridBagLayout());
        rightFade.setBackground(UIStyles.BG_PAGE);
        rightFade.setOpaque(true);

        JPanel formOuter = new JPanel(new GridBagLayout());
        formOuter.setOpaque(false);
        formOuter.setBackground(UIStyles.BG_PAGE);

        // Form card (white card with shadow effect via border)
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(UIStyles.BG_CARD);
        formCard.setBorder(new CompoundBorder(
                new LineBorder(UIStyles.BORDER_LIGHT, 1, true),
                new EmptyBorder(28, 32, 28, 32)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // Welcome back
        JLabel lblWelcome = new JLabel("Welcome back");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblWelcome.setForeground(UIStyles.TEXT_PRIMARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        formCard.add(lblWelcome, gbc);

        JLabel lblSub = new JLabel("Sign in to your account");
        lblSub.setFont(UIStyles.FONT_BODY);
        lblSub.setForeground(UIStyles.TEXT_SECONDARY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        formCard.add(lblSub, gbc);

        // Username label
        JLabel lblUser = new JLabel("Username / ID");
        lblUser.setFont(UIStyles.FONT_SMALL_BOLD);
        lblUser.setForeground(UIStyles.TEXT_SECONDARY);
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 4, 0);
        formCard.add(lblUser, gbc);

        // Username field with icon
        JPanel userContainer = new JPanel(new BorderLayout(8, 0));
        userContainer.setBackground(UIStyles.BG_CARD);
        userContainer.setBorder(new CompoundBorder(
                new LineBorder(UIStyles.BORDER_INPUT, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        userContainer.setPreferredSize(new Dimension(340, 42));

        JLabel userIcon = new JLabel(IconFactory.getIcon(IconType.USER, 16, UIStyles.TEXT_MUTED));
        txtUsername = new JTextField("admin");
        txtUsername.setFont(UIStyles.FONT_BODY);
        txtUsername.setBorder(null);
        txtUsername.setOpaque(false);

        userContainer.add(userIcon, BorderLayout.WEST);
        userContainer.add(txtUsername, BorderLayout.CENTER);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        formCard.add(userContainer, gbc);

        // Password label
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UIStyles.FONT_SMALL_BOLD);
        lblPass.setForeground(UIStyles.TEXT_SECONDARY);
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 0, 4, 0);
        formCard.add(lblPass, gbc);

        // Password container with eye toggle
        JPanel passContainer = new JPanel(new BorderLayout(8, 0));
        passContainer.setBackground(UIStyles.BG_CARD);
        passContainer.setBorder(new CompoundBorder(
                new LineBorder(UIStyles.BORDER_INPUT, 1, true),
                new EmptyBorder(6, 8, 6, 4)
        ));
        passContainer.setPreferredSize(new Dimension(340, 42));

        JLabel lockIcon = new JLabel(IconFactory.getIcon(IconType.LOCK, 16, UIStyles.TEXT_MUTED));
        txtPassword = new JPasswordField("Admin@123");
        txtPassword.setFont(UIStyles.FONT_BODY);
        txtPassword.setBorder(null);
        txtPassword.setOpaque(false);

        btnEye = new JButton(IconFactory.getIcon(IconType.EYE_OPEN, 18, UIStyles.TEXT_MUTED));
        btnEye.setPreferredSize(new Dimension(36, 28));
        btnEye.setBackground(UIStyles.BG_CARD);
        btnEye.setBorder(null);
        btnEye.setFocusPainted(false);
        btnEye.setContentAreaFilled(false);
        btnEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEye.setToolTipText("Show / Hide password");
        btnEye.addActionListener(e -> togglePasswordVisibility());

        passContainer.add(lockIcon, BorderLayout.WEST);
        passContainer.add(txtPassword, BorderLayout.CENTER);
        passContainer.add(btnEye, BorderLayout.EAST);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 14, 0);
        formCard.add(passContainer, gbc);

        // Status label
        lblStatus = new JLabel("Checking MySQL connection...", SwingConstants.CENTER);
        lblStatus.setFont(UIStyles.FONT_SMALL);
        lblStatus.setForeground(UIStyles.TEXT_MUTED);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 8, 0);
        formCard.add(lblStatus, gbc);

        // Sign In animated button
        btnLogin = UIStyles.createAnimatedPrimaryButton("Sign In to CampusHub", IconType.CHECK);
        btnLogin.setPreferredSize(new Dimension(340, 44));
        btnLogin.addActionListener(e -> performLogin());
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 14, 0);
        formCard.add(btnLogin, gbc);

        // Quick Demo Access section
        JLabel demoLabel = new JLabel("Quick Demo Access", SwingConstants.CENTER);
        demoLabel.setFont(UIStyles.FONT_SMALL_BOLD);
        demoLabel.setForeground(UIStyles.TEXT_MUTED);
        gbc.gridy = 8;
        gbc.insets = new Insets(2, 0, 6, 0);
        formCard.add(demoLabel, gbc);

        JPanel demoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        demoPanel.setOpaque(false);
        demoPanel.add(createPillButton("Admin", "admin", "Admin@123"));
        demoPanel.add(createPillButton("Faculty", "fac_cse_01", "Faculty@123"));
        demoPanel.add(createPillButton("Student", "25bcs0001", "Student@123"));
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 14, 0);
        formCard.add(demoPanel, gbc);

        // Database config link
        btnDbConfig = new JButton("Configure MySQL Database");
        btnDbConfig.setIcon(IconFactory.getIcon(IconType.DATABASE, 14, UIStyles.PRIMARY));
        btnDbConfig.setIconTextGap(6);
        btnDbConfig.setFont(UIStyles.FONT_SMALL);
        btnDbConfig.setForeground(UIStyles.PRIMARY);
        btnDbConfig.setContentAreaFilled(false);
        btnDbConfig.setBorderPainted(false);
        btnDbConfig.setFocusPainted(false);
        btnDbConfig.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDbConfig.addActionListener(e -> showDatabaseConfigDialog());
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, 0, 0);
        formCard.add(btnDbConfig, gbc);

        // Form outer with card and owner attribution footer
        formOuter.setLayout(new BoxLayout(formOuter, BoxLayout.Y_AXIS));
        formCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        formOuter.add(formCard);

        formOuter.add(Box.createRigidArea(new Dimension(0, 14)));
        JLabel ownerFooter = new JLabel("Crafted & Engineered by Raushan Raj · Smart Campus ERP");
        ownerFooter.setFont(UIStyles.FONT_SMALL);
        ownerFooter.setForeground(new Color(100, 116, 139));
        ownerFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        formOuter.add(ownerFooter);

        GridBagConstraints outerGbc = new GridBagConstraints();
        outerGbc.anchor = GridBagConstraints.CENTER;
        outerGbc.fill = GridBagConstraints.NONE;
        rightFade.add(formOuter, outerGbc);

        root.add(rightFade, BorderLayout.CENTER);

        setContentPane(root);
        getRootPane().setDefaultButton(btnLogin);
    }

    private JPanel createCreatorCard() {
        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(255, 255, 255, 45));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 14, 10, 14));
        card.setMaximumSize(new Dimension(300, 64));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Monogram Avatar (RR)
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(37, 99, 235), getWidth(), getHeight(), new Color(124, 58, 237));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String text = "RR";
                int tx = (getWidth() - fm.stringWidth(text)) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, tx, ty);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setOpaque(false);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel tag = new JLabel("● PROJECT ARCHITECT");
        tag.setFont(new Font("Segoe UI", Font.BOLD, 9));
        tag.setForeground(new Color(96, 165, 250));

        JLabel name = new JLabel("Raushan Raj");
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        name.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Lead Developer · Java OOP");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sub.setForeground(new Color(148, 163, 184));

        info.add(tag);
        info.add(name);
        info.add(sub);

        card.add(avatar, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    private JLabel createFeaturePill(String text) {
        JLabel pill = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(37, 99, 235, 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        pill.setFont(UIStyles.FONT_SMALL_BOLD);
        pill.setForeground(new Color(147, 197, 253));
        pill.setOpaque(false);
        pill.setBorder(new EmptyBorder(5, 16, 5, 16));
        return pill;
    }

    private JButton createPillButton(String text, String user, String pass) {
        JButton btn = new JButton(text);
        btn.setFont(UIStyles.FONT_SMALL);
        btn.setBackground(new Color(241, 245, 249));
        btn.setForeground(UIStyles.TEXT_SECONDARY);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(new LineBorder(UIStyles.BORDER_LIGHT, 1, true), new EmptyBorder(4, 10, 4, 10)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            txtUsername.setText(user);
            txtPassword.setText(pass);
        });
        return btn;
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            txtPassword.setEchoChar('\u2022');
            btnEye.setIcon(IconFactory.getIcon(IconType.EYE_OPEN, 18, UIStyles.TEXT_MUTED));
            btnEye.setToolTipText("Click to view password");
            isPasswordVisible = false;
        } else {
            txtPassword.setEchoChar((char) 0);
            btnEye.setIcon(IconFactory.getIcon(IconType.EYE_CLOSED, 18, UIStyles.PRIMARY));
            btnEye.setToolTipText("Click to hide password");
            isPasswordVisible = true;
        }
    }

    public void checkDatabaseConnectivity() {
        new Thread(() -> {
            boolean connected = DBConnection.testConnection();
            SwingUtilities.invokeLater(() -> {
                if (connected) {
                    DatabaseInitializer.initializeDatabase();
                    lblStatus.setText("Connected to MySQL (Live Database Mode)");
                    lblStatus.setForeground(UIStyles.SUCCESS);
                } else {
                    lblStatus.setText("Demo Mode Active (Click below to configure MySQL)");
                    lblStatus.setForeground(UIStyles.WARNING);
                }
            });
        }).start();
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please provide both username and password.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnLogin.setEnabled(false);
        lblStatus.setText("Authenticating credentials...");
        lblStatus.setForeground(UIStyles.PRIMARY);

        new Thread(() -> {
            try {
                User user = AuthService.login(username, password);
                SwingUtilities.invokeLater(() -> {
                    try {
                        DashboardFrame dashboard = new DashboardFrame(user);
                        dashboard.setVisible(true);
                        dispose();
                    } catch (Throwable t) {
                        t.printStackTrace();
                        lblStatus.setText("Dashboard error: " + t.getMessage());
                        lblStatus.setForeground(UIStyles.DANGER);
                        JOptionPane.showMessageDialog(this,
                                "Error opening dashboard: " + t.getMessage(),
                                "Dashboard Error", JOptionPane.ERROR_MESSAGE);
                        btnLogin.setEnabled(true);
                    }
                });
            } catch (InvalidUserException ex) {
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText("Authentication failed.");
                    lblStatus.setForeground(UIStyles.DANGER);
                    JOptionPane.showMessageDialog(this,
                            ex.getMessage() + "\n\nTip: You can click the Demo buttons to auto-fill.",
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                    btnLogin.setEnabled(true);
                });
            } catch (Throwable ex) {
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText("Authentication error: " + ex.getMessage());
                    lblStatus.setForeground(UIStyles.DANGER);
                    JOptionPane.showMessageDialog(this,
                            "Error: " + ex.getMessage(),
                            "System Notice", JOptionPane.WARNING_MESSAGE);
                    btnLogin.setEnabled(true);
                });
            }
        }).start();
    }

    private void showDatabaseConfigDialog() {
        JDialog dialog = new JDialog(this, "Configure MySQL Database Connection", true);
        dialog.setSize(440, 360);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIStyles.BG_CARD);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel lblInfo = new JLabel("<html><b>MySQL Database Setup</b><br><span style='color: #64748b; font-size: 11px;'>Configure your local MySQL Server credentials</span></html>");
        gbc.gridy = 0;
        panel.add(lblInfo, gbc);

        gbc.gridy = 1;
        panel.add(new JLabel("JDBC URL:"), gbc);

        JTextField txtUrl = new JTextField(DBConnection.DatabaseConfig.getUrl());
        txtUrl.setFont(UIStyles.FONT_BODY);
        gbc.gridy = 2;
        panel.add(txtUrl, gbc);

        gbc.gridy = 3;
        panel.add(new JLabel("MySQL Username:"), gbc);

        JTextField txtUser = new JTextField(DBConnection.DatabaseConfig.getUser());
        txtUser.setFont(UIStyles.FONT_BODY);
        gbc.gridy = 4;
        panel.add(txtUser, gbc);

        gbc.gridy = 5;
        panel.add(new JLabel("MySQL Password:"), gbc);

        JPasswordField txtPass = new JPasswordField(DBConnection.DatabaseConfig.getPassword());
        txtPass.setFont(UIStyles.FONT_BODY);
        gbc.gridy = 6;
        panel.add(txtPass, gbc);

        JLabel lblTestResult = new JLabel(" ", SwingConstants.CENTER);
        lblTestResult.setFont(UIStyles.FONT_SMALL);
        gbc.gridy = 7;
        panel.add(lblTestResult, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton btnTest = UIStyles.createSecondaryButton("Test & Connect", IconType.DATABASE);
        JButton btnClose = new JButton("Close");
        btnClose.setFont(UIStyles.FONT_BODY);
        btnClose.addActionListener(e -> dialog.dispose());

        btnTest.addActionListener(e -> {
            lblTestResult.setText("Testing connection...");
            lblTestResult.setForeground(UIStyles.PRIMARY);
            new Thread(() -> {
                boolean ok = DBConnection.DatabaseConfig.saveAndApplyConfig(
                        txtUrl.getText().trim(),
                        txtUser.getText().trim(),
                        new String(txtPass.getPassword())
                );
                SwingUtilities.invokeLater(() -> {
                    if (ok) {
                        lblTestResult.setText("Connected successfully to MySQL!");
                        lblTestResult.setForeground(UIStyles.SUCCESS);
                        DatabaseInitializer.initializeDatabase();
                        checkDatabaseConnectivity();
                    } else {
                        lblTestResult.setText("Could not connect. Check MySQL password.");
                        lblTestResult.setForeground(UIStyles.DANGER);
                    }
                });
            }).start();
        });

        btnPanel.add(btnTest);
        btnPanel.add(btnClose);

        gbc.gridy = 8;
        panel.add(btnPanel, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}
