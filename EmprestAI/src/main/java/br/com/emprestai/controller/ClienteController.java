package br.com.emprestai.controller;

import br.com.emprestai.model.Cliente;
import br.com.emprestai.repository.ClienteRepositoryImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class ClienteController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            ClienteRepositoryImpl clienteRepo = new ClienteRepositoryImpl();
            String path = exchange.getRequestURI().getPath(); // ex.: "/clientes/2"
            String[] pathParts = path.split("/");
            int clienteId = Integer.parseInt(pathParts[4]);
            Optional<Cliente> clienteOpt = clienteRepo.findById(clienteId);
            String response = "";
            if(clienteOpt.isPresent()){
                Cliente cliente = clienteOpt.get();
                response = cliente.toString();
            }
            exchange.sendResponseHeaders(200, response.getBytes().length); // Define o status 200 e o tamanho da resposta
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes()); // Envia a resposta ao cliente
            os.close(); // Fecha o fluxo de saída
        } else {
            exchange.sendResponseHeaders(405, -1); // Método não permitido
        }
    }
}
