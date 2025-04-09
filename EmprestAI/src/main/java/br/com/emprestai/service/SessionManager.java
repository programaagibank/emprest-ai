package br.com.emprestai.service;

/**
 * Classe responsável por gerenciar a sessão do usuário logado
 * Singleton para manter os dados da sessão em toda a aplicação
 */
public class SessionManager {

    // Instância única do SessionManager
    private static SessionManager instance;

    // Dados da sessão
    private Long clienteLogadoId;
    private String clienteLogadoCpf;
    private String clienteLogadoNome;

    // Construtor privado para padrão Singleton
    private SessionManager() {
        // Inicializa os dados da sessão como nulos
        clienteLogadoId = null;
        clienteLogadoCpf = null;
        clienteLogadoNome = null;
    }

    /**
     * Retorna a instância única do SessionManager
     * @return Instância do SessionManager
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Inicia uma sessão com os dados do cliente logado
     * @param id ID do cliente
     * @param cpf CPF do cliente
     * @param nome Nome do cliente
     */
    public void login(Long id, String cpf, String nome) {
        clienteLogadoId = id;
        clienteLogadoCpf = cpf;
        clienteLogadoNome = nome;
    }

    /**
     * Encerra a sessão atual
     */
    public void logout() {
        clienteLogadoId = null;
        clienteLogadoCpf = null;
        clienteLogadoNome = null;
    }

    /**
     * Verifica se há um usuário logado
     * @return true se houver um usuário logado, false caso contrário
     */
    public boolean isLogado() {
        return clienteLogadoId != null;
    }

    // Getters para os dados da sessão

    public Long getClienteLogadoId() {
        return clienteLogadoId;
    }

    public String getClienteLogadoCpf() {
        return clienteLogadoCpf;
    }

    public String getClienteLogadoNome() {
        return clienteLogadoNome;
    }
}
