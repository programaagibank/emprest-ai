package br.com.emprestai.controller;

import br.com.emprestai.dao.LoginDAO;
import br.com.emprestai.model.Login;
import br.com.emprestai.service.LoginService;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {
    //private loginDAO loginDAO = new loginDAO();

    public static boolean validaLogin(String CPF, String senha){
        // Passo 1: Buscar o cliente
        LoginDAO loginDao = new LoginDAO();

        Login login = loginDao.buscarPorUsuario(CPF);
        if (login == null) {
            throw new IllegalArgumentException("Erro: Cliente não encontrado");
        }
        String senhaHash = login.getSenha();
        return BCrypt.checkpw(senha, senhaHash);

    }

    public static Login criarLogin(String CPF, String senha){
        // Passo 1: Buscar o cliente
        LoginDAO loginDao = new LoginDAO();
        Login login = new Login(null, CPF, senhaHash(senha));

        Login loginUser = loginDao.criar(login);
        if (loginUser == null) {
            throw new IllegalArgumentException("Erro: Ao criar cliente");
        }
        return loginUser;
    }

    public static String senhaHash(String plainPassword) {
        // Gera um hash com um salt automático (work factor padrão é 10, mas pode ser ajustado)
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
}
