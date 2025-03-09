package br.com.emprestai;

import br.com.emprestai.controller.clienteController.ClienteController;
import br.com.emprestai.dataBaseConfig.exception.ApiException;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) {

        // Instanciar o ClienteController
        ClienteController clienteController = new ClienteController();

        // Inicialização do Javalin
        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false; // impede que o banner do javalin não apareça no console.
        }).start(4567);

        // Configurar tratamento de exceções
        app.exception(ApiException.class, ClienteController::handleApiException);

        // Configurar tratamento de outras exceções
        app.exception(Exception.class, (e, ctx) -> { //ctx == context
            Map<String, Object> error = new HashMap<>();
            error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.getCode());
            error.put("mensagem", "Erro interno do servidor: " + e.getMessage());

            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.json(error);
        });

        // Configurar rotas para Cliente
        app.post("/api/clientes", clienteController::criarCliente);
        app.get("/api/clientes", clienteController::buscarTodosClientes);
        app.get("/api/clientes/{id}", clienteController::buscarClientePorId);
        app.put("/api/clientes/{id}", clienteController::atualizarCliente);
        app.patch("/api/clientes/{id}", clienteController::atualizarClienteParcial);
        app.delete("/api/clientes/{id}", clienteController::excluirCliente);

        // Rota inicial para verificar se o servidor está ativo
        app.get("/", ctx -> { //ctx == context
            ctx.json(Map.of("mensagem", "API de Gerenciamento de Empréstimos ativa!"));
        });

        System.out.println("Servidor iniciado na porta 4567");
    }
}
