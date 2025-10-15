package utilitarios;

/**
 * Cronômetro simples baseado em System.nanoTime().
 * Use iniciar() antes do bloco e depois pegue nanosDecorridos() ou millisDecorridos().
 */
public class Temporizador {

    private long inicio;

    /** Marca o início da medição. */
    public void iniciar() {
        inicio = System.nanoTime();
    }

    /** Retorna o tempo decorrido desde iniciar(), em nanossegundos. */
    public long nanosDecorridos() {
        return System.nanoTime() - inicio;
    }

    /** Retorna o tempo decorrido desde iniciar(), em milissegundos. */
    public double millisDecorridos() {
        return (System.nanoTime() - inicio) / 1_000_000.0;
    }
}
