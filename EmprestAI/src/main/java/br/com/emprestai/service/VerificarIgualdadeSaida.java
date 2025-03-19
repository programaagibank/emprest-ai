package br.com.emprestai.service;

public class VerificarIgualdadeSaida {

    public static void main(String[] args) {
        try {
            // Verificação de  Empréstimo Pessoal
            System.out.println("Verificando Empréstimo Pessoal:");
            SimulacaoResultado resultado = SimulacaoEmprestimos.simularEmprestimoPessoal(5000.00, 12, 500, false, 2500.00, 0);

            System.out.println("Total: " + String.format("%.2f", resultado.valorTotalFinanciado));
            System.out.println("Parcela: " + String.format("%.2f", resultado.parcelaMensal));

            if (resultado.valorTotalFinanciado >= 5166.60 - 0.01 && resultado.valorTotalFinanciado <= 5166.60 + 0.01 &&
                    resultado.parcelaMensal >= 748.68 - 0.01 && resultado.parcelaMensal <= 748.68 + 0.01) {
                System.out.println("Está certo!");
            } else {
                System.out.println("Está errado!");
            }
            System.out.println();

            // Verificação do Empréstimo Consignado
            System.out.println("Verificando o  Empréstimo Consignado:");
            resultado = SimulacaoEmprestimos.simularEmprestimoConsignado(10000.00, 36, 3000.00);

            System.out.println("Total: " + String.format("%.2f", resultado.valorTotalFinanciado));
            System.out.println("Parcela: " + String.format("%.2f", resultado.parcelaMensal));

            if (resultado.valorTotalFinanciado >= 10337.30 - 0.01 && resultado.valorTotalFinanciado <= 10337.30 + 0.01 &&
                    resultado.parcelaMensal >= 396.50 - 0.01 && resultado.parcelaMensal <= 396.50 + 0.01) {
                System.out.println("Tá certo!");
            } else {
                System.out.println("Tá errado!");
            }
            System.out.println();

            // Verificando o Empréstimo Pessoal com Seguro
            System.out.println("Verificando o Empréstimo Pessoal com Seguro:");
            resultado = SimulacaoEmprestimos.simularEmprestimoPessoal(15000.00, 24, 700, true, 6000.00, 30);

            System.out.println("Total: " + String.format("%.2f", resultado.valorTotalFinanciado));
            System.out.println("Parcela: " + String.format("%.2f", resultado.parcelaMensal));

            if (resultado.valorTotalFinanciado >= 15625.95 - 0.01 && resultado.valorTotalFinanciado <= 15625.95 + 0.01 &&
                    resultado.parcelaMensal >= 1640.71 - 0.01 && resultado.parcelaMensal <= 1640.71 + 0.01) {
                System.out.println("Está certo!");
            } else {
                System.out.println("Está errado!");
            }
            System.out.println();

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}