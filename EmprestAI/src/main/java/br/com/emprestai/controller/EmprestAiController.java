package br.com.emprestai.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import br.com.emprestai.model.Cliente;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException; // Import ajustado
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmprestAiController {
    private List<Cliente> clientes = new ArrayList<>();
    private Gson gson = new Gson();

    public EmprestAiController() {
        clientes.add(new Cliente("João Silva", 5000.0, LocalDate.of(1990, 5, 20)));
        clientes.add(new Cliente("Maria Souza", 7000.0, LocalDate.of(1985, 10, 15)));
    }

    public static void main(String[] args) throws IOException { // Ajustado para IOException
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        EmprestAiController controller = new EmprestAiController();

        // Usando lambdas para chamar os métodos do controller
        server.createContext("/clientes", exchange -> controller.handleClientes(exchange));
        server.createContext("/cliente", exchange -> controller.handleClienteById(exchange));

        server.setExecutor(null);
        server.start();
        System.out.println("Servidor iniciado na porta 8080");
    }

    private void handleClientes(HttpExchange exchange) throws IOException { // Ajustado para IOException
        if ("GET".equals(exchange.getRequestMethod())) {
            String response = gson.toJson(clientes);
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private void handleClienteById(HttpExchange exchange) throws IOException { // Ajustado para IOException
        if ("GET".equals(exchange.getRequestMethod())) {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.startsWith("id=")) {
                int id = Integer.parseInt(query.split("=")[1]);
                Optional<Cliente> cliente = clientes.stream().filter(c -> c.getClienteId() == id).findFirst();
                String response = cliente.map(gson::toJson).orElse("Cliente não encontrado");
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }
        } else if ("PATCH".equals(exchange.getRequestMethod())) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            Cliente updatedCliente = gson.fromJson(requestBody.toString(), Cliente.class);
            boolean updated = updateCliente(updatedCliente.getClienteId(), updatedCliente.getNome(),
                    updatedCliente.getRemuneracaoLiqMes());
            String response = updated ? "Cliente atualizado" : "Cliente não encontrado";
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        } else if ("DELETE".equals(exchange.getRequestMethod())) {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.startsWith("id=")) {
                int id = Integer.parseInt(query.split("=")[1]);
                boolean deleted = deleteCliente(id);
                String response = deleted ? "Cliente removido" : "Cliente não encontrado";
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
    }

    public boolean updateCliente(int id, String nome, Double remuneracaoLiqMes) {
        for (Cliente cliente : clientes) {
            if (cliente.getClienteId() == id) {
                if (nome != null) {
                    cliente.setNome(nome);
                }
                if (remuneracaoLiqMes != null) {
                    cliente.setRemuneracaoLiqMes(remuneracaoLiqMes);
                }
                return true;
            }
        }
        return false;
    }

    public boolean deleteCliente(int id) {
        return clientes.removeIf(cliente -> cliente.getClienteId() == id);
    }
}