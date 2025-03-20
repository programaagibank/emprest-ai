package br.com.emprestai.service;

import org.mindrot.jbcrypt.BCrypt;

public class LoginService {
    public static boolean authenticateUser(String cpf, String senha, String senhaHash) {
        return BCrypt.checkpw(senha, senhaHash);
    }
}
