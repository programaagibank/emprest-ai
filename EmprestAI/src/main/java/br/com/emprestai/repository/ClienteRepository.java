package br.com.emprestai.repository;

import br.com.emprestai.model.Cliente;

import java.util.Optional;

public interface ClienteRepository {
    Optional<Cliente> findById(int idCliente);
}
