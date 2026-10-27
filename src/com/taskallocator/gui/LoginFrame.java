package com.taskallocator.gui;

import com.taskallocator.exception.AppException;
import com.taskallocator.model.User;
import com.taskallocator.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Swing login window for student authentication.
 * Uses layout managers, JPasswordField, and SwingWorker to perform database
 * authentication asynchronously off the Event Dispatch Thread (EDT).
 */
public class LoginFrame extends JFrame {

    private final AuthService authService;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;

    public LoginFrame() {
        this.authService = new AuthService();
        initUI();
    }

    private void initUI() {
        setTitle("Student Login — Project Task & Collaboration Allocator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 520);
        setMinimumSize(new Dimension(420, 480));
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        backgroundPanel.setBackground(UITheme.BG_LIGHT);

        // Main Login Card
        JPanel cardPanel = UITheme.createCardPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setPreferredSize(new Dimension(400, 440));

        // 1. Header Section
        JLabel appTitle = new JLabel("Task & Collaboration Allocator");
        appTitle.setFont(UITheme.FONT_TITLE);
        appTitle.setForeground(UITheme.NAVY_PRIMARY);
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitle = new JLabel("Sign in to access your project workspace");
        subTitle.setFont(UITheme.FONT_SUBTITLE);
        subTitle.setForeground(UITheme.TEXT_MUTED);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.add(appTitle);
        cardPanel.add(Box.createVerticalStrut(4));
        cardPanel.add(subTitle);
        cardPanel.add(Box.createVerticalStrut(24));

        // 2. Email Field Section
        JLabel emailLabel = UITheme.createFieldLabel("College Email Address");
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField = UITheme.createTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardPanel.add(emailLabel);
        cardPanel.add(Box.createVerticalStrut(6));
        cardPanel.add(emailField);
        cardPanel.add(Box.createVerticalStrut(14));

        // 3. Password Field Section
        JLabel passwordLabel = UITheme.createFieldLabel("Password");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = UITheme.createPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Allow pressing Enter in password field to trigger login
        passwordField.addActionListener(this::handleLogin);

        cardPanel.add(passwordLabel);
        cardPanel.add(Box.createVerticalStrut(6));
        cardPanel.add(passwordField);
        cardPanel.add(Box.createVerticalStrut(10));

        // Status / Error message label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(UITheme.ERROR_COLOR);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(statusLabel);
        cardPanel.add(Box.createVerticalStrut(12));

        // 4. Action Buttons Section
        loginButton = UITheme.createPrimaryButton("Sign In");
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(this::handleLogin);

        registerButton = UITheme.createSecondaryButton("Create New Account");
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.addActionListener(e -> navigateToRegister());

        cardPanel.add(loginButton);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(registerButton);

        backgroundPanel.add(cardPanel);
        setContentPane(backgroundPanel);
    }

    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        char[] password = passwordField.getPassword();

        if (email.isEmpty()) {
            statusLabel.setText("Please enter your email address.");
            emailField.requestFocus();
            return;
        }
        if (password.length == 0) {
            statusLabel.setText("Please enter your password.");
            passwordField.requestFocus();
            return;
        }

        // Disable buttons and show processing status to prevent duplicate clicks
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        loginButton.setText("Authenticating...");
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        statusLabel.setText("Verifying credentials with database...");

        // Execute authentication in background using SwingWorker
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.login(email, password);
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                registerButton.setEnabled(true);
                loginButton.setText("Sign In");

                try {
                    User authenticatedUser = get();
                    statusLabel.setText(" ");
                    dispose();
                    // Open authenticated landing frame
                    SwingUtilities.invokeLater(() -> new LandingFrame(authenticatedUser).setVisible(true));
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    statusLabel.setForeground(UITheme.ERROR_COLOR);
                    statusLabel.setText(cause.getMessage());
                    passwordField.setText("");
                    passwordField.requestFocus();
                }
            }
        };

        worker.execute();
    }

    private void navigateToRegister() {
        dispose();
        SwingUtilities.invokeLater(() -> new RegisterFrame().setVisible(true));
    }
}
