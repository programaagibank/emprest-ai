package br.com.emprestai.controller;

import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.enums.VinculoEnum;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.service.calculos.CalculoConsignado;
import br.com.emprestai.service.calculos.CalculoPessoal;

import java.util.List;

import static br.com.emprestai.enums.VinculoEnum.*;

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
        if (cliente == null) {
            throw new ApiException("Cliente não pode ser nulo.", 400); // Bad Request
        }
        return clienteDAO.criar(cliente);
    }

    // GET - Buscar todos os clientes
    public List<Cliente> getTodos() throws ApiException {
        return clienteDAO.buscarTodos();
    }

    // GET - Buscar cliente por CPF
    public Cliente getByCpf(String cpf) throws ApiException {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new ApiException("CPF não pode ser nulo ou vazio.", 400); // Bad Request
        }

        Cliente cliente = clienteDAO.buscarPorCpf(cpf);
        if (cliente == null) {
            throw new ApiException("Cliente não encontrado para o CPF informado.", 404); // Not Found
        }

        if(cliente.getTipoCliente() == APOSENTADO || cliente.getTipoCliente() ==PENSIONISTA || cliente.getTipoCliente() == SERVIDOR){
            cliente.setMargemConsignavel(CalculoConsignado.calcularMargemEmprestimoConsig(
                    cliente.getRendaMensalLiquida(), cliente.getParcelasAtivas()));
        }

        cliente.setMargemPessoal(CalculoPessoal.calculoDeCapacidadeDePagamento(
                cliente.getRendaMensalLiquida(), cliente.getParcelasAtivas()));

        return cliente;
    }

    // PUT - Atualizar um cliente existente
    public Cliente put(Cliente cliente) throws ApiException {
        if (cliente == null || cliente.getIdCliente() == null) {
            throw new ApiException("ID do cliente e cliente não podem ser nulos.", 400); // Bad Request
        }
        return clienteDAO.atualizar(cliente);
    }

    // DELETE - Remover um cliente por ID
    public void delete(Long idCliente) throws ApiException {
        if (idCliente == null) {
            throw new ApiException("ID do cliente não pode ser nulo.", 400); // Bad Request
        }

        boolean excluido = clienteDAO.excluir(idCliente);
        if (!excluido) {
            throw new ApiException("Empréstimo não encontrado para exclusão.", 404); // Not Found
        }
    }
}