INSERT INTO usuarios (username, password) VALUES
    ('admin',   '$2b$10$B4HUB1gXFYYiSZu7C4QG1.cFuf2dH6/7pkLSzqk.9EzD5LCDgDq8W'),
    ('usuario', '$2b$10$ZoNYz3Y/.pXKxVyxyyli7.9gHuh9fDAn1yGVDzlrODFbSowJRVOIK')
ON CONFLICT (username) DO NOTHING;

INSERT INTO usuario_roles (usuario_id, rol)
SELECT id, 'ROLE_ADMIN' FROM usuarios WHERE username = 'admin'
ON CONFLICT DO NOTHING;

INSERT INTO usuario_roles (usuario_id, rol)
SELECT id, 'ROLE_USER' FROM usuarios WHERE username = 'admin'
ON CONFLICT DO NOTHING;

INSERT INTO usuario_roles (usuario_id, rol)
SELECT id, 'ROLE_USER' FROM usuarios WHERE username = 'usuario'
ON CONFLICT DO NOTHING;
