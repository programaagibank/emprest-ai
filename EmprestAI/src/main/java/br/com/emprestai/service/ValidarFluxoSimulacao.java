package br.com.emprestai.service;

public class ValidarFluxoSimulacao {

    public static void main(String[] args) {
        System.out.println(" 1 - Testando Empréstimo Pessoal");
        try {
            SimulacaoResultado resultado = SimulacaoEmprestimos.simularEmprestimoPessoal(5000.00, 12, 500, false, 2500.00, 0);
            System.out.println("Total: " + String.format("%.2f", resultado.valorTotalFinanciado));
            System.out.println("Parcela: " + String.format("%.2f", resultado.parcelaMensal));
            System.out.println("Cálculo concluído com sucesso!");
        } catch (Exception e) {
            System.out.println("Ocorreu um problema: " + e.getMessage());
        }
        System.out.println();

        System.out.println(" 2-Testando Empréstimo Consignado:");
        try {
            SimulacaoResultado resultado = SimulacaoEmprestimos.simularEmprestimoConsignado(10000.00, 36, 3000.00);
            System.out.println("Total: " + String.format("%.2f", resultado.valorTotalFinanciado));
            System.out.println("Parcela: " + String.format("%.2f", resultado.parcelaMensal));
            System.out.println("Cálculo concluído com sucesso!");
        } catch (Exception e) {
            System.out.println("Ocorreu um problema: " + e.getMessage());
        }
        System.out.println();

        System.out.println(" 3 -Testando Empréstimo Pessoal com Seguro:");
        try {
            SimulacaoResultado resultado = SimulacaoEmprestimos.simularEmprestimoPessoal(15000.00, 24, 700, true, 6000.00, 30);
            System.out.println("Total: " + String.format("%.2f", resultado.valorTotalFinanciado));
            System.out.println("Parcela: " + String.format("%.2f", resultado.parcelaMensal));
            System.out.println("Cálculo concluído com sucesso!");
        } catch (Exception e) {
            System.out.println("Ocorreu um problema: " + e.getMessage());
        }
        System.out.println();

        System.out.println(" 4 - Testando Empréstimo com renda baixa:");
        try {
            SimulacaoResultado resultado = SimulacaoEmprestimos.simularEmprestimoPessoal(5000.00, 12, 500, false, 100.00, 0);
            System.out.println("Total: " + String.format("%.2f", resultado.valorTotalFinanciado));
            System.out.println("Parcela: " + String.format("%.2f", resultado.parcelaMensal));
            System.out.println("Cálculo concluído? Não, algo está errado!");
        } catch (Exception e) {
            System.out.println("Ocorreu um problema: " + e.getMessage());
            System.out.println("Erro detectado como esperado!");
        }
        System.out.println();
    }
}