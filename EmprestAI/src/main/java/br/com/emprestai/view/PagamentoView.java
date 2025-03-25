package br.com.emprestai.view;

import java.util.Scanner;

public class PagamentoView {
    private ClienteView clienteView;
    private EmprestimoView emprestimoView;
    private PagamentoView pagamentoView; // Nota: Parece haver uma referência circular desnecessária aqui
    private ConsultaView consultaView;

    private static final Scanner scanner = new Scanner(System.in);

    // Menu de Pagamento
    public void mostrarMenuPagar() {
        System.out.println("\n\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.println("\033[1;33m=== GERENCIAR PAGAMENTOS ===\033[0m");
        System.out.println("1. Pagar Parcelas");
        System.out.println("0. Voltar ao menu anterior");
        System.out.println("\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.print("\033[1;32m➜ Escolha uma opção: \033[0m");
    }

    // Tela de Pagamento de Parcelas
    public void exibePagarParcelas() {
        System.out.println("\n\033[1;34m" + "=".repeat(50) + "\033[0m");
        System.out.println("\033[1;33m=== PAGAMENTO DE PARCELAS ===\033[0m");

        System.out.print("\033[1;32m➜ Deseja pagar a parcela a vencer? (S/N): \033[0m");
        String confirma = scanner.nextLine();
        if (confirma.equalsIgnoreCase("S")) {
            System.out.println("\033[1;32m✓ Parcela a vencer selecionada para pagamento.\033[0m");
        } else if (confirma.equalsIgnoreCase("N")) {
            System.out.println("\033[1;33mParcela a vencer não será paga agora.\033[0m");
        } else {
            System.out.println("\033[1;31m✗ Opção inválida! Use S ou N.\033[0m");
            return;
        }

        System.out.print("\033[1;32m➜ Deseja adiantar quantas parcelas? (0 para nenhuma): \033[0m");
        try {
            int parcelasAdiantadas = scanner.nextInt();
            scanner.nextLine(); // Consumir a linha restante
            if (parcelasAdiantadas < 0) {
                System.out.println("\033[1;31m✗ Número inválido! Use um valor positivo ou zero.\033[0m");
                return;
            }
            if (parcelasAdiantadas > 0) {
                System.out.println("\033[1;32m✓ " + parcelasAdiantadas + " parcela(s) serão adiantada(s).\033[0m");
            } else {
                System.out.println("\033[1;33mNenhuma parcela será adiantada.\033[0m");
            }
            // TODO: Chamar o método de pagamento de parcelas e retornar a confirmação
            System.out.println("\033[1;32m✓ Pagamento processado com sucesso! (Simulação)\033[0m");
        } catch (Exception e) {
            System.out.println("\033[1;31m✗ Entrada inválida! Digite um número.\033[0m");
            scanner.nextLine(); // Limpar buffer
        }

        System.out.println("\033[1;34m" + "=".repeat(50) + "\033[0m");
    }
}
