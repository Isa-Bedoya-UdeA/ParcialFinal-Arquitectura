-- ============================================
-- Datos semilla para la base de datos colegioudea
-- Ejecutar con: psql -U postgres -d colegioudea -f seed.sql
-- ============================================

-- Limpiar datos existentes (respetando integridad referencial)
DELETE FROM notas;
DELETE FROM materias;
DELETE FROM estudiantes;

-- Resetear secuencias
ALTER SEQUENCE IF EXISTS notas_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS materias_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS estudiantes_id_seq RESTART WITH 1;

-- ============================================
-- Estudiantes
-- ============================================
INSERT INTO estudiantes (cedula, nombre, apellido, email, programa) VALUES
('1001234567', 'María',    'González',  'maria.gonzalez@udea.edu.co',  'Ingeniería de Sistemas'),
('1002345678', 'Carlos',   'Rodríguez', 'carlos.rodriguez@udea.edu.co','Ingeniería de Sistemas'),
('1003456789', 'Ana',      'Martínez',  'ana.martinez@udea.edu.co',    'Ingeniería Electrónica'),
('1004567890', 'Juan',     'Pérez',     'juan.perez@udea.edu.co',      'Ingeniería de Sistemas'),
('1005678901', 'Laura',    'Sánchez',   'laura.sanchez@udea.edu.co',   'Ingeniería Industrial'),
('1006789012', 'Pedro',    'López',     'pedro.lopez@udea.edu.co',     'Ingeniería de Sistemas'),
('1007890123', 'Camila',   'Torres',    'camila.torres@udea.edu.co',   'Ingeniería Electrónica'),
('1008901234', 'Diego',    'Ramírez',   'diego.ramirez@udea.edu.co',   'Ingeniería Industrial');

-- ============================================
-- Materias
-- ============================================
INSERT INTO materias (codigo, nombre, creditos) VALUES
('ING303', 'Arquitectura de Software',    4),
('ING201', 'Bases de Datos',              3),
('ING305', 'Ingeniería de Requisitos',    3),
('ING401', 'Inteligencia Artificial',     4),
('ING302', 'Redes y Comunicaciones',      3),
('ING204', 'Programación Avanzada',       3);

-- ============================================
-- Notas (escala 0.0 a 5.0)
-- ============================================
INSERT INTO notas (estudiante_id, materia_id, valor, periodo, observaciones) VALUES
-- María González (id=1): Arq. Soft 4.6, BD 4.25, Ing. Req 3.9
(1, 1, 4.6, '2026-1', 'Excelente desempeño'),
(1, 2, 4.25, '2026-1', NULL),
(1, 3, 3.9, '2026-1', NULL),

-- Carlos Rodríguez (id=2): Arq. Soft 4.4, IA 3.6
(2, 1, 4.4, '2026-1', NULL),
(2, 4, 3.6, '2026-1', NULL),

-- Ana Martínez (id=3): Arq. Soft 4.75, BD 4.55, Redes 4.15
(3, 1, 4.75, '2026-1', 'Destacada'),
(3, 2, 4.55, '2026-1', NULL),
(3, 5, 4.15, '2026-1', NULL),

-- Juan Pérez (id=4): Arq. Soft 3.25, Prog. Avanzada 2.9
(4, 1, 3.25, '2026-1', NULL),
(4, 6, 2.9, '2026-1', 'Requiere refuerzo'),

-- Laura Sánchez (id=5): BD 4.45, Ing. Req 3.8
(5, 2, 4.45, '2026-1', NULL),
(5, 3, 3.8, '2026-1', NULL),

-- Pedro López (id=6): Arq. Soft 4.7, IA 4.35
(6, 1, 4.7, '2026-1', 'Excelente'),
(6, 4, 4.35, '2026-1', NULL),

-- Camila Torres (id=7): Arq. Soft 3.55
(7, 1, 3.55, '2026-1', NULL),

-- Diego Ramírez (id=8): BD 4.1
(8, 2, 4.1, '2026-1', NULL);