package com.taskallocator.gui;

import com.taskallocator.model.User;
import com.taskallocator.model.UserRole;
import com.taskallocator.service.AuthService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Swing registration window for student account creation.
 * Collects student name, email, selected role (Project Leader or Team Member), and password.
 * Performs validation and PBKDF2 hashing via AuthService asynchronously using SwingWorker.
 */
public class RegisterFrame extends JFrame {

    private final AuthService authService;

    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<UserRole> roleComboBox;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;
    private JLabel statusLabel;

    public RegisterFrame() {
        this.authService = new AuthService();
        initUI();
    }

    private void initUI() {
        setTitle("Student Registration — Project Task & Collaboration Allocator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 640);
        setMinimumSize(new Dimension(460, 580));
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        backgroundPanel.setBackground(UITheme.BG_LIGHT);

        // Main Registration Card
        JPanel cardPanel = UITheme.createCardPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setPreferredSize(new Dimension(440, 560));

        // 1. Header Section
        JLabel appTitle = new JLabel("Create Student Account");
        appTitle.setFont(UITheme.FONT_TITLE);
        appTitle.setForeground(UITheme.NAVY_PRIMARY);
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitle = new JLabel("Enter your details to participate in project allocation");
        subTitle.setFont(UITheme.FONT_SUBTITLE);
        subTitle.setForeground(UITheme.TEXT_MUTED);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.add(appTitle);
        cardPanel.add(Box.createVerticalStrut(4));
        cardPanel.add(subTitle);
        cardPanel.add(Box.createVerticalStrut(20));

        // 2. Full Name
        JLabel nameLabel = UITheme.createFieldLabel("Full Name");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameField = UITheme.createTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(nameLabel);
        cardPanel.add(Box.createVerticalStrut(4));
        cardPanel.add(nameField);
        cardPanel.add(Box.createVerticalStrut(10));

        // 3. Email
        JLabel emailLabel = UITheme.createFieldLabel("College Email Address");
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField = UITheme.createTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(emailLabel);
        cardPanel.add(Box.createVerticalStrut(4));
        cardPanel.add(emailField);
        cardPanel.add(Box.createVerticalStrut(10));

        // 4. Role Selection
        JLabel roleLabel = UITheme.createFieldLabel("Account Role");
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleComboBox = new JComboBox<>(new UserRole[]{UserRole.PROJECT_LEADER, UserRole.TEAM_MEMBER});
        roleComboBox.setFont(UITheme.FONT_INPUT);
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        roleComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleComboBox.setBorder(new CompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(4, 6, 4, 6)
        ));
        cardPanel.add(roleLabel);
        cardPanel.add(Box.createVerticalStrut(4));
        cardPanel.add(roleComboBox);
        cardPanel.add(Box.createVerticalStrut(10));

        // 5. Password
        JLabel passwordLabel = UITheme.createFieldLabel("Password (min. 6 characters)");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = UITheme.createPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(passwordLabel);
        cardPanel.add(Box.createVerticalStrut(4));
        cardPanel.add(passwordField);
        cardPanel.add(Box.createVerticalStrut(10));

        // 6. Confirm Password
        JLabel confirmLabel = UITheme.createFieldLabel("Confirm Password");
        confirmLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmPasswordField = UITheme.createPasswordField();
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmPasswordField.addActionListener(this::handleRegister);
        cardPanel.add(confirmLabel);
        cardPanel.add(Box.createVerticalStrut(4));
        cardPanel.add(confirmPasswordField);
        cardPanel.add(Box.createVerticalStrut(8));

        // Status Label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(UITheme.ERROR_COLOR);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(statusLabel);
        cardPanel.add(Box.createVerticalStrut(10));

        // 7. Buttons
        registerButton = UITheme.createPrimaryButton("Register Account");
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.addActionListener(this::handleRegister);

        backButton = UITheme.createSecondaryButton("Back to Login");
        backButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.addActionListener(e -> navigateToLogin());

        cardPanel.add(registerButton);
        cardPanel.add(Box.createVerticalStrut(8));
        cardPanel.add(backButton);

        backgroundPanel.add(cardPanel);
        setContentPane(backgroundPanel);
    }

    private void handleRegister(ActionEvent event) {
        String fullName = nameField.getText().trim();
        String email = emailField.getText().trim();
        UserRole selectedRole = (UserRole) roleComboBox.getSelectedItem();
        char[] password = passwordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();

        registerButton.setEnabled(false);
        backButton.setEnabled(false);
        registerButton.setText("Registering...");
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        statusLabel.setText("Saving account to database...");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.register(fullName, email, password, confirmPassword, selectedRole);
            }

            @Override
            protected void done() {
                registerButton.setEnabled(true);
                backButton.setEnabled(true);
                registerButton.setText("Register Account");

                try {
                    User newUser = get();
                    JOptionPane.showMessageDialog(
                            RegisterFrame.this,
                            "Registration successful!\n\nWelcome, " + newUser.getFullName() + 
                            ".\nYou can now sign in with your email.",
                            "Account Created",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    navigateToLogin();
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    statusLabel.setForeground(UITheme.ERROR_COLOR);
                    statusLabel.setText(cause.getMessage());
                    passwordField.setText("");
                    confirmPasswordField.setText("");
                }
            }
        };

        worker.execute();
    }

    private void navigateToLogin() {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
