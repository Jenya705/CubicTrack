CREATE TABLE IF NOT EXISTS `worlds_map` (
	`id` SMALLINT UNSIGNED NOT NULL,
	`name` TEXT NOT NULL,
	PRIMARY KEY(`id`),
	UNIQUE KEY(`name`)
);

CREATE TABLE IF NOT EXISTS `actions_map` (
	`id` SMALLINT UNSIGNED NOT NULL,
	`name` TEXT NOT NULL,
	PRIMARY KEY(`id`),
	UNIQUE KEY(`name`)
);

CREATE TABLE IF NOT EXISTS `materials_map` (
	`id` SMALLINT UNSIGNED NOT NULL,
	`name` TEXT NOT NULL,
	PRIMARY KEY(`id`),
	UNIQUE KEY(`name`)
);

CREATE TABLE IF NOT EXISTS `entities_map` (
	`id` INT NOT NULL,
	`name` TEXT NOT NULL,
	PRIMARY KEY(`id`),
	UNIQUE KEY(`name`)
);

CREATE TABLE IF NOT EXISTS `materials_data` (
	`id` BIGINT NOT NULL,
	`hash` BINARY(16) NOT NULL DEFAULT UNHEX(MD5(`data`)), -- for future optimization
	`data` TEXT NOT NULL,
	PRIMARY KEY(`id`),
	KEY(`hash`)
);

CREATE TABLE IF NOT EXISTS `actions` (
	`id` BIGINT NOT NULL,
	`epoch` BIGINT NOT NULL,
	`action_id` SMALLINT UNSIGNED NOT NULL,
	`world_id` SMALLINT UNSIGNED NOT NULL,
	`restored` BOOLEAN NOT NULL DEFAULT FALSE,
	`invoker_id` INT NOT NULL,
	`target_id` INT NOT NULL,
	`location_x` INT NOT NULL,
	`location_y` INT NOT NULL,
	`location_z` INT NOT NULL,
	`old_material_id` SMALLINT UNSIGNED NOT NULL,
	`old_material_data_id` BIGINT NOT NULL,
	`new_material_id` SMALLINT UNSIGNED NOT NULL,
	`new_material_data_id` BIGINT NOT NULL,
	PRIMARY KEY(`id`),
	FOREIGN KEY(`action_id`) REFERENCES `actions_map`(`id`) ON DELETE CASCADE,
	FOREIGN KEY(`world_id`) REFERENCES `worlds_map`(`id`) ON DELETE CASCADE,
	FOREIGN KEY(`invoker_id`) REFERENCES `entities_map`(`id`) ON DELETE CASCADE,
	FOREIGN KEY(`target_id`) REFERENCES `entities_map`(`id`) ON DELETE CASCADE,
	FOREIGN KEY(`old_material_id`) REFERENCES `materials_map`(`id`) ON DELETE CASCADE,
	FOREIGN KEY(`new_material_id`) REFERENCES `materials_map`(`id`) ON DELETE CASCADE,
	FOREIGN KEY(`old_material_data_id`) REFERENCES `materials_data`(`id`) ON DELETE CASCADE,
	FOREIGN KEY(`new_material_data_id`) REFERENCES `materials_data`(`id`) ON DELETE CASCADE,
	KEY `epoch` (`epoch`),
	KEY `location` (`world_id`, `location_x`, `location_y`, `location_z`),
	KEY `restored` (`restored`)
);
