-- Prasad: Temple Food Notification Platform
CREATE DATABASE IF NOT EXISTS prasad_db;
USE prasad_db;

-- Temples that serve food
CREATE TABLE IF NOT EXISTS temples (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    location_url VARCHAR(500) NOT NULL,
    food_days VARCHAR(120) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- People who receive SMS / WhatsApp alerts
CREATE TABLE IF NOT EXISTS subscribers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE,
    channel ENUM('SMS', 'WHATSAPP') NOT NULL DEFAULT 'SMS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_temple_name ON temples(name);
