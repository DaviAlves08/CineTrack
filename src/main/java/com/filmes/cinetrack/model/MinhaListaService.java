package com.filmes.cinetrack.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MinhaListaService {

    @Autowired
    MinhaListaDAO mldao;

    public void inserir(MinhaLista item) {
        mldao.inserir(item);
    }

    public void atualizar(int id, MinhaLista novo) {
        mldao.atualizar(id, novo);
    }

    public void excluir(int id) {
        mldao.excluir(id);
    }

    public MinhaLista obterPorId(int id) {
        return mldao.obterPorId(id);
    }

    public List<MinhaLista> obterPorUsuario(int usuarioId) {
        return mldao.obterPorUsuario(usuarioId);
    }

    public boolean existePorUsuarioETmdb(int usuarioId, long tmdbId) {
        return mldao.existePorUsuarioETmdb(usuarioId, tmdbId);
    }
}
