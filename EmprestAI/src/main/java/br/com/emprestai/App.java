package br.com.emprestai;

import br.com.emprestai.controller.EmprestimoController;
import br.com.emprestai.enums.TipoEmpEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static br.com.emprestai.enums.TipoEmpEnum.CONSIGNADO;

public class App {
    public static void main(String[] args) {
        // Instanciar o EmprestimoController
        EmprestimoController emprestimoController = new EmprestimoController();

        // Definir os parâmetros para a simulação
        Long idCliente = 1L; // ID do cliente
        double valorEmprestimo = 5000.00; // Valor do empréstimo
        TipoEmpEnum tipoEmp = TipoEmpEnum.CONSIGNADO; // Tipo de empréstimo (supondo que o enum tenha CONSIGNADO e
                                                      // PESSOAL)
        int parcelas = 24; // Quantidade de parcelas
        boolean contratarSeguro = true; // Contratar seguro?
        LocalDate dataInicioPagamento = LocalDate.of(2025, 4, 1); // Data de início do pagamento (1º de abril de 2025)

        // Chamar o método simularEmprestimo
        Map<String, Object> resultado = emprestimoController.obterEmprestimo(
                idCliente, valorEmprestimo, tipoEmp, parcelas, contratarSeguro, dataInicioPagamento);

        resultado.forEach((item, valor) -> System.out.println(item + " : " + valor));

    }
}
