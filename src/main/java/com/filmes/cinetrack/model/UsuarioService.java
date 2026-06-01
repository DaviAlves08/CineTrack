package com.filmes.cinetrack.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    UsuarioDAO udao;

    public void inserirUsuario(Usuario u) {
        udao.inserirUsuario(u);
    }

    public Usuario obterPorEmail(String email) {
        return udao.obterPorEmail(email);
    }

    public boolean existePorEmail(String email) {
        return udao.existePorEmail(email);
    }
}
