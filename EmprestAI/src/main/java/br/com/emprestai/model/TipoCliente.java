package br.com.emprestai.model;

public class TipoCliente {
    private int tipoClienteId;
    private String descTipoCliente;

    public TipoCliente(String descTipoCliente) {
        this.descTipoCliente = descTipoCliente;
    }

    public int getTipoClienteId() {
        return tipoClienteId;
    }

    public String getDescTipoCliente() {
        return descTipoCliente;
    }

    public void setDescTipoCliente(String descTipoCliente) {
        this.descTipoCliente = descTipoCliente;
    }

    @Override
    public String toString() {
        return "TipoCliente [tipoClienteId=" + tipoClienteId
                + ", descTipoCliente=" + descTipoCliente
                + "]";
    }

}
