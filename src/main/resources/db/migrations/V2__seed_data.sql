-- SrihariMart Database Migration V2: Seed Data

INSERT INTO users (id, name, email, password_hash, role, created_at) VALUES
(1, 'System Administrator', 'admin@sriharimart.com', '$2a$12$ndzkvILHDZneEL0droKDsuVYRX/dwP8yL8sQ4O0ZuUpCPWo4FVe6.', 'ADMIN', CURRENT_TIMESTAMP),
(2, 'Verified Seller Srihari', 'seller@sriharimart.com', '$2a$12$wh7Z3E7Qqz7ztNwE7o3L3u7g7PQQD8NiA3hsYXMlvKjDyixm3cXWe', 'SELLER', CURRENT_TIMESTAMP),
(3, 'Demo Buyer', 'buyer@sriharimart.com', '$2a$12$mn7oDBG6Owjza893pggOjen2p14Zy6BXJFAE91SW6w/qVFcq0QXT6', 'BUYER', CURRENT_TIMESTAMP);

INSERT INTO products (id, seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES
(1, 2, 'Sony WH-1000XM5 Wireless Noise-Cancelling Headphones', 'Industry-leading noise cancellation with 2 processors, 8 microphones, and up to 30 hours battery life.', 349.99, 25, 'Electronics', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600', CURRENT_TIMESTAMP),
(2, 2, 'Apple MacBook Air 13" M2 Chip 256GB', 'Ultra-thin, all-aluminum unibody laptop with blazing-fast Apple M2 chip, 8GB unified memory and Liquid Retina display.', 999.00, 15, 'Electronics', 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600', CURRENT_TIMESTAMP),
(3, 2, 'Logitech MX Master 3S Performance Wireless Mouse', 'Quiet clicks, 8K DPI any-surface tracking, MagSpeed electromagnetic scrolling, and ergonomic design.', 99.99, 50, 'Electronics', 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=600', CURRENT_TIMESTAMP),
(4, 2, 'Slim Fit 100% Breathable Cotton Shirt', 'Premium oxford woven casual shirt designed for comfort, breathability, and everyday wear.', 39.50, 80, 'Fashion', 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600', CURRENT_TIMESTAMP),
(5, 2, 'Waterproof Leather Business Laptop Backpack', 'Handcrafted genuine leather backpack with dedicated 15.6 inch padded compartment and USB charging port.', 79.00, 35, 'Fashion', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600', CURRENT_TIMESTAMP),
(6, 2, 'Clean Code: A Handbook of Agile Software Craftsmanship', 'A legendary programming handbook by Robert C. Martin on software craftsmanship and writing clean code.', 44.95, 40, 'Books', 'https://images.unsplash.com/photo-1532012164546-f432f2e3777a?w=600', CURRENT_TIMESTAMP),
(7, 2, 'Design Patterns: Elements of Reusable Object-Oriented Software', 'The classic GoF reference cataloging 23 patterns that form the foundation of object-oriented software engineering.', 54.20, 20, 'Books', 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600', CURRENT_TIMESTAMP),
(8, 2, 'Vacuum Insulated Stainless Steel Thermal Tumbler 500ml', 'Double-wall vacuum insulation keeps beverages piping hot for 12 hours or ice-cold for 24 hours.', 24.99, 60, 'Home', 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=600', CURRENT_TIMESTAMP);

ALTER TABLE users ALTER COLUMN id RESTART WITH 10;
ALTER TABLE products ALTER COLUMN id RESTART WITH 10;
