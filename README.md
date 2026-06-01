# 🎬 CineTrack

Catálogo de filmes e séries com lista pessoal.
**Stack:** Spring Boot 4 · JdbcTemplate · Thymeleaf · PostgreSQL · TMDB API

---

## ⚙️ Setup

### 1. Criar o banco no PostgreSQL
```sql
CREATE DATABASE cinetrack_db;
```
> As tabelas são criadas automaticamente pelo `schema-postgresql.sql` ao iniciar.

### 2. Configurar `application.yaml`
```yaml
spring:
  datasource:
    url: jdbc:postgresql://127.0.0.1:5432/cinetrack_db
    username: SEU_USUARIO
    password: SUA_SENHA

tmdb:
  api:
    key: SUA_CHAVE_TMDB
```

### 3. Obter chave da TMDB (gratuito)
1. Crie conta em https://www.themoviedb.org/signup
2. Vá em **Configurações → API → Criar chave**
3. Cole a **API Key (v3)** no `application.yaml`

### 4. Rodar
```bash
mvn spring-boot:run
```
Acesse: **http://localhost:8080**

---

## 🗂 Estrutura (padrão AulaBD)

```
model/
  Usuario.java          ← POJO + converterRegistros()
  UsuarioDAO.java       ← @Repository + JdbcTemplate + SQL puro
  UsuarioService.java   ← @Service
  MinhaLista.java       ← POJO + converterRegistros()
  MinhaListaDAO.java    ← @Repository + JdbcTemplate + SQL puro
  MinhaListaService.java← @Service
  TmdbService.java      ← consome API TMDB

controller/
  MainController.java   ← @Controller + ApplicationContext.getBean()

resources/
  application.yaml
  schema-postgresql.sql
  templates/  login · registro · catalogo · lista · formeditar
  static/css/ style.css
```

---

## 🔑 Rotas

| Método | Rota | Descrição |
|---|---|---|
| GET | `/` | Redireciona para login ou catálogo |
| GET/POST | `/login` | Login |
| GET/POST | `/registro` | Cadastro |
| GET | `/logout` | Encerra sessão |
| GET | `/catalogo` | Grid de filmes/séries populares |
| GET | `/catalogo?busca=xxx` | Busca na TMDB |
| POST | `/catalogo/adicionar` | Adiciona item à lista |
| GET | `/lista` | Minha lista pessoal |
| GET/POST | `/lista/{id}/editar` | Editar status e nota |
| POST | `/lista/{id}/excluir` | Remover da lista |
