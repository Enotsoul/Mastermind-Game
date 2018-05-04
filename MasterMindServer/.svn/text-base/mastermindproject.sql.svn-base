-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.1.49-1ubuntu8.1


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema mastermind
--

CREATE DATABASE IF NOT EXISTS mastermind;
USE mastermind;

--
-- Definition of table `mastermind`.`GameMoves`
--

DROP TABLE IF EXISTS `mastermind`.`GameMoves`;
CREATE TABLE  `mastermind`.`GameMoves` (
  `gameID` int(11) NOT NULL DEFAULT '0',
  `playerID` int(11) NOT NULL DEFAULT '0',
  `playerMove` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`gameID`,`playerID`),
  KEY `playerID` (`playerID`),
  CONSTRAINT `gameID` FOREIGN KEY (`gameID`) REFERENCES `PlayerGames` (`gameID`),
  CONSTRAINT `playerID` FOREIGN KEY (`playerID`) REFERENCES `Players` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `mastermind`.`GameMoves`
--

/*!40000 ALTER TABLE `GameMoves` DISABLE KEYS */;
LOCK TABLES `GameMoves` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `GameMoves` ENABLE KEYS */;


--
-- Definition of table `mastermind`.`PlayerGames`
--

DROP TABLE IF EXISTS `mastermind`.`PlayerGames`;
CREATE TABLE  `mastermind`.`PlayerGames` (
  `gameID` int(11) NOT NULL AUTO_INCREMENT,
  `player1ID` int(11) DEFAULT NULL,
  `player2ID` int(11) DEFAULT NULL,
  `gameStartTime` bigint(20) DEFAULT NULL,
  `gameEndTime` bigint(20) DEFAULT NULL,
  `winnerID` int(11) DEFAULT NULL,
  `gameType` int(11) DEFAULT NULL,
  `player1Solution` varchar(250) DEFAULT NULL,
  `player2Solution` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`gameID`),
  KEY `player1ID` (`player1ID`),
  KEY `player2ID` (`player2ID`),
  KEY `winnerID` (`winnerID`),
  CONSTRAINT `player1ID` FOREIGN KEY (`player1ID`) REFERENCES `Players` (`userID`),
  CONSTRAINT `player2ID` FOREIGN KEY (`player2ID`) REFERENCES `Players` (`userID`),
  CONSTRAINT `winnerID` FOREIGN KEY (`winnerID`) REFERENCES `Players` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

--
-- Dumping data for table `mastermind`.`PlayerGames`
--

/*!40000 ALTER TABLE `PlayerGames` DISABLE KEYS */;
LOCK TABLES `PlayerGames` WRITE;
INSERT INTO `mastermind`.`PlayerGames` VALUES  (2,1,1,1302785180,NULL,NULL,1,NULL,NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `PlayerGames` ENABLE KEYS */;


--
-- Definition of table `mastermind`.`Players`
--

DROP TABLE IF EXISTS `mastermind`.`Players`;
CREATE TABLE  `mastermind`.`Players` (
  `userID` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(250) DEFAULT NULL,
  `password` varchar(250) DEFAULT NULL,
  `email` varchar(250) DEFAULT NULL,
  `banned` int(11) NOT NULL DEFAULT '0',
  `level` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`userID`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `mastermind`.`Players`
--

/*!40000 ALTER TABLE `Players` DISABLE KEYS */;
LOCK TABLES `Players` WRITE;
INSERT INTO `mastermind`.`Players` VALUES  (1,'Test','test123','test@test.net',1,1),
 (3,'Test3','test123','test3@testerminators.net',0,1),
 (6,'LostOne','andrei','the.lostone@yahoo.com',0,1),
 (7,'andrei','andrei','andrei@andrei.net',0,1),
 (8,'GeorgeBush','whitehouse','president@whitebouse.us',0,1),
 (9,'Bram','brampie','bram@tough.net',0,1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `Players` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
