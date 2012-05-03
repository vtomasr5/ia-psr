
package psr;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 */
public class PSR extends javax.swing.JFrame {

    /** Creates new form PSR */
    public PSR() {
        initComponents();
        ventana = new JDialog();
    }
    
    JDialog ventana;
    private Lista_tareas ltareas = new Lista_tareas();
    private int[] asignaciones, fallo; // Índice = Nº tarea. Contenido = Nº trabajador que realiza la tarea.
    private int[] trabajadores; // Índice = trabajador. Contenido = Nº tareas asignadas.
    private Lista_tareas[] ltareas_trab; // Índice = Trabajador. Contenido = Lista de tareas del trabajador.
    private Lista_tareas[] ltareas_trab_parcial;
    private int tareas_creadas = 0;
    private int ntareas;
    private String laboral_inicio;
    private String laboral_fin;
    private Date laboral_inicio_date;
    private Date laboral_fin_date;
    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private int numero_trabajadores;
    private boolean[][] matriz_res;
    private double tiempo,tiempo2,tiempo3;
    private Integer nodos_visitados=0;
    private int[] parcial;
    private int num_tar2=0;
    private Lista_trabajadores[] consistencia_arco;
    private void GestionTareasCreadas(tarea t){
    int i=0;
    while (TablaTareas.getValueAt(i, 0) != null){
        i++;
    }
    for (int j=0;j<ntareas;j++){
        TablaTareas.setValueAt(t.get_nombre(), i, 0);
        if (!t.get_hora_inicio().equals("")){
            TablaTareas.setValueAt(t.get_hora_inicio(), i, 1);
            TablaTareas.setValueAt(t.get_hora_fin(), i, 2);
        }else{
            TablaTareas.setValueAt("Sin hora", i, 1);
            TablaTareas.setValueAt("Sin hora", i, 2);
        }
        TablaTareas.setValueAt(t.get_duracion(), i, 3);
        i++;
    }
    TextoNumeroTotalTareas.setText(Integer.toString(tareas_creadas));
}
private int [] ordenar_trabajadores2 (int [] trabajadores){
    int [] aux = new int [trabajadores.length];
    int [] trab = new int [trabajadores.length];
    for (int i=0; i<trabajadores.length; i++){
        trab[i] = trabajadores[i];
    }
    int pos = -1;
    int valor = 1000;
    for (int i=0; i<trabajadores.length; i++){
        valor = 1000;
        for (int j=0; j<trabajadores.length; j++){
            if (trab[j] <= valor){
                valor= trab[j];
                pos = j;
            }
        }
        trab [pos] = 1000;
        aux [i] = pos+1;
    }
//    System.out.println (trabajadores.length);
//    System.out.println ("comienzo");
//    for (int j=0; j<trabajadores.length; j++){
//           System.out.println (aux[j]);
//    }
//    System.out.println ("fin");
    int min_tareas_trab = trabajadores[aux[0]-1];
    for (int i=1; i<aux.length; i++){
        if (trabajadores[aux[i]-1] > min_tareas_trab ){
            aux[i]=-1;
        }
    }
     return aux;
}
    private int [] ordenar_trabajadores (int [] trabajadores){
    int [] aux = new int [trabajadores.length];
    int [] trab = new int [trabajadores.length];
    for (int i=0; i<trabajadores.length; i++){
        trab[i] = trabajadores[i];
    }
    int pos = -1;
    int valor = 1000;
    for (int i=0; i<trabajadores.length; i++){
        valor = 1000;
        for (int j=0; j<trabajadores.length; j++){
            if (trab[j] <= valor){
                valor= trab[j];
                pos = j;
            }
        }
        trab [pos] = 1000;
        aux [i] = pos+1;
    }
//    System.out.println (trabajadores.length);
//    System.out.println ("comienzo");
//    for (int j=0; j<trabajadores.length; j++){
//           System.out.println (aux[j]);
//    }
//    System.out.println ("fin");
    return aux;
}
    private boolean equitativo(int[] asig){
        int min_tareas_trab = 1000;
        int max_tareas_trab = -1;
        for (int i=0;i<trabajadores.length;i++){
            if (trabajadores[i] < min_tareas_trab ){
                min_tareas_trab = trabajadores[i];
            }
        }
        for (int i=0;i<trabajadores.length;i++){
            if(trabajadores[i] > max_tareas_trab){
                max_tareas_trab = trabajadores[i];
            }
        }
        if ((max_tareas_trab - min_tareas_trab)>=2){
            return false;
        }else{
//            System.out.println(max_tareas_trab - min_tareas_trab);
            return true;
        }
    }
    private boolean equitativo2(int[] asig){
        int min_tareas_trab = 1000;
        int max_tareas_trab = -1;
        int num_tar=0;
        for (int i=0;i<asig.length;i++){
            if (asig[i]!=-1){
                num_tar++;
            }
        }
        for (int i=0;i<trabajadores.length;i++){
            if (trabajadores[i] < min_tareas_trab ){
                min_tareas_trab = trabajadores[i];
            }
        }
        for (int i=0;i<trabajadores.length;i++){
            if(trabajadores[i] > max_tareas_trab){
                max_tareas_trab = trabajadores[i];
            }
        }
        if ((max_tareas_trab - min_tareas_trab)>=2){
            return false;
        }else{
//            System.out.println(max_tareas_trab - min_tareas_trab);
            if (num_tar >= num_tar2){
                num_tar2=num_tar;
                return true;
            }else{
                return false;
            }
        }
    }
private int[] PrimeroProfundidadVA2(int[] asignaciones, int[] trabajadores, Lista_tareas[] ltareas_trab,Lista_tareas ltareas,boolean[][] matriz_res) throws ParseException{
    int pos_tarea;
    String hora_inicio, hora_fin;
    tarea tar;
    int pos_trabajador;
    int[] resultado = new int [tareas_creadas];
    if (asignacion_completa(asignaciones) && equitativo(asignaciones)){
        return asignaciones;
    }
    pos_tarea = seleccionar_tarea(asignaciones);//ltareas.obtener_tarea(pos_tarea).get_hora_inicio();
    if (pos_tarea !=-1){
        for (int i=0;i<numero_trabajadores;i++){
//            nodos_visitados++;
            pos_trabajador = i+1;
            nodos_visitados++;
            //nodos_visitados++;
//            pos_trabajador = aux[i];//seleccionar_trabajador(trabajadores);
            if (pos_trabajador != -1){
                if (asignacion_valida(pos_tarea, pos_trabajador-1, ltareas_trab, ltareas, matriz_res)){
                    asignaciones[pos_tarea-1]=pos_trabajador;
                    trabajadores[pos_trabajador-1]++;
                    if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()<10){
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes()<10){
                        hora_inicio = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }else{
                        hora_inicio = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }
                    }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes()<10){
                        hora_inicio = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }else{
                        hora_inicio = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }
                    }
                    if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()<10){
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes()<10){
                        hora_fin = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }else{
                        hora_fin = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }
                    }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes()<10){
                        hora_fin = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }else{
                        hora_fin = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }
                    }
                   
                   
                    ltareas_trab[pos_trabajador-1].insertarnuevoespecial(ltareas.obtener_tarea(pos_tarea).get_nombre(), hora_inicio, hora_fin);
//                    nodos_visitados++;
                    if (equitativo2(asignaciones) && !asignacion_completa(asignaciones)){
                        System.arraycopy(asignaciones, 0,parcial, 0, asignaciones.length);
//                        System.arraycopy(ltareas_trab, 0, ltareas_trab_parcial, 0, ltareas_trab.length);
                        ltareas_trab_parcial = ltareas_trab.clone();
                                //parcial = asignaciones;
                    }
                    resultado = PrimeroProfundidadVA2(asignaciones, trabajadores, ltareas_trab, ltareas, matriz_res);
                    //nodos_visitados++;
                   if (asignacion_completa(resultado) && equitativo(resultado)){
                       return resultado;
                   }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_tipo()==true){
                            ltareas.obtener_tarea(pos_tarea).set_hora_inicio_date(null);
                            ltareas.obtener_tarea(pos_tarea).set_hora_fin_date(null);
                            comprobar_restricciones(matriz_res, ltareas);
                        }
                       asignaciones[pos_tarea-1]=-1;
                       trabajadores[pos_trabajador-1]--;
                       ltareas_trab[pos_trabajador-1].eliminar_tarea(ltareas.obtener_tarea(pos_tarea).get_nombre());
                   }
                }
            }

        }
    }
        return asignaciones;//cambiado
//    }

}

private  void propagacion (int num_trab, int num_tar, Lista_trabajadores[] ca, boolean[][] matriz_res){
    ca [num_tar-1].eliminar_trabajadores();
    ca [num_tar-1].insertarnuevo(num_trab);
    for (int i=0; i< tareas_creadas; i++){
        if (num_tar-1 != i){
            if (matriz_res [num_tar-1][i] ){
                ca [i].eliminar_trabajador(num_trab);
            }
        }
    }
    //return ca;
}

private int[] PrimeroMVR2(int[] asignaciones, int[] trabajadores, Lista_tareas[] ltareas_trab, Lista_tareas ltareas,boolean[][] matriz_res, Lista_trabajadores[] ca) throws ParseException{
        int pos_tarea;
    String hora_inicio, hora_fin;
    int pos_trabajador;
    trabajador tra;
    int[] resultado = new int [tareas_creadas];
    Lista_trabajadores[] ca_backup = new Lista_trabajadores[tareas_creadas];
    if (asignacion_completa(asignaciones) && equitativo(asignaciones)){
        return asignaciones;
    }
    pos_tarea = seleccionar_tarea_mvr(asignaciones);//ltareas.obtener_tarea(pos_tarea).get_hora_inicio();
    if (pos_tarea !=-1){
//        tra = ca [pos_tarea-1].primero();
//        while (tra != null){
        for (int i=0;i<numero_trabajadores;i++){
            pos_trabajador = i+1;
              nodos_visitados++;
//            pos_trabajador = tra.get_numero();
            if (pos_trabajador != -1){
                if (asignacion_valida(pos_tarea, pos_trabajador-1, ltareas_trab, ltareas, matriz_res)){
                    asignaciones[pos_tarea-1]=pos_trabajador;
                    trabajador t;
                    for (int x=0 ; x<tareas_creadas; x++){
                        ca_backup[x] = new Lista_trabajadores ();
                        t = ca [x].primero();
                        while (t != null){
                            ca_backup [x].insertarnuevo(t.get_numero());
                            t = t.get_siguiente();
                        }
                    }
                    propagacion (pos_trabajador, pos_tarea, ca, matriz_res);
                    trabajadores[pos_trabajador-1]++;if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()<10){
                    if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes()<10){
                        hora_inicio = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }else{
                        hora_inicio = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }
                    }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes()<10){
                        hora_inicio = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }else{
                        hora_inicio = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }
                    }
                    if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()<10){
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes()<10){
                        hora_fin = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }else{
                        hora_fin = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }
                    }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes()<10){
                        hora_fin = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }else{
                        hora_fin = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }
                    }
                    ltareas_trab[pos_trabajador-1].insertarnuevoespecial(ltareas.obtener_tarea(pos_tarea).get_nombre(), hora_inicio, hora_fin);
                    if (equitativo2(asignaciones) && !asignacion_completa(asignaciones)){
                        System.arraycopy(asignaciones, 0,parcial, 0, asignaciones.length);
//                        System.arraycopy(ltareas_trab, 0, ltareas_trab_parcial, 0, ltareas_trab.length);
                        ltareas_trab_parcial = ltareas_trab.clone();
                                //parcial = asignaciones;
                    }
                    resultado = PrimeroMVR2(asignaciones, trabajadores, ltareas_trab, ltareas, matriz_res,ca);
                    //nodos_visitados++;
                   if (asignacion_completa(resultado) && equitativo(resultado)){
                       return resultado;
                   }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_tipo()==true){
                            ltareas.obtener_tarea(pos_tarea).set_hora_inicio_date(null);
                            ltareas.obtener_tarea(pos_tarea).set_hora_fin_date(null);
                            comprobar_restricciones(matriz_res, ltareas);
                        }

                    for (int x=0 ; x<tareas_creadas; x++){
                        ca[x] = new Lista_trabajadores ();
                        t = ca_backup [x].primero();
                        while (t != null){
                            ca [x].insertarnuevo(t.get_numero());
                            t = t.get_siguiente();
                        }
                    }

                       asignaciones[pos_tarea-1]=-1;
                       trabajadores[pos_trabajador-1]--;
                       ltareas_trab[pos_trabajador-1].eliminar_tarea(ltareas.obtener_tarea(pos_tarea).get_nombre());
                   }
                }
            }
//            tra = tra.get_siguiente();
        }
    }
        return fallo;
}

