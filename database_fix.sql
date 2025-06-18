-- Fix the role column in users table to accommodate PROVIDER role
-- Run this script manually in your MySQL database

USE BookingAppAggy;

-- Check current table structure
DESCRIBE users;

-- Update the role column to include PROVIDER
ALTER TABLE users MODIFY COLUMN role ENUM('USER', 'ADMIN', 'PROVIDER') NOT NULL;

-- Verify the change
DESCRIBE users;

-- You can also check existing data
SELECT username, email, role FROM users;
