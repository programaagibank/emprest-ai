package br.com.emprestai.enums;

public enum StatusEmpParcela {
    PAGA(1), PENDENTE(2), ATRASADA(3), PAGA_EM_ATRASO(4);
    private final int valor;

    StatusEmpParcela(int valor) {this.valor = valor; }

    public int getValor() {return valor; }

    public static StatusEmpParcela fromValor(int valor){
        for (StatusEmpParcela status: StatusEmpParcela.values()){
            if (status.getValor() == valor){
                return status;
            }
        }
        throw new IllegalArgumentException("Nenhum StatusParcela encontrado para o valor: " + valor);
    }
}
