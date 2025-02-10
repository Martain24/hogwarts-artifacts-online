-- Insertar Wizards
INSERT INTO wizards (id, name) VALUES (1, 'Albus Dumbledore');
INSERT INTO wizards (id, name) VALUES (2, 'Severus Snape');
INSERT INTO wizards (id, name) VALUES (3, 'Hermione Granger');

-- Insertar Artifacts (algunos con owner_id y otros sin él)
INSERT INTO artifacts (id, name, description, image_url, owner_id) VALUES 
('artifact1', 'Varita de Saúco', 'Una de las Reliquias de la Muerte, extremadamente poderosa.', 'https://example.com/wand.jpg', 1),
('artifact2', 'Capa de Invisibilidad', 'Hace invisible al usuario cuando la usa.', 'https://example.com/cloak.jpg', 1),
('artifact3', 'Piedra de la Resurrección', 'Permite comunicarse con los muertos.', 'https://example.com/stone.jpg', 1),
('artifact4', 'Libro de Pociones del Príncipe Mestizo', 'Un libro lleno de anotaciones avanzadas de pociones.', 'https://example.com/potions.jpg', 2),
('artifact5', 'Giratiempos', 'Permite viajar en el tiempo durante cortos periodos.', 'https://example.com/timeturner.jpg', 3),
('artifact6', 'Espada de Gryffindor', 'Una espada mágica con poderes especiales.', 'https://example.com/sword.jpg', 2),
('artifact7', 'Mapa del Merodeador', 'Muestra todas las personas dentro de Hogwarts en tiempo real.', 'https://example.com/map.jpg', 3),
('artifact8', 'Pensadero', 'Permite visualizar recuerdos.', 'https://example.com/pensieve.jpg', NULL),
('artifact9', 'Snitch Dorada', 'Una pequeña esfera dorada utilizada en el Quidditch.', 'https://example.com/snitch.jpg', 1),
('artifact10', 'Horrocrux de Voldemort', 'Un fragmento del alma de Voldemort.', 'https://example.com/horcrux.jpg', NULL);
