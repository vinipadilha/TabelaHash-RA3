package utilitarios;

/**
 * Guarda as métricas de uma execução (tabela × hash × m × dataset).
 */
public class Metricas {

    // --- Inserção ---
    public long tempoInsercaoNanos;  // tempo total de inserir todos (ns)
    public long colisoesInsercao;    // soma das colisões na inserção

    // --- Busca ---
    public long tempoBuscaNanos;     // tempo total de buscar todos (ns)

    // --- Gaps (endereçamento aberto) ---
    public int gapMin;               // menor gap (bloco de VAZIO)
    public int gapMax;               // maior gap
    public double gapMedio;          // média dos gaps

    // --- Top-3 listas (encadeada) ---
    public int top1Tamanho, top1Bucket;
    public int top2Tamanho, top2Bucket;
    public int top3Tamanho, top3Bucket;

    /** Zera tudo antes de iniciar uma nova rodada de medições. */
    public void reset() {
        tempoInsercaoNanos = 0L;
        colisoesInsercao = 0L;
        tempoBuscaNanos = 0L;

        gapMin = 0;
        gapMax = 0;
        gapMedio = 0.0;

        top1Tamanho = top2Tamanho = top3Tamanho = 0;
        top1Bucket = top2Bucket = top3Bucket = -1;
    }
}
