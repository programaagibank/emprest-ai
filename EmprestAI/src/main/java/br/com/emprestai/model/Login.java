package br.com.emprestai.model;

public class Login {
    private Long idLogin; // Autoincrementado no banco
    private String cpf; // Chave estrangeira para clientes
    private String senha; // Será criptografada com Bcrypt


    public Login() {
    }

    // Construtor com parâmetros
    public Login(Long idLogin, String username, String senha) {
        this.idLogin = idLogin;
        this.cpf = username;
        this.senha = senha;
    }

    // Getters e Setters
    public Long getIdLogin() {
        return idLogin;
    }

    public void setIdLogin(Long idLogin) {
        this.idLogin = idLogin;
    }

    public String getUsername() {
        return cpf;
    }

    public void setUsername(String username) {
        this.cpf = username;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return "Login{" +
                "id_login=" + idLogin +
                ", username='" + cpf + '\'' +
                ", senha='[PROTEGIDA]'" +
                '}';
    }
}