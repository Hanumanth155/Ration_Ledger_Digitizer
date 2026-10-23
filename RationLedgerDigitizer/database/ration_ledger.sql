CREATE DATABASE IF NOT EXISTS ration_ledger;
USE ration_ledger;

DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS ration_distributions;
DROP TABLE IF EXISTS announcements;
DROP TABLE IF EXISTS stock_transactions;
DROP TABLE IF EXISTS stock;
DROP TABLE IF EXISTS ration_allocations;
DROP TABLE IF EXISTS distribution_schedule;
DROP TABLE IF EXISTS beneficiaries;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(80) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    role ENUM('ADMIN','USER') NOT NULL,
    status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE beneficiaries (
    beneficiary_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE,
    ration_card_number VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    mobile VARCHAR(15) NOT NULL,
    email VARCHAR(120),
    address VARCHAR(255),
    family_size INT NOT NULL DEFAULT 1,
    card_type VARCHAR(30) DEFAULT 'PHH',
    status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE ration_allocations (
    allocation_id INT PRIMARY KEY AUTO_INCREMENT,
    allocation_month INT NOT NULL,
    allocation_year INT NOT NULL,
    rice_kg DECIMAL(10,2) DEFAULT 0,
    wheat_kg DECIMAL(10,2) DEFAULT 0,
    sugar_kg DECIMAL(10,2) DEFAULT 0,
    dal_kg DECIMAL(10,2) DEFAULT 0,
    other_item VARCHAR(80),
    other_kg DECIMAL(10,2) DEFAULT 0,
    status ENUM('DRAFT','PUBLISHED','CLOSED') DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_allocation_month (allocation_month, allocation_year)
);

CREATE TABLE distribution_schedule (
    schedule_id INT PRIMARY KEY AUTO_INCREMENT,
    allocation_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    start_time TIME DEFAULT '10:00:00',
    end_time TIME DEFAULT '17:00:00',
    location VARCHAR(255) NOT NULL,
    notes VARCHAR(500),
    FOREIGN KEY (allocation_id) REFERENCES ration_allocations(allocation_id) ON DELETE CASCADE
);

CREATE TABLE ration_distributions (
    distribution_id INT PRIMARY KEY AUTO_INCREMENT,
    beneficiary_id INT NOT NULL,
    allocation_id INT NOT NULL,
    rice_kg DECIMAL(10,2) DEFAULT 0,
    wheat_kg DECIMAL(10,2) DEFAULT 0,
    sugar_kg DECIMAL(10,2) DEFAULT 0,
    dal_kg DECIMAL(10,2) DEFAULT 0,
    other_kg DECIMAL(10,2) DEFAULT 0,
    distribution_date DATE NOT NULL,
    status ENUM('COLLECTED','MISSED','CANCELLED') DEFAULT 'COLLECTED',
    receipt_no VARCHAR(60) UNIQUE,
    remarks VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (beneficiary_id) REFERENCES beneficiaries(beneficiary_id),
    FOREIGN KEY (allocation_id) REFERENCES ration_allocations(allocation_id),
    UNIQUE KEY uq_beneficiary_allocation (beneficiary_id, allocation_id)
);

CREATE TABLE announcements (
    announcement_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(180) NOT NULL,
    description TEXT NOT NULL,
    announcement_type ENUM('GENERAL','RATION','IMPORTANT','STOCK') DEFAULT 'GENERAL',
    published_date DATE NOT NULL,
    expiry_date DATE,
    is_published BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE stock (
    stock_id INT PRIMARY KEY AUTO_INCREMENT,
    item_name VARCHAR(60) NOT NULL UNIQUE,
    opening_stock DECIMAL(12,2) DEFAULT 0,
    received_stock DECIMAL(12,2) DEFAULT 0,
    distributed_stock DECIMAL(12,2) DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE stock_transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    stock_id INT NOT NULL,
    transaction_type ENUM('RECEIVED','ADJUSTMENT') NOT NULL,
    quantity DECIMAL(12,2) NOT NULL,
    remarks VARCHAR(255),
    transaction_date DATE NOT NULL,
    FOREIGN KEY (stock_id) REFERENCES stock(stock_id) ON DELETE CASCADE
);

CREATE TABLE audit_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(80),
    action VARCHAR(120) NOT NULL,
    details VARCHAR(500),
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SHA-256 hashes:
-- admin123 -> 240be518fabd272b2e3f8e04c1f2f0f5e7f6c7d1f5f0c3b0d3e6f5a1c8e6f1a4 (demo placeholder replaced below)
-- Use application bootstrap to create users if you prefer.
INSERT INTO users(username,password_hash,role) VALUES
('admin', SHA2('admin123',256), 'ADMIN'),
('user1', SHA2('user123',256), 'USER');

INSERT INTO beneficiaries(user_id,ration_card_number,full_name,mobile,email,address,family_size,card_type)
VALUES
(2,'RC10001','Ramesh Kumar','9876543210','user1@example.com','Bengaluru, Karnataka',4,'PHH');

INSERT INTO ration_allocations(allocation_month,allocation_year,rice_kg,wheat_kg,sugar_kg,dal_kg,other_item,other_kg,status)
VALUES (9,2026,20,10,2,2,'Salt',1,'PUBLISHED');

INSERT INTO distribution_schedule(allocation_id,start_date,end_date,start_time,end_time,location,notes)
VALUES (1,'2026-09-05','2026-09-30','10:00:00','17:00:00','Main Ration Distribution Center','Bring ration card and valid ID.');

INSERT INTO announcements(title,description,announcement_type,published_date,expiry_date,is_published)
VALUES
('September Ration Distribution','September ration distribution is open from 5 September to 30 September.','RATION','2026-09-01','2026-09-30',1),
('Important Notice','Beneficiaries are requested to carry their ration card during collection.','IMPORTANT','2026-09-01','2026-09-30',1);

INSERT INTO stock(item_name,opening_stock,received_stock,distributed_stock) VALUES
('Rice',50000,10000,0),('Wheat',30000,5000,0),('Sugar',10000,2000,0),('Dal',10000,2000,0),('Salt',5000,500,0);
