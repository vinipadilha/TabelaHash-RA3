package tabelas;

import utilitarios.FuncoesHash;
import utilitarios.Metricas;

public class TabelaHashLinear implements TabelaHash {

    private final int[] tabela;

    private final int m;

    private static final int VAZIO    = Integer.MIN_VALUE;
    private static final int REMOVIDO = Integer.MIN_VALUE + 1;

    private final int tipoHash;
    private final int dUltimos = 5;

    public TabelaHashLinear(int capacidade, int tipoHash) {
        this.m = capacidade;
        this.tipoHash = tipoHash;
        this.tabela = new int[m];

        for (int i = 0; i < m; i++) {
            tabela[i] = VAZIO;
        }
    }

    private int h(int k) {
        if (tipoHash == 1) {
            return FuncoesHash.divisao(k, m);
        }
        if (tipoHash == 2) {
            return FuncoesHash.multiplicacao(k, m);
        }
        return FuncoesHash.ultimosDigitos(k, m, dUltimos);
    }

    private int passoLinear(int pos) {
        pos++;
        if (pos == m) {
            pos = 0;
        }
        return pos;
    }

    @Override
    public void inserir(int chave, Metricas met) {
        int pos = h(chave);

        int posInicial = pos;

        int passosPorColisao = 0;

        while (tabela[pos] != VAZIO && tabela[pos] != REMOVIDO && tabela[pos] != chave) {
            pos = passoLinear(pos);
            passosPorColisao++;     

            if (pos == posInicial) {
                met.colisoesInsercao += passosPorColisao;
                return;
            }
        }

        if (tabela[pos] == chave) {
            met.colisoesInsercao += passosPorColisao;
            return;
        }

        tabela[pos] = chave;

        met.colisoesInsercao += passosPorColisao;
    }

    @Override
    public boolean contem(int chave) {
        int pos = h(chave);
        int start = pos;

        while (tabela[pos] != VAZIO) {
            if (tabela[pos] == chave) {
                return true; 
            }
            pos = passoLinear(pos); 
            if (pos == start) {
                break;
            }
            break; 
        }
        return false; 
    }

    @Override
    public boolean remover(int chave) {
        int pos = h(chave);
        int start = pos;

        while (tabela[pos] != VAZIO) { 
            if (tabela[pos] == chave) {
                tabela[pos] = REMOVIDO;
                return true;
            }
            pos = passoLinear(pos);
            if (pos == start) {
                break;
            } 
        }
        return false; 
    }

    @Override
    public int capacidade() { return m; }

    @Override
    public int[] vetorBruto() { return tabela; }
}
