package br.com.emprestai.controller;

import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.enums.StatusEmprestimoEnum;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.exception.ElegibilidadeException;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.service.ClienteService;
import br.com.emprestai.service.calculos.CalculadoraContrato;
import br.com.emprestai.service.calculos.CalculoTaxaJuros;
import br.com.emprestai.service.elegibilidade.ElegibilidadeCliente;
import br.com.emprestai.service.validator.ValidatorConsignado;
import br.com.emprestai.service.validator.ValidatorPessoal;
import br.com.emprestai.util.EmprestimoParams;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static br.com.emprestai.enums.StatusEmprestimoEnum.NEGADO;

public class EmprestimoController {

    private final EmprestimoDAO emprestimoDAO;
    private final ClienteDAO clienteDAO;
    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    public EmprestimoController(EmprestimoDAO emprestimoDAO, ClienteDAO clienteDAO) {
        this.emprestimoDAO = emprestimoDAO;
        this.clienteDAO = clienteDAO;
    }

    public Emprestimo postEmprestimo(Emprestimo emprestimo) throws ApiException {
        if (emprestimo.getStatusEmprestimo() == NEGADO) {
            throw new ApiException("Empréstimo não é elegível para concessão.", 400);
        }
        emprestimo.setStatusEmprestimo(StatusEmprestimoEnum.ABERTO);
        emprestimo.setTaxaMulta(params.getPercentualMultaAtraso());
        emprestimo.setTaxaJurosMora(params.getPercentualJurosMora());
        return emprestimoDAO.criar(emprestimo);
    }

    public List<Emprestimo> getByClienteTipoEmprestimo(Long id, TipoEmprestimoEnum empEnum) throws ApiException {
        try {
            List<Emprestimo> emprestimos = emprestimoDAO.buscarPorIdClienteEmprestimo(id, empEnum);
            return emprestimos;
        } catch (Exception e) {
            throw new ApiException("Erro ao buscar empréstimos: " + e.getMessage(), 500);
        }
    }

    public List<Emprestimo> getTodos() throws ApiException {
        try {
            return emprestimoDAO.buscarTodos();
        } catch (Exception e) {
            throw new ApiException("Erro ao buscar todos os empréstimos: " + e.getMessage(), 500);
        }
    }

    public Emprestimo getPrice(Emprestimo emprestimo, Cliente cliente) throws ApiException, ElegibilidadeException {
        processarEmprestimo(emprestimo, cliente);
        verificarElegibilidade(emprestimo, cliente);
        return emprestimo;
    }

    public Emprestimo put(Long idEmprestimo, Long idEmprestimoOrigem) throws ApiException {
        try {
            return emprestimoDAO.atualizarRefin(idEmprestimo, idEmprestimoOrigem);
        } catch (Exception e) {
            throw new ApiException("Erro ao atualizar refinanciamento: " + e.getMessage(), 500);
        }
    }

    private void processarEmprestimo(Emprestimo emprestimo, Cliente cliente) throws ApiException {
        try {
            double taxaJuros = CalculoTaxaJuros.calcularTaxaJurosMensal(cliente.getScore(),
                    emprestimo.getQuantidadeParcelas(), emprestimo.getTipoEmprestimo());
            emprestimo.setTaxaJuros(taxaJuros);
            CalculadoraContrato.contratoPrice(emprestimo, cliente.getIdade());
            emprestimo.setTaxaMulta(params.getPercentualMultaAtraso());
            emprestimo.setTaxaJurosMora(params.getPercentualJurosMora());
        } catch (Exception e) {
            throw new ApiException("Erro ao processar cálculos do empréstimo: " + e.getMessage(), 500);
        }
    }

    private void verificarElegibilidade(Emprestimo emprestimo, Cliente cliente) throws ApiException, ElegibilidadeException {
        ElegibilidadeCliente elegibilidadeCliente = new ElegibilidadeCliente(cliente);
        boolean elegivelGeral = emprestimo.getTipoEmprestimo() == TipoEmprestimoEnum.PESSOAL
                ? elegibilidadeCliente.isElegivelPessoal()
                : elegibilidadeCliente.isElegivelConsignado();

        if (!elegivelGeral) {
            emprestimo.setStatusEmprestimo(NEGADO);
            throw new ApiException("Cliente não elegível para o tipo de empréstimo", 400);
        }

        if (emprestimo.getTipoEmprestimo() == TipoEmprestimoEnum.PESSOAL) {
            ValidatorPessoal.verificarElegibilidadePessoal(cliente, emprestimo);
        } else {
            ValidatorConsignado.verificarElegibilidadeConsignado(cliente, emprestimo);
        }
    }

    public List<Emprestimo> gerarOfertasEmprestimo(double valorSolicitado, TipoEmprestimoEnum tipoEmprestimo, Cliente cliente)
            throws ApiException, ElegibilidadeException {
        List<Emprestimo> ofertasValidas = new ArrayList<>();

        // Verificar elegibilidade geral
        ElegibilidadeCliente elegibilidade = new ElegibilidadeCliente(cliente);
        boolean elegivel = tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO
                ? elegibilidade.isElegivelConsignado()
                : elegibilidade.isElegivelPessoal();
        if (!elegivel) {
            throw new ElegibilidadeException("Cliente não elegível para o tipo de empréstimo solicitado.");
        }

        // Calcular limite de crédito e margem disponível
        double limiteCredito = tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO
                ? ClienteService.calcularLimiteCreditoConsignado(cliente)
                : ClienteService.calcularLimiteCreditoPessoal(cliente);
        if (valorSolicitado > limiteCredito) {
            throw new ApiException("Valor solicitado (R$" + valorSolicitado + ") excede o limite de crédito (R$" + limiteCredito + ").", 400);
        }

        // Definir prazos possíveis com base no cliente
        int prazoMaximo = tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO
                ? ClienteService.calcularPrazoMaximoConsignado(cliente)
                : ClienteService.calcularPrazoMaximoPessoal(cliente);
        int prazoMinimo = tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO
                ? params.getPrazoMinimoConsignado()
                : params.getPrazoMinimoPessoal();
        int carenciaMaxima = tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO
                ? params.getCarenciaMaximaConsignado()
                : params.getCarenciaMaximaPessoal();
        int[] prazosPossiveis = gerarPrazosViaveis(prazoMinimo, prazoMaximo, tipoEmprestimo);

        // Gerar ofertas para cada prazo
        for (int parcelas : prazosPossiveis) {
            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setValorEmprestimo(valorSolicitado);
            System.out.println(parcelas);
            emprestimo.setQuantidadeParcelas(parcelas);
            emprestimo.setTipoEmprestimo(tipoEmprestimo);
            emprestimo.setDataContratacao(LocalDate.now());
            emprestimo.setDataInicio(LocalDate.now().plusDays(carenciaMaxima)); // Padrão, pode ser ajustado
            emprestimo.setContratarSeguro(true); // Padrão, pode ser ajustado

            try {
                // Calcular taxa de juros e contrato
                double taxaJuros = CalculoTaxaJuros.calcularTaxaJurosMensal(cliente.getScore(), parcelas, tipoEmprestimo);
                emprestimo.setTaxaJuros(Math.min(taxaJuros, tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO
                        ? params.getJurosMaximoConsignado() : params.getJurosMaximoPessoal()));
                CalculadoraContrato.contratoPrice(emprestimo, cliente.getIdade());
                emprestimo.setTaxaMulta(params.getPercentualMultaAtraso());
                emprestimo.setTaxaJurosMora(params.getPercentualJurosMora());

                // Validar oferta
                if (tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO) {
                    ValidatorConsignado.verificarElegibilidadeConsignado(cliente, emprestimo);
                } else {
                    ValidatorPessoal.verificarElegibilidadePessoal(cliente, emprestimo);
                }

                ofertasValidas.add(emprestimo);
            } catch (Exception e) {
                // Ignorar prazos inválidos (ex.: margem excedida, idade final inválida)
                System.out.println(e.getMessage());
                continue;
            }
        }

        if (ofertasValidas.isEmpty()) {
            throw new ApiException("Nenhuma oferta válida encontrada para os parâmetros fornecidos.", 400);
        }

        return ofertasValidas;
    }

    private int[] gerarPrazosViaveis(int prazoMinimo, int prazoMaximo, TipoEmprestimoEnum tipoEmprestimo) {
        List<Integer> prazos = new ArrayList<>();
        int incremento = 0;
        if(tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO){
            incremento = 12;
        } else {
            incremento = 4;
        }
        for (int i = prazoMinimo; i <= prazoMaximo; i += incremento) { // Incremento de 12 meses
            prazos.add(i);
        }
        if (!prazos.contains(prazoMaximo) && prazoMaximo >= prazoMinimo) {
            prazos.add(prazoMaximo);
        }
        return prazos.stream().mapToInt(Integer::intValue).toArray();
    }
}