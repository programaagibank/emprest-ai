package br.com.emprestai.controller;

import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.enums.StatusEmprestimoEnum;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.exception.ElegibilidadeException;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.service.calculos.CalculadoraContrato;
import br.com.emprestai.service.calculos.CalculoTaxaJuros;
import br.com.emprestai.service.elegibilidade.ElegibilidadeCliente;
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
    private final ClienteDAO clienteDAO;
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
            emprestimo.setStatusEmprestimo(StatusEmprestimoEnum.ABERTO);
            emprestimo.setTaxaMulta(params.getPercentualMultaAtraso());
            emprestimo.setTaxaJurosMora(params.getPercentualJurosMora());

            Emprestimo idEmprestimo = emprestimoDAO.criar(emprestimo);
            return idEmprestimo;
        } catch (Exception e) {
            throw new ApiException("Erro ao conceder empréstimo: " + e.getMessage(), 500); // Internal Server Error
        }
    }

    // GET - Buscar empréstimo por ID do cliente e tipo
    public List<Emprestimo> getByCliente(Long id, TipoEmprestimoEnum EmpEnum) throws ApiException {
        List<Emprestimo> emprestimos = emprestimoDAO.buscarPorIdCliente(id, EmpEnum);
        for (Emprestimo e : emprestimos) {
            double valorEmprestimo = e.getValorTotal() - e.getValorIOF() -
                    e.getValorSeguro() - e.getOutrosCustos();
            e.setValorEmprestimo(valorEmprestimo);
        }
        return emprestimos;
    }

    // GET - Buscar todos os empréstimos
    public List<Emprestimo> getTodos() throws ApiException {
        return emprestimoDAO.buscarTodos();
    }

    // GET - Calcular contrato Price e verificar elegibilidade
    public Emprestimo getPrice(Emprestimo emprestimo, Cliente cliente) throws ApiException, ElegibilidadeException {
        try {
            processarEmprestimo(emprestimo, cliente);
            verificarElegibilidade(emprestimo, cliente);
            return emprestimo;
        } catch (ApiException | ElegibilidadeException e) {
            emprestimo.setStatusEmprestimo(NEGADO);
            throw e;
        } catch (Exception e) {
            throw new ApiException("Erro ao processar empréstimo: " + e.getMessage(), 500);
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
    private void processarEmprestimo(Emprestimo emprestimo, Cliente cliente) throws ApiException {
        double taxaJuros = emprestimo.getTipoEmprestimo() == TipoEmprestimoEnum.PESSOAL
                ? CalculoTaxaJuros.calculoTaxaDeJurosMensal(cliente.getScore())
                : CalculoTaxaJuros.calcularTaxaJurosMensal(emprestimo.getQuantidadeParcelas());

        emprestimo.setTaxaJuros(taxaJuros);
        CalculadoraContrato.contratoPrice(emprestimo, cliente.getDataNascimento());
        emprestimo.setTaxaMulta(params.getPercentualMultaAtraso());
        emprestimo.setTaxaJurosMora(params.getPercentualJurosMora());
    }

    private void verificarElegibilidade(Emprestimo emprestimo, Cliente cliente) throws ApiException, ElegibilidadeException {
        ElegibilidadeCliente elegibilidadeCliente = new ElegibilidadeCliente(cliente);
        int idade = (int) YEARS.between(cliente.getDataNascimento(), emprestimo.getDataContratacao());
        int carencia = (int) DAYS.between(emprestimo.getDataContratacao(), emprestimo.getDataInicio());

        boolean elegivelGeral = emprestimo.getTipoEmprestimo() == TipoEmprestimoEnum.PESSOAL
                ? elegibilidadeCliente.isElegivelPessoal()
                : elegibilidadeCliente.isElegivelConsignado();

        if (!elegivelGeral) {
            throw new ApiException("Cliente não elegível para o tipo de empréstimo", 400);
        }

        if (emprestimo.getTipoEmprestimo() == TipoEmprestimoEnum.PESSOAL) {
            ElegibilidadePessoal.verificarElegibilidadePessoal(
                    emprestimo.getValorParcela(), idade, emprestimo.getQuantidadeParcelas(),
                    cliente.getScore(), emprestimo.getTaxaJuros(), carencia, emprestimo.getValorEmprestimo()
            );
        } else {
            ElegibilidadeConsignado.verificarElegibilidadeConsignado(
                    emprestimo.getValorParcela(), idade, emprestimo.getQuantidadeParcelas(),
                    emprestimo.getTaxaJuros(), carencia, emprestimo.getValorEmprestimo()
            );
        }
    }
}