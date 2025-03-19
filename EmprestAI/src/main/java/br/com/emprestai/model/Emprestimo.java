package br.com.emprestai.model;

import br.com.emprestai.enums.MotivosEncerramentosEmpEnum;
import br.com.emprestai.enums.StatusEmpEnum;
import br.com.emprestai.enums.TipoEmpEnum;

import java.time.LocalDate;
import java.util.List;

public class Emprestimo {
    private long idContrato;
    private String nomeCliente;
    private String cpfCliente;
    private long idCliente;
    private double valorTotal;
    private double valorEmprestimo;
    private double valorParcela;
    private int quantidadeParcelas;
    private double juros;
    private LocalDate dataInicio;
    private StatusEmpEnum statusEmprestimo;
    private TipoEmpEnum tipoEmprestimo;
    private double valorSeguro;
    private double valorIOF;
    private double outrosCustos;
    private LocalDate dataContratacao;
    private MotivosEncerramentosEmpEnum motivoEncerramento;
    private double taxaJurosMora;
    private double taxaMulta;
    private long idEmprestimoOrigem;
    private List<Parcela> parcelaList;
    private double saldoDevedorAtualizado;
    private LocalDate dataLiberacaoCred;
    private Boolean contratarSeguro;
    private double taxaEfetivaMensal;

    //Construtor vazio
    public Emprestimo() {
    }

    //Construtor com parâmetros
    public Emprestimo(long idContrato, String nomeCliente, String cpfCliente, long idCliente, double valorTotal,
                      double valorEmprestimo, double valorParcela, int quantidadeParcelas, double juros, LocalDate dataInicio,
                      StatusEmpEnum statusEmprestimo, TipoEmpEnum tipoEmprestimo, double valorSeguro, double valorIOF,
                      double outrosCustos, LocalDate dataContratacao, MotivosEncerramentosEmpEnum motivoEncerramento,
                      double taxaJurosMora, double taxaMulta, long idEmprestimoOrigem, List<Parcela> parcelaList,
                      double saldoDevedorAtualizado, LocalDate dataLiberacaoCred, Boolean contratarSeguro,
                      double taxaEfetivaMensal) {
        this.idContrato = idContrato;
        this.nomeCliente = nomeCliente;
        this.cpfCliente = cpfCliente;
        this.idCliente = idCliente;
        this.valorTotal = valorTotal;
        this.valorEmprestimo = valorEmprestimo;
        this.valorParcela = valorParcela;
        this.quantidadeParcelas = quantidadeParcelas;
        this.juros = juros;
        this.dataInicio = dataInicio;
        this.statusEmprestimo = statusEmprestimo;
        this.tipoEmprestimo = tipoEmprestimo;
        this.valorSeguro = valorSeguro;
        this.valorIOF = valorIOF;
        this.outrosCustos = outrosCustos;
        this.dataContratacao = dataContratacao;
        this.motivoEncerramento = motivoEncerramento;
        this.taxaJurosMora = taxaJurosMora;
        this.taxaMulta = taxaMulta;
        this.idEmprestimoOrigem = idEmprestimoOrigem;
        this.parcelaList = parcelaList;
        this.saldoDevedorAtualizado = saldoDevedorAtualizado;
        this.dataLiberacaoCred = dataLiberacaoCred;
        this.contratarSeguro = contratarSeguro;
        this.taxaEfetivaMensal = taxaEfetivaMensal;
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

    public StatusEmpEnum getStatusEmprestimo(){
        return statusEmprestimo;
    }

    public void setStatusEmprestimo(StatusEmpEnum idStatusEmprestimo){
        this.statusEmprestimo = idStatusEmprestimo;
    }

    public TipoEmpEnum getTipoEmprestimo(){
        return tipoEmprestimo;
    }
    public void setTipoEmprestimo(TipoEmpEnum tipoEmprestimo){
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

    public MotivosEncerramentosEmpEnum getIdMotivoEncerramento(){
        return motivoEncerramento;
    }
    public void setIdMotivoEncerramento(MotivosEncerramentosEmpEnum idMotivoEncerramento){
        this.motivoEncerramento = idMotivoEncerramento;
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

    public String getCpfCliente(){
        return cpfCliente;
    }
    public void setCpfCliente(String cpfCliente){
        this.cpfCliente = cpfCliente;
    }

    public String getNomeCliente(){
        return nomeCliente;
    }
    public void setNomeCliente(String nomeCliente){
        this.nomeCliente = nomeCliente;
    }

    public List<Parcela> getParcelaList() {
        return parcelaList;
    }
    public void setParcelaList(List<Parcela> parcelaList) {
        this.parcelaList = parcelaList;
    }
    public double getSaldoDevedorAtualizado() {
        return saldoDevedorAtualizado;
    }

    public void setSaldoDevedorAtualizado(double saldoDevedorAtualizado) {
        this.saldoDevedorAtualizado = saldoDevedorAtualizado;
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

    @Override
    public String toString() {
        return "Emprestimo{" +
                "idContrato=" + idContrato +
                ", nomeCliente='" + nomeCliente + '\'' +
                ", cpfCliente='" + cpfCliente + '\'' +
                ", idCliente=" + idCliente +
                ", valorTotal=" + valorTotal +
                ", valorEmprestimo=" + valorEmprestimo +
                ", valorParcela=" + valorParcela +
                ", quantidadeParcelas=" + quantidadeParcelas +
                ", juros=" + juros +
                ", dataInicio=" + dataInicio +
                ", statusEmprestimo=" + statusEmprestimo +
                ", tipoEmprestimo=" + tipoEmprestimo +
                ", valorSeguro=" + valorSeguro +
                ", valorIOF=" + valorIOF +
                ", outrosCustos=" + outrosCustos +
                ", dataContratacao=" + dataContratacao +
                ", motivoEncerramento=" + motivoEncerramento +
                ", taxaJurosMora=" + taxaJurosMora +
                ", taxaMulta=" + taxaMulta +
                ", idEmprestimoOrigem=" + idEmprestimoOrigem +
                ", parcelaList=" + parcelaList +
                ", saldoDevedorAtualizado=" + saldoDevedorAtualizado +
                ", dataLiberacaoCred=" + dataLiberacaoCred +
                ", contratarSeguro=" + contratarSeguro +
                ", taxaEfetivaMensal=" + taxaEfetivaMensal +
                '}';
    }
}

