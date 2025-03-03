package br.com.emprestai;

import br.com.emprestai.server.Servidor;

public class Main {
    public static void main(String[] args) {
        try {
            Servidor.iniciar();
        } catch (Exception e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}
