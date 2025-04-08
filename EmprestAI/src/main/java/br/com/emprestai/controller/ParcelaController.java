package br.com.emprestai.controller;

import br.com.emprestai.dao.ParcelaDAO;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
import br.com.emprestai.service.calculos.CalculadoraParcela;
import br.com.emprestai.service.validator.ParcelaValidator;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static br.com.emprestai.enums.StatusParcelaEnum.ATRASADA;

public class ParcelaController {

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private final ParcelaDAO parcelaDAO;

    // --------------------------------------------------------------------------------
    // Constructor
    // --------------------------------------------------------------------------------
    public ParcelaController(ParcelaDAO parcelaDAO) {
        this.parcelaDAO = parcelaDAO;
    }

    // --------------------------------------------------------------------------------
    // CRUD Methods
    // --------------------------------------------------------------------------------

    // POST - Criar uma única parcela
    public Parcela postParcela(Parcela parcela) throws ApiException, SQLException, IOException {
        ParcelaValidator.validarAntesDeSalvar(parcela);
        return parcelaDAO.criar(parcela);
    }

    // GET - Buscar parcelas por empréstimo
    public List<Parcela> getParcelasByEmprestimo(Emprestimo emprestimo) throws ApiException, SQLException {
        if (emprestimo == null) {
            throw new ApiException("Empréstimo não pode ser nulo.", 400);
        }
        ParcelaValidator.validarIdEmprestimo(emprestimo.getIdEmprestimo());
        emprestimo.setParcelaList(parcelaDAO.buscarParcelasPorEmprestimo(emprestimo.getIdEmprestimo()));
        if (emprestimo.getParcelaList().isEmpty()) {
            throw new ApiException("Nenhuma parcela encontrada para o empréstimo com ID: " + emprestimo.getIdEmprestimo(), 404);
        }
        return CalculadoraParcela.processarValoresParcela(emprestimo);
    }

    // PUT - Atualizar uma lista de parcelas (exemplo: pagar várias parcelas)
    public List<Parcela> putListParcelas(List<Parcela> parcelas) throws ApiException, SQLException, IOException {
        ParcelaValidator.validarListaParcelas(parcelas);
        List<Parcela> parcelasOriginais = parcelaDAO.buscarParcelasPorEmprestimo(parcelas.getFirst().getIdEmprestimo());
        if (parcelasOriginais.isEmpty()) {
            throw new ApiException("Nenhuma parcela original encontrada para o empréstimo com ID: " + parcelas.getFirst().getIdEmprestimo(), 404);
        }
        boolean atrasada = parcelasOriginais.stream().anyMatch(p -> p.getStatus() == ATRASADA);
        return parcelaDAO.pagarParcelas(parcelas);
    }

    // GET - Buscar últimas parcelas não pagas
    public List<Parcela> getUltimasNaoPagas(Long clientId, TipoEmprestimoEnum tipoEmprestimo) throws SQLException, ApiException {
        ParcelaValidator.validarIdCliente(clientId);
        ParcelaValidator.validarTipoEmprestimo(tipoEmprestimo);
        List<Parcela> parcelas = parcelaDAO.BuscarUltimasNaoPagas(clientId, tipoEmprestimo);
        if (parcelas.isEmpty()) {
            throw new ApiException("Nenhuma parcela não paga encontrada para o cliente com ID: " + clientId + " e tipo de empréstimo: " + tipoEmprestimo, 404);
        }
        return parcelas;
    }
}