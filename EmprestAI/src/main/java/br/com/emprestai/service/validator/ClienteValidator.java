package br.com.emprestai.service.validator;

import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Cliente;

import java.time.LocalDate;

public class ClienteValidator {

    public static void validarClienteParaCriacao(Cliente cliente) throws ApiException {
        if (cliente == null) {
            throw new ApiException("Cliente não pode ser nulo.", 400);
        }
        if (cliente.getCpfCliente() == null || cliente.getCpfCliente().trim().isEmpty()) {
            throw new ApiException("CPF não pode ser nulo ou vazio.", 400);
        }
        if (!isCpfValido(cliente.getCpfCliente())) {
            throw new ApiException("CPF inválido.", 400);
        }
        if (cliente.getNomeCliente() == null || cliente.getNomeCliente().trim().isEmpty()) {
            throw new ApiException("Nome não pode ser nulo ou vazio.", 400);
        }
        if (cliente.getDataNascimento() == null) {
            throw new ApiException("Data de nascimento não pode ser nula.", 400);
        }
        if (cliente.getDataNascimento().isAfter(LocalDate.now().minusYears(18))) {
            throw new ApiException("Cliente deve ter pelo menos 18 anos.", 400);
        }
        if (cliente.getSenha() == null || cliente.getSenha().isEmpty()) {
            throw new ApiException("Senha não pode ser nula ou vazia.", 400);
        }
    }

    public static void validarClienteParaAtualizacao(Cliente cliente) throws ApiException {
        if (cliente == null || cliente.getIdCliente() == null) {
            throw new ApiException("ID do cliente e cliente não podem ser nulos.", 400);
        }
        if (cliente.getCpfCliente() != null && !cliente.getCpfCliente().trim().isEmpty() && !isCpfValido(cliente.getCpfCliente())) {
            throw new ApiException("CPF inválido.", 400);
        }
        if (cliente.getNomeCliente() != null && cliente.getNomeCliente().trim().isEmpty()) {
            throw new ApiException("Nome não pode ser vazio.", 400);
        }
        if (cliente.getDataNascimento() != null && cliente.getDataNascimento().isAfter(LocalDate.now().minusYears(18))) {
            throw new ApiException("Cliente deve ter pelo menos 18 anos.", 400);
        }
        if (cliente.getSenha() != null && cliente.getSenha().isEmpty()) {
            throw new ApiException("Senha não pode ser vazia.", 400);
        }
    }

    public static void validarCpf(String cpf) throws ApiException {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new ApiException("CPF não pode ser nulo ou vazio.", 400);
        }
        if (!isCpfValido(cpf)) {
            throw new ApiException("CPF inválido.", 400);
        }
    }

    public static void validarId(Long id) throws ApiException {
        if (id == null) {
            throw new ApiException("ID do cliente não pode ser nulo.", 400);
        }
    }

    // Método para validar CPF (simplificado, você pode usar uma lógica mais robusta)
    private static boolean isCpfValido(String cpf) {
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() != 11) {
            return false;
        }
        // Aqui você pode adicionar a lógica completa de validação de CPF (dígitos verificadores)
        // Por simplicidade, apenas verificamos o tamanho e se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        return true;
    }
}
