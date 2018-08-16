package com.luciayanicelli.icsalud.DataBase;

import android.provider.BaseColumns;

import com.luciayanicelli.icsalud.Services.Constants;

/**
 * Created by LuciaYanicelli on 29/6/2017.
 *
 * Determina el "contrato" con la base de datos.
 * La estructura que tendrá la misma. Tabla, columnas etc.
 */

public class RecordatoriosContract {


    //Creamos esta clase abstracta para guardar el nombre de las columnas y de la tabla
    public static abstract class RecordatoriosEntry implements BaseColumns {

        public static final String TABLE_NAME ="recordatorios";

        public static final String _ID = "_id";
        public static final String RECORDATORIO = "recordatorio";
        public static final String TIPO = "tipo"; //consejo saludable o recordatorio acción
        public static final String FECHA_HORA = "fechayhora";
        public static final String FECHA = "fecha";
        public static final String PARAMETRO = Constants.PARAMETRO;
        public static final String ID_NOTIFICACION = "idNotificacion";

        public static final String TIPO_RECORDATORIO = "CONSEJO SALUDABLE";
        public static final String TIPO_ACCION = "RECUERDE MONITOREARSE";
        public static final String TIPO_MEDICAMENTOS= "MEDICAMENTOS";
        public static final String TIPO_ENCUESTAS= "ENCUESTAS";
        public static final String TIPO_SERVICIO_TECNICO= "AYUDA TÉCNICA";

    }
}
