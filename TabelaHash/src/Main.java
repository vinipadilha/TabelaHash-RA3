import dados.GeradorDados;
import modelo.Registro;
import tabelas.*;
import utilitarios.Metricas;
import utilitarios.Temporizador;
import utilitarios.Gaps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class Main {

    // ===== Tamanhos oficiais (primos) =====
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

    public static void main(String[] args) throws IOException {
        // Garante pasta de saída
        File outDir = new File("resultados");
        outDir.mkdirs();

        // 100k e 1M: rodagem padrão (array em memória)
        rodarDatasetParaCSV("100k", GeradorDados.N_100K, GeradorDados.SEED_100K, "resultados/resultado_100k.csv");
        rodarDatasetParaCSV("1M",   GeradorDados.N_1M,   GeradorDados.SEED_1M,   "resultados/resultado_1M.csv");

        // 10M: streaming (mesma seed, sem alocar arrayzão)
        rodarDatasetStreamingParaCSV("10M", GeradorDados.N_10M, GeradorDados.SEED_10M, "resultados/resultado_10M.csv");

        System.out.println("Arquivos gerados em: " + outDir.getAbsolutePath());
    }

    // ===========================
    // Rodagem com array em memória
    // ===========================
    private static void rodarDatasetParaCSV(String rotuloDataset, int n, long seed, String csvPath) throws IOException {
        Registro[] dados = GeradorDados.gerar(n, seed);

        try (BufferedWriter bw = abrirCSV(csvPath)) {
            escreverCabecalho(bw);
            for (String tabelaNome : new String[] { "Linear", "Encadeada", "Dupla" }) {
                for (int func : FUNCS) {
                    for (int m : MS) {
                        if ((tabelaNome.equals("Linear") || tabelaNome.equals("Dupla")) && n > m) continue;

                        TabelaHash tabela = criarTabela(tabelaNome, m, func);
                        Metricas met = new Metricas(); met.reset();
                        Temporizador tmp = new Temporizador();

                        // Inserção
                        tmp.iniciar();
                        for (Registro r : dados) tabela.inserir(r.codigo, met);
                        met.tempoInsercaoNanos = tmp.nanosDecorridos();

                        // Gaps (endereçamento aberto)
                        int[] vetor = tabela.vetorBruto();
                        if (vetor != null) Gaps.calcularGaps(vetor, met);

                        // Busca (presentes)
                        tmp.iniciar();
                        int achou = 0;
                        for (Registro r : dados) if (tabela.contem(r.codigo)) achou++;
                        met.tempoBuscaNanos = tmp.nanosDecorridos();

                        // Linha principal
                        escreverLinhaPrincipal(bw, rotuloDataset, tabelaNome, func, m, n, met);

                        // Top-3 encadeada
                        if (tabela instanceof TabelaHashEncadeada enc) {
                            enc.preencherTop3Listas(met);
                            escreverLinhaTop3(bw, rotuloDataset, func, m, met);
                        }
                    }
                }
            }
        }
    }

    // ===========================
    // Rodagem em streaming (10M)
    // ===========================
    private static void rodarDatasetStreamingParaCSV(String rotuloDataset, int n, long seed, String csvPath) throws IOException {
        try (BufferedWriter bw = abrirCSV(csvPath)) {
            escreverCabecalho(bw);
            for (String tabelaNome : new String[] { "Linear", "Encadeada", "Dupla" }) {
                for (int func : FUNCS) {
                    for (int m : MS) {
                        if ((tabelaNome.equals("Linear") || tabelaNome.equals("Dupla")) && n > m) continue;

                        TabelaHash tabela = criarTabela(tabelaNome, m, func);
                        Metricas met = new Metricas(); met.reset();
                        Temporizador tmp = new Temporizador();

                        // Inserção (gera on-the-fly com seed)
                        java.util.Random rndIns = new java.util.Random(seed);
                        tmp.iniciar();
                        for (int i = 0; i < n; i++) {
                            int codigo = rndIns.nextInt(1_000_000_000);
                            tabela.inserir(codigo, met);
                        }
                        met.tempoInsercaoNanos = tmp.nanosDecorridos();

                        // Gaps
                        int[] vetor = tabela.vetorBruto();
                        if (vetor != null) Gaps.calcularGaps(vetor, met);

                        // Busca (gera a MESMA sequência)
                        java.util.Random rndBusca = new java.util.Random(seed);
                        tmp.iniciar();
                        int achou = 0;
                        for (int i = 0; i < n; i++) {
                            int codigo = rndBusca.nextInt(1_000_000_000);
                            if (tabela.contem(codigo)) achou++;
                        }
                        met.tempoBuscaNanos = tmp.nanosDecorridos();

                        // Linha principal
                        escreverLinhaPrincipal(bw, rotuloDataset, tabelaNome, func, m, n, met);

                        // Top-3 encadeada
                        if (tabela instanceof TabelaHashEncadeada enc) {
                            enc.preencherTop3Listas(met);
                            escreverLinhaTop3(bw, rotuloDataset, func, m, met);
                        }
                    }
                }
            }
        }
    }

    // ===== helpers de escrita CSV =====
    private static BufferedWriter abrirCSV(String path) throws IOException {
        // usa ; como separador e força Locale.US para ponto decimal
        // (o separador ; está no conteúdo, aqui só abrimos o arquivo)
        File f = new File(path);
        f.getParentFile().mkdirs();
        return new BufferedWriter(new FileWriter(f, false));
    }

    private static void escreverCabecalho(BufferedWriter bw) throws IOException {
        bw.write("dataset;tabela;funcHash;m;n;tempoInsercao_ms;colisoesInsercao;tempoBusca_ms;gapMin;gapMax;gapMedio");
        bw.newLine();
    }

    private static void escreverLinhaPrincipal(BufferedWriter bw, String dataset, String tabelaNome, int func, int m, int n, Metricas met) throws IOException {
        // usa Locale.US para garantir ponto decimal
        String linha = String.format(Locale.US,
                "%s;%s;%s;%d;%d;%.3f;%d;%.3f;%d;%d;%.2f",
                dataset, tabelaNome, nomeFunc(func), m, n,
                met.tempoInsercaoNanos / 1_000_000.0,
                met.colisoesInsercao,
                met.tempoBuscaNanos / 1_000_000.0,
                met.gapMin, met.gapMax, met.gapMedio
        );
        bw.write(linha); bw.newLine();
    }

    private static void escreverLinhaTop3(BufferedWriter bw, String dataset, int func, int m, Metricas met) throws IOException {
        // formato extra, fácil de ler no README:
        // top3_encadeada;dataset;funcHash;m;bucket:tam;bucket:tam;bucket:tam
        String linha = String.format(Locale.US,
                "top3_encadeada;%s;%s;%d;%d:%d;%d:%d;%d:%d",
                dataset, nomeFunc(func), m,
                met.top1Bucket, met.top1Tamanho,
                met.top2Bucket, met.top2Tamanho,
                met.top3Bucket, met.top3Tamanho
        );
        bw.write(linha); bw.newLine();
    }

    // ===== fábrica de tabelas =====
    private static TabelaHash criarTabela(String nome, int m, int func) {
        if (nome.equals("Linear"))    return new TabelaHashLinear(m, func);
        if (nome.equals("Encadeada")) return new TabelaHashEncadeada(m, func);
        return new TabelaHashDupla(m, func);
    }
}
