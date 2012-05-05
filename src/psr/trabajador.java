package psr;

/**
 *
 */
public class trabajador {

    private int numero;
    private trabajador siguiente;

    public trabajador(int num) {
        this.numero = num;
    }

    protected int get_numero() {
        return numero;
    }

    protected trabajador get_siguiente() {
        return siguiente;
    }

    protected void set_siguiente(trabajador sig) {
        this.siguiente = sig;
    }
}
