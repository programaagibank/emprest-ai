# Documentações Emprest.AI 📄

# EMPRÉSTIMO CONSIGNADO E PESSOAL

## Índice
1. [Autores](#autores)
2. [Referências](#referências)
3. [1. Objetivo](#1-objetivo)
4. [2. Variáveis](#2-variáveis)
5. [3. Visão Geral do Funcionamento](#3-visão-geral-de-funcionamento)
6. [4. Dados Armazenados do Cliente](#4-dados-armazenados-do-cliente)
7. [5. Simulação de Empréstimo](#5-simulação-de-empréstimo)
8. [6. Concessão de Empréstimo](#6-concessão-de-empréstimo)
9. [7. Consulta de Dados de Empréstimo](#7-consulta-de-dados-de-empréstimo)
10. [8. Pagamento de Empréstimo](#8-pagamento-do-empréstimo)
11. [9. Refinanciamento de Empréstimo](#9-refinanciamento-do-empréstimo)
12. [10. Portabilidade](#10-portabilidade)
13. [11. Elegibilidade para Empréstimos](#11-elegibilidade-para-empréstimos)
14. [12. Cálculos](#12-cálculos)
15. [13. Glossário](#13-glossário)
16. [14. DER](#14-der-diagrama-entidade-relacionamento)

## AUTORES
- [@Dalleth Martins](https://github.com/dalleth-martinss)
- [@Josué Davi da Costa](https://github.com/josdcosta)
- [@Carollina Guedes](https://github.com/CarollinaGuedes)
- [@Victor Augusto Ferreira](https://github.com/Victor-augusto-ferreira)
- [@João Formigari](https://github.com/Joao-Formigari)


<br>

## REFERÊNCIAS
- [Planilha de cálculo](https://docs.google.com/spreadsheets/d/1Y_vrP424Qpyh_nWdp_xtSSbsdswpp4XKPIOVeIV9B4E/edit?usp=sharing)
- Leis e Regulamentações: Lei 10.820/2003 (base para consignados), Lei 14.509/2022 (margem consignável de 35%), Regulamentação INSS, Resoluções do Banco Central, Código de Defesa do Consumidor (art. 52, §2º para multa e juros mora).
- Leis e Regulamentações: Lei nº 8.078/1990: O Código de Defesa do Consumidor (CDC). Esta lei protege você quando contrata um empréstimo pessoal, garantindo informações claras, proibindo cobranças abusivas, protegendo contra problemas e ajudando a evitar o superendividamento.

<br>

## 1. OBJETIVO
O Emprest.AI é um backend projetado para gerenciar de forma eficiente e transparente o ciclo completo de empréstimos, abrangendo as modalidades Empréstimo Pessoal e Empréstimo Consignado, todas utilizando sistema Price de Amortização (Parcela Fixa). Suas funcionalidades incluem concessão de novos contratos, simulação de condições, consulta de dados, pagamento antecipado de parcela, refinanciamento (quando aplicável), portabilidade (para consignado e pessoal), cancelamento e gestão básica de inadimplência via multa e juros de mora, com critérios adaptados a cada modalidade.

<br>

## 2. VARIÁVEIS
Os parâmetros abaixo do sistema Emprest.AI (taxas baseadas em médias de mercado):

| Variável                   | Descrição                                                 | Valor Padrão            |
|----------------------------|-----------------------------------------------------------|-------------------------|
| jurosMinimoPessoal         | Taxa mínima de juros mensal (Empréstimo Pessoal)          | 8,49% ao mês            |
| jurosMaximoPessoal         | Taxa máxima de juros mensal (Empréstimo Pessoal)          | 9,99% ao mês            |
| jurosMinimoConsignado      | Taxa mínima de juros mensal (Empréstimo Consignado)       | 1,80% ao mês            |
| jurosMaximoConsignado      | Taxa máxima de juros mensal (Empréstimo Consignado)       | 2,14% ao mês            |
| valorMinimoPessoal         | Valor mínimo do Empréstimo Pessoal                        | R$ 100,00               |
| valorMaximoPessoal         | Valor máximo do Empréstimo Pessoal                        | R$ 20.000,00            |
| valorMinimoConsignado      | Valor mínimo do Empréstimo Consignado                     | R$ 1.000,00             |
| prazoMinimoPessoal         | Prazo mínimo em parcelas (Empréstimo Pessoal)             | 6 parcelas              |
| prazoMaximoPessoal         | Prazo máximo em parcelas (Empréstimo Pessoal)             | 30 parcelas             |
| prazoMinimoConsignado      | Prazo mínimo em parcelas (Empréstimo Consignado)          | 24 parcelas             |
| prazoMaximoConsignado      | Prazo máximo em parcelas (Empréstimo Consignado)          | 92 parcelas             |
| carenciaMaximaPessoal      | Período máximo de carência (Empréstimo Pessoal)           | 30 dias                 |
| carenciaMaximaConsignado   | Período máximo de carência (Empréstimo Consignado)        | 60 dias                 |
| idadeMaximaConsignado      | Idade máxima ao final (Empréstimo Consignado)             | 80 anos                 |
| idadeMaximaPessoal         | Idade máxima ao final (Empréstimo Pessoal)                | 75 anos                 |
| margemConsignavel          | Percentual da remuneração líquida para margem             | 35%                     |
| iof                        | Imposto sobre Operações Financeiras                       | 0,38% fixo + 0,0082% por dia (limitado ao prazo do contrato ou 365 dias, o que for menor) |
| percentualRendaPessoal     | Percentual máximo da renda líquida para parcela (Empréstimo Pessoal) | 30%         |
| percentualMinimoRefinanciamento | Percentual mínimo de parcelas pagas para refinanciamento | 20%                 |
| percentualJurosMora        | Percentual máximo de juros mora por dia                   | 0,033%/dia (1% ao mês)  |
| percentualMultaAtraso      | Percentual máximo para multa por atraso                   | 2%                      |



<br>

## 3. VISÃO GERAL DE FUNCIONAMENTO
O sistema é estruturado em áreas principais, aplicáveis a ambas as modalidades com ajustes específicos:

- Concessão de Empréstimos: Análise de crédito adaptada (Consignado: margem consignável; Pessoal: score e renda). Simulação e aprovação de contratos.
- Consulta de Empréstimos: Acompanhamento de status, parcelas e histórico de pagamentos.
- Pagamento de Empréstimos: Registro de pagamentos, incluindo antecipação de parcelas futuras, conforme opção do cliente.
- Refinanciamento: Renegociação de contratos existentes (ambas as modalidades).
- Portabilidade: Transferência de consignados e empréstimo pessoal para outro banco.
- Cancelamento de Contrato: Gestão de desistências ou finalizações.

<br>

## 4. DADOS ARMAZENADOS DO CLIENTE
CPF depende do governo, portanto é interessante pensar em relação a mudanças do cadastro ou regras pelo governo. O autoincremente seria uma saida para evitar problemas futuros.
```json
{
  "idCliente": "[cpf ou autoincremente]",
  "nome": "[nome]",
  "remuneracaoLiquidaMensal": "[valor em reais]",
  "dataNascimento": "[número]",
  "rendaPessoasFamilia": "[valor em reais]",
  "qtdePessoasFamilia": "[número]",
  "tipoVinculo": "[aposentado, servidor, pensionista, empregado ou nulo]",
  "scoreCredito": "[número entre 0 e 1000, consultado via analisador de risco]"
}
```

<br>

## 5. SIMULAÇÃO DE EMPRÉSTIMO
O cliente / setores do banco envia os dados para o sistema simular as condições do empréstimo.
### 5.1. Uso de dados para simular o emprestimo
#### Empréstimo Consignado | Empréstimo Pessoal
```json
{
  "idCliente": "[cpf ou autoincremente]",
  "valorEmprestimo": "[valor em reais]",
  "tipoEmprestimo": "[consignado ou pessoal]",
  "quantidadeParcelas": "[número]",
  "contratarSeguro": "[true ou false]",
  "dataInicioPagamento": "[data no formato DD/MM/AAAA]"
}
```

### 5.2. Processo Passo a Passo
**Passo 1: Consulta de Dados do Cliente**  
O sistema busca o `idCliente` na base e retorna `remuneracaoLiquidaMensal`, `dataNascimento`, `tipoVinculo` (para consignado) e por meio do `idCliente` consulta o analisador de risco que retorna o `scoreCredito` (para pessoal). Se não encontrado, "Erro: Cliente não encontrado".

**Passo 2: Verificação Inicial de Elegibilidade**  
Empréstimo Consignado:  
- Aplica [11.1.1. Margem Consignável](#1111-margem-consignável).  
- Aplica [11.1.2. Idade Máxima](#1112-idade-máxima).  
- Aplica [11.1.3. Quantidade de Parcelas](#1113-quantidade-de-parcelas).  
- Aplica [11.1.5. Tipo de Vínculo](#1115-tipo-de-vínculo).  
- Calcula dias de carência e aplica [11.1.6. Carência](#1116-carência).  

Empréstimo Pessoal:  
- Aplica [11.2.1. Idade Máxima](#1121-idade-máxima).  
- Aplica [11.2.2. Valor do Empréstimo](#1122-valor-do-empréstimo).  
- Aplica [11.2.3. Quantidade de Parcelas](#1123-quantidade-de-parcelas).  
- Aplica [11.2.5. Score de Crédito](#1125-score-de-crédito).  
- Aplica [11.2.6. Capacidade de Pagamento](#1126-capacidade-de-pagamento).  
- Calcula dias de carência e aplica [11.2.7. Carência](#1127-carência).  

**Passo 3: Determinação da Capacidade de Pagamento**  
- Pessoal: Executa [12.1. Capacidade de Pagamento](#121-capacidade-de-pagamento).  
- Consignado: Executa [12.2. Margem Consignável](#122-margem-consignável).  

**Passo 4: Definição da Taxa de Juros**  
- Consignado: Aplica [12.3. Taxa de Juros Mensal](#123-taxa-de-juros-mensal).  
- Pessoal: Aplica [12.3. Taxa de Juros Mensal](#123-taxa-de-juros-mensal).  

**Passo 5: Cálculo do Custo do Seguro**  
- Se `contratarSeguro = true`, aplica [12.4. Custo do Seguro](#124-custo-do-seguro).  

**Passo 6: Cálculo do IOF**  
- Executa [12.5. IOF](#125-iof).  

**Passo 7: Cálculo do Valor Total Financiado**  
- Aplica [12.6. Valor Total Financiado](#126-valor-total-financiado).  

**Passo 8: Cálculo da Parcela Mensal**  
- Executa [12.7. Parcela Mensal](#127-parcela-mensal).  

**Passo 9: Geração da Tabela de Parcelas**  
Com base no `valorTotalFinanciado`, `quantidadeParcelas`, `taxaJurosMensal` e `dataInicioPagamento`, o sistema calcula e gera uma tabela detalhando cada parcela, incluindo:  
1. **Número da parcela**  
2. **Data de vencimento** (mensal a partir da `dataInicioPagamento`)  
3. **Valor da parcela**  
4. **Juros da parcela**  
5. **Amortização do principal**  
6. **Saldo devedor restante**

**Passo 10: Retorno da Simulação**  
- Retorna os valores calculados sem gravar o contrato.
### 5.3. Saída
#### Empréstimo Consignado/Pessoal
```json
{
  "idCliente": "[cpf ou autoincremente]",
  "valorEmprestimo": "[valor em reais]",
  "tipoEmprestimo": "[consignado ou pessoal]",
  "quantidadeParcelas": "[número]",
  "taxaJurosMensal": "[valor decimal]",
  "custoSeguro": "[valor em reais]",
  "iof": "[valor em reais]",
  "valorTotalFinanciado": "[valor em reais]",
  "parcelaMensal": "[valor em reais]",
  "cetMensal": "[valor decimal]",
  "saldoDevedorAtualizado": "[valor em reais]"
  "tabelaParcelas": [
    {
      "numeroParcela": "[número]",
      "dataVencimento": "[data no formato DD/MM/AAAA]",
      "valorPresenteParcela": "[valor em reais]",
      "juros": "[valor em reais]",
      "amortizacao": "[valor em reais]",
    }
    "... Demais parcelas"
  ],
  "mensagem": "Simulação realizada com sucesso."
}
```

<br>

## 6. CONCESSÃO DE EMPRÉSTIMO
O cliente / setores do banco solicita formalmente a concessão do empréstimo ao sistema.
### 6.1. Uso de dados para a concessão de emprestimo
#### Empréstimo Consignado | Empréstimo Pessoal

```json
{
  "idCliente": "[cpf ou autoincremente]",
  "valorEmprestimo": "[valor em reais]",
  "tipoEmprestimo": "[consignado ou pessoal]",
  "quantidadeParcelas": "[número]",
  "contratarSeguro": "[true ou false]",
  "dataInicioPagamento": "[data no formato DD/MM/AAAA]"
}
```

### 6.2. Processo Passo a Passo
- Executa os passos 1 a 9 da Simulação (ver [5.2](#52-processo-passo-a-passo)) para determinar elegibilidade, cálculos e tabela de parcelas.
- **Registro do Contrato**: Cria o contrato para associação do pagamento (folha para consignado, débito automático para pessoal).

### 6.3. Saída
(Idêntica à Simulação, com "mensagem": "Empréstimo concedido com sucesso." e com registro da `dataContratacao`)

### 6.4. Cancelamento de Contrato em 7 Dias
O cliente solicita o cancelamento do contrato ao sistema dentro do prazo legal.
  #### 6.4.1. Objetivo
  Permitir que o cliente cancele o contrato de empréstimo (consignado ou pessoal) em até 7 dias corridos após a concessão, sem custos adicionais, desde que respeitadas as condições legais e operacionais do sistema.
  
  #### 6.4.2. Regras Gerais
  - **Prazo**: O cancelamento deve ser solicitado em até 7 dias corridos a partir da data de concessão do empréstimo (data de registro do contrato no sistema).
  - **Condições**:
    - O valor liberado deve ser devolvido integralmente pelo cliente, descontando eventuais custos já incorridos (ex.: IOF pago ao governo, se houver).
  - **Custos**: Sem multa ou juros adicionais, conforme CDC. Apenas custos operacionais obrigatórios (ex.: IOF) podem ser retidos, se aplicável.
   
  #### 6.4.3. Verificação do Prazo
  - Calcula a diferença entre `dataSolicitacao` e a data de concessão do empréstimo (obtida do registro do contrato em 6.2).
  - Se a diferença for maior que 7 dias corridos, retorna "Erro: Prazo de cancelamento expirado".

<br>

## 7. CONSULTA DE DADOS DE EMPRÉSTIMO
O cliente / setores do banco consulta os dados do empréstimo diretamente no sistema. Essas consultas podem envolver vários filtros, como status, atrasos, etc... abaixo a consulta principal que retornará todos valores de um emprestimo.

### 7.1. Uso de dados para consulta de empréstimo
```json
{
  "idCliente": "[cpf ou autoincremente]",
  "idEmprestimo": "[identificador único]"
}
```
### 7.2. Processo Passo a Passo

1. **Consulta de Dados do Cliente:**
   - Valida `idCliente`. Se não encontrado, retorna "Erro: Cliente não encontrado".

2. **Verificação do Empréstimo:**
   - Busca `idEmprestimo`. Se inválido, retorna "Erro: Empréstimo não encontrado ou inválido".

3. **Recuperação dos Dados do Empréstimo:**
   - Obtém dados do contrato, incluindo a tabela de parcelas.

4. **Consulta do Histórico de Pagamentos:**
   - Verifica parcelas pagas e restantes, atualizando o status de cada parcela na tabela ("paga" ou "pendente").

5. **Cálculo do Saldo Devedor:**  
   - Executa [12.8. Saldo Devedor](#128-saldo-devedor).

6. **Retorno dos Dados:**
   - Compila e retorna as informações, incluindo a tabela de parcelas atualizada.


### 7.3. Saída
```json
{
  "idCliente": "[cpf ou autoincremente]",
  "idEmprestimo": "[identificador único]",
  "valorEmprestimo": "[valor em reais]",
  "quantidadeParcelas": "[número]",
  "taxaJurosMensal": "[valor decimal]",
  "custoSeguro": "[valor em reais]",
  "iof": "[valor em reais]",
  "valorTotalFinanciado": "[valor em reais]",
  "parcelaMensal": "[valor em reais]",
  "totalParcelasPagas": "[número]",
  "totalParcelasRestantes": "[número]",
  "saldoDevedorAtualizado": "[valor em reais]",
  "dataContratacao": "[data no formato DD/MM/AAAA]"
  "tabelaParcelas": [
    {
      "numeroParcela": "[número]",
      "dataVencimento": "[data no formato DD/MM/AAAA]",
      "valorPresenteParcela": "[valor em reais]",
      "juros": "[valor em reais]",
      "multa": "[valor em reais ou null]",
      "jurosMora": "[valor em reais ou null]",
      "amortizacao": "[valor em reais]",
      "status": "[paga ou pendente]",
      "dataPagamento": "[data no formato DD/MM/AAAA ou null]"
    }
  ],
  "mensagem": "Consulta realizada com sucesso."
}
```

<br>

## 8. PAGAMENTO DO EMPRÉSTIMO
O cliente / setores do banco informa o pagamento das parcelas para registro no sistema.
### 8.1. Uso de dados para pagamento do empréstimo
#### Pagamento Parcela
```json
{
  "idCliente": "[cpf ou autoincremente]",
  "idEmprestimo": "[identificador único]",
  "quantidadeParcelas": "[número de parcelas a pagar]",
  "parcelaAtual": "[true para incluir a atual após atrasadas, false para apenas últimas após atrasadas]",
  "dataPagamento": "[data no formato DD/MM/AAAA]"
}
```


### 8.2. Processo Passo a Passo
1. **Consulta de Dados do Cliente**: Valida `idCliente`. Se não encontrado, retorna "Erro: Cliente não encontrado".
2. **Verificação do Empréstimo**: Busca `idEmprestimo`. Se inválido, retorna "Erro: Empréstimo não encontrado ou inválido".
3. **Validação do Status do Empréstimo**: Confirma se está ativo. Se todas as parcelas estiverem quitadas, retorna "Erro: Empréstimo já liquidado".
4. **Processamento do Pagamento**:  
   - **4.1. Identificação das Parcelas a Pagar**:  
     - Identifica a próxima parcela a vencer (atual) com base na `dataPagamento` e todas as parcelas pendentes.  
     - **Priorização de Atrasadas**: Verifica se há parcelas atrasadas (`dataVencimento < dataPagamento` e status "pendente"). Se houver, seleciona todas até a mais recente (em ordem crescente) e consome `quantidadeParcelas` para quitá-las primeiro. Exemplo: Parcelas 3 e 4 atrasadas, `quantidadeParcelas = 4`. Quita 3 e 4 (2 parcelas), sobram 2 para o próximo passo.  
     - **Após Atrasadas (ou se não houver)**: Calcula parcelas restantes disponíveis em `quantidadeParcelas`. Se `parcelaAtual = true`, inclui a atual (se pendente); se `false`, quita as últimas pendentes (da última para a primeira). Ajusta para o máximo de parcelas pendentes, se exceder.  
   - Marca as parcelas selecionadas como "paga" na `dataPagamento`, considerando o pagamento já processado externamente.
5. **Atualização do Contrato**: Atualiza a tabela de parcelas com `multa` e `jurosMora` (se aplicável, informados externamente), refletindo as parcelas quitadas e recalculando o saldo devedor.
6. **Retorno da Confirmação**: Retorna o status atualizado, incluindo a tabela de parcelas revisada.

### 8.3. Saída
#### Pagamento Parcela
```json
{
  "idCliente": "[cpf ou autoincremente]",
  "idEmprestimo": "[identificador único]",
  "valorEmprestimo": "[valor em reais]",
  "quantidadeParcelas": "[número]",
  "taxaJurosMensal": "[valor decimal]",
  "custoSeguro": "[valor em reais]",
  "iof": "[valor em reais]",
  "valorTotalFinanciado": "[valor em reais]",
  "parcelaMensal": "[valor em reais]",
  "totalParcelasPagas": "[número]",
  "totalParcelasRestantes": "[número]",
  "saldoDevedorAtualizado": "[valor em reais]",
  "dataContratacao": "[data no formato DD/MM/AAAA]"
  "tabelaParcelas": [
    {
      "numeroParcela": "[número]",
      "dataVencimento": "[data no formato DD/MM/AAAA]",
      "valorPresenteParcela": "[valor em reais]",
      "juros": "[valor em reais]",
      "multa": "[valor em reais ou null]",
      "jurosMora": "[valor em reais ou null]",
      "amortizacao": "[valor em reais]",
      "status": "[paga ou pendente]",
      "dataPagamento": "[data no formato DD/MM/AAAA ou null]"
    }
  ],
  "mensagem": "Pagamento da parcela registrado com sucesso."
}
```

### 8.4. Tratamento de Atrasos
- Se uma parcela não for paga até a `dataVencimento`, aplica-se:
  - **Multa**: 2% sobre o valor da parcela (limite do Código de Defesa do Consumidor, art. 52, §2º).
  - **Juros de Mora**: 1% ao mês (0,033% ao dia) sobre o valor da parcela, proporcional aos dias de atraso.
- Exemplo: Parcela de R$ 305.96 vencida em 01/09/2025, paga em 11/09/2025 (10 dias de atraso):
  - Multa: `305.96 * 0,02 = 6,12`.
  - Juros de mora: `305.96 * 0,00033 * 10 = 1,00`.
  - Total a pagar: `305.96 + 6,12 + 1,00 = 313,08`.

<br>

## 9. REFINANCIAMENTO DO EMPRÉSTIMO
O cliente / setores do banco solicita o refinanciamento do contrato existente ao sistema.
### 9.1. Uso de dados para refinanciamento do empréstimo

```json
{
  "idCliente": "[cpf ou autoincremente]",
  "idEmprestimoOriginal": "[identificador único]",
  "novoValorEmprestimo": "[valor em reais]",
  "novaQuantidadeParcelas": "[número]",
  "contratarSeguro": "[true ou false]",
  "dataInicioPagamento": "[data no formato DD/MM/AAAA]"
}
```

### 9.2. Processo Passo a Passo
1. **Consulta de Dados do Cliente**: Valida `idCliente`.
2. **Verificação do Empréstimo Original**: Busca `idEmprestimoOriginal`.
3. **Validação do Status**: Confirma se ativo.
4. **Verificação de Elegibilidade**: Aplica [11.3.1. Percentual Mínimo Pago](#1131-percentual-mínimo-pago), [11.1.2](#1112-idade-máxima), [11.1.3](#1113-quantidade-de-parcelas), [11.1.6](#1116-carência) (consignado) ou [11.2.3](#1123-quantidade-de-parcelas), [11.2.7](#1127-carência) (pessoal).
5. **Cálculo do Saldo Devedor**: Executa [12.8. Saldo Devedor](#128-saldo-devedor) do contrato original.
6. **Determinação da Capacidade**: Consignado: [12.2](#122-margem-consignável); Pessoal: [12.1](#121-capacidade-de-pagamento).
7. **Definição da Taxa de Juros**: Aplica [12.3](#123-taxa-de-juros-mensal).
8. **Cálculo do Custo do Seguro**: Aplica [12.4](#124-custo-do-seguro) se `contratarSeguro = true`.
9. **Cálculo do IOF**: Executa [12.5](#125-iof).
10. **Cálculo do Valor Total Financiado**: `valorEmprestimo = saldoDevedorOriginal + novoValorEmprestimo` (limitado por margem consignável ou capacidade de pagamento); Aplica [12.6](#126-valor-total-financiado).
11. **Cálculo da Nova Parcela**: Executa [12.7](#127-parcela-mensal).
12. **Geração da Tabela de Parcelas**: Baseada em `valorTotalFinanciado`, `novaQuantidadeParcelas`, `taxaJurosMensal` e `dataInicioPagamento`.
13. **Registro do Refinanciamento**: Cria novo contrato e marca o original como "refinanciado".

### 9.3. Saída

```json
{
  "idCliente": "[cpf ou autoincremente]",
  "idEmprestimo": "[identificador único]",
  "idEmprestimoOriginal": "[identificador único]", "(Autoreferenciamento)"
  "valorEmprestimo": "[valor em reais]",
  "quantidadeParcelas": "[número]",
  "taxaJurosMensal": "[valor decimal]",
  "custoSeguro": "[valor em reais]",
  "iof": "[valor em reais]",
  "valorTotalFinanciado": "[valor em reais]",
  "parcelaMensal": "[valor em reais]",
  "totalParcelasPagas": "[número]",
  "totalParcelasRestantes": "[número]",
  "saldoDevedorAtualizado": "[valor em reais]",
  "dataContratacao": "[data no formato DD/MM/AAAA]"
  "tabelaParcelas": [
    {
      "numeroParcela": "[número]",
      "dataVencimento": "[data no formato DD/MM/AAAA]",
      "valorPresenteParcela": "[valor em reais]",
      "juros": "[valor em reais]",
      "multa": "[valor em reais ou null]",
      "jurosMora": "[valor em reais ou null]",
      "amortizacao": "[valor em reais]",
      "status": "[paga ou pendente]",
      "dataPagamento": "[data no formato DD/MM/AAAA ou null]"
    }
  ],
  "mensagem": "Refinanciamento realizado com sucesso."
}
```

<br>
<br>

## 10. Portabilidade
### Regras
A portabilidade permite transferir contratos de empréstimo entre instituições, com o Emprest.AI gerenciando os registros e se comunicando com o setor financeiro da instituição para coordenar quitações e notificações. Suporta:

- **Recebendo**: Registra contratos portados após quitação pelo setor financeiro.
- **Enviando**: Atualiza contratos como "liquidado por portabilidade" após confirmação do setor financeiro.

### 10.1. Recebendo Portabilidade
O Setor Interno envia os dados ao sistema após coordenar com o cliente e quitar o contrato original.
#### Processo

1. Cliente solicita portabilidade à instituição.
2. Emprest.AI solicita ao setor interno os dados do contrato original (saldo [12.8](#128-saldo-devedor), parcelas, taxa).
3. Emprest.AI simula condições ([11.1.4](#1114-taxa-de-juros), [11.2.4](#1124-taxa-de-juros), [11.1.3](#1113-quantidade-de-parcelas), [11.2.3](#1123-quantidade-de-parcelas), [12.4](#124-custo-do-seguro), [12.5](#125-iof), [12.6](#126-valor-total-financiado), [12.7](#127-parcela-mensal)) e envia ao setor interno.
4. Após confirmação do cliente, setor interno quita o saldo e notifica o Emprest.AI.
5. Emprest.AI registra o contrato como "portado" e solicita ao setor interno ajustes (ex.: folha ou débito).

#### Elegibilidade
  - Deve estar em conformidade com as regras [11.4.1.](#1141-sem-atrasos) e [11.4.2.](#1142-contrato-ativo)
  - **Consignado**: [11.1.1](#1111-margem-consignável), [11.1.2](#1112-idade-máxima).
  - **Pessoal**: [11.2.6](#1126-capacidade-de-pagamento),[11.1.2](#1112-idade-máxima), [11.2.5](#1125-score-de-crédito).

#### Uso de dados para elegibilidade
```json
{
  "idCliente": "[cpf ou autoincremente]",
  "idEmprestimoOriginal": "[id original]",
  "tipoEmprestimo": "[consignado ou pessoal]",
  "novoValorEmprestimo": "[saldo em reais]",
  "novaQuantidadeParcelas": "[número]",
  "contratarSeguro": "[true/false]",
  "dataInicioPagamento": "[DD/MM/AAAA]",
  "dataQuitacaoOriginal": "[DD/MM/AAAA]"
}
```

#### Saída
```json
{
  "idCliente": "[cpf ou autoincremente]",
  "idNovoEmprestimo": "[id novo]",
  "valorEmprestimo": "[saldo quitado]",
  "quantidadeParcelas": "[número]",
  "taxaJurosMensal": "[decimal]",
  "parcelaMensal": "[valor em reais]",
  "status": "portado",
  "mensagem": "Contrato portado registrado."
}
```

### 10.2. Enviando Portabilidade
O Setor Interno solicita ao sistema os dados do contrato para coordenar a quitação com outra instituição.
#### Processo

1. Cliente solicita portabilidade a outra instituição.
2. Setor interno recebe do Emprest.AI os dados do contrato.
3. Emprest.AI fornece saldo [12.8](#128-saldo-devedor) e parcelas ao setor interno.
4. Setor interno confirma quitação por outra instituição.
5. Emprest.AI atualiza o contrato como "liquidado por portabilidade" e solicita ajustes ao setor interno (ex.: folha ou débito).

#### Elegibilidade
  - Deve estar em conformidade com as regras [11.4.1.](#1141-sem-atrasos) e [11.4.2.](#1142-contrato-ativo)
  - **Consignado**: [11.1.1](#1111-margem-consignável), [11.1.2](#1112-idade-máxima).
  - **Pessoal**: [11.2.6](#1126-capacidade-de-pagamento), [11.2.5](#1125-score-de-crédito).

#### Uso de dados do Setor Interno
```json
{
  "idCliente": "[cpf ou autoincremente]",
  "idEmprestimo": "[id no Emprest.AI]"
}
```

#### Saída
```json
{
  "idCliente": "[cpf ou autoincremente]",
  "idEmprestimo": "[id]",
  "saldoDevedorQuitado": "[valor em reais]",
  "dataQuitacao": "[DD/MM/AAAA]",
  "status": "liquidado por portabilidade",
  "mensagem": "Contrato quitado e transferido."
}
```

### Exemplo

#### Recebendo
Saldo de R$ 5.000,00 (20 parcelas, 2,14%). Emprest.AI simula 1,90%, R$ 287,50/parcela. Setor interno quita e Emprest.AI registra como "portado".

#### Enviando
Saldo de R$ 5.000,00 (20 parcelas, 1,90%). Setor interno confirma quitação, Emprest.AI marca como "liquidado por portabilidade".

<br>
<br>

## 11. Elegibilidade para Empréstimos

### 11.1. Empréstimo Consignado

#### 11.1.1. Margem Consignável
    Parcela ≤ (remuneracaoLiquida * margemConsignavel) - soma de parcelas ativas.

#### 11.1.2. Idade Máxima
    Idade = dataAtual - dataNascimento
    idade + quantidadeParcelas / 12 ≤ idadeMaximaConsignado: A idade aproximada do cliente ao final do contrato não deve exceder idadeMaximaConsignado.
    
#### 11.1.3. Quantidade de Parcelas
    prazoMinimoConsignado ≤ quantidadeParcelas ≤ prazoMaximoConsignado

#### 11.1.4. Taxa de Juros
    jurosMinimoConsignado ≤ Taxa mensal ≤ jurosMaximoConsignado.

#### 11.1.5. Tipo de Vínculo
    "Aposentado", "servidor público" ou "pensionista".

#### 11.1.6. Carência
    Dias até o primeiro pagamento ≤ carenciaMaximaConsignado.

### 11.2. Empréstimo Pessoal

#### 11.2.1. Idade Máxima
    Idade = dataAtual - dataNascimento
    idade + quantidadeParcelas / 12 ≤ idadeMaximaPessoal: A idade aproximada do cliente ao final do contrato não deve exceder idadeMaximaPessoal.
    
#### 11.2.2. Valor do Empréstimo
    valorMinimoPessoal ≤ valorEmprestimo ≤ valorMaximoPessoal, conforme score:

    | Faixa de Score | Nível de Risco     | Limite Crédito     |
    |----------------|--------------------|--------------------|
    | 0-200          | Altíssimo risco    | N/A                |
    | 201-400        | Alto risco         | R$ 100 a R$ 1.000  |
    | 401-600        | Risco moderado     | R$ 100 a R$ 5.000  |
    | 601-800        | Risco baixo        | R$ 100 a R$ 15.000 |
    | 801-1000       | Risco muito baixo  | R$ 100 a R$ 20.000 |

#### 11.2.3. Quantidade de Parcelas
    prazoMinimoPessoal ≤ quantidadeParcelas ≤ prazoMaximoPessoal, conforme score:

    | Faixa de Score | Nível de Risco     | Meses    |
    |----------------|--------------------|----------|
    | 0-200          | Altíssimo risco    | N/A      |
    | 201-400        | Alto risco         | 6 a 12   |
    | 401-600        | Risco moderado     | 6 a 18   |
    | 601-800        | Risco baixo        | 6 a 24   |
    | 801-1000       | Risco muito baixo  | 6 a 30   |

#### 11.2.4. Taxa de Juros
    jurosMinimoPessoal ≤ Taxa mensal ≤ jurosMaximoPessoal, conforme score:
    
    | Faixa de Score | Nível de Risco     | Taxa               |
    |----------------|--------------------|--------------------|
    | 0-200          | Altíssimo risco    | N/A                |
    | 201-400        | Alto risco         | 9,99%              |
    | 401-600        | Risco moderado     | 9,49% a 9,99%      |
    | 601-800        | Risco baixo        | 8,99% a 9,49%      |
    | 801-1000       | Risco muito baixo  | 8,49% a 8,99%      |

#### 11.2.5. Score de Crédito
    scoreCredito ≥ 201.

#### 11.2.6. Capacidade de Pagamento
    Parcela ≤ rendaTotalLiquida * percentualRendaPessoal.

#### 11.2.7. Carência
    Dias até o primeiro pagamento ≤ carenciaMaximaPessoal.

<br>

### 11.3. REFINANCIAMENTO (COMUM)

#### 11.3.1. Percentual Mínimo Pago
    ≥ 20% das parcelas pagas.

<br>

### 11.4. Portabilidade (Empréstimo Consignado, Empréstimo Pessoal)

#### 11.4.1. Sem atrasos e ativo
    contrato deve estar ativo
    
#### 11.4.2. Contrato ativo
    contrato deve estar sem atrasos de parcelas

<br>

## 12. CÁLCULOS

### 12.1. Capacidade de Pagamento (Empréstimo Pessoal)
    rendaTotalLiquida = remuneracaoLiquida  
    capacidadeMaxima = rendaTotalLiquida * percentualRendaPessoal

### 12.2. Margem Consignável (Empréstimo Consignado)
    margemMaxima = remuneracaoLiquida * margemConsignavel - Parcela de Empréstimos Ativos

### 12.3. Taxa de Juros Mensal
    **Consignado**:  
        taxaJurosMensal = 0,018 + 0,00005 * (quantidadeParcelas - prazoMinimoConsignado), limitada a 2,14%.

    **Pessoal**:  
        Taxa = TaxaMax - [(TaxaMax - TaxaMin) * (Score - ScoreMin)] / (ScoreMax - ScoreMin)
        | Faixa de Score | Nível de Risco     | Taxa               |
        |----------------|--------------------|--------------------|
        | 0-200          | Altíssimo risco    | N/A                |
        | 201-400        | Alto risco         | 9,99%              |
        | 401-600        | Risco moderado     | 9,49% a 9,99%      |
        | 601-800        | Risco baixo        | 8,99% a 9,49%      |
        | 801-1000       | Risco muito baixo  | 8,49% a 8,99%      |
      Exemplos:
        Score 401: 0,0999 - [0,005 * (401 - 401)] / 199 = 0,0999 (9,99%)
        Score 500: 0,0999 - [0,005 * (500 - 401)] / 199 = 0,0999 - 0,0025 = 0,0974 (9,74%)
        Score 600: 0,0999 - [0,005 * (600 - 401)] / 199 = 0,0999 - 0,005 = 0,0949 (9,49%)

### 12.4. Custo do Seguro
    CustoSeguro = valorEmprestimo * (0,0025 + 0,00005 * idade) * (quantidadeParcelas / 12)

### 12.5. IOF
    percentualFixo = 0,0038  
    percentualVariado = 0,000082  
    IOF = (percentualFixo * valorEmprestimo) + (percentualVariado * valorEmprestimo * quantidadeParcelas em dias, máx. 365)

### 12.6. Valor Total Financiado
    ValorTotalFinanciado = valorEmprestimo + IOF + CustoSeguro

### 12.7. Parcela Mensal
    ParcelaMensal = [ValorTotalFinanciado * TaxaJurosMensal] / [1 - (1 + TaxaJurosMensal)^(-quantidadeParcelas)]

### 12.8. Saldo Devedor
    SaldoDevedor = ParcelaMensal * [1 - (1 + TaxaJurosMensal)^(-quantidadeParcelasRestantes)] / TaxaJurosMensal

### 12.9. Juros Mora e Multa por Atraso
    Multa = valorParcela * percentualMultaAtraso  
    Juros de mora = valorParcela * percentualJurosMora * diasAtraso

## 13. Glossário

#### Amortização
Redução gradual do saldo devedor de um empréstimo através de pagamentos periódicos.

#### Capacidade de Pagamento
Valor máximo que um cliente pode comprometer com o pagamento de um empréstimo, calculado com base em sua renda líquida.

#### Carência
Período inicial de um empréstimo durante o qual o cliente não precisa fazer pagamentos.

#### CET (Custo Efetivo Total)
Custo total de um empréstimo, incluindo juros, taxas e outros encargos, expresso como uma taxa percentual anual.

#### Consignado
Tipo de empréstimo cujas parcelas são descontadas diretamente da folha de pagamento ou benefício do cliente.

#### IOF (Imposto sobre Operações Financeiras)
Imposto federal cobrado sobre operações de crédito, câmbio, seguros e títulos mobiliários.

#### Juros de Mora
Encargos cobrados pelo atraso no pagamento de uma parcela de empréstimo.

#### Margem Consignável
Percentual máximo da renda líquida que pode ser comprometido com o pagamento de empréstimos consignados.

#### Multa por Atraso
Valor fixo ou percentual cobrado pelo atraso no pagamento de uma parcela de empréstimo.

#### Parcela
Valor periódico a ser pago pelo cliente para quitar um empréstimo.

#### Refinanciamento
Renegociação de um contrato de empréstimo existente, geralmente para obter melhores condições de pagamento.

#### Renda Líquida
Valor da renda total após a dedução de impostos e outras despesas.

#### Saldo Devedor
Valor total que o cliente ainda deve em um empréstimo.

#### Score de Crédito
Pontuação que indica o risco de inadimplência de um cliente, utilizada para avaliar a concessão de crédito.

#### Taxa de Juros
Percentual cobrado sobre o valor emprestado, representando o custo do empréstimo.

#### Valor Financiado
Valor total do empréstimo, incluindo o valor principal, juros e outros encargos.

#### ValorPresenteParcela
Valor da parcela abatido dos juros futuros, calculado como parte do método Price e elevado a (1 + taxa)^n, onde n é o número de períodos.

## 14. DER (Diagrama Entidade Relacionamento)

![Diagrama -Projeto-AgiBank drawio (7)](https://github.com/user-attachments/assets/cd67f76d-0d0d-43f9-871c-6612edb97547)
