package br.com.emprestai.service.calculos;

import br.com.emprestai.model.Parcela;

import java.math.BigDecimal;
import java.util.List;

import static java.math.MathContext.DECIMAL128;

public class CalculadoraSaldos {

    public static double calcularSaldoDevedorAtualizado(List<Parcela> parcelas) {
        BigDecimal somaValorPresentePagas = BigDecimal.ZERO;

        for (Parcela parcela : parcelas) {
            if (parcela.getDataPagamento() == null) {
                somaValorPresentePagas = somaValorPresentePagas.add(BigDecimal.valueOf(parcela.getValorPresenteParcela()), DECIMAL128);
            }
        }

        return somaValorPresentePagas.doubleValue();
    }
}
