package psr;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 */
public class Lista_tareas {

    private tarea Cap;

    public Lista_tareas() {
//        Cap = new vuelo();
//        Cap.set_sig(null);
        Cap = null;
    }

    public tarea primero() {
        return Cap;
    }

    public void insertarnuevo(String nombre, String hora_ini, String hora_fin) throws ParseException {
        tarea q;
        tarea nuevo;
        q = primero();
        nuevo = new tarea(nombre, hora_ini, hora_fin);
        nuevo.set_siguiente(q);
        Cap = nuevo;
    }

    public void insertarnuevoespecial(String nombre, String hora_ini, String hora_fin) throws ParseException {
        tarea p = null, q;
        boolean salir = false, actualizado = false;
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        tarea nuevo;
        q = primero();
        if (q == null) {
            nuevo = new tarea(nombre, hora_ini, hora_fin);
            nuevo.set_siguiente(null);
            Cap = nuevo;
        } else {
            if (q.get_siguiente() == null) {
                if (q.get_hora_fin_date().before(dateFormat.parse(hora_ini)) || q.get_hora_fin_date().equals(dateFormat.parse(hora_ini))) {
                    p = q;
                    nuevo = new tarea(nombre, hora_ini, hora_fin);
                    nuevo.set_siguiente(null);
                    p.set_siguiente(nuevo);
                } else {
                    if (q.get_hora_inicio_date().after(dateFormat.parse(hora_fin)) || q.get_hora_inicio_date().equals(dateFormat.parse(hora_fin))) {
                        nuevo = new tarea(nombre, hora_ini, hora_fin);
                        nuevo.set_siguiente(q);
                        Cap = nuevo;
                    }
                }
            } else {
                if (dateFormat.parse(hora_fin).before(q.get_hora_inicio_date()) || dateFormat.parse(hora_fin).equals(q.get_hora_inicio_date())) {
                    nuevo = new tarea(nombre, hora_ini, hora_fin);
                    nuevo.set_siguiente(q);
                    Cap = nuevo;
                } else {
                    while (q != null && salir != true) {
                        p = q;
                        q = q.get_siguiente();
                        if (q != null) {
                            if ((dateFormat.parse(hora_ini).after(p.get_hora_fin_date()) || dateFormat.parse(hora_ini).equals(p.get_hora_fin_date())) && (dateFormat.parse(hora_fin).before(q.get_hora_inicio_date())) || dateFormat.parse(hora_fin).equals(q.get_hora_inicio_date())) {
                                nuevo = new tarea(nombre, hora_ini, hora_fin);
                                nuevo.set_siguiente(q);
                                p.set_siguiente(nuevo);
                                salir = true;
                                actualizado = true;
                            }
                        } else {
                            salir = true;
                        }
                    }
                    if (actualizado == false) {
                        nuevo = new tarea(nombre, hora_ini, hora_fin);
                        nuevo.set_siguiente(null);
                        p.set_siguiente(nuevo);
                    }
                }
            }
        }
    }

    public void insertarnuevo(String nombre, String duracion) {
        tarea q;
        tarea nuevo;
        q = primero();
        nuevo = new tarea(nombre, duracion);
        nuevo.set_siguiente(q);
        Cap = nuevo;
    }

