package br.com.emprestai.controller;

import br.com.emprestai.model.Cliente;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private ClienteController clienteController;

    // --------------------------------------------------------------------------------
    // Constructor
    // --------------------------------------------------------------------------------
    public LoginController(ClienteController clienteController) {
        this.clienteController = clienteController;
    }

    // --------------------------------------------------------------------------------
    // Authentication Method
    // --------------------------------------------------------------------------------

    // GET - Buscar cliente e autenticar login
    public Cliente autenticaLogin(String cpf, String senhaInformada) {
        try {
            // Validação inicial de parâmetros
            if (cpf == null || cpf.isEmpty() || senhaInformada == null || senhaInformada.isEmpty()) {
                return null;
            }

            // Busca o cliente pelo CPF
            Cliente cliente = clienteController.getByCpf(cpf);

            // Verifica se a senha informada corresponde ao hash armazenado
            if (BCrypt.checkpw(senhaInformada, cliente.getSenha())) {
                return cliente;
            }
        } catch (Exception e) {
            System.out.println("Erro ao verificar login: " + e.getMessage());
        }
        return null;
    }

    public boolean validaSenha(String senhaInformada, String senhaHash) {
        // Verifica se a senha informada corresponde ao hash armazenado
        if (BCrypt.checkpw(senhaInformada, senhaHash)) {
            return true;
        }
        return false;
    }

}