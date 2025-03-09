package br.com.emprestai.controller.clienteController;

import br.com.emprestai.dao.clienteDAO.ClienteDAO;
import br.com.emprestai.dataBaseConfig.exception.ApiException;

import br.com.emprestai.dataBaseConfig.jsonUtil.JsonUtil;
import br.com.emprestai.model.Cliente;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;




import java.util.HashMap;
import java.util.Map;

public class ClienteController {
    private final ClienteDAO clienteDAO;

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();

         this.clienteDAO.criarTabela();
    }

    // Método para criar um cliente
    public void criarCliente(Context ctx) {
        Cliente cliente = JsonUtil.fromJson(ctx.body(), Cliente.class);
        Cliente novoCliente = clienteDAO.criar(cliente);
        ctx.status(HttpStatus.CREATED);
        ctx.json(novoCliente);
    }

    // Método para buscar todos os clientes
    public void buscarTodosClientes(Context ctx) {
        ctx.json(clienteDAO.buscarTodos());
    }

    // Método para buscar cliente por ID
    public void buscarClientePorId(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        Cliente cliente = clienteDAO.buscarPorId(id);
        ctx.json(cliente);
    }

    // Método para atualizar cliente completo
    public void atualizarCliente(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        Cliente cliente = JsonUtil.fromJson(ctx.body(), Cliente.class);
        Cliente clienteAtualizado = clienteDAO.atualizar(id, cliente);
        ctx.json(clienteAtualizado);
    }

    // Método para atualizar cliente parcialmente
    public void atualizarClienteParcial(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        Cliente cliente = JsonUtil.fromJson(ctx.body(), Cliente.class);
        Cliente clienteAtualizado = clienteDAO.atualizarParcial(id, cliente);
        ctx.json(clienteAtualizado);
    }

    // Método para excluir cliente
    public void excluirCliente(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        clienteDAO.excluir(id);
        ctx.status(HttpStatus.NO_CONTENT);
    }

    // Método para tratar ApiException
    public static void handleApiException(ApiException e, Context ctx) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", e.getStatusCode());
        error.put("mensagem", e.getMessage());

        ctx.status(e.getStatusCode());
        ctx.json(error);
    }
}
