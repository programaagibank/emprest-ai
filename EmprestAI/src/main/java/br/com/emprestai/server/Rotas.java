package br.com.emprestai.server;

import br.com.emprestai.controller.ClienteController;
import com.sun.net.httpserver.HttpServer;

public class Rotas {
    public static void registrarRotas(HttpServer server) {
        server.createContext("/api/emprestai/cliente", new ClienteController());
    }
}
