package br.com.emprestai.service;

import br.com.emprestai.model.Cliente;

public class CalculoConsignado implements CalculoEmprestimo {

    public static double verificarEmprestimoConsignado(Cliente cliente, double valor, int quantidadeParcelas, int idade) {
        if (cliente == null) {
            System.out.println("Cliente não pode ser vazio!");
            return 0;
        }

        double RemuneracaoLiqMes = cliente.getRemuneracaoLiqMes();
        if (RemuneracaoLiqMes < 0) {
            System.out.println("A renda não pode ser negativa!");
            return 0;
        }

        double margem = RemuneracaoLiqMes * 0.35;

        if (valor < 1000) {
            System.out.println("Valor mínimo é R$ 1.000!");
            return 0;
        }

        if (quantidadeParcelas < 24 || quantidadeParcelas > 92) {
            System.out.println("Parcelas devem ser entre 24 e 92!");
            return 0;
        }

        int idadeFinal = idade + (quantidadeParcelas / 12);
        if (idadeFinal > 80) {
            System.out.println("Idade final não pode passar de 80 anos!");
            return 0;
        }

        String tipoVinculo = cliente.getTipoVinculo();
        if (tipoVinculo == null || (!tipoVinculo.equals("aposentado") && !tipoVinculo.equals("servidor") && !tipoVinculo.equals("pensionista"))) {
            System.out.println("Vínculo deve ser aposentado, servidor ou pensionista!");
            return 0;
        }

        double valorParcela = valor / quantidadeParcelas;
        if (valorParcela > margem) {
            System.out.println("Parcela não cabe na margem!");
            return 0;
        }

        System.out.println("Empréstimo consignado OK!");
        return margem;
    }

    public double calcularMargemDisponivel(Cliente cliente) {
        if (cliente == null || cliente.getRemuneracaoLiqMes() < 0) { // Linha 56
            System.out.println("Cliente inválido ou renda negativa!");
            return 0;
        }
        return cliente.getRemuneracaoLiqMes() * 0.35;
    }

    public boolean isElegivel(Cliente cliente, double valorEmprestimo) {
        if (valorEmprestimo < 0) {
            System.out.println("Valor não pode ser negativo!");
            return false;
        }
        double margem = calcularMargemDisponivel(cliente);
        return valorEmprestimo <= margem;
    }

    @Override
    public boolean isElegivel(Cliente cliente, double valor, int quantidadeParcelas, String scoreCredito) {
        return false;
    }
}