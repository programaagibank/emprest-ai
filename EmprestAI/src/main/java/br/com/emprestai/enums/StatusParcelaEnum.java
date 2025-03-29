package br.com.emprestai.enums;

public enum StatusParcelaEnum {
    PAGA(1), PENDENTE(2), ATRASADA(3), PAGA_EM_ATRASO(4), PAGA_POR_ANTECIPACAO(5);
    private final int valor;

    StatusParcelaEnum(int valor) {this.valor = valor; }

    public int getValor() {return valor; }

    public static StatusParcelaEnum fromValor(int valor){
        for (StatusParcelaEnum status: StatusParcelaEnum.values()){
            if (status.getValor() == valor){
                return status;
            }
        }
        throw new IllegalArgumentException("Nenhum StatusParcela encontrado para o valor: " + valor);
    }
}
