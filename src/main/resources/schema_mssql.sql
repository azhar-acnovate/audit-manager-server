USE [master];
GO

-- Checking if a database with that name exists and if it does, drops it
IF EXISTS (SELECT 1 FROM sys.databases WHERE [name] = 'audit_manager_db')
BEGIN
  ALTER DATABASE audit_manager_db SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
  DROP DATABASE audit_manager_db;
END

-- Creates a new database
CREATE DATABASE audit_manager_db;
GO

-- Checks if a login exists, if not, creates one
IF NOT EXISTS (SELECT * 
               FROM sys.server_principals 
               WHERE [name] = 'auditManagerUser')
BEGIN
    CREATE LOGIN [auditManagerUser] WITH PASSWORD = N'admin@123', 
        DEFAULT_DATABASE = audit_manager_db, 
        CHECK_EXPIRATION = OFF, 
        CHECK_POLICY = OFF;
        
    ALTER LOGIN [auditManagerUser] ENABLE;
END
GO

-- Switch to the new database
USE [audit_manager_db];
GO

-- Create a user for the specified login
CREATE USER [DB_User] FOR LOGIN [auditManagerUser];
GO


-- Check if the user is mapped correctly
USE [audit_manager_db];
GO

-- Map the login to the user
ALTER USER [DB_User] WITH LOGIN = [auditManagerUser];
GO

-- Grant necessary permissions (e.g., db_owner or specific roles)
ALTER ROLE [db_owner] ADD MEMBER [DB_User];
GO


-- Grant read and write permissions
GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA::dbo TO [DB_User];
GO

-- Create the table
CREATE TABLE user_mst (
    id BIGINT IDENTITY(1,1) NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    active BIT DEFAULT 1, -- BIT type is used for boolean values in MSSQL (1 = true, 0 = false)
    profile_image_name VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),
    PRIMARY KEY (id)
);
GO

CREATE TABLE audit_object_change_tracker (
    id BIGINT IDENTITY(1,1) NOT NULL,
    ref_object_id BIGINT,
    event_type VARCHAR(255) NOT NULL,
    event_occurence DATETIME NOT NULL DEFAULT GETDATE(),
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),
    created_by BIGINT,
    updated_by BIGINT,
    active BIT DEFAULT 1 NOT NULL,
    PRIMARY KEY (id)
);
GO

CREATE TABLE audit_attribute_change_tracker (
    id BIGINT IDENTITY(1,1) NOT NULL,
    attribute_name VARCHAR(255) NOT NULL,
    old_value VARCHAR(255) NOT NULL,
    new_value VARCHAR(255) NOT NULL,
    changed_by VARCHAR(255) NOT NULL,
    audit_object_change_tracker_id BIGINT,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),
    created_by BIGINT,
    updated_by BIGINT,
    active BIT DEFAULT 1 NOT NULL,
    CONSTRAINT fk_audit_object_change_tracker
        FOREIGN KEY (audit_object_change_tracker_id)
        REFERENCES audit_object_change_tracker(id),
    PRIMARY KEY (id)
);
GO

-- Alter user_mst adding user_role and user_email - 18-Sep-24
ALTER TABLE user_mst
ADD user_role VARCHAR(100) NULL, user_email varchar(255) NULL;

CREATE UNIQUE INDEX unique_user_name
ON user_mst (user_name);

-- Insert the default user
INSERT INTO user_mst (user_name, password, created_at, updated_at, active, profile_image_name)
VALUES ('admin', '$2a$10$bgR4EcS6I.nIT48XQSj.uO3epcGgWdfl.SbVIIK62FFV3./4KEcu6', 
        GETDATE(), 
        GETDATE(), 
        1, NULL);
GO

-- Insert records into audit_object_change_tracker
INSERT INTO audit_object_change_tracker (ref_object_id, event_type, event_occurence, created_at, updated_at, created_by, updated_by, active)
VALUES 
(0001, 'colorway-create', GETDATE(), GETDATE(), GETDATE(), 1, 1, 1),
(0001, 'colorway-update', GETDATE(), GETDATE(), GETDATE(), 1, 1, 1),
(0001, 'colorway-update', GETDATE(), GETDATE(), GETDATE(), 1, 1, 1),
(0002, 'product-create', GETDATE(), GETDATE(), GETDATE(), 1, 1, 1),
(0002, 'product-update', GETDATE(), GETDATE(), GETDATE(), 1, 1, 1),
(0002, 'product-update', GETDATE(), GETDATE(), GETDATE(), 1, 1, 1);
GO

-- Insert records into audit_attribute_change_tracker
INSERT INTO audit_attribute_change_tracker (attribute_name, old_value, new_value, changed_by, audit_object_change_tracker_id, created_at, updated_at, created_by, updated_by, active)
VALUES 
('color-name', 'blue', 'blue', 'user123', 1, GETDATE(), GETDATE(), 1, 1, 1),
('pattern', 'striped', 'striped', 'user123', 1, GETDATE(), GETDATE(), 1, 1, 1),
('material', 'cotton', 'cotton', 'user123', 1, GETDATE(), GETDATE(), 1, 1, 1),
('release-date', '2024-09-01', '2024-09-01', 'user123', 1, GETDATE(), GETDATE(), 1, 1, 1),
('status', 'pending', 'pending', 'user123', 1, GETDATE(), GETDATE(), 1, 1, 1),
('color-name', 'blue', 'green', 'user123', 2, GETDATE(), GETDATE(), 1, 1, 1),
('pattern', 'striped', 'solid', 'user123', 2, GETDATE(), GETDATE(), 1, 1, 1),
('material', 'cotton', 'polyester', 'user123', 2, GETDATE(), GETDATE(), 1, 1, 1),
('release-date', '2024-09-01', '2024-09-09', 'user123', 2, GETDATE(), GETDATE(), 1, 1, 1),
('status', 'pending', 'approved', 'user123', 2, GETDATE(), GETDATE(), 1, 1, 1),
('color-name', 'green', 'green', 'user123', 3, GETDATE(), GETDATE(), 1, 1, 1),
('pattern', 'solid', 'solid', 'user123', 3, GETDATE(), GETDATE(), 1, 1, 1),
('material', 'polyester', 'lycra', 'user123', 3, GETDATE(), GETDATE(), 1, 1, 1),
('release-date', '2024-09-09', '2024-09-09', 'user123', 3, GETDATE(), GETDATE(), 1, 1, 1),
('status', 'approved', 'canceled', 'user123', 3, GETDATE(), GETDATE(), 1, 1, 1),
('product-name', 'shirt', 'shirt', 'user456', 4, GETDATE(), GETDATE(), 1, 1, 1),
('price', '50.00', '50.00', 'user456', 4, GETDATE(), GETDATE(), 1, 1, 1),
('size', 'M', 'M', 'user456', 4, GETDATE(), GETDATE(), 1, 1, 1),
('availability', 'in-stock', 'in-stock', 'user456', 4, GETDATE(), GETDATE(), 1, 1, 1),
('discount', '5%', '5%', 'user456', 4, GETDATE(), GETDATE(), 1, 1, 1),
('product-name', 'shirt', 'jacket', 'user456', 5, GETDATE(), GETDATE(), 1, 1, 1),
('price', '50.00', '55.00', 'user456', 5, GETDATE(), GETDATE(), 1, 1, 1),
('size', 'M', 'L', 'user456', 5, GETDATE(), GETDATE(), 1, 1, 1),
('availability', 'in-stock', 'out-of-stock', 'user456', 5, GETDATE(), GETDATE(), 1, 1, 1),
('discount', '5%', '10%', 'user456', 5, GETDATE(), GETDATE(), 1, 1, 1),
('product-name', 'jacket', 'Polo-jacket', 'newUser', 6, GETDATE(), GETDATE(), 1, 1, 1),
('price', '50.00', '55.00', 'user456', 6, GETDATE(), GETDATE(), 1, 1, 1),
('size', 'M', 'L', 'user456', 6, GETDATE(), GETDATE(), 1, 1, 1),
('availability', 'in-stock', 'out-of-stock', 'user456', 6, GETDATE(), GETDATE(), 1, 1, 1),
('discount', '5%', '10%', 'user456', 6, GETDATE(), GETDATE(), 1, 1, 1);
GO

CREATE TABLE audit_report (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ref_object_id BIGINT NULL,
    report_name NVARCHAR(255) NULL,
    start_date_range DATETIME NULL,
    end_date_range DATETIME NULL,
    changed_user_names NVARCHAR(MAX) NULL,
	created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),
    created_by BIGINT,
    updated_by BIGINT,
    active BIT DEFAULT 1 NOT NULL
);
GO

CREATE TABLE source_references (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    source_reference_name NVARCHAR(255),
    source_reference_key NVARCHAR(255),
    additional_info NVARCHAR(MAX), -- Define as NVARCHAR(MAX) for JSON storage
	created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),
    created_by BIGINT,
    updated_by BIGINT,
    active BIT DEFAULT 1 NOT NULL
);
Go;

