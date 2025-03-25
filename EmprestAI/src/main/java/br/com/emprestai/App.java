package br.com.emprestai;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.controller.EmprestimoController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.view.*;

public class App {
    public static void main(String[] args) {
        // Criando os DAOs
        ClienteDAO clienteDAO = new ClienteDAO();
        EmprestimoDAO emprestimoDAO = new EmprestimoDAO();

        // Criando os Controllers
        ClienteController clienteController = new ClienteController(clienteDAO);
        EmprestimoController emprestimoController = new EmprestimoController(emprestimoDAO);

        // Criando as Views
        ClienteView clienteView = new ClienteView(clienteController);
        EmprestimoView emprestimoView = new EmprestimoView(emprestimoController);
        PagamentoView pagamentoView = new PagamentoView();
        ConsultaView consultaView = new ConsultaView();  // Se não precisar de um Controller, pode ser instanciada diretamente.

        // Criando o Menu Principal e passando as outras views como dependências
        MenuPrincipalView menuPrincipal = new MenuPrincipalView(clienteView, emprestimoView, pagamentoView, consultaView);

        // Iniciando a aplicação chamando o menu principal
        menuPrincipal.inicio();
    }
}
