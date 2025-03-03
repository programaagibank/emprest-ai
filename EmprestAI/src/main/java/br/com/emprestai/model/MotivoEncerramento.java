package br.com.emprestai.model;

public class MotivoEncerramento {
    private int motivoEncerramentoId;
    private String descEncerramento;

    public int getMotivoEncerramentoId() {
        return motivoEncerramentoId;
    }

    public void setMotivoEncerramentoId(int motivoEncerramentoId) {
        this.motivoEncerramentoId = motivoEncerramentoId;
    }

    public String getDescEncerramento() {
        return descEncerramento;
    }

    public void setDescEncerramento(String descEncerramento) {
        this.descEncerramento = descEncerramento;
    }

    @Override
    public String toString() {
        return "MotivoEncerramento [motivoEncerramentoId="
                + motivoEncerramentoId
                + ", descEncerramento="
                + descEncerramento
                + "]";
    }

}