private int[] PrimeroMVRGradoHeuristico2(int[] asignaciones, int[] trabajadores, Lista_tareas[] ltareas_trab, Lista_tareas ltareas,boolean[][] matriz_res, Lista_trabajadores[] ca) throws ParseException{
             int pos_tarea;
    String hora_inicio, hora_fin;
    int pos_trabajador;
     trabajador tra;
    int[] resultado = new int [tareas_creadas];
    Lista_trabajadores[] ca_backup = new Lista_trabajadores[tareas_creadas];
    if (asignacion_completa(asignaciones) && equitativo(asignaciones)){
        return asignaciones;
    }
    pos_tarea = seleccionar_tarea_mvr_gh(asignaciones, ltareas_trab, ltareas, matriz_res);//ltareas.obtener_tarea(pos_tarea).get_hora_inicio();
    if (pos_tarea !=-1){
        for (int i=0;i<numero_trabajadores;i++){
//        tra = ca [pos_tarea-1].primero();
//        while (tra != null){
            pos_trabajador = i+1;//tra.get_numero();
            nodos_visitados++;
            if (pos_trabajador != -1){
                if (asignacion_valida(pos_tarea, pos_trabajador-1, ltareas_trab, ltareas, matriz_res)){
                    asignaciones[pos_tarea-1]=pos_trabajador;
                    trabajador t;
                    for (int x=0 ; x<tareas_creadas; x++){
                        ca_backup[x] = new Lista_trabajadores ();
                        t = ca [x].primero();
                        while (t != null){
                            ca_backup [x].insertarnuevo(t.get_numero());
                            t = t.get_siguiente();
                        }
                    }
                    propagacion (pos_trabajador, pos_tarea, ca, matriz_res);
                    trabajadores[pos_trabajador-1]++;
                   if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()<10){
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes()<10){
                        hora_inicio = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }else{
                        hora_inicio = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }
                    }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes()<10){
                        hora_inicio = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }else{
                        hora_inicio = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }
                    }
                    if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()<10){
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes()<10){
                        hora_fin = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }else{
                        hora_fin = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }
                    }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes()<10){
                        hora_fin = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }else{
                        hora_fin = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }
                    }
                    ltareas_trab[pos_trabajador-1].insertarnuevoespecial(ltareas.obtener_tarea(pos_tarea).get_nombre(), hora_inicio, hora_fin);
                    if (equitativo2(asignaciones) && !asignacion_completa(asignaciones)){
                        System.arraycopy(asignaciones, 0,parcial, 0, asignaciones.length);
//                        System.arraycopy(ltareas_trab, 0, ltareas_trab_parcial, 0, ltareas_trab.length);
                        ltareas_trab_parcial = ltareas_trab.clone();
                                //parcial = asignaciones;
                    }
                    resultado = PrimeroMVRGradoHeuristico2(asignaciones, trabajadores, ltareas_trab, ltareas, matriz_res, ca);
                    //nodos_visitados++;
                   if (asignacion_completa(resultado) && equitativo(resultado)){
                       return resultado;
                   }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_tipo()==true){
                            ltareas.obtener_tarea(pos_tarea).set_hora_inicio_date(null);
                            ltareas.obtener_tarea(pos_tarea).set_hora_fin_date(null);
                            comprobar_restricciones(matriz_res, ltareas);
                        }

                        for (int x=0 ; x<tareas_creadas; x++){
                        ca[x] = new Lista_trabajadores ();
                        t = ca_backup [x].primero();
                        while (t != null){
                            ca [x].insertarnuevo(t.get_numero());
                            t = t.get_siguiente();
                        }
                    }
                       asignaciones[pos_tarea-1]=-1;
                       trabajadores[pos_trabajador-1]--;
                       ltareas_trab[pos_trabajador-1].eliminar_tarea(ltareas.obtener_tarea(pos_tarea).get_nombre());
                   }
                }
            }
            // tra = tra.get_siguiente();
        }
    }
        return fallo;
}

private int[] PrimeroMVRGradoHeuristicoConsistenciaArco2(int[] asignaciones, int[] trabajadores, Lista_tareas[] ltareas_trab, Lista_tareas ltareas,boolean[][] matriz_res, Lista_trabajadores[] ca) throws ParseException{
             int pos_tarea;
    String hora_inicio, hora_fin;
    int pos_trabajador;
     trabajador tra;
    int[] resultado = new int [tareas_creadas];
    Lista_trabajadores[] ca_backup = new Lista_trabajadores[tareas_creadas];
    if (asignacion_completa(asignaciones) && equitativo(asignaciones)){
        return asignaciones;
    }
    pos_tarea = seleccionar_tarea_mvr_gh(asignaciones, ltareas_trab, ltareas, matriz_res);//ltareas.obtener_tarea(pos_tarea).get_hora_inicio();
    if (pos_tarea !=-1){
        //for (int i=0;i<numero_trabajadores;i++){
        tra = ca [pos_tarea-1].primero();
        while (tra != null){
            pos_trabajador = tra.get_numero();
            nodos_visitados++;
            if (pos_trabajador != -1){
                if (asignacion_valida(pos_tarea, pos_trabajador-1, ltareas_trab, ltareas, matriz_res)){
                    asignaciones[pos_tarea-1]=pos_trabajador;
                    trabajador t;
                    for (int x=0 ; x<tareas_creadas; x++){
                        ca_backup[x] = new Lista_trabajadores ();
                        t = ca [x].primero();
                        while (t != null){
                            ca_backup [x].insertarnuevo(t.get_numero());
                            t = t.get_siguiente();
                        }
                    }
                    propagacion (pos_trabajador, pos_tarea, ca, matriz_res);
                    trabajadores[pos_trabajador-1]++;
                   if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()<10){
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes()<10){
                        hora_inicio = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }else{
                        hora_inicio = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }
                    }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes()<10){
                        hora_inicio = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }else{
                        hora_inicio = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }
                    }
                    if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()<10){
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes()<10){
                        hora_fin = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }else{
                        hora_fin = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }
                    }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes()<10){
                        hora_fin = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }else{
                        hora_fin = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }
                    }
                    ltareas_trab[pos_trabajador-1].insertarnuevoespecial(ltareas.obtener_tarea(pos_tarea).get_nombre(), hora_inicio, hora_fin);
                    AC3 (ca, matriz_res, asignaciones);
                    if (equitativo2(asignaciones) && !asignacion_completa(asignaciones)){
                        System.arraycopy(asignaciones, 0,parcial, 0, asignaciones.length);
//                        System.arraycopy(ltareas_trab, 0, ltareas_trab_parcial, 0, ltareas_trab.length);
                        ltareas_trab_parcial = ltareas_trab.clone();
                                //parcial = asignaciones;
                    }
                    resultado = PrimeroMVRGradoHeuristicoConsistenciaArco2(asignaciones, trabajadores, ltareas_trab, ltareas, matriz_res, ca);
                    //nodos_visitados++;
                   if (asignacion_completa(resultado) && equitativo(resultado)){
                       return resultado;
                   }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_tipo()==true){
                            ltareas.obtener_tarea(pos_tarea).set_hora_inicio_date(null);
                            ltareas.obtener_tarea(pos_tarea).set_hora_fin_date(null);
                            comprobar_restricciones(matriz_res, ltareas);
                        }

                        for (int x=0 ; x<tareas_creadas; x++){
                        ca[x] = new Lista_trabajadores ();
                        t = ca_backup [x].primero();
                        while (t != null){
                            ca [x].insertarnuevo(t.get_numero());
                            t = t.get_siguiente();
                        }
                    }
                       asignaciones[pos_tarea-1]=-1;
                       trabajadores[pos_trabajador-1]--;
                       ltareas_trab[pos_trabajador-1].eliminar_tarea(ltareas.obtener_tarea(pos_tarea).get_nombre());
                   }
                }
            }
             tra = tra.get_siguiente();
        }
    }
        return fallo;
}

private int[] PrimeroGradoHeuristico2(int[] asignaciones, int[] trabajadores, Lista_tareas[] ltareas_trab, Lista_tareas ltareas,boolean[][] matriz_res) throws ParseException{
           int pos_tarea;
    boolean tar_dur = false;
    String hora_inicio, hora_fin;
    tarea tar;
    int pos_trabajador;
    int[] resultado = new int [tareas_creadas];
    int [] aux = new int [numero_trabajadores];
    if (asignacion_completa(asignaciones) && equitativo(asignaciones)){
        return asignaciones;
    }
    pos_tarea = seleccionar_tarea_gh(asignaciones);//ltareas.obtener_tarea(pos_tarea).get_hora_inicio();
    if (pos_tarea !=-1){
//    System.out.println ("hola");
//    aux = ordenar_trabajadores2 (trabajadores);
//    if (pos_tarea != -1){
        for (int i=0;i<numero_trabajadores;i++){
//            nodos_visitados++;
            pos_trabajador = i+1;
//            pos_trabajador = aux[i];//seleccionar_trabajador(trabajadores);
            if (pos_trabajador != -1){
                if (asignacion_valida(pos_tarea, pos_trabajador-1, ltareas_trab, ltareas, matriz_res)){
                    asignaciones[pos_tarea-1]=pos_trabajador;
                    trabajadores[pos_trabajador-1]++;
if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()<10){
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes()<10){
                        hora_inicio = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }else{
                        hora_inicio = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }
                    }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes()<10){
                        hora_inicio = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }else{
                        hora_inicio = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }
                    }
                    if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()<10){
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes()<10){
                        hora_fin = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }else{
                        hora_fin = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }
                    }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes()<10){
                        hora_fin = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }else{
                        hora_fin = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }
                    }
                    ltareas_trab[pos_trabajador-1].insertarnuevoespecial(ltareas.obtener_tarea(pos_tarea).get_nombre(), hora_inicio, hora_fin);
//                    nodos_visitados++;
                    if (equitativo2(asignaciones) && !asignacion_completa(asignaciones)){
                        System.arraycopy(asignaciones, 0,parcial, 0, asignaciones.length);
//                        System.arraycopy(ltareas_trab, 0, ltareas_trab_parcial, 0, ltareas_trab.length);
                        ltareas_trab_parcial = ltareas_trab.clone();
                                //parcial = asignaciones;
                    }
                    resultado = PrimeroGradoHeuristico2(asignaciones, trabajadores, ltareas_trab, ltareas, matriz_res);
                    nodos_visitados++;
                   if (asignacion_completa(resultado) && equitativo(resultado)){
                       return resultado;
                   }else{
//                        System.out.println("HOLAAAAAAAA CALAMAR222222222");
                        if (ltareas.obtener_tarea(pos_tarea).get_tipo()==true){
                            ltareas.obtener_tarea(pos_tarea).set_hora_inicio_date(null);
                            ltareas.obtener_tarea(pos_tarea).set_hora_fin_date(null);
                            comprobar_restricciones(matriz_res, ltareas);
                        }
                       asignaciones[pos_tarea-1]=-1;
                       trabajadores[pos_trabajador-1]--;
                       ltareas_trab[pos_trabajador-1].eliminar_tarea(ltareas.obtener_tarea(pos_tarea).get_nombre());
                   }
                }
            }

        }
    }
        return fallo;
}

private boolean borrar_valores_incons (elemento e, Lista_trabajadores[] ca){
    int t_ini, t_fin;
    trabajador t1, t2, candidato;
    t_ini = e.get_t1();
    t_fin = e.get_t2();
    boolean borrado = false;
    t1 = ca [t_ini].primero();
    while (t1 != null){
        t2 = null;
        candidato = ca [t_fin].primero();
        while (candidato != null){
            if (candidato != t1){
                t2 = candidato;
            }
            candidato = candidato.get_siguiente();
        }
        if (t2 == null){
            ca [t_ini].eliminar_trabajador(t1.get_numero());
            borrado = true;
        }
        t1 = t1.get_siguiente();
    }
    return borrado;
}

private void AC3 (Lista_trabajadores[] ca, boolean[][] matriz_res, int[] asignaciones){
    cola c = new cola ();
    elemento e;
    for (int i=0; i< asignaciones.length; i++){
        if (asignaciones [i] == -1){
            for (int j=0; j<tareas_creadas; j++){
                if (matriz_res [i][j] == true && i!=j){
                    if (asignaciones [j] == -1){
                        c.añadir_cola(i, j);
                    }
                }
            }
        }
    }
    while (!c.Vacia()){
        e = c.quitar_cola();
        if (borrar_valores_incons (e, ca)){
            int t_ini = e.get_t1();
            for (int k=0; k< tareas_creadas; k++){
                if (matriz_res [t_ini][k] == true && t_ini!=k){
                    if (asignaciones [k] == -1){
                        c.añadir_cola(k, t_ini);
                    }
                }
            }
        }
    }
    //return ca;
}

