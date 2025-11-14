DROP DATABASE IF EXISTS farmalife;
DROP USER IF EXISTS usuario_prueba_farmalife;
DROP USER IF EXISTS usuario_reportes_farmalife;
 
-- Crear esquema
CREATE DATABASE farmalife
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
 
-- Usuarios 
CREATE USER 'usuario_prueba_farmalife'@'%' IDENTIFIED BY 'Usuar1o_Clave.';
CREATE USER 'usuario_reportes_farmalife'@'%' IDENTIFIED BY 'Usuar1o_Reportes.';
 
-- Permisos
GRANT SELECT, INSERT, UPDATE, DELETE ON farmalife.* TO 'usuario_prueba_farmalife'@'%';
GRANT SELECT ON farmalife.* TO 'usuario_reportes_farmalife'@'%';
FLUSH PRIVILEGES;
 
USE farmalife;
 
-- ====== Tablas ======
 
-- 1) Categorías
CREATE TABLE categoria (
  id_categoria INT NOT NULL AUTO_INCREMENT,
  descripcion  VARCHAR(80) NOT NULL,
  ruta_imagen  VARCHAR(1024),
  activo       BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_categoria),
  UNIQUE KEY uq_categoria_descripcion (descripcion),
  INDEX ndx_descripcion (descripcion)
) ENGINE=InnoDB;
 
-- 2) Productos
CREATE TABLE producto (
  id_producto      INT NOT NULL AUTO_INCREMENT,
  id_categoria     INT NOT NULL,
 
  descripcion      VARCHAR(100) NOT NULL,
  principio_activo VARCHAR(120),
  laboratorio      VARCHAR(80),
  presentacion     VARCHAR(60),
  lote             VARCHAR(40),
  fecha_vencimiento DATE,
  requiere_receta  BOOLEAN NOT NULL DEFAULT FALSE,
 
  codigo_barras    VARCHAR(32),
  detalle          TEXT,
 
  precio           DECIMAL(12,2) CHECK (precio >= 0),
  existencias      INT UNSIGNED CHECK (existencias >= 0),
 
  ruta_imagen      VARCHAR(1024),
  activo           BOOLEAN NOT NULL DEFAULT TRUE,
 
  fecha_creacion      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 
  PRIMARY KEY (id_producto),
  UNIQUE KEY uq_producto_descripcion (descripcion),
  INDEX ndx_producto_descripcion (descripcion),
  INDEX ndx_producto_codbar (codigo_barras),
  CONSTRAINT fk_producto_categoria
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB;
 
-- 3) Usuarios
CREATE TABLE usuario (
  id_usuario   INT NOT NULL AUTO_INCREMENT,
  username     VARCHAR(30)  NOT NULL UNIQUE,
  password     VARCHAR(512) NOT NULL,
  nombre       VARCHAR(30)  NOT NULL,
  apellidos    VARCHAR(40)  NOT NULL,
  correo       VARCHAR(75)  NULL UNIQUE,
  telefono     VARCHAR(25)  NULL,
  ruta_imagen  VARCHAR(1024),
  activo       BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario),
  CHECK (correo IS NULL OR correo REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$'),
  INDEX ndx_username (username)
) ENGINE=InnoDB;
 
-- 4) Roles
CREATE TABLE rol (
  id_rol  INT NOT NULL AUTO_INCREMENT,
  rol     VARCHAR(20) UNIQUE,
  fecha_creacion      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_rol)
) ENGINE=InnoDB;
 
-- 5) Relación usuario-rol
CREATE TABLE usuario_rol (
  id_usuario INT NOT NULL,
  id_rol     INT NOT NULL,
  fecha_creacion      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario, id_rol),
  CONSTRAINT fk_usuarioRol_usuario
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
    ON UPDATE RESTRICT ON DELETE CASCADE,
  CONSTRAINT fk_usuarioRol_rol
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB;
 
-- 6) Facturas
CREATE TABLE factura (
  id_factura  INT NOT NULL AUTO_INCREMENT,
  id_usuario  INT NOT NULL,
  fecha       DATE NOT NULL,
  total       DECIMAL(12,2) CHECK (total >= 0),
  estado      ENUM('Activa', 'Pagada', 'Anulada') NOT NULL,
  metodo_pago ENUM('Efectivo','Tarjeta','Transferencia','Sinpe','Otro') DEFAULT 'Efectivo',
  observacion VARCHAR(200),
 
  fecha_creacion      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 
  PRIMARY KEY (id_factura),
  INDEX ndx_factura_usuario (id_usuario),
  CONSTRAINT fk_factura_usuario
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB;
 
-- 7) Venta (detalle)
CREATE TABLE venta (
  id_venta     INT NOT NULL AUTO_INCREMENT,
  id_factura   INT NOT NULL,
  id_producto  INT NOT NULL,
 
  precio_historico DECIMAL(12,2) CHECK (precio_historico >= 0),
  cantidad         INT UNSIGNED CHECK (cantidad > 0),
  descuento        DECIMAL(12,2) DEFAULT 0 CHECK (descuento >= 0),
  impuesto         DECIMAL(12,2) DEFAULT 0 CHECK (impuesto >= 0),
 
  fecha_creacion      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 
  PRIMARY KEY (id_venta),
  INDEX ndx_vta_factura (id_factura),
  INDEX ndx_vta_producto (id_producto),
  UNIQUE KEY uq_factura_producto (id_factura, id_producto),
  CONSTRAINT fk_venta_factura
    FOREIGN KEY (id_factura)  REFERENCES factura(id_factura)
    ON UPDATE RESTRICT ON DELETE CASCADE,
  CONSTRAINT fk_venta_producto
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB;
 
-- 8) Constantes de la aplicación
CREATE TABLE constante (
  id_constante INT NOT NULL AUTO_INCREMENT,
  atributo     VARCHAR(40) NOT NULL,
  valor        VARCHAR(200) NOT NULL,
  fecha_creacion      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_constante),
  UNIQUE KEY uq_constante_atributo (atributo)
) ENGINE=InnoDB;
 
-- Índices útiles extra
CREATE INDEX ndx_prod_vencimiento ON producto (fecha_vencimiento);
CREATE INDEX ndx_prod_receta      ON producto (requiere_receta)