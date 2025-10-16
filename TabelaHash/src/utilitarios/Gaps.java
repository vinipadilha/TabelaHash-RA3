package utilitarios;

public final class Gaps {

    private static final int VAZIO = Integer.MIN_VALUE;

    private Gaps() {} 

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
                gapAtual++;
            } else {
                if (gapAtual > 0) {
                    if (gapAtual < gapMin) gapMin = gapAtual;
                    if (gapAtual > gapMax) gapMax = gapAtual;
                    somaGaps += gapAtual;
                    qtdGaps++;
                    gapAtual = 0;
                }
            }
        }

        if (gapAtual > 0) {
            if (gapAtual < gapMin) gapMin = gapAtual;
            if (gapAtual > gapMax) gapMax = gapAtual;
            somaGaps += gapAtual;
            qtdGaps++;
        }

        if (qtdGaps == 0) {
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