private int[] PrimeroConsistenciaArco(int[] asignaciones, int[] trabajadores, Lista_tareas[] ltareas_trab, Lista_tareas ltareas,boolean[][] matriz_res, Lista_trabajadores[] ca) throws ParseException{
        int pos_tarea;
    String hora_inicio, hora_fin;
    trabajador trab;
    int pos_trabajador;
    int[] resultado = new int [tareas_creadas];
    if (asignacion_completa(asignaciones) && equitativo(asignaciones)){
        return asignaciones;
    }
    Lista_trabajadores[] ca_backup = new Lista_trabajadores[tareas_creadas];
    pos_tarea = seleccionar_tarea(asignaciones);//ltareas.obtener_tarea(pos_tarea).get_hora_inicio();
    if (pos_tarea !=-1){
        trab = ca[pos_tarea-1].primero();
        //for (int i=0;i< ca[pos_tarea-1].numero_trabajador();i++){
        while (trab != null){
            //nodos_visitados++;
            pos_trabajador = trab.get_numero();
            if (pos_trabajador != -1){
                if (asignacion_valida(pos_tarea, pos_trabajador-1, ltareas_trab, ltareas, matriz_res)){
                    trabajador t;
                    for (int x=0 ; x<tareas_creadas; x++){
                        ca_backup[x] = new Lista_trabajadores ();
                        t = ca [x].primero();
                        while (t != null){
                            ca_backup [x].insertarnuevo(t.get_numero());
                            t = t.get_siguiente();
                        }
                    }
//                    for (int z=0; z<tareas_creadas;z++){
//                        System.out.println ("TAREA " + z);
//                        t= ca_backup[z].primero();
//                        while (t != null){
//                            System.out.println ("trabajador: " + t.get_numero());
//                            t = t.get_siguiente();
//                        }
//                    }
                    propagacion (pos_trabajador, pos_tarea, ca, matriz_res);
                        asignaciones[pos_tarea-1]=pos_trabajador;
//                        System.out.println ("ASIGNACIONES");
//                        for (int z=0; z<asignaciones.length; z++){
//                            System.out.println ("POS: " + z + " valor: " + asignaciones [z]);
//                        }
                        trabajadores[pos_trabajador-1]++;
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()<10){
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes()<10){
                        hora_inicio = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }else{
                        hora_inicio = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }
                    }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes()<10){
                        hora_inicio = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }else{
                        hora_inicio = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }
                    }
                    if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()<10){
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes()<10){
                        hora_fin = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }else{
                        hora_fin = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }
                    }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes()<10){
                        hora_fin = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }else{
                        hora_fin = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }
                    }


                        ltareas_trab[pos_trabajador-1].insertarnuevoespecial(ltareas.obtener_tarea(pos_tarea).get_nombre(), hora_inicio, hora_fin);
                        //nodos_visitados++;
                        AC3 (ca, matriz_res, asignaciones);
                        if (equitativo2(asignaciones) && !asignacion_completa(asignaciones)){
                        System.arraycopy(asignaciones, 0,parcial, 0, asignaciones.length);
//                        System.arraycopy(ltareas_trab, 0, ltareas_trab_parcial, 0, ltareas_trab.length);
                        ltareas_trab_parcial = ltareas_trab.clone();
                                //parcial = asignaciones;
                    }
                        resultado = PrimeroConsistenciaArco(asignaciones, trabajadores, ltareas_trab, ltareas, matriz_res,ca);
                        nodos_visitados++;
                       if (asignacion_completa(resultado) && equitativo(resultado)){
                           return resultado;
                       }else{
                            if (ltareas.obtener_tarea(pos_tarea).get_tipo()==true){
                                ltareas.obtener_tarea(pos_tarea).set_hora_inicio_date(null);
                                ltareas.obtener_tarea(pos_tarea).set_hora_fin_date(null);
                                comprobar_restricciones(matriz_res, ltareas);
                            }
//                            System.out.println ("llam rec ");
                             for (int x=0 ; x<tareas_creadas; x++){
                                ca[x].eliminar_trabajadores();
                                t = ca_backup [x].primero();
                                while (t != null){
                                    ca [x].insertarnuevo(t.get_numero());
                                    t = t.get_siguiente();
                                }
                            }
//                                for (int z=0; z<tareas_creadas;z++){
//                                    System.out.println ("TAREA " + z);
//                                    t= ca[z].primero();
//                                    while (t != null){
//                                        System.out.println ("trabajador: " + t.get_numero());
//                                        t = t.get_siguiente();
//                                    }
//                                }
//                           System.out.println ("ASIGNACIONES");
//                            for (int z=0; z<asignaciones.length; z++){
//                                System.out.println ("POS: " + z + " valor: " + asignaciones [z]);
//                            }
                           asignaciones[pos_tarea-1]=-1;
                           trabajadores[pos_trabajador-1]--;
                           ltareas_trab[pos_trabajador-1].eliminar_tarea(ltareas.obtener_tarea(pos_tarea).get_nombre());
                       }
                }
            }
            trab = trab.get_siguiente();
        }
    }
        return fallo;
//    }

}

private int[] Extra(int[] asignaciones, int[] trabajadores, Lista_tareas[] ltareas_trab, Lista_tareas ltareas,boolean[][] matriz_res, Lista_trabajadores[] ca) throws ParseException{
    int pos_tarea;
    String hora_inicio, hora_fin;
    int pos_trabajador;
     trabajador tra;
     int [] aux = new int [numero_trabajadores];
    int[] resultado = new int [tareas_creadas];
    Lista_trabajadores[] ca_backup = new Lista_trabajadores[tareas_creadas];
    if (asignacion_completa(asignaciones)){
        return asignaciones;
    }
    pos_tarea = seleccionar_tarea_mvr_gh(asignaciones, ltareas_trab, ltareas, matriz_res);//ltareas.obtener_tarea(pos_tarea).get_hora_inicio();
    if (pos_tarea !=-1){
        aux = ordenar_trabajadores2 (trabajadores);
        for (int i=0;i<numero_trabajadores;i++){
//        tra = ca [pos_tarea-1].primero();
//        while (tra != null){
//            pos_trabajador = i+1;//tra.get_numero();
            pos_trabajador = aux[i];
            nodos_visitados++;
            if (pos_trabajador != -1){
                if (asignacion_valida(pos_tarea, pos_trabajador-1, ltareas_trab, ltareas, matriz_res)){
                    asignaciones[pos_tarea-1]=pos_trabajador;
                    trabajador t;
                    for (int x=0 ; x<tareas_creadas; x++){
                        ca_backup[x] = new Lista_trabajadores ();
                        t = ca [x].primero();
                        while (t != null){
                            ca_backup [x].insertarnuevo(t.get_numero());
                            t = t.get_siguiente();
                        }
                    }
                    propagacion (pos_trabajador, pos_tarea, ca, matriz_res);
                    trabajadores[pos_trabajador-1]++;
                   if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()<10){
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes()<10){
                        hora_inicio = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }else{
                        hora_inicio = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }
                    }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes()<10){
                        hora_inicio = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }else{
                        hora_inicio = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_inicio_date().getMinutes());
                        }
                    }
                    if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()<10){
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes()<10){
                        hora_fin = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }else{
                        hora_fin = "0"+Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }
                    }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes()<10){
                        hora_fin = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":0" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }else{
                        hora_fin = Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getHours()) + ":" +
                           Integer.toString(ltareas.obtener_tarea(pos_tarea).get_hora_fin_date().getMinutes());
                        }
                    }
                    ltareas_trab[pos_trabajador-1].insertarnuevoespecial(ltareas.obtener_tarea(pos_tarea).get_nombre(), hora_inicio, hora_fin);
                    if (equitativo2(asignaciones) && !asignacion_completa(asignaciones)){
                        System.arraycopy(asignaciones, 0,parcial, 0, asignaciones.length);
//                        System.arraycopy(ltareas_trab, 0, ltareas_trab_parcial, 0, ltareas_trab.length);
                        ltareas_trab_parcial = ltareas_trab.clone();
                                //parcial = asignaciones;
                    }
                    resultado = Extra(asignaciones, trabajadores, ltareas_trab, ltareas, matriz_res, ca);
                    //nodos_visitados++;
                   if (asignacion_completa(resultado)){
                       return resultado;
                   }else{
                        if (ltareas.obtener_tarea(pos_tarea).get_tipo()==true){
                            ltareas.obtener_tarea(pos_tarea).set_hora_inicio_date(null);
                            ltareas.obtener_tarea(pos_tarea).set_hora_fin_date(null);
                            comprobar_restricciones(matriz_res, ltareas);
                        }

                        for (int x=0 ; x<tareas_creadas; x++){
                        ca[x] = new Lista_trabajadores ();
                        t = ca_backup [x].primero();
                        while (t != null){
                            ca [x].insertarnuevo(t.get_numero());
                            t = t.get_siguiente();
                        }
                    }
                       asignaciones[pos_tarea-1]=-1;
                       trabajadores[pos_trabajador-1]--;
                       ltareas_trab[pos_trabajador-1].eliminar_tarea(ltareas.obtener_tarea(pos_tarea).get_nombre());
                   }
                }
            }
            // tra = tra.get_siguiente();
        }
    }
        return fallo;
}

