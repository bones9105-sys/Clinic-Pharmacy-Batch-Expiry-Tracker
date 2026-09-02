package com.pharmacy.inventory.service;

import com.pharmacy.inventory.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// =====================================================
// BatchRecallSystem
// Done by: Krish Gupta
// Handles product recalls: marks batches as RECALLED
// and removes their quantity from stock.
// =====================================================
public class BatchRecallSystem {

    // -------------------------------------------------
    // 1. Recall a single batch by its batch number
    // -------------------------------------------------
    public boolean recallBatch(String batchNumber) {
        String findSql   = "SELECT medicine_id, quantity, status FROM Batches WHERE batch_number = ?";
        String updateSql = "UPDATE Batches SET status = 'RECALLED' WHERE batch_number = ?";
        String stockSql  = "UPDATE Stock SET total_quantity = total_quantity - ? WHERE medicine_id = ?";

        try (Connection conn = DBConnection.getConnection()) {

            // Step 1: find the batch (we need its quantity + medicine_id)
            int medicineId, quantity;
            String status;

            try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                ps.setString(1, batchNumber);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    System.out.println("Batch " + batchNumber + " not found!");
                    return false;
                }
                medicineId = rs.getInt("medicine_id");
                quantity   = rs.getInt("quantity");
                status     = rs.getString("status");
            }

            // Safety check: don't recall twice
            if (status.equals("RECALLED")) {
                System.out.println("Batch " + batchNumber + " is already recalled!");
                return false;
            }

            // Step 2: mark the batch as RECALLED
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, batchNumber);
                ps.executeUpdate();
            }

            // Step 3: remove its quantity from stock
            try (PreparedStatement ps = conn.prepareStatement(stockSql)) {
                ps.setInt(1, quantity);
                ps.setInt(2, medicineId);
                ps.executeUpdate();
            }

            System.out.println("Batch " + batchNumber + " RECALLED. " +
                    quantity + " units removed from stock.");
            return true;

        } catch (SQLException e) {
            System.out.println("Error while recalling batch!");
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------
    // 2. Recall ALL active batches of a medicine
    // -------------------------------------------------
    public int recallMedicine(int medicineId) {
        String findSql = "SELECT batch_number FROM Batches WHERE medicine_id = ? AND status = 'ACTIVE'";
        int count = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(findSql)) {

            ps.setInt(1, medicineId);
            ResultSet rs = ps.executeQuery();

            // Reuse recallBatch() for each batch found
            while (rs.next()) {
                if (recallBatch(rs.getString("batch_number"))) {
                    count++;
                }
            }

            System.out.println(count + " batch(es) of medicine ID " + medicineId + " recalled.");

        } catch (SQLException e) {
            System.out.println("Error while recalling medicine!");
            e.printStackTrace();
        }
        return count;
    }

    // -------------------------------------------------
    // 3. Recall report: show all recalled batches
    // -------------------------------------------------
    public void viewRecalledBatches() {
        String sql = "SELECT b.batch_number, b.expiry_date, b.quantity, m.name " +
                "FROM Batches b " +
                "JOIN Medicine m ON b.medicine_id = m.medicine_id " +
                "WHERE b.status = 'RECALLED' " +
                "ORDER BY b.batch_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== RECALLED BATCHES =====");
            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("[RECALLED] " + rs.getString("name") +
                        " | Batch: " + rs.getString("batch_number") +
                        " | Qty: " + rs.getInt("quantity"));
            }

            if (!found) {
                System.out.println("No recalled batches.");
            }

        } catch (SQLException e) {
            System.out.println("Error while viewing recalls!");
            e.printStackTrace();
        }
    }
}