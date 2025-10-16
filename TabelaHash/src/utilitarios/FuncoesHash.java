package utilitarios;

public class FuncoesHash {

    public static int divisao(int k, int m) {
        return k % m;
    }

    public static int multiplicacao(int k, int m) {
        final double A = 0.6180339887498949;
        double prod = k * A;
        long inteiro = (long) prod;
        double frac = prod - inteiro;
        return (int) (frac * m);
    }

    public static int ultimosDigitos(int k, int m, int d) {
        if (d <= 0) return k % m;

        int limite = (d > 9) ? 9 : d;

        int pow10 = 1;
        for (int i = 0; i < limite; i++) pow10 *= 10;

        int slice = k % pow10;
        return slice % m;
    }

    public static int h2Duplo(int k, int m) {
        int r = k % (m - 1);
        if (r < 0) r += (m - 1);
        return 1 + r;
    }

}
