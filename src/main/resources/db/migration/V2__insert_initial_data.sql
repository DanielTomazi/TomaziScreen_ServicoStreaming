-- Inserção de dados iniciais do sistema

-- Inserção de categorias padrão
INSERT INTO categories (name, description, display_order, active) VALUES
('Filmes', 'Filmes de diversos gêneros', 1, true),
('Séries', 'Séries e episódios completos', 2, true),
('Documentários', 'Documentários educativos e informativos', 3, true),
('Esportes', 'Conteúdo esportivo e competições', 4, true),
('Música', 'Shows musicais e clipes', 5, true),
('Educação', 'Conteúdo educativo e tutoriais', 6, true),
('Entretenimento', 'Programas de entretenimento variados', 7, true);

-- Inserção de planos de assinatura
INSERT INTO subscription_plans (name, description, price, duration_days, allows_premium_content, max_concurrent_streams, max_quality_resolution, active) VALUES
('Básico', 'Plano básico com acesso limitado', 19.90, 30, false, 1, '720p', true),
('Premium', 'Plano premium com acesso completo', 39.90, 30, true, 2, '1080p', true),
('Família', 'Plano familiar para múltiplos usuários', 59.90, 30, true, 4, '1080p', true),
('Anual Básico', 'Plano básico anual com desconto', 199.90, 365, false, 1, '720p', true),
('Anual Premium', 'Plano premium anual com desconto', 399.90, 365, true, 3, '1080p', true);

-- Inserção de usuário administrador padrão
-- Senha: admin123 (hash BCrypt)
INSERT INTO users (username, email, password, first_name, last_name, role, active) VALUES
('admin', 'admin@tomazistreaming.com', '$2a$10$DowJonesRy4KMiGuvAq.LeQYdBh.7/M/Bc3RkX8KpYZqOy0H2yUy.', 'Administrator', 'System', 'ADMIN', true),
('creator', 'creator@tomazistreaming.com', '$2a$10$DowJonesRy4KMiGuvAq.LeQYdBh.7/M/Bc3RkX8KpYZqOy0H2yUy.', 'Content', 'Creator', 'CREATOR', true),
('viewer', 'viewer@tomazistreaming.com', '$2a$10$DowJonesRy4KMiGuvAq.LeQYdBh.7/M/Bc3RkX8KpYZqOy0H2yUy.', 'Regular', 'Viewer', 'VIEWER', true);