    public void eliminar_tarea(String nombre) {
//        tarea t=primero();
//        Cap = t.get_siguiente();
        boolean salir = false;
        tarea p, q;
        q = primero();
        if (q != null) {
            if (q.get_siguiente() == null) {
                if (q.get_nombre().equals(nombre)) {
                    Cap = null;
                }
            } else {
                if (q.get_nombre().equals(nombre)) {
                    Cap = q.get_siguiente();
                } else {
                    p = q;
                    q = q.get_siguiente();
                    while (q != null && !salir) {
                        if (q.get_nombre().equals(nombre)) {
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

    public int numero_tareas() {
        int j = 0;
        tarea q = primero();
        while (q != null) {
            j = j + 1;
            q = q.get_siguiente();
        }
        return j;
    }

    public tarea obtener_tarea(int num) {
        tarea q = primero();
        int j = 1;
        while (q != null && j != num) {
            j = j + 1;
            q = q.get_siguiente();
        }
        return q;
    }

    protected tarea obtener_tarea(String nombre) {
        tarea q = primero();
        int j = 1;
        while (q != null && !nombre.equals(q.get_nombre())) {
            j++;
            q = q.get_siguiente();
        }
        return q;
    }

    protected void separar_tipo_tareas() {
        boolean haycambios = true;
        tarea t = primero();
        tarea t2 = null;
        tarea t3, t4 = null;
        if (t != null && t.get_siguiente() != null) {
            t2 = t.get_siguiente();
        }
        while (haycambios) {
            haycambios = false;
            if (t.get_hora_inicio().equals("") && !t2.get_hora_inicio().equals("")) {
                t.set_siguiente(t2.get_siguiente());
                t2.set_siguiente(t);
                Cap = t2;
                t4 = t2;
                haycambios = true;
            } else {
                t4 = t;
                t = t2;
            }
            while (t.get_siguiente() != null) {
                t3 = t.get_siguiente();
                if (t.get_hora_inicio().equals("") && !t3.get_hora_inicio().equals("")) {
                    t.set_siguiente(t3.get_siguiente());
                    t3.set_siguiente(t);
                    t4.set_siguiente(t3);
                    t4 = t3;
                    haycambios = true;
                } else {
                    t4 = t;
                    t = t3;
                }
            }
            t = Cap;
            t2 = t.get_siguiente();
        }
    }

    protected void ordenar_tareas() {
//        separar_tipo_tareas();
        boolean haycambios = true;
        tarea t = primero();
        tarea t2 = null;
        tarea t3, t4 = null;
        if (t != null && t.get_siguiente() != null && !t.get_siguiente().get_hora_inicio().equals("")) {
            t2 = t.get_siguiente();
            while (haycambios) {
                haycambios = false;
                if (!t.get_hora_inicio().equals("")) {
                    if (t.get_hora_inicio_date().getTime() > t2.get_hora_inicio_date().getTime()) {
                        t.set_siguiente(t2.get_siguiente());
                        t2.set_siguiente(t);
                        Cap = t2;
                        t4 = t2;
                        haycambios = true;
                    } else {
                        t4 = t;
                        t = t2;
                    }
                    while (t.get_siguiente() != null && !t.get_hora_inicio().equals("") && !t.get_siguiente().get_hora_inicio().equals("")) {
                        t3 = t.get_siguiente();
                        if (t.get_hora_inicio_date().getTime() > t3.get_hora_inicio_date().getTime()) {
                            t.set_siguiente(t3.get_siguiente());
                            t3.set_siguiente(t);
                            t4.set_siguiente(t3);
                            t4 = t3;
                            haycambios = true;
                        } else {
                            t4 = t;
                            t = t3;
                        }
                    }
                    t = Cap;
                    t2 = t.get_siguiente();
                }
                if (t2 == null || t == null || t2.get_hora_inicio().equals("")) {
                    haycambios = false;
                }
            }
        }

    }

    protected void visualizar_lista() {
        tarea t = primero();
        while (t != null) {
            System.out.println(t.get_nombre());
            t = t.get_siguiente();
        }
    }

    public tarea siguiente_tarea(tarea p) {
        return (p.get_siguiente());
    }

    public String recuperar_nombre(tarea p) {
        return (p.get_nombre());
    }

    public String recuperar_duracion(tarea p) {
        return (p.get_duracion());
    }

    public String recuperar_hora_inicio(tarea p) {
        return (p.get_hora_inicio());
    }

    public String recuperar_hora_fin(tarea p) {
        return (p.get_hora_fin());
    }

    public boolean Vacia() {

        if (Cap == null) {
            return true;
        } else {
            return false;
        }
    }
}
