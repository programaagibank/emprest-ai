package br.com.emprestai.server;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.controller.SimulacaoController;
import com.sun.net.httpserver.HttpServer;

public class Rotas {
    public static void registrarRotas(HttpServer server) {
        server.createContext("/api/emprestai/simulacao", new SimulacaoController());
        server.createContext("/api/emprestai/cliente", new ClienteController());
    }
}
