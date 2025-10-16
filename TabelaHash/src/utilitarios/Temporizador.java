package utilitarios;

public class Temporizador {

    private long inicio;

    public void iniciar() {
        inicio = System.nanoTime();
    }

    public long nanosDecorridos() {
        return System.nanoTime() - inicio;
    }

    public double millisDecorridos() {
        return (System.nanoTime() - inicio) / 1_000_000.0;
    }
}
