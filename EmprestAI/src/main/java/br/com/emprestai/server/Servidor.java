package br.com.emprestai.server;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class Servidor {
    public static void iniciar() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        Rotas.registrarRotas(server);

        server.setExecutor(null);
        server.start();

        System.out.println("Servidor rodando na porta 8080.");
    }
}
