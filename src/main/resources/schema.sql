CREATE TABLE roles (
                       role_id   UUID PRIMARY KEY,
                       role_name VARCHAR(50) NOT NULL
);
CREATE TABLE branches (
                          branch_id      UUID PRIMARY KEY,
                          branch_name    VARCHAR(100) NOT NULL,
                          location       TEXT,
                          payment_config JSONB,
                          created_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE users (
                       user_id        UUID PRIMARY KEY,
                       first_name     VARCHAR(50),
                       last_name      VARCHAR(50),
                       image          TEXT,
                       email          VARCHAR(50) NOT NULL UNIQUE,
                       password       TEXT NOT NULL,

                       is_first_login BOOLEAN NOT NULL DEFAULT TRUE,
                       phone_number   VARCHAR(20),
                       is_active      BOOLEAN NOT NULL DEFAULT TRUE,

                       address        TEXT,
                       gender         VARCHAR(10),
                       date_of_birth  DATE,

                       role_id        UUID NOT NULL REFERENCES roles(role_id),
                       branch_id      UUID REFERENCES branches(branch_id),

                       created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

-- insert default roles
INSERT INTO roles (role_id, role_name) VALUES
                                           (gen_random_uuid(), 'ADMIN'),
                                           (gen_random_uuid(), 'MANAGER'),
                                           (gen_random_uuid(), 'STAFF');

-- insert default admin account (password: admin123)
INSERT INTO users (user_id, first_name, last_name, email, password, is_first_login, role_id)
VALUES (
           gen_random_uuid(),
           'Admin',
           'System',
           'admin@gmail.com',
           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
           TRUE,
           (SELECT role_id FROM roles WHERE role_name = 'ADMIN')
       );