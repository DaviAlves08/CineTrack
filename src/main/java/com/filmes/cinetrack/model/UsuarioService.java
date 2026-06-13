package com.filmes.cinetrack.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    UsuarioDAO udao;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public void inserirUsuario(Usuario u) {
        u.setSenha(encoder.encode(u.getSenha()));
        udao.inserirUsuario(u);
    }

    public boolean verificarSenha(String senhaDigitada, String senhaHash) {
        return encoder.matches(senhaDigitada, senhaHash);
    }

    public void atualizar(Usuario u) {
        udao.atualizar(u);
    }

    public void alterarSenha(Usuario u, String novaSenha) {
        u.setSenha(encoder.encode(novaSenha));
        udao.atualizarSenha(u);
    }

    public Usuario obterPorEmail(String email) {
        return udao.obterPorEmail(email);
    }

    public boolean existePorEmail(String email) {
        return udao.existePorEmail(email);
    }
}
