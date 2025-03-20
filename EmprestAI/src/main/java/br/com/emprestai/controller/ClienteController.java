package br.com.emprestai.controller;

import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.model.Cliente;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class ClienteController {
    private final ClienteDAO clienteDAO;

    // Construtor com injeção de dependências
    public ClienteController(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    // Metodo para criar um cliente
    public Cliente criarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo.");
        }
        return clienteDAO.criar(cliente);
    }

    // Metodo para listar todos os clientes
    public List<Cliente> listarClientes() {
        return clienteDAO.buscarTodos();
    }

    // Metodo para buscar um cliente por ID
    public Cliente buscarClientePorId(Long idCliente) {
        if (idCliente == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo.");
        }
        return clienteDAO.buscarPorId(idCliente);
    }

    // Metodo para buscar um cliente por CPF
    public Cliente buscarClientePorCPF(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio.");
        }
        return clienteDAO.buscarPorCPF(cpf);
    }

    // Metodo para atualizar um cliente
    public Cliente atualizarCliente(Long idCliente, Cliente cliente) {
        if (idCliente == null || cliente == null) {
            throw new IllegalArgumentException("ID do cliente e cliente não podem ser nulos.");
        }
        return clienteDAO.atualizar(idCliente, cliente);
    }

    // Metodo para atualizar parcialmente um cliente
    public Cliente atualizarClienteParcial(Long idCliente, Cliente cliente) {
        if (idCliente == null || cliente == null) {
            throw new IllegalArgumentException("ID do cliente e cliente não podem ser nulos.");
        }
        return clienteDAO.atualizarParcial(idCliente, cliente);
    }

    // Metodo para deletar um cliente por ID
    public void deletarClientePorId(Long idCliente) {
        if (idCliente == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo.");
        }
        clienteDAO.excluir(idCliente);
    }

    // Metodo de login usando Bcrypt
    public boolean autenticarCliente(String cpf, String senha) {
        if (cpf == null || cpf.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF e senha não podem ser nulos ou vazios.");
        }
        Cliente cliente = clienteDAO.buscarPorCPF(cpf);
        return cliente != null && BCrypt.checkpw(senha, cliente.getSenha());
    }
}

