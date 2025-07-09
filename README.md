# Tomazi Screen - Serviço de Streaming

Um serviço de streaming completo e robusto desenvolvido em Java com Spring Boot, seguindo os princípios SOLID e GRASP, implementando clean code.

## Características

### Arquitetura
- **Clean Architecture** com separação clara de responsabilidades
- **SOLID Principles** aplicados em toda a base de código
- **GRASP Patterns** para distribuição de responsabilidades
- **Domain-Driven Design** com entidades ricas
- **Repository Pattern** para abstração de dados
- **Service Layer** para lógica de negócio

### Funcionalidades Principais

#### Gestão de Usuários
- Cadastro e autenticação com JWT
- Diferentes níveis de acesso (VIEWER, CREATOR, MODERATOR, ADMIN)
- Perfis de usuário completos
- Sistema de ativação/desativação

#### Gestão de Conteúdo
- Upload de múltiplos formatos (vídeo, áudio, podcasts)
- Processamento automático de qualidade múltipla
- Sistema de categorização
- Controle de visibilidade (público/privado)
- Conteúdo premium com controle de acesso

#### Sistema de Streaming
- Streaming adaptativo com múltiplas qualidades
- Suporte a diferentes resoluções (480p, 720p, 1080p)
- Geração automática de thumbnails
- Controle de bandwidth otimizado

#### Análise e Métricas
- Histórico de visualização detalhado
- Contadores de views e likes
- Sistema de avaliações (1-5 estrelas)
- Analytics de engajamento

#### Sistema de Assinaturas
- Múltiplos planos de assinatura
- Controle de acesso baseado em assinatura
- Renovação automática
- Gestão de pagamentos

### Tecnologias Utilizadas

#### Backend
- **Java 17** - Linguagem principal
- **Spring Boot 3.2** - Framework principal
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL** - Banco de dados principal
- **Redis** - Cache e sessões
- **Kafka** - Mensageria assíncrona

#### Storage e Media
- **MinIO** - Object storage para arquivos
- **JWT** - Tokens de autenticação
- **MapStruct** - Mapeamento de objetos

#### Documentação e Monitoramento
- **OpenAPI/Swagger** - Documentação da API
- **Spring Actuator** - Métricas e health checks

## Estrutura do Projeto

```
src/main/java/com/tomazi/streaming/
├── domain/                 # Domínio da aplicação
│   ├── entities/          # Entidades de negócio
│   └── repositories/      # Interfaces de repositório
├── application/           # Camada de aplicação
│   └── services/         # Serviços de negócio
├── infrastructure/       # Infraestrutura
│   ├── config/          # Configurações
│   ├── exceptions/      # Tratamento de exceções
│   └── security/        # Segurança
└── presentation/         # Camada de apresentação
    ├── controllers/     # Controllers REST
    ├── dto/            # Data Transfer Objects
    └── mappers/        # Mapeadores
```

## Como Executar

### Pré-requisitos
- Java 17+
- Docker e Docker Compose
- Maven 3.8+

### Configuração do Ambiente

1. **Banco de Dados PostgreSQL**
```bash
docker run -d --name streaming-postgres \
  -e POSTGRES_DB=streaming_db \
  -e POSTGRES_USER=streaming_user \
  -e POSTGRES_PASSWORD=streaming_pass \
  -p 5432:5432 postgres:15
```

2. **Redis**
```bash
docker run -d --name streaming-redis \
  -p 6379:6379 redis:7-alpine
```

3. **MinIO (Object Storage)**
```bash
docker run -d --name streaming-minio \
  -p 9000:9000 -p 9001:9001 \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin \
  minio/minio server /data --console-address ":9001"
```

4. **Kafka**
```bash
docker run -d --name streaming-kafka \
  -p 9092:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  confluentinc/cp-kafka:latest
```

### Executando a Aplicação

```bash
# Clone o repositório
git clone <repository-url>
cd TomaziScreen_ServicoStreaming

# Compile e execute
mvn clean install
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

### Documentação da API
Acesse: `http://localhost:8080/swagger-ui.html`

## Autenticação

### Usuários Padrão
- **Admin**: `admin` / `admin123`
- **Creator**: `creator` / `admin123`
- **Viewer**: `viewer` / `admin123`

### Obtendo Token JWT
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## Endpoints Principais

### Autenticação
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Registro
- `GET /api/auth/validate` - Validar token

### Usuários
- `GET /api/users` - Listar usuários
- `POST /api/users` - Criar usuário
- `GET /api/users/{id}` - Buscar usuário
- `PUT /api/users/{id}` - Atualizar usuário

### Conteúdo
- `POST /api/content` - Upload de conteúdo
- `GET /api/content` - Listar conteúdo público
- `GET /api/content/search` - Buscar conteúdo
- `GET /api/content/{id}/stream` - Stream de conteúdo
- `POST /api/content/{id}/like` - Curtir conteúdo

## 🔧 Configuração

### Variáveis de Ambiente
```properties
# Database
DB_USERNAME=streaming_user
DB_PASSWORD=streaming_pass

# JWT
JWT_SECRET=mySecretKey123456789012345678901234567890

# Storage
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

## Segurança

- Autenticação JWT com refresh tokens
- Autorização baseada em roles
- Validação de entrada com Bean Validation
- Proteção CORS configurável
- Rate limiting implementado

## Monitoramento

### Health Checks
- `GET /actuator/health` - Status da aplicação
- `GET /actuator/metrics` - Métricas da aplicação

### Logs
Logs estruturados com níveis configuráveis para monitoramento e debugging.
