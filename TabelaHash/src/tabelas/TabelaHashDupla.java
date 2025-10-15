package tabelas;

import utilitarios.FuncoesHash;
import utilitarios.Metricas;

/**
 * Tabela Hash com ENDEREÇAMENTO ABERTO (Duplo Hash).
 *
 * IDEIA (bem simples):
 * - Índice inicial: h1(k, m)  → pode ser Divisão, Multiplicação ou Últimos Dígitos.
 * - Em colisão: em vez de andar +1 (linear), anda um "salto" que depende da chave:
 *      próxima = (h1 + j * h2(k, m)) % m   (j = 1, 2, 3, ...)
 * - h2(k, m) = 1 + (k % (m-1))  (assumindo m primo). Assim o passo NUNCA é 0 e
 *   não "trava" em ciclos curtos, cobrindo bem a tabela.
 *
 * MÉTRICA (colisões):
 * - Cada avanço por causa de colisão (cada j++) conta 1 colisão (sondagem extra).
 */
public class TabelaHashDupla implements TabelaHash {

    // Array da tabela
    private final int[] tabela;

    // Capacidade (m)
    private final int m;

    // Marcadores de célula
    private static final int VAZIO    = Integer.MIN_VALUE;
    private static final int REMOVIDO = Integer.MIN_VALUE + 1;

    // Qual hash usar como h1: 1 = divisão, 2 = multiplicação, 3 = últimos dígitos
    private final int tipoHash;
    private final int dUltimos = 5; // parâmetro para "últimos dígitos"

    public TabelaHashDupla(int capacidade, int tipoHash) {
        this.m = capacidade;
        this.tipoHash = tipoHash;
        this.tabela = new int[m];
        for (int i = 0; i < m; i++) tabela[i] = VAZIO;
    }

    // h1: índice inicial conforme a função escolhida
    private int h1(int k) {
        if (tipoHash == 1) {
            return FuncoesHash.divisao(k, m);
        }

        if (tipoHash == 2) {
            return FuncoesHash.multiplicacao(k, m);
        }
        return FuncoesHash.ultimosDigitos(k, m, dUltimos);
    }

    // h2: tamanho do salto (depende da chave). Requer m primo para melhor cobertura.
    private int h2(int k) {
        return FuncoesHash.h2Duplo(k, m); // 1..m-1
    }

    @Override
    public void inserir(int chave, Metricas met) {
        int base = h1(chave); // posição inicial
        int passo = h2(chave); // salto dependente da chave
        int pos = base;

        int posInicial = base; // para detectar "dei a volta"
        int sondagensExtras = 0; // conta colisões (cada avanço por colisão)

        // Enquanto célula ocupada por OUTRA chave, avança com passo "h2"
        while (tabela[pos] != VAZIO && tabela[pos] != REMOVIDO && tabela[pos] != chave) {
            sondagensExtras++; // colisão (precisou avançar)
            pos = (pos + passo) % m; // duplo hash
            if (pos == posInicial) { // voltou ao início → tabela cheia
                met.colisoesInsercao += sondagensExtras;
                return;
            }
        }

        // Evita duplicar a mesma chave
        if (tabela[pos] == chave) {
            met.colisoesInsercao += sondagensExtras;
            return;
        }

        // Achou vaga (VAZIO/REMOVIDO) → insere
        tabela[pos] = chave;
        met.colisoesInsercao += sondagensExtras;
    }

    @Override
    public boolean contem(int chave) {
        int base = h1(chave);
        int passo = h2(chave);
        int pos = base;
        int start = base;

        // Procura seguindo a mesma sequência usada na inserção
        while (tabela[pos] != VAZIO) { // se achar VAZIO, não existe
            if (tabela[pos] == chave) return true;
            pos = (pos + passo) % m;
            if (pos == start) break; // deu a volta
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
                tabela[pos] = REMOVIDO; // remoção lógica para não quebrar a sonda
                return true;
            }
            pos = (pos + passo) % m;
            if (pos == start) break;
        }
        return false;
    }

    @Override
    public int capacidade() { return m; }

    // Expondo o vetor para cálculo de "gaps" (faz sentido em endereçamento aberto)
    @Override
    public int[] vetorBruto() { return tabela; }
}
