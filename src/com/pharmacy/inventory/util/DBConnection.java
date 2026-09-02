package com.pharmacy.inventory.util;

import java.sql.Connection;
import java.sql.DriverManager;

// this class handles the connection to the MySQL database
public class DBConnection {

    // change these according to your MySQL setup
    private static final String URL = "jdbc:mysql://localhost:3306/pharmacy_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "R4Odi747#@Go%%"; // put your mysql password here

    // this method gives a connection object to run queries
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            System.out.println("Error while connecting to database!");
            e.printStackTrace();
        }
        return con;
    }
}