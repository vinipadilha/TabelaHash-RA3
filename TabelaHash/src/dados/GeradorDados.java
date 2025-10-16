package dados;

import modelo.Registro;

import java.util.Random;

public class GeradorDados {

    public static final long SEED_100K = 42L;
    public static final long SEED_1M = 4242L;
    public static final long SEED_10M = 424242L;

    public static final int N_100K = 100_000;
    public static final int N_1M = 1_000_000;
    public static final int N_10M = 10_000_000;

    public static Registro[] gerar(int n, long seed) {
        Registro[] arr = new Registro[n];
        Random rnd = new Random(seed);
        for (int i = 0; i < n; i++) {
            int codigo = rnd.nextInt(1_000_000_000);
            arr[i] = new Registro(codigo);
        }
        return arr;
    }
}
