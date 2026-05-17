CREATE TABLE orders (
    id VARCHAR(64) PRIMARY KEY,
    saga_id VARCHAR(64),
    customer_id VARCHAR(64) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);