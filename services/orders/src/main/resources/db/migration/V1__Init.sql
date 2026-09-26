CREATE TABLE orders (
  id VARCHAR(255) PRIMARY KEY,
  user_id VARCHAR(255) NOT NULL,
  subtotal DECIMAL(10, 2) NOT NULL,
  shipping_fee DECIMAL(10, 2) NOT NULL,
  tax DECIMAL(10, 2) NOT NULL,
  total DECIMAL(10, 2) NOT NULL,
  status VARCHAR(50) NOT NULL,
  date TIMESTAMP NOT NULL,
  address VARCHAR(255) NOT NULL,
  city VARCHAR(100) NOT NULL,
  phone VARCHAR(20) NOT NULL
);

CREATE TABLE order_payment_intent_id (
  order_entity_jpa_id VARCHAR(255) NOT NULL,
  payment_intent_id VARCHAR(255),
  CONSTRAINT fk_order_payment_intent
    FOREIGN KEY (order_entity_jpa_id)
    REFERENCES orders(id)
);

CREATE TABLE order_items (
  order_entity_jpa_id VARCHAR(255) NOT NULL,
  product_id VARCHAR(255) NOT NULL,
  quantity INT NOT NULL,
  price DECIMAL(10, 2) NOT NULL,
  CONSTRAINT fk_order_items_order
    FOREIGN KEY (order_entity_jpa_id)
    REFERENCES orders(id)
);

