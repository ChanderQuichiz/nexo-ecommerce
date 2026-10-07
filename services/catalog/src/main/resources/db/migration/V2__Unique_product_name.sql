CREATE UNIQUE INDEX uq_products_name_lower
    ON products (LOWER(name));