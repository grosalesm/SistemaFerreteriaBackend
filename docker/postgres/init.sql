-- Se ejecuta solo la primera vez que se crea el volumen de PostgreSQL.
-- Una base de datos por microservicio (database-per-service).
CREATE DATABASE db_auth;
CREATE DATABASE db_clientes;
CREATE DATABASE db_productos;
CREATE DATABASE db_cotizaciones;
CREATE DATABASE db_ventas;
CREATE DATABASE db_notificaciones;