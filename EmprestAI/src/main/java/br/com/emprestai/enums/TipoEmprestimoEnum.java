package br.com.emprestai.enums;

public enum TipoEmprestimoEnum {
    CONSIGNADO(1), PESSOAL(2);
    private final int valor;

    TipoEmprestimoEnum(int valor){
        this.valor = valor;
    }

    public int getValor(){
        return valor;
    }

    public static TipoEmprestimoEnum fromValor(int valor){
        for (TipoEmprestimoEnum tipo: TipoEmprestimoEnum.values()){
            if (tipo.getValor() == valor){
                return tipo;
            }
        }
        throw new IllegalArgumentException("Nenhum TipoEmprestimo encontrado para o valor: " + valor);
    }
}
