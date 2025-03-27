package br.com.emprestai.service.calculos;

import br.com.emprestai.enums.StatusEmpParcela;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
import br.com.emprestai.util.EmprestimoParams;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.emprestai.util.DateUtils.primeiroDiaUtil;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_UP;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;

public class CalculadoraEmprestimo {
    private static final EmprestimoParams params = EmprestimoParams.getInstance();
    private static final BigDecimal DOZE = new BigDecimal("12");

    /**
     * Calcula os valores do contrato de empréstimo usando o método Price.
     */
    public static Emprestimo contratoPrice(Emprestimo emprestimo, LocalDate dtNasc) {
        Map<String, Object> dadosContrato = new HashMap<>();

        // Valores para cálculo
        BigDecimal valorEmprestimo = new BigDecimal(String.valueOf(emprestimo.getValorEmprestimo()));
        int qtdeParcelas = emprestimo.getQuantidadeParcelas();
        double taxaJurosMensal = emprestimo.getJuros();

        // Datas
        LocalDate dataContratacao = emprestimo.getDataContratacao();
        LocalDate dataLiberacaoCred = primeiroDiaUtil(dataContratacao);
        emprestimo.setDataLiberacaoCred(dataLiberacaoCred);
        LocalDate dataInicioPagamento = emprestimo.getDataInicio();
        LocalDate dataFimContrato = emprestimo.getDataInicio().plusMonths(qtdeParcelas);

        // Outros valores
        BigDecimal seguro = (emprestimo.getContratarSeguro() ? calcSeguro(valorEmprestimo, dtNasc, dataContratacao, qtdeParcelas) : ZERO);
        BigDecimal valorTotalComSeguro = (valorEmprestimo.add(seguro));
        BigDecimal iof = calcIOF(valorTotalComSeguro, dataLiberacaoCred, dataFimContrato);
        BigDecimal valorTotalSemCarencia = (valorEmprestimo.add(iof).add(seguro));

        int calcularDiasCarencia = (int) DAYS.between(dataContratacao, dataInicioPagamento);
        BigDecimal resultadoCarencia = calcularCarencia(valorTotalSemCarencia, taxaJurosMensal, calcularDiasCarencia);
        BigDecimal valorTotalFinanciado = valorTotalSemCarencia.add(resultadoCarencia);

        emprestimo.setValorSeguro(seguro.doubleValue());
        emprestimo.setValorIOF(iof.doubleValue());
        emprestimo.setValorTotal(valorTotalFinanciado.doubleValue());

        BigDecimal parcelaMensal = calcParcela(valorTotalFinanciado, taxaJurosMensal, qtdeParcelas);
        emprestimo.setValorParcela(parcelaMensal.doubleValue());
        BigDecimal taxaEfetivaMensal = calcTxEfetivaMes(valorEmprestimo, parcelaMensal, taxaJurosMensal, qtdeParcelas);
        emprestimo.setTaxaEfetivaMensal(taxaEfetivaMensal.doubleValue());

        return emprestimo;
    }

    /**
     * Calcula o valor da parcela mensal usando o método Price.
     */
    public static BigDecimal calcParcela(BigDecimal valorTotalFinanciado, double taxaJurosMensal, int qtdeParcelas) {
        if (valorTotalFinanciado.doubleValue() <= 0 || qtdeParcelas <= 1 || taxaJurosMensal < 0) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        BigDecimal umMaisTaxa = ONE.add(BigDecimal.valueOf(taxaJurosMensal / 100));
        BigDecimal denominador = ONE.subtract(umMaisTaxa.pow(-qtdeParcelas, DECIMAL128));
        return valorTotalFinanciado.multiply(BigDecimal.valueOf(taxaJurosMensal / 100)).divide(denominador, DECIMAL128);
    }

    /**
     * Calcula o valor do seguro com base no valor do empréstimo, idade e quantidade de parcelas.
     */
    public static BigDecimal calcSeguro(BigDecimal valorEmprestimo, LocalDate dtNasc, LocalDate dataContratacao, int qtdeParcelas) {
        BigDecimal segFixo = new BigDecimal(Double.toString(params.getPercentualSegFixo() / 100));
        BigDecimal segVar = new BigDecimal(Double.toString(params.getPercentualSegVar() / 100));
        if (valorEmprestimo.doubleValue() <= 0 || qtdeParcelas <= 1) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        long idade = YEARS.between(dtNasc, dataContratacao);
        BigDecimal fatorParcelas = BigDecimal.valueOf(qtdeParcelas).divide(DOZE, DECIMAL128);
        return (valorEmprestimo.multiply(segFixo.add(segVar.multiply(BigDecimal.valueOf(idade))).multiply(fatorParcelas)));
    }

    /**
     * Calcula o valor do IOF com base no valor financiado e período do contrato.
     */
    public static BigDecimal calcIOF(BigDecimal valorEmprestimoComSeguro, LocalDate dataLiberacaoCred, LocalDate dataFimContrato) {
        BigDecimal percentualFixoIof = new BigDecimal(Double.toString(params.getPercentualIofFixo() / 100));
        BigDecimal percentualVarIof = new BigDecimal(Double.toString(params.getPercentualIofVar() / 100));
        if (valorEmprestimoComSeguro.doubleValue() <= 0) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        long diasDeContrato = DAYS.between(dataLiberacaoCred, dataFimContrato);
        long diasIOF = Long.min(diasDeContrato, 365);
        BigDecimal fixoIof = percentualFixoIof.multiply(valorEmprestimoComSeguro);
        BigDecimal varIof = percentualVarIof.multiply(BigDecimal.valueOf(diasIOF)).multiply(valorEmprestimoComSeguro);
        return fixoIof.add(varIof);
    }

    /**
     * Calcula a taxa efetiva mensal usando o método de Newton-Raphson.
     */
    public static BigDecimal calcTxEfetivaMes(BigDecimal valorEmprestimo, BigDecimal parcelaMensal, double taxaNominal, int qtdeParcelas) {
        BigDecimal TOLERANCIA = new BigDecimal("0.000001");
        int MAX_ITERACOES = 100;
        if (valorEmprestimo.doubleValue() <= 0 || parcelaMensal.doubleValue() < 0 || taxaNominal <= 0 || qtdeParcelas <= 1) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        BigDecimal taxaEfetiva = BigDecimal.valueOf(taxaNominal / 100);
        BigDecimal fr;
        BigDecimal frlin;
        for (int i = 0; i < MAX_ITERACOES; i++) {
            fr = fr(valorEmprestimo, parcelaMensal, taxaEfetiva, qtdeParcelas);
            frlin = frlin(parcelaMensal, taxaEfetiva, qtdeParcelas);
            if (fr.abs().compareTo(TOLERANCIA) < 0) {
                return taxaEfetiva.multiply(BigDecimal.valueOf(100)).setScale(2, HALF_UP);
            }
            taxaEfetiva = taxaEfetiva.subtract(fr.divide(frlin, DECIMAL128));
        }
        return taxaEfetiva.multiply(BigDecimal.valueOf(100)).setScale(2, HALF_UP);
    }

    private static BigDecimal fr(BigDecimal valorEmprestimo, BigDecimal parcelaMensal, BigDecimal taxaNominal, int qtdeParcelas) {
        BigDecimal umMaisTaxa = ONE.add(taxaNominal);
        BigDecimal umMaisTaxaPowNeg = umMaisTaxa.pow(-qtdeParcelas, DECIMAL128);
        BigDecimal numerador = ONE.subtract(umMaisTaxaPowNeg);
        BigDecimal denominador = taxaNominal;
        BigDecimal resultado = numerador.divide(denominador, DECIMAL128);
        return (parcelaMensal.multiply(resultado)).subtract(valorEmprestimo);
    }

    private static BigDecimal frlin(BigDecimal parcelaMensal, BigDecimal taxaNominal, int qtdeParcelas) {
        BigDecimal umMaisTaxa = ONE.add(taxaNominal);
        BigDecimal umMaisTaxaPowNeg = umMaisTaxa.pow(-qtdeParcelas, DECIMAL128);
        BigDecimal umMaisTaxaPowNegMaisUm = umMaisTaxa.pow(-qtdeParcelas - 1, DECIMAL128);
        BigDecimal numerador1 = umMaisTaxaPowNeg.subtract(ONE);
        BigDecimal numerador2 = BigDecimal.valueOf(qtdeParcelas).multiply(umMaisTaxaPowNegMaisUm);
        BigDecimal denominador1 = taxaNominal.pow(2, DECIMAL128);
        BigDecimal denominador2 = taxaNominal;
        BigDecimal resultado1 = numerador1.divide(denominador1, DECIMAL128);
        BigDecimal resultado2 = numerador2.divide(denominador2, DECIMAL128);
        return (resultado1.add(resultado2)).multiply(parcelaMensal);
    }

