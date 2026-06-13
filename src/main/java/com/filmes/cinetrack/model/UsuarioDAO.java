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
public class UsuarioDAO {

    @Autowired
    DataSource dataSource;

    JdbcTemplate jdbc;

    @PostConstruct
    private void initialize() {
        jdbc = new JdbcTemplate(dataSource);
    }

    public void inserirUsuario(Usuario u) {
        String sql = "INSERT INTO usuario(nome, email, senha) VALUES(?,?,?)";
        Object[] obj = new Object[3];
        obj[0] = u.getNome();
        obj[1] = u.getEmail();
        obj[2] = u.getSenha();
        jdbc.update(sql, obj);
    }

    public Usuario obterPorEmail(String email) {
        String sql = "SELECT * FROM usuario WHERE email = ?";
        return Usuario.converterRegistros(
            (Map<String, Object>) jdbc.queryForMap(sql, email)
        );
    }

    public Usuario obterPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        return Usuario.converterRegistros(
            (Map<String, Object>) jdbc.queryForMap(sql, id)
        );
    }

    public boolean existePorEmail(String email) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE email = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public void atualizar(Usuario u) {
        String sql = "UPDATE usuario SET nome = ?, email = ? WHERE id = ?";
        Object[] obj = new Object[3];
        obj[0] = u.getNome();
        obj[1] = u.getEmail();
        obj[2] = u.getId();
        jdbc.update(sql, obj);
    }

    public void atualizarSenha(Usuario u) {
        String sql = "UPDATE usuario SET senha = ? WHERE id = ?";
        Object[] obj = new Object[2];
        obj[0] = u.getSenha();
        obj[1] = u.getId();
        jdbc.update(sql, obj);
    }

    public List<Usuario> obterTodos() {
        String sql = "SELECT * FROM usuario";
        List<Map<String, Object>> listaRegistros = jdbc.queryForList(sql);
        ArrayList<Usuario> aux = new ArrayList<>();
        for (Map<String, Object> registro : listaRegistros) {
            aux.add(Usuario.converterRegistros(registro));
        }
        return aux;
    }
}
