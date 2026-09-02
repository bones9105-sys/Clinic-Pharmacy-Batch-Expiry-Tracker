package com.pharmacy.inventory.dao;

import com.pharmacy.inventory.model.StockItem;
import com.pharmacy.inventory.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

// this class handles CRUD operations for the Stock table
public class StockDAO {

    // CREATE - add a stock entry when a new medicine is added
    public void addStock(StockItem s) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO Stock (medicine_id, total_quantity, reorder_level) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, s.getMedicineId());
            ps.setInt(2, s.getQuantity());
            ps.setInt(3, s.getReorderLevel());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Stock entry added!");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // READ - get the stock details of one medicine
    public StockItem getStockByMedicineId(int medicineId) {
        StockItem s = null;
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM Stock WHERE medicine_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, medicineId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                s = new StockItem(
                        rs.getInt("stock_id"),
                        rs.getInt("medicine_id"),
                        rs.getInt("total_quantity"),
                        rs.getInt("reorder_level")
                );
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    // READ - get all stock entries as a list
    public ArrayList<StockItem> getAllStock() {
        ArrayList<StockItem> list = new ArrayList<StockItem>();
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM Stock";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                StockItem s = new StockItem(
                        rs.getInt("stock_id"),
                        rs.getInt("medicine_id"),
                        rs.getInt("total_quantity"),
                        rs.getInt("reorder_level")
                );
                list.add(s);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE - change total quantity after a sale or new purchase
    public void updateQuantity(int medicineId, int newQuantity) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "UPDATE Stock SET total_quantity = ? WHERE medicine_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, newQuantity);
            ps.setInt(2, medicineId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Stock quantity updated!");
            } else {
                System.out.println("Stock entry not found.");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DELETE - remove a stock entry
    public void deleteStock(int stockId) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "DELETE FROM Stock WHERE stock_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, stockId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Stock entry deleted!");
            } else {
                System.out.println("Stock entry not found.");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // READ - show medicines that are low on stock
    // this one joins with the Medicine table so we can print the medicine name
    public void showLowStock() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT m.name, s.total_quantity, s.reorder_level "
                    + "FROM Stock s JOIN Medicine m ON s.medicine_id = m.medicine_id "
                    + "WHERE s.total_quantity <= s.reorder_level";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("---- Low Stock Medicines ----");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(rs.getString("name")
                        + " | Left: " + rs.getInt("total_quantity")
                        + " | Reorder Level: " + rs.getInt("reorder_level"));
            }
            if (!found) {
                System.out.println("All medicines have enough stock.");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}