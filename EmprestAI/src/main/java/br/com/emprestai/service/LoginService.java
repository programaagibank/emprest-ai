package br.com.emprestai.service;

import org.mindrot.jbcrypt.BCrypt;

public class LoginService {

    public static String senhaHash(String plainPassword) {
        // Gera um hash com um salt automático (work factor padrão é 10, mas pode ser ajustado)
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checarSenha(String senhaEnviada, String senhaHash) {
        // Verifica se a senha em texto puro corresponde ao hash
        return BCrypt.checkpw(senhaEnviada, senhaHash);
    }

}
