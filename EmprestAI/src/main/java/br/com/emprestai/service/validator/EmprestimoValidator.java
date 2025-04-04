package br.com.emprestai.service.validator;

import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.util.EmprestimoParams;

import java.math.BigDecimal;
import java.time.LocalDate;

import static br.com.emprestai.enums.TipoEmprestimoEnum.CONSIGNADO;

public class EmprestimoValidator {

    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    public static void validarAntesDeSalvar(Emprestimo emprestimo) throws ApiException {
        // Campos obrigatórios
        if (emprestimo.getValorEmprestimo() <= 0) {
            throw new ApiException("O valor do empréstimo é obrigatório e deve ser positivo.", 400);
        }
        if (emprestimo.getQuantidadeParcelas() <= 0) {
            throw new ApiException("A quantidade de parcelas deve ser maior que zero.", 400);
        }
        if (emprestimo.getTaxaJuros() < 0) {
            throw new ApiException("A taxa de juros não pode ser negativa.", 400);
        }
        if (emprestimo.getDataContratacao() == null) {
            throw new ApiException("A data de contratação é obrigatória.", 400);
        }
        if (emprestimo.getDataInicio() == null) {
            throw new ApiException("A data de início do pagamento é obrigatória.", 400);
        }
        if (emprestimo.getTipoEmprestimo() == null) {
            throw new ApiException("O tipo de empréstimo é obrigatório.", 400);
        }

        // Validações de consistência de datas
        if (emprestimo.getDataContratacao().isAfter(LocalDate.now())) {
            throw new ApiException("A data de contratação não pode ser futura.", 400);
        }
        if (emprestimo.getDataInicio().isBefore(emprestimo.getDataContratacao())) {
            throw new ApiException("A data de início deve ser posterior à data de contratação.", 400);
        }

        // Validações numéricas
        BigDecimal valorEmprestimo = BigDecimal.valueOf(emprestimo.getValorEmprestimo());
        if (valorEmprestimo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException("O valor do empréstimo deve ser maior que zero.", 400);
        }
        if (emprestimo.getCarencia() < 0) {
            throw new ApiException("O período de carência não pode ser negativo.", 400);
        }
        if (emprestimo.getTaxaMulta() < 0 || emprestimo.getTaxaJurosMora() < 0) {
            throw new ApiException("Taxa de multa e juros de mora não podem ser negativas.", 400);
        }

        // Limites de negócio (exemplo baseado em EmprestimoParams)
        int prazoMaximo = emprestimo.getTipoEmprestimo() == CONSIGNADO
                ? params.getPrazoMaximoConsignado()
                : params.getPrazoMaximoPessoal();
        if (emprestimo.getQuantidadeParcelas() > prazoMaximo) {
            throw new ApiException("Quantidade de parcelas excede o prazo máximo permitido: " + prazoMaximo, 400);
        }
        double jurosMaximo = emprestimo.getTipoEmprestimo() == CONSIGNADO
                ? params.getJurosMaximoConsignado()
                : params.getJurosMaximoPessoal();
        if (emprestimo.getTaxaJuros() > jurosMaximo) {
            throw new ApiException("Taxa de juros excede o máximo permitido: " + jurosMaximo, 400);
        }
    }
}