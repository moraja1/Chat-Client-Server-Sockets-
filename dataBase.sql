-- MySQL Workbench Forward Engineering

SET
@OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET
@OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET
@OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema chat
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema chat
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `chat` DEFAULT CHARACTER SET utf8;
USE
`chat` ;

-- -----------------------------------------------------
-- Table `chat`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `chat`.`user`
(
    `id_user` BIGINT
(
    20
) NOT NULL auto_increment,
    `username` VARCHAR
(
    32
) NOT NULL,
    `password` VARCHAR
(
    32
) NOT NULL,
    PRIMARY KEY
(
    `id_user`
),
    UNIQUE INDEX `username_UNIQUE`
(
    `username` ASC
) VISIBLE,
    UNIQUE INDEX `id_user_UNIQUE`
(
    `id_user` ASC
) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `chat`.`message`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `chat`.`message`
(
    `id_message` BIGINT
(
    20
) NOT NULL AUTO_INCREMENT,
    `remitent` BIGINT
(
    20
) NOT NULL,
    `destinatary` BIGINT
(
    20
) NOT NULL,
    `message` VARCHAR
(
    512
) DEFAULT NULL,
    `date_time` TIMESTAMP DEFAULT NULL,
    `delivered` BOOLEAN DEFAULT FALSE,
    PRIMARY KEY
(
    `id_message`
),
    INDEX `fk_remitent_idx`
(
    `remitent` ASC
) VISIBLE,
    INDEX `fk_destinatary_idx`
(
    `destinatary` ASC
) VISIBLE,
    CONSTRAINT `fk_remitent`
    FOREIGN KEY
(
    `remitent`
)
    REFERENCES `chat`.`user`
(
    `id_user`
)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_destinatary`
    FOREIGN KEY
(
    `destinatary`
)
    REFERENCES `chat`.`user`
(
    `id_user`
)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `chat`.`contact_list`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `chat`.`contact_list`
(
    `owner_user` BIGINT
(
    20
) NOT NULL,
    `member_user` BIGINT
(
    20
) NOT NULL,
    PRIMARY KEY
(
    `owner_user`,
    `member_user`
),
    INDEX `fk_user_has_user_user2_idx`
(
    `member_user` ASC
) VISIBLE,
    INDEX `fk_user_has_user_user1_idx`
(
    `owner_user` ASC
) VISIBLE,
    CONSTRAINT `fk_user_has_user_user1`
    FOREIGN KEY
(
    `owner_user`
)
    REFERENCES `chat`.`user`
(
    `id_user`
)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
    CONSTRAINT `fk_user_has_user_user2`
    FOREIGN KEY
(
    `member_user`
)
    REFERENCES `chat`.`user`
(
    `id_user`
)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `chat`.`group`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `chat`.`group`
(
    `id_group` BIGINT
(
    20
) NOT NULL AUTO_INCREMENT,
    `id_user` BIGINT
(
    20
) NOT NULL,
    PRIMARY KEY
(
    `id_group`
),
    INDEX `fk_id_user_idx`
(
    `id_user` ASC
) VISIBLE,
    CONSTRAINT `fk_id_user`
    FOREIGN KEY
(
    `id_user`
)
    REFERENCES `chat`.`user`
(
    `id_user`
)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
    ENGINE = InnoDB;


SET
SQL_MODE=@OLD_SQL_MODE;
SET
FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET
UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

insert into user
(username, password)
values
("jaison", "admin");