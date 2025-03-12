package br.com.emprestai.model;

import br.com.emprestai.enums.MotivosEncerramentosEmpEnum;
import br.com.emprestai.enums.StatusEmpEnum;
import br.com.emprestai.enums.TipoEmpEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Emprestimo {
    private long idContrato;
    private long idCliente;
    private BigDecimal valorTotal;
    private int quantidadeParcelas;
    private double juros;
    private LocalDate dataInicio;
    private StatusEmpEnum idStatusEmprestimo;
    private TipoEmpEnum idTipoEmprestimo;
    private BigDecimal valorSeguro;
    private BigDecimal valorIOF;
    private BigDecimal outrosCustos;
    private LocalDate dataContratacao;
    private MotivosEncerramentosEmpEnum idMotivoEncerramento;
    private double jurosMora;
    private double taxaMulta;
    private long idEmprestimoOrigem;
}

