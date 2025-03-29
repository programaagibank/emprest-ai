package br.com.emprestai.controller;

import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Cliente;
import java.util.List;

import javafx.event.ActionEvent;
import org.mindrot.jbcrypt.BCrypt;

public class ClienteController {
    private final ClienteDAO clienteDAO;

    // Construtor com injeção de dependências
    public ClienteController(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public Cliente post(Cliente cliente) throws ApiException {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo.");
        }
        return clienteDAO.criar(cliente);
    }

    public List<Cliente> getTodos() throws ApiException {
        return clienteDAO.buscarTodos();
    }

    public Cliente get(Long idCliente) throws ApiException {
        if (idCliente == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo.");
        }
        return clienteDAO.buscarPorId(idCliente);
    }

    public Cliente get(String cpf) throws ApiException {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio.");
        }
        return clienteDAO.buscarPorCpf(cpf);
    }

    public Cliente put(Cliente cliente) throws ApiException {
        if (cliente == null) {
            throw new IllegalArgumentException("ID do cliente e cliente não podem ser nulos.");
        }
        return clienteDAO.atualizar(cliente);    }

    public void delete(Long idCliente) throws ApiException {
        if (idCliente == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo.");
        }
        clienteDAO.excluir(idCliente);
    }
}
