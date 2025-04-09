package br.com.emprestai.util;

import br.com.emprestai.controller.ParcelaController;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
import br.com.emprestai.service.calculos.CalculadoraSaldos;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static br.com.emprestai.enums.StatusParcelaEnum.ATRASADA;
import static br.com.emprestai.enums.StatusParcelaEnum.PENDENTE;
import static br.com.emprestai.util.Formatos.formatCpf;
import static br.com.emprestai.util.Formatos.formatCurrency;

public class GeneratePDF {

    public static String generateHtmlContent(Emprestimo emprestimo, ParcelaController parcelaController) throws SQLException {
        Cliente clienteLogado = SessionManager.getInstance().getClienteLogado();
        StringBuilder html = new StringBuilder();
        List<Parcela> parcelas = parcelaController.getParcelasByEmprestimo(emprestimo);
        html.append("<!DOCTYPE html>")
                .append("<html lang=\"pt-br\">")
                .append("<head>")
                .append("<meta charset=\"UTF-8\">")
                .append("<title>Documento Descritivo de Crédito</title>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; font-size: 14px; }")
                .append("table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }")
                .append("th, td { border: 1px solid #333; padding: 5px; text-align: left; }")
                .append("th { background-color: #eee; }")
                .append(".section-title { font-weight: bold; font-size: 16px; margin-top: 20px; border-bottom: 1px solid #333; padding-bottom: 5px; }")
                .append(".header { margin-bottom: 20px; }")
                .append(".header div { margin-bottom: 5px; }")
                .append(".separator { border-top: 1px solid #333; margin: 20px 0; }")
                .append(".summary { display: flex; justify-content: space-between; margin-bottom: 20px; }")
                .append(".summary div { width: 30%; }")
                .append("</style>")
                .append("</head>")
                .append("<body>");

        html.append("<div class=\"header\">")
                .append("<h2>Documento Descritivo de Crédito</h2>")
                .append("<div><strong>Nome:</strong> ").append(clienteLogado != null ? clienteLogado.getNomeCliente() : "Não disponível").append("</div>")
                .append("<div><strong>CPF:</strong> ").append(clienteLogado != null ? formatCpf(clienteLogado.getCpfCliente()) : "Não disponível").append("</div>")
                .append("<div><strong>Emissão:</strong> ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</div>")
                .append("</div>");

        html.append("<div class=\"separator\"></div>");

        html.append("<div class=\"section-title\">Característica do Contrato</div>")
                .append("<table>")
                .append("<tr><td>Modalidade de Operação</td><td>").append(emprestimo.getTipoEmprestimo().name()).append("</td></tr>")
                .append("<tr><td>Valor da Operação</td><td>").append(formatCurrency(emprestimo.getValorTotal() - emprestimo.getValorSeguro() - emprestimo.getOutrosCustos() - emprestimo.getValorIOF())).append("</td></tr>")
                .append("<tr><td>Número do Contrato</td><td>").append(emprestimo.getIdEmprestimo()).append("</td></tr>")
                .append("<tr><td>Data da Contratação</td><td>").append(emprestimo.getDataContratacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</td></tr>")
                .append("<tr><td>Data de Liberação do Crédito</td><td>").append(emprestimo.getDataLiberacaoCred().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</td></tr>")
                .append("<tr><td>Data do Último Vencimento</td><td>")
                .append(emprestimo.getDataInicio().plusMonths(emprestimo.getQuantidadeParcelas()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .append("</td></tr>")
                .append("</table>");

        html.append("<div class=\"separator\"></div>");

        double taxaJurosAnual = emprestimo.getTaxaJuros() * 12;
        double tributos = emprestimo.getValorIOF();
        double tarifas = emprestimo.getOutrosCustos() + (emprestimo.getContratarSeguro() != null && emprestimo.getContratarSeguro() ? emprestimo.getValorSeguro() : 0);
        html.append("<div class=\"section-title\">Taxa de Juros</div>")
                .append("<table>")
                .append("<tr><td>Taxa de Juros Mensal Nominal</td><td>").append(String.format("%.2f%% a.m.", emprestimo.getTaxaJuros())).append("</td></tr>")
                .append("<tr><td>Taxa de Juros Anual Nominal</td><td>").append(String.format("%.2f%% a.a.", taxaJurosAnual)).append("</td></tr>")
                .append("<tr><td>Taxa de Juros Efetiva (CET)</td><td>").append(String.format("%.2f%% a.m.", emprestimo.getTaxaEfetivaMensal())).append("</td></tr>")
                .append("<tr><td>Tarifas</td><td>").append(formatCurrency(tarifas)).append("</td></tr>")
                .append("<tr><td>Tributos</td><td>").append(formatCurrency(tributos)).append("</td></tr>")
                .append("</table>");

        html.append("<div class=\"separator\"></div>");

        int prazoTotal = emprestimo.getQuantidadeParcelas();
        int prazoRemanescente = emprestimo.getQuantidadeParcelas() - emprestimo.getParcelasPagas();
        double saldoDevedorAtualizado = CalculadoraSaldos.calcularSaldoDevedorAtualizado(parcelas);
        html.append("<div class=\"section-title\">Resumo</div>")
                .append("<div class=\"summary\">")
                .append("<div><strong>Prazo Total da Operação:</strong> ").append(prazoTotal).append(" meses</div>")
                .append("<div><strong>Prazo Remanescente:</strong> ").append(prazoRemanescente).append(" meses</div>")
                .append("<div><strong>Saldo Devedor Atual:</strong> ").append(formatCurrency(saldoDevedorAtualizado)).append("</div>")
                .append("</div>");

        html.append("<div class=\"separator\"></div>");

        html.append("<div class=\"section-title\">Demonstrativo de Evolução do Saldo Devedor/Composição do Valor das Parcelas</div>")
                .append("<table>")
                .append("<tr>")
                .append("<th>Nro Parcela</th>")
                .append("<th>Vencimento da Parcela</th>")
                .append("<th>Valor Parcela</th>")
                .append("<th>Valor Principal da Parcela</th>")
                .append("<th>Valor dos Juros da Parcela</th>")
                .append("<th>Saldo Devedor Presente</th>")
                .append("<th>Situação da Parcela</th>")
                .append("</tr>");

        if (parcelas != null && !parcelas.isEmpty()) {
            for (int i = 0; i < parcelas.size(); i++) {
                Parcela parcela = parcelas.get(i);
                double valorParcela = emprestimo.getValorParcela();
                double valorPrincipal = parcela.getAmortizacao();
                double valorJuros = parcela.getJuros();
                double valorPresenteParcela = parcela.getStatus() == PENDENTE || parcela.getStatus() == ATRASADA? parcela.getValorPresenteParcela(): 0;

                html.append("<tr>")
                        .append("<td>").append(i + 1).append("</td>")
                        .append("<td>").append(parcela.getDataVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</td>")
                        .append("<td>").append(formatCurrency(valorParcela)).append("</td>")
                        .append("<td>").append(formatCurrency(valorPrincipal)).append("</td>")
                        .append("<td>").append(formatCurrency(valorJuros)).append("</td>")
                        .append("<td>").append(formatCurrency(valorPresenteParcela)).append("</td>")
                        .append("<td>").append(parcela.getStatus() != null ? parcela.getStatus().toString() : "PARCELA PAGA").append("</td>")
                        .append("</tr>");
            }
        }
        html.append("</table>");

        html.append("</body>")
                .append("</html>");

        return html.toString();
    }
}
