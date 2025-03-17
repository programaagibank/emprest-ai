package br.com.emprestai.model;

public class Login {
    private String usuarioBanco;
    private String senhaBanco;
    private String usuarioRecebido;
    private String senhaRecebido;

    public Login(String usuarioBanco, String senhaBanco, String usuarioRecebido, String senhaRecebido) {
        this.usuarioBanco = usuarioBanco;
        this.senhaBanco = senhaBanco;
        this.usuarioRecebido = usuarioRecebido;
        this.senhaRecebido = senhaRecebido;
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

    public String getUsuarioRecebido() {
        return usuarioRecebido;
    }

    public void setUsuarioRecebido(String usuarioRecebido) {
        this.usuarioRecebido = usuarioRecebido;
    }

    public String getSenhaRecebido() {
        return senhaRecebido;
    }

    public void setSenhaRecebido(String senhaRecebido) {
        this.senhaRecebido = senhaRecebido;
    }

    @Override
    public String toString() {
        return "Login{" +
                "usuarioBanco='" + usuarioBanco + '\'' +
                ", senhaBanco='" + senhaBanco + '\'' +
                ", usuarioRecebido='" + usuarioRecebido + '\'' +
                ", senhaRecebido='" + senhaRecebido + '\'' +
                '}';
    }
}