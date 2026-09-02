package com.pharmacy.inventory.model;

import java.sql.Date;

// =====================================================
// StockLedger (Model class)
// Done by: Sandesh Aryal
// Represents ONE entry in the stock ledger book.
// Every time stock comes IN or goes OUT, we write
// one row here - like a digital version of the
// old paper register at the pharmacy.
// =====================================================
public class StockLedger {

    // private fields = ENCAPSULATION
    private int ledgerId;
    private int medicineId;
    private String movementType;   // "IN" or "OUT"
    private int quantity;
    private String reason;         // e.g. "Purchase", "Sale", "Recall"
    private Date movementDate;

    // constructor
    public StockLedger(int ledgerId, int medicineId, String movementType,
                       int quantity, String reason, Date movementDate) {
        this.ledgerId = ledgerId;
        this.medicineId = medicineId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.reason = reason;
        this.movementDate = movementDate;
    }

    // getters and setters
    public int getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(int ledgerId) {
        this.ledgerId = ledgerId;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getMovementDate() {
        return movementDate;
    }

    public void setMovementDate(Date movementDate) {
        this.movementDate = movementDate;
    }
}