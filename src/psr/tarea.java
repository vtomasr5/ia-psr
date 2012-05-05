package psr;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class tarea {

    private String nombre_tarea;
    private String hora_ini, hora_fin;
    private String duracion;
    private tarea siguiente;
    protected boolean tarea_sin_horario;
    private Date date_ini = null, date_fin = null, date_duracion = null;
    DateFormat dateFormat = new SimpleDateFormat("HH:mm");

    public tarea(String nombre, String hora_i, String hora_f) throws ParseException {
        this.nombre_tarea = nombre;
        this.hora_ini = hora_i;
        this.hora_fin = hora_f;
        this.tarea_sin_horario = false;

//        try {
        this.date_ini = dateFormat.parse(hora_ini);
//        } catch (ParseException ex) {
//            Logger.getLogger(tarea.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        try {
        this.date_fin = dateFormat.parse(hora_fin);
//        } catch (ParseException ex) {
//            Logger.getLogger(tarea.class.getName()).log(Level.SEVERE, null, ex);
//        }

        long dif = date_fin.getTime() - date_ini.getTime() - 3600000;
//       if ((date_fin.getMinutes()-date_ini.getMinutes()) == 0) {
//            if (date_fin.getHours()-date_ini.getHours() <10){
//                this.duracion= "0" + Integer.toString(date_fin.getHours()-date_ini.getHours()) + ":" + "00";
//            }else{
//                this.duracion= Integer.toString(date_fin.getHours()-date_ini.getHours()) + ":" + "00";
//            }
//       } else{
//            if (date_fin.getHours()-date_ini.getHours() <10){
//                this.duracion= "0" + Integer.toString(date_fin.getHours()-date_ini.getHours()) + ":" + Integer.toString(date_fin.getMinutes()-date_ini.getMinutes());
//            }else{
//                this.duracion= Integer.toString(date_fin.getHours()-date_ini.getHours()) + ":" + Integer.toString(date_fin.getMinutes()-date_ini.getMinutes());
//            }
//
//       }
        this.duracion = dateFormat.format(new Date(dif));
        try {
            date_duracion = dateFormat.parse(duracion);
        } catch (ParseException ex) {
            Logger.getLogger(tarea.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public tarea(String nombre, String duracion) {
        this.nombre_tarea = nombre;
        this.tarea_sin_horario = true;
        this.duracion = duracion;
        try {
            date_duracion = dateFormat.parse(duracion);
        } catch (ParseException ex) {
            Logger.getLogger(tarea.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.hora_ini = "";
        this.hora_fin = "";
    }

    protected String get_nombre() {
        return nombre_tarea;
    }

    protected String get_hora_inicio() {
        return hora_ini;
    }

    protected Date get_hora_inicio_date() {
        return date_ini;
    }

    protected String get_hora_fin() {
        return hora_fin;
    }

    protected Date get_hora_fin_date() {
        return date_fin;
    }

    protected String get_duracion() {
        return duracion;
    }

    protected Date get_duracion_date() {
        return date_duracion;
    }

    protected tarea get_siguiente() {
        return siguiente;
    }

    protected boolean get_tipo() {
        return tarea_sin_horario;
    }

    protected void set_siguiente(tarea sig) {
        this.siguiente = sig;
    }

    protected void set_hora_inicio_date(Date hi) {//cambios
        this.date_ini = hi;
        String ini;
        if (hi == null) {
            this.hora_ini = "";
        } else {
//        this.hora_ini= hi.toString();
            if (hi.getHours() < 10) {
                ini = "0" + "" + hi.getHours() + ":";
            } else {
                ini = hi.getHours() + ":";
            }
            if (hi.getMinutes() < 10) {
                ini = ini + "" + hi.getMinutes() + "0";
            } else {
                ini = ini + hi.getMinutes();
            }
            this.hora_ini = ini;
        }
    }

    protected void set_hora_fin_date(Date hf) {//cambios
        this.date_fin = hf;
        String ini;
        if (hf == null) {
            this.hora_fin = "";
        } else {
            if (hf.getHours() < 10) {
                ini = "0" + hf.getHours() + ":";
            } else {
                ini = hf.getHours() + ":";
            }
            if (hf.getMinutes() < 10) {
                ini = ini + hf.getMinutes() + "0";
            } else {
                ini = ini + hf.getMinutes();
            }
            this.hora_fin = ini;
        }
    }
}
