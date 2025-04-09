package br.com.emprestai.view;

import br.com.emprestai.model.Cliente;
import br.com.emprestai.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ChatbotViewController {

    @FXML private VBox chatArea; // Usando VBox em vez de TextArea
    @FXML private TextField chatInput;
    @FXML private Button chatbotButton;

    private enum ChatState { INICIAL, SUPORTE, EDUCACAO, NEGOCIACAO, NEGOCIACAO_CONFIRMACAO }
    private ChatState estadoAtual = ChatState.INICIAL;
    private double valorNegociacao;

    @FXML
    private void initialize() {
        chatArea.setSpacing(10); // Espaçamento entre mensagens
    }

    @FXML
    private void startChatbot() {
        chatArea.getChildren().clear();
        estadoAtual = ChatState.INICIAL;
        Cliente clienteLogado = SessionManager.getInstance().getClienteLogado();
        appendBotMessage("Oi, " + clienteLogado.getNomeCliente() + "! Sou a Clara do EmprestAI. 😊");
        appendBotMessage("Como posso te ajudar hoje?");
        appendBotMessage("1 - Suporte ao Cliente (perguntas frequentes)\n2 - Educação Financeira (dicas)\n3 - Negociação de Dívidas\nDigite o número da opção:");
        chatbotButton.setDisable(true);
    }

    @FXML
    private void sendMessage() {
        String mensagem = chatInput.getText().trim();
        if (mensagem.isEmpty()) return;

        appendUserMessage(mensagem);
        chatInput.clear();

        switch (estadoAtual) {
            case INICIAL:
                switch (mensagem) {
                    case "1":
                        estadoAtual = ChatState.SUPORTE;
                        appendBotMessage("Escolha uma pergunta frequente:");
                        appendBotMessage("1 - Quais documentos preciso para um empréstimo?");
                        appendBotMessage("2 - Quanto tempo demora a liberação?");
                        appendBotMessage("3 - Como cancelo um empréstimo?");
                        appendBotMessage("Digite o número da pergunta ou 'voltar' para o menu.");
                        break;
                    case "2":
                        estadoAtual = ChatState.EDUCACAO;
                        appendBotMessage("Escolha uma dica financeira:");
                        appendBotMessage("1 - Como economizar para pagar dívidas?");
                        appendBotMessage("2 - O que é taxa de juros?");
                        appendBotMessage("3 - Como melhorar meu score?");
                        appendBotMessage("Digite o número da dica ou 'voltar' para o menu.");
                        break;
                    case "3":
                        estadoAtual = ChatState.NEGOCIACAO;
                        appendBotMessage("Você tem uma dívida pendente? Digite o valor aproximado da dívida (ex: 5000) ou 'não' se não tiver.");
                        break;
                    default:
                        appendBotMessage("Opção inválida. Digite 1, 2 ou 3, por favor.");
                        break;
                }
                break;

            case SUPORTE:
                if (mensagem.equalsIgnoreCase("voltar")) {
                    voltarAoMenu();
                } else {
                    switch (mensagem) {
                        case "1":
                            appendBotMessage("Para um empréstimo, você precisa de: RG, CPF, comprovante de renda e residência. Para consignado, também o contracheque.");
                            break;
                        case "2":
                            appendBotMessage("A liberação leva de 1 a 3 dias úteis após a aprovação, dependendo do tipo de empréstimo.");
                            break;
                        case "3":
                            appendBotMessage("Para cancelar, entre em contato pelo 0800-123-4567 ou no app, dentro de 7 dias após a contratação.");
                            break;
                        default:
                            appendBotMessage("Pergunta inválida. Digite 1, 2, 3 ou 'voltar'.");
                            break;
                    }
                    appendBotMessage("Mais alguma dúvida? Digite outra pergunta ou 'voltar'.");
                }
                break;

            case EDUCACAO:
                if (mensagem.equalsIgnoreCase("voltar")) {
                    voltarAoMenu();
                } else {
                    switch (mensagem) {
                        case "1":
                            appendBotMessage("Para economizar: crie um orçamento mensal, corte gastos desnecessários e priorize o pagamento de dívidas com juros altos.");
                            break;
                        case "2":
                            appendBotMessage("Taxa de juros é o custo do dinheiro emprestado. Quanto maior, mais você paga além do valor principal!");
                            break;
                        case "3":
                            appendBotMessage("Para melhorar seu score: pague contas em dia, evite dívidas altas e mantenha seu cadastro atualizado.");
                            break;
                        default:
                            appendBotMessage("Dica inválida. Digite 1, 2, 3 ou 'voltar'.");
                            break;
                    }
                    appendBotMessage("Quer outra dica? Digite outro número ou 'voltar'.");
                }
                break;

            case NEGOCIACAO:
                if (mensagem.equalsIgnoreCase("não")) {
                    appendBotMessage("Que bom! Se precisar de algo, é só voltar. 😊");
                    voltarAoMenu();
                } else {
                    try {
                        valorNegociacao = Double.parseDouble(mensagem);
                        estadoAtual = ChatState.NEGOCIACAO_CONFIRMACAO;
                        appendBotMessage("Ok, para uma dívida de R$ " + String.format("%.2f", valorNegociacao) + ", oferecemos:");
                        appendBotMessage("1 - Pagamento à vista com 20% de desconto: R$ " + String.format("%.2f", valorNegociacao * 0.8));
                        appendBotMessage("2 - Parcelado em 6x sem juros: R$ " + String.format("%.2f", valorNegociacao / 6) + " por mês.");
                        appendBotMessage("Digite 1 ou 2 para escolher, ou 'voltar' para o menu.");
                    } catch (NumberFormatException e) {
                        appendBotMessage("Por favor, digite um valor numérico válido (ex: 5000) ou 'não'.");
                    }
                }
                break;

            case NEGOCIACAO_CONFIRMACAO:
                if (mensagem.equalsIgnoreCase("voltar")) {
                    voltarAoMenu();
                } else {
                    switch (mensagem) {
                        case "1":
                            appendBotMessage("Ótimo! Pagamento à vista com 20% de desconto confirmado. Um atendente entrará em contato para finalizar. 🎉");
                            resetChat();
                            break;
                        case "2":
                            appendBotMessage("Perfeito! Parcelamento em 6x sem juros confirmado. Um atendente entrará em contato para os detalhes. 🎉");
                            resetChat();
                            break;
                        default:
                            appendBotMessage("Opção inválida. Digite 1, 2 ou 'voltar'.");
                            break;
                    }
                }
                break;
        }
    }

    private void appendBotMessage(String message) {
        Image botImage = new Image(getClass().getResourceAsStream("../images/bot-profile.png")); // Imagem do bot
        ImageView botImageView = new ImageView(botImage);
        botImageView.setFitWidth(30);
        botImageView.setFitHeight(30);

        Text text = new Text(message);
        text.setWrappingWidth(250); // Limita a largura do texto

        HBox messageBox = new HBox(10, botImageView, text);
        messageBox.setAlignment(Pos.CENTER_LEFT);
        messageBox.getStyleClass().add("bot-message");

        chatArea.getChildren().add(messageBox);
    }

    private void appendUserMessage(String message) {
        Text text = new Text(message);
        text.setWrappingWidth(250);

        HBox messageBox = new HBox(text);
        messageBox.setAlignment(Pos.CENTER_RIGHT);
        messageBox.getStyleClass().add("user-message");

        chatArea.getChildren().add(messageBox);
    }

    private void voltarAoMenu() {
        estadoAtual = ChatState.INICIAL;
        appendBotMessage("Voltando ao menu principal...");
        appendBotMessage("1 - Suporte ao Cliente\n2 - Educação Financeira\n3 - Negociação de Dívidas\nDigite o número da opção:");
    }

    private void resetChat() {
        estadoAtual = ChatState.INICIAL;
        chatbotButton.setDisable(false);
    }
}