package br.com.emprestai.controller;

import br.com.emprestai.dao.ParcelaDAO;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Parcela;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ParcelaController {
    private final ParcelaDAO parcelaDAO;

    // Construtor que recebe o Emprestimo diretamente
    public ParcelaController(ParcelaDAO parcelaDAO) {
        this.parcelaDAO = parcelaDAO;
    }

    public Parcela post(Parcela parcela) throws ApiException {
        return null;
    }

    public List<Parcela> post(List<Parcela> parcelas) throws ApiException, SQLException, IOException {
        return parcelaDAO.pagarParcelas(parcelas);
    }

    public List<Parcela> get(Long idEmprestimo, TipoEmprestimoEnum empEnum) throws ApiException, SQLException {
        // Busca todas as parcelas do DAO
        return parcelaDAO.buscarParcelasPorEmprestimoETipo(idEmprestimo, empEnum);
    }

    public Parcela get(Long id) throws ApiException {
        return null;
    }

    public Parcela get(String cpf) throws ApiException {
        return null;
    }

    public List<Parcela> put(List<Parcela> parcelas) throws ApiException {
        List<Parcela> parcelasAtualizadas = List.of();
        for (int i = 0; i < parcelas.size() ; i++) {
            parcelasAtualizadas.add( parcelaDAO.pagarParcela(parcelas.get(i)));
        }
        return parcelas;
    }

    public void delete(Long id) throws ApiException {

    }
}