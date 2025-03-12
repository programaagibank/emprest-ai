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

    public Emprestimo() {
    }

    public Emprestimo(long idCliente, long idContrato, BigDecimal valorTotal, int quantidadeParcelas, double juros, LocalDate dataInicio,
                      StatusEmpEnum idStatusEmprestimo, BigDecimal valorSeguro, BigDecimal valorIOF, BigDecimal outrosCustos,
                      LocalDate dataContratacao, MotivosEncerramentosEmpEnum idMotivoEncerramento, double jurosMora, double taxaMulta, long idEmprestimoOrigem){
        this.idCliente = idCliente;
        this.idContrato = idContrato;
        this.valorTotal = valorTotal;
        this.quantidadeParcelas = quantidadeParcelas;
        this.juros = juros;
        this.dataInicio = dataInicio;
        this.idStatusEmprestimo = idStatusEmprestimo;
        this.valorSeguro = valorSeguro;
        this.valorIOF = valorIOF;
        this.outrosCustos = outrosCustos;
        this.dataContratacao = dataContratacao;
        this.idMotivoEncerramento = idMotivoEncerramento;
        this.jurosMora = jurosMora;
        this.taxaMulta = taxaMulta;
        this.idEmprestimoOrigem = idEmprestimoOrigem;
    }

}

