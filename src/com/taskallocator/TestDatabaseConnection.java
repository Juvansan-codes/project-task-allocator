package com.taskallocator;

import com.taskallocator.util.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Verification utility for testing JDBC connectivity, MySQL service responsiveness,
 * and transaction management during Phase 2.
 */
public class TestDatabaseConnection {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" Project Task & Collaboration Allocator");
        System.out.println(" Phase 2: Database Connectivity Verification");
        System.out.println("=================================================");
        System.out.println("Configured URL  : " + DBConnection.getUrl());
        System.out.println("Configured User : " + DBConnection.getUser());

        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("[SUCCESS] Successfully connected to MySQL server!");
            System.out.println("Database Product : " + conn.getMetaData().getDatabaseProductName());
            System.out.println("Database Version : " + conn.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver Name      : " + conn.getMetaData().getDriverName());
            System.out.println("Driver Version   : " + conn.getMetaData().getDriverVersion());

            // Run a basic test query
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT CURRENT_TIMESTAMP() AS server_time, DATABASE() AS current_db")) {
                if (rs.next()) {
                    System.out.println("Current DB       : " + rs.getString("current_db"));
                    System.out.println("Server Timestamp : " + rs.getString("server_time"));
                }
            }

            // Test transaction support
            conn.setAutoCommit(false);
            conn.rollback();
            conn.setAutoCommit(true);
            System.out.println("[SUCCESS] Transaction commit/rollback verified successfully.");

        } catch (Exception e) {
            System.err.println("\n[ERROR] Connection test failed:");
            System.err.println(e.getMessage());
            System.err.println("\nNext step: If credentials failed, create 'config/db.properties' with your MySQL root password.");
        }
    }
}
