package com.filmes.cinetrack.model;

import java.util.Map;

// POJO - Plain Old Java Object
public class MinhaLista {

    private int    id;
    private int    usuarioId;
    private long   tmdbId;
    private String titulo, tipo, posterPath, status, nota;

    // Início do form
    public MinhaLista() {}

    // Insert
    public MinhaLista(int usuarioId, long tmdbId, String titulo,
                      String tipo, String posterPath, String status, String nota) {
        this.usuarioId  = usuarioId;
        this.tmdbId     = tmdbId;
        this.titulo     = titulo;
        this.tipo       = tipo;
        this.posterPath = posterPath;
        this.status     = status;
        this.nota       = nota;
    }

    // Select
    public MinhaLista(int id, int usuarioId, long tmdbId, String titulo,
                      String tipo, String posterPath, String status, String nota) {
        this.id         = id;
        this.usuarioId  = usuarioId;
        this.tmdbId     = tmdbId;
        this.titulo     = titulo;
        this.tipo       = tipo;
        this.posterPath = posterPath;
        this.status     = status;
        this.nota       = nota;
    }

    public int    getId()         { return id; }
    public int    getUsuarioId()  { return usuarioId; }
    public long   getTmdbId()     { return tmdbId; }
    public String getTitulo()     { return titulo; }
    public String getTipo()       { return tipo; }
    public String getPosterPath() { return posterPath; }
    public String getStatus()     { return status; }
    public String getNota()       { return nota; }

    public void setId(int id)                { this.id         = id; }
    public void setUsuarioId(int usuarioId)  { this.usuarioId  = usuarioId; }
    public void setTmdbId(long tmdbId)       { this.tmdbId     = tmdbId; }
    public void setTitulo(String titulo)     { this.titulo     = titulo; }
    public void setTipo(String tipo)         { this.tipo       = tipo; }
    public void setPosterPath(String p)      { this.posterPath = p; }
    public void setStatus(String status)     { this.status     = status; }
    public void setNota(String nota)         { this.nota       = nota; }

    public static MinhaLista converterRegistros(Map<String, Object> registros) {
        int    id         = (int)    registros.get("id");
        int    usuarioId  = (int)    registros.get("usuario_id");
        long   tmdbId     = ((Number) registros.get("tmdb_id")).longValue();
        String titulo     = (String) registros.get("titulo");
        String tipo       = (String) registros.get("tipo");
        String posterPath = (String) registros.get("poster_path");
        String status     = (String) registros.get("status");
        String nota       = (String) registros.get("nota");
        return new MinhaLista(id, usuarioId, tmdbId, titulo, tipo, posterPath, status, nota);
    }
}
