package br.com.emprestai.controller;

import br.com.emprestai.dao.ParcelaDAO;
import br.com.emprestai.enums.TipoEmpEnum;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Parcela;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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

    public List<Parcela> post(List<Parcela> parcelas) throws ApiException {
        return null;
    }

    // Ajustado para receber o tipo de empréstimo e o número total de parcelas, se necessário
    public List<Parcela> get(Long idEmprestimo, TipoEmpEnum empEnum) throws ApiException, SQLException {
        // Busca todas as parcelas do DAO
        List<Parcela> parcelas = parcelaDAO.buscarParcelasPorEmprestimoETipo(idEmprestimo, empEnum);
        List<Parcela> parcelasOrdenadas = new ArrayList<>();

        LocalDate today = LocalDate.now();

        if (empEnum == TipoEmpEnum.CONSIGNADO) {
            // Ordem decrescente: 32 → 1
            parcelasOrdenadas.addAll(parcelas);
            parcelasOrdenadas.sort(Comparator.comparingInt(Parcela::getNumeroParcela).reversed());
        } else if (empEnum == TipoEmpEnum.PESSOAL) {
            // Encontrar a primeira parcela dentro de 30 dias
            Parcela firstValidParcela = null;
            for (Parcela p : parcelas) {
                if (p.getDataVencimento().isAfter(today.minusDays(1))) {
                    firstValidParcela = p;
                    break;
                }
            }
            // Se não houver parcela válida, usar a primeira da lista original
            if (firstValidParcela == null && !parcelas.isEmpty()) {
                firstValidParcela = parcelas.get(0);
            }

            // Adicionar a primeira válida no topo
            if (firstValidParcela != null) {
                parcelasOrdenadas.add(firstValidParcela);
            }

            // Adicionar as demais em ordem decrescente, excluindo a já adicionada
            List<Parcela> tempParcelas = new ArrayList<>(parcelas);
            if (firstValidParcela != null) {
                tempParcelas.remove(firstValidParcela);
            }
            tempParcelas.sort(Comparator.comparingInt(Parcela::getNumeroParcela).reversed());
            parcelasOrdenadas.addAll(tempParcelas);
        }

        return parcelasOrdenadas;
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