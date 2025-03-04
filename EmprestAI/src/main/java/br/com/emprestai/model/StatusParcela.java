package br.com.emprestai.model;

public class StatusParcela {
    private int statusParcelaId;
    private String descStatusParcela;

    public StatusParcela(String descStatusParcela) {
        this.descStatusParcela = descStatusParcela;
    }

    public int getStatusParcela() {
        return statusParcelaId;
    }

    public String getDescStatusParcela() {
        return descStatusParcela;
    }

    public void setDescStatusParcela(String descStatusParcela) {
        this.descStatusParcela = descStatusParcela;
    }

    @Override
    public String toString() {
        return "StatusParcela{" +
                "statusParcela=" + statusParcelaId +
                ", descStatusParcela='" + descStatusParcela + '\'' +
                '}';
    }
}
