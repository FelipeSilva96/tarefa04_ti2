package com.tarefa03.ti2;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.ai.textanalytics.models.DocumentSentiment;
import com.azure.core.credential.AzureKeyCredential;

public class ConsumoIA {

    private static final String CHAVE = "AvzyXJIVCaFdtso8uxU4cBeZZh3Hyyb32SBcme1cTgb4qBBLZNRDJQQJ99CCACZoyfiXJ3w3AAAaACOGsElT";

    private static final String ENDPOINT = "https://ia-felipe-ti2.cognitiveservices.azure.com/";

    public static void main(String[] args) {

        System.out.println("1. Iniciando conexão com o cérebro da Azure...");

        TextAnalyticsClient cliente = new TextAnalyticsClientBuilder()
                .credential(new AzureKeyCredential(CHAVE))
                .endpoint(ENDPOINT)
                .buildClient();

        System.out.println("2. Conexão estabelecida com sucesso!");

        String textoParaAnalisar = "Estou extremamente empolgado com este trabalho de nuvem. No começo foi um pouco frustrante e confuso, mas agora tudo faz sentido e o resultado é espetacular!";

        System.out.println("\n--- TEXTO ENVIADO PARA A IA ---");
        System.out.println("\"" + textoParaAnalisar + "\"");
        System.out.println("-------------------------------\n");

        System.out.println("3. Processando redes neurais da Microsoft (Analisando sentimento)...");

        try {

            DocumentSentiment analise = cliente.analyzeSentiment(textoParaAnalisar);

            System.out.println("\n=== RESULTADO DA ANÁLISE COGNITIVA ===");

            System.out.printf("Sentimento Geral Predominante: %s%n", analise.getSentiment().toString().toUpperCase());

            System.out.println("\nGraus de Confiança (0 a 100%):");
            System.out.printf("-> Positividade: %.2f%%%n", analise.getConfidenceScores().getPositive() * 100);
            System.out.printf("-> Negatividade: %.2f%%%n", analise.getConfidenceScores().getNegative() * 100);
            System.out.printf("-> Neutralidade: %.2f%%%n", analise.getConfidenceScores().getNeutral() * 100);
            System.out.println("======================================");

        } catch (Exception e) {
            System.err.println("Erro ao comunicar com a IA do Azure: " + e.getMessage());
            System.err.println("Verifique se a sua CHAVE e ENDPOINT estão corretos e sem espaços em branco.");
        }
    }
}
