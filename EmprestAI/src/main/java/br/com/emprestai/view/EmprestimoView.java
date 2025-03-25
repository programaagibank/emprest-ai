package br.com.emprestai.view;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.controller.EmprestimoController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.enums.StatusEmpEnum;
import br.com.emprestai.enums.TipoEmpEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.service.LoginService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import static br.com.emprestai.enums.MotivosEncerramentosEmpEnum.ABERTO;
import static br.com.emprestai.enums.StatusEmpEnum.APROVADO;
import static br.com.emprestai.enums.TipoEmpEnum.CONSIGNADO;

public class EmprestimoView {

    private final EmprestimoController emprestimoController;
    private static final Scanner scanner = new Scanner(System.in);

    public EmprestimoView(EmprestimoController emprestimoController) {
        this.emprestimoController = emprestimoController;
    }

    // Tela de Tipo de Empréstimo
    public void mostrarTelaTipoEmprestimo() {
        System.out.println("\n\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.println("\033[1;33m=== ESCOLHA O TIPO DE EMPRÉSTIMO ===\033[0m");
        System.out.println("1. Consignado");
        System.out.println("2. Pessoal");
        System.out.println("0. Deslogar");
        System.out.println("\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.print("\033[1;32m➜ Selecione uma opção: \033[0m");
    }

    // Menu Consignado
    public void mostrarMenuConsignado() {
        System.out.println("\n\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.println("\033[1;36mBem-vindo ao Gerenciador de Empréstimos Consignados\033[0m");
        System.out.println("\033[1;33m=== OPÇÕES DISPONÍVEIS ===\033[0m");
        System.out.println("1. Simular Empréstimo Consignado");
        System.out.println("2. Buscar Seu Empréstimo Consignado");
        System.out.println("0. Voltar ao menu anterior");
        System.out.println("\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.print("\033[1;32m➜ Escolha uma opção: \033[0m");
    }

    // Menu Pessoal
    public void mostrarMenuPessoal() {
        System.out.println("\n\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.println("\033[1;36mBem-vindo ao Gerenciador de Empréstimos Pessoais\033[0m");
        System.out.println("\033[1;33m=== OPÇÕES DISPONÍVEIS ===\033[0m");
        System.out.println("1. Simular Empréstimo Pessoal");
        System.out.println("2. Buscar Seu Empréstimo Pessoal");
        System.out.println("0. Voltar ao menu anterior");
        System.out.println("\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.print("\033[1;32m➜ Escolha uma opção: \033[0m");
    }

    // Simulação de Empréstimo
    public Emprestimo simularEmprestimo(Cliente cliente, TipoEmpEnum tipoEmp) {
        Scanner scanner = new Scanner(System.in);
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setTipoEmprestimo(tipoEmp);

        System.out.println("\n\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.println("\033[1;33m=== SIMULAÇÃO DE EMPRÉSTIMO ===\033[0m");

        // Entrada de Dados
        System.out.print("\033[1;32m➜ Digite o valor do empréstimo (ex.: 10000.0): \033[0m");
        emprestimo.setValorEmprestimo(scanner.nextDouble());

        System.out.print("\033[1;32m➜ Digite a quantidade de parcelas (ex.: 12): \033[0m");
        emprestimo.setQuantidadeParcelas(scanner.nextInt());
        scanner.nextLine();

        System.out.print("\033[1;32m➜ Contratar seguro? (S/N): \033[0m");
        String seguro = scanner.nextLine();
        if (seguro.equalsIgnoreCase("S")) {
            emprestimo.setContratarSeguro(true);
            System.out.println("\033[1;32m✓ Seguro contratado!\033[0m");
        } else if (seguro.equalsIgnoreCase("N")) {
            emprestimo.setContratarSeguro(false);
            System.out.println("\033[1;33mSem seguro.\033[0m");
        } else {
            System.out.println("\033[1;31m✗ Opção inválida! Use S ou N.\033[0m");
            throw new IllegalArgumentException("Opção inválida para seguro!");
        }

        emprestimo.setDataContratacao(LocalDate.now());

        System.out.print("\033[1;32m➜ Carência: \033[0m");

        if(emprestimo.getTipoEmprestimo() == CONSIGNADO){
            System.out.print("\033[1;32m➜ Digite a data do recebimento do seu próximo salario / vencimento (dd/MM/yyyy): \033[0m");
            String dataInput = scanner.nextLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataSalario;
            try {
                dataSalario = LocalDate.parse(dataInput, formatter);
                if(dataSalario.isBefore(LocalDate.now())){
                    System.out.println("\033[1;31m✗ Data deve ser futura!.\033[0m");
                }
            } catch (DateTimeParseException e) {
                System.out.println("\033[1;31m✗ Formato de data inválido! Use dd/MM/yyyy.\033[0m");
                System.out.println("\033[1;34m" + "=".repeat(50) + "\033[0m");
                return null;
            }
            emprestimo.setDataInicio(dataSalario);
        } else {
            menuCarencia();
            int diasCarencia = scanner.nextInt();
            switch (diasCarencia){
                case 1 -> emprestimo.setDataInicio(LocalDate.now().plusDays(15));
                case 2 -> emprestimo.setDataInicio(LocalDate.now().plusDays(30));
            }
        }

        // Cálculo da Simulação
        emprestimoController.obterEmprestimo(cliente, emprestimo);

        // Formatação da Data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Exibição dos Resultados
        System.out.println("\n\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.println("\033[1;32m✓ RESULTADO DA SIMULAÇÃO\033[0m");
        System.out.println("\033[1;34m" + "-".repeat(50) + "\033[0m");
        System.out.println("Tipo: " + emprestimo.getTipoEmprestimo());
        System.out.println("Renda Mensal Líquida: R$ " + String.format("%.2f", cliente.getRendaMensalLiquida()));
        System.out.println("Idade: " + (LocalDate.now().getYear() - cliente.getDataNascimento().getYear()));
        if (tipoEmp == TipoEmpEnum.PESSOAL) {
            System.out.println("Score: " + cliente.getScore());
        } else {
            System.out.println("Tipo de Cliente: " + cliente.getTipoCliente());
        }
        System.out.println("Valor do Empréstimo: R$ " + String.format("%.2f", emprestimo.getValorEmprestimo()));
        System.out.println("Valor Total: R$ " + String.format("%.2f", emprestimo.getValorTotal()));
        System.out.println("Quantidade de Parcelas: " + emprestimo.getQuantidadeParcelas());
        System.out.println("Taxa de Juros Mensal: " + String.format("%.2f", emprestimo.getJuros()) + "%");
        System.out.println("Valor por Parcela: R$ " + String.format("%.2f", emprestimo.getValorParcela()));
        System.out.println("Data de Contratação: " + emprestimo.getDataContratacao().format(formatter));
        System.out.println("Data de Liberação do Crédito: " +
                (emprestimo.getDataLiberacaoCred() != null ? emprestimo.getDataLiberacaoCred().format(formatter) : "Não definida"));
        System.out.println("Data de Início de Pagamento: " + emprestimo.getDataInicio().format(formatter));
        System.out.println("\033[1;34m" + "=".repeat(50) + "\033[0m");

        emprestimo.setCliente(cliente);
        return emprestimo;
    }

    public void menuCarencia(){
        System.out.println("\n\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.println("\033[1;33m=== CARẼNCIA ===\033[0m");
        System.out.println("1. 15");
        System.out.println("2. 30");
        System.out.println("\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.print("\033[1;32m➜ Escolha uma opção: \033[0m");
    }

    // Menu de Contratação
    public void mostrarMenuContratar() {
        System.out.println("\n\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.println("\033[1;33m=== CONFIRMAR CONTRATAÇÃO ===\033[0m");
        System.out.println("1. Contratar");
        System.out.println("0. Voltar ao menu anterior");
        System.out.println("\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.print("\033[1;32m➜ Escolha uma opção: \033[0m");
    }

    // Confirmação de Contrato
    public void mostrarConfirmarContrato(Emprestimo emprestimo) {
        System.out.println("\n\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.print("\033[1;32m➜ Digite sua senha para confirmar: \033[0m");
        String senha = scanner.nextLine();

        if (LoginService.authenticateUser(senha, emprestimo.getCliente().getSenha())) {
            try {
                emprestimo.setStatusEmprestimo(APROVADO);
                emprestimo.setMotivoEncerramento(ABERTO);
                emprestimoController.salvarEmprestimo(emprestimo);
                System.out.println("\033[1;32m✓ Contratado com sucesso!\033[0m");
            } catch (Exception e) {
                System.out.println("\033[1;31m✗ Erro ao contratar: " + e.getMessage() + "\033[0m");
            }
        } else {
            System.out.println("\033[1;31m✗ CPF ou senha incorretos!\033[0m");
        }
        System.out.println("\033[1;34m" + "=".repeat(50) + "\033[0m");
    }
}