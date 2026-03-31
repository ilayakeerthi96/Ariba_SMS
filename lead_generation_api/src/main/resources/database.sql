-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS lead_capture_db;
USE lead_capture_db;

-- Drop tables in order to avoid dependency issues (if any)
DROP TABLE IF EXISTS lead_capture_db_history;
DROP TABLE IF EXISTS target;
DROP TABLE IF EXISTS lead_capture;

-- ==========================================================
-- 1. Main Lead Capture Table (Based on Lead.java)
-- ==========================================================
CREATE TABLE lead_capture (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Lead Contact Info
    company_name VARCHAR(255),
    address VARCHAR(255),
    contact_person VARCHAR(255),
    job_title VARCHAR(255),
    email VARCHAR(255),
    email1 VARCHAR(255),
    phone VARCHAR(50),
    phone1 VARCHAR(50),
    mobile VARCHAR(50),
    website VARCHAR(255),
    country_region VARCHAR(100),
    source VARCHAR(100),
    technology VARCHAR(255),
    domain VARCHAR(255),
    technical_skills VARCHAR(255),
    remarks VARCHAR(500),

    -- User/Owner Fields
    employee_code VARCHAR(255),
    assigned_to VARCHAR(255),

    -- Migrated Opportunity Fields
    stage VARCHAR(100),
    expected_value DECIMAL(15,2),
    expected_close_date DATE,
    probability INT,

    -- System Fields
    created_on DATETIME,
    is_deleted BIT DEFAULT 0 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================================
-- 2. Target Table (Based on Target.java)
-- ==========================================================
CREATE TABLE target (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    employee_code VARCHAR(50) NOT NULL,

    -- RANGE-BASED TARGETS
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,

    -- OPTIONAL: Helps reporting + filtering
    target_type ENUM('MONTHLY', 'ANNUAL', 'CUSTOM') DEFAULT 'CUSTOM',

    target_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    achieved_amount DECIMAL(15,2) NOT NULL DEFAULT 0,

    created_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Index for fast range queries per employee
    INDEX idx_target_employee_range (employee_code, start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================================
-- 3. Lead History/Audit Table (Based on LeadHistory.java)
-- ==========================================================
CREATE TABLE lead_capture_db_history (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Original Lead ID
    lead_id BIGINT NOT NULL,

    -- Audit Metadata
    updated_on_history DATETIME NOT NULL,
    updated_by_code VARCHAR(255),
    change_type VARCHAR(20), -- 'UPDATE' or 'DELETE'

    -- Mirrored Lead Fields (all nullable)
    company_name VARCHAR(255),
    address VARCHAR(255),
    contact_person VARCHAR(255),
    job_title VARCHAR(255),
    email VARCHAR(255),
    email1 VARCHAR(255),
    phone VARCHAR(50),
    phone1 VARCHAR(50),
    mobile VARCHAR(50),
    website VARCHAR(255),
    country_region VARCHAR(100),
    source VARCHAR(100),
    technology VARCHAR(255),
    domain VARCHAR(255),
    technical_skills VARCHAR(255),
    remarks VARCHAR(500),
    employee_code VARCHAR(255),
    assigned_to VARCHAR(255),
    stage VARCHAR(100),
    expected_value DECIMAL(15,2),
    expected_close_date DATE,
    probability INT,
    created_on DATETIME,
    is_deleted BIT,

    INDEX idx_history_lead_id (lead_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================================
-- Additional ALTER statements
-- ==========================================================
ALTER TABLE `lead_capture_db`.`lead_capture`
ADD COLUMN `other_source` VARCHAR(45) NULL AFTER `is_deleted`;

ALTER TABLE `lead_capture_db`.`lead_capture_db_history`
ADD COLUMN `other_source` VARCHAR(45) NULL AFTER `is_deleted`;

ALTER TABLE `lead_capture_db`.`lead_capture` 
ADD COLUMN `other_source` VARCHAR(45) NULL AFTER `is_deleted`;

ALTER TABLE `lead_capture_db`.`lead_capture_db_history` 
ADD COLUMN `other_source` VARCHAR(45) NULL AFTER `is_deleted`;

ALTER TABLE lead_capture
ADD COLUMN first_name VARCHAR(255) AFTER company_name,
ADD COLUMN middle_name VARCHAR(255) AFTER first_name,
ADD COLUMN last_name VARCHAR(255) AFTER middle_name;

ALTER TABLE lead_capture_db_history
ADD COLUMN first_name VARCHAR(255) AFTER company_name,
ADD COLUMN middle_name VARCHAR(255) AFTER first_name,
ADD COLUMN last_name VARCHAR(255) AFTER middle_name;


ALTER TABLE target 
ADD COLUMN target_unit ENUM('AMOUNT','LEADS','NONE') DEFAULT 'AMOUNT';

ALTER TABLE target 
ADD COLUMN currency VARCHAR(45) NULL AFTER target_unit;

ALTER TABLE `lead_capture_db`.`target` 
CHANGE COLUMN `target_amount` `target_amount` DECIMAL(15,2) NULL DEFAULT '0.00' ,
CHANGE COLUMN `achieved_amount` `achieved_amount` DECIMAL(15,2) NULL DEFAULT '0.00' ;

ALTER TABLE `lead_capture_db`.`lead_capture` 
ADD COLUMN `expected_currency_value` VARCHAR(45) NULL AFTER `other_source`;

ALTER TABLE `lead_capture_db`.`lead_capture_history` 
ADD COLUMN `expected_currency_value` VARCHAR(45) NULL AFTER `other_source`;



-- //Hot and cold lead
ALTER TABLE `lead_capture_db`.`lead_capture` ADD COLUMN contact_type VARCHAR(20);
ALTER TABLE `lead_capture_db`.`lead_capture_db_history` ADD COLUMN contact_type VARCHAR(20);

ALTER TABLE `lead_capture_db`.`lead_capture` ADD COLUMN requirement_type VARCHAR(100);
ALTER TABLE `lead_capture_db`.`lead_capture_db_history` ADD COLUMN requirement_type VARCHAR(100);