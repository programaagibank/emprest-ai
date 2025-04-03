package br.com.emprestai.util;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.model.Cliente;

public class SessionManager {
    private static final SessionManager instance = new SessionManager();
    private Cliente clienteLogado;
    private final ClienteController clienteController;

    private SessionManager() {
        this.clienteController = new ClienteController(new ClienteDAO());
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public void setClienteLogado(Cliente cliente) {
        this.clienteLogado = cliente;
    }

    public Cliente getClienteLogado() {
        return clienteLogado;
    }

    public void clearSession() {
        clienteLogado = null;
    }

    public void refreshClienteLogado() {
        if (clienteLogado != null && clienteLogado.getIdCliente() != null && clienteLogado.getCpfCliente() != null) {
            try {
                Cliente clienteAtualizado = clienteController.getByCpf(clienteLogado.getCpfCliente());
                if (clienteAtualizado != null) {
                    clienteLogado = clienteAtualizado;
                } else {
                    clearSession();
                }
            } catch (Exception e) {
                e.printStackTrace();
                clearSession();
            }
        }
    }
}