package br.com.emprestai.service.calculos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_UP;

public class CalculadoraMultaJurosMora {

    public static BigDecimal multaAtraso(BigDecimal valorParcela, double taxaMulta) {
        return valorParcela.multiply(BigDecimal.valueOf(taxaMulta));
    }

    public static BigDecimal valorJurosMora(double valorParcela, double taxaJurosMora, LocalDate dataVencimento) {
        LocalDate dataAtual = LocalDate.now();
        if (dataAtual.isBefore(dataVencimento) || dataAtual.isEqual(dataVencimento)) {
            return BigDecimal.ZERO;
        }
        long diasAtraso = ChronoUnit.DAYS.between(dataVencimento, dataAtual);
        BigDecimal taxaDiaria = BigDecimal.valueOf(taxaJurosMora / 100);
        BigDecimal jurosMora = new BigDecimal(valorParcela)
                .multiply(taxaDiaria, DECIMAL128)
                .multiply(BigDecimal.valueOf(diasAtraso), DECIMAL128);
        return jurosMora.setScale(2, HALF_UP);
    }
}
