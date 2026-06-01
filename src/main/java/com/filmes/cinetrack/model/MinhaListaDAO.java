package com.filmes.cinetrack.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;

@Repository
public class MinhaListaDAO {

    @Autowired
    DataSource dataSource;

    JdbcTemplate jdbc;

    @PostConstruct
    private void initialize() {
        jdbc = new JdbcTemplate(dataSource);
    }

    public void inserir(MinhaLista item) {
        String sql = "INSERT INTO minha_lista(usuario_id, tmdb_id, titulo, tipo, poster_path, status, nota) VALUES(?,?,?,?,?,?,?)";
        Object[] obj = new Object[7];
        obj[0] = item.getUsuarioId();
        obj[1] = item.getTmdbId();
        obj[2] = item.getTitulo();
        obj[3] = item.getTipo();
        obj[4] = item.getPosterPath();
        obj[5] = item.getStatus();
        obj[6] = item.getNota();
        jdbc.update(sql, obj);
    }

    public void atualizar(int id, MinhaLista novo) {
        String sql = "UPDATE minha_lista SET status = ?, nota = ? WHERE id = ?";
        Object[] obj = new Object[3];
        obj[0] = novo.getStatus();
        obj[1] = novo.getNota();
        obj[2] = id;
        jdbc.update(sql, obj);
    }

    public void excluir(int id) {
        String sql = "DELETE FROM minha_lista WHERE id = ?";
        jdbc.update(sql, id);
    }

    public MinhaLista obterPorId(int id) {
        String sql = "SELECT * FROM minha_lista WHERE id = ?";
        return MinhaLista.converterRegistros(
            (Map<String, Object>) jdbc.queryForMap(sql, id)
        );
    }

    public List<MinhaLista> obterPorUsuario(int usuarioId) {
        String sql = "SELECT * FROM minha_lista WHERE usuario_id = ?";
        List<Map<String, Object>> listaRegistros = jdbc.queryForList(sql, usuarioId);
        ArrayList<MinhaLista> aux = new ArrayList<>();
        for (Map<String, Object> registro : listaRegistros) {
            aux.add(MinhaLista.converterRegistros(registro));
        }
        return aux;
    }

    public boolean existePorUsuarioETmdb(int usuarioId, long tmdbId) {
        String sql = "SELECT COUNT(*) FROM minha_lista WHERE usuario_id = ? AND tmdb_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, usuarioId, tmdbId);
        return count != null && count > 0;
    }
}
