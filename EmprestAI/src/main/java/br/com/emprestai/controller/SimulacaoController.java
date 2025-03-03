package br.com.emprestai.controller;

import br.com.emprestai.model.Cliente;
import br.com.emprestai.repository.ClienteRepositoryImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class SimulacaoController implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
