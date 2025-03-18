package br.com.emprestai.controller;

import br.com.emprestai.model.Login;
import br.com.emprestai.service.LoginService;

public class LoginController {
    //private loginDAO loginDAO = new loginDAO();

    public boolean validaLogin(String CPF, String senha){
        // Passo 1: Buscar o cliente
        Login login = null; //= loginDAO.buscarPorCPF("42218555840");
        if (login == null) {
            throw new IllegalArgumentException("Erro: Cliente não encontrado");
        }
        return LoginService.checarSenha(senha, login.getSenhaBanco());
    }
}
