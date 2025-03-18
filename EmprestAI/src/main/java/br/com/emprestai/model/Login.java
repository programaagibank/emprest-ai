package br.com.emprestai.model;

public class Login {
    private String usuarioBanco;
    private String senhaBanco;

    public Login(String usuarioBanco, String senhaBanco) {
        this.usuarioBanco = usuarioBanco;
        this.senhaBanco = senhaBanco;
    }

    public String getUsuarioBanco() {
        return usuarioBanco;
    }

    public void setUsuarioBanco(String usuarioBanco) {
        this.usuarioBanco = usuarioBanco;
    }

    public String getSenhaBanco() {
        return senhaBanco;
    }

    public void setSenhaBanco(String senhaBanco) {
        this.senhaBanco = senhaBanco;
    }

   @Override
    public String toString() {
        return "Login{" +
                "usuarioBanco='" + usuarioBanco + '\'' +
                ", senhaBanco='" + senhaBanco + '\'' +
                '}';
    }
}