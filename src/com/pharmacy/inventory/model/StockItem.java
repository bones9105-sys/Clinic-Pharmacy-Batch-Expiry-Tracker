package com.pharmacy.inventory.model;

// model class for the Stock table
// keeps track of total quantity of a medicine and its reorder level
public class StockItem {

    private int stockId;
    private int medicineId;
    private int quantity;
    private int reorderLevel;

    public StockItem(int stockId, int medicineId, int quantity, int reorderLevel) {
        this.stockId = stockId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
}