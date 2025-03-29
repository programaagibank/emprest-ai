package br.com.emprestai.controller;

import br.com.emprestai.model.Cliente;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {

    private ClienteController clienteController;// Injetar via DI em um sistema real

    public LoginController(ClienteController clienteController) {
        this.clienteController = clienteController;
    }

    // Metodo para verificar login
    public Cliente autenticaLogin(String cpf, String senhaInformada) {
        try {
            if (cpf == null || cpf.isEmpty() || senhaInformada == null || senhaInformada.isEmpty()) {
                return null;
            }

            Cliente cliente = clienteController.get(cpf);

            // Verifica se a senha informada corresponde ao hash armazenado
            if(BCrypt.checkpw(senhaInformada, cliente.getSenha())){
                return cliente;
            }
        } catch (Exception e) {
            System.out.println("Erro ao verificar login: " + e.getMessage());
        }
        return null;
    }
}
