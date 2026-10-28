package com.taskallocator;

import com.taskallocator.gui.MainFrame;
import com.taskallocator.util.DBConnection;

import javax.swing.*;

/**
 * =====================================================================
 * Application Entry Point: Project Task & Collaboration Allocator
 * =====================================================================
 * Initializes database configuration and launches the simplified Swing
 * desktop application directly to the main workspace window on the
 * Event Dispatch Thread (EDT).
 */
public class Main {

    public static void main(String[] args) {
        // Initialize DB configuration from config/db.properties
        DBConnection.loadConfiguration();

        // Use system look and feel for native OS appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Launch MainFrame directly on the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
