import tabelas.*;
import utilitarios.Metricas;
import utilitarios.Temporizador;
import utilitarios.Gaps;

public class Main {

    // ===== Tamanhos oficiais da tabela (primos, ≈x10) =====
    static final int M1 = 1_003;
    static final int M2 = 10_007;
    static final int M3 = 100_003;
    static final int[] MS = { M1, M2, M3 };

    // ===== IDs das funções hash =====
    static final int HASH_DIV = 1;
    static final int HASH_MUL = 2;
    static final int HASH_ULT = 3;
    static final int[] FUNCS = { HASH_DIV, HASH_MUL, HASH_ULT };

    static String nomeFunc(int f) {
        if (f == HASH_DIV) return "DIVISAO";
        if (f == HASH_MUL) return "MULTIPLICACAO";
        return "ULTIMOS";
    }

    public static void main(String[] args) {
        // ===== Datasets OFICIAIS (n, seed) — usar exatamente estes =====
        rodarDataset("100k", GeradorDados.N_100K, GeradorDados.SEED_100K);
        rodarDataset("1M",   GeradorDados.N_1M,   GeradorDados.SEED_1M);
        rodarDataset("10M",  GeradorDados.N_10M,  GeradorDados.SEED_10M);
    }

    private static void rodarDataset(String rotuloDataset, int n, long seed) {
        // Gera UMA VEZ e reutiliza para todas as combinações (exigência do trabalho)
        Registro[] dados = GeradorDados.gerar(n, seed);

        // Cabeçalho CSV (uma vez por dataset)
        System.out.println("dataset,tabela,funcHash,m,n,tempoInsercao_ms,colisoesInsercao,tempoBusca_ms,gapMin,gapMax,gapMedio");

        // Loop: Tabela × Função × m
        for (String tabelaNome : new String[] { "Linear", "Encadeada", "Dupla" }) {
            for (int func : FUNCS) {
                for (int m : MS) {

                    // Regras práticas:
                    // - Endereçamento aberto (Linear/Dupla) funciona bem com n <= m (α ≤ 1).
                    //   Para evitar “tabela cheia”, pulamos essas combinações quando n > m.
                    if ((tabelaNome.equals("Linear") || tabelaNome.equals("Dupla")) && n > m) {
                        continue; // pula combinação inviável para rehashing
                    }

                    // Cria a tabela específica desta rodada
                    TabelaHash tabela = criarTabela(tabelaNome, m, func);

                    // Métricas/tempo
                    Metricas met = new Metricas(); met.reset();
                    Temporizador tmp = new Temporizador();

                    // INSERÇÃO
                    tmp.iniciar();
                    for (Registro r : dados) {
                        tabela.inserir(r.codigo, met);
                    }
                    met.tempoInsercaoNanos = tmp.nanosDecorridos();

                    // GAPS (apenas para Linear/Dupla; Encadeada retorna null)
                    int[] vetor = tabela.vetorBruto();
                    if (vetor != null) {
                        Gaps.calcularGaps(vetor, met);
                    } else {
                        met.gapMin = 0; met.gapMax = 0; met.gapMedio = 0.0; // N/A
                    }

                    // BUSCA (presentes)
                    tmp.iniciar();
                    int achou = 0;
                    for (Registro r : dados) {
                        if (tabela.contem(r.codigo)) achou++;
                    }
                    met.tempoBuscaNanos = tmp.nanosDecorridos();

                    // Saída CSV (ms; gaps N/A = 0,0,0.0)
                    double insMs = met.tempoInsercaoNanos / 1_000_000.0;
                    double busMs = met.tempoBuscaNanos / 1_000_000.0;

                    System.out.printf("%s,%s,%s,%d,%d,%.3f,%d,%.3f,%d,%d,%.2f%n",
                            rotuloDataset, tabelaNome, nomeFunc(func), m, n,
                            insMs, met.colisoesInsercao, busMs, met.gapMin, met.gapMax, met.gapMedio
                    );
                }
            }
        }
    }

    // Fábrica simples de tabelas
    private static TabelaHash criarTabela(String nome, int m, int func) {
        if (nome.equals("Linear"))    return new TabelaHashLinear(m, func);
        if (nome.equals("Encadeada")) return new TabelaHashEncadeada(m, func);
        return new TabelaHashDupla(m, func); // "Dupla"
    }
}
