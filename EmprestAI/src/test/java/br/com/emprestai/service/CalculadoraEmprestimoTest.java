package br.com.emprestai.service;

import br.com.emprestai.enums.StatusEmpParcela;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
import br.com.emprestai.service.calculos.CalculadoraEmprestimo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.*;

/*
Utilizado para validação dos valores esperado no teste o site do Investnews e calculadora financeira online
https://investnews.com.br/ferramentas/calculadoras/calculadora-price/
https://www.calculadoraonline.com.br/financeira
https://www.dinkytown.net/
 */
class CalculadoraEmprestimoTest {

    @Test
    void contratoPrice() { // Ok
        // Configuração do objeto Emprestimo
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setValorEmprestimo(5000.00); // Agora double
        emprestimo.setQuantidadeParcelas(24);
        emprestimo.setJuros(9.49);
        emprestimo.setDataContratacao(LocalDate.of(2025, 3, 7)); // Sexta-feira
        emprestimo.setDataInicio(LocalDate.of(2025, 4, 7)); // Início do pagamento (1 mês após contratação)
        emprestimo.setContratarSeguro(true);
        emprestimo.setTaxaMulta(0.02); // 2% (exemplo)
        emprestimo.setTaxaJurosMora(0.033); // 1% ao mês / 30 dias = 0.033% ao dia (exemplo)

        LocalDate dtNasc = LocalDate.of(1995, 1, 1);

        // Executa o método
        Emprestimo resultado = CalculadoraEmprestimo.contratoPrice(emprestimo, dtNasc);

        // Validações
        assertEquals(5000.00, resultado.getValorEmprestimo(), 0.01); // Valor inicial do empréstimo como double
        assertEquals(24, resultado.getQuantidadeParcelas());
        assertEquals(LocalDate.of(2025, 3, 7), resultado.getDataContratacao());
        assertEquals(LocalDate.of(2025, 3, 10), resultado.getDataLiberacaoCred()); // Primeiro dia útil após 07/03/2025
        assertEquals(LocalDate.of(2027, 4, 7), resultado.getDataInicio().plusMonths(24)); // Fim do contrato
        assertEquals(40.00, resultado.getValorSeguro(), 0.01); // Seguro aproximado como double
        assertEquals(170.00, resultado.getValorIOF(), 0.01); // IOF como double
        assertEquals(5343.24, resultado.getValorTotal(), 0.01); // Valor total financiado como double
        assertEquals(571.99, resultado.getValorParcela(), 0.01); // Parcela mensal como double
        assertEquals(10.37, resultado.getTaxaEfetivaMensal(), 0.01); // Taxa efetiva como double
    }

    @Test
    void calcParcela() { //Ok
        BigDecimal valorTotalFinanciado = new BigDecimal("5209.32");
        double taxaJurosMensal = 9.49;
        int qtdeParcelas = 24;
        BigDecimal resultado = CalculadoraEmprestimo.calcParcela(valorTotalFinanciado, taxaJurosMensal, qtdeParcelas);
        assertEquals(557.66, resultado.setScale(2, HALF_UP).doubleValue(), 0.01); // Convertido para double
    }

    @Test
    void calcSeguro() { //Ok
        BigDecimal valorEmprestimo = new BigDecimal("5000.00");
        LocalDate dtNasc = LocalDate.of(1995, 1, 1);
        LocalDate dataContratacao = LocalDate.of(2025, 3, 7);
        int qtdeParcelas = 24;
        BigDecimal resultado = CalculadoraEmprestimo.calcSeguro(valorEmprestimo, dtNasc, dataContratacao, qtdeParcelas);
        assertEquals(40.00, resultado.setScale(2, HALF_UP).doubleValue(), 0.01); // Convertido para double
    }

    @Test
    void calcIOF() { //OK
        BigDecimal valorEmprestimoComSeguro = new BigDecimal("5040.00"); // 5000 + 40 (seguro)
        LocalDate dataLiberacaoCred = LocalDate.of(2025, 3, 10); // Segunda-feira
        LocalDate dataFimContrato = LocalDate.of(2027, 3, 10); // Segunda-feira (24 meses após liberação)
        BigDecimal resultado = CalculadoraEmprestimo.calcIOF(valorEmprestimoComSeguro, dataLiberacaoCred, dataFimContrato);
        assertEquals(170.00, resultado.setScale(2, HALF_UP).doubleValue(), 0.01); // Convertido para double
    }

    @Test
    void calcTxEfetivaMes() { //ok
        BigDecimal valorEmprestimo = new BigDecimal("5000");
        BigDecimal parcelaMensal = new BigDecimal("557.73");
        double taxaJurosMensal = 9.49;
        int qtdeParcelas = 24;
        BigDecimal resultado = CalculadoraEmprestimo.calcTxEfetivaMes(valorEmprestimo, parcelaMensal, taxaJurosMensal, qtdeParcelas);
        assertEquals(10.03, resultado.doubleValue(), 0.01); // Convertido para double
    }

        @Test
        void testProcessarValoresParcela() {

            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setValorTotal(1000.0);
            emprestimo.setJuros(10.0);
            emprestimo.setValorParcela(110.0);
            emprestimo.setQuantidadeParcelas(10);
            emprestimo.setTaxaMulta(2.0);
            emprestimo.setTaxaJurosMora(0.1);

            // Criando lista de parcelas
            List<Parcela> parcelas = new ArrayList<>();
            LocalDate hoje = LocalDate.now();

            // Parcelas de exemplo  - Casos (Atrasado, parcelas futuras)
            Parcela parcela1 = new Parcela();
            parcela1.setDataVencimento(hoje.minusDays(5));  // Atrasada
            parcelas.add(parcela1);

            Parcela parcela2 = new Parcela();
            parcela2.setDataVencimento(hoje.plusDays(5));   // Futura
            parcelas.add(parcela2);

            Parcela parcela3 = new Parcela();
            parcela3.setDataVencimento(hoje.plusDays(10));  // Futura
            parcelas.add(parcela3);

            emprestimo.setParcelaList(parcelas);

            // Executar o método
            Emprestimo resultado = CalculadoraEmprestimo.processarValoresParcela(emprestimo);

            // Verificações
            // 1. Verificar cálculo básico de juros e amortização da primeira parcela
            assertEquals(100.0, parcelas.get(0).getJuros(), 0.01);
            assertEquals(10.0, parcelas.get(0).getAmortizacao(), 0.01);

            // 2. Verificar parcela atrasada
            assertEquals(StatusEmpParcela.ATRASADA, parcelas.get(0).getIdStatus());
            assertEquals(110.0, parcelas.get(0).getValorPresenteParcela(), 0.01);
            assertEquals(2.2, parcelas.get(0).getMulta(), 0.01);

            // 3. Verificar cálculo de juros mora (5 dias atrasados)
            assertEquals(0.55, parcelas.get(0).getJurosMora(), 0.01);

            // 4. Verificar que parcelas futuras têm valor presente descontado
            assertTrue(parcelas.get(1).getValorPresenteParcela() < 110.0);
            assertNull(parcelas.get(1).getIdStatus());

            // 5. Verificar saldo devedor atualizado
            assertTrue(resultado.getSaldoDevedorAtualizado() > 0.0);

            // 6. Verificar exceção para valores inválidos
            Emprestimo emprestimoInvalido = new Emprestimo();
            emprestimoInvalido.setValorParcela(0.0);
            emprestimoInvalido.setJuros(-1.0);
            emprestimoInvalido.setQuantidadeParcelas(0);
            assertThrows(IllegalArgumentException.class, () -> {
                CalculadoraEmprestimo.processarValoresParcela(emprestimoInvalido);
            });
        }
    }



