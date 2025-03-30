package br.com.emprestai.controller;

import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.enums.TipoEmprestimoEnum;
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

public class EmprestimoController {

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private final EmprestimoDAO emprestimoDAO;
    private final ClienteDAO    clienteDAO;
    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    // --------------------------------------------------------------------------------
    // Constructor
    // --------------------------------------------------------------------------------
    public EmprestimoController(EmprestimoDAO emprestimoDAO, ClienteDAO clienteDAO) {
        this.emprestimoDAO = emprestimoDAO;
        this.clienteDAO = clienteDAO;
    }

    // --------------------------------------------------------------------------------
    // CRUD Methods
    // --------------------------------------------------------------------------------

    // POST - Salvar um novo empréstimo
    public Emprestimo postEmprestimo(Emprestimo emprestimo) throws ApiException {
        try {
            if (emprestimo.getStatusEmprestimo() == NEGADO) {
                throw new ApiException("Empréstimo não é elegível para concessão.", 400); // Bad Request
            }

            emprestimo.setTaxaMulta(params.getPercentualMultaAtraso());
            emprestimo.setTaxaJurosMora(params.getPercentualJurosMora());

            Emprestimo idEmprestimo = emprestimoDAO.criar(emprestimo);
            return idEmprestimo;
        } catch (Exception e) {
            throw new ApiException("Erro ao conceder empréstimo: " + e.getMessage(), 500); // Internal Server Error
        }
    }

    // GET - Buscar empréstimo por ID do cliente e tipo
    public Emprestimo getByCliente(Long id, TipoEmprestimoEnum EmpEnum) throws ApiException {
        Emprestimo emprestimo = emprestimoDAO.buscarPorIdCliente(id, EmpEnum);
        double valorEmprestimo = emprestimo.getValorTotal() - emprestimo.getValorIOF() -
                emprestimo.getValorSeguro() - emprestimo.getOutrosCustos();
        emprestimo.setValorEmprestimo(valorEmprestimo);
        System.out.println(emprestimo.getStatusEmprestimo().name());
        return emprestimo;
    }

    // GET - Buscar todos os empréstimos
    public List<Emprestimo> getTodos() throws ApiException {
        return emprestimoDAO.buscarTodos();
    }

    // GET - Calcular contrato Price e verificar elegibilidade
    public Emprestimo getPrice(Emprestimo emprestimo, Cliente cliente) throws ApiException {
        try {
            TipoEmprestimoEnum tipoEmp = emprestimo.getTipoEmprestimo();

            boolean elegivel = switch (tipoEmp) {
                case PESSOAL -> processarEmprestimoPessoal(cliente, emprestimo);
                case CONSIGNADO -> processarEmprestimoConsignado(cliente, emprestimo);
                default -> throw new ApiException("Tipo de empréstimo inválido.", 400); // Bad Request
            };

            if (!elegivel) {
                emprestimo.setStatusEmprestimo(NEGADO);
                throw new ApiException("Empréstimo não aprovado devido à elegibilidade", 400); // Bad Request
            }

            return emprestimo;
        } catch (Exception e) {
            throw new ApiException("Erro ao processar empréstimo: " + e.getMessage(), 500); // Internal Server Error
        }
    }

    // PUT - Atualizar empréstimo como refinanciamento
    public Emprestimo put(Long idEmprestimo, Long idEmprestimoOrigem) throws ApiException {
        return emprestimoDAO.atualizarRefin(idEmprestimo, idEmprestimoOrigem);
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------

    // Processar cálculos e elegibilidade para empréstimo pessoal
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

    // Processar cálculos e elegibilidade para empréstimo consignado
    private boolean processarEmprestimoConsignado(Cliente cliente, Emprestimo emprestimo) {
        double rendaLiquida = cliente.getRendaMensalLiquida();
        int idade = (int) YEARS.between(cliente.getDataNascimento(), emprestimo.getDataContratacao());
        int carenciaEmDias = (int) DAYS.between(emprestimo.getDataContratacao(), emprestimo.getDataInicio());

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