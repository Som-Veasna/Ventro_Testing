CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TYPE payment_method_enum AS ENUM ('CASH', 'CARD', 'KHQR');
CREATE TYPE transaction_status_enum AS ENUM ('COMPLETED', 'VOIDED');
CREATE TYPE discount_type_enum AS ENUM ('FIXED', 'PERCENTAGE');
CREATE TYPE order_status_enum AS ENUM ('HELD', 'COMPLETED', 'CANCELLED');
CREATE TYPE stock_status_enum AS ENUM ('DRAFT', 'PROCESSED', 'CANCELLED');
CREATE TYPE transfer_status_enum AS ENUM ('DRAFT', 'PROCESSED', 'CANCELLED');
CREATE TYPE adjustment_reason_enum AS ENUM ('DAMAGED', 'EXPIRED', 'LOST', 'CORRECTION');

CREATE TABLE roles (
                       role_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       role_name VARCHAR(15) NOT NULL UNIQUE
);

CREATE TABLE branches (
                          branch_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          branch_name VARCHAR(50) NOT NULL,
                          location TEXT,
                          description VARCHAR(250),
                          is_active BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMP DEFAULT NOW(),
                          updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE users (
                       user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       first_name VARCHAR(50),
                       last_name VARCHAR(50),
                       image TEXT,
                       email VARCHAR(50) NOT NULL UNIQUE,
                       password TEXT NOT NULL,
                       requires_password_change BOOLEAN DEFAULT TRUE,
                       phone_number VARCHAR(20),
                       is_active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT NOW(),
                       updated_at TIMESTAMP DEFAULT NOW(),

                       role_id UUID NOT NULL
                           REFERENCES roles(role_id)
                               ON DELETE RESTRICT
                               ON UPDATE CASCADE,

                       branch_id UUID
                           REFERENCES branches(branch_id)
                               ON DELETE SET NULL
                               ON UPDATE CASCADE
);

CREATE TABLE payment_configs (
                                 payment_config_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 bakong_account_id VARCHAR(50),
                                 account_phone VARCHAR(20),
                                 acquiring_bank VARCHAR(15),
                                 merchant_name VARCHAR(50),
                                 merchant_city VARCHAR(20),
                                 store_label VARCHAR(20),
                                 terminal_label VARCHAR(20),

                                 branch_id UUID NOT NULL
                                     REFERENCES branches(branch_id)
                                         ON DELETE CASCADE
                                         ON UPDATE CASCADE
);

CREATE TABLE exchange_rates (
                                rate_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                rate_value NUMERIC(10,2) NOT NULL,
                                updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE categories (
                            category_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            category_name VARCHAR(50) NOT NULL,
                            is_active BOOLEAN DEFAULT TRUE,
                            created_at TIMESTAMP DEFAULT NOW(),
                            updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE units (
                       unit_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       unit_name VARCHAR(50) NOT NULL
);

CREATE TABLE products (
                          product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          product_name VARCHAR(50) NOT NULL,
                          image TEXT,
                          description VARCHAR(250),
                          barcode VARCHAR(100),
                          sku VARCHAR(100),
                          cost_price NUMERIC(10,2),
                          price NUMERIC(10,2),
                          is_active BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMP DEFAULT NOW(),
                          updated_at TIMESTAMP DEFAULT NOW(),

                          unit_id UUID
                              REFERENCES units(unit_id)
                                  ON DELETE RESTRICT
                                  ON UPDATE CASCADE,

                          category_id UUID
                              REFERENCES categories(category_id)
                                  ON DELETE SET NULL
                                  ON UPDATE CASCADE
);

CREATE TABLE branch_stock (
                              branch_stock_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              quantity INTEGER DEFAULT 0,

                              branch_id UUID NOT NULL
                                  REFERENCES branches(branch_id)
                                      ON DELETE CASCADE
                                      ON UPDATE CASCADE,

                              product_id UUID NOT NULL
                                  REFERENCES products(product_id)
                                      ON DELETE RESTRICT
                                      ON UPDATE CASCADE
);

CREATE TABLE suppliers (
                           supplier_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           supplier_name VARCHAR(50) NOT NULL,
                           email VARCHAR(50),
                           phone_number VARCHAR(20),
                           location TEXT,
                           is_active BOOLEAN DEFAULT TRUE,
                           created_at TIMESTAMP DEFAULT NOW(),
                           updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE stock_receipts (
                                stock_receipt_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                receipt_name VARCHAR(50),
                                status stock_status_enum,
                                note TEXT,
                                created_at TIMESTAMP DEFAULT NOW(),
                                updated_at TIMESTAMP DEFAULT NOW(),

                                supplier_id UUID
                                    REFERENCES suppliers(supplier_id)
                                        ON DELETE SET NULL
                                        ON UPDATE CASCADE,

                                branch_id UUID NOT NULL
                                    REFERENCES branches(branch_id)
                                        ON DELETE CASCADE
                                        ON UPDATE CASCADE
);

CREATE TABLE stock_receipt_items (
                                     stock_receipt_item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     quantity INTEGER NOT NULL,

                                     stock_receipt_id UUID NOT NULL
                                         REFERENCES stock_receipts(stock_receipt_id)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE,

                                     product_id UUID NOT NULL
                                         REFERENCES products(product_id)
                                             ON DELETE RESTRICT
                                             ON UPDATE CASCADE
);

CREATE TABLE stock_adjustments (
                                   stock_adjustment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   adjustment_name VARCHAR(50),
                                   status stock_status_enum,
                                   adjustment_reason adjustment_reason_enum,
                                   note TEXT,
                                   created_at TIMESTAMP DEFAULT NOW(),
                                   updated_at TIMESTAMP DEFAULT NOW(),

                                   branch_id UUID NOT NULL
                                       REFERENCES branches(branch_id)
                                           ON DELETE CASCADE
                                           ON UPDATE CASCADE
);

CREATE TABLE stock_adjustment_items (
                                        stock_adjustment_item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                        quantity INTEGER NOT NULL,

                                        stock_adjustment_id UUID NOT NULL
                                            REFERENCES stock_adjustments(stock_adjustment_id)
                                                ON DELETE CASCADE
                                                ON UPDATE CASCADE,

                                        product_id UUID NOT NULL
                                            REFERENCES products(product_id)
                                                ON DELETE RESTRICT
                                                ON UPDATE CASCADE
);

CREATE TABLE stock_transfers (
                                 transfer_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 stock_transfer_name VARCHAR(50),
                                 status transfer_status_enum,
                                 created_at TIMESTAMP DEFAULT NOW(),

                                 created_by UUID
                                     REFERENCES users(user_id)
                                         ON DELETE SET NULL
                                         ON UPDATE CASCADE,

                                 received_by UUID
                                     REFERENCES users(user_id)
                                         ON DELETE SET NULL
                                         ON UPDATE CASCADE,

                                 source_branch_id UUID NOT NULL
                                     REFERENCES branches(branch_id)
                                         ON DELETE CASCADE
                                         ON UPDATE CASCADE,

                                 destination_branch_id UUID NOT NULL
                                     REFERENCES branches(branch_id)
                                         ON DELETE CASCADE
                                         ON UPDATE CASCADE
);

CREATE TABLE stock_transfer_items (
                                      transfer_item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      requested_qty INTEGER NOT NULL,
                                      received_qty INTEGER DEFAULT 0,

                                      transfer_id UUID NOT NULL
                                          REFERENCES stock_transfers(transfer_id)
                                              ON DELETE CASCADE
                                              ON UPDATE CASCADE,

                                      product_id UUID NOT NULL
                                          REFERENCES products(product_id)
                                              ON DELETE RESTRICT
                                              ON UPDATE CASCADE
);

CREATE TABLE stock_confirmations (
                                     confirmation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     status BOOLEAN DEFAULT FALSE,
                                     notes TEXT,
                                     confirmed_at TIMESTAMP,

                                     confirmed_by UUID
                                         REFERENCES users(user_id)
                                             ON DELETE SET NULL
                                             ON UPDATE CASCADE,

                                     branch_id UUID NOT NULL
                                         REFERENCES branches(branch_id)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE,

                                     transfer_id UUID NOT NULL
                                         REFERENCES stock_transfers(transfer_id)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE
);

CREATE TABLE stock_confirmation_items (
                                          confirmed_item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                          arrived_qty INTEGER NOT NULL,
                                          is_flagged BOOLEAN DEFAULT FALSE,
                                          flag_notes TEXT,

                                          confirmation_id UUID NOT NULL
                                              REFERENCES stock_confirmations(confirmation_id)
                                                  ON DELETE CASCADE
                                                  ON UPDATE CASCADE,

                                          transfer_item_id UUID NOT NULL
                                              REFERENCES stock_transfer_items(transfer_item_id)
                                                  ON DELETE CASCADE
                                                  ON UPDATE CASCADE
);

CREATE TABLE pos_sessions (
                              session_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              opening_cash NUMERIC(10,2),
                              closing_cash_actual NUMERIC(10,2),
                              closing_cash_system NUMERIC(10,2),
                              session_note TEXT,
                              rate_at_transaction NUMERIC(6,2),
                              opened_at TIMESTAMP,
                              closed_at TIMESTAMP,

                              opened_by UUID
                                  REFERENCES users(user_id)
                                      ON DELETE SET NULL
                                      ON UPDATE CASCADE,

                              closed_by UUID
                                  REFERENCES users(user_id)
                                      ON DELETE SET NULL
                                      ON UPDATE CASCADE,

                              branch_id UUID NOT NULL
                                  REFERENCES branches(branch_id)
                                      ON DELETE CASCADE
                                      ON UPDATE CASCADE
);

CREATE TABLE transactions (
                              transaction_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              receipt_no VARCHAR(100) UNIQUE,
                              subtotal NUMERIC(10,2),
                              discount NUMERIC(10,2),
                              discount_types discount_type_enum,
                              total_amount NUMERIC(10,2),
                              payment_method payment_method_enum,
                              received_amount_usd NUMERIC(10,2),
                              received_amount_khr NUMERIC(10,2),
                              change_amount_usd NUMERIC(10,2),
                              change_amount_khr NUMERIC(10,2),
                              rate_at_transaction NUMERIC(6,2),
                              transaction_date_time TIMESTAMP DEFAULT NOW(),
                              status transaction_status_enum,

                              session_id UUID
                                  REFERENCES pos_sessions(session_id)
                                      ON DELETE SET NULL
                                      ON UPDATE CASCADE,

                              sold_by UUID
                                  REFERENCES users(user_id)
                                      ON DELETE SET NULL
                                      ON UPDATE CASCADE,

                              branch_id UUID
                                  REFERENCES branches(branch_id)
                                      ON DELETE SET NULL
                                      ON UPDATE CASCADE
);

CREATE TABLE transaction_items (
                                   transaction_item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   quantity INTEGER NOT NULL,
                                   discount_percentage NUMERIC(4,2),
                                   unit_price NUMERIC(10,2),

                                   transaction_id UUID NOT NULL
                                       REFERENCES transactions(transaction_id)
                                           ON DELETE CASCADE
                                           ON UPDATE CASCADE,

                                   product_id UUID NOT NULL
                                       REFERENCES products(product_id)
                                           ON DELETE RESTRICT
                                           ON UPDATE CASCADE
);

CREATE TABLE held_orders (
                             order_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             order_no VARCHAR(50) UNIQUE,
                             hold_note TEXT,
                             total_amount NUMERIC(10,2),
                             discount NUMERIC(10,2),
                             discount_types discount_type_enum,
                             status order_status_enum,

                             session_id UUID
                                 REFERENCES pos_sessions(session_id)
                                     ON DELETE SET NULL
                                     ON UPDATE CASCADE,

                             branch_id UUID
                                 REFERENCES branches(branch_id)
                                     ON DELETE SET NULL
                                     ON UPDATE CASCADE,

                             created_by UUID
                                 REFERENCES users(user_id)
                                     ON DELETE SET NULL
                                     ON UPDATE CASCADE
);

CREATE TABLE held_order_items (
                                  held_order_item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  quantity INTEGER NOT NULL,
                                  unit_price NUMERIC(10,2),
                                  discount_percentage NUMERIC(4,2),
                                  total NUMERIC(10,2),

                                  order_id UUID NOT NULL
                                      REFERENCES held_orders(order_id)
                                          ON DELETE CASCADE
                                          ON UPDATE CASCADE,

                                  product_id UUID NOT NULL
                                      REFERENCES products(product_id)
                                          ON DELETE RESTRICT
                                          ON UPDATE CASCADE
);

INSERT INTO roles (role_name)
VALUES ('ADMIN'), ('MANAGER'), ('STAFF')
ON CONFLICT (role_name) DO NOTHING;