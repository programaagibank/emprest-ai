package br.com.emprestai.service.calculos;

import br.com.emprestai.enums.StatusParcelaEnum;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
import br.com.emprestai.util.ConversorFinanceiro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static br.com.emprestai.util.ConversorFinanceiro.conversorTaxaDeJurosDiaria;
import static java.math.BigDecimal.ONE;
import static java.math.MathContext.DECIMAL128;
import static java.time.temporal.ChronoUnit.DAYS;

public class CalculadoraParcela {

    public static BigDecimal calcularParcelaPrice(BigDecimal valorTotalFinanciado, double taxaJurosMensal, int qtdeParcelas) {
        if (valorTotalFinanciado.doubleValue() <= 0 || qtdeParcelas <= 1 || taxaJurosMensal < 0) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        BigDecimal umMaisTaxa = ONE.add(BigDecimal.valueOf(taxaJurosMensal / 100));
        BigDecimal denominador = ONE.subtract(umMaisTaxa.pow(-qtdeParcelas, DECIMAL128));
        return valorTotalFinanciado.multiply(BigDecimal.valueOf(taxaJurosMensal / 100)).divide(denominador, DECIMAL128);
    }


    public static List<Parcela> processarValoresParcela(Emprestimo emprestimo) {
        if (emprestimo.getValorParcela() <= 0 || emprestimo.getTaxaJuros() <= 0 || emprestimo.getQuantidadeParcelas() <= 1) {
            throw new IllegalArgumentException("Valores inválidos");
        }

        List<Parcela> parcelas = emprestimo.getParcelaList();
        BigDecimal saldoDevedor = BigDecimal.valueOf(emprestimo.getValorTotal());
        BigDecimal taxa = BigDecimal.valueOf(emprestimo.getTaxaJuros() / 100);
        BigDecimal taxaDiaria = BigDecimal.valueOf(conversorTaxaDeJurosDiaria(emprestimo.getTaxaJuros()) / 100);
        BigDecimal umMaisTaxa = BigDecimal.ONE.add(taxaDiaria);
        BigDecimal valorParcela = BigDecimal.valueOf(emprestimo.getValorParcela());

        for (int i = 0; i < emprestimo.getQuantidadeParcelas(); i++) {
            Parcela parcela = parcelas.get(i);
            LocalDate dataVencimento = parcela.getDataVencimento();
            LocalDate hoje = LocalDate.now();
            int diasHojeVenc = (int) DAYS.between(hoje, dataVencimento);

            // Calcula os juros sobre o saldo devedor restante
            BigDecimal juros = saldoDevedor.multiply(taxa, DECIMAL128);
            parcela.setJuros(juros.doubleValue());

            // Amortização = Parcela mensal - Juros
            BigDecimal amortizacao = valorParcela.subtract(juros, DECIMAL128);
            parcela.setAmortizacao(amortizacao.doubleValue());

            // Atualiza o saldo devedor
            saldoDevedor = saldoDevedor.subtract(amortizacao, DECIMAL128);

            parcela.setSaldoDevedor(saldoDevedor.doubleValue());

            if (parcela.getDataPagamento() == null) {
                BigDecimal valorPresente;
                if (dataVencimento.isBefore(hoje)) {
                    valorPresente = valorParcela;
                    parcela.setValorPresenteParcela(valorPresente.doubleValue());
                    parcela.setStatusParcela(StatusParcelaEnum.ATRASADA);

                    BigDecimal multa = CalculadoraMultaJurosMora.multaAtraso(valorParcela, emprestimo.getTaxaMulta());
                    parcela.setMulta(multa.doubleValue());

                    BigDecimal jurosMora = CalculadoraMultaJurosMora.valorJurosMora(valorParcela.doubleValue(),
                            emprestimo.getTaxaJurosMora(), dataVencimento);
                    parcela.setJurosMora(jurosMora.doubleValue());
                } else {
                    valorPresente = valorParcela.divide(umMaisTaxa.pow(diasHojeVenc, DECIMAL128), DECIMAL128);
                    parcela.setValorPresenteParcela(valorPresente.doubleValue());
                }
            } else {
                parcela.setValorPresenteParcela(parcela.getValorPago());
            }
        }

        return parcelas;
    }
}