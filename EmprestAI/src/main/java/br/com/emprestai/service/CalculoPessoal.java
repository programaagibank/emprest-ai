package br.com.emprestai.service;

public class CalculoPessoal {


        private static final double PERCENTUAL_RENDA_PESSOAL = 0.30;
        private static final double PERCENTUAL_MULTA_ATRASO = 0.02;
        private static final double PERCENTUAL_JUROS_MORA = 0.00033;

        // 12.1 Capacidade de Pagamento
        public static double calculoDeCapacidadeDePagamento(double rendaLiquida) {
            double capacidadeMaxima = rendaLiquida * PERCENTUAL_RENDA_PESSOAL;
            return capacidadeMaxima;
        }

        // 12.3 Taxa de Juros Mensal
            public static double calculoTaxaDeJurosMensal(double score) {
            double taxaMaxima = 0.0999;
            double taxaMinima = 0.0849;


            if (score >= 801 && score <= 1000) {
                return taxaMinima;
            } else if (score >= 601 && score <= 800) {
                return 0.0899;
            } else if (score >= 401 && score <= 600) {
                return 0.0949;
            } else if (score >= 201 && score <= 400) {
                return taxaMaxima;
            } else {
                return taxaMaxima;
            }
        }

        // 12.9 Juros Mora e Multa por Atraso
        public static double calculoDeJurosMoraEMulta(double valorParcela, double diasAtraso) {
            double multa = valorParcela * PERCENTUAL_MULTA_ATRASO;
            double jurosMora = valorParcela * PERCENTUAL_JUROS_MORA * diasAtraso;
            return multa + jurosMora;
        }
    }


