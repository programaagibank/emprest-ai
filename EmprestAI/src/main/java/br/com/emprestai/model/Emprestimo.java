package br.com.emprestai.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Emprestimo {
    private long idContrato;
    private long idCliente;
    private BigDecimal valorTotal;
    private int quantidadeParcelas;
    private double juros;
    private LocalDate dataInicio;
    private StatusEmprestimo idStatusEmprestimo;
    private int idTipoEmprestimo;
    private BigDecimal valorSeguro;
    private BigDecimal valorIOF;
    private BigDecimal outrosCustos;
    private LocalDate dataContratacao;
    private int idMotivoEncerramento;
    private double jurosMora;
    private double taxaMulta;
    private long idEmprestimoOrigem;

    public enum StatusEmprestimo{
        APROVADO(1), NEGADO(2);
        private final int valor;

        StatusEmprestimo(int valor){
            this.valor = valor;
        }

        public int getValor(){
            return valor;
        }

        public static StatusEmprestimo fromValor(int valor){
            for(StatusEmprestimo tipo: StatusEmprestimo.values()){
                if (tipo.getValor() == valor){
                    return tipo;
                }
            }
            throw new IllegalArgumentException("Nenhum StatusEmprestimo encontrado para o valor: " + valor);
        }
    }
}

