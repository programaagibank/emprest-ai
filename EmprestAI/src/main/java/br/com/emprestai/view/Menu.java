package br.com.emprestai.service;

import br.com.emprestai.App;
import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.enums.VinculoEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.util.EmprestimoParams;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Properties;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

public class Menu {

    private static final Scanner scanner = new Scanner(System.in);
    private static final EmprestimoParams params = EmprestimoParams.getInstance();
    private final Properties properties = new Properties();
    private final ClienteController clienteController;

    public Menu() {
        ClienteDAO clienteDAO = new ClienteDAO();
        this.clienteController = new ClienteController(clienteDAO);
    }

    // Metodo adicionado do App.java
    public void carregarPropriedadesBanco() {
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("Arquivo database.properties não encontrado no classpath.");
            }
            properties.load(input);
            System.out.println("Conectando ao banco de dados em: " + properties.getProperty("db.url"));
        } catch (Exception e) {
            System.out.println("Erro ao carregar configurações do banco de dados: " + e.getMessage());
        }
    }

    // Metodo adicionado do App.java (menu principal simplificado)
    public void mostrarMenuPrincipal() {
        System.out.println("\n===== Sistema de Gerenciamento de Clientes =====");
        System.out.println("1 - Logar");
        System.out.println("2 - Criar Usuário");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    public boolean exibirLogin() {
        System.out.println("Digite seu CPF:"); // Ajustado para CPF
        String cpf = scanner.nextLine();
        System.out.println("Digite sua senha:");
        String senha = scanner.nextLine();
        Cliente cliente = clienteController.buscarClientePorCPF(cpf);

        if (LoginService.authenticateUser(cpf, senha, cliente.getSenha())) {
            System.out.println("Login realizado com sucesso!");
            try {
                System.out.println("Bem-vindo, " + cliente.getNomecliente() + "!");
            } catch (Exception e) {
                System.out.println("Bem-vindo ao sistema!");
            }
            return true;
        } else {
            System.out.println("CPF ou senha incorretos.");
            return false;
        }
    }

    public void mostrarMenuConsignado() {
        System.out.println("Bem-vindo ao Gerenciador de Empréstimos para Empréstimo Consignados");
        System.out.println("Escolha uma opção:");
        System.out.println("1. Simular Empréstimo Consignado");
        System.out.println("2. Buscar Seu Empréstimos Consignado");
        System.out.println("0. Voltar ao menu anterior");
    }

    public void mostrarMenuPessoal() {
        System.out.println("Bem-vindo ao Gerenciador de Empréstimos para Empréstimo Pessoal");
        System.out.println("Escolha uma opção:");
        System.out.println("1. Simular Empréstimo Pessoal");
        System.out.println("2. Buscar Seu Empréstimo Pessoal");
        System.out.println("0. Voltar ao menu anterior");
    }

    public boolean exibirCriarUsuario() {
        System.out.println("\n===== Criar Novo Usuário =====");

        System.out.print("Digite o CPF: ");
        String cpf = scanner.nextLine();

        System.out.print("Digite o nome: ");
        String nome = scanner.nextLine();

        System.out.print("Digite a senha: ");
        String senha = scanner.nextLine();

        System.out.print("Digite a renda mensal líquida: ");
        double rendaMensal = Double.parseDouble(scanner.nextLine());

        // Dentro do método criarUsuario
        System.out.print("Digite a data de nascimento (formato: dd/mm/aaaa): ");
        String dataInput = scanner.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataNascimento;
        try {
            dataNascimento = LocalDate.parse(dataInput, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data inválido. Use o formato dd/mm/aaaa (ex.: 20/03/1990).");
            return false;
        }

        System.out.print("Digite a renda familiar líquida: ");
        double rendaFamiliar = Double.parseDouble(scanner.nextLine());

        System.out.print("Digite a quantidade de pessoas na casa: ");
        int qtdePessoas = Integer.parseInt(scanner.nextLine());

        System.out.print("Digite o tipo de vínculo (1 - APOSENTADO, 2 - SERVIDOR, 3 - PENSIONISTA, 4 - CLT): ");
        int tipoClienteOpcao = Integer.parseInt(scanner.nextLine());
        VinculoEnum tipoCliente = switch (tipoClienteOpcao) {
            case 1 -> VinculoEnum.APOSENTADO;
            case 2 -> VinculoEnum.SERVIDOR;
            case 3 -> VinculoEnum.PENSIONISTA;
            case 4 -> VinculoEnum.EMPREGADO;
            default -> throw new IllegalArgumentException("Tipo de vínculo inválido");
        };

        // Criar o objeto Cliente com os dados coletados
        Cliente novoCliente = new Cliente();
        novoCliente.setCpfCliente(cpf);
        novoCliente.setNomecliente(nome);
        novoCliente.setSenha(BCrypt.hashpw(senha, BCrypt.gensalt())); // Criptografando a senha
        novoCliente.setRendaMensalLiquida(rendaMensal);
        novoCliente.setDataNascimento(dataNascimento);
        novoCliente.setRendaFamiliarLiquida(rendaFamiliar);
        novoCliente.setQtdePessoasNaCasa(qtdePessoas);
        novoCliente.setTipoCliente(tipoCliente);
        // O score pode ser calculado automaticamente pelo controller ou DAO, então deixei fora por enquanto

        try {
            clienteController.criarCliente(novoCliente);
            System.out.println("Usuário criado com sucesso!");
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao criar usuário: " + e.getMessage());
            return false;
        }
    }

    public void mostrarTelaTipoEmprestimo() {
        System.out.println("Escolha o tipo de empréstimo:");
        System.out.println("1. Consignado");
        System.out.println("2. Pessoal");
        System.out.println("0. Deslogar");
    }

    public void simularEmprestimoConsignado() {
        System.out.println("Simulação de Empréstimo Consignado");
        System.out.println("Valor: R$ " + params.getValorMinimoConsignado());
        System.out.println("Taxa de juros: " + params.getJurosMinimoConsignado() + "% ao mês");
        System.out.println("Parcelas: " + params.getPrazoMinimoConsignado() + "x de R$ "
                + (params.getValorMinimoConsignado() / params.getPrazoMinimoConsignado()));
    }

    public void simularEmprestimoPessoal() {
        System.out.println("Simulação de Empréstimo Pessoal");
        System.out.println("Valor: R$ " + params.getValorMinimoPessoal());
        System.out.println("Taxa de juros: " + params.getJurosMinimoPessoal() + "% ao mês");
        System.out.println("Parcelas: " + params.getPrazoMinimoPessoal() + "x de R$ "
                + (params.getValorMinimoPessoal() / params.getPrazoMinimoPessoal()));
    }

    public void fazerEmprestimoConsignado() {
        System.out.println("Fazendo Empréstimo Consignado...");
        System.out.println("Empréstimo consignado aprovado!");
    }

    public void fazerEmprestimoPessoal() {
        System.out.println("Fazendo Empréstimo Pessoal...");
        System.out.println("Empréstimo pessoal aprovado!");
    }

    public void buscarEmprestimo() {
        System.out.println("Buscando Empréstimo...");
        System.out.println("Empréstimo consignado: R$ " + params.getValorMinimoConsignado() + " - "
                + params.getPrazoMinimoConsignado() + "x de R$ "
                + (params.getValorMinimoConsignado() / params.getPrazoMinimoConsignado()));
        System.out.println("Empréstimo pessoal: R$ " + params.getValorMinimoPessoal() + " - "
                + params.getPrazoMinimoPessoal() + "x de R$ "
                + (params.getValorMinimoPessoal() / params.getPrazoMinimoPessoal()));
    }
}