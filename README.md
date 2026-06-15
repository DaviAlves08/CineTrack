# 🎬 CineTrack

CineTrack é uma aplicação web para catalogar filmes e séries, organizar uma lista pessoal de "quero assistir / assistindo / assistido" e avaliar cada título com comentários e notas em estrelas.

Os dados de filmes e séries (poster, descrição, nota, ano) são obtidos em tempo real através da API pública do **TMDB (The Movie Database)**.

🔗 **Demo online:** [cinetrack-qqbt.onrender.com](https://cinetrack-qqbt.onrender.com/filmes)

> O projeto está hospedado no plano gratuito do Render — se o site estiver "dormindo", a primeira requisição pode demorar de 30 a 60 segundos para responder.

---

## ✨ Funcionalidades

- Cadastro e login de usuários, com senhas criptografadas (BCrypt)
- Catálogo de filmes e séries populares, mais bem avaliados e em cartaz
- Busca por título direto na API do TMDB
- Lista pessoal organizada por abas (Filmes / Séries)
- Avaliação em estrelas (1 a 5) e comentários pessoais para cada item
- Edição de perfil (dados pessoais e senha)
- Estatísticas do perfil (total de itens, assistidos, assistindo, etc.)

---

## 🛠️ Tecnologias

- **Java 21** + **Spring Boot**
- **Thymeleaf** (templates)
- **PostgreSQL** + **JdbcTemplate**
- **TMDB API** (catálogo de filmes e séries)
- HTML, CSS e JavaScript puro

---

## ▶️ Como executar

1. Crie um banco PostgreSQL e configure as credenciais em `application.yml`
2. Configure sua chave da API do TMDB em `application.yml`
3. Execute o projeto:

```bash
./mvnw spring-boot:run
```

4. Acesse: **http://localhost:8080**

> As tabelas do banco são criadas automaticamente na primeira execução.

---

## 📁 Estrutura do projeto

```
src/main/java/com/filmes/cinetrack/
├── controller/        # Controlador principal (rotas)
└── model/              # Entidades, DAOs e Services

src/main/resources/
├── templates/          # Páginas Thymeleaf
├── static/css/         # Estilos
└── schema-postgresql.sql
```

---

Projeto acadêmico desenvolvido para a disciplina de Banco de Dados.
