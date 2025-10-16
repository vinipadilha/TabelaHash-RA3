package modelo;

public class Registro {
    public final int codigo;

    public Registro(int codigo) {
        this.codigo = codigo;
    }

    public String formatado() {
        return String.format("%09d", codigo);
    }
}
