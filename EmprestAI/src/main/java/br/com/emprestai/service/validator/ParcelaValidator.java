package br.com.emprestai.service.validator;

import br.com.emprestai.enums.StatusParcelaEnum;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Parcela;

import java.time.LocalDate;
import java.util.List;

public class ParcelaValidator {

    public static void validarAntesDeSalvar(Parcela parcela) throws ApiException {
        if (parcela == null) {
            throw new ApiException("Parcela não pode ser nula.", 400);
        }
        if (parcela.getIdEmprestimo() <= 0) {
            throw new ApiException("ID do empréstimo deve ser um valor positivo.", 400);
        }
        if (parcela.getNumeroParcela() <= 0) {
            throw new ApiException("Número da parcela deve ser maior que zero.", 400);
        }
        if (parcela.getDataVencimento() == null) {
            throw new ApiException("Data de vencimento não pode ser nula.", 400);
        }
        if (parcela.getValorPago() < 0) {
            throw new ApiException("Valor pago não pode ser negativo.", 400);
        }
        if (parcela.getStatus() == null) {
            throw new ApiException("Status da parcela não pode ser nulo.", 400);
        }
    }

    public static void validarIdEmprestimo(Long idEmprestimo) throws ApiException {
        if (idEmprestimo == null || idEmprestimo <= 0) {
            throw new ApiException("ID do empréstimo deve ser um valor positivo.", 400);
        }
    }

    public static void validarListaParcelas(List<Parcela> parcelas) throws ApiException {
        if (parcelas == null || parcelas.isEmpty()) {
            throw new ApiException("Lista de parcelas não pode ser nula ou vazia.", 400);
        }
        for (Parcela parcela : parcelas) {
            if (parcela.getIdParcela() <= 0) {
                throw new ApiException("ID da parcela deve ser um valor positivo.", 400);
            }
            if (parcela.getValorPresenteParcela() <= 0) {
                throw new ApiException("Valor presente da parcela deve ser maior que zero.", 400);
            }
            if (parcela.getMulta() < 0) {
                throw new ApiException("Multa não pode ser negativa.", 400);
            }
            if (parcela.getJurosMora() < 0) {
                throw new ApiException("Juros mora não pode ser negativo.", 400);
            }
        }
    }

    public static void validarIdCliente(Long clientId) throws ApiException {
        if (clientId == null || clientId <= 0) {
            throw new ApiException("ID do cliente deve ser um valor positivo.", 400);
        }
    }

    public static void validarTipoEmprestimo(TipoEmprestimoEnum tipoEmprestimo) throws ApiException {
        if (tipoEmprestimo == null) {
            throw new ApiException("Tipo de empréstimo não pode ser nulo.", 400);
        }
    }
}
