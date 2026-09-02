package com.pharmacy.inventory.service;

import com.pharmacy.inventory.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// =====================================================
// ExpiryAlertEngine
// Done by: Krish Gupta
// Scans all batches and raises alerts for expired and
// soon-to-expire medicines.
// =====================================================
public class ExpiryAlertEngine {

    // Alert thresholds (in days)
    public static final int CRITICAL_DAYS = 30;   // urgent!
    public static final int WARNING_DAYS = 60;    // warning
    public static final int INFO_DAYS     = 90;   // just info

    // -------------------------------------------------
    // 1. Show batches expiring within the next X days
    //    (advanced query: JOIN + DATEDIFF)
    // -------------------------------------------------
    public void checkExpiringBatches(int days) {
        String sql = "SELECT b.batch_number, b.expiry_date, b.quantity, m.name, " +
                "DATEDIFF(b.expiry_date, CURDATE()) AS days_left " +
                "FROM Batches b " +
                "JOIN Medicine m ON b.medicine_id = m.medicine_id " +
                "WHERE b.status = 'ACTIVE' " +
                "AND b.expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                "ORDER BY b.expiry_date";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, days);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== EXPIRY ALERTS (next " + days + " days) =====");
            boolean found = false;

            while (rs.next()) {
                found = true;
                int daysLeft = rs.getInt("days_left");

                System.out.println(getSeverity(daysLeft) + " " + rs.getString("name") +
                        " | Batch: " + rs.getString("batch_number") +
                        " | Expires: " + rs.getDate("expiry_date") +
                        " | " + daysLeft + " days left" +
                        " | Qty: " + rs.getInt("quantity"));
            }

            if (!found) {
                System.out.println("No batches expiring in the next " + days + " days. All good!");
            }

        } catch (SQLException e) {
            System.out.println("Error while checking expiring batches!");
            e.printStackTrace();
        }
    }

    // -------------------------------------------------
    // 2. Show batches that are ALREADY expired
    // -------------------------------------------------
    public void checkExpiredBatches() {
        String sql = "SELECT b.batch_number, b.expiry_date, b.quantity, m.name " +
                "FROM Batches b " +
                "JOIN Medicine m ON b.medicine_id = m.medicine_id " +
                "WHERE b.expiry_date < CURDATE() AND b.status = 'ACTIVE' " +
                "ORDER BY b.expiry_date";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== EXPIRED BATCHES =====");
            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("[EXPIRED] " + rs.getString("name") +
                        " | Batch: " + rs.getString("batch_number") +
                        " | Expired on: " + rs.getDate("expiry_date") +
                        " | Qty: " + rs.getInt("quantity"));
            }

            if (!found) {
                System.out.println("No expired batches. All good!");
            }

        } catch (SQLException e) {
            System.out.println("Error while checking expired batches!");
            e.printStackTrace();
        }
    }

    // -------------------------------------------------
    // 3. Mark all expired batches as EXPIRED in the DB
    // -------------------------------------------------
    public int markExpiredBatches() {
        String sql = "UPDATE Batches SET status = 'EXPIRED' " +
                "WHERE expiry_date < CURDATE() AND status = 'ACTIVE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int rows = ps.executeUpdate();
            System.out.println("\n" + rows + " batch(es) marked as EXPIRED.");
            return rows;

        } catch (SQLException e) {
            System.out.println("Error while marking expired batches!");
            e.printStackTrace();
        }
        return 0;
    }

    // -------------------------------------------------
    // 4. Summary report: expiring units per medicine
    //    (advanced query: JOIN + GROUP BY + SUM/COUNT)
    // -------------------------------------------------
    public void expirySummary() {
        String sql = "SELECT m.name, COUNT(*) AS expiring_batches, SUM(b.quantity) AS total_units " +
                "FROM Batches b " +
                "JOIN Medicine m ON b.medicine_id = m.medicine_id " +
                "WHERE b.status = 'ACTIVE' " +
                "AND b.expiry_date <= DATE_ADD(CURDATE(), INTERVAL 90 DAY) " +
                "GROUP BY m.name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== EXPIRY SUMMARY (next 90 days) =====");
            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println(rs.getString("name") +
                        " | Batches expiring: " + rs.getInt("expiring_batches") +
                        " | Units at risk: " + rs.getInt("total_units"));
            }

            if (!found) {
                System.out.println("Nothing expiring in the next 90 days.");
            }

        } catch (SQLException e) {
            System.out.println("Error while generating summary!");
            e.printStackTrace();
        }
    }

    // -------------------------------------------------
    // 5. Run the full daily check (calls everything)
    // -------------------------------------------------
    public void runDailyCheck() {
        System.out.println("\n########## DAILY EXPIRY CHECK ##########");
        checkExpiredBatches();
        checkExpiringBatches(INFO_DAYS);
        expirySummary();
        markExpiredBatches();
        System.out.println("########## CHECK COMPLETE ##########");
    }

    // Helper: pick alert level based on days left
    private String getSeverity(int daysLeft) {
        if (daysLeft <= CRITICAL_DAYS) return "[CRITICAL]";
        if (daysLeft <= WARNING_DAYS)  return "[WARNING]";
        return "[INFO]";
    }
}