-- ============================================================
-- UPGRADE SCRIPT: For databases already initialized with V1–V4
-- Run this ONLY if your DB already has the original tables.
-- ============================================================

-- Step 1: Drop legacy tables (no longer used)
-- WARNING: Back up data first if you need to migrate existing records.
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS departments CASCADE;

-- Step 2: Update Flyway schema history so it matches the new migration filenames.
-- This updates the checksums to reflect renumbered migrations V1/V2.
-- NOTE: Only run this if you are resetting the Flyway baseline.
-- DELETE FROM flyway_schema_history WHERE version IN ('1','2','3','4');

-- Step 3: The ent_departments and ent_employees tables remain unchanged.
-- They were created by the original V3/V4 and are now referenced by V1/V2.
