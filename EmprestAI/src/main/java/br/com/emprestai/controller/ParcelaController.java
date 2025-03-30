package br.com.emprestai.controller;

import br.com.emprestai.dao.ParcelaDAO;
import br.com.emprestai.enums.StatusParcelaEnum;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
import br.com.emprestai.service.calculos.CalculadoraEmprestimo;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static br.com.emprestai.enums.StatusParcelaEnum.PAGA;

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
        if (parcela == null) {
            throw new ApiException("Parcela não pode ser nula.", 400); // Bad Request
        }
        return parcelaDAO.criar(parcela);
    }

    // GET - Buscar parcelas por empréstimo
    public List<Parcela> getParcelasByEmprestimo(Emprestimo emprestimo) throws ApiException, SQLException {
        if (emprestimo == null) {
            throw new ApiException("ID do empréstimo e tipo de empréstimo não podem ser nulos.", 400); // Bad Request
        }

        emprestimo.setParcelaList(parcelaDAO.buscarParcelasPorEmprestimo(emprestimo.getIdEmprestimo()));
        return CalculadoraEmprestimo.processarValoresParcela(emprestimo);
    }

    // PUT - Atualizar uma lista de parcelas (exemplo: pagar várias parcelas)
    public List<Parcela> putListParcelas(List<Parcela> parcelas) throws ApiException, SQLException, IOException {
        if (parcelas == null || parcelas.isEmpty()) {
            throw new ApiException("Lista de parcelas não pode ser nula ou vazia.", 400); // Bad Request
        }

        for (Parcela p : parcelas){
            p.setStatusParcela(PAGA);
            p.setDataPagamento(LocalDate.now());
        }
        return parcelaDAO.pagarParcelas(parcelas);
    }
}