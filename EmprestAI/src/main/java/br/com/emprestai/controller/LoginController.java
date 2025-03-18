package br.com.emprestai.controller;

import br.com.emprestai.model.Login;
import br.com.emprestai.service.LoginService;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {
    //private loginDAO loginDAO = new loginDAO();

    public boolean validaLogin(String CPF, String senha){
        // Passo 1: Buscar o cliente
        Login login = null; //= loginDAO.buscarPorCPF("42218555840");
        if (login == null) {
            throw new IllegalArgumentException("Erro: Cliente não encontrado");
        }
        String senhaHash = login.getSenhaBanco();
        return BCrypt.checkpw(senha, senhaHash);
    }

    public static String senhaHash(String plainPassword) {
        // Gera um hash com um salt automático (work factor padrão é 10, mas pode ser
        // ajustado)
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
}
