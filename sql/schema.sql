-- Database setup for Sunrise Dental Clinic

CREATE DATABASE IF NOT EXISTS sunrise_dental
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sunrise_dental;

DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS treatments;
DROP TABLE IF EXISTS dentists;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(64) NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  role VARCHAR(20) NOT NULL,
  CONSTRAINT chk_role CHECK (role IN ('ADMIN', 'RECEPTIONIST'))
);

CREATE TABLE dentists (
  dentist_id INT AUTO_INCREMENT PRIMARY KEY,
  dentist_name VARCHAR(100) NOT NULL,
  specialization VARCHAR(80) NOT NULL
);

CREATE TABLE treatments (
  treatment_id INT AUTO_INCREMENT PRIMARY KEY,
  treatment_name VARCHAR(80) NOT NULL,
  cost DECIMAL(10,2) NOT NULL
);

CREATE TABLE appointments (
  appointment_id INT AUTO_INCREMENT PRIMARY KEY,
  appointment_number VARCHAR(20) NOT NULL UNIQUE,
  patient_name VARCHAR(100) NOT NULL,
  address VARCHAR(255) NOT NULL,
  contact_number VARCHAR(20) NOT NULL,
  dentist_id INT NOT NULL,
  treatment_id INT NOT NULL,
  appointment_date DATE NOT NULL,
  appointment_time TIME NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'BOOKED',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_apt_dentist FOREIGN KEY (dentist_id) REFERENCES dentists(dentist_id),
  CONSTRAINT fk_apt_treatment FOREIGN KEY (treatment_id) REFERENCES treatments(treatment_id),
  CONSTRAINT uq_dentist_slot UNIQUE (dentist_id, appointment_date, appointment_time)
);

CREATE TABLE bills (
  bill_id INT AUTO_INCREMENT PRIMARY KEY,
  bill_number VARCHAR(20) NOT NULL UNIQUE,
  appointment_id INT NOT NULL UNIQUE,
  consultation_fee DECIMAL(10,2) NOT NULL,
  treatment_cost DECIMAL(10,2) NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  billed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_bill_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
);

-- hashed passwords - admin/admin123, receptionist/rec123
INSERT INTO users (username, password_hash, full_name, role) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Nadeesha Perera', 'ADMIN'),
('receptionist', '1270ddbd388e309b1234f4e500ea78a83c9d111040fa6cce86c31df0144a3659', 'Ishara Fernando', 'RECEPTIONIST');

INSERT INTO dentists (dentist_name, specialization) VALUES
('Dr. Nimal Perera', 'General Dentistry'),
('Dr. Anusha Fernando', 'Orthodontics'),
('Dr. Kasun Jayawardena', 'Endodontics'),
('Dr. Dilani Silva', 'Cosmetic Dentistry');

INSERT INTO treatments (treatment_name, cost) VALUES
('Consultation Only', 0.00),
('Teeth Cleaning', 5000.00),
('Dental Filling', 8000.00),
('Tooth Extraction', 6000.00),
('Root Canal Treatment', 25000.00),
('Teeth Whitening', 15000.00),
('Braces Check-up', 4000.00),
('Crown / Bridge', 35000.00);
