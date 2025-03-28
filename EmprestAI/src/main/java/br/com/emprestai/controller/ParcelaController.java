package br.com.emprestai.controller;

import br.com.emprestai.dao.ParcelaDAO;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Parcela;

import java.util.List;

public class ParcelaController {
    private final ParcelaDAO parcelaDAO;

    // Construtor que recebe o Emprestimo diretamente
    public ParcelaController(ParcelaDAO parcelaDAO) {
        this.parcelaDAO = parcelaDAO;
    }

    public Parcela post(Parcela entidade) throws ApiException {
        return null;
    }

    public List<Parcela> post(List<Parcela> parcelas) throws ApiException {
        return null;
    }

    public List<Parcela> get() throws ApiException {
        return List.of();
    }

    public Parcela get(Long id) throws ApiException {
        return null;
    }

    public Parcela get(String cpf) throws ApiException {
        return null;
    }

    public Parcela put(Long id, Parcela entidade) throws ApiException {
        return null;
    }

    public void delete(Long id) throws ApiException {

    }
}