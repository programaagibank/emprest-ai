package br.com.emprestai.service;

public class CalculoPessoal {


        // 11.2.1 Idade Máxima
        public static void idadeMaxima(double idade, double quantidadeParcelas) {
            double idadeFinal = idade + (quantidadeParcelas / 12);
            if (idadeFinal <= 75) {
                System.out.println("Idade ok. Máximo 75 anos ao final.");
            } else {
                System.out.println("Idade final ultrapassa 75 anos!");
            }
        }

        // 11.2.2 Valor do Empréstimo
        public static void scoreEmprestimo(double score, double valorEmprestimo) {
            if (score >= 0 && score <= 200) {
                System.out.println("Altíssimo risco. Empréstimo não permitido.");
            } else if (score >= 201 && score <= 400) {
                if (valorEmprestimo >= 100 && valorEmprestimo <= 1000) {
                    System.out.println("Alto risco. Limite entre R$ 100 e R$ 1.000.");
                } else {
                    System.out.println("Valor fora do limite permitido para Alto Risco.");
                }
            } else if (score >= 401 && score <= 600) {
                if (valorEmprestimo >= 100 && valorEmprestimo <= 5000) {
                    System.out.println("Risco moderado. Limite entre R$ 100 e R$ 5.000.");
                } else {
                    System.out.println("Valor fora do limite permitido entre R$ 100 e R$ 5.000.");
                }
            } else if (score >= 601 && score <= 800) {
                if (valorEmprestimo >= 100 && valorEmprestimo <= 15000) {
                    System.out.println("Risco baixo. Limite entre R$ 100 e R$ 15.000.");
                } else {
                    System.out.println("Valor fora do limite permitido entre R$ 100 e R$ 15.000.");
                }
            } else if (score >= 801 && score <= 1000) {
                if (valorEmprestimo >= 100 && valorEmprestimo <= 20000) {
                    System.out.println("Risco muito baixo. Limite entre R$ 100 e R$ 20.000.");
                } else {
                    System.out.println("Valor fora do limite permitido entre R$ 100 e R$ 20.000.");
                }
            } else {
                System.out.println("Score inválido. Deve estar entre 0 e 1000.");
            }
        }

        // 11.2.3 Quantidade de Parcelas
        public static void quantidadeParcelas(double score, double quantidadeParcelas) {
            if (score >= 0 && score <= 200) {
                System.out.println("Altíssimo risco. Empréstimo não permitido.");
            } else if (score >= 201 && score <= 400) {
                if (quantidadeParcelas >= 6 && quantidadeParcelas <= 12) {
                    System.out.println("Alto risco. Permitido de 6 até 12 parcelas.");
                } else {
                    System.out.println("Quantidade de parcelas fora do limite permitido (6 a 12).");
                }
            } else if (score >= 401 && score <= 600) {
                if (quantidadeParcelas >= 6 && quantidadeParcelas <= 18) {
                    System.out.println("Risco moderado. Permitido de 6 até 18 parcelas.");
                } else {
                    System.out.println("Quantidade de parcelas fora do limite permitido (6 a 18).");
                }
            } else if (score >= 601 && score <= 800) {
                if (quantidadeParcelas >= 6 && quantidadeParcelas <= 24) {
                    System.out.println("Risco baixo. Permitido de 6 até 24 parcelas.");
                } else {
                    System.out.println("Quantidade de parcelas fora do limite permitido (6 a 24).");
                }
            } else if (score >= 801 && score <= 1000) {
                if (quantidadeParcelas >= 6 && quantidadeParcelas <= 30) {
                    System.out.println("Risco muito baixo. Permitido de 6 até 30 parcelas.");
                } else {
                    System.out.println("Quantidade de parcelas fora do limite permitido (6 a 30).");
                }
            } else {
                System.out.println("Score inválido. Deve estar entre 0 e 1000.");
            }
        }

        // 11.2.4 Taxa de Juros
        public static void taxaJuros(double score, double taxaMensal) {
            if (score >= 0 && score <= 200) {
                System.out.println("Altíssimo risco. Empréstimo não permitido.");
            } else if (score >= 201 && score <= 400) {
                if (taxaMensal == 9.99) {
                    System.out.println("Alto risco. Taxa permitida: 9,99%.");
                } else {
                    System.out.println("Taxa fora do valor permitido (exatamente 9,99%).");
                }
            } else if (score >= 401 && score <= 600) {
                if (taxaMensal >= 8.49 && taxaMensal <= 9.99) {
                    System.out.println("Risco moderado. Taxa permitida entre 8,49% e 9,99%.");
                } else {
                    System.out.println("Taxa fora do limite permitido (8,49% a 9,99%).");
                }
            } else if (score >= 601 && score <= 800) {
                if (taxaMensal >= 8.49 && taxaMensal <= 9.99) {
                    System.out.println("Risco baixo. Taxa permitida entre 8,49% e 9,99%.");
                } else {
                    System.out.println("Taxa fora do limite permitido (8,49% a 9,99%).");
                }
            } else if (score >= 801 && score <= 1000) {
                if (taxaMensal >= 8.49 && taxaMensal <= 9.99) {
                    System.out.println("Risco muito baixo. Taxa permitida entre 8,49% e 8,99%.");
                } else {
                    System.out.println("Taxa fora do limite permitido (8,49% a 8,99%).");
                }
            } else {
                System.out.println("Score inválido. Deve estar entre 0 e 1000.");
            }
        }

        // 11.2.5 Score de Crédito
        public static void scoreCredito(double score) {
            if (score >= 201) {
                System.out.println("Score ok. Mínimo de 201.");
            } else {
                System.out.println("Score muito baixo! Precisa ser pelo menos 201.");
            }
        }

        // 11.2.6 Capacidade de Pagamento
        public static void capacidadePagamento(double renda, double valorEmprestimo, double quantidadeParcelas) {
            double parcela = valorEmprestimo / quantidadeParcelas;
            double limiteRenda = renda * 0.30;
            if (parcela <= limiteRenda) {
                System.out.println("Parcela ok. Cabe em 30% da renda.");
            } else {
                System.out.println("Parcela maior que 30% da renda!");
            }
        }

        // 11.2.7 Carência
        public static void carencia(double diasCarencia) {
            if (diasCarencia <= 30) {
                System.out.println("Carência ok. Máximo 30 dias.");
            } else {
                System.out.println("Carência maior que 30 dias!");
            }
        }

        // 11.3.1 Percentual Mínimo Pago (Refinanciamento)
        public static void percentualMinimoPago(double parcelasPagas, double totalParcelas) {
            double percentual = (parcelasPagas / totalParcelas) * 100;
            if (percentual >= 20) {
                System.out.println("Ok para refinanciamento. Pelo menos 20% pago.");
            } else {
                System.out.println("Menos de 20% pago. Não pode refinanciar!");
            }
        }
    }

