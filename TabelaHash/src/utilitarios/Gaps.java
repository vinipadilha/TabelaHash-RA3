package utilitarios;

/**
 * Cálculo simples de "gaps" para tabelas de endereçamento aberto (Linear / Dupla).
 *
 * DEFINIÇÃO USADA:
 * - GAP = quantidade de células VAZIAS consecutivas (blocos de VAZIO).
 * - Considera VAZIO = Integer.MIN_VALUE (mesmo valor usado nas tabelas).
 * - REMOVIDO NÃO é considerado vazio para fins de gap.
 *
 * O que preenche:
 * - met.gapMin, met.gapMax e met.gapMedio (média dos tamanhos dos gaps encontrados).
 *
 * Observação:
 * - Varre o vetor do início ao fim (não faz "circular"). É suficiente para o relatório.
 */
public final class Gaps {

    private static final int VAZIO = Integer.MIN_VALUE;

    private Gaps() {} // utilitário estático

    /**
     * Calcula min, max e média dos gaps em um vetor de tabela hash (endereçamento aberto).
     * Ignora se o vetor for null (ex.: encadeamento retorna null em vetorBruto()).
     */
    public static void calcularGaps(int[] vetor, Metricas met) {
        if (vetor == null || vetor.length == 0) {
            met.gapMin = 0;
            met.gapMax = 0;
            met.gapMedio = 0.0;
            return;
        }

        int n = vetor.length;
        int gapAtual = 0;
        int gapMin = Integer.MAX_VALUE;
        int gapMax = 0;
        long somaGaps = 0L;
        int qtdGaps = 0;

        for (int i = 0; i < n; i++) {
            if (vetor[i] == VAZIO) {
                // estamos dentro de um gap
                gapAtual++;
            } else {
                // fechou um gap (se existia)
                if (gapAtual > 0) {
                    if (gapAtual < gapMin) gapMin = gapAtual;
                    if (gapAtual > gapMax) gapMax = gapAtual;
                    somaGaps += gapAtual;
                    qtdGaps++;
                    gapAtual = 0;
                }
            }
        }

        // se o vetor termina com gap, fecha aqui
        if (gapAtual > 0) {
            if (gapAtual < gapMin) gapMin = gapAtual;
            if (gapAtual > gapMax) gapMax = gapAtual;
            somaGaps += gapAtual;
            qtdGaps++;
        }

        if (qtdGaps == 0) {
            // sem gaps (tabela completamente cheia)
            met.gapMin = 0;
            met.gapMax = 0;
            met.gapMedio = 0.0;
        } else {
            met.gapMin = gapMin;
            met.gapMax = gapMax;
            met.gapMedio = (double) somaGaps / (double) qtdGaps;
        }
    }
}
