package br.com.emprestai.controller;

import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.service.validator.ClienteValidator;

import java.util.List;

public class ClienteController {

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private final ClienteDAO clienteDAO;

    // --------------------------------------------------------------------------------
    // Constructor
    // --------------------------------------------------------------------------------
    public ClienteController(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    // --------------------------------------------------------------------------------
    // CRUD Methods
    // --------------------------------------------------------------------------------

    // POST - Criar um novo cliente
    public Cliente post(Cliente cliente) throws ApiException {
        ClienteValidator.validarClienteParaCriacao(cliente);
        return clienteDAO.criar(cliente);
    }

    // GET - Buscar todos os clientes
    public List<Cliente> getTodos() throws ApiException {
        return clienteDAO.buscarTodos();
    }

    // GET - Buscar cliente por CPF
    public Cliente getByCpf(String cpf) throws ApiException {
        ClienteValidator.validarCpf(cpf);
        Cliente cliente = clienteDAO.buscarPorCpf(cpf);
        if (cliente == null) {
            throw new ApiException("Cliente não encontrado para o CPF informado.", 404); // Not Found
        }
        return cliente;
    }

    // PUT - Atualizar um cliente existente
    public Cliente put(Cliente cliente) throws ApiException {
        ClienteValidator.validarClienteParaAtualizacao(cliente);
        return clienteDAO.atualizar(cliente);
    }

//    // DELETE - Remover um cliente por ID
//    public void delete(Long idCliente) throws ApiException {
//        ClienteValidator.validarId(idCliente);
//        boolean excluido = clienteDAO.excluir(idCliente);
//        if (!excluido) {
//            throw new ApiException("Cliente não encontrado para exclusão.", 404); // Not Found
//        }
//    }
}