private void Todos() throws ParseException{
     tiempo=System.nanoTime();
    int[] resultado2 = null;
    try {
        resultado2 = PrimeroMVRGradoHeuristicoConsistenciaArco2(asignaciones, trabajadores, ltareas_trab, ltareas, matriz_res, consistencia_arco);
    } catch (ParseException ex) {
        Logger.getLogger(PSR.class.getName()).log(Level.SEVERE, null, ex);
    }
            tiempo2 = System.nanoTime();
            tiempo3=(tiempo2-tiempo);
            Double t = tiempo3/Math.pow(10, 9);
//            System.out.println("Tiempo de ejecución: "+t);
            TextoNodosVisitados.setText(nodos_visitados.toString());
            nodos_visitados=0;
            TextoTiempoEjecucion.setText(t.toString());
//            for (int i = 0; i < resultado2.length; i++) {
//                System.out.println("La tarea: " + i + " la realiza el trabajador: " + resultado2[i]);
//                System.out.println("hora inicio: " + ltareas.obtener_tarea(i + 1).get_hora_inicio());
//                System.out.println("hora fin: " + ltareas.obtener_tarea(i + 1).get_hora_fin());
//            }
            visualizar_resultados(resultado2);
}
private boolean asignacion_completa (int[]asignaciones){
    for (int i=0; i<asignaciones.length;i++){
        if (asignaciones[i]==-1){
            return false;
        }
    }
    return true;
}
private boolean tarea_valida (tarea t){
    if (!t.get_hora_inicio().equals("")){
        if (t.get_hora_inicio_date().getTime() < laboral_inicio_date.getTime() || t.get_hora_fin_date().getTime() > laboral_fin_date.getTime()){
            return false;
        }else{
            return true;
        }
    }else{
        if (t.get_duracion_date().getTime() > (laboral_fin_date.getTime() - laboral_inicio_date.getTime())){
            return false;
        }else{
            return true;
        }
    }
}
private boolean comprobacion_tareas(){
    tarea t=ltareas.primero();
    while (t!=null){
        if (!tarea_valida(t)){
            return false;
        }
        t=t.get_siguiente();
    }
    return true;
}
private boolean solapamiento(int i, int j){
    tarea t1 = ltareas.obtener_tarea(i);
    tarea t2 = ltareas.obtener_tarea(j);
    if ((t1.get_hora_inicio_date().after(t2.get_hora_inicio_date()) && t1.get_hora_fin_date().after(t2.get_hora_fin_date()) &&
        t1.get_hora_inicio_date().before(t2.get_hora_fin_date()) && t1.get_hora_fin_date().after(t2.get_hora_inicio_date()))||
        (t1.get_hora_inicio_date().before(t2.get_hora_inicio_date()) && t1.get_hora_fin_date().after(t2.get_hora_fin_date()))||
        (t1.get_hora_inicio_date().before(t2.get_hora_inicio_date()) && t1.get_hora_fin_date().before(t2.get_hora_fin_date()) &&
        t1.get_hora_inicio_date().before (t2.get_hora_fin_date()) && t1.get_hora_fin_date().after (t2.get_hora_inicio_date())) ||
        (t1.get_hora_inicio_date().equals(t2.get_hora_inicio_date()) && t1.get_hora_fin_date().equals(t2.get_hora_fin_date())) ||
        (t2.get_hora_inicio_date().before(t1.get_hora_inicio_date()) && t2.get_hora_fin_date().after(t1.get_hora_fin_date())) ||

         (t1.get_hora_inicio_date().equals(t2.get_hora_inicio_date()) && t1.get_hora_fin_date().after(t2.get_hora_fin_date())) ||

         (t1.get_hora_inicio_date().equals(t2.get_hora_inicio_date()) && t1.get_hora_fin_date().before(t2.get_hora_fin_date())) ||
         (t1.get_hora_inicio_date().before(t2.get_hora_inicio_date()) && t1.get_hora_fin_date().equals(t2.get_hora_fin_date())) ||

        (t1.get_hora_inicio_date().after(t2.get_hora_inicio_date()) && t1.get_hora_fin_date().equals(t2.get_hora_fin_date()))
        ){
            return true;
    }else{
       return false;
    }
}
private void comprobar_restricciones(boolean[][] matriz_res, Lista_tareas ltareas){
    tarea tar1, tar2;
    for (int i=0;i<tareas_creadas;i++){
        tar1 = ltareas.obtener_tarea(i+1);
        for (int j=0;j<tareas_creadas;j++){
            if (i!=j){
                tar2 = ltareas.obtener_tarea(j+1);
                if (tar1.get_hora_inicio().equals("") || tar2.get_hora_inicio().equals("")){
                    matriz_res[i][j]=false;
                }else{
                     if (solapamiento(i+1,j+1)){
                        matriz_res[i][j]=true;
                    }else{
                        matriz_res[i][j]=false;
                    }
                }
            }else{
                matriz_res[i][j]=true;
            }
        }
    }
}
private int seleccionar_tarea (int[] asignaciones){
    for (int i=0;i<asignaciones.length;i++){
        if (asignaciones[i]==-1){
            return i+1;////OJO; cambiado
        }
    }
    return -1;
}
private int seleccionar_tarea_mvr2 (int[] asignaciones) throws ParseException{
    int candidato=-1;
    tarea t;
    Date d = dateFormat.parse("01:00");
    System.out.println(d.getTime());
    for (int i=0;i<asignaciones.length;i++){
        if (asignaciones[i]==-1){
            t=ltareas.obtener_tarea(i+1);
            System.out.println(t.get_nombre());
            System.out.println(i+1);
            System.out.println(t.get_duracion_date().getTime());
            if (t.get_duracion_date().getTime() >d.getTime()){
                d=t.get_duracion_date();
                candidato=i+1;
            }
        }
    }
    return candidato;
}
private int seleccionar_tarea_mvr (int[] asignaciones) throws ParseException{
    int contador=0, contref=1000;
    int tarea_sel = -1;
    for (int ntar=1;ntar<=tareas_creadas;ntar++){
        if (asignaciones[ntar-1]==-1){
            for (int ntrab=1;ntrab<=numero_trabajadores;ntrab++){
                if(asignacion_valida_mvr(ntar,ntrab-1, ltareas_trab, ltareas, matriz_res)){
    //            if (!solapamiento(ntar,ntrab-1)){
                    contador++;
                }
            }
            if (contador < contref){
                tarea_sel = ntar;
                contref = contador;
            }
            contador = 0;
        }
    }
    return tarea_sel;
}
private int seleccionar_tarea_gh (int[] asignaciones){
    int contador=0, contref=-1;
    int tarea_sel = -1;
    for (int i=0;i<tareas_creadas;i++){
        if (asignaciones[i]==-1){
            for (int j=0;j<tareas_creadas;j++){
                if (matriz_res[i][j]){
                    contador++;
                }
            }
            if(contador > contref){
                tarea_sel = i+1;
                contref = contador;
            }
            contador=0;
        }
    }
    return tarea_sel;
}
private boolean x_mas_restrictivo_que_y (int x, int y){
    int nx=0,ny=0;
    for(int i=0;i<tareas_creadas;i++){
        if (matriz_res[x][i]){
            nx++;
        }
        if (matriz_res[y][i]){
            ny++;
        }
    }
    if (nx>=ny){
        return true;
    }else{
        return false;
    }
}
private int seleccionar_tarea_mvr_gh2 (int[] asignaciones, Lista_tareas[] ltareas_trab, Lista_tareas ltareas, boolean[][] matriz_res) throws ParseException{
        int contador=0, contref=-1, contadora=0, contadorb=0;
    int tarea_sel = -1;
        for (int i=0;i<tareas_creadas;i++){
        if (asignaciones[i]==-1){
            for (int j=0;j<tareas_creadas;j++){
                if (matriz_res[i][j]){
                    contador++;
                }
            }
            if(contador > contref){
                tarea_sel = i+1;
                contref = contador;
            }else if (contador == contref){
                for (int ntrab=1;ntrab<=numero_trabajadores;ntrab++){
                    if(asignacion_valida_mvr(tarea_sel,ntrab-1, ltareas_trab, ltareas, matriz_res)){
                        contadora++;
                    }
                    if (asignacion_valida_mvr(i+1,ntrab-1, ltareas_trab, ltareas, matriz_res)){
                        contadorb++;
                    }
                }
                if (contadorb < contadora){
                    tarea_sel = i+1;System.out.println("BINGO");
                    contref = contador;
                }
            }
            contador=0;contadora=0;contadorb=0;
        }
    }
    return tarea_sel;
}
private int seleccionar_tarea_mvr_gh (int[] asignaciones, Lista_tareas[] ltareas_trab, Lista_tareas ltareas, boolean[][] matriz_res) throws ParseException{
        int contador=0, contref=1000;
    int tarea_sel = -1;
    for (int ntar=1;ntar<=tareas_creadas;ntar++){
        if (asignaciones[ntar-1]==-1){
            for (int ntrab=1;ntrab<=numero_trabajadores;ntrab++){
                if(asignacion_valida_mvr(ntar,ntrab-1, ltareas_trab, ltareas, matriz_res)){
                    contador++;
                }
            }
            if (contador < contref){
                tarea_sel = ntar;
                contref = contador;
            }else if (contador == contref){
                if (x_mas_restrictivo_que_y(ntar-1,tarea_sel-1)){
                    tarea_sel = ntar;
                    contref = contador;
                }
            }
            contador = 0;
        }
    }
    return tarea_sel;
}
//private int seleccionar_trabajador(int[] trabajadores){
//    int pos_trabajador=-1;
//    int n_asignaciones=1000;
//    for (int i=0;i<trabajadores.length;i++){
//        if (trabajadores[i] < n_asignaciones){
//            n_asignaciones = trabajadores[i];
//            pos_trabajador=i;
//        }
//    }
//    return pos_trabajador+1;//cambiado
//}
private boolean asignacion_valida_mvr(int ntarea, int ntrab, Lista_tareas[] ltareas_trab, Lista_tareas ltareas, boolean[][] matriz_res) throws ParseException{
    tarea ttrab,tpdt;
    String ini, dur;
    tpdt = ltareas.obtener_tarea(ntarea);
    if (ltareas_trab[ntrab].Vacia()){   //cambio                       //PRIMERA TAREA
        return true;
    }else{
        if (!tpdt.get_hora_inicio().equals("")){    //TAREA CON HORARIO
            ttrab=ltareas_trab[ntrab].primero();
            while (ttrab!=null){
                  if ((tpdt.get_hora_inicio_date().after(ttrab.get_hora_inicio_date()) && tpdt.get_hora_fin_date().after(ttrab.get_hora_fin_date()) &&
                    tpdt.get_hora_inicio_date().before(ttrab.get_hora_fin_date()) && tpdt.get_hora_fin_date().after(ttrab.get_hora_inicio_date()))||
                    (tpdt.get_hora_inicio_date().before(ttrab.get_hora_inicio_date()) && tpdt.get_hora_fin_date().after(ttrab.get_hora_fin_date()))||
                    (tpdt.get_hora_inicio_date().before(ttrab.get_hora_inicio_date()) && tpdt.get_hora_fin_date().before(ttrab.get_hora_fin_date()) &&
                    tpdt.get_hora_inicio_date().before (ttrab.get_hora_fin_date()) && tpdt.get_hora_fin_date().after (ttrab.get_hora_inicio_date())) ||
                    (tpdt.get_hora_inicio_date().equals(ttrab.get_hora_inicio_date()) && tpdt.get_hora_fin_date().equals(ttrab.get_hora_fin_date())) ||
                    (ttrab.get_hora_inicio_date().before(tpdt.get_hora_inicio_date()) && ttrab.get_hora_fin_date().after(tpdt.get_hora_fin_date())) ||
                    tpdt.get_hora_inicio_date().equals(ttrab.get_hora_inicio_date()) && tpdt.get_hora_fin_date().after(ttrab.get_hora_fin_date()) ||

                     tpdt.get_hora_inicio_date().equals(ttrab.get_hora_inicio_date()) && tpdt.get_hora_fin_date().before(ttrab.get_hora_fin_date()) ||
                     tpdt.get_hora_inicio_date().before(ttrab.get_hora_inicio_date()) && tpdt.get_hora_fin_date().equals(ttrab.get_hora_fin_date()) ||

                    tpdt.get_hora_inicio_date().after(ttrab.get_hora_inicio_date()) && tpdt.get_hora_fin_date().equals(ttrab.get_hora_fin_date())){
                    return false;
                }                                                           //FALLO EN 15:45 - 17:30 | 15:45 - 17:45 TB 15:15 - 16:45 | 15:30 - 16:45 |
                  ttrab=ttrab.get_siguiente();
            }
            return true;
        }else{                                      //TAREA SIN HORARIO
            ttrab=ltareas_trab[ntrab].primero();
            if (ttrab!=null){
                if (((ttrab.get_hora_inicio_date().getTime() +3600000) - (laboral_inicio_date.getTime() + 3600000)) >= (tpdt.get_duracion_date().getTime())+3600000){
                        return true;
                }
            }
            while (ttrab.get_siguiente()!=null){
                if (((ttrab.get_siguiente().get_hora_inicio_date().getTime()+3600000) - (ttrab.get_hora_fin_date().getTime()+3600000)) >= (tpdt.get_duracion_date().getTime())+3600000){
                        return true;
                }
                ttrab=ttrab.get_siguiente();
            }
            if (((laboral_fin_date.getTime()+3600000) - (ttrab.get_hora_fin_date().getTime()+3600000)) >= (tpdt.get_duracion_date().getTime()+3600000)){
                    return true;
            }
            return false;
        }
    }
}
private boolean asignacion_valida(int ntarea, int ntrab, Lista_tareas[] ltareas_trab, Lista_tareas ltareas, boolean[][] matriz_res) throws ParseException{
    tarea ttrab,tpdt;
    String ini, dur;
    tpdt = ltareas.obtener_tarea(ntarea);
    if (ltareas_trab[ntrab].Vacia()){   //cambio                       //PRIMERA TAREA
        if (tpdt.get_hora_inicio().equals("")){  //cambiado
//            System.out.println ("he entrat");
//            tpdt.set_hora_inicio_date(dateFormat.parse(laboral_inicio_date.toString()));
            ini = Integer.toString(laboral_inicio_date.getHours()) + ":" + Integer.toString(laboral_inicio_date.getMinutes());
            tpdt.set_hora_inicio_date(dateFormat.parse(ini));
            if (laboral_inicio_date.getHours()+tpdt.get_duracion_date().getHours()==12){
                dur = "11" + ":" + Integer.toString(laboral_inicio_date.getMinutes()+tpdt.get_duracion_date().getMinutes()+60);
            } else{
                dur = Integer.toString(laboral_inicio_date.getHours()+tpdt.get_duracion_date().getHours()) + ":" +
                    Integer.toString(laboral_inicio_date.getMinutes()+tpdt.get_duracion_date().getMinutes());
            }          
            tpdt.set_hora_fin_date(dateFormat.parse(dur));
            comprobar_restricciones(matriz_res, ltareas);
        }
        return true;
    }else{
        if (!tpdt.get_hora_inicio().equals("")){    //TAREA CON HORARIO
            ttrab=ltareas_trab[ntrab].primero();
            while (ttrab!=null){
                  if ((tpdt.get_hora_inicio_date().after(ttrab.get_hora_inicio_date()) && tpdt.get_hora_fin_date().after(ttrab.get_hora_fin_date()) &&
                    tpdt.get_hora_inicio_date().before(ttrab.get_hora_fin_date()) && tpdt.get_hora_fin_date().after(ttrab.get_hora_inicio_date()))||
                    (tpdt.get_hora_inicio_date().before(ttrab.get_hora_inicio_date()) && tpdt.get_hora_fin_date().after(ttrab.get_hora_fin_date()))||
                    (tpdt.get_hora_inicio_date().before(ttrab.get_hora_inicio_date()) && tpdt.get_hora_fin_date().before(ttrab.get_hora_fin_date()) &&
                    tpdt.get_hora_inicio_date().before (ttrab.get_hora_fin_date()) && tpdt.get_hora_fin_date().after (ttrab.get_hora_inicio_date())) ||
                    (tpdt.get_hora_inicio_date().equals(ttrab.get_hora_inicio_date()) && tpdt.get_hora_fin_date().equals(ttrab.get_hora_fin_date())) ||
                    (ttrab.get_hora_inicio_date().before(tpdt.get_hora_inicio_date()) && ttrab.get_hora_fin_date().after(tpdt.get_hora_fin_date())) ||
                    tpdt.get_hora_inicio_date().equals(ttrab.get_hora_inicio_date()) && tpdt.get_hora_fin_date().after(ttrab.get_hora_fin_date()) ||

                     tpdt.get_hora_inicio_date().equals(ttrab.get_hora_inicio_date()) && tpdt.get_hora_fin_date().before(ttrab.get_hora_fin_date()) ||
                     tpdt.get_hora_inicio_date().before(ttrab.get_hora_inicio_date()) && tpdt.get_hora_fin_date().equals(ttrab.get_hora_fin_date()) ||

                    tpdt.get_hora_inicio_date().after(ttrab.get_hora_inicio_date()) && tpdt.get_hora_fin_date().equals(ttrab.get_hora_fin_date())){
                    return false;
                }                                                           //FALLO EN 15:45 - 17:30 | 15:45 - 17:45 TB 15:15 - 16:45 | 15:30 - 16:45 |
                  ttrab=ttrab.get_siguiente();
            }
            return true;
        }else{                                      //TAREA SIN HORARIO
            ttrab=ltareas_trab[ntrab].primero();
            if (ttrab!=null){
                if (((ttrab.get_hora_inicio_date().getTime() +3600000) - (laboral_inicio_date.getTime() + 3600000)) >= (tpdt.get_duracion_date().getTime())+3600000){
                    ini = Integer.toString(laboral_inicio_date.getHours()) + ":" + Integer.toString(laboral_inicio_date.getMinutes());
                    tpdt.set_hora_inicio_date(dateFormat.parse(ini));
                    if (laboral_inicio_date.getHours()+tpdt.get_duracion_date().getHours()==12){
                        dur = "11" + ":" + Integer.toString(laboral_inicio_date.getMinutes()+tpdt.get_duracion_date().getMinutes()+60);
                    } else{
                        dur = Integer.toString(laboral_inicio_date.getHours()+tpdt.get_duracion_date().getHours()) + ":" +
                            Integer.toString(laboral_inicio_date.getMinutes()+tpdt.get_duracion_date().getMinutes());
                    }
                        tpdt.set_hora_fin_date(dateFormat.parse(dur));
                        comprobar_restricciones(matriz_res, ltareas);
                        return true;
                }
            }
            while (ttrab.get_siguiente()!=null){
//                            System.out.println ("he entrat3"); SI CABE ENTRE DOS TAREAS
                if (((ttrab.get_siguiente().get_hora_inicio_date().getTime()+3600000) - (ttrab.get_hora_fin_date().getTime()+3600000)) >= (tpdt.get_duracion_date().getTime())+3600000){
                    ini = Integer.toString(ttrab.get_hora_fin_date().getHours()) + ":" + Integer.toString(ttrab.get_hora_fin_date().getMinutes());
                    tpdt.set_hora_inicio_date(dateFormat.parse(ini));
                    if (ttrab.get_hora_fin_date().getHours()+tpdt.get_duracion_date().getHours()==12){
                        dur = "11" + ":" + Integer.toString(ttrab.get_hora_fin_date().getMinutes()+tpdt.get_duracion_date().getMinutes()+60);
                    } else{
                        dur = Integer.toString(ttrab.get_hora_fin_date().getHours()+tpdt.get_duracion_date().getHours()) + ":" +
                        Integer.toString(ttrab.get_hora_fin_date().getMinutes()+tpdt.get_duracion_date().getMinutes());
                    }
                        tpdt.set_hora_fin_date(dateFormat.parse(dur));
                        comprobar_restricciones(matriz_res, ltareas);
                        return true;  
                }
                ttrab=ttrab.get_siguiente();
            }


            //OJOOOOOOOOO COMPROBAR EL ALGORITMO, DEPUES DEL BUCLE TTRAB ES LA 1ª.
            if (((laboral_fin_date.getTime()+3600000) - (ttrab.get_hora_fin_date().getTime()+3600000)) >= (tpdt.get_duracion_date().getTime()+3600000)){
                    ini = Integer.toString(ttrab.get_hora_fin_date().getHours()) + ":" + Integer.toString(ttrab.get_hora_fin_date().getMinutes());
                    tpdt.set_hora_inicio_date(dateFormat.parse(ini));
                    if (ttrab.get_hora_fin_date().getHours()+tpdt.get_duracion_date().getHours()==12){
                        dur = "11" + ":" + Integer.toString(ttrab.get_hora_fin_date().getMinutes()+tpdt.get_duracion_date().getMinutes()+60);
                    } else{
                        dur = Integer.toString(ttrab.get_hora_fin_date().getHours()+tpdt.get_duracion_date().getHours()) + ":" +
                        Integer.toString(ttrab.get_hora_fin_date().getMinutes()+tpdt.get_duracion_date().getMinutes());
                    }
                    tpdt.set_hora_fin_date(dateFormat.parse(dur));
                    comprobar_restricciones(matriz_res, ltareas);
                    return true;
            }
            return false;
        }
    }
}
private void tarea_horario_auto (int numero_tareas_crear, String rh_ini, String rh_fin,String d_min, String d_max) throws ParseException{
    DateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
    Date rhora_ini = dateFormat2.parse(rh_ini);
        Date rhora_fin = dateFormat2.parse(rh_fin);
        Date rd_min = dateFormat2.parse(d_min);
        Date rd_max = dateFormat2.parse(d_max);
        Long lhora_inih,lhora_inim,lhora_finh,lhora_finm;
        Long lduracion;
        long constante = 3600000;
        String shora_inih,shora_inim, shora_finh,shora_finm;
        if (rhora_ini.before(rhora_fin)){
            for (int j=0; j<numero_tareas_crear; j++){
                String nombre_tarea = "tarea_" + String.valueOf(tareas_creadas+1);
//                System.out.println("rdmax: "+rd_max.getTime()+" rd_min: "+rd_min.getTime());
                lduracion = (long) ((rd_max.getTime() - rd_min.getTime())* Math.random()) + rd_min.getTime()+constante;
//                System.out.println("DURACION: "+lduracion);
                long ldur = lduracion%3600000;
                ldur = ldur%900000;
                if (ldur > 450000){
                    lduracion = lduracion + (900000 - ldur);
                }else{
                    lduracion = lduracion -ldur;
                }
                lhora_inih = (long) ((rhora_fin.getTime() - rhora_ini.getTime()) * Math.random()) + rhora_ini.getTime()+constante;
                long lho = lhora_inih%3600000;
                lho = lho%900000;
                lhora_inih = lhora_inih - lho;
//                System.out.println("duracion: "+(lduracion%3600000)/60000+" horas: "+lhora_inih+" minutos: "+(lhora_inih%3600000)/60000);

                while ((lhora_inih+lduracion)>rhora_fin.getTime()+constante){
                    lhora_inih = (long) ((rhora_fin.getTime() - rhora_ini.getTime()) * Math.random()) + rhora_ini.getTime()+constante;
                    lho = lhora_inih%3600000;
                    lho = lho%900000;
                    lhora_inih = lhora_inih - lho;
                }
                lhora_finh = lhora_inih + lduracion;

                lhora_inim=lhora_inih%3600000;
                lhora_inih = lhora_inih/3600000;
                lhora_inim = lhora_inim/60000;

                lhora_finm = lhora_finh%3600000;
                lhora_finh = lhora_finh/3600000;
                lhora_finm = lhora_finm/60000;
//                System.out.println("duracion: "+lduracion);
//                System.out.println("HORA INI: "+lhora_inih);
//                System.out.println("Min ini: "+lhora_inim);
//                System.out.println("hora fin: "+lhora_finh);
//                System.out.println("Min fin: "+lhora_finm);

                shora_inih = Long.toString(lhora_inih);
                shora_inim = Long.toString(lhora_inim);
                shora_finh = Long.toString(lhora_finh);
                shora_finm = Long.toString(lhora_finm);
                if (Integer.parseInt(shora_inih)<10){
                    shora_inih="0"+shora_inih;
                }
                if (Integer.parseInt(shora_inim)<10){
                    shora_inim="0"+shora_inim;
                }
                if (Integer.parseInt(shora_finh)<10){
                    shora_finh="0"+shora_finh;
                }
                if (Integer.parseInt(shora_finm)<10){
                    shora_finm="0"+shora_finm;
                }
                shora_inih= (shora_inih + ":" + shora_inim);
                shora_finh=(shora_finh + ":"+ shora_finm);
//                System.out.println("Hora inicio: "+shora_inih);
//                System.out.println("Hora fin: "+shora_finh);
                ltareas.insertarnuevo(nombre_tarea, shora_inih, shora_finh);
                tareas_creadas++;
                ntareas=1;
                GestionTareasCreadas(ltareas.primero());
            }
        }
}
private void tarea_libre_auto (int numero_tareas_crear, String d_min, String d_max) throws ParseException{
        Date rd_min = dateFormat.parse(d_min);
        Date rd_max = dateFormat.parse(d_max);
        Long lduracion;
        long constante = 3600000;
        String shora_inih,shora_inim;
        if (rd_min.before(rd_max)){
            for (int j=0; j<numero_tareas_crear; j++){
                String nombre_tarea = "tarea_libre_" + String.valueOf(tareas_creadas+1);
                lduracion = (long) ((rd_max.getTime() - rd_min.getTime())* Math.random()) + rd_min.getTime()+constante;
//                System.out.println("DURACION: "+lduracion);
                long ldur = lduracion%3600000;
                ldur = ldur%900000;
                if (ldur > 450000){
                    lduracion = lduracion + (900000 - ldur);
                }else{
                    lduracion = lduracion -ldur;
                }
                shora_inih = Long.toString(lduracion/3600000);
                shora_inim = Long.toString((lduracion%3600000)/60000);
                if (Integer.parseInt(shora_inih)<10){
                    shora_inih="0"+shora_inih;
                }
                if (Integer.parseInt(shora_inim)<10){
                    shora_inim="0"+shora_inim;
                }
                shora_inih= (shora_inih + ":" + shora_inim);
//                System.out.println("Hora inicio: "+shora_inih);
                ltareas.insertarnuevo(nombre_tarea, shora_inih);
                tareas_creadas++;
                ntareas=1;
                GestionTareasCreadas(ltareas.primero());
            }
        }
//        else System.out.println("NO");
    }
