package br.com.emprestai.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimulacaoEmprestimosTest {

    // Teste número 1 : Empréstimo Pessoal
    @Test
    void simularEmprestimoPessoal() {

        SimulacaoResultado resultadoSimples = SimulacaoEmprestimos.simularEmprestimoPessoal(5000.00, 12, 500, false, 2500.00, 0);
        assertEquals(5166.60, resultadoSimples.valorTotalFinanciado, 0.01);
        assertEquals(748.68, resultadoSimples.parcelaMensal, 0.01);
        assertEquals(166.60, resultadoSimples.iof, 0.01);
        assertEquals(0.00, resultadoSimples.custoSeguro, 0.01);

        // Teste número 2: Empréstimo Pessoal com Seguro
        SimulacaoResultado resultadoComSeguro = SimulacaoEmprestimos.simularEmprestimoPessoal(15000.00, 24, 700, true, 6000.00, 30);
        assertEquals(15625.95, resultadoComSeguro.valorTotalFinanciado, 0.01);
        assertEquals(1640.71, resultadoComSeguro.parcelaMensal, 0.01);
        assertEquals(505.95, resultadoComSeguro.iof, 0.01);
        assertEquals(120.00, resultadoComSeguro.custoSeguro, 0.01);
    }

      //Teste número 3: Empréstimo Consignado
    @Test
    void simularEmprestimoConsignado() {
        SimulacaoResultado resultado = SimulacaoEmprestimos.simularEmprestimoConsignado(10000.00, 36, 3000.00);
        assertEquals(10337.30, resultado.valorTotalFinanciado, 0.01);
        assertEquals(396.50, resultado.parcelaMensal, 0.01);
        assertEquals(337.30, resultado.iof, 0.01);
        assertEquals(0.00, resultado.custoSeguro, 0.01);
    }
}