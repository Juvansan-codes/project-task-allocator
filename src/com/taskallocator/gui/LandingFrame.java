package com.taskallocator.gui;

import com.taskallocator.model.User;
import com.taskallocator.model.UserRole;
import com.taskallocator.model.UserSession;
import com.taskallocator.service.AuthService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Temporary landing destination frame for authenticated users.
 * Confirms successful authentication, displays session details and user roles,
 * and serves as the integration entry point for Member 2 and Member 3 dashboards.
 */
public class LandingFrame extends JFrame {

    private final User authenticatedUser;
    private final AuthService authService;

    public LandingFrame(User user) {
        this.authenticatedUser = user;
        this.authService = new AuthService();

        initUI();
    }

    private void initUI() {
        setTitle("Project Task & Collaboration Allocator — Workspace");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 520);
        setMinimumSize(new Dimension(650, 450));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 16));
        mainPanel.setBackground(UITheme.BG_LIGHT);
        mainPanel.setBorder(new EmptyBorder(24, 28, 24, 28));

        // 1. Top Header Bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.BG_LIGHT);

        JLabel titleLabel = new JLabel("Project Task & Collaboration Allocator");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.NAVY_PRIMARY);

        JLabel subtitleLabel = new JLabel("OOPS Micro-Project — Student Collaboration Workspace");
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBlock.setBackground(UITheme.BG_LIGHT);
        titleBlock.add(titleLabel);
        titleBlock.add(subtitleLabel);

        JButton logoutBtn = UITheme.createSecondaryButton("Log Out");
        logoutBtn.addActionListener(e -> handleLogout());

        headerPanel.add(titleBlock, BorderLayout.WEST);
        headerPanel.add(logoutBtn, BorderLayout.EAST);

        // 2. Center Content Cards
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 16));
        centerPanel.setBackground(UITheme.BG_LIGHT);

        // User Profile Card
        JPanel profileCard = UITheme.createCardPanel();
        profileCard.setLayout(new BorderLayout(16, 12));

        JLabel cardHeading = new JLabel("Authenticated User Profile");
        cardHeading.setFont(UITheme.FONT_HEADER);
        cardHeading.setForeground(UITheme.NAVY_PRIMARY);

        JPanel detailsGrid = new JPanel(new GridLayout(3, 2, 12, 10));
        detailsGrid.setBackground(UITheme.CARD_BG);

        detailsGrid.add(createDetailRow("Student Name:", authenticatedUser.getFullName()));
        detailsGrid.add(createDetailRow("Email Address:", authenticatedUser.getEmail()));
        detailsGrid.add(createDetailRow("Account ID:", "#" + authenticatedUser.getId()));
        
        String roleText = authenticatedUser.getRole() == UserRole.PROJECT_LEADER ? "Project Leader" : "Team Member";
        detailsGrid.add(createDetailRow("Registered Role:", roleText));
        detailsGrid.add(createDetailRow("Session Status:", "Active (Logged In)"));
        detailsGrid.add(createDetailRow("Database Source:", "MySQL 8.x (task_allocator_db)"));

        profileCard.add(cardHeading, BorderLayout.NORTH);
        profileCard.add(detailsGrid, BorderLayout.CENTER);

        // Team Integration Notice Card
        JPanel integrationCard = UITheme.createCardPanel();
        integrationCard.setLayout(new BorderLayout(0, 10));

        JLabel infoHeader = new JLabel("Team Module Status");
        infoHeader.setFont(UITheme.FONT_HEADER);
        infoHeader.setForeground(UITheme.NAVY_PRIMARY);

        JTextArea infoText = new JTextArea(
                "• Member 1 (Authentication & Security): ACTIVE\n" +
                "  PBKDF2 cryptographic password hashing, User DAO, and secure session management verified.\n\n" +
                "• Member 2 (Project & Membership Management): Ready for integration.\n" +
                "• Member 3 (Task Management & Progress): Ready for integration.\n" +
                "• Member 4 (Allocation Engine & Reports): Ready for integration."
        );
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoText.setForeground(UITheme.TEXT_PRIMARY);
        infoText.setBackground(new Color(241, 245, 249));
        infoText.setEditable(false);
        infoText.setBorder(new CompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        integrationCard.add(infoHeader, BorderLayout.NORTH);
        integrationCard.add(infoText, BorderLayout.CENTER);

        centerPanel.add(profileCard);
        centerPanel.add(integrationCard);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel createDetailRow(String labelText, String valueText) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setBackground(UITheme.CARD_BG);

        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_LABEL);
        label.setForeground(UITheme.TEXT_MUTED);

        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Segoe UI", Font.BOLD, 13));
        value.setForeground(UITheme.TEXT_PRIMARY);

        row.add(label);
        row.add(value);
        return row;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out of your session?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}
