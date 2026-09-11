CREATE TABLE orders (
  id VARCHAR(255) PRIMARY KEY,
  userId VARCHAR(255) NOT NULL,
  subtotal DECIMAL(10, 2) NOT NULL,
  shippingFee DECIMAL(10, 2) NOT NULL,
  tax DECIMAL(10, 2) NOT NULL,
  total DECIMAL(10, 2) NOT NULL,
  status VARCHAR(50) NOT NULL,
  date TIMESTAMP NOT NULL,
  address VARCHAR(255) NOT NULL,
  city VARCHAR(100) NOT NULL,
  phone VARCHAR(20) NOT NULL,
  stripePaymentIntentId VARCHAR(255)
);

CREATE TABLE order_items (
  id VARCHAR(255) PRIMARY KEY,
  orderId VARCHAR(255) NOT NULL,
  productId INT NOT NULL,
  name VARCHAR(255) NOT NULL,
  price DECIMAL(10, 2) NOT NULL,
  quantity INT NOT NULL,
  CONSTRAINT fk_order_items_order
    FOREIGN KEY (orderId)
    REFERENCES orders(id)
);
