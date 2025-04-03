package br.com.emprestai.model;

import br.com.emprestai.enums.StatusEmprestimoEnum;
import br.com.emprestai.enums.TipoEmprestimoEnum;

import java.time.LocalDate;
import java.util.List;

public class Emprestimo {
    private long idEmprestimo;
    private double valorTotal;
    private double valorEmprestimo;
    private double valorParcela;
    private int quantidadeParcelas;
    private int parcelasPagas;
    private double taxaJuros;
    private LocalDate dataInicio;
    private StatusEmprestimoEnum statusEmprestimo;
    private TipoEmprestimoEnum tipoEmprestimo;
    private double valorSeguro;
    private double valorIOF;
    private double outrosCustos;
    private LocalDate dataContratacao;
    private double taxaJurosMora;
    private double taxaMulta;
    private long idEmprestimoOrigem;
    private List<Parcela> parcelaList;
    private LocalDate dataLiberacaoCred;
    private Boolean contratarSeguro;
    private double taxaEfetivaMensal;
    private long idCliente;

    //Construtor vazio
    public Emprestimo() {
    }


    public long getIdEmprestimo(){
        return idEmprestimo;
    }
    public void setIdEmprestimo(long idEmprestimo){
        this.idEmprestimo = idEmprestimo;
    }

    public double getValorTotal(){
        return valorTotal;
    }
    public void setValorTotal(double valorTotal){
        this.valorTotal = valorTotal;
    }

    public int getQuantidadeParcelas(){
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(int quantidadeParcelas){
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public double getTaxaJuros(){
        return taxaJuros;
    }
    public void setTaxaJuros(double taxaJuros){
        this.taxaJuros = taxaJuros;
    }

    public LocalDate getDataInicio(){
        return dataInicio;
    }
    public void setDataInicio(LocalDate dataInicio){
        this.dataInicio = dataInicio;
    }

    public StatusEmprestimoEnum getStatusEmprestimo(){
        return statusEmprestimo;
    }

    public void setStatusEmprestimo(StatusEmprestimoEnum idStatusEmprestimo){
        this.statusEmprestimo = idStatusEmprestimo;
    }

    public TipoEmprestimoEnum getTipoEmprestimo(){
        return tipoEmprestimo;
    }

    public void setTipoEmprestimo(TipoEmprestimoEnum tipoEmprestimo){
        this.tipoEmprestimo = tipoEmprestimo;
    }

    public double getValorSeguro(){
        return valorSeguro;
    }
    public void setValorSeguro(double valorSeguro){
        this.valorSeguro = valorSeguro;
    }

    public double getValorIOF(){
        return valorIOF;
    }
    public void setValorIOF(double valorIOF){
        this.valorIOF = valorIOF;
    }

    public double getOutrosCustos(){
        return outrosCustos;
    }

    public void setOutrosCustos(double outrosCustos){
        this.outrosCustos = outrosCustos;
    }

    public LocalDate getDataContratacao(){
        return dataContratacao;
    }
    public void setDataContratacao(LocalDate dataContratacao){
        this.dataContratacao = dataContratacao;
    }

    public double getTaxaJurosMora(){
        return taxaJurosMora;
    }
    public void setTaxaJurosMora(double taxaJurosMora){
        this.taxaJurosMora = taxaJurosMora;
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

    public List<Parcela> getParcelaList() {
        return parcelaList;
    }

    public void setParcelaList(List<Parcela> parcelaList) {
        this.parcelaList = parcelaList;
    }

    public LocalDate getDataLiberacaoCred() {
        return dataLiberacaoCred;
    }

    public void setDataLiberacaoCred(LocalDate dataLiberacaoCred) {
        this.dataLiberacaoCred = dataLiberacaoCred;
    }

    public double getValorEmprestimo() {
        return valorEmprestimo;
    }

    public void setValorEmprestimo(double valorEmprestimo) {
        this.valorEmprestimo = valorEmprestimo;
    }

    public Boolean getContratarSeguro() {
        return contratarSeguro;
    }

    public void setContratarSeguro(Boolean contratarSeguro) {
        this.contratarSeguro = contratarSeguro;
    }

    public double getTaxaEfetivaMensal() {
        return taxaEfetivaMensal;
    }

    public void setTaxaEfetivaMensal(double taxaEfetivaMensal) {
        this.taxaEfetivaMensal = taxaEfetivaMensal;
    }

    public double getValorParcela() {
        return valorParcela;
    }

    public void setValorParcela(double valorParcela) {
        this.valorParcela = valorParcela;
    }

    public long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(long idCliente) {
        this.idCliente = idCliente;
    }

    public int getParcelasPagas() {
        return parcelasPagas;
    }

    public void setParcelasPagas(int parcelasPagas) {
        this.parcelasPagas = parcelasPagas;
    }
}
