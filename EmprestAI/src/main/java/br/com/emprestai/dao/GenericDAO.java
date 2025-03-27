package br.com.emprestai.dao;

import java.util.List;

public interface GenericDAO<T> {
    T criar(T entidade);
    T buscarPorId(Long id);
    List<T> buscarTodos();
    T atualizar(Long id, T entidade);
    void excluir(Long id);
}

