package com.pharmacy.inventory.model;

// model class for the Batches table
// expiryDate is kept as a String in YYYY-MM-DD format to keep things simple
public class Batch {

    private int batchId;
    private int medicineId;
    private String batchNumber;
    private String expiryDate;
    private int quantity;
    private String status;

    public Batch(int batchId, int medicineId, String batchNumber, String expiryDate, int quantity, String status) {
        this.batchId = batchId;
        this.medicineId = medicineId;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.status = status;
    }

    public int getBatchId() {
        return batchId;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}