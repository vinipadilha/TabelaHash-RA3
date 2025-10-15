package tabelas;

import utilitarios.FuncoesHash;
import utilitarios.Metricas;

/**
 * Tabela Hash com ENCADEAMENTO (versão mínima).
 * - Vetor de buckets; cada bucket é o início de uma lista ligada (Node).
 * - Inserção conta "colisões" como a quantidade de passos (saltos de nó) até o ponto de inserção.
 * - Busca percorre a lista do bucket e retorna true/false.
 * - Remoção desconecta o nó da lista.
 *
 * Observação: "gaps" não se aplicam aqui (só fazem sentido em endereçamento aberto).
 */
public class TabelaHashEncadeada implements TabelaHash {

    /** Nó simples da lista encadeada. */
    static class Node {
        int chave;
        Node prox;
        Node(int c) { this.chave = c; }
    }

    private final Node[] buckets;  // vetor de cabeças de lista
    private final int m;           // quantidade de buckets
    private final int tipoHash;    // 1=divisão, 2=multiplicação, 3=últimos
    private final int dUltimos = 5;

    public TabelaHashEncadeada(int capacidade, int tipoHash) {
        this.m = capacidade;
        this.tipoHash = tipoHash;
        this.buckets = new Node[m]; // todos null
    }

    // Índice do bucket pelo hash escolhido
    private int h(int k) {
        if (tipoHash == 1){
            return FuncoesHash.divisao(k, m);
        }
        if (tipoHash == 2){
            return FuncoesHash.multiplicacao(k, m);
        }
        return FuncoesHash.ultimosDigitos(k, m, dUltimos);
    }

    @Override
    public void inserir(int chave, Metricas met) {
        int idx = h(chave);
        Node head = buckets[idx];

        // Bucket vazio → insere direto (0 colisões)
        if (head == null) {
            buckets[idx] = new Node(chave);
            return;
        }

        // Percorre a lista:
        // - Se encontrar a chave, não duplica (sai).
        // - Senão, ao chegar no último nó, insere no fim.
        Node atual = head;
        int passos = 0;

        // Checa primeiro nó
        if (atual.chave == chave) {
            // já existe; sem colisões extras
            return;
        }

        // Do segundo nó em diante
        while (atual.prox != null) {
            passos++;               // cada salto de nó conta como 1 colisão
            atual = atual.prox;
            if (atual.chave == chave) {
                met.colisoesInsercao += passos;
                return;             // já existe
            }
        }

        // Estamos no último nó (atual.prox == null): inserir no fim
        passos++;                    // conta o "salto" até o null onde insere
        atual.prox = new Node(chave);
        met.colisoesInsercao += passos;
    }

    @Override
    public boolean contem(int chave) {
        int idx = h(chave);
        Node n = buckets[idx];
        while (n != null) {
            if (n.chave == chave) return true;
            n = n.prox;
        }
        return false;
    }

    @Override
    public boolean remover(int chave) {
        int idx = h(chave);
        Node n = buckets[idx];
        if (n == null) return false;

        // Caso especial: está no primeiro nó
        if (n.chave == chave) {
            buckets[idx] = n.prox;
            return true;
        }

        // Percorre mantendo o anterior
        Node ant = n;
        n = n.prox;
        while (n != null) {
            if (n.chave == chave) {
                ant.prox = n.prox;  // remove "pulando" o nó
                return true;
            }
            ant = n;
            n = n.prox;
        }
        return false;
    }

    @Override
    public int capacidade() { return m; }

    // Para encadeamento, não há um vetor “plano” de ints para gaps → retornamos null.
    @Override
    public int[] vetorBruto() { return null; }
}
