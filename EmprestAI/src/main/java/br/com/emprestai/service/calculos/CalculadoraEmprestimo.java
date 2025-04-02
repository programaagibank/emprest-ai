package br.com.emprestai.service.calculos;

import br.com.emprestai.enums.StatusParcelaEnum;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
import br.com.emprestai.util.EmprestimoParams;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    public static Emprestimo contratoPrice(Emprestimo emprestimo, LocalDate dtNasc) {
        Map<String, Object> dadosContrato = new HashMap<>();

        //Valores para calculo
        BigDecimal valorEmprestimo = new BigDecimal(String.valueOf(emprestimo.getValorEmprestimo()));
        int qtdeParcelas = emprestimo.getQuantidadeParcelas();
        double taxaJurosMensal = emprestimo.getTaxaJuros();

        //Datas
        LocalDate dataContratacao = emprestimo.getDataContratacao();
        LocalDate dataLiberacaoCred = primeiroDiaUtil(dataContratacao);
        emprestimo.setDataLiberacaoCred(dataLiberacaoCred);
        LocalDate dataInicioPagamento = emprestimo.getDataInicio();
        LocalDate dataFimContrato = emprestimo.getDataInicio().plusMonths(qtdeParcelas);

        //Outros valores
        BigDecimal seguro = (emprestimo.getContratarSeguro() ? calcSeguro(valorEmprestimo, dtNasc, dataContratacao, qtdeParcelas) : ZERO);
        BigDecimal valorTotalComSeguro = (valorEmprestimo.add(seguro));
        BigDecimal iof = calcIOF(valorTotalComSeguro, dataLiberacaoCred, dataFimContrato);
        BigDecimal valorTotalSemCarencia = (valorEmprestimo.add(iof).add(seguro));

        int calcularDiasCarencia = (int) DAYS.between(dataContratacao,dataInicioPagamento);
        BigDecimal resultadoCarencia = calcularCarencia(valorTotalSemCarencia,taxaJurosMensal,calcularDiasCarencia);
        emprestimo.setOutrosCustos(resultadoCarencia.doubleValue());
        BigDecimal valorTotalFinanciado = valorTotalSemCarencia.add(resultadoCarencia) ;

        emprestimo.setValorSeguro(seguro.doubleValue());
        emprestimo.setValorIOF(iof.doubleValue());
        emprestimo.setValorTotal(valorTotalFinanciado.doubleValue());

        BigDecimal parcelaMensal = calcParcela(valorTotalFinanciado, taxaJurosMensal, qtdeParcelas);
        emprestimo.setValorParcela(parcelaMensal.doubleValue());
        BigDecimal taxaEfetivaMensal = calcTxEfetivaMes(valorEmprestimo, parcelaMensal, taxaJurosMensal, qtdeParcelas);
        emprestimo.setTaxaEfetivaMensal(taxaEfetivaMensal.doubleValue());

        return emprestimo;
    }

    public static BigDecimal calcParcela(BigDecimal valorTotalFinanciado, double taxaJurosMensal, int qtdeParcelas) {
        if (valorTotalFinanciado.doubleValue() <= 0 || qtdeParcelas <= 1 || taxaJurosMensal < 0) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        BigDecimal umMaisTaxa = ONE.add(BigDecimal.valueOf(taxaJurosMensal/100));
        BigDecimal denominador = ONE.subtract(umMaisTaxa.pow(-qtdeParcelas, DECIMAL128));
        return valorTotalFinanciado.multiply(BigDecimal.valueOf(taxaJurosMensal/100)).divide(denominador, DECIMAL128);
    }

    public static BigDecimal calcSeguro(BigDecimal valorEmprestimo, LocalDate dtNasc, LocalDate dataContratacao, int qtdeParcelas) {
        BigDecimal segFixo = new BigDecimal(Double.toString(params.getPercentualSegFixo()/100));
        BigDecimal segVar = new BigDecimal(Double.toString(params.getPercentualSegVar()/100));
        if (valorEmprestimo.doubleValue() <= 0 || qtdeParcelas <= 1) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        long idade = YEARS.between(dtNasc, dataContratacao);
        BigDecimal fatorParcelas = BigDecimal.valueOf(qtdeParcelas).divide(DOZE, DECIMAL128);
        return (valorEmprestimo.multiply(segFixo.add(segVar.multiply(BigDecimal.valueOf(idade))).multiply(fatorParcelas)));
    }

    public static BigDecimal calcIOF(BigDecimal valorEmprestimoComSeguro, LocalDate dataLiberacaoCred, LocalDate dataFimContrato) {
        BigDecimal percentualFixoIof = new BigDecimal(Double.toString(params.getPercentualIofFixo()/100));
        BigDecimal percentualVarIof = new BigDecimal(Double.toString(params.getPercentualIofVar()/100));
        if (valorEmprestimoComSeguro.doubleValue() <= 0) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        long diasDeContrato = DAYS.between(dataLiberacaoCred, dataFimContrato);
        long diasIOF = Long.min(diasDeContrato, 365);
        BigDecimal fixoIof = percentualFixoIof.multiply(valorEmprestimoComSeguro);
        BigDecimal varIof = percentualVarIof.multiply(BigDecimal.valueOf(diasIOF)).multiply(valorEmprestimoComSeguro);
        return fixoIof.add(varIof);
    }

    //Utilizando metodo de Newton-Raphson //Até aqui existem testes
    public static BigDecimal calcTxEfetivaMes(BigDecimal valorEmprestimo, BigDecimal parcelaMensal, double taxaNominal, int qtdeParcelas) {
        // Tolerância para convergência (ex.: 0,000001)
        BigDecimal TOLERANCIA = new BigDecimal("0.000001");
        // Número máximo de iterações
        int MAX_ITERACOES = 100;
        if (valorEmprestimo.doubleValue() <= 0 || parcelaMensal.doubleValue() < 0 || taxaNominal <= 0 || qtdeParcelas <= 1) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        BigDecimal taxaEfetiva = BigDecimal.valueOf(taxaNominal/100);
        BigDecimal fr;
        BigDecimal frlin;
        for (int i = 0; i < MAX_ITERACOES; i++) {
            //Calculo da parcela
            fr = fr(valorEmprestimo, parcelaMensal, taxaEfetiva, qtdeParcelas);
            frlin = frlin(parcelaMensal, taxaEfetiva, qtdeParcelas);
            // Verifica convergência: se |f(r)| < tolerância, para
            if (fr.abs().compareTo(TOLERANCIA) < 0) {
                return taxaEfetiva.multiply(BigDecimal.valueOf(100)).setScale(2, HALF_UP);
            }
            taxaEfetiva = taxaEfetiva.subtract(fr.divide(frlin, DECIMAL128));
        }
        return taxaEfetiva.multiply(BigDecimal.valueOf(100)).setScale(2, HALF_UP);
    }

    private static BigDecimal fr(BigDecimal valorEmprestimo, BigDecimal parcelaMensal, BigDecimal taxaNominal, int qtdeParcelas) {
        // Passo 1: Calcular (1 + r)
        BigDecimal umMaisTaxa = ONE.add(taxaNominal);

        // Passo 2: Calcular (1 + r)^-n
        BigDecimal umMaisTaxaPowNeg = umMaisTaxa.pow(-qtdeParcelas, DECIMAL128);

        // Passo 3: Calcular 1 - (1 + r)^-n
        BigDecimal numerador = ONE.subtract(umMaisTaxaPowNeg);

        // Passo 4: Definir o denominador r
        BigDecimal denominador = taxaNominal;

        // Passo 5: Calcular (1 - (1 + r)^-n) / r
        BigDecimal resultado = numerador.divide(denominador, DECIMAL128);

        // Passo 6: Calcular f(r) = PMT * [(1 - (1 + r)^-n) / r] - PV
        return (parcelaMensal.multiply(resultado)).subtract(valorEmprestimo);
    }

    private static BigDecimal frlin(BigDecimal parcelaMensal, BigDecimal taxaNominal, int qtdeParcelas) {
        // Passo 1: Calcular (1 + r)
        BigDecimal umMaisTaxa = ONE.add(taxaNominal);

        // Passo 2: Calcular (1 + r)^-n
        BigDecimal umMaisTaxaPowNeg = umMaisTaxa.pow(-qtdeParcelas, DECIMAL128);

        // Passo 3: Calcular (1 + r)^-(n+1)
        BigDecimal umMaisTaxaPowNegMaisUm = umMaisTaxa.pow(-qtdeParcelas - 1, DECIMAL128);

        // Passo 4: Calcular (1 + r)^-n - 1
        BigDecimal numerador1 = umMaisTaxaPowNeg.subtract(ONE);

        // Passo 5: Calcular n * (1 + r)^-(n+1)
        BigDecimal numerador2 = BigDecimal.valueOf(qtdeParcelas).multiply(umMaisTaxaPowNegMaisUm);

        // Passo 6: Calcular r^2
        BigDecimal denominador1 = taxaNominal.pow(2, DECIMAL128);

        // Passo 7: Definir o denominador r
        BigDecimal denominador2 = taxaNominal;

        // Passo 8: Calcular [(1 + r)^-n - 1] / r^2
        BigDecimal resultado1 = numerador1.divide(denominador1, DECIMAL128);

        // Passo 9: Calcular [n * (1 + r)^-(n+1)] / r
        BigDecimal resultado2 = numerador2.divide(denominador2, DECIMAL128);

        // Passo 10: Somar os resultados e multiplicar por PMT
        return (resultado1.add(resultado2)).multiply(parcelaMensal);
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

            if (parcela.getDataPagamento() == null) {
                BigDecimal valorPresente;
                if (dataVencimento.isBefore(hoje)) {
                    valorPresente = valorParcela;
                    parcela.setValorPresenteParcela(valorPresente.doubleValue());
                    parcela.setStatusParcela(StatusParcelaEnum.ATRASADA);

                    BigDecimal multa = multaAtraso(valorParcela, emprestimo.getTaxaMulta());
                    parcela.setMulta(multa.doubleValue());

                    BigDecimal jurosMora = valorJurosMora(valorParcela.doubleValue(),
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

    //A fazer
    public static BigDecimal calcularCarencia(BigDecimal valorTotalFinanciado, double taxaDeJurosMensal ,int diasCarencia){
        if(valorTotalFinanciado == null){
            throw new IllegalArgumentException("O Total da carência não pode ser menor ou igual a zero");
        }
        BigDecimal valorCarencia =  valorTotalFinanciado.multiply((ONE.add(new BigDecimal(conversorTaxaDeJurosDiaria(taxaDeJurosMensal)/100)).pow(diasCarencia)).subtract(ONE));

        return valorCarencia;
    }

    // A Fazer
    public static double conversorTaxaDeJurosDiaria(double taxaDeJurosMensal){
        double taxaDeJurosDiaria = Math.pow((1+taxaDeJurosMensal), (double) 1 /30)-1;

        return taxaDeJurosDiaria;
    }


    public static BigDecimal multaAtraso(BigDecimal valorParcela, double taxaMulta){
        return valorParcela.multiply(BigDecimal.valueOf(taxaMulta));
    }
    // A fazer
    public static BigDecimal valorJurosMora(double valorParcela, double taxaJurosMora, LocalDate dataVencimento) {
        // Data atual (18 de março de 2025, conforme fornecido)
        LocalDate dataAtual = LocalDate.now();

        // Verifica se há atraso
        if (dataAtual.isBefore(dataVencimento) || dataAtual.isEqual(dataVencimento)) {
            return BigDecimal.ZERO; // Sem juros de mora se não houver atraso
        }

        // Calcula o número de dias em atraso
        long diasAtraso = ChronoUnit.DAYS.between(dataVencimento, dataAtual);

        // Converte a taxa de juros mora mensal para diária (dividindo por 30)
        BigDecimal taxaDiaria = BigDecimal.valueOf(taxaJurosMora / 100);

        // Calcula os juros de mora: valorParcela * taxaDiaria * diasAtraso
        BigDecimal jurosMora = new BigDecimal(valorParcela)
                .multiply(taxaDiaria, DECIMAL128)
                .multiply(BigDecimal.valueOf(diasAtraso), DECIMAL128);

        return jurosMora.setScale(2, HALF_UP); // Arredonda para 2 casas decimais
    }

}
