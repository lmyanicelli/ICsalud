package com.luciayanicelli.icsalud.DataBase;

import android.provider.BaseColumns;

/**
 * Created by LuciaYanicelli on 29/6/2017.
 *
 * Determina el "contrato" con la base de datos.
 * La estructura que tendrá la misma. Tabla, columnas etc.
 */

public class JuegoContract {


    //Creamos esta clase abstracta para guardar el nombre de las columnas y de la tabla
    public static abstract class JuegoEntry implements BaseColumns {

        public static final String TABLE_NAME_PREGUNTAS ="juegotablapreguntas";

        public static final String PREGUNTA_ID = "pregunta_id";
        public static final String PREGUNTA_TEXTO = "pregunta_texto";
        public static final String PREGUNTA_ID_NIVEL = "pregunta_id_nivel";



        public static final String TABLE_NAME_OPCIONES ="juegotablaopciones";

        //Clave principal compartida entre PREGUNTA_ID Y OPCIONES_ID

        public static final String OPCIONES_ID = "opciones_id";
        public static final String OPCIONES_TEXTO = "opciones_texto";
        public static final String OPCIONES_PUNTAJE = "opciones_puntaje";


        //JUGADA
        public static final String TABLE_NAME_JUGADA ="TABLA_JUGADA";

        public static final String JUGADA_PUNTAJE_ACUMULADO = "jugada_puntaje_acumulado";


    }
}
