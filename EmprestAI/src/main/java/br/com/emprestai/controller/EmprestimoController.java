package br.com.emprestai.controller;

import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.model.Cliente;

import java.util.HashMap;
import java.util.Map;

public class EmprestimoController {
    private ClienteDAO clienteDAO = new ClienteDAO();
    //Map<String, Object> request
    public Map<String, Object> simularEmprestimo() {
        Cliente cliente = clienteDAO.buscarPorId(1L);
        Map<String, Object> response = new HashMap<>();
        response.put("Cliente", cliente);
        return response;
    }
}
