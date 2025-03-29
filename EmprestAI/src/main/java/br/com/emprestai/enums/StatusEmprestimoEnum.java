package br.com.emprestai.enums;

public enum StatusEmprestimoEnum {
    ABERTO(1), QUITADO(2), RENEGOCIADO(3), NEGADO(4);
    private final int valor;

    StatusEmprestimoEnum(int valor){
        this.valor = valor;
    }

    public int getValor(){
        return valor;
    }

    public static StatusEmprestimoEnum fromValor(int valor){
        for(StatusEmprestimoEnum status: StatusEmprestimoEnum.values()){
            if (status.getValor() == valor){
                return status;
            }
        }
        throw new IllegalArgumentException("Nenhum StatusEmprestimo encontrado para o valor: " + valor);
    }
}