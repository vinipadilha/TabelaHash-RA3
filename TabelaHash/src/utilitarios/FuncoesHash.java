package utilitarios;

public class FuncoesHash {

    // 1) Divisão: h(k) = k % m
    // EXEMPLO:
    //  - m = 1003, k = 123456
    //  - 123456 % 1003 = 232  →  índice 232
    //
    // BOA PRÁTICA:
    //  - Usar m PRIMO ajuda a evitar padrões ruins e reduzir colisões
    //    (menos alinhamento entre divisores de m e padrões dos dados).
    //
    // OBS.:
    //  - Se a posição estiver ocupada, entra a TÉCNICA DE COLISÃO
    //    (ex.: rehashing linear, duplo hash ou encadeamento).
    public static int divisao(int k, int m) {
        return k % m;
    }

    public static int multiplicacao(int k, int m) {
        // Usamos a constante A (~0,618...) já NUMÉRICA em vez de calcular (√5 − 1)/2,
        // Além disso, a aproximação fixa é suficiente para dispersar bem as chaves.
        final double A = 0.6180339887498949; // (√5 − 1) / 2 aproximado
        // porque não tenho certeza se é permitido usar Math.sqrt(5) nesta atividade.

        // 1) Multiplica a chave k por A (vira número com casas decimais).
        double prod = k * A;          // exemplo: 123456 * 0,618... ≈ 76393,845...

        // 2) Pega a parte inteira (antes da vírgula). Isso equivale a "floor(prod)".
        long inteiro = (long) prod;   // 76393

        // 3) Fica só com a parte decimal (entre 0 e 1). É o "miolo aleatório".
        double frac = prod - inteiro; // 0,845...

        // 4) Escala essa fração para o tamanho da tabela (0 .. m-1) e converte para int.
        return (int) (frac * m);      // índice final (ex.: 0,845... * m → 0..m-1)
    }

    // Ex.: k=123456, d=4 → “3456”; se m=1003, índice = 3456 % 1003 = 447.
    public static int ultimosDigitos(int k, int m, int d) {
        // Ideia: olhar só os "d" últimos dígitos de k e então reduzir para 0..m-1.
        // Ex.: k=123456, d=3 → "456" → depois % m → índice.

        if (d <= 0) return k % m;

        // Nossos códigos têm 9 dígitos; limitar d evita desperdício/overflow.
        int limite = (d > 9) ? 9 : d;

        // Calcula 10^d sem Math.pow (restrição do trabalho).
        int pow10 = 1;
        for (int i = 0; i < limite; i++) pow10 *= 10;

        // Pega apenas os d últimos dígitos (k>=0 pelos dados → já fica >=0).
        int slice = k % pow10; // ex.: 123456 % 1000 = 456

        // Ajusta para o intervalo de índices da tabela.
        return slice % m;
    }

    // 4) h2 para Duplo Hash
    // IDEIA:
    //   - No duplo hash, se a posição inicial (h1) já está ocupada, a gente
    //     não anda só +1, +1, +1... (como no linear). Em vez disso, damos
    //     "saltos" de um tamanho que depende da própria chave k.
    //   - Esse tamanho do salto é h2. Aqui definimos h2 = 1 + (k % (m-1)).
    //
    // POR QUE "1 + (k % (m-1))"?
    //   1) Nunca é zero → o salto nunca fica parado (precisamos sair do lugar).
    //   2) Quando m é PRIMO, esse passo (h2) é "incompatível" com m (coprimos),
    //      então, se for preciso, conseguimos visitar TODAS as posições da tabela
    //      antes de repetir, evitando ficar preso em poucos índices.
    //   3) Como h2 depende da chave, cada chave "pula" com um passo diferente,
    //      o que reduz o acúmulo de colisões em sequência (menos aglomeração).
    //
    // REQUISITO prático: usar m primo (ex.: 1003, 10007, 100003).
    //
    public static int h2Duplo(int k, int m) {
        int r = k % (m - 1);
        if (r < 0) r += (m - 1);
        return 1 + r; // valor final no intervalo [1, m-1] (nunca zero)
    }

}
