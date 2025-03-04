package br.com.emprestai.controller;

import br.com.emprestai.model.Cliente;
import br.com.emprestai.repository.ClienteRepositoryImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

public class ClienteController implements HttpHandler {
    private final ClienteRepositoryImpl clienteRepo = new ClienteRepositoryImpl();
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        switch (method) {
            case "GET":
                handleGet(exchange, pathParts);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "PATCH":
                handlePatch(exchange, pathParts);
                break;
            case "DELETE":
                handleDelete(exchange, pathParts);
                break;
            default:
                sendResponse(exchange, 405, "Método não permitido");
        }
    }

    private void handleGet(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) { // /clientes
            List<Cliente> clientes = clienteRepo.findAll();
            String response = gson.toJson(clientes);
            sendResponse(exchange, 200, response);
        } else if (pathParts.length == 3) { // /clientes/{id}
            int id = Integer.parseInt(pathParts[2]);
            Optional<Cliente> cliente = clienteRepo.findById(id);
            if (cliente.isPresent()) {
                String response = gson.toJson(cliente.get());
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 404, "Cliente não encontrado");
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        Cliente cliente = gson.fromJson(requestBody, Cliente.class);
        Cliente savedCliente = clienteRepo.save(cliente);
        String response = gson.toJson(savedCliente);
        sendResponse(exchange, 201, response);
    }

    private void handlePatch(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3) {
            int id = Integer.parseInt(pathParts[2]);
            Optional<Cliente> existingCliente = clienteRepo.findById(id);
            if (existingCliente.isPresent()) {
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                Cliente updates = gson.fromJson(requestBody, Cliente.class);
                Cliente cliente = existingCliente.get();

                if (updates.getNome() != null)
                    cliente.setNome(updates.getNome());
                if (updates.getRemuneracaoLiqMes() != 0)
                    cliente.setRemuneracaoLiqMes(updates.getRemuneracaoLiqMes());
                if (updates.getDtNasc() != null)
                    cliente.setIdade(updates.getDtNasc());
                if (updates.getTipoVinculo() != null)
                    cliente.setTipoVinculo(updates.getTipoVinculo());
                if (updates.getRendaPessFamilia() != 0)
                    cliente.setRendaPessFamilia(updates.getRendaPessFamilia());
                if (updates.getQtdePessFamilia() != 0)
                    cliente.setQtdePessFamilia(updates.getQtdePessFamilia());
                if (updates.getScoreCredito() != null)
                    cliente.setScoreCredito(updates.getScoreCredito());

                clienteRepo.save(cliente);
                sendResponse(exchange, 200, gson.toJson(cliente));
            } else {
                sendResponse(exchange, 404, "Cliente não encontrado");
            }
        }
    }

    private void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3) {
            int id = Integer.parseInt(pathParts[2]);
            Optional<Cliente> cliente = clienteRepo.findById(id);
            if (cliente.isPresent()) {
                clienteRepo.deleteById(id);
                sendResponse(exchange, 204, "");
            } else {
                sendResponse(exchange, 404, "Cliente não encontrado");
            }
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        if (response.isEmpty()) {
            exchange.sendResponseHeaders(statusCode, -1);
        } else {
            byte[] responseBytes = response.getBytes();
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }
}