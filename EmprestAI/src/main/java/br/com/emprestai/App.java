package br.com.emprestai;

import br.com.emprestai.controller.EmprestimoController;

import java.time.LocalDate;

import static br.com.emprestai.enums.TipoEmpEnum.CONSIGNADO;

public class App {
    public static void main(String[] args) {

        EmprestimoController emprestimoController = new EmprestimoController();
        emprestimoController.simularEmprestimo("30191092053", CONSIGNADO, 24, true, LocalDate.of(2025, 3, 2));

    }
}