private void limpiar_tabla(){
            for (int j=0;j<TablaTareasAsignadas.getRowCount();j++){
                for (int k=0;k<TablaTareasAsignadas.getColumnCount();k++){
                    TablaTareasAsignadas.setValueAt(null, j, k);
                }
            }
}
private void visualizar_resultadosNO(int[] resultados){
    tarea t;
    limpiar_tabla();
    if (resultados[1]!=-1){
        for (int k=0;k<numero_trabajadores;k++){
            TablaTareasAsignadas.setValueAt(k+1, k, 0);
        }
        for (int i=0; i< resultados.length; i++){
            int j=1;
            while (TablaTareasAsignadas.getValueAt(resultados[i]-1, j)!=null){
                j++;
            }
            t = ltareas.obtener_tarea(i+1);
            TablaTareasAsignadas.setValueAt(t.get_nombre(), resultados[i]-1,j);
    //            System.out.println("La tarea: " + i + " la realiza el trabajador: " + resultado2 [i]);
    //            System.out.println ("hora inicio: " + ltareas.obtener_tarea(i+1).get_hora_inicio());
    //            System.out.println ("hora fin: " + ltareas.obtener_tarea(i+1).get_hora_fin());
        }
        TextoDetalleTarea.setText("");
    }else{
        TextoDetalleTarea.setText("NO EXISTE SOLUCIÓN");
    }
}
//CAMBIAR!!!! PARA QUE SE VEAN LOS RESULTADOS PARCIALES
private void visualizar_resultados(int[] resultados) throws ParseException{
    tarea t;
    limpiar_tabla();
//    if (hay_solucion()){
        if (asignacion_completa (resultados)){
            for (int k=0;k<numero_trabajadores;k++){
                TablaTareasAsignadas.setValueAt(k+1, k, 0);
            }
            for (int i=0; i< numero_trabajadores; i++){
                int j=1;
                ltareas_trab[i].ordenar_tareas();
                t=ltareas_trab[i].primero();
                while (t!=null){
                    TablaTareasAsignadas.setValueAt(t.get_nombre(), i,j);
                    j++;
                    t=t.get_siguiente();
                }
            }
            TextoDetalleTarea.setText("");
//            TextoEstadoSolucion.setForeground(Color.green);
//            TextoEstadoSolucion.setBackground(Color.black);
//            TextoEstadoSolucion.setText("CORRECTA");
       }
        else{
            for (int k=0;k<numero_trabajadores;k++){
                TablaTareasAsignadas.setValueAt(k+1, k, 0);
            }
            int k=0,colum=1;
            for (int tt=1;tt<=numero_trabajadores;tt++){
                while (k<tareas_creadas){
                  if (resultados[k]==tt){
    //                    TablaTareasAsignadas.setValueAt(ltareas.obtener_tarea(k+1).get_nombre(), tt-1,colum);
                        ltareas_trab_parcial[tt-1].insertarnuevoespecial(ltareas.obtener_tarea(k+1).get_nombre(), ltareas.obtener_tarea(k+1).get_hora_inicio(), ltareas.obtener_tarea(k+1).get_hora_fin());
                        colum++;
                  }
                  k++;
                }
                k=0;colum=1;
            }
            for (int i=0; i< numero_trabajadores; i++){
                int j=1;
                ltareas_trab_parcial[i].ordenar_tareas();
                t=ltareas_trab_parcial[i].primero();
                while (t!=null){
                    TablaTareasAsignadas.setValueAt(t.get_nombre(), i,j);
                    j++;
                    t=t.get_siguiente();
                }
            }
//            TextoEstadoSolucion.setBackground(Color.black);
//            TextoEstadoSolucion.setForeground(Color.ORANGE);
//            TextoEstadoSolucion.setText("PARCIAL");
        }
//    }else{
//        TextoEstadoSolucion.setBackground(Color.black);
//        TextoEstadoSolucion.setForeground(Color.red);
//        TextoEstadoSolucion.setText("NO EXISTE");
//        TextoDetalleTarea.setText("NO EXISTE SOLUCIÓN");
//    }
}
private boolean hay_solucion(){
    int conflictos=0;
    for (int i=0;i<tareas_creadas;i++){
        for (int j=0;j<tareas_creadas;j++){
            if (matriz_res[i][j] && i!=j){
                conflictos++;
            }
        }
        if (conflictos > numero_trabajadores){
            return false;
        }else{
            conflictos=0;
        }
    }
    return true;
}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        GrupoAlgoritmos = new javax.swing.ButtonGroup();
        PanelGenerarAleatorio = new javax.swing.JPanel();
        LabelNumeroTareasR = new javax.swing.JLabel();
        LabelHoraInicioR = new javax.swing.JLabel();
        LabelHoraFinR = new javax.swing.JLabel();
        LabelDuracionMinimaR = new javax.swing.JLabel();
        LabelDuracionMaximaR = new javax.swing.JLabel();
        TextoHoraInicioR = new javax.swing.JTextField();
        TextoHoraFinR = new javax.swing.JTextField();
        TextoDuracionMaximaR = new javax.swing.JTextField();
        TextoDuracionMinimaR = new javax.swing.JTextField();
        TextoNumeroTareasR = new javax.swing.JTextField();
        BotonGenerarRandom = new javax.swing.JButton();
        ConHorario = new javax.swing.JRadioButton();
        SinHorario = new javax.swing.JRadioButton();
        LabelRango = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        GrupoTareasR = new javax.swing.ButtonGroup();
        MenuAlgoritmos = new javax.swing.JPanel();
        PanelAlgoritmos = new javax.swing.JPanel();
        PrimeroProf = new javax.swing.JRadioButton();
        PrimeroMVRGradoHeuristico = new javax.swing.JRadioButton();
        PrimeroConsistenciaArco = new javax.swing.JRadioButton();
        PrimeroGradoHeuristico = new javax.swing.JRadioButton();
        PrimeroMVR = new javax.swing.JRadioButton();
        PanelInfoEmpresa = new javax.swing.JPanel();
        TextoHoraInicial = new javax.swing.JTextField();
        TextoHoraFin = new javax.swing.JTextField();
        LabelHoraInicial = new javax.swing.JLabel();
        LabelHoraFinal = new javax.swing.JLabel();
        LabelHorarioLaboral = new javax.swing.JLabel();
        LabelNumeroTrabajadores = new javax.swing.JLabel();
        TextoNumeroTrabajadores = new javax.swing.JTextField();
        BotonEjecutar = new javax.swing.JButton();
        MenuGeneral = new javax.swing.JTabbedPane();
        PanelGestionTareas = new javax.swing.JPanel();
        PanelCreacionTareas = new javax.swing.JPanel();
        LabelHoraInicioTarea = new javax.swing.JLabel();
        LabelHoraFinTarea = new javax.swing.JLabel();
        TextoNombreTarea = new javax.swing.JTextField();
        TextoHoraInicioTarea = new javax.swing.JTextField();
        TextoHoraFinTarea = new javax.swing.JTextField();
        LabelDuracionTarea = new javax.swing.JLabel();
        TextoDuracionTarea = new javax.swing.JTextField();
        TextoCantidadTareas = new javax.swing.JTextField();
        LabelNombreTarea = new javax.swing.JLabel();
        LabelCantidadTareas = new javax.swing.JLabel();
        BotonCrearTarea = new javax.swing.JButton();
        BotonGenerarAleatorio = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        PanelReiniciarTareas = new javax.swing.JPanel();
        LabelReiniciarTareas = new javax.swing.JLabel();
        BotonReiniciarTareas = new javax.swing.JButton();
        PanelVisualizaTareasCreadas = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaTareas = new javax.swing.JTable();
        LabelNumeroTotalTareas = new javax.swing.JLabel();
        TextoNumeroTotalTareas = new javax.swing.JTextField();
        PanelResultados = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TablaTareasAsignadas = new javax.swing.JTable();
        PanelEficienciaComputacional = new javax.swing.JPanel();
        LabelNodosVisitados = new javax.swing.JLabel();
        LabelTiempoEjecucion = new javax.swing.JLabel();
        TextoNodosVisitados = new javax.swing.JTextField();
        TextoTiempoEjecucion = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        TextoDetalleTarea = new javax.swing.JTextArea();

        LabelNumeroTareasR.setText("Nº tareas");

        LabelHoraInicioR.setText("Hora inicio:");

        LabelHoraFinR.setText("Hora fin:");

        LabelDuracionMinimaR.setText("Duración minima:");

        LabelDuracionMaximaR.setText("Duración máxima:");

        TextoHoraInicioR.setText("08:00");
        TextoHoraInicioR.setPreferredSize(new java.awt.Dimension(50, 27));

        TextoHoraFinR.setText("18:00");
        TextoHoraFinR.setPreferredSize(new java.awt.Dimension(50, 27));

        TextoDuracionMaximaR.setText("03:00");
        TextoDuracionMaximaR.setPreferredSize(new java.awt.Dimension(50, 27));

        TextoDuracionMinimaR.setText("01:00");
        TextoDuracionMinimaR.setPreferredSize(new java.awt.Dimension(50, 27));

        TextoNumeroTareasR.setText("1");
        TextoNumeroTareasR.setPreferredSize(new java.awt.Dimension(40, 27));

        BotonGenerarRandom.setText("Generar");
        BotonGenerarRandom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGenerarRandomActionPerformed(evt);
            }
        });

        GrupoTareasR.add(ConHorario);
        ConHorario.setSelected(true);
        ConHorario.setText("Con horario");

        GrupoTareasR.add(SinHorario);
        SinHorario.setText("Sin horario");

        LabelRango.setText("RANGO");

        jButton1.setText("Sortir");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelGenerarAleatorioLayout = new javax.swing.GroupLayout(PanelGenerarAleatorio);
        PanelGenerarAleatorio.setLayout(PanelGenerarAleatorioLayout);
        PanelGenerarAleatorioLayout.setHorizontalGroup(
            PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelGenerarAleatorioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelGenerarAleatorioLayout.createSequentialGroup()
                        .addComponent(LabelNumeroTareasR)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TextoNumeroTareasR, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(211, 211, 211)
                        .addComponent(LabelRango))
                    .addGroup(PanelGenerarAleatorioLayout.createSequentialGroup()
                        .addGroup(PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(PanelGenerarAleatorioLayout.createSequentialGroup()
                                .addGroup(PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ConHorario)
                                    .addComponent(SinHorario))
                                .addGap(52, 52, 52)
                                .addGroup(PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(LabelHoraInicioR)
                                    .addComponent(LabelDuracionMinimaR))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(TextoHoraInicioR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(TextoDuracionMinimaR, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(LabelDuracionMaximaR)
                                    .addComponent(LabelHoraFinR)))
                            .addComponent(jButton1))
                        .addGap(18, 18, 18)
                        .addGroup(PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BotonGenerarRandom)
                            .addGroup(PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(TextoDuracionMaximaR, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(TextoHoraFinR, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelGenerarAleatorioLayout.setVerticalGroup(
            PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelGenerarAleatorioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelNumeroTareasR)
                    .addComponent(TextoNumeroTareasR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelRango))
                .addGap(18, 18, 18)
                .addGroup(PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ConHorario)
                    .addGroup(PanelGenerarAleatorioLayout.createSequentialGroup()
                        .addGroup(PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LabelHoraFinR)
                            .addComponent(TextoHoraFinR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LabelDuracionMaximaR)
                            .addComponent(TextoDuracionMaximaR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(PanelGenerarAleatorioLayout.createSequentialGroup()
                        .addGroup(PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LabelHoraInicioR)
                            .addComponent(TextoHoraInicioR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LabelDuracionMinimaR)
                            .addComponent(TextoDuracionMinimaR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SinHorario))))
                .addGap(18, 18, 18)
                .addGroup(PanelGenerarAleatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonGenerarRandom)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("IA - PSR. UIB 2011/12");

        MenuAlgoritmos.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        PanelAlgoritmos.setBorder(javax.swing.BorderFactory.createTitledBorder("Algoritmos"));

        GrupoAlgoritmos.add(PrimeroProf);
        PrimeroProf.setSelected(true);
        PrimeroProf.setText("1º profundidad vuelta atrás");

        GrupoAlgoritmos.add(PrimeroMVRGradoHeuristico);
        PrimeroMVRGradoHeuristico.setText("1º profundidad + MVR + grado heurístico");

        GrupoAlgoritmos.add(PrimeroConsistenciaArco);
        PrimeroConsistenciaArco.setText("1º profundidad + consistencia arco");

        GrupoAlgoritmos.add(PrimeroGradoHeuristico);
        PrimeroGradoHeuristico.setText("1º profundidad + grado heurístico");

        GrupoAlgoritmos.add(PrimeroMVR);
        PrimeroMVR.setText("1º profundidad + MVR");

        javax.swing.GroupLayout PanelAlgoritmosLayout = new javax.swing.GroupLayout(PanelAlgoritmos);
        PanelAlgoritmos.setLayout(PanelAlgoritmosLayout);
        PanelAlgoritmosLayout.setHorizontalGroup(
            PanelAlgoritmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelAlgoritmosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelAlgoritmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PrimeroMVR)
                    .addComponent(PrimeroGradoHeuristico)
                    .addComponent(PrimeroConsistenciaArco)
                    .addComponent(PrimeroMVRGradoHeuristico)
                    .addComponent(PrimeroProf))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelAlgoritmosLayout.setVerticalGroup(
            PanelAlgoritmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelAlgoritmosLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(PrimeroProf)
                .addGap(18, 18, 18)
                .addComponent(PrimeroMVR)
                .addGap(18, 18, 18)
                .addComponent(PrimeroGradoHeuristico)
                .addGap(18, 18, 18)
                .addComponent(PrimeroConsistenciaArco)
                .addGap(18, 18, 18)
                .addComponent(PrimeroMVRGradoHeuristico)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        PanelInfoEmpresa.setBorder(javax.swing.BorderFactory.createTitledBorder("Información empresa"));

        TextoHoraInicial.setText("08:00");
        TextoHoraInicial.setPreferredSize(new java.awt.Dimension(45, 27));

        TextoHoraFin.setText("18:00");
        TextoHoraFin.setMaximumSize(new java.awt.Dimension(45, 27));
        TextoHoraFin.setMinimumSize(new java.awt.Dimension(45, 27));
        TextoHoraFin.setPreferredSize(new java.awt.Dimension(45, 27));

        LabelHoraInicial.setText("Hora inicio");

        LabelHoraFinal.setText("Hora fin");

        LabelHorarioLaboral.setText("Horario laboral:");

        LabelNumeroTrabajadores.setText("Nº trabajadores:");

        TextoNumeroTrabajadores.setText("5");
        TextoNumeroTrabajadores.setPreferredSize(new java.awt.Dimension(45, 27));

        javax.swing.GroupLayout PanelInfoEmpresaLayout = new javax.swing.GroupLayout(PanelInfoEmpresa);
        PanelInfoEmpresa.setLayout(PanelInfoEmpresaLayout);
        PanelInfoEmpresaLayout.setHorizontalGroup(
            PanelInfoEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelInfoEmpresaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelInfoEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelInfoEmpresaLayout.createSequentialGroup()
                        .addGroup(PanelInfoEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelInfoEmpresaLayout.createSequentialGroup()
                                .addComponent(LabelHoraInicial)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(TextoHoraInicial, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(LabelHoraFinal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(TextoHoraFin, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                            .addGroup(PanelInfoEmpresaLayout.createSequentialGroup()
                                .addComponent(LabelNumeroTrabajadores)
                                .addGap(18, 18, 18)
                                .addComponent(TextoNumeroTrabajadores, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(14, 14, 14))
                    .addGroup(PanelInfoEmpresaLayout.createSequentialGroup()
                        .addComponent(LabelHorarioLaboral)
                        .addContainerGap())))
        );
        PanelInfoEmpresaLayout.setVerticalGroup(
            PanelInfoEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelInfoEmpresaLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(LabelHorarioLaboral)
                .addGap(18, 18, 18)
                .addGroup(PanelInfoEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelHoraInicial)
                    .addComponent(TextoHoraInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TextoHoraFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelHoraFinal))
                .addGap(26, 26, 26)
                .addGroup(PanelInfoEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelNumeroTrabajadores)
                    .addComponent(TextoNumeroTrabajadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        BotonEjecutar.setText("Ejecutar");
        BotonEjecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEjecutarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MenuAlgoritmosLayout = new javax.swing.GroupLayout(MenuAlgoritmos);
        MenuAlgoritmos.setLayout(MenuAlgoritmosLayout);
        MenuAlgoritmosLayout.setHorizontalGroup(
            MenuAlgoritmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuAlgoritmosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MenuAlgoritmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MenuAlgoritmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(PanelInfoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(PanelAlgoritmos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(BotonEjecutar, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MenuAlgoritmosLayout.setVerticalGroup(
            MenuAlgoritmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MenuAlgoritmosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelAlgoritmos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelInfoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BotonEjecutar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        MenuGeneral.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        PanelCreacionTareas.setBorder(javax.swing.BorderFactory.createTitledBorder("Creación tareas"));

        LabelHoraInicioTarea.setText("hora inicio:");

        LabelHoraFinTarea.setText("hora fin:");

        TextoNombreTarea.setPreferredSize(new java.awt.Dimension(300, 27));

        TextoHoraInicioTarea.setText("08:00");
        TextoHoraInicioTarea.setPreferredSize(new java.awt.Dimension(45, 27));

        TextoHoraFinTarea.setText("09:00");

        TextoDuracionTarea.setText("01:00");
        TextoDuracionTarea.setPreferredSize(new java.awt.Dimension(45, 27));
        TextoDuracionTarea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextoDuracionTareaActionPerformed(evt);
            }
        });

        TextoCantidadTareas.setText("1");
        TextoCantidadTareas.setPreferredSize(new java.awt.Dimension(40, 27));

        LabelNombreTarea.setText("Nombre tarea");

        LabelCantidadTareas.setText("Cantidad:");

        BotonCrearTarea.setText("Crear tarea");
        BotonCrearTarea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCrearTareaActionPerformed(evt);
            }
        });

        BotonGenerarAleatorio.setText("Gen. Aleatoria");
        BotonGenerarAleatorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGenerarAleatorioActionPerformed(evt);
            }
        });

        jLabel2.setText("duracion:");

        PanelReiniciarTareas.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        LabelReiniciarTareas.setText("Reiniciar tareas");

        BotonReiniciarTareas.setText("Reiniciar");
        BotonReiniciarTareas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonReiniciarTareasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelReiniciarTareasLayout = new javax.swing.GroupLayout(PanelReiniciarTareas);
        PanelReiniciarTareas.setLayout(PanelReiniciarTareasLayout);
        PanelReiniciarTareasLayout.setHorizontalGroup(
            PanelReiniciarTareasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelReiniciarTareasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelReiniciarTareasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BotonReiniciarTareas)
                    .addComponent(LabelReiniciarTareas))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelReiniciarTareasLayout.setVerticalGroup(
            PanelReiniciarTareasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelReiniciarTareasLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LabelReiniciarTareas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BotonReiniciarTareas)
                .addGap(32, 32, 32))
        );

        javax.swing.GroupLayout PanelCreacionTareasLayout = new javax.swing.GroupLayout(PanelCreacionTareas);
        PanelCreacionTareas.setLayout(PanelCreacionTareasLayout);
        PanelCreacionTareasLayout.setHorizontalGroup(
            PanelCreacionTareasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCreacionTareasLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(LabelNombreTarea)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCreacionTareasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelCreacionTareasLayout.createSequentialGroup()
                        .addComponent(LabelDuracionTarea)
                        .addContainerGap())
                    .addGroup(PanelCreacionTareasLayout.createSequentialGroup()
                        .addGroup(PanelCreacionTareasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelCreacionTareasLayout.createSequentialGroup()
                                .addGroup(PanelCreacionTareasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelCreacionTareasLayout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(18, 18, 18)
                                        .addComponent(TextoDuracionTarea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(PanelCreacionTareasLayout.createSequentialGroup()
                                        .addComponent(LabelHoraInicioTarea)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(TextoHoraInicioTarea, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(LabelHoraFinTarea)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(TextoHoraFinTarea, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(59, 59, 59)
                                .addGroup(PanelCreacionTareasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(BotonGenerarAleatorio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(BotonCrearTarea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(PanelCreacionTareasLayout.createSequentialGroup()
                                .addComponent(TextoNombreTarea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(LabelCantidadTareas)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(TextoCantidadTareas, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                        .addComponent(PanelReiniciarTareas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37))))
        );
        PanelCreacionTareasLayout.setVerticalGroup(
            PanelCreacionTareasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCreacionTareasLayout.createSequentialGroup()
                .addGroup(PanelCreacionTareasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelCreacionTareasLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(PanelCreacionTareasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LabelNombreTarea)
                            .addComponent(TextoNombreTarea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LabelCantidadTareas)
                            .addComponent(TextoCantidadTareas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(PanelCreacionTareasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LabelHoraInicioTarea)
                            .addComponent(TextoHoraInicioTarea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LabelHoraFinTarea)
                            .addComponent(TextoHoraFinTarea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BotonCrearTarea))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(PanelCreacionTareasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(TextoDuracionTarea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BotonGenerarAleatorio))
                        .addGap(1, 1, 1)
                        .addComponent(LabelDuracionTarea))
                    .addGroup(PanelCreacionTareasLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(PanelReiniciarTareas, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        PanelVisualizaTareasCreadas.setBorder(javax.swing.BorderFactory.createTitledBorder("Visualización tareas creadas"));

        TablaTareas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Nombre tarea", "hora inicio", "hora fin", "duración"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(TablaTareas);
        TablaTareas.getColumnModel().getColumn(0).setResizable(false);
        TablaTareas.getColumnModel().getColumn(1).setResizable(false);
        TablaTareas.getColumnModel().getColumn(2).setResizable(false);
        TablaTareas.getColumnModel().getColumn(3).setResizable(false);

        LabelNumeroTotalTareas.setText("Número total tareas:");

        TextoNumeroTotalTareas.setText("0");
        TextoNumeroTotalTareas.setFocusable(false);
        TextoNumeroTotalTareas.setPreferredSize(new java.awt.Dimension(50, 27));

        javax.swing.GroupLayout PanelVisualizaTareasCreadasLayout = new javax.swing.GroupLayout(PanelVisualizaTareasCreadas);
        PanelVisualizaTareasCreadas.setLayout(PanelVisualizaTareasCreadasLayout);
        PanelVisualizaTareasCreadasLayout.setHorizontalGroup(
            PanelVisualizaTareasCreadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelVisualizaTareasCreadasLayout.createSequentialGroup()
                .addGroup(PanelVisualizaTareasCreadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelVisualizaTareasCreadasLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(LabelNumeroTotalTareas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TextoNumeroTotalTareas, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(PanelVisualizaTareasCreadasLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );
        PanelVisualizaTareasCreadasLayout.setVerticalGroup(
            PanelVisualizaTareasCreadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelVisualizaTareasCreadasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelVisualizaTareasCreadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelNumeroTotalTareas)
                    .addComponent(TextoNumeroTotalTareas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout PanelGestionTareasLayout = new javax.swing.GroupLayout(PanelGestionTareas);
        PanelGestionTareas.setLayout(PanelGestionTareasLayout);
        PanelGestionTareasLayout.setHorizontalGroup(
            PanelGestionTareasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelGestionTareasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelGestionTareasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelCreacionTareas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelVisualizaTareasCreadas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        PanelGestionTareasLayout.setVerticalGroup(
            PanelGestionTareasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelGestionTareasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelCreacionTareas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelVisualizaTareasCreadas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(84, Short.MAX_VALUE))
        );

        MenuGeneral.addTab("Gestionar tareas", PanelGestionTareas);

        TablaTareasAsignadas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Trabajador", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"
            }
        ));
        TablaTareasAsignadas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaTareasAsignadasMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(TablaTareasAsignadas);

        PanelEficienciaComputacional.setBorder(javax.swing.BorderFactory.createTitledBorder("Eficiencia computacional"));

        LabelNodosVisitados.setText("Nodos visitados:");

        LabelTiempoEjecucion.setText("Tiempo ejecución:");

        TextoNodosVisitados.setFocusable(false);
        TextoNodosVisitados.setPreferredSize(new java.awt.Dimension(100, 27));

        TextoTiempoEjecucion.setFocusable(false);
        TextoTiempoEjecucion.setPreferredSize(new java.awt.Dimension(100, 27));

        javax.swing.GroupLayout PanelEficienciaComputacionalLayout = new javax.swing.GroupLayout(PanelEficienciaComputacional);
        PanelEficienciaComputacional.setLayout(PanelEficienciaComputacionalLayout);
        PanelEficienciaComputacionalLayout.setHorizontalGroup(
            PanelEficienciaComputacionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelEficienciaComputacionalLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(LabelNodosVisitados)
                .addGap(44, 44, 44)
                .addComponent(TextoNodosVisitados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(43, 43, 43)
                .addComponent(LabelTiempoEjecucion)
                .addGap(18, 18, 18)
                .addComponent(TextoTiempoEjecucion, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64))
        );
        PanelEficienciaComputacionalLayout.setVerticalGroup(
            PanelEficienciaComputacionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelEficienciaComputacionalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelEficienciaComputacionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelEficienciaComputacionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(LabelTiempoEjecucion)
                        .addComponent(TextoTiempoEjecucion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelEficienciaComputacionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(LabelNodosVisitados)
                        .addComponent(TextoNodosVisitados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TextoDetalleTarea.setColumns(20);
        TextoDetalleTarea.setRows(5);
        jScrollPane3.setViewportView(TextoDetalleTarea);

        javax.swing.GroupLayout PanelResultadosLayout = new javax.swing.GroupLayout(PanelResultados);
        PanelResultados.setLayout(PanelResultadosLayout);
        PanelResultadosLayout.setHorizontalGroup(
            PanelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelResultadosLayout.createSequentialGroup()
                .addGroup(PanelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelResultadosLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 824, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(PanelEficienciaComputacional, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        PanelResultadosLayout.setVerticalGroup(
            PanelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(PanelEficienciaComputacional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        MenuGeneral.addTab("Visualizar resultados", PanelResultados);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(MenuAlgoritmos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MenuGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 853, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MenuAlgoritmos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MenuGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BotonCrearTareaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCrearTareaActionPerformed
        if (!TextoNombreTarea.getText().equals("")){
            ntareas = Integer.parseInt(TextoCantidadTareas.getText());
            if (!TextoHoraInicioTarea.getText().equals("") && !TextoHoraFinTarea.getText().equals("")){
                for (int i=0;i<ntareas;i++){
                    try {
                        ltareas.insertarnuevo(TextoNombreTarea.getText(), TextoHoraInicioTarea.getText(), TextoHoraFinTarea.getText());
                    } catch (ParseException ex) {
                        Logger.getLogger(PSR.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }else if (!TextoDuracionTarea.getText().equals("")){
                for (int i=0;i<ntareas;i++){
                    ltareas.insertarnuevo(TextoNombreTarea.getText(), TextoDuracionTarea.getText());
                }
            }
            tareas_creadas = tareas_creadas + ntareas;
            GestionTareasCreadas(ltareas.primero());
            TextoNombreTarea.setText("");
            //TextoHoraInicioTarea.setText("");
            //TextoHoraFinTarea.setText("");
            //TextoDuracionTarea.setText("");
            //TextoCantidadTareas.setText("1");
        }
    }//GEN-LAST:event_BotonCrearTareaActionPerformed

    private void BotonEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEjecutarActionPerformed
        if (!TextoHoraInicial.getText().equals("") && !TextoHoraFin.getText().equals("") && !TextoNumeroTrabajadores.getText().equals("")
                && tareas_creadas >0){
            ltareas.separar_tipo_tareas();
            num_tar2=0;
                laboral_inicio = TextoHoraInicial.getText();
                //DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            try {
                laboral_inicio_date = dateFormat.parse(laboral_inicio);
            } catch (ParseException ex) {
                Logger.getLogger(PSR.class.getName()).log(Level.SEVERE, null, ex);
            }
                laboral_fin = TextoHoraFin.getText();
            try {
                laboral_fin_date = dateFormat.parse(laboral_fin);
            } catch (ParseException ex) {
                Logger.getLogger(PSR.class.getName()).log(Level.SEVERE, null, ex);
            }
                numero_trabajadores = Integer.parseInt(TextoNumeroTrabajadores.getText());
                if (comprobacion_tareas()){
                    asignaciones = new int[tareas_creadas];
                    ltareas_trab = new Lista_tareas[numero_trabajadores];
                    ltareas_trab_parcial = new Lista_tareas[numero_trabajadores];
                    consistencia_arco = new Lista_trabajadores[tareas_creadas];
                    matriz_res = new boolean[tareas_creadas][tareas_creadas];
                                    //PETA CUANDO HAY TAREAS SIN HORARIO!!
                    for (int i = 0; i < numero_trabajadores; i++) {
                        ltareas_trab [i] = new Lista_tareas ();
                        ltareas_trab_parcial[i] = new Lista_tareas();
                    }
                    for (int i = 0; i < tareas_creadas; i++) {
                        consistencia_arco [i] = new Lista_trabajadores ();
                        for (int j=0; j< numero_trabajadores; j++){
                            consistencia_arco [i].insertarnuevo(j+1);
                        }
                    }
                    fallo = new int[tareas_creadas];
                    parcial = new int[tareas_creadas];
                    for (int i = 0; i < tareas_creadas; i++) {
                        asignaciones[i] = -1;
                        fallo[i] = -1;
                        parcial[i] = 0;
                        if (ltareas.obtener_tarea(i+1).get_tipo()==true){
                            ltareas.obtener_tarea(i+1).set_hora_inicio_date(null);
                            ltareas.obtener_tarea(i+1).set_hora_fin_date(null);
                        }
                    }
                    comprobar_restricciones(matriz_res, ltareas);
                    trabajadores = new int[numero_trabajadores];
                    for (int i = 0; i < numero_trabajadores; i++) {
                        trabajadores[i] = 0;
                    }
                    //ltareas.separar_tipo_tareas();//nuevo...
                    if (PrimeroProf.isSelected()) {
                    try {
                        tiempo=System.nanoTime();
                        int[] resultado2 = PrimeroProfundidadVA2(asignaciones, trabajadores, ltareas_trab, ltareas, matriz_res);
                        tiempo2 = System.nanoTime();
                        tiempo3=(tiempo2-tiempo);
                        Double t = tiempo3/Math.pow(10, 9);
                        TextoNodosVisitados.setText(nodos_visitados.toString());
                        nodos_visitados=0;
                        TextoTiempoEjecucion.setText(t.toString());
                        if (!asignacion_completa(resultado2)){
                            visualizar_resultados(parcial);
                        }else{
                            visualizar_resultados(resultado2);
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(PSR.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    } else if (PrimeroMVR.isSelected()) {
                    try {
                        tiempo=System.nanoTime();
                        int[] resultado2 = PrimeroMVR2(asignaciones, trabajadores, ltareas_trab, ltareas, matriz_res, consistencia_arco);
                        tiempo2 = System.nanoTime();
                        tiempo3=(tiempo2-tiempo);
                        Double t = tiempo3/Math.pow(10, 9);
                        TextoNodosVisitados.setText(nodos_visitados.toString());
                        nodos_visitados=0;
                        TextoTiempoEjecucion.setText(t.toString());
                        if (!asignacion_completa(resultado2)){
                            visualizar_resultados(parcial);
                        }else{
                            visualizar_resultados(resultado2);
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(PSR.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    } else if (PrimeroGradoHeuristico.isSelected()) {
                    try {
                        tiempo=System.nanoTime();
                        int[] resultado2 = PrimeroGradoHeuristico2(asignaciones, trabajadores, ltareas_trab, ltareas, matriz_res);
                        tiempo2 = System.nanoTime();
                        tiempo3=(tiempo2-tiempo);
                        Double t = tiempo3/Math.pow(10, 9);
                        TextoNodosVisitados.setText(nodos_visitados.toString());
                        nodos_visitados=0;
                        TextoTiempoEjecucion.setText(t.toString());
                        if (!asignacion_completa(resultado2)){
                            visualizar_resultados(parcial);
                        }else{
                            visualizar_resultados(resultado2);
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(PSR.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    } else if (PrimeroConsistenciaArco.isSelected()) {
                    try {
                        tiempo=System.nanoTime();
                        int[] resultado2 = PrimeroConsistenciaArco(asignaciones, trabajadores, ltareas_trab, ltareas, matriz_res, consistencia_arco);
                        tiempo2 = System.nanoTime();
                        tiempo3=(tiempo2-tiempo);
                        Double t = tiempo3/Math.pow(10, 9);
                        TextoNodosVisitados.setText(nodos_visitados.toString());
                        nodos_visitados=0;
                        TextoTiempoEjecucion.setText(t.toString());
                        if (!asignacion_completa(resultado2)){
                            visualizar_resultados(parcial);
                        }else{
                            visualizar_resultados(resultado2);
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(PSR.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    } else if (PrimeroMVRGradoHeuristico.isSelected()) {
                    try {
                        tiempo=System.nanoTime();
                        int[] resultado2 = PrimeroMVRGradoHeuristico2(asignaciones, trabajadores, ltareas_trab, ltareas, matriz_res, consistencia_arco);
                        tiempo2 = System.nanoTime();
                        tiempo3=(tiempo2-tiempo);
                        Double t = tiempo3/Math.pow(10, 9);
                        TextoNodosVisitados.setText(nodos_visitados.toString());
                        nodos_visitados=0;
                        TextoTiempoEjecucion.setText(t.toString());
                        if (!asignacion_completa(resultado2)){
                            visualizar_resultados(parcial);
                        }else{
                            visualizar_resultados(resultado2);
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(PSR.class.getName()).log(Level.SEVERE, null, ex);
                    }              
                    } 
//                    else if(Todo.isSelected()) {
//                    try {
//                        Todos();
//                    } catch (ParseException ex) {
//                        Logger.getLogger(PSR.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    }
//                    else{
//                    try {
//                        tiempo=System.nanoTime();
//                        int [] resultado2 = Extra(asignaciones, trabajadores, ltareas_trab, ltareas, matriz_res, consistencia_arco);
//                        tiempo2 = System.nanoTime();
//                        tiempo3=(tiempo2-tiempo);
//                        Double t = tiempo3/Math.pow(10, 9);
//                        TextoNodosVisitados.setText(nodos_visitados.toString());
//                        nodos_visitados=0;
//                        TextoTiempoEjecucion.setText(t.toString());
//                        if (!asignacion_completa(resultado2)){
//                            visualizar_resultados(parcial);
//                        }else{
//                            visualizar_resultados(resultado2);
//                        }
//                    } catch (ParseException ex) {
//                        Logger.getLogger(PSR.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    }
                }else{
                    TextoDetalleTarea.setText("¡Horario incompatible con las tareas!");
                    //JOptionPane.showMessageDialog(this, "¡Horario incompatible con las tareas!", "Información", JOptionPane.INFORMATION_MESSAGE);
                }
            }
    }//GEN-LAST:event_BotonEjecutarActionPerformed

    private void BotonReiniciarTareasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonReiniciarTareasActionPerformed
        for (int j=0;j<TablaTareas.getRowCount();j++){
            TablaTareas.setValueAt(null, j,0);
            TablaTareas.setValueAt(null, j,1);
            TablaTareas.setValueAt(null,j,2);
            TablaTareas.setValueAt(null, j,3);
        }
        ltareas = new Lista_tareas();
        tareas_creadas = 0;
        TextoNumeroTotalTareas.setText("0");
    }//GEN-LAST:event_BotonReiniciarTareasActionPerformed

    private void BotonGenerarAleatorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGenerarAleatorioActionPerformed
        //ventana.setSize(725, 250);
        ventana.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        ventana.setLocationRelativeTo(this);
        ventana.add(PanelGenerarAleatorio);
        ventana.pack();
        ventana.setVisible(true);
    }//GEN-LAST:event_BotonGenerarAleatorioActionPerformed

    private void BotonGenerarRandomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGenerarRandomActionPerformed
        if (ConHorario.isSelected()){
            if (!TextoHoraInicioR.getText().equals("") && !TextoHoraFinR.getText().equals("")){
                try {
                    tarea_horario_auto(Integer.parseInt(TextoNumeroTareasR.getText()), TextoHoraInicioR.getText(), TextoHoraFinR.getText(), TextoDuracionMinimaR.getText(), TextoDuracionMaximaR.getText());
                } catch (ParseException ex) {
                    Logger.getLogger(PSR.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }else{
             if (!TextoDuracionMinimaR.getText().equals("") && !TextoDuracionMaximaR.getText().equals("")){
                try {
                    tarea_libre_auto(Integer.parseInt(TextoNumeroTareasR.getText()), TextoDuracionMinimaR.getText(), TextoDuracionMaximaR.getText());
                } catch (ParseException ex) {
                    Logger.getLogger(PSR.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        ventana.setVisible(false);
    }//GEN-LAST:event_BotonGenerarRandomActionPerformed

    private void TablaTareasAsignadasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaTareasAsignadasMouseClicked
        tarea t;
        TextoDetalleTarea.setText("");
        if (TablaTareasAsignadas.getValueAt(TablaTareasAsignadas.getSelectedRow(), 0)!=null){
            t = ltareas_trab[TablaTareasAsignadas.getSelectedRow()].primero();
            while (t!=null){
//                System.out.println(t.get_hora_inicio()+" - "+ t.get_hora_fin());
                TextoDetalleTarea.append(t.get_hora_inicio()+" - "+ t.get_hora_fin()+ " | ");
                t=t.get_siguiente();
            }
        }
    }//GEN-LAST:event_TablaTareasAsignadasMouseClicked

    private void TextoDuracionTareaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TextoDuracionTareaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TextoDuracionTareaActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ventana.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PSR().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BotonCrearTarea;
    private javax.swing.JButton BotonEjecutar;
    private javax.swing.JButton BotonGenerarAleatorio;
    private javax.swing.JButton BotonGenerarRandom;
    private javax.swing.JButton BotonReiniciarTareas;
    private javax.swing.JRadioButton ConHorario;
    private javax.swing.ButtonGroup GrupoAlgoritmos;
    private javax.swing.ButtonGroup GrupoTareasR;
    private javax.swing.JLabel LabelCantidadTareas;
    private javax.swing.JLabel LabelDuracionMaximaR;
    private javax.swing.JLabel LabelDuracionMinimaR;
    private javax.swing.JLabel LabelDuracionTarea;
    private javax.swing.JLabel LabelHoraFinR;
    private javax.swing.JLabel LabelHoraFinTarea;
    private javax.swing.JLabel LabelHoraFinal;
    private javax.swing.JLabel LabelHoraInicial;
    private javax.swing.JLabel LabelHoraInicioR;
    private javax.swing.JLabel LabelHoraInicioTarea;
    private javax.swing.JLabel LabelHorarioLaboral;
    private javax.swing.JLabel LabelNodosVisitados;
    private javax.swing.JLabel LabelNombreTarea;
    private javax.swing.JLabel LabelNumeroTareasR;
    private javax.swing.JLabel LabelNumeroTotalTareas;
    private javax.swing.JLabel LabelNumeroTrabajadores;
    private javax.swing.JLabel LabelRango;
    private javax.swing.JLabel LabelReiniciarTareas;
    private javax.swing.JLabel LabelTiempoEjecucion;
    private javax.swing.JPanel MenuAlgoritmos;
    private javax.swing.JTabbedPane MenuGeneral;
    private javax.swing.JPanel PanelAlgoritmos;
    private javax.swing.JPanel PanelCreacionTareas;
    private javax.swing.JPanel PanelEficienciaComputacional;
    private javax.swing.JPanel PanelGenerarAleatorio;
    private javax.swing.JPanel PanelGestionTareas;
    private javax.swing.JPanel PanelInfoEmpresa;
    private javax.swing.JPanel PanelReiniciarTareas;
    private javax.swing.JPanel PanelResultados;
    private javax.swing.JPanel PanelVisualizaTareasCreadas;
    private javax.swing.JRadioButton PrimeroConsistenciaArco;
    private javax.swing.JRadioButton PrimeroGradoHeuristico;
    private javax.swing.JRadioButton PrimeroMVR;
    private javax.swing.JRadioButton PrimeroMVRGradoHeuristico;
    private javax.swing.JRadioButton PrimeroProf;
    private javax.swing.JRadioButton SinHorario;
    private javax.swing.JTable TablaTareas;
    private javax.swing.JTable TablaTareasAsignadas;
    private javax.swing.JTextField TextoCantidadTareas;
    private javax.swing.JTextArea TextoDetalleTarea;
    private javax.swing.JTextField TextoDuracionMaximaR;
    private javax.swing.JTextField TextoDuracionMinimaR;
    private javax.swing.JTextField TextoDuracionTarea;
    private javax.swing.JTextField TextoHoraFin;
    private javax.swing.JTextField TextoHoraFinR;
    private javax.swing.JTextField TextoHoraFinTarea;
    private javax.swing.JTextField TextoHoraInicial;
    private javax.swing.JTextField TextoHoraInicioR;
    private javax.swing.JTextField TextoHoraInicioTarea;
    private javax.swing.JTextField TextoNodosVisitados;
    private javax.swing.JTextField TextoNombreTarea;
    private javax.swing.JTextField TextoNumeroTareasR;
    private javax.swing.JTextField TextoNumeroTotalTareas;
    private javax.swing.JTextField TextoNumeroTrabajadores;
    private javax.swing.JTextField TextoTiempoEjecucion;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables

}