    /**
     * Processa os valores das parcelas, calculando juros, amortização, multa e juros mora.
     * Retorna os detalhes em uma lista de mapas associada ao Emprestimo.
     */
    public static Emprestimo processarValoresParcela(Emprestimo emprestimo) {
        if (emprestimo.getValorParcela() <= 0 || emprestimo.getJuros() <= 0 || emprestimo.getQuantidadeParcelas() <= 1) {
            throw new IllegalArgumentException("Valores inválidos");
        }

        List<Parcela> parcelas = emprestimo.getParcelaList();
        BigDecimal saldoDevedor = BigDecimal.valueOf(emprestimo.getValorTotal());
        BigDecimal taxa = BigDecimal.valueOf(emprestimo.getJuros() / 100);
        BigDecimal taxaDiaria = BigDecimal.valueOf(conversorTaxaDeJurosDiaria(emprestimo.getJuros()) / 100);
        BigDecimal umMaisTaxa = BigDecimal.ONE.add(taxaDiaria);
        BigDecimal valorParcela = BigDecimal.valueOf(emprestimo.getValorParcela());
        BigDecimal saldoDevedorPresenteAtualizado = BigDecimal.ZERO;

        List<Map<String, Object>> detalhesParcelas = new ArrayList<>();

        for (int i = 0; i < emprestimo.getQuantidadeParcelas(); i++) {
            Parcela parcela = parcelas.get(i);
            LocalDate dataVencimento = parcela.getDataVencimento();
            LocalDate hoje = LocalDate.now();
            int diasHojeVenc = (int) DAYS.between(hoje, dataVencimento);

            BigDecimal juros = saldoDevedor.multiply(taxa, DECIMAL128);
            BigDecimal amortizacao = valorParcela.subtract(juros, DECIMAL128);
            saldoDevedor = saldoDevedor.subtract(amortizacao, DECIMAL128);

            Map<String, Object> detalhesParcela = new HashMap<>();
            detalhesParcela.put("numeroParcela", parcela.getNumeroParcela());
            detalhesParcela.put("juros", juros.doubleValue());
            detalhesParcela.put("amortizacao", amortizacao.doubleValue());

            if (parcela.getDataPagamento() == null) {
                BigDecimal valorPresente;
                if (dataVencimento.isBefore(hoje)) {
                    valorPresente = valorParcela;
                    parcela.setStatusParcela(StatusEmpParcela.ATRASADA);

                    BigDecimal multa = multaAtraso(valorParcela, emprestimo.getTaxaMulta());
                    BigDecimal jurosMora = valorJurosMora(valorParcela.doubleValue(),
                            emprestimo.getTaxaJurosMora(), dataVencimento);

                    detalhesParcela.put("valorPresenteParcela", valorPresente.doubleValue());
                    detalhesParcela.put("multa", multa.doubleValue());
                    detalhesParcela.put("jurosMora", jurosMora.doubleValue());
                } else {
                    valorPresente = valorParcela.divide(umMaisTaxa.pow(diasHojeVenc, DECIMAL128), DECIMAL128);
                    detalhesParcela.put("valorPresenteParcela", valorPresente.doubleValue());
                }
                saldoDevedorPresenteAtualizado = saldoDevedorPresenteAtualizado.add(valorPresente);
            } else {
                detalhesParcela.put("valorPresenteParcela", 0.0);
            }

            detalhesParcelas.add(detalhesParcela);
        }

        emprestimo.setSaldoDevedorAtualizado(saldoDevedorPresenteAtualizado.doubleValue());
        emprestimo.setDetalhesParcelas(detalhesParcelas); // Assumindo que Emprestimo tem esse método
        return emprestimo;
    }

    /**
     * Calcula o valor da carência com base na taxa de juros e dias de carência.
     */
    public static BigDecimal calcularCarencia(BigDecimal valorTotalFinanciado, double taxaDeJurosMensal, int diasCarencia) {
        if (valorTotalFinanciado == null || valorTotalFinanciado.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("O total da carência não pode ser menor ou igual a zero");
        }
        BigDecimal taxaDiaria = BigDecimal.valueOf(conversorTaxaDeJurosDiaria(taxaDeJurosMensal) / 100);
        BigDecimal valorCarencia = valorTotalFinanciado.multiply(
                (ONE.add(taxaDiaria).pow(diasCarencia, DECIMAL128)).subtract(ONE), DECIMAL128);
        return valorCarencia;
    }

    /**
     * Converte taxa de juros mensal para diária.
     */
    public static double conversorTaxaDeJurosDiaria(double taxaDeJurosMensal) {
        return (Math.pow(1 + (taxaDeJurosMensal / 100), (double) 1 / 30) - 1) * 100;
    }

    /**
     * Calcula a multa por atraso com base no valor da parcela e taxa de multa.
     */
    public static BigDecimal multaAtraso(BigDecimal valorParcela, double taxaMulta) {
        return valorParcela.multiply(BigDecimal.valueOf(taxaMulta / 100), DECIMAL128);
    }

    /**
     * Calcula os juros de mora com base no valor da parcela, taxa de mora e dias de atraso.
     */
    public static BigDecimal valorJurosMora(double valorParcela, double taxaJurosMora, LocalDate dataVencimento) {
        LocalDate dataAtual = LocalDate.now();
        if (dataAtual.isBefore(dataVencimento) || dataAtual.isEqual(dataVencimento)) {
            return BigDecimal.ZERO;
        }
        long diasAtraso = DAYS.between(dataVencimento, dataAtual);
        BigDecimal taxaDiaria = BigDecimal.valueOf(taxaJurosMora / 100 / 30); // Taxa mensal para diária
        BigDecimal jurosMora = new BigDecimal(valorParcela)
                .multiply(taxaDiaria, DECIMAL128)
                .multiply(BigDecimal.valueOf(diasAtraso), DECIMAL128);
        return jurosMora.setScale(2, HALF_UP);
    }
}