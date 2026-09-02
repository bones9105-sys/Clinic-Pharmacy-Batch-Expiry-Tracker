package com.pharmacy.inventory.dao;

import com.pharmacy.inventory.model.Batch;
import com.pharmacy.inventory.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

// this class handles CRUD operations for the Batches table
public class BatchDAO {

    // CREATE - add a new batch for a medicine
    public void addBatch(Batch b) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO Batches (medicine_id, batch_number, expiry_date, quantity, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, b.getMedicineId());
            ps.setString(2, b.getBatchNumber());
            ps.setString(3, b.getExpiryDate()); // format: YYYY-MM-DD
            ps.setInt(4, b.getQuantity());
            ps.setString(5, b.getStatus());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Batch added successfully!");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // READ - get all batches as a list
    public ArrayList<Batch> getAllBatches() {
        ArrayList<Batch> list = new ArrayList<Batch>();
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM Batches";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Batch b = new Batch(
                        rs.getInt("batch_id"),
                        rs.getInt("medicine_id"),
                        rs.getString("batch_number"),
                        rs.getString("expiry_date"),
                        rs.getInt("quantity"),
                        rs.getString("status")
                );
                list.add(b);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE - change the quantity of a batch
    public void updateBatchQuantity(int batchId, int newQuantity) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "UPDATE Batches SET quantity = ? WHERE batch_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, newQuantity);
            ps.setInt(2, batchId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Batch quantity updated!");
            } else {
                System.out.println("Batch not found.");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DELETE - remove a batch
    public void deleteBatch(int batchId) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "DELETE FROM Batches WHERE batch_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, batchId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Batch deleted!");
            } else {
                System.out.println("Batch not found.");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}