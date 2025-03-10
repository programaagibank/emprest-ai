package br.com.emprestai;

import br.com.emprestai.controller.EmprestimoController;

public class App {
    public static void main(String[] args) {

        EmprestimoController emprestimoController = new EmprestimoController();
        emprestimoController.simularEmprestimo();

    }
}
