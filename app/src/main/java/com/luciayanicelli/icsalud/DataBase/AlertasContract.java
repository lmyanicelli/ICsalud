package com.luciayanicelli.icsalud.DataBase;

import android.provider.BaseColumns;

/**
 * Created by LuciaYanicelli on 29/6/2017.
 *
 * Determina el "contrato" con la base de datos.
 * La estructura que tendrá la misma. Tabla, columnas etc.
 */

public class AlertasContract {


    //Creamos esta clase abstracta para guardar el nombre de las columnas y de la tabla
    public static abstract class AlertasEntry implements BaseColumns {

        public static final String TABLE_NAME ="alertas";

        public static final String _ID = "_id";
        public static final String FECHA = "fecha";
        public static final String TIPO = "tipo";
        public static final String PARAMETRO = "parametro";
        public static final String DESCRIPCION = "descripcion";
        public static final String ESTADO = "estado";
        public static final String VISIBILIDAD = "visibilidad";


        //TIPO
        public static final String ALERTA_TIPO_VERDE = "ALERTA VERDE";
        public static final String ALERTA_TIPO_AMARILLA = "ALERTA AMARILLA";
        public static final String ALERTA_TIPO_ROJA = "ALERTA ROJA";

        //PARAMETRO
        public static final String ALERTA_PARAMETRO_SOS = "sos";
        public static final String ALERTA_PARAMETRO_MEDICINE = "medicine";

        //ESTADO PARA VER SI LA ALERTA FUE ENVIADA POR MAIL O NO
        public static final String ALERTA_ESTADO_PENDIENTE = "PENDIENTE";
        public static final String ALERTA_ESTADO_ENVIADA = "ENVIADA";

        //VISIBILDIAD
        public static final int ALERTA_VISIBILIDAD_PUBLICA = 10;
        public static final int ALERTA_VISIBILIDAD_PRIVADA = 20;


    }
}
