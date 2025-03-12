package br.com.emprestai.enums;

import br.com.emprestai.model.Cliente;

public enum VinculoEnum {
    APOSENTADO(1), SERVIDOR(2), PENSIONISTA(3), EMPREGADO(4);
    private final int valor;

    // Construtor do enum
    VinculoEnum(int valor) {
        this.valor = valor;
    }

    // Getter para o valor
    public int getValor() {
        return valor;
    }

    // Metodo estático para buscar o enum pelo valor
    public static VinculoEnum fromValor(int valor) {
        for (VinculoEnum tipo : VinculoEnum.values()) {
            if (tipo.getValor() == valor) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Nenhum TipoCliente encontrado para o valor: " + valor);
    }

}
