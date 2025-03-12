package br.com.emprestai.enums;

public enum MotivosEncerramentosEmpEnum {
    ABERTO(1), QUITADO(2), RENEGOCIADO(3);
    private final int valor;

    MotivosEncerramentosEmpEnum(int valor){
        this.valor = valor;
    }

    public int getValor(){
        return valor;
    }

    public static MotivosEncerramentosEmpEnum fromValor(int valor){
        for(MotivosEncerramentosEmpEnum motivo: MotivosEncerramentosEmpEnum.values()){
            if(motivo.getValor() == valor){
                return motivo;
            }
        }
        throw new IllegalArgumentException("Nenhum MotivoEncerramento encontrado para o valor: " + valor);
    }
}
