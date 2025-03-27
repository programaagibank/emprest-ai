package br.com.emprestai.controller;

import br.com.emprestai.enums.StatusEmpEnum;
import br.com.emprestai.enums.TipoEmpEnum;
import br.com.emprestai.model.Emprestimo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PagarParcela {
    private final Emprestimo emprestimo;

    // Construtor que recebe o Emprestimo diretamente
    public PagarParcela(Emprestimo emprestimo) {
        this.emprestimo = emprestimo;
    }

    public Map<Integer, Double> pagamentoParcela( String cpfCliente
                                                , int idParcela
                                                , int quantidadeParcelas
                                                , Map<Integer, Double> parcelasPagar) {
        try {
            // Validação inicial
            if (cpfCliente == null || cpfCliente.trim().isEmpty()) {
                throw new IllegalArgumentException("CPF do cliente não pode ser nulo ou vazio.");
            }
            if (quantidadeParcelas <= 0) {
                throw new IllegalArgumentException("Quantidade de parcelas deve ser maior que zero.");
            }
            if (parcelasPagar == null || parcelasPagar.isEmpty()) {
                throw new IllegalArgumentException("Mapa de parcelas não pode ser nulo ou vazio.");
            }

            // Verificar se o empréstimo existe e pertence ao CPF informado
            if (emprestimo == null || (emprestimo.getCliente() != null && !emprestimo.getCliente().getCpfCliente().equals(cpfCliente))) {
                throw new IllegalStateException("Empréstimo não encontrado para o CPF: " + cpfCliente);
            }

            // Verificar se o número de parcelas a pagar não excede o total do empréstimo
            if (idParcela > quantidadeParcelas || idParcela <= 0) {
                throw new IllegalArgumentException("ID da parcela inválido: " + idParcela);
            }

            // Mapa para armazenar as parcelas pagas com sucesso
            Map<Integer, Double> parcelasPagas = new HashMap<>();

            // Mapa interno para simular o estado das parcelas (substituindo Parcela e DAO)
            Map<Integer, Boolean> statusParcelasPagas = new HashMap<>();
            Map<Integer, Boolean> statusParcelasVencidas = new HashMap<>();

            // Inicializar o estado das parcelas (assumindo todas não pagas e não vencidas por padrão)
            for (int i = 1; i <= quantidadeParcelas; i++) {
                statusParcelasPagas.putIfAbsent(i, false);
                statusParcelasVencidas.putIfAbsent(i, false);
            }

            // Verificar a parcela específica informada pelo idParcela
            if (!parcelasPagar.containsKey(idParcela)) {
                throw new IllegalArgumentException("Parcela " + idParcela + " não está no mapa de parcelas a pagar.");
            }

            double valorParcela = parcelasPagar.get(idParcela);

            // Verificar se a parcela já foi paga
            if (statusParcelasPagas.get(idParcela)) {
                throw new IllegalStateException("Parcela " + idParcela + " já foi paga anteriormente.");
            }

            // Verificar se o valor corresponde ao esperado
            double valorEsperado = calcularValorParcelaComJuros(statusParcelasVencidas.get(idParcela));
            if (Math.abs(valorParcela - valorEsperado) > 0.01) {
                throw new IllegalArgumentException("Valor da parcela " + idParcela + " inválido. Esperado: " + valorEsperado + ", Informado: " + valorParcela);
            }

            // Registrar o pagamento da parcela específica
            statusParcelasPagas.put(idParcela, true);
            parcelasPagas.put(idParcela, valorParcela);

            // Atualizar saldo devedor do empréstimo
            double novoSaldo = emprestimo.getSaldoDevedorAtualizado() - valorParcela;
            emprestimo.setSaldoDevedorAtualizado(novoSaldo);

            return parcelasPagas;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar pagamento de parcela: " + e.getMessage(), e);
        }
    }

    // Método auxiliar para calcular o valor da parcela com juros/multa
    private double calcularValorParcelaComJuros(boolean vencida) {
        double valorBase = emprestimo.getValorParcela();
        if (vencida) {
            valorBase += valorBase * emprestimo.getTaxaJurosMora() + emprestimo.getTaxaMulta();
        }
        return valorBase;
    }

//    public static void main(String[] args) {
//        // Criando um cliente (assumindo que existe uma classe Cliente com CPF)
//        Cliente cliente = new Cliente();
//        cliente.setCpf("123.456.789-00");
//
//        // Criando um empréstimo
//        Emprestimo emprestimo = new Emprestimo();
//        emprestimo.setIdContrato(1L);
//        emprestimo.setValorEmprestimo(10000.0);
//        emprestimo.setQuantidadeParcelas(12);
//        emprestimo.setValorParcela(900.0);
//        emprestimo.setSaldoDevedorAtualizado(10000.0);
//        emprestimo.setDataInicio(LocalDate.now());
//        emprestimo.setStatusEmprestimo(StatusEmpEnum.APROVADO);
//        emprestimo.setTipoEmprestimo(TipoEmpEnum.PESSOAL);
//        emprestimo.setParcelaList(new ArrayList<>());
//        emprestimo.setTaxaJurosMora(0.02); // 2% de juros de mora
//        emprestimo.setTaxaMulta(50.0);     // R$50 de multa
//      // emprestimo.setCliente(cliente );     // Associando o cliente ao empréstimo
//
//        // Instanciando PagarParcela com o empréstimo
//        PagarParcela pagarParcela = new PagarParcela(emprestimo);
//
//        // Criando um mapa de parcelas a pagar
//        Map<Integer, Double> parcelasPagar = new HashMap<>();
//        parcelasPagar.put(1, 900.0); // Parcela 1 com valor normal
//
//        try {
//            // Testando o método pagamentoParcela
//            Map<Integer, Double> parcelasPagas = pagarParcela.pagamentoParcela(
//                    "123.456.789-00", // cpfCliente
//                    1,                // idParcela (parcela específica a pagar)
//                    12,               // quantidadeParcelas
//                    parcelasPagar     // mapa de parcelas a pagar
//            );
//
//            // Exibindo resultado
//            System.out.println("Parcelas pagas com sucesso:");
//            for (Map.Entry<Integer, Double> entry : parcelasPagas.entrySet()) {
//                System.out.println("Parcela " + entry.getKey() + ": R$" + entry.getValue());
//            }
//            System.out.println("Novo saldo devedor: R$" + emprestimo.getSaldoDevedorAtualizado());
//
//        } catch (Exception e) {
//            System.err.println("Erro no teste: " + e.getMessage());
//        }
//    }
//}
//
//// Classe Cliente simulada (caso não exista, você pode substituir pela sua implementação)
//class Cliente {
//    private String cpf;
//
//    public String getCpf() {
//        return cpf;
//    }
//
//    public void setCpf(String cpf) {
//        this.cpf = cpf;
//    }
}