CREATE TABLE products (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(2000),
    price NUMERIC(12, 2) NOT NULL,
    stock INTEGER NOT NULL,
    image_url VARCHAR(1000) NOT NULL,
    category VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT chk_product_price
        CHECK (price >= 0),

    CONSTRAINT chk_product_stock
        CHECK (stock >= 0)
);

CREATE INDEX idx_products_name
    ON products (LOWER(name));

CREATE INDEX idx_products_category
    ON products (LOWER(category));

CREATE INDEX idx_products_active
    ON products (active);