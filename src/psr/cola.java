package psr;

/**
 */
public class cola {

    private elemento Cap;

    public cola() {
        Cap = null;
    }

    public elemento primero() {
        return Cap;
    }

    public void añadir_cola(int t1, int t2) {
        elemento q = primero();
        elemento nuevo = new elemento(t1, t2);
        nuevo.set_sig(Cap);
        Cap = nuevo;
    }

    public boolean Vacia() {
        if (Cap == null) {
            return true;
        } else {
            return false;
        }
    }

    public elemento quitar_cola() {//quita el último de la cola
        elemento q, r;
        q = primero();
        if (q.get_sig() == null) {
            Cap = null;
            return q;
        } else {
            r = primero().get_sig();
            while (r.get_sig() != null) {
                q = r;
                r = r.get_sig();
            }
            q.set_sig(null);
            return r;
        }
    }
}
