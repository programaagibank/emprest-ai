
package br.com.emprestai.controller;

import br.com.emprestai.enums.TipoEmpEnum;
import br.com.emprestai.dao.ClienteDAO;
//import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.service.CalculadoraEmprestimo;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class EmprestimoController {
    private ClienteDAO clienteDAO = new ClienteDAO();
    //private EmprestimoDAO emprestimoDAO = new EmprestimoDAO();

    // Método genérico para obter os dados do empréstimo (antigo simularEmprestimo)
    public Map<String, Object> obterEmprestimo(Long idCliente, double valorEmprestimo, TipoEmpEnum tipoEmp,
                                               int parcelas, boolean contratarSeguro, LocalDate dataInicioPagamento) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Passo 1: Buscar o cliente
            Cliente cliente = clienteDAO.buscarPorId(idCliente);
            if (cliente == null) {
                throw new IllegalArgumentException("Erro: Cliente não encontrado");
            }

            // Passo 2: Definir taxa de juros
            //TODO taxa de juros será retornado pelos metodos do service, eu passaria informações do cliente e retornaria taxa de juros

            double taxaJurosMensal = 1.80;

            // Passo 3: Calcular o contrato usando CalculadoraEmprestimo
            Map<String, Object> contrato = CalculadoraEmprestimo.contratoPrice(
                    valorEmprestimo, parcelas, taxaJurosMensal, LocalDate.now(),
                    cliente.getData_nascimento(), contratarSeguro);

            // Passo 4: Montar a resposta
            response.put("idCliente", idCliente);
            response.putAll(contrato);
            response.put("cliente", cliente);

        } catch (IllegalArgumentException e) {
            response.put("mensagem", e.getMessage());
        } catch (Exception e) {
            response.put("mensagem", "Tentativa de obter empréstimo falhou: " + e.getMessage());
        }

        return response;
    }

    // Método para conceder o empréstimo, reutilizando obterEmprestimo
    public Map<String, Object> concederEmprestimo(Long idCliente, double valorEmprestimo, TipoEmpEnum tipoEmp,
                                                  int parcelas, boolean contratarSeguro, LocalDate dataInicioPagamento) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Passo 1: Obter os dados do empréstimo
            Map<String, Object> dadosEmprestimo = obterEmprestimo(idCliente, valorEmprestimo, tipoEmp, parcelas,
                    contratarSeguro, dataInicioPagamento);

            // Passo 2: Criar o objeto Emprestimo para salvar no banco
            //TODO criar um emprestimo

            // Passo 3: Salvar no banco
            // Long idEmprestimo = emprestimoDAO.salvar(emprestimo);
            //emprestimo.setId(idEmprestimo);

            long idEmprestimo = 0;
            // Passo 4: Montar a resposta com informações adicionais
            response.putAll(dadosEmprestimo); // Copia todos os dados obtidos
            response.put("idEmprestimo", idEmprestimo);
            response.put("dataConcessao", LocalDate.now());

        } catch (Exception e) {
            response.put("mensagem", "Erro ao conceder o empréstimo: " + e.getMessage());
        }

        return response;
    }


}