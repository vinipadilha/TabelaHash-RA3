import dados.GeradorDados;
import modelo.Registro;
import tabelas.*;
import utilitarios.Metricas;
import utilitarios.Temporizador;
import utilitarios.Gaps;

public class Main {

    static final int M1 = 1_003;
    static final int M2 = 10_007;
    static final int M3 = 100_003;
    static final int[] MS = { M1, M2, M3 };

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
        rodarDataset("100k", GeradorDados.N_100K, GeradorDados.SEED_100K);
        rodarDataset("1M",   GeradorDados.N_1M,   GeradorDados.SEED_1M);
        rodarDatasetStreaming("10M", GeradorDados.N_10M, GeradorDados.SEED_10M);
    }

    private static void rodarDataset(String rotuloDataset, int n, long seed) {
        Registro[] dados = GeradorDados.gerar(n, seed);

        System.out.println("dataset,tabela,funcHash,m,n,tempoInsercao_ms,colisoesInsercao,tempoBusca_ms,gapMin,gapMax,gapMedio");

        for (String tabelaNome : new String[] { "Linear", "Encadeada", "Dupla" }) {
            for (int func : FUNCS) {
                for (int m : MS) {

                    if ((tabelaNome.equals("Linear") || tabelaNome.equals("Dupla")) && n > m) {
                        continue;
                    }

                    TabelaHash tabela = criarTabela(tabelaNome, m, func);

                    Metricas met = new Metricas(); met.reset();
                    Temporizador tmp = new Temporizador();

                    tmp.iniciar();
                    for (Registro r : dados) {
                        tabela.inserir(r.codigo, met);
                    }
                    met.tempoInsercaoNanos = tmp.nanosDecorridos();

                    int[] vetor = tabela.vetorBruto();
                    if (vetor != null) {
                        Gaps.calcularGaps(vetor, met);
                    } else {
                        met.gapMin = 0; met.gapMax = 0; met.gapMedio = 0.0;
                    }

                    tmp.iniciar();
                    int achou = 0;
                    for (Registro r : dados) {
                        if (tabela.contem(r.codigo)) achou++;
                    }
                    met.tempoBuscaNanos = tmp.nanosDecorridos();

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

    private static TabelaHash criarTabela(String nome, int m, int func) {
        if (nome.equals("Linear"))    return new TabelaHashLinear(m, func);
        if (nome.equals("Encadeada")) return new TabelaHashEncadeada(m, func);
        return new TabelaHashDupla(m, func);
    }
    private static void rodarDatasetStreaming(String rotuloDataset, int n, long seed) {
        System.out.println("dataset,tabela,funcHash,m,n,tempoInsercao_ms,colisoesInsercao,tempoBusca_ms,gapMin,gapMax,gapMedio");

        for (String tabelaNome : new String[] { "Linear", "Encadeada", "Dupla" }) {
            for (int func : FUNCS) {
                for (int m : MS) {

                    if ((tabelaNome.equals("Linear") || tabelaNome.equals("Dupla")) && n > m) {
                        continue;
                    }

                    TabelaHash tabela = criarTabela(tabelaNome, m, func);
                    Metricas met = new Metricas(); met.reset();
                    Temporizador tmp = new Temporizador();

                    java.util.Random rndIns = new java.util.Random(seed);
                    tmp.iniciar();
                    for (int i = 0; i < n; i++) {
                        int codigo = rndIns.nextInt(1_000_000_000);
                        tabela.inserir(codigo, met);
                    }
                    met.tempoInsercaoNanos = tmp.nanosDecorridos();

                    int[] vetor = tabela.vetorBruto();
                    if (vetor != null) {
                        Gaps.calcularGaps(vetor, met);
                    } else {
                        met.gapMin = 0; met.gapMax = 0; met.gapMedio = 0.0;
                    }

                    java.util.Random rndBusca = new java.util.Random(seed);
                    tmp.iniciar();
                    int achou = 0;
                    for (int i = 0; i < n; i++) {
                        int codigo = rndBusca.nextInt(1_000_000_000);
                        if (tabela.contem(codigo)) achou++;
                    }
                    met.tempoBuscaNanos = tmp.nanosDecorridos();

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

}
