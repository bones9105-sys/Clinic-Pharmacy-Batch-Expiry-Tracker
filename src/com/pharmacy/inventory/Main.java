package com.pharmacy.inventory;

import com.pharmacy.inventory.dao.BatchDAO;
import com.pharmacy.inventory.dao.LedgerDAO;
import com.pharmacy.inventory.dao.MedicineDAO;
import com.pharmacy.inventory.dao.StockDAO;
import com.pharmacy.inventory.model.Batch;
import com.pharmacy.inventory.model.Medicine;
import com.pharmacy.inventory.model.StockLedger;
import com.pharmacy.inventory.service.BatchRecallSystem;
import com.pharmacy.inventory.service.ExpiryAlertEngine;

import java.util.List;
import java.util.Scanner;

// =====================================================
// Clinic Pharmacy Batch Expiry Tracker
// Team: Ashish Swar, Krish Gupta, Sandesh Aryal, Krish Paudel
// Main menu system - ties all modules together
// (Console flow done by: Sandesh Aryal)
// =====================================================
public class Main {

    public static void main(String[] args) {

        // create all our helper objects
        Scanner scanner = new Scanner(System.in);
        MedicineDAO medicineDAO = new MedicineDAO();
        BatchDAO batchDAO = new BatchDAO();
        StockDAO stockDAO = new StockDAO();
        LedgerDAO ledgerDAO = new LedgerDAO();
        ExpiryAlertEngine alertEngine = new ExpiryAlertEngine();
        BatchRecallSystem recallSystem = new BatchRecallSystem();

        int choice = -1;

        // main menu loop - keeps running until user picks 0
        while (choice != 0) {
            System.out.println("\n========================================");
            System.out.println("  CLINIC PHARMACY INVENTORY SYSTEM");
            System.out.println("========================================");
            System.out.println("1. View all medicines");
            System.out.println("2. Add new medicine");
            System.out.println("3. View all batches");
            System.out.println("4. Record stock IN (purchase)");
            System.out.println("5. Record stock OUT (sale)");
            System.out.println("6. View stock ledger");
            System.out.println("7. Run expiry check");
            System.out.println("8. Recall a batch");
            System.out.println("9. View low stock warnings");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // clear the leftover newline

            if (choice == 1) {
                // view all medicines
                List<Medicine> medicines = medicineDAO.getAllMedicines();
                System.out.println("\n===== ALL MEDICINES =====");
                for (Medicine m : medicines) {
                    System.out.println(m.getId() + " | " + m.getName()
                            + " | " + m.getCategory() + " | Rs." + m.getPrice());
                }

            } else if (choice == 2) {
                // add a new medicine
                System.out.print("Name: ");
                String name = scanner.nextLine();
                System.out.print("Manufacturer: ");
                String manufacturer = scanner.nextLine();
                System.out.print("Category: ");
                String category = scanner.nextLine();
                System.out.print("Unit price: ");
                double price = scanner.nextDouble();

                Medicine newMed = new Medicine(0, name, price, manufacturer, category);
                medicineDAO.addMedicine(newMed);

            } else if (choice == 3) {
                // view all batches
                List<Batch> batches = batchDAO.getAllBatches();
                System.out.println("\n===== ALL BATCHES =====");
                for (Batch b : batches) {
                    System.out.println(b.getBatchId() + " | Med ID: " + b.getMedicineId()
                            + " | " + b.getBatchNumber() + " | Expiry: " + b.getExpiryDate()
                            + " | Qty: " + b.getQuantity() + " | " + b.getStatus());
                }

            } else if (choice == 4) {
                // stock coming in
                System.out.print("Medicine ID: ");
                int medId = scanner.nextInt();
                System.out.print("Quantity: ");
                int qty = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Reason (e.g. Purchase): ");
                String reason = scanner.nextLine();

                ledgerDAO.recordStockIn(medId, qty, reason);

            } else if (choice == 5) {
                // stock going out
                System.out.print("Medicine ID: ");
                int medId = scanner.nextInt();
                System.out.print("Quantity: ");
                int qty = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Reason (e.g. Sale): ");
                String reason = scanner.nextLine();

                ledgerDAO.recordStockOut(medId, qty, reason);

            } else if (choice == 6) {
                // view the ledger
                List<StockLedger> entries = ledgerDAO.getAllEntries();
                System.out.println("\n===== STOCK LEDGER =====");
                for (StockLedger e : entries) {
                    System.out.println(e.getLedgerId() + " | Med ID: " + e.getMedicineId()
                            + " | " + e.getMovementType() + " | Qty: " + e.getQuantity()
                            + " | " + e.getReason() + " | " + e.getMovementDate());
                }

            } else if (choice == 7) {
                // Krish Gupta's expiry engine
                alertEngine.runDailyCheck();

            } else if (choice == 8) {
                // Krish Gupta's recall system
                System.out.print("Batch number to recall: ");
                String batchNumber = scanner.nextLine();
                recallSystem.recallBatch(batchNumber);
                recallSystem.viewRecalledBatches();

            } else if (choice == 9) {
                // low stock warning
                stockDAO.showLowStock();

            } else if (choice == 0) {
                System.out.println("Goodbye!");

            } else {
                System.out.println("Invalid choice, try again.");
            }
        }

        scanner.close();
    }
}