package br.com.emprestai.service.calculos;

import br.com.emprestai.model.Emprestimo;
import java.math.BigDecimal;
import java.time.LocalDate;

import static java.math.BigDecimal.ONE;
import static java.math.MathContext.DECIMAL128;
import static java.time.temporal.ChronoUnit.DAYS;

import static br.com.emprestai.util.DateUtils.primeiroDiaUtil;

public class CalculadoraContrato {

    public static Emprestimo contratoPrice(Emprestimo emprestimo, int idade) {
        BigDecimal valorEmprestimo = new BigDecimal(String.valueOf(emprestimo.getValorEmprestimo()));
        int qtdeParcelas = emprestimo.getQuantidadeParcelas();
        double taxaJurosMensal = emprestimo.getTaxaJuros();

        LocalDate dataContratacao = emprestimo.getDataContratacao();
        LocalDate dataLiberacaoCred = primeiroDiaUtil(dataContratacao);
        emprestimo.setDataLiberacaoCred(dataLiberacaoCred);
        LocalDate dataInicioPagamento = emprestimo.getDataInicio();
        LocalDate dataFimContrato = dataInicioPagamento.plusMonths(qtdeParcelas);

        BigDecimal seguro = emprestimo.getContratarSeguro()
                ? CalculadoraCustosAdicionais.calcSeguro(valorEmprestimo, idade, qtdeParcelas)
                : BigDecimal.ZERO;
        BigDecimal valorTotalComSeguro = valorEmprestimo.add(seguro);
        BigDecimal iof = CalculadoraCustosAdicionais.calcIOF(valorTotalComSeguro, dataLiberacaoCred, dataFimContrato);
        BigDecimal valorTotalSemCarencia = valorEmprestimo.add(iof).add(seguro);

        BigDecimal resultadoCarencia = CalculadoraCustosAdicionais.calcularCarencia(valorTotalSemCarencia, taxaJurosMensal, emprestimo.getCarencia());
        BigDecimal valorTotalFinanciado = valorTotalSemCarencia.add(resultadoCarencia);

        emprestimo.setOutrosCustos(resultadoCarencia.doubleValue());
        emprestimo.setValorSeguro(seguro.doubleValue());
        emprestimo.setValorIOF(iof.doubleValue());
        emprestimo.setValorTotal(valorTotalFinanciado.doubleValue());

        BigDecimal parcelaMensal = CalculadoraParcela.calcularParcelaPrice(valorTotalFinanciado, taxaJurosMensal, qtdeParcelas);
        emprestimo.setValorParcela(parcelaMensal.doubleValue());

        BigDecimal taxaEfetivaMensal = CalculoTaxaJuros.calcTxEfetivaMes(valorEmprestimo, parcelaMensal, taxaJurosMensal, qtdeParcelas);
        emprestimo.setTaxaEfetivaMensal(taxaEfetivaMensal.doubleValue());

        return emprestimo;
    }

    // metodo que calcula o valor total financiado a partir da parcela
    public static double calcularValorTotalFinanciado(double valorParcela, double taxaJurosMensal, int qtdeParcelas) {
        BigDecimal parcelaBD = BigDecimal.valueOf(valorParcela);
        if (parcelaBD.doubleValue() <= 0 || qtdeParcelas <= 1 || taxaJurosMensal <= 0) {
            throw new IllegalArgumentException("Valores inválidos");
        }

        BigDecimal i = BigDecimal.valueOf(taxaJurosMensal / 100); // Taxa em decimal
        BigDecimal umMaisTaxa = ONE.add(i);
        BigDecimal denominador = ONE.subtract(umMaisTaxa.pow(-qtdeParcelas, DECIMAL128));
        BigDecimal valorTotalFinanciado = parcelaBD.multiply(denominador).divide(i, DECIMAL128);

        return valorTotalFinanciado.doubleValue();
    }

    public static void main(String[] args) {
        // Criando um objeto Emprestimo com valores de teste
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setValorEmprestimo(10000.0); // Valor do empréstimo: R$ 10.000,00
        emprestimo.setQuantidadeParcelas(12);   // 12 parcelas
        emprestimo.setTaxaJuros(1.5);          // Taxa de juros mensal: 1,5%
        emprestimo.setDataContratacao(LocalDate.now()); // Data de contratação: hoje
        emprestimo.setDataInicio(LocalDate.now().plusMonths(1)); // Início do pagamento: daqui a 1 mês
        emprestimo.setContratarSeguro(true);   // Contratar seguro

        // Idade do cliente
        int idade = 30;

        // Executando o cálculo do contrato
        CalculadoraContrato.contratoPrice(emprestimo, idade);

        // Exibindo os resultados do contrato
        System.out.println("=== Resultados do Contrato ===");
        System.out.printf("Valor do Empréstimo: R$ %.2f%n", emprestimo.getValorEmprestimo());
        System.out.printf("Quantidade de Parcelas: %d%n", emprestimo.getQuantidadeParcelas());
        System.out.printf("Taxa de Juros Mensal: %.2f%%%n", emprestimo.getTaxaJuros());
        System.out.printf("Valor do Seguro: R$ %.2f%n", emprestimo.getValorSeguro());
        System.out.printf("Valor do IOF: R$ %.2f%n", emprestimo.getValorIOF());
        System.out.printf("Custo da Carência: R$ %.2f%n", emprestimo.getOutrosCustos());
        System.out.printf("Valor Total Financiado: R$ %.2f%n", emprestimo.getValorTotal());
        System.out.printf("Valor da Parcela Mensal: R$ %.2f%n", emprestimo.getValorParcela());
        System.out.printf("Taxa Efetiva Mensal: %.4f%%%n", emprestimo.getTaxaEfetivaMensal());

        // Teste adicional: verificar consistência com calcularValorTotalFinanciado
        double valorTotalCalculado = CalculadoraContrato.calcularValorTotalFinanciado(
                emprestimo.getValorParcela(), emprestimo.getTaxaJuros(), emprestimo.getQuantidadeParcelas());
        System.out.printf("Valor Total Financiado (recalculado pela parcela): R$ %.2f%n", valorTotalCalculado);

        // Teste de reversão: reverter o valor total financiado para o valor original
        double valorRevertido = CalculadoraCustosAdicionais.reverterValorEmprestimo(
                emprestimo.getValorTotal(), // Valor total financiado
                idade,                      // Idade
                emprestimo.getQuantidadeParcelas(), // Quantidade de parcelas
                emprestimo.getTaxaJuros(),  // Taxa de juros mensal
                emprestimo.getCarencia(),   // Dias de carência
                emprestimo.getContratarSeguro() // Contratar seguro
        );
        System.out.printf("Valor Revertido (valor original do empréstimo): R$ %.2f%n", valorRevertido);

        // Verificação de consistência
        double diferenca = Math.abs(emprestimo.getValorEmprestimo() - valorRevertido);
        System.out.printf("Diferença entre valor original e revertido: R$ %.2f%n", diferenca);
        if (diferenca < 0.01) {
            System.out.println("Reversão consistente (diferença insignificante)!");
        } else {
            System.out.println("Atenção: Reversão inconsistente!");
        }
    }
}