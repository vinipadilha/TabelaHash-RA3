package tabelas;

import utilitarios.FuncoesHash;
import utilitarios.Metricas;

public class TabelaHashLinear implements TabelaHash {

    // Vetor que representa a tabela
    private final int[] tabela;

    // m = tamanho da tabela (capacidade total de posições)
    private final int m;

    // Marcadores especiais:
    // VAZIO   → posição nunca usada
    // REMOVIDO→ posição já foi ocupada, mas foi removida (mantém a sonda funcionando)
    private static final int VAZIO    = Integer.MIN_VALUE;
    private static final int REMOVIDO = Integer.MIN_VALUE + 1;

    // Qual função hash usar nesta instância:
    // 1 = divisão (k % m)
    // 2 = multiplicação (Knuth)
    // 3 = últimos dígitos (d = 5)
    private final int tipoHash;
    private final int dUltimos = 5;

    public TabelaHashLinear(int capacidade, int tipoHash) {
        this.m = capacidade;
        this.tipoHash = tipoHash;
        this.tabela = new int[m];

        // Inicializa todas as células como VAZIO
        for (int i = 0; i < m; i++) {
            tabela[i] = VAZIO;
        }
    }

    // Calcula o índice inicial com a função hash escolhida:
    // - divisão, multiplicação ou últimos dígitos
    private int h(int k) {
        if (tipoHash == 1) {
            return FuncoesHash.divisao(k, m);
        }
        if (tipoHash == 2) {
            return FuncoesHash.multiplicacao(k, m);
        }
        return FuncoesHash.ultimosDigitos(k, m, dUltimos);
    }

    // Passo do rehashing linear: anda 1 posição (com volta no fim)
    // Ex.: se está na última posição, volta para 0
    private int passoLinear(int pos) {
        pos++;
        if (pos == m) {
            pos = 0;
        }
        return pos;
    }

    @Override
    public void inserir(int chave, Metricas met) {
        // 1) Começa na posição calculada pela função hash
        int pos = h(chave);

        // Guardamos a posição inicial para detectar “dei a volta”
        int posInicial = pos;

        // Quantos passos extras tivemos por causa de colisão nessa inserção.
        // Ex.: se precisou andar 3 vezes até achar vaga → passosPorColisao = 3
        int passosPorColisao = 0;

        // 2) Enquanto a célula atual estiver ocupada por OUTRA chave,
        //    avançamos linearmente
        while (tabela[pos] != VAZIO && tabela[pos] != REMOVIDO && tabela[pos] != chave) {
            pos = passoLinear(pos); // (pos + 1) % m
            passosPorColisao++;     // cada avanço conta como uma colisão

            // Se voltamos à posição inicial, a tabela está cheia
            if (pos == posInicial) {
                met.colisoesInsercao += passosPorColisao; // registra o custo desta tentativa
                return; // sem vaga
            }
        }

        // 3) Evita duplicata: se a chave já está na tabela, não reinsere
        if (tabela[pos] == chave) {
            met.colisoesInsercao += passosPorColisao;
            return;
        }

        // 4) Achamos uma vaga (VAZIO ou REMOVIDO) → grava a chave
        tabela[pos] = chave;

        // 5) Soma os passos decorrentes de colisões desta inserção
        met.colisoesInsercao += passosPorColisao;
    }

    @Override
    public boolean contem(int chave) {
        // Busca segue a mesma sonda da inserção:
        // começa em h(chave) e avança linearmente enquanto fizer sentido
        int pos = h(chave);
        int start = pos;

        // Regra da busca:
        // - Se achou a chave → true
        // - Se encontrou VAZIO → pode parar (nunca foi preenchido aqui → não existe)
        // - Se deu a volta → para
        while (tabela[pos] != VAZIO) {
            if (tabela[pos] == chave) {
                return true; // encontrou
            }
            pos = passoLinear(pos); // avança para próxima posição
            if (pos == start) {
                break;
            }
            break; // deu a volta
        }
        return false; // não encontrou
    }

    @Override
    public boolean remover(int chave) {
        // Remoção lógica: marca como REMOVIDO (não usa VAZIO para não quebrar a sonda)
        int pos = h(chave);
        int start = pos;

        while (tabela[pos] != VAZIO) { // se encontrar VAZIO, não existe
            if (tabela[pos] == chave) {
                tabela[pos] = REMOVIDO;
                return true;
            }
            pos = passoLinear(pos);
            if (pos == start) {
                break;
            } // evitamos loop infinito
        }
        return false; // não achou para remover
    }

    @Override
    public int capacidade() { return m; }

    // Exposição do vetor “nu” para calcular métricas de gaps (após inserir tudo)
    @Override
    public int[] vetorBruto() { return tabela; }
}
