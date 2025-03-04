package br.com.emprestai.server;

import br.com.emprestai.controller.ClienteController;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Servidor {
    public static void iniciar() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/clientes", new ClienteController());
        server.setExecutor(null); // Usa o executor padrão
        server.start();
        System.out.println("Servidor rodando em http://localhost:8080/clientes");
    }
}