INSERT INTO roles (role_id, role_name)
VALUES
    (gen_random_uuid(), 'ADMIN'),
    (gen_random_uuid(), 'MANAGER'),
    (gen_random_uuid(), 'STAFF');
INSERT INTO branches (
    branch_id,
    branch_name,
    location,
    description,
    is_active
)
VALUES
    (
        gen_random_uuid(),
        'Main 1',
        'Phnom Penh',
        'Main branch of Ventro',
        TRUE
    ),
    (
        gen_random_uuid(),
        'Branch 2',
        'Takeo',
        'Takeo branch',
        TRUE
    );
INSERT INTO users (
    user_id,
    first_name,
    last_name,
    email,
    password,
    requires_password_change,
    phone_number,
    is_active,
    role_id,
    branch_id
)
VALUES
    (
        gen_random_uuid(),
        'Mr',
        'Bona',
        'veasnavk5@gmail.com',
        '$2a$12$9iGU6iEWsiiDhkBqYcwT1.5rWr1PuhfZ1CBlspceG/VvI7UPXJ3HS',
        TRUE,
        '093453745',
        TRUE,
        (SELECT role_id FROM roles WHERE role_name = 'ADMIN'),
        NULL
    );
-- password: 123