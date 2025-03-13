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

    //Construtor vazio
    public Emprestimo() {
    }

    //Construtor com parâmetros
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

    // Getters e setters
    public long getIdCliente(){
        return idCliente;
    }
    public void setIdCliente(long idCliente){
        this.idCliente = idCliente;
    }

    public long getIdContrato(){
        return idContrato;
    }
    public void setIdContrato(long idContrato){
        this.idContrato = idContrato;
    }

    public BigDecimal getValorTotal(){
        return valorTotal;
    }
    public void setValorTotal(BigDecimal valorTotal){
        this.valorTotal = valorTotal;
    }

    public int getQuantidadeParcelas(){
        return quantidadeParcelas;
    }
    public void setQuantidadeParcelas(int quantidadeParcelas){
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public double getJuros(){
        return juros;
    }
    public void setJuros(double juros){
        this.juros = juros;
    }

    public LocalDate getDataInicio(){
        return dataInicio;
    }
    public void setDataInicio(LocalDate dataInicio){
        this.dataInicio = dataInicio;
    }

    public StatusEmpEnum getIdStatusEmprestimo(){
        return idStatusEmprestimo;
    }
    public void setIdStatusEmprestimo(StatusEmpEnum idStatusEmprestimo){
        this.idStatusEmprestimo = idStatusEmprestimo;
    }

    public TipoEmpEnum getIdTipoEmprestimo(){
        return idTipoEmprestimo;
    }
    public void setIdTipoEmprestimo(TipoEmpEnum idTipoEmprestimo){
        this.idTipoEmprestimo = idTipoEmprestimo;
    }

    public BigDecimal getValorSeguro(){
        return valorSeguro;
    }
    public void setValorSeguro(BigDecimal valorSeguro){
        this.valorSeguro = valorSeguro;
    }

    public BigDecimal getValorIOF(){
        return valorIOF;
    }
    public void setValorIOF(BigDecimal valorIOF){
        this.valorIOF = valorIOF;
    }

    public BigDecimal getOutrosCustos(){
        return outrosCustos;
    }
    public void setOutrosCustos(BigDecimal outrosCustos){
        this.outrosCustos = outrosCustos;
    }

    public LocalDate getDataContratacao(){
        return dataContratacao;
    }
    public void setDataContratacao(LocalDate dataContratacao){
        this.dataContratacao = dataContratacao;
    }

    public MotivosEncerramentosEmpEnum getIdMotivoEncerramento(){
        return idMotivoEncerramento;
    }
    public void setIdMotivoEncerramento(MotivosEncerramentosEmpEnum idMotivoEncerramento){
        this.idMotivoEncerramento = idMotivoEncerramento;
    }

    public double getJurosMora(){
        return jurosMora;
    }
    public void setJurosMora(double jurosMora){
        this.jurosMora = jurosMora;
    }

    public double getTaxaMulta(){
        return taxaMulta;
    }
    public void setTaxaMulta(double taxaMulta){
        this.taxaMulta = taxaMulta;
    }

    public long getIdEmprestimoOrigem(){
        return idEmprestimoOrigem;
    }
    public void setIdEmprestimoOrigem(long idEmprestimoOrigem){
        this.idEmprestimoOrigem = idEmprestimoOrigem;
    }
}

