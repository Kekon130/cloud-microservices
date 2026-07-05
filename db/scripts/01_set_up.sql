-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema Cloud_DB
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `Cloud_DB` ;

-- -----------------------------------------------------
-- Schema Cloud_DB
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `Cloud_DB` DEFAULT CHARACTER SET utf8 ;
USE `Cloud_DB` ;

-- -----------------------------------------------------
-- Table `Cloud_DB`.`Users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Cloud_DB`.`Users` ;

CREATE TABLE IF NOT EXISTS `Cloud_DB`.`Users` (
  `idUsers` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `surname` VARCHAR(60) NOT NULL,
  PRIMARY KEY (`idUsers`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Users manager declaration
-- -----------------------------------------------------
DROP USER IF EXISTS 'users_manager'@'%';

CREATE USER 'users_manager'@'%' IDENTIFIED BY 'users_password';

GRANT ALL PRIVILEGES ON Cloud_DB.Users TO 'users_manager'@'%';

-- -----------------------------------------------------
-- Table `Cloud_DB`.`Products`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Cloud_DB`.`Products` ;

CREATE TABLE IF NOT EXISTS `Cloud_DB`.`Products` (
  `idProducts` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `price` FLOAT NOT NULL,
  PRIMARY KEY (`idProducts`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Products manager declaration
-- -----------------------------------------------------
DROP USER IF EXISTS 'products_manager'@'%';

CREATE USER 'products_manager'@'%' IDENTIFIED BY 'products_password';

GRANT ALL PRIVILEGES ON Cloud_DB.Products TO 'products_manager'@'%';

-- -----------------------------------------------------
-- Table `Cloud_DB`.`Orders`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Cloud_DB`.`Orders` ;

CREATE TABLE IF NOT EXISTS `Cloud_DB`.`Orders` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `userId` INT NOT NULL,
  `productId` INT NOT NULL,
  `units` INT NOT NULL,
  `date` DATETIME NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Orders manager declaration
-- -----------------------------------------------------
DROP USER IF EXISTS 'orders_manager'@'%';

CREATE USER 'orders_manager'@'%' IDENTIFIED BY 'orders_password';

GRANT ALL PRIVILEGES ON Cloud_DB.Orders TO 'orders_manager'@'%';
GRANT SELECT ON Cloud_DB.Users TO 'orders_manager'@'%';
GRANT SELECT ON Cloud_DB.Products TO 'orders_manager'@'%';

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
