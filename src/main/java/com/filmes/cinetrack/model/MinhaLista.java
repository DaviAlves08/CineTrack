package com.filmes.cinetrack.model;

import java.util.Map;

// POJO - Plain Old Java Object
public class MinhaLista {

    private int    id;
    private int    usuarioId;
    private long   tmdbId;
    private String titulo, tipo, posterPath, status, nota;
    private int    avaliacao; // 0-5 estrelas

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
        this.avaliacao  = 0;
    }

    // Select
    public MinhaLista(int id, int usuarioId, long tmdbId, String titulo,
                      String tipo, String posterPath, String status, String nota, int avaliacao) {
        this.id         = id;
        this.usuarioId  = usuarioId;
        this.tmdbId     = tmdbId;
        this.titulo     = titulo;
        this.tipo       = tipo;
        this.posterPath = posterPath;
        this.status     = status;
        this.nota       = nota;
        this.avaliacao  = avaliacao;
    }

    public int    getId()          { return id; }
    public int    getUsuarioId()   { return usuarioId; }
    public long   getTmdbId()      { return tmdbId; }
    public String getTitulo()      { return titulo; }
    public String getTipo()        { return tipo; }
    public String getPosterPath()  { return posterPath; }
    public String getStatus()      { return status; }
    public String getNota()        { return nota; }
    public int    getAvaliacao()   { return avaliacao; }

    public void setId(int id)                { this.id         = id; }
    public void setUsuarioId(int usuarioId)  { this.usuarioId  = usuarioId; }
    public void setTmdbId(long tmdbId)       { this.tmdbId     = tmdbId; }
    public void setTitulo(String titulo)     { this.titulo     = titulo; }
    public void setTipo(String tipo)         { this.tipo       = tipo; }
    public void setPosterPath(String p)      { this.posterPath = p; }
    public void setStatus(String status)     { this.status     = status; }
    public void setNota(String nota)         { this.nota       = nota; }
    public void setAvaliacao(int avaliacao)  { this.avaliacao  = avaliacao; }

    public static MinhaLista converterRegistros(Map<String, Object> r) {
        int    id         = (int)     r.get("id");
        int    usuarioId  = (int)     r.get("usuario_id");
        long   tmdbId     = ((Number) r.get("tmdb_id")).longValue();
        String titulo     = (String)  r.get("titulo");
        String tipo       = (String)  r.get("tipo");
        String posterPath = (String)  r.get("poster_path");
        String status     = (String)  r.get("status");
        String nota       = (String)  r.get("nota");
        int    avaliacao  = r.get("avaliacao") != null ? ((Number) r.get("avaliacao")).intValue() : 0;
        return new MinhaLista(id, usuarioId, tmdbId, titulo, tipo, posterPath, status, nota, avaliacao);
    }
}
