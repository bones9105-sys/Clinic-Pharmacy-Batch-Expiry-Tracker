-- =============================================
-- Clinic Pharmacy Batch Expiry Tracker
-- Database Schema
-- Done by: Ashish Swar
-- =============================================
DROP DATABASE IF EXISTS pharmacy_db;

CREATE DATABASE IF NOT EXISTS pharmacy_db;
USE pharmacy_db;

-- Table 1: Medicine
CREATE TABLE Medicine (
                          medicine_id INT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(100) NOT NULL,
                          manufacturer VARCHAR(100),
                          category VARCHAR(50),
                          unit_price DOUBLE
);

-- Table 2: Batches
CREATE TABLE Batches (
                         batch_id INT PRIMARY KEY AUTO_INCREMENT,
                         medicine_id INT,
                         batch_number VARCHAR(50) NOT NULL,
                         expiry_date DATE NOT NULL,
                         quantity INT,
                         status VARCHAR(20) DEFAULT 'ACTIVE',
                         FOREIGN KEY (medicine_id) REFERENCES Medicine(medicine_id)
);

-- Table 3: Stock
CREATE TABLE Stock (
                       stock_id INT PRIMARY KEY AUTO_INCREMENT,
                       medicine_id INT,
                       total_quantity INT DEFAULT 0,
                       reorder_level INT DEFAULT 10,
                       FOREIGN KEY (medicine_id) REFERENCES Medicine(medicine_id)
);

-- Table 4: Stock_Ledger (Sandesh's part)
CREATE TABLE Stock_Ledger (
                              ledger_id INT AUTO_INCREMENT PRIMARY KEY,
                              medicine_id INT NOT NULL,
                              transaction_type VARCHAR(10) NOT NULL,
                              quantity INT NOT NULL,
                              reason VARCHAR(100),
                              transaction_date DATE NOT NULL,
                              FOREIGN KEY (medicine_id) REFERENCES Medicine(medicine_id)
);

-- sample data for testing
INSERT INTO Medicine (name, manufacturer, category, unit_price) VALUES
                                                                    ('Paracetamol 500mg', 'Cipla', 'Painkiller', 2.50),
                                                                    ('Amoxicillin 250mg', 'Sun Pharma', 'Antibiotic', 8.00),
                                                                    ('Cetirizine 10mg', 'Dr Reddys', 'Antihistamine', 1.50),
                                                                    ('Ibuprofen 400mg', 'Cipla', 'Painkiller', 3.00);

INSERT INTO Batches (medicine_id, batch_number, expiry_date, quantity) VALUES
                                                                           (1, 'PCM001', '2027-03-15', 500),
                                                                           (1, 'PCM002', '2026-08-20', 200),
                                                                           (2, 'AMX101', '2026-09-25', 300),
                                                                           (3, 'CTZ050', '2026-11-10', 150);

INSERT INTO Stock (medicine_id, total_quantity, reorder_level) VALUES
                                                                   (1, 700, 100),
                                                                   (2, 300, 50),
                                                                   (3, 150, 40),
                                                                   (4, 400, 60);

-- sample ledger entries
INSERT INTO Stock_Ledger (medicine_id, transaction_type, quantity, reason, transaction_date) VALUES
                                                                                                 (1, 'IN', 500, 'Initial stock', '2026-09-01'),
                                                                                                 (2, 'IN', 300, 'Initial stock', '2026-09-01');
