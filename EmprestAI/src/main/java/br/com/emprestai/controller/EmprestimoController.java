package br.com.emprestai.controller;

import br.com.emprestai.enums.TipoEmpEnum;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.service.CalculadoraEmprestimo;
import br.com.emprestai.service.CalculoConsignado;
import br.com.emprestai.service.CalculoPessoal;
import br.com.emprestai.service.SimulacaoEmprestimos;
import br.com.emprestai.service.SimulacaoResultado;

import static br.com.emprestai.enums.StatusEmpEnum.APROVADO;
import static br.com.emprestai.enums.StatusEmpEnum.NEGADO;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;

public class EmprestimoController {
    private final ClienteDAO clienteDAO;
    private final EmprestimoDAO emprestimoDAO;

    // Construtor com injeção de dependências
    public EmprestimoController(ClienteDAO clienteDAO, EmprestimoDAO emprestimoDAO) {
        this.clienteDAO = clienteDAO;
        this.emprestimoDAO = emprestimoDAO;
    }

    public Emprestimo obterEmprestimo(Cliente cliente, Emprestimo emprestimo) {
        try {
            // Tipo do Emprestimo
            TipoEmpEnum tipoEmp = emprestimo.getTipoEmprestimo();

            int carenciaEmDias = (int) DAYS.between(emprestimo.getDataContratacao(), emprestimo.getDataInicio());

            // Passo 3: Chamar o processamento específico
            boolean elegivel = switch (tipoEmp) {
                case PESSOAL -> processarEmprestimoPessoal(cliente, emprestimo);
                case CONSIGNADO -> processarEmprestimoConsignado(cliente, emprestimo, carenciaEmDias);
                default -> throw new IllegalArgumentException("Tipo de empréstimo inválido.");
            };

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar empréstimo: " + e.getMessage(), e);
        }

        return emprestimo;
    }

    // Metodo para conceder o empréstimo
    public Emprestimo salvarEmprestimo(Emprestimo emprestimo) {
        try {

            if (emprestimo.getStatusEmprestimo() == NEGADO) {
                throw new IllegalStateException("Empréstimo não é elegível para concessão.");
            }

            // Salvar no banco e obter o ID
            Emprestimo idEmprestimo = emprestimoDAO.criarEmp(emprestimo);

            return emprestimo;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao conceder empréstimo: " + e.getMessage(), e);
        }
    }

    public SimulacaoResultado SolicitarSimulacaoEmp(Cliente cliente, TipoEmpEnum tipoEmprestimo, double valor, int parcelas) {
        try {   // Verificar se o cliente é válido
            if (cliente == null || !clienteDAO.existeCliente(cliente.getId())) {
                throw new IllegalArgumentException("Cliente inválido ou não encontrado.");
            }
            // Calcular idade do cliente
            int idade = (int) YEARS.between(cliente.getDataNascimento(), java.time.LocalDate.now());

            // Processar simulação com base no tipo de empréstimo
            SimulacaoResultado resultado;
            switch (tipoEmprestimo) {
                case PESSOAL:
                    resultado = SimulacaoEmprestimos.simularEmprestimoPessoal(
                            valor, parcelas, cliente.getScore(), false, // Seguro como false por padrão
                            cliente.getRendaMensalLiquida(), idade
                    );
                    break;
                case CONSIGNADO:
                    resultado = SimulacaoEmprestimos.simularEmprestimoConsignado(
                            valor, parcelas, cliente.getRendaMensalLiquida()
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de empréstimo inválido para simulação.");
            }
            return resultado;
        } catch (IllegalArgumentException e) {
            throw e; // Propagar exceções específicas
        } catch (Exception e) {
            throw new RuntimeException("Erro ao realizar simulação de empréstimo: " + e.getMessage(), e);
        }
    }
}