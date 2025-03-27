package br.com.emprestai.controller;

import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.model.Cliente;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class EncryptSenhaController {
    private final ClienteDAO clienteDAO;

    // Construtor com injeção de dependências
    public EncryptSenhaController(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    // Metodo para encryptar senha do cliente


    }

