package com.luciayanicelli.icsalud.DataBase;

import android.provider.BaseColumns;

/**
 * Created by LuciaYanicelli on 29/6/2017.
 *
 * Determina el "contrato" con la base de datos.
 * La estructura que tendrá la misma. Tabla, columnas etc.
 */

public class Educacion_PreguntasFrecuentesContract {


    //Creamos esta clase abstracta para guardar el nombre de las columnas y de la tabla
    public static abstract class PreguntasFrecuentesEntry implements BaseColumns {

        public static final String TABLE_NAME ="preguntasFrecuentes";

        public static final String _ID = "_id";
        public static final String PREGUNTA = "pregunta";
        public static final String RESPUESTA = "respuesta";

    }
}
