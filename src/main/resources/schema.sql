DROP DATABASE IF EXISTS `audit_manager_db`;

-- Create the database
CREATE DATABASE audit_manager_db;


USE audit_manager_db;

-- Create the table
CREATE TABLE user_mst (
    id bigint NOT NULL AUTO_INCREMENT,
    user_name varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    active BIT DEFAULT 1, -- BIT type is used for boolean values in MSSQL (1 = true, 0 = false)
    profile_image_name varchar(255),
    created_at datetime NOT NULL,
    updated_at datetime NOT NULL,
	PRIMARY KEY (`id`)
);

create Table audit_object_change_tracker(
    id bigint NOT NULL AUTO_INCREMENT,
	ref_object_id BIGINT,
	event_type varchar(255) not null,
	event_occurence DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    active BOOLEAN DEFAULT TRUE NOT NULL,
	PRIMARY KEY (`id`)
);

create Table audit_attribute_change_tracker(
	id bigint NOT NULL AUTO_INCREMENT,
	attribute_name varchar(255) not null,
	old_value varchar(255) not null,
	new_value varchar(255) not null,
	changed_by varchar(255) not null,
	audit_object_change_tracker_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    active BOOLEAN DEFAULT TRUE NOT NULL,
	CONSTRAINT fk_audit_object_change_tracker
    FOREIGN KEY (audit_object_change_tracker_id)
    REFERENCES audit_object_change_tracker(id),
	PRIMARY KEY (`id`)

);

-- Insert the default user
INSERT INTO user_mst (user_name, password, created_at, updated_at, active, profile_image_name)
VALUES ('admin', '$2a$10$bgR4EcS6I.nIT48XQSj.uO3epcGgWdfl.SbVIIK62FFV3./4KEcu6', 
        '2024-04-18 13:46:12', 
        '2024-04-18 13:46:12', 
        1, NULL);




