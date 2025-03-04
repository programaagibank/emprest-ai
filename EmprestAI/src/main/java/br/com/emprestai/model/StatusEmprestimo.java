package br.com.emprestai.model;

public class StatusEmprestimo {
    private int statusParcelaId;
    private String descStatusParcela;

    public StatusEmprestimo(String descStatusParcela) {
        this.descStatusParcela = descStatusParcela;
    }

    public int getStatusParcelaId() {
        return statusParcelaId;
    }

    public void setStatusParcelaId(int statusParcelaId) {
        this.statusParcelaId = statusParcelaId;
    }

    public String getDescStatusParcela() {
        return descStatusParcela;
    }

    public void setDescStatusParcela(String descStatusParcela) {
        this.descStatusParcela = descStatusParcela;
    }

    @Override
    public String toString() {
        return "StatusEmprestimo [statusParcelaId="
                + statusParcelaId
                + ", descStatusParcela="
                + descStatusParcela
                + "]";
    }

}
