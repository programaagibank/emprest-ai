package br.com.emprestai.view;

import br.com.emprestai.enums.StatusEmpParcela;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
import br.com.emprestai.service.calculos.CalculadoraEmprestimo;
import br.com.emprestai.service.calculos.CalculoPessoal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ConsultaView {

    public void buscarEmprestimo() {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setValorEmprestimo(5000.00); // Agora double
        emprestimo.setQuantidadeParcelas(12);
        double taxa = CalculoPessoal.calculoTaxaDeJurosMensal(emprestimo.getQuantidadeParcelas());
        emprestimo.setJuros(taxa);
        emprestimo.setDataContratacao(LocalDate.of(2025, 3, 7)); // Sexta-feira
        emprestimo.setDataInicio(LocalDate.of(2025, 4, 7)); // Início do pagamento (1 mês após contratação)
        emprestimo.setContratarSeguro(true);
        emprestimo.setTaxaMulta(0.02); // 2% (exemplo)
        emprestimo.setTaxaJurosMora(0.033); // 1% ao mês / 30 dias = 0.033% ao dia (exemplo)

        LocalDate dtNasc = LocalDate.of(1995, 1, 1);

        // Executa o metodo
        Emprestimo resultado = CalculadoraEmprestimo.contratoPrice(emprestimo, dtNasc);

        List<Parcela> parcelas = new ArrayList<>();
        LocalDate dataVencimento = emprestimo.getDataInicio().minusMonths(2);

        for (int i = 1; i <= emprestimo.getQuantidadeParcelas(); i++) {
            Parcela parcela = new Parcela();
            parcela.setNumeroParcela(i);
            parcela.setDataVencimento(dataVencimento);
            parcela.setStatusParcela(StatusEmpParcela.PENDENTE);

            // Avança para o próximo mês
            dataVencimento = dataVencimento.plusMonths(1);

            parcelas.add(parcela);
        }

        // Associa as parcelas ao empréstimo
        emprestimo.setParcelaList(parcelas);

        // Chama o método de processamento
        emprestimo = CalculadoraEmprestimo.processarValoresParcela(emprestimo);


        // Códigos ANSI para cores
        final String RESET = "\u001B[0m";
        final String YELLOW = "\u001B[33m";
        final String GREEN = "\u001B[32m";
        final String RED = "\u001B[31m";
        final String CYAN = "\u001B[36m";
        final String GRAY = "\u001B[37m";

        // Imprime o cabeçalho da tabela com cor amarela
        System.out.println(YELLOW + "\nTabela de Parcelas do Empréstimo" + RESET);
        System.out.println("------------------------------------------------------------------------------------------------------------");
        System.out.printf(YELLOW + "%-5s | %-12s | %-14s | %-10s | %-12s | %-8s | %-12s | %-10s%n" + RESET,
                "Nº", "Vencimento", "Valor Presente", "Juros", "Amortização", "Multa", "Juros Mora", "Status");
        System.out.println("------------------------------------------------------------------------------------------------------------");

        // Imprime cada parcela com cores alternadas
        int linha = 0;
        for (Parcela parcela : parcelas) {
            String corLinha = (linha % 2 == 0) ? GRAY : RESET; // Alterna entre cinza e normal
            linha++;

            // Define cor para valores negativos
            String corJuros = parcela.getJuros() < 0 ? RED : corLinha;
            String corMulta = parcela.getMulta() < 0 ? RED : corLinha;
            String corJurosMora = parcela.getJurosMora() < 0 ? RED : corLinha;

            // Define cor para o status
            String corStatus;
            switch (parcela.getstatusParcela()) {
                case PAGA:
                    corStatus = GREEN; // Status "Paga" em verde
                    break;
                case ATRASADA:
                    corStatus = RED; // Status "Paga" em verde
                    break;
                case PENDENTE:
                    corStatus = YELLOW; // Status "Atrasada" em vermelho
                    break;
                default:
                    corStatus = YELLOW; // Outros status (como "Pendente") em amarelo
            }

            // Imprime a linha da parcela
            System.out.printf(corLinha + "%-5d | %-12s | %-14.2f | " + corJuros + "%-10.2f" + corLinha + " | %-12.2f | " + corMulta + "%-8.2f" + corLinha + " | " + corJurosMora + "%-12.2f" + corLinha + " | " + corStatus + "%-10s" + RESET + "%n",
                    parcela.getNumeroParcela(),
                    parcela.getDataVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    parcela.getValorPresenteParcela(),
                    parcela.getJuros(),
                    parcela.getAmortizacao(),
                    parcela.getMulta(),
                    parcela.getJurosMora(),
                    parcela.getstatusParcela());

            System.out.println("------------------------------------------------------------------------------------------------------------");
        }

        // Imprime o saldo devedor final em verde
        System.out.printf(GREEN + "Saldo Devedor Final: %.2f%n" + RESET, emprestimo.getSaldoDevedorAtualizado());
    }
}
