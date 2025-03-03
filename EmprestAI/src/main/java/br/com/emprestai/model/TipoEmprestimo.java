package br.com.emprestai.model;

public enum TipoEmprestimo {
    CONSIGNADO(1),
    PESSOAL(2),
    CONSIGNADORENEGOCIADO(3),
    PESSOALRENEGOCIADO(4);

    private final int codigo;

    // Construtor privado do enum
    TipoEmprestimo(int codigo) {
        this.codigo = codigo;
    }

    // Método público para acessar o valor
    public int getCodigo() {
        return codigo;
    }
}
