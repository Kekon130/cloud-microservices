USE Cloud_DB;

-- =========================
-- USERS
-- =========================
INSERT INTO Users (name, surname) VALUES
('Sergio', 'Plaza'),
('Laura', 'Gomez'),
('Carlos', 'Martinez'),
('Ana', 'Lopez'),
('David', 'Sanchez'),
('Maria', 'Fernandez'),
('Javier', 'Ruiz'),
('Lucia', 'Hernandez'),
('Pablo', 'Diaz'),
('Elena', 'Torres');

-- =========================
-- PRODUCTS
-- =========================
INSERT INTO Products (name, price) VALUES
('Laptop Dell XPS 13', 1299.99),
('iPhone 15', 999.99),
('Samsung Galaxy S24', 899.99),
('Monitor LG 27"', 249.99),
('Teclado Mecánico Logitech', 119.50),
('Ratón Inalámbrico Logitech', 49.99),
('Tablet iPad Air', 699.00),
('Auriculares Sony WH-1000XM5', 379.99),
('Disco SSD 1TB Samsung', 109.90),
('Webcam Logitech HD', 89.99);

-- =========================
-- ORDERS
-- =========================
INSERT INTO Orders (userId, productId, units, date) VALUES
(1, 1, 1, '2025-01-10 10:15:00'),
(2, 2, 2, '2025-01-12 12:30:00'),
(3, 3, 1, '2025-01-15 09:45:00'),
(4, 4, 3, '2025-01-18 14:20:00'),
(5, 5, 1, '2025-01-20 16:00:00'),
(6, 6, 4, '2025-01-22 11:10:00'),
(7, 7, 1, '2025-01-25 18:35:00'),
(8, 8, 2, '2025-01-28 13:50:00'),
(9, 9, 1, '2025-02-01 08:25:00'),
(10, 10, 5, '2025-02-05 17:40:00');
