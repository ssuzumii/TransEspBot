SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;


CREATE TABLE IF NOT EXISTS `advertencia` (
  `id_advertencia` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuarie` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `motivo` varchar(300) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_responsable` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `unix_alta` bigint(20) NOT NULL,
  `unix_baja` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_advertencia`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `temporizador_activo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_solicitante` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tiempo_unix` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `variable` (
  `clave` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `valor` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`clave`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `variable` (`clave`, `valor`) VALUES
('aviso_boost', '1'),
('tiempo_novates', '0');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
