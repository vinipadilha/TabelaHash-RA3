package tabelas;

import utilitarios.FuncoesHash;
import utilitarios.Metricas;

/**
 * Tabela Hash com ENCADEAMENTO (lista ligada por "baldes"/buckets).
 *
 * EXPLICAÇÃO SIMPLES:
 * - Pense na tabela como um vetor de "baldes". Cada balde guarda o INÍCIO de uma lista.
 * - Quando calculamos o índice h(k), escolhemos qual balde usar.
 * - Se várias chaves caem no mesmo balde, elas viram uma fila (lista ligada) naquele balde.
 *
 * OTIMIZAÇÃO QUE FACILITA A VIDA:
 * - Guardamos para cada balde:
 *    (a) o ponteiro para o ÚLTIMO nó da lista (tail)
 *    (b) o TAMANHO atual da lista (tamanhos[idx])
 * - Assim, inserir no fim da lista vira O(1) (ligamos direto no último).
 * - A contagem de "colisões" continua correta:
 *    se a lista tinha L nós, para chegar no null do final seriam L "saltos" + 1 → colisões = L + 1.
 *   Mesmo que a gente não caminhe de verdade (porque temos tail), o NÚMERO contado é o mesmo.
 */
public class TabelaHashEncadeada implements TabelaHash {

    /** Nó da lista: guarda a chave e o ponteiro pro próximo. Simples, sem bibliotecas. */
    static class Node {
        int chave;
        Node prox;
        Node(int c) { this.chave = c; }
    }

    // Vetor de buckets: cada posição aponta para o primeiro nó da lista daquele balde
    private final Node[] buckets;

    // "tail" de cada bucket: aponta direto para o ÚLTIMO nó (para inserir no fim sem percorrer)
    private final Node[] tails;

    // Tamanho da lista de cada bucket (quantos nós existem naquele balde)
    private final int[] tamanhos;

    // Quantidade de buckets (tamanho da tabela) e qual função hash estamos usando
    private final int m;            // ex.: 1.003, 10.007, 100.003 (primos)
    private final int tipoHash;     // 1=divisão, 2=multiplicação, 3=últimos dígitos
    private final int dUltimos = 5; // quando usamos "últimos dígitos", quantos dígitos pegamos

    public TabelaHashEncadeada(int capacidade, int tipoHash) {
        this.m = capacidade;
        this.tipoHash = tipoHash;
        this.buckets  = new Node[m];  // começam todos null (sem lista)
        this.tails    = new Node[m];  // começam todos null (sem "último")
        this.tamanhos = new int[m];   // começam todos 0
    }

    // Calcula em qual bucket a chave deve cair, conforme a função hash escolhida
    private int h(int k) {
        if (tipoHash == 1){
            return FuncoesHash.divisao(k, m);  // h(k)=k % m
        }

        if (tipoHash == 2) {
            return FuncoesHash.multiplicacao(k, m);   // Knuth (parte fracionária)
        }
        return FuncoesHash.ultimosDigitos(k, m, dUltimos);           // pega d últimos dígitos
    }

    @Override
    public void inserir(int chave, Metricas met) {
        int idx = h(chave); // escolhe o balde pelo hash

        // Se o balde está vazio: cria o primeiro nó e marca head=tail=novo
        // Colisões = 0 porque não "andamos" por nó nenhum
        if (buckets[idx] == null) {
            Node novo = new Node(chave);
            buckets[idx]  = novo;   // início da lista
            tails[idx]    = novo;   // também é o último
            tamanhos[idx] = 1;      // agora tem 1 nó
            return;
        }

        // (Opcional) Se quiser impedir chaves repetidas, você poderia percorrer aqui e checar.
        // Para dados aleatórios isso quase não acontece, então deixamos desativado para ficar mais rápido.
        /*
        Node p = buckets[idx];
        while (p != null) {
            if (p.chave == chave) return; // já existe; não duplica
            p = p.prox;
        }
        */

        // INSERÇÃO O(1) NO FIM:
        // Em vez de caminhar até o final, usamos o "tail": ligamos direto e atualizamos o tail.
        Node novo = new Node(chave);
        tails[idx].prox = novo;   // o antigo último agora aponta para o novo
        tails[idx] = novo;        // o novo passa a ser o último

        // CONTAGEM DE COLISÕES (como define o enunciado):
        // Se antes havia L nós no balde, seriam L "saltos" até o último + 1 salto até o null → L+1
        met.colisoesInsercao += (tamanhos[idx] + 1);

        // Atualiza o tamanho do balde
        tamanhos[idx]++;
    }

    @Override
    public boolean contem(int chave) {
        // Para buscar, percorremos a lista do balde (caminho natural do encadeamento)
        int idx = h(chave);
        Node n = buckets[idx];
        while (n != null) {
            if (n.chave == chave) {
                return true; // achou
            }
            n = n.prox;
        }
        return false; // não achou
    }

    @Override
    public boolean remover(int chave) {
        // Remove "desligando" o nó da lista
        int idx = h(chave);
        Node n = buckets[idx];
        if (n == null) {
            return false; // lista vazia
        }

        // Caso simples: a chave está logo no primeiro nó
        if (n.chave == chave) {
            buckets[idx] = n.prox;   // o próximo vira o novo head
            tamanhos[idx]--;
            if (buckets[idx] == null) {
                // ficou vazio → também zera o tail
                tails[idx] = null;
            } else if (tails[idx] == n) {
                // caso raro (lista tinha 1 elemento): tail acompanha o head
                tails[idx] = buckets[idx];
            }
            return true;
        }

        // Caso geral: procuramos o nó mantendo referência ao anterior
        Node ant = n;
        n = n.prox;
        while (n != null) {
            if (n.chave == chave) {
                ant.prox = n.prox;  // "pula" o nó atual (remove)
                tamanhos[idx]--;
                if (tails[idx] == n) {
                    // se removemos o último, o anterior vira o novo último
                    tails[idx] = ant;
                }
                return true;
            }
            ant = n;
            n = n.prox;
        }
        return false; // não encontrou
    }

    @Override
    public int capacidade() { return m; }

    // Encadeada não usa vetor plano (não faz sentido calcular "gaps" aqui)
    @Override
    public int[] vetorBruto() { return null; }

    /* =========================
       Top-3 listas (requisito)
       ========================= */
    /**
     * Encontra as 3 maiores listas (tamanho e índice do bucket).
     * Chamamos no final das inserções para reportar no trabalho.
     */
    public void preencherTop3Listas(Metricas met) {
        // zera o top-3
        met.top1Tamanho = met.top2Tamanho = met.top3Tamanho = 0;
        met.top1Bucket  = met.top2Bucket  = met.top3Bucket  = -1;

        // percorre todos os buckets e mede o comprimento da lista de cada um
        for (int i = 0; i < m; i++) {
            int tam = tamanhoLista(buckets[i]); // mede pelo encadeamento real

            // insere (tam, i) manualmente no ranking top-3
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

    // Conta quantos nós existem a partir do head (sem usar bibliotecas prontas)
    private int tamanhoLista(Node head) {
        int t = 0;
        Node n = head;
        while (n != null) { t++; n = n.prox; }
        return t;
    }
}
