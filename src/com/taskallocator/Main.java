package com.taskallocator;

import com.taskallocator.gui.LoginFrame;
import com.taskallocator.util.DBConnection;

import javax.swing.*;

/**
 * Application entry point for Project Task & Collaboration Allocator.
 * Initializes database configuration and launches the Swing GUI on the Event Dispatch Thread (EDT).
 */
public class Main {

    public static void main(String[] args) {
        // Initialize DB configuration
        DBConnection.loadConfiguration();

        // Use system look and feel for native OS appearance where supported
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Launch GUI strictly on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
