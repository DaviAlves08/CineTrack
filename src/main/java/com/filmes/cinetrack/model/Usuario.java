package com.filmes.cinetrack.model;

import java.util.Map;


public class Usuario {

    private int id;
    private String nome, email, senha;


    public Usuario() {}


    public Usuario(String nome, String email, String senha) {
        this.nome  = nome;
        this.email = email;
        this.senha = senha;
    }

    public Usuario(int id, String nome, String email, String senha) {
        this.id    = id;
        this.nome  = nome;
        this.email = email;
        this.senha = senha;
    }

    public int    getId()    { return id; }
    public String getNome()  { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }

    public void setId(int id)          { this.id    = id; }
    public void setNome(String nome)   { this.nome  = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }

    public static Usuario converterRegistros(Map<String, Object> registros) {
        int    id    = (int)    registros.get("id");
        String nome  = (String) registros.get("nome");
        String email = (String) registros.get("email");
        String senha = (String) registros.get("senha");
        return new Usuario(id, nome, email, senha);
    }
}
