package br.com.emprestai.enums;

public enum StatusEmpEnum{
    APROVADO(1), NEGADO(2);
    private final int valor;

    StatusEmpEnum(int valor){
        this.valor = valor;
    }

    public int getValor(){
        return valor;
    }

    public static StatusEmpEnum fromValor(int valor){
        for(StatusEmpEnum status: StatusEmpEnum.values()){
            if (status.getValor() == valor){
                return status;
            }
        }
        throw new IllegalArgumentException("Nenhum StatusEmprestimo encontrado para o valor: " + valor);
    }
}