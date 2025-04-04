package br.com.emprestai.service;

import br.com.emprestai.enums.VinculoEnum;
import br.com.emprestai.exception.ValidationException;
import br.com.emprestai.service.elegibilidade.ValidatorConsignado;
import br.com.emprestai.service.elegibilidade.ValidatorPessoal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ElegibilidadeTest {

        // Testes para Empréstimo Consignado

        @Test
        void testMargemConsignavel() {
            double rendaLiquida = 2500.0;
            double valorParcela = 1000.00; // Acima de 35% (875.0)
            double parcelasAtivas = 0.0;

            assertThrows(ValidationException.class, () ->
                    ValidatorConsignado.verificarMargemEmprestimoConsig(valorParcela, rendaLiquida, parcelasAtivas)
            );
        }


        @Test
        void testTaxaDeJuros() {
            double taxaJuros = 1.84; // Dentro de 1.80% a 2.14%
            assertDoesNotThrow(() -> ValidatorConsignado.verificarTaxaJurosEmprestimoConsig(taxaJuros));
        }


        @Test
        void testTipoDeVinculo() {
            VinculoEnum vinculo = VinculoEnum.APOSENTADO;
            assertDoesNotThrow(() -> ValidatorConsignado.verificarVinculoEmprestimoConsig(vinculo));
        }

        @Test
        void testCarencia() {
            int carencia = 30; // Menor que 60
            assertDoesNotThrow(() -> ValidatorConsignado.verificarCarenciaEmprestimoConsig(carencia));
        }

        @Test
        void testValorMinimoParaEmprestimoConsignado() {
            double valorSolicitado = 1500.0; // Acima de R$1000
            assertDoesNotThrow(() -> ValidatorConsignado.verificarValorMinimoEmprestimo(valorSolicitado));
        }

        @Test
        void testVerificacaoDaElegibilidadeParaEmprestimoConsignado() {
            double rendaLiquida = 2500.0;
            double valorParcela = 875.0;
            double parcelasAtivas = 0.0;
            int idade = 30;
            int parcelas = 24;
            double taxaJuros = 1.86;
            VinculoEnum vinculo = VinculoEnum.APOSENTADO;
            int carencia = 30;
            double valorSolicitado = 1500.0;
            assertDoesNotThrow(() -> ValidatorConsignado.verificarElegibilidadeConsignado(
                    rendaLiquida, valorParcela, parcelasAtivas, idade, parcelas, taxaJuros, vinculo, carencia, valorSolicitado
            ));
        }

         // Testes para Empréstimo Pessoal

             @Test
          void testVerificarRendaMinimaPessoal() {
          double rendaLiquida = 2000.0; // Acima de R$1000
          assertDoesNotThrow(() -> ValidatorPessoal.verificarRendaMinimaPessoal(rendaLiquida));
          }


          @Test
         void testVerificarComprometimentoPessoal() {
         double rendaLiquida = 2000.0;
         double valorParcela = 600.0; // 30% de 2000
        assertDoesNotThrow(() -> ValidatorPessoal.verificarComprometimentoPessoal(valorParcela, rendaLiquida));
          }


           @Test
       void testVerificarIdadePessoal() {
           int idade = 35;
        int parcelas = 12; // 1 ano, idade final = 36
        assertDoesNotThrow(() -> ValidatorPessoal.verificarIdadePessoal(idade, parcelas));
        }


      @Test
      void testVerificarScorePessoal() {
       int score = 300; // Acima de 201
      assertDoesNotThrow(() -> ValidatorPessoal.verificarScorePessoal(score));
       }

     @Test
     void testVerificarParcelasPessoal() {
       int parcelas = 12;
       int score = 700; // 601-800 permite até 24
       assertDoesNotThrow(() -> ValidatorPessoal.verificarParcelasPessoal(parcelas, score));
      }


     @Test
      void testVerificarElegibilidadePessoal() {
        double rendaLiquida = 2000.0;
          double valorParcela = 600.0;
         int idade = 35;
          int parcelas = 12;
       int score = 700;
     assertDoesNotThrow(() -> ValidatorPessoal.verificarElegibilidadePessoal(rendaLiquida, valorParcela, idade, parcelas, score
     ));
     }
     }
