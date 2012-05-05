package psr;

/**
 *
 */
public class elemento {

    int tar1;
    int tar2;
    elemento sig;

    public elemento(int t1, int t2) {
        this.tar1 = t1;
        this.tar2 = t2;
        sig = null;
    }

    public void set_sig(elemento s) {
        sig = s;
    }

    public void set_t1(int t1) {
        tar1 = t1;
    }

    public void set_t2(int t2) {
        tar2 = t2;
    }

    public int get_t1() {
        return tar1;
    }

    public int get_t2() {
        return tar2;
    }

    public elemento get_sig() {
        return sig;
    }
}
