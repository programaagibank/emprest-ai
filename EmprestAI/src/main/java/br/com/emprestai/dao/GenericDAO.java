package br.com.emprestai.dao;

import br.com.emprestai.exception.ApiException;

import java.util.List;

public interface GenericDAO<T> {
    // Criar uma nova entidade
    T criar(T entidade) throws ApiException;

    // Buscar todas as entidades
    List<T> buscarTodos() throws ApiException;

    // Buscar uma entidade por ID
    T buscarPorId(Long id) throws ApiException;

    // Buscar por CPF
    T buscarPorCpf(String cpf) throws ApiException;

    // Atualizar uma entidade existente
    T atualizar(T entidade) throws ApiException;

    // Excluir uma entidade por ID
    boolean excluir(Long id) throws ApiException;
}