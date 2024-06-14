CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(255) PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS subscriptions (
    subscription_id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255),
    website_url VARCHAR(255),
    frequency VARCHAR(255),
    keyword VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
