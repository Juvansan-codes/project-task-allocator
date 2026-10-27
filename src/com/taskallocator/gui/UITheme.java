package com.taskallocator.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Centralized UI Theme for the application.
 * Defines a professional college desktop aesthetic:
 * Clean light background (#F8FAFC), dark navy/blue elements (#1E3A8A), and restrained orange accents (#EA580C).
 */
public final class UITheme {

    public static final Color NAVY_PRIMARY = new Color(30, 58, 138);       // #1E3A8A
    public static final Color NAVY_DARK = new Color(23, 37, 84);           // #172554
    public static final Color BLUE_SECONDARY = new Color(37, 99, 235);     // #2563EB
    public static final Color ORANGE_ACCENT = new Color(234, 88, 12);      // #EA580C
    public static final Color BG_LIGHT = new Color(248, 250, 252);         // #F8FAFC
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);        // #0F172A
    public static final Color TEXT_MUTED = new Color(100, 116, 139);       // #64748B
    public static final Color BORDER_COLOR = new Color(203, 213, 225);     // #CBD5E1
    public static final Color SUCCESS_COLOR = new Color(22, 163, 74);      // #16A34A
    public static final Color ERROR_COLOR = new Color(220, 38, 38);        // #DC2626

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    private UITheme() {
        // Prevent instantiation
    }

    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(NAVY_PRIMARY);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new LineBorder(NAVY_DARK, 1, true),
                new EmptyBorder(10, 20, 10, 20)
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(NAVY_DARK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(NAVY_PRIMARY);
            }
        });
        return button;
    }

    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setForeground(NAVY_PRIMARY);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(241, 245, 249));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
        return button;
    }

    public static JButton createAccentButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(ORANGE_ACCENT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new LineBorder(new Color(194, 65, 12), 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        return button;
    }

    public static JTextField createTextField() {
        JTextField field = new JTextField();
        styleTextInput(field);
        return field;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        styleTextInput(field);
        return field;
    }

    private static void styleTextInput(JTextField field) {
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setCaretColor(NAVY_PRIMARY);
        Border outer = new LineBorder(BORDER_COLOR, 1, true);
        Border inner = new EmptyBorder(8, 10, 8, 10);
        field.setBorder(new CompoundBorder(outer, inner));
    }

    public static JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(24, 28, 24, 28)
        ));
        return panel;
    }
}
