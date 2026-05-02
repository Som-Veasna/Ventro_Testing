
INSERT INTO roles (role_id, role_name) VALUES
                                           (gen_random_uuid(), 'ADMIN'),
                                           (gen_random_uuid(), 'MANAGER'),
                                           (gen_random_uuid(), 'STAFF');
INSERT INTO branches (branch_id, branch_name, location)
VALUES (
           gen_random_uuid(),
           'Main Branch',
           'Phnom Penh'
       );
INSERT INTO users (
    user_id,
    first_name,
    last_name,
    email,
    password,
    is_first_login,
    is_active,
    created_at,
    updated_at,
    role_id
)
VALUES (
           gen_random_uuid(),
           'Veansa',
           'Som',
           'somveasna00@gmail.com',
           '$2a$12$gYcjQNpkizI0XgdWS2/zpeEtkCTn09Z2CM3I4P7DL1cUPmKlyfZyO',
           true,
           true,
           now(),
           now(),
           (SELECT role_id FROM roles WHERE role_name = 'ADMIN')
       );
-- password:123