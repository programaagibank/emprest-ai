package br.com.emprestai.service;

import br.com.emprestai.model.Cliente;

    public interface CalculoEmprestimo {
        double calcularMargemDisponivel(Cliente cliente);
        boolean isElegivel(Cliente cliente, double valorEmprestimo);

        boolean isElegivel(Cliente cliente, double valor, int quantidadeParcelas, String scoreCredito);
}
