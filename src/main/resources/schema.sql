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

ALTER TABLE user_mst
ADD UNIQUE INDEX unique_user_name (user_name);

ALTER TABLE user_mst
ADD user_role VARCHAR(100) NULL;

ALTER TABLE user_mst
ADD user_email varchar(255) NULL;

-- Insert the default user
INSERT INTO user_mst (user_name, password, created_at, updated_at, active, profile_image_name)
VALUES ('admin', '$2a$10$bgR4EcS6I.nIT48XQSj.uO3epcGgWdfl.SbVIIK62FFV3./4KEcu6', 
        '2024-04-18 13:46:12', 
        '2024-04-18 13:46:12', 
        1, NULL);
		
INSERT INTO `audit_manager_db`.`audit_object_change_tracker` (`ref_object_id`, `event_type`, `event_occurence`, `created_at`, `updated_at`, `created_by`, `updated_by`, `active`) VALUES ('0001', 'colorway-create', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1');				
INSERT INTO `audit_manager_db`.`audit_object_change_tracker` (`ref_object_id`, `event_type`, `event_occurence`, `created_at`, `updated_at`, `created_by`, `updated_by`, `active`) VALUES ('0001', 'colorway-update', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1');

INSERT INTO `audit_manager_db`.`audit_object_change_tracker` (`ref_object_id`, `event_type`, `event_occurence`, `created_at`, `updated_at`, `created_by`, `updated_by`, `active`) VALUES ('0001', 'colorway-update', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1');



INSERT INTO `audit_manager_db`.`audit_object_change_tracker` (`ref_object_id`, `event_type`, `event_occurence`, `created_at`, `updated_at`, `created_by`, `updated_by`, `active`) VALUES ('0002', 'product-create', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'); 

INSERT INTO `audit_manager_db`.`audit_object_change_tracker` (`ref_object_id`, `event_type`, `event_occurence`, `created_at`, `updated_at`, `created_by`, `updated_by`, `active`) VALUES ('0002', 'product-update', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1');

INSERT INTO `audit_manager_db`.`audit_object_change_tracker` (`ref_object_id`, `event_type`, `event_occurence`, `created_at`, `updated_at`, `created_by`, `updated_by`, `active`) VALUES ('0002', 'product-update', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1');

INSERT INTO `audit_manager_db`.`audit_attribute_change_tracker` (`attribute_name`, `old_value`, `new_value`, `changed_by`, `audit_object_change_tracker_id`, `created_at`, `updated_at`, `created_by`, `updated_by`, `active`) 
VALUES 
('color-name', 'blue', 'blue', 'user123', '1', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('pattern', 'striped', 'striped', 'user123', '1', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('material', 'cotton', 'cotton', 'user123', '1', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('release-date', '2024-09-01', '2024-09-01', 'user123', '1', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('status', 'pending', 'pending', 'user123', '1', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1');

INSERT INTO `audit_manager_db`.`audit_attribute_change_tracker` (`attribute_name`, `old_value`, `new_value`, `changed_by`, `audit_object_change_tracker_id`, `created_at`, `updated_at`, `created_by`, `updated_by`, `active`) 
VALUES 
('color-name', 'blue', 'green', 'user123', '2', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('pattern', 'striped', 'solid', 'user123', '2', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('material', 'cotton', 'polyester', 'user123', '2', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('release-date', '2024-09-01', '2024-09-09', 'user123', '2', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('status', 'pending', 'approved', 'user123', '2', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1');

INSERT INTO `audit_manager_db`.`audit_attribute_change_tracker` (`attribute_name`, `old_value`, `new_value`, `changed_by`, `audit_object_change_tracker_id`, `created_at`, `updated_at`, `created_by`, `updated_by`, `active`) 
VALUES 
('color-name', 'green', 'green', 'user123', '3', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('pattern', 'solid', 'solid', 'user123', '3', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('material', 'polyester', 'lycra', 'user123', '3', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('release-date', '2024-09-09', '2024-09-09', 'user123', '3', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('status', 'approved', 'canceled', 'user123', '3', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1');


INSERT INTO `audit_manager_db`.`audit_attribute_change_tracker` (`attribute_name`, `old_value`, `new_value`, `changed_by`, `audit_object_change_tracker_id`, `created_at`, `updated_at`, `created_by`, `updated_by`, `active`) 
VALUES 
('product-name', 'shirt', 'shirt', 'user456', '4', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('price', '50.00', '50.00', 'user456', '4', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('size', 'M', 'M', 'user456', '4', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('availability', 'in-stock', 'in-stock', 'user456', '4', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('discount', '5%', '5%', 'user456', '4', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1');



INSERT INTO `audit_manager_db`.`audit_attribute_change_tracker` (`attribute_name`, `old_value`, `new_value`, `changed_by`, `audit_object_change_tracker_id`, `created_at`, `updated_at`, `created_by`, `updated_by`, `active`) 
VALUES 
('product-name', 'shirt', 'jacket', 'user456', '5', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('price', '50.00', '55.00', 'user456', '5', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('size', 'M', 'L', 'user456', '5', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('availability', 'in-stock', 'out-of-stock', 'user456', '5', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('discount', '5%', '10%', 'user456', '5', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1');

INSERT INTO `audit_manager_db`.`audit_attribute_change_tracker` (`attribute_name`, `old_value`, `new_value`, `changed_by`, `audit_object_change_tracker_id`, `created_at`, `updated_at`, `created_by`, `updated_by`, `active`) 
VALUES 
('product-name', 'jacket', 'Polo-jacket', 'newUser', '6', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('price', '50.00', '55.00', 'user456', '6', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('size', 'M', 'L', 'user456', '6', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('availability', 'in-stock', 'out-of-stock', 'user456', '6', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1'),
('discount', '5%', '10%', 'user456', '6', '2024-09-09 22:48:43', '2024-09-09 22:48:43', '1', '1', '1');





