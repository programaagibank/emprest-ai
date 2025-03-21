package br.com.emprestai.view;

import br.com.emprestai.service.LoginService;
import br.com.emprestai.App;
import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.controller.EmprestimoController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.enums.TipoEmpEnum;
import br.com.emprestai.enums.VinculoEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
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

    public Cliente exibirLogin() {
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
            return cliente;
        } else {
            System.out.println("CPF ou senha incorretos.");
            return null;
        }
    }

    public Cliente exibirCriarUsuario() {
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
            return null;
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
        // O score pode ser calculado automaticamente pelo controller ou DAO, então
        // deixei fora por enquanto

        try {
            Cliente cliente = clienteController.criarCliente(novoCliente);
            System.out.println("Usuário criado com sucesso!");
            return cliente;
        } catch (Exception e) {
            System.out.println("Erro ao criar usuário: " + e.getMessage());
            return null;
        }
    }

    public void mostrarTelaTipoEmprestimo() {
        System.out.println("Escolha o tipo de empréstimo:");
        System.out.println("1. Consignado");
        System.out.println("2. Pessoal");
        System.out.println("0. Deslogar");
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

    public void simularEmprestimo(Cliente cliente, TipoEmpEnum tipoEmp) {
        Scanner scanner = new Scanner(System.in);

        // === Dados do Empréstimo ===
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setTipoEmprestimo(tipoEmp);

        System.out.print("Digite o valor do empréstimo (ex.: 10000.0): ");
        emprestimo.setValorEmprestimo(scanner.nextDouble());

        System.out.print("Digite a quantidade de parcelas (ex.: 12): ");
        emprestimo.setQuantidadeParcelas(scanner.nextInt());
        scanner.nextLine(); // Consumir o \n após nextInt

        System.out.print("Contratar seguro? Digite S ou N: ");
        String seguro = scanner.nextLine();
        if (seguro.equalsIgnoreCase("S")) {
            emprestimo.setContratarSeguro(true);
        } else if (seguro.equalsIgnoreCase("N")) {
            emprestimo.setContratarSeguro(false);
        } else {
            throw new IllegalArgumentException("Opção inválida!");
        }

        System.out.print("Digite os dias de carência até o início (ex.: 30): ");
        int diasCarencia = scanner.nextInt();
        emprestimo.setDataContratacao(LocalDate.now());
        emprestimo.setDataInicio(LocalDate.now().plusDays(diasCarencia));

        // === Cálculo da Simulação ===
        EmprestimoController emprestimoController = new EmprestimoController(new ClienteDAO(), new EmprestimoDAO());
        emprestimoController.obterEmprestimo(cliente, emprestimo);
        // === Exibição dos Resultados ===
        System.out.println("\nResultado da simulação:");
        System.out.println("Tipo: " + emprestimo.getTipoEmprestimo());
        System.out.println("Renda Mensal Líquida: R$ " + cliente.getRendaMensalLiquida());
        System.out.println("Idade: " + (LocalDate.now().getYear() - cliente.getDataNascimento().getYear()));
        if (tipoEmp == TipoEmpEnum.PESSOAL) {
            System.out.println("Score: " + cliente.getScore());
        } else {
            System.out.println("Tipo de Cliente: " + cliente.getTipoCliente());
        }
        System.out.println("Valor do Empréstimo: R$ " + emprestimo.getValorEmprestimo());
        System.out.println("Valor do Total: R$ " + emprestimo.getValorTotal());
        System.out.println("Quantidade de Parcelas: " + emprestimo.getQuantidadeParcelas());
        System.out.println("Taxa de Juros Mensal: " + emprestimo.getJuros() + "%");
        System.out.println("Valor por Parcela: R$ " + emprestimo.getValorParcela());
        System.out.println("Data de Contratação: " + emprestimo.getDataContratacao());
        System.out.println("Data de Início: " + emprestimo.getDataInicio());
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