package modelo;

/**
 * Registro simples: guarda o código (9 dígitos).
 * Armazenamos como int (0..999_999_999). Para imprimir, use %09d.
 */
public class Registro {
    public final int codigo;

    public Registro(int codigo) {
        this.codigo = codigo;
    }

    /** Ex.: 42 -> "000000042" */
    public String formatado() {
        return String.format("%09d", codigo);
    }
}
