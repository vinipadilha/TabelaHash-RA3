package tabelas;

import utilitarios.FuncoesHash;
import utilitarios.Metricas;

public class TabelaHashDupla implements TabelaHash {

    private final int[] tabela;

    private final int m;

    private static final int VAZIO    = Integer.MIN_VALUE;
    private static final int REMOVIDO = Integer.MIN_VALUE + 1;

    private final int tipoHash;
    private final int dUltimos = 5;

    public TabelaHashDupla(int capacidade, int tipoHash) {
        this.m = capacidade;
        this.tipoHash = tipoHash;
        this.tabela = new int[m];
        for (int i = 0; i < m; i++) tabela[i] = VAZIO;
    }

    private int h1(int k) {
        if (tipoHash == 1) {
            return FuncoesHash.divisao(k, m);
        }

        if (tipoHash == 2) {
            return FuncoesHash.multiplicacao(k, m);
        }
        return FuncoesHash.ultimosDigitos(k, m, dUltimos);
    }

    private int h2(int k) {
        return FuncoesHash.h2Duplo(k, m); 
    }

    @Override
    public void inserir(int chave, Metricas met) {
        int base = h1(chave);
        int passo = h2(chave);
        int pos = base;

        int posInicial = base;
        int sondagensExtras = 0;

        while (tabela[pos] != VAZIO && tabela[pos] != REMOVIDO && tabela[pos] != chave) {
            sondagensExtras++;
            pos = (pos + passo) % m;
            if (pos == posInicial) {
                met.colisoesInsercao += sondagensExtras;
                return;
            }
        }

        if (tabela[pos] == chave) {
            met.colisoesInsercao += sondagensExtras;
            return;
        }

        tabela[pos] = chave;
        met.colisoesInsercao += sondagensExtras;
    }

    @Override
    public boolean contem(int chave) {
        int base = h1(chave);
        int passo = h2(chave);
        int pos = base;
        int start = base;

        while (tabela[pos] != VAZIO) {
            if (tabela[pos] == chave) return true;
            pos = (pos + passo) % m;
            if (pos == start) break;
        }
        return false;
    }

    @Override
    public boolean remover(int chave) {
        int base = h1(chave);
        int passo = h2(chave);
        int pos = base;
        int start = base;

        while (tabela[pos] != VAZIO) {
            if (tabela[pos] == chave) {
                tabela[pos] = REMOVIDO;
                return true;
            }
            pos = (pos + passo) % m;
            if (pos == start) break;
        }
        return false;
    }

    @Override
    public int capacidade() { return m; }

    @Override
    public int[] vetorBruto() { return tabela; }
}
