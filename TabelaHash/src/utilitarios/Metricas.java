package utilitarios;

/**
 * Guarda as métricas coletadas durante os testes.
 * A ideia é: a cada execução (tabela × hash × m × dataset),
 * você usa UM objeto Metricas, reseta no início e preenche os campos.
 */
public class Metricas {

    // --- Inserção ---
    // Tempo total de inserir TODOS os elementos (ns)
    public long tempoInsercaoNanos;

    // Colisões acumuladas na inserção:
    // - Rehashing (Linear/Dupla): soma das sondagens extras (passos) até achar vaga
    // - Encadeamento: soma dos "saltos" na lista até o null onde insere
    public long colisoesInsercao;

    // --- Busca ---
    // Tempo total de buscar TODOS os elementos (ns)
    public long tempoBuscaNanos;

    // --- Gaps (apenas endereçamento aberto) ---
    // gap = bloco de células VAZIAS consecutivas
    public int gapMin;      // menor gap encontrado
    public int gapMax;      // maior gap encontrado
    public double gapMedio; // média dos tamanhos de gaps

    /** Zera tudo antes de iniciar uma nova rodada de medições. */
    public void reset() {
        tempoInsercaoNanos = 0L;
        colisoesInsercao = 0L;
        tempoBuscaNanos = 0L;
        gapMin = 0;
        gapMax = 0;
        gapMedio = 0.0;
    }
}
