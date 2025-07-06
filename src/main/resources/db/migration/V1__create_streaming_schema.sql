-- Criação das tabelas principais do sistema de streaming

-- Tabela de usuários
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('VIEWER', 'CREATOR', 'MODERATOR', 'ADMIN')),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Tabela de categorias
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(500),
    display_order INTEGER DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Tabela de planos de assinatura
CREATE TABLE subscription_plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    duration_days INTEGER NOT NULL,
    allows_premium_content BOOLEAN NOT NULL DEFAULT false,
    max_concurrent_streams INTEGER DEFAULT 1,
    max_quality_resolution VARCHAR(10) DEFAULT '720p',
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Tabela de conteúdos
CREATE TABLE contents (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL CHECK (type IN ('VIDEO', 'LIVE_STREAM', 'PODCAST', 'AUDIO')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('UPLOADED', 'PROCESSING', 'PROCESSED', 'PUBLISHED', 'UNPUBLISHED', 'DELETED', 'FAILED')),
    file_path VARCHAR(500),
    thumbnail_path VARCHAR(500),
    duration_seconds INTEGER,
    file_size_bytes BIGINT,
    view_count BIGINT DEFAULT 0,
    like_count BIGINT DEFAULT 0,
    is_public BOOLEAN DEFAULT true,
    is_premium BOOLEAN DEFAULT false,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT REFERENCES categories(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    published_at TIMESTAMP
);

-- Tabela de qualidades de conteúdo
CREATE TABLE content_qualities (
    id BIGSERIAL PRIMARY KEY,
    content_id BIGINT NOT NULL REFERENCES contents(id) ON DELETE CASCADE,
    resolution VARCHAR(10) NOT NULL,
    bitrate INTEGER NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size_bytes BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de histórico de visualização
CREATE TABLE viewing_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content_id BIGINT NOT NULL REFERENCES contents(id) ON DELETE CASCADE,
    watch_time_seconds INTEGER NOT NULL DEFAULT 0,
    completed BOOLEAN NOT NULL DEFAULT false,
    last_position_seconds INTEGER DEFAULT 0,
    quality_watched VARCHAR(10),
    watched_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(user_id, content_id)
);

-- Tabela de avaliações de conteúdo
CREATE TABLE content_ratings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content_id BIGINT NOT NULL REFERENCES contents(id) ON DELETE CASCADE,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(user_id, content_id)
);

-- Tabela de assinaturas de usuários
CREATE TABLE user_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    subscription_plan_id BIGINT NOT NULL REFERENCES subscription_plans(id),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    auto_renew BOOLEAN DEFAULT false,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para otimização de performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(active);

CREATE INDEX idx_contents_user_id ON contents(user_id);
CREATE INDEX idx_contents_category_id ON contents(category_id);
CREATE INDEX idx_contents_status ON contents(status);
CREATE INDEX idx_contents_type ON contents(type);
CREATE INDEX idx_contents_published_at ON contents(published_at);
CREATE INDEX idx_contents_view_count ON contents(view_count);
CREATE INDEX idx_contents_public_premium ON contents(is_public, is_premium);

CREATE INDEX idx_content_qualities_content_id ON content_qualities(content_id);
CREATE INDEX idx_content_qualities_resolution ON content_qualities(resolution);

CREATE INDEX idx_viewing_history_user_id ON viewing_history(user_id);
CREATE INDEX idx_viewing_history_content_id ON viewing_history(content_id);
CREATE INDEX idx_viewing_history_watched_at ON viewing_history(watched_at);

CREATE INDEX idx_content_ratings_content_id ON content_ratings(content_id);
CREATE INDEX idx_content_ratings_user_id ON content_ratings(user_id);

CREATE INDEX idx_user_subscriptions_user_id ON user_subscriptions(user_id);
CREATE INDEX idx_user_subscriptions_plan_id ON user_subscriptions(subscription_plan_id);
CREATE INDEX idx_user_subscriptions_dates ON user_subscriptions(start_date, end_date);
CREATE INDEX idx_user_subscriptions_active ON user_subscriptions(active);
