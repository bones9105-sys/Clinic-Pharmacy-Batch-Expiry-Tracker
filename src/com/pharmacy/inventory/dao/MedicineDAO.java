package com.pharmacy.inventory.dao;

import com.pharmacy.inventory.model.Medicine;
import com.pharmacy.inventory.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

// DAO = Data Access Object
// this class has all the CRUD methods for the Medicine table
public class MedicineDAO {

    // CREATE - add a new medicine to the database
    public void addMedicine(Medicine m) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO Medicine (name, manufacturer, category, unit_price) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, m.getName());
            ps.setString(2, m.getManufacturer());
            ps.setString(3, m.getCategory());
            ps.setDouble(4, m.getPrice());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Medicine added successfully!");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // READ - get all medicines from the database
    public ArrayList<Medicine> getAllMedicines() {
        ArrayList<Medicine> list = new ArrayList<Medicine>();
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM Medicine";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            // go through each row and make a Medicine object
            while (rs.next()) {
                Medicine m = new Medicine(
                        rs.getInt("medicine_id"),
                        rs.getString("name"),
                        rs.getDouble("unit_price"),
                        rs.getString("manufacturer"),
                        rs.getString("category")
                );
                list.add(m);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE - change the price of a medicine
    public void updateMedicinePrice(int medicineId, double newPrice) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "UPDATE Medicine SET unit_price = ? WHERE medicine_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setDouble(1, newPrice);
            ps.setInt(2, medicineId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Medicine price updated!");
            } else {
                System.out.println("Medicine not found.");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DELETE - remove a medicine from the database
    public void deleteMedicine(int medicineId) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "DELETE FROM Medicine WHERE medicine_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, medicineId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Medicine deleted!");
            } else {
                System.out.println("Medicine not found.");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}