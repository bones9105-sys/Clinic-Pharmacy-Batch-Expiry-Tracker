package com.pharmacy.inventory.dao;

import com.pharmacy.inventory.model.StockLedger;
import com.pharmacy.inventory.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// =====================================================
// LedgerDAO (Data Access Object for Stock_Ledger)
// Done by: Sandesh Aryal
// Handles saving and reading stock movements.
// Also updates the Stock table at the same time,
// so the ledger and stock always match.
// =====================================================
public class LedgerDAO {

    // -------------------------------------------------
    // 1. Record stock coming IN (e.g. new purchase)
    // -------------------------------------------------
    public boolean recordStockIn(int medicineId, int quantity, String reason) {
        String ledgerSql = "INSERT INTO Stock_Ledger (medicine_id, movement_type, quantity, reason, movement_date) " +
                "VALUES (?, 'IN', ?, ?, CURDATE())";
        String stockSql  = "UPDATE Stock SET total_quantity = total_quantity + ? WHERE medicine_id = ?";

        try (Connection conn = DBConnection.getConnection()) {

            // write to the ledger
            try (PreparedStatement ps = conn.prepareStatement(ledgerSql)) {
                ps.setInt(1, medicineId);
                ps.setInt(2, quantity);
                ps.setString(3, reason);
                ps.executeUpdate();
            }

            // increase the stock
            try (PreparedStatement ps = conn.prepareStatement(stockSql)) {
                ps.setInt(1, quantity);
                ps.setInt(2, medicineId);
                ps.executeUpdate();
            }

            System.out.println("IN: " + quantity + " units added to medicine ID " + medicineId +
                    " (" + reason + ")");
            return true;

        } catch (SQLException e) {
            System.out.println("Error while recording stock IN!");
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------
    // 2. Record stock going OUT (e.g. a sale)
    // -------------------------------------------------
    public boolean recordStockOut(int medicineId, int quantity, String reason) {
        String checkSql  = "SELECT total_quantity FROM Stock WHERE medicine_id = ?";
        String ledgerSql = "INSERT INTO Stock_Ledger (medicine_id, movement_type, quantity, reason, movement_date) " +
                "VALUES (?, 'OUT', ?, ?, CURDATE())";
        String stockSql  = "UPDATE Stock SET total_quantity = total_quantity - ? WHERE medicine_id = ?";

        try (Connection conn = DBConnection.getConnection()) {

            // first check we have enough stock
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, medicineId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int current = rs.getInt("total_quantity");
                    if (current < quantity) {
                        System.out.println("Not enough stock! Only " + current + " units available.");
                        return false;
                    }
                } else {
                    System.out.println("No stock record found for medicine ID " + medicineId);
                    return false;
                }
            }

            // write to the ledger
            try (PreparedStatement ps = conn.prepareStatement(ledgerSql)) {
                ps.setInt(1, medicineId);
                ps.setInt(2, quantity);
                ps.setString(3, reason);
                ps.executeUpdate();
            }

            // decrease the stock
            try (PreparedStatement ps = conn.prepareStatement(stockSql)) {
                ps.setInt(1, quantity);
                ps.setInt(2, medicineId);
                ps.executeUpdate();
            }

            System.out.println("OUT: " + quantity + " units removed from medicine ID " + medicineId +
                    " (" + reason + ")");
            return true;

        } catch (SQLException e) {
            System.out.println("Error while recording stock OUT!");
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------
    // 3. View the full ledger (all movements)
    // -------------------------------------------------
    public List<StockLedger> getAllEntries() {
        List<StockLedger> entries = new ArrayList<>();
        String sql = "SELECT * FROM Stock_Ledger ORDER BY ledger_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                StockLedger entry = new StockLedger(
                        rs.getInt("ledger_id"),
                        rs.getInt("medicine_id"),
                        rs.getString("movement_type"),
                        rs.getInt("quantity"),
                        rs.getString("reason"),
                        rs.getDate("movement_date")
                );
                entries.add(entry);
            }

        } catch (SQLException e) {
            System.out.println("Error while reading ledger!");
            e.printStackTrace();
        }
        return entries;
    }

    // -------------------------------------------------
    // 4. View ledger for ONE medicine (JOIN query)
    // -------------------------------------------------
    public void showLedgerForMedicine(int medicineId) {
        String sql = "SELECT l.*, m.name FROM Stock_Ledger l " +
                "JOIN Medicine m ON l.medicine_id = m.medicine_id " +
                "WHERE l.medicine_id = ? ORDER BY l.ledger_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, medicineId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== LEDGER for Medicine ID " + medicineId + " =====");
            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println(rs.getDate("movement_date") +
                        " | " + rs.getString("movement_type") +
                        " | Qty: " + rs.getInt("quantity") +
                        " | " + rs.getString("reason"));
            }

            if (!found) {
                System.out.println("No ledger entries for this medicine.");
            }

        } catch (SQLException e) {
            System.out.println("Error while reading ledger!");
            e.printStackTrace();
        }
    }
}