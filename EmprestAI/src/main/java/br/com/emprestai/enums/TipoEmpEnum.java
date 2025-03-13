package br.com.emprestai.enums;

public enum TipoEmpEnum {
    CONSIGNADO(1), PESSOAL(2);
    private final int valor;

    TipoEmpEnum(int valor){
        this.valor = valor;
    }

    public int getValor(){
        return valor;
    }

    public static TipoEmpEnum fromValor(int valor){
        for (TipoEmpEnum tipo: TipoEmpEnum.values()){
            if (tipo.getValor() == valor){
                return tipo;
            }
        }
        throw new IllegalArgumentException("Nenhum TipoEmprestimo encontrado para o valor: " + valor);
    }
}
