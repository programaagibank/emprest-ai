package br.com.emprestai.controller;

import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.model.Cliente;

public class ClienteController {
    private final ClienteDAO clienteDAO;

    // Construtor com injeção de dependências
    public ClienteController(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    // Método para criar um cliente
    public Cliente criarCliente(Cliente cliente) {
        return clienteDAO.criar(cliente, null);
    }

    // Método para atualizar um cliente
    public Cliente atualizarCliente(Cliente cliente) {
        return clienteDAO.atualizar(null, cliente);
    }

    // Método para buscar um cliente por ID
    public Cliente buscarClientePorId(Long idCliente) {
        return clienteDAO.buscarPorId(idCliente);
    }

    // Método para deletar um cliente por ID
    public void deletarClientePorId(Long idCliente) {
        clienteDAO.excluir(idCliente);
    }
}
