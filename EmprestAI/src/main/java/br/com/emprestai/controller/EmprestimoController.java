package br.com.emprestai.controller;

import br.com.emprestai.enums.TipoEmpEnum;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.service.calculos.CalculadoraEmprestimo;
import br.com.emprestai.service.calculos.CalculoConsignado;
import br.com.emprestai.service.calculos.CalculoPessoal;
import br.com.emprestai.service.elegibilidade.ElegibilidadeConsignado;
import br.com.emprestai.service.elegibilidade.ElegibilidadePessoal;
import br.com.emprestai.util.EmprestimoParams;

import static br.com.emprestai.enums.StatusEmpEnum.APROVADO;
import static br.com.emprestai.enums.StatusEmpEnum.NEGADO;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;

public class EmprestimoController {
    private final EmprestimoDAO emprestimoDAO;
    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    // Construtor com injeção de dependências
    public EmprestimoController(EmprestimoDAO emprestimoDAO) {
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

            // Definir se o empréstimo é elegível
            emprestimo.setStatusEmprestimo(elegivel ? APROVADO : NEGADO);

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