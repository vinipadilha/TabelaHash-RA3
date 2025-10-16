package tabelas;

import utilitarios.FuncoesHash;
import utilitarios.Metricas;

public class TabelaHashEncadeada implements TabelaHash {

    static class Node {
        int chave;
        Node prox;
        Node(int c) { this.chave = c; }
    }

    private final Node[] buckets;

    private final Node[] tails;

    private final int[] tamanhos;

    private final int m;            
    private final int tipoHash;     
    private final int dUltimos = 5; 

    public TabelaHashEncadeada(int capacidade, int tipoHash) {
        this.m = capacidade;
        this.tipoHash = tipoHash;
        this.buckets  = new Node[m];  
        this.tails    = new Node[m];  
        this.tamanhos = new int[m];   
    }

    private int h(int k) {
        if (tipoHash == 1){
            return FuncoesHash.divisao(k, m);  
        }

        if (tipoHash == 2) {
            return FuncoesHash.multiplicacao(k, m);   
        }
        return FuncoesHash.ultimosDigitos(k, m, dUltimos);           
    }

    @Override
    public void inserir(int chave, Metricas met) {
        int idx = h(chave); 

        if (buckets[idx] == null) {
            Node novo = new Node(chave);
            buckets[idx]  = novo;   
            tails[idx]    = novo;   
            tamanhos[idx] = 1;      
            return;
        }

        Node novo = new Node(chave);
        tails[idx].prox = novo;   
        tails[idx] = novo;        

        met.colisoesInsercao += (tamanhos[idx] + 1);

        tamanhos[idx]++;
    }

    @Override
    public boolean contem(int chave) {
        int idx = h(chave);
        Node n = buckets[idx];
        while (n != null) {
            if (n.chave == chave) {
                return true; 
            }
            n = n.prox;
        }
        return false; 
    }

    @Override
    public boolean remover(int chave) {
        int idx = h(chave);
        Node n = buckets[idx];
        if (n == null) {
            return false; 
        }

        if (n.chave == chave) {
            buckets[idx] = n.prox;   
            tamanhos[idx]--;
            if (buckets[idx] == null) {
                tails[idx] = null;
            } else if (tails[idx] == n) {
                tails[idx] = buckets[idx];
            }
            return true;
        }

        Node ant = n;
        n = n.prox;
        while (n != null) {
            if (n.chave == chave) {
                ant.prox = n.prox;  
                tamanhos[idx]--;
                if (tails[idx] == n) {
                    tails[idx] = ant;
                }
                return true;
            }
            ant = n;
            n = n.prox;
        }
        return false; 
    }

    @Override
    public int capacidade() { return m; }

    @Override
    public int[] vetorBruto() { return null; }

    public void preencherTop3Listas(Metricas met) {
        met.top1Tamanho = met.top2Tamanho = met.top3Tamanho = 0;
        met.top1Bucket  = met.top2Bucket  = met.top3Bucket  = -1;

        for (int i = 0; i < m; i++) {
            int tam = tamanhoLista(buckets[i]);

            if (tam > met.top1Tamanho) {
                met.top3Tamanho = met.top2Tamanho; met.top3Bucket = met.top2Bucket;
                met.top2Tamanho = met.top1Tamanho; met.top2Bucket = met.top1Bucket;
                met.top1Tamanho = tam;             met.top1Bucket = i;
            } else if (tam > met.top2Tamanho) {
                met.top3Tamanho = met.top2Tamanho; met.top3Bucket = met.top2Bucket;
                met.top2Tamanho = tam;             met.top2Bucket = i;
            } else if (tam > met.top3Tamanho) {
                met.top3Tamanho = tam;             met.top3Bucket = i;
            }
        }
    }

    private int tamanhoLista(Node head) {
        int t = 0;
        Node n = head;
        while (n != null) { t++; n = n.prox; }
        return t;
    }
}
