package br.com.emprestai.view;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.service.LoginService;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ClienteView {
    private final ClienteController clienteController;
    private static final Scanner scanner = new Scanner(System.in);

    public ClienteView(ClienteController clienteController) {
        this.clienteController = clienteController;
    }

    // Tela de Login
    public Cliente exibirLogin() {
        System.out.println("\n\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.println("\033[1;33m=== LOGIN ===\033[0m");

        System.out.print("\033[1;32m➜ Digite seu CPF: \033[0m");
        String cpf = scanner.nextLine();

        System.out.print("\033[1;32m➜ Digite sua senha: \033[0m");
        String senha = scanner.nextLine();

        Cliente cliente = clienteController.buscarClientePorCPF(cpf);
        if (cliente != null && LoginService.authenticateUser(senha, cliente.getSenha())) {
            System.out.println("\033[1;32m✓ Login realizado com sucesso!\033[0m");
            System.out.println("\033[1;36mBem-vindo, " + cliente.getNomecliente() + "!\033[0m");
            System.out.println("\033[1;34m" + "=".repeat(50) + "\033[0m");
            return cliente;
        } else {
            System.out.println("\033[1;31m✗ CPF ou senha incorretos!\033[0m");
            System.out.println("\033[1;34m" + "=".repeat(50) + "\033[0m");
            return null;
        }
    }

    // Tela de Criação de Usuário
    public Cliente exibirCriarUsuario() {
        System.out.println("\n\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.println("\033[1;33m=== CRIAR NOVO USUÁRIO ===\033[0m");

        System.out.print("\033[1;32m➜ Digite o CPF: \033[0m");
        String cpf = scanner.nextLine();

        System.out.print("\033[1;32m➜ Digite o nome: \033[0m");
        String nome = scanner.nextLine();

        System.out.print("\033[1;32m➜ Digite a senha: \033[0m");
        String senha = scanner.nextLine();

        System.out.print("\033[1;32m➜ Digite a data de nascimento (dd/MM/yyyy): \033[0m");
        String dataInput = scanner.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataNascimento;

        try {
            dataNascimento = LocalDate.parse(dataInput, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("\033[1;31m✗ Formato de data inválido! Use dd/MM/yyyy.\033[0m");
            System.out.println("\033[1;34m" + "=".repeat(50) + "\033[0m");
            return null;
        }

        Cliente novoCliente = new Cliente();
        novoCliente.setCpfCliente(cpf);
        novoCliente.setNomecliente(nome);
        novoCliente.setSenha(BCrypt.hashpw(senha, BCrypt.gensalt()));
        novoCliente.setDataNascimento(dataNascimento);

        try {
            Cliente cliente = clienteController.criarCliente(novoCliente);
            System.out.println("\033[1;32m✓ Usuário criado com sucesso!\033[0m");
            System.out.println("\033[1;34m" + "=".repeat(50) + "\033[0m");
            return cliente;
        } catch (Exception e) {
            System.out.println("\033[1;31m✗ Erro ao criar usuário: " + e.getMessage() + "\033[0m");
            System.out.println("\033[1;34m" + "=".repeat(50) + "\033[0m");
            return null;
        }
    }
}