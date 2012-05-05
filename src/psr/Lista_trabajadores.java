package psr;

/**
 *
 */
public class Lista_trabajadores {

    private trabajador Cap;

    public Lista_trabajadores() {
//        Cap = new vuelo();
//        Cap.set_sig(null);
        Cap = null;
    }

    public trabajador primero() {
        return Cap;
    }

    public void insertarnuevo(int numero) {
        trabajador q;
        trabajador nuevo;
        q = primero();
        nuevo = new trabajador(numero);
        nuevo.set_siguiente(q);
        Cap = nuevo;
    }

    public void eliminar_trabajadores() {
        Cap = null;
    }

    public void eliminar_trabajador(int numero) {
//        tarea t=primero();
//        Cap = t.get_siguiente();
        boolean salir = false;
        trabajador p, q;
        q = primero();
        if (q != null) {
            if (q.get_siguiente() == null) {
                if (q.get_numero() == (numero)) {
                    Cap = null;
                }
            } else {
                if (q.get_numero() == (numero)) {
                    Cap = q.get_siguiente();
                } else {
                    p = q;
                    q = q.get_siguiente();
                    while (q != null && !salir) {
                        if (q.get_numero() == numero) {
                            p.set_siguiente(q.get_siguiente());
                            salir = true;
                        }
                        p = q;
                        q = q.get_siguiente();
                    }
                }
            }
        }
    }

    public int numero_trabajador() {
        int j = 0;
        trabajador q = primero();
        while (q != null) {
            j = j + 1;
            q = q.get_siguiente();
        }
        return j;
    }

    public trabajador obtener_trabajador(int num) {
        trabajador q = primero();
        int j = 1;
        while (q != null && j != num) {
            j = j + 1;
            q = q.get_siguiente();
        }
        return q;
    }

    protected void visualizar_lista() {
        trabajador t = primero();
        while (t != null) {
            System.out.println(t.get_numero());
            t = t.get_siguiente();
        }
    }

    public trabajador siguiente_trabajador(trabajador p) {
        return (p.get_siguiente());
    }

    public int recuperar_nombre(trabajador p) {
        return (p.get_numero());
    }

    public boolean Vacia() {

        if (Cap == null) {
            return true;
        } else {
            return false;
        }
    }
}
