package br.com.emprestai.controller;

import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.service.calculos.CalculadoraEmprestimo;
import br.com.emprestai.service.calculos.CalculoConsignado;
import br.com.emprestai.service.calculos.CalculoPessoal;
import br.com.emprestai.service.elegibilidade.ElegibilidadeConsignado;
import br.com.emprestai.service.elegibilidade.ElegibilidadePessoal;
import br.com.emprestai.util.EmprestimoParams;

import java.util.List;

import static br.com.emprestai.enums.StatusEmprestimoEnum.NEGADO;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;

public class EmprestimoController{
    private final EmprestimoDAO emprestimoDAO;
    private final ClienteDAO clienteDAO;
    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    // Construtor com injeção de dependências
    public EmprestimoController(EmprestimoDAO emprestimoDAO, ClienteDAO clienteDAO) {
        this.emprestimoDAO = emprestimoDAO;
        this.clienteDAO = clienteDAO;
    }

    public Emprestimo post(Emprestimo emprestimo) throws ApiException {
        try {

            if (emprestimo.getStatusEmprestimo() == NEGADO) {
                throw new IllegalStateException("Empréstimo não é elegível para concessão.");
            }

            // Salvar no banco e obter o ID
            Emprestimo idEmprestimo = emprestimoDAO.criar(emprestimo);

            return emprestimo;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao conceder empréstimo: " + e.getMessage(), e);
        }
    }

    public List<Emprestimo> post(List<Emprestimo> entidade) throws ApiException {
        return List.of();
    }

    public List<Emprestimo> get() throws ApiException {
        return List.of();
    }

    public Emprestimo get(Long id) throws ApiException {
        return null;
    }

    public Emprestimo get(String cpf) throws ApiException {
        return null;
    }

    public Emprestimo get(Long id, TipoEmprestimoEnum EmpEnum) throws ApiException {
        Emprestimo emprestimo =  emprestimoDAO.buscarPorIdCliente(id, EmpEnum);
        double valorEmprestimo = emprestimo.getValorTotal() - emprestimo.getValorIOF() - emprestimo.getValorSeguro() - emprestimo.getOutrosCustos();
        emprestimo.setValorEmprestimo(valorEmprestimo);
        System.out.println(emprestimo.getStatusEmprestimo().name());
        return emprestimo;
    }

    public Emprestimo get(Emprestimo emprestimo, Cliente cliente) throws ApiException {
        try {
            // Tipo do Emprestimo
            TipoEmprestimoEnum tipoEmp = emprestimo.getTipoEmprestimo();

            int carenciaEmDias = (int) DAYS.between(emprestimo.getDataContratacao(), emprestimo.getDataInicio());

            // Passo 3: Chamar o processamento específico

            boolean elegivel = switch (tipoEmp) {
                case PESSOAL -> processarEmprestimoPessoal(cliente, emprestimo);
                case CONSIGNADO -> processarEmprestimoConsignado(cliente, emprestimo, carenciaEmDias);
                default -> throw new IllegalArgumentException("Tipo de empréstimo inválido.");
            };

            // Definir se o empréstimo é elegível
            emprestimo.setStatusEmprestimo(!elegivel ? NEGADO: null);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar empréstimo: " + e.getMessage(), e);
        }

        return emprestimo;
    }

    public Emprestimo put(Long id, Emprestimo entidade) throws ApiException {
        return null;
    }

    public Emprestimo put(Long idEmprestimo, Long idEmprestimoOrigem) throws ApiException {
        return emprestimoDAO.atualizarRefin(idEmprestimo, idEmprestimoOrigem);
    }


    public void delete(Long id) throws ApiException {

    }


    private boolean processarEmprestimoPessoal(Cliente cliente, Emprestimo emprestimo) {
        double rendaLiquida = cliente.getRendaMensalLiquida();
        int idade = (int) YEARS.between(cliente.getDataNascimento(), emprestimo.getDataContratacao());

        double taxaJurosMensal = CalculoPessoal.calculoTaxaDeJurosMensal(cliente.getScore());
        emprestimo.setJuros(taxaJurosMensal);
        emprestimo.setTaxaMulta(params.getPercentualMultaAtraso());
        emprestimo.setTaxaJurosMora(params.getPercentualJurosMora());


        CalculadoraEmprestimo.contratoPrice(emprestimo, cliente.getDataNascimento());

        ElegibilidadePessoal.verificarElegibilidadePessoal(
                rendaLiquida,
                emprestimo.getValorParcela(),
                idade,
                emprestimo.getQuantidadeParcelas(),
                cliente.getScore()
        );
        return true;
    }

    private boolean processarEmprestimoConsignado(Cliente cliente, Emprestimo emprestimo, int carenciaEmDias) {
        double rendaLiquida = cliente.getRendaMensalLiquida();
        int idade = (int) YEARS.between(cliente.getDataNascimento(), emprestimo.getDataContratacao());

        // Obter parcelas ativas do cliente
        double parcelasAtivas = 0;

        double taxaJurosMensal = CalculoConsignado.calcularTaxaJurosMensal(emprestimo.getQuantidadeParcelas());
        emprestimo.setJuros(taxaJurosMensal);

        CalculadoraEmprestimo.contratoPrice(emprestimo, cliente.getDataNascimento());

        ElegibilidadeConsignado.verificarElegibilidadeConsignado(
                rendaLiquida,
                emprestimo.getValorParcela(),
                parcelasAtivas,
                idade,
                emprestimo.getQuantidadeParcelas(),
                taxaJurosMensal,
                cliente.getTipoCliente(),
                carenciaEmDias,
                emprestimo.getValorEmprestimo()
        );
        return true;
    }
}