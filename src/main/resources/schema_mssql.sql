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
    full_name VARCHAR(255),
    user_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
	user_role VARCHAR(100) NULL,
	user_email varchar(255) NULL,
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
CREATE UNIQUE INDEX unique_user_name
ON user_mst (user_name);

-- Insert the default user
INSERT INTO user_mst (full_name,user_name, password, created_at, updated_at, active, profile_image_name,user_role,user_email)
VALUES 
('Admin','admin', '$2a$10$bgR4EcS6I.nIT48XQSj.uO3epcGgWdfl.SbVIIK62FFV3./4KEcu6',GETDATE(),GETDATE(),1, NULL,'admin','admin@gmail.com'), 
('Abhishek Mali','abhishek', '$2a$10$bgR4EcS6I.nIT48XQSj.uO3epcGgWdfl.SbVIIK62FFV3./4KEcu6', GETDATE(), GETDATE(), 1, NULL, 'auditor', 'abhishek.mali@acnovate.com'),
('Azharudeen Nazeerdeen','azhar', '$2a$10$bgR4EcS6I.nIT48XQSj.uO3epcGgWdfl.SbVIIK62FFV3./4KEcu6', GETDATE(), GETDATE(), 1, NULL, 'admin', 'azharudeennazeerdeen@acnovate.com'),
('Himanshu Purohit','himanshu', '$2a$10$bgR4EcS6I.nIT48XQSj.uO3epcGgWdfl.SbVIIK62FFV3./4KEcu6', GETDATE(), GETDATE(), 1, NULL, 'auditor', 'himanshu.purohit@acnovate.com'),
('Hitesh Soni','hitesh', '$2a$10$bgR4EcS6I.nIT48XQSj.uO3epcGgWdfl.SbVIIK62FFV3./4KEcu6', GETDATE(), GETDATE(), 1, NULL, 'auditor', 'hitesh.soni@acnovate.com'),
('Khushboo Mishra','khushboo', '$2a$10$bgR4EcS6I.nIT48XQSj.uO3epcGgWdfl.SbVIIK62FFV3./4KEcu6', GETDATE(), GETDATE(), 1, NULL, 'auditor', 'khushboo.mishra@acnovate.com'),
('Prabhat Ahirwar','prabhat', '$2a$10$bgR4EcS6I.nIT48XQSj.uO3epcGgWdfl.SbVIIK62FFV3./4KEcu6', GETDATE(), GETDATE(), 1, NULL, 'auditor', 'prabhat.ahirwar@acnovate.com'),
('Praveen Hosamani','praveen', '$2a$10$bgR4EcS6I.nIT48XQSj.uO3epcGgWdfl.SbVIIK62FFV3./4KEcu6', GETDATE(), GETDATE(), 1, NULL, 'auditor', 'praveen.hosamani@acnovate.com');



;
GO

CREATE TABLE audit_report (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ref_object_ids VARCHAR(MAX) NULL,
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

CREATE TABLE source_reference_object (
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

Go

CREATE TABLE scheduling_audit_report (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    report_ids NVARCHAR(MAX),
	frequency NVARCHAR(255) NULL,
	scheduling_hour INT ,
    scheduling_minute INT ,
	time_marker  CHAR(2) CHECK (time_marker IN ('AM', 'PM')),
	recipients NVARCHAR(MAX) NULL    
);

GO

ALTER TABLE scheduling_audit_report
ADD 
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),
    created_by BIGINT NOT NULL DEFAULT 1,
    updated_by BIGINT NOT NULL DEFAULT 1,
    active BIT NOT NULL DEFAULT 1;
	

ALTER TABLE source_reference_object
ADD CONSTRAINT unique_name_key UNIQUE (source_reference_name,source_reference_key);


ALTER TABLE audit_object_change_tracker add CONSTRAINT fk_source_reference_object
    FOREIGN KEY (ref_object_id)
    REFERENCES source_reference_object(id);