package com.luciayanicelli.icsalud.DataBase;

import android.provider.BaseColumns;

/**
 * Created by LuciaYanicelli on 29/6/2017.
 *
 * Determina el "contrato" con la base de datos.
 * La estructura que tendrá la misma. Tabla, columnas etc.
 */

public class Autodiagnostico_SintomasContract {


    //Creamos esta clase abstracta para guardar el nombre de las columnas y de la tabla
    public static abstract class SintomasEntry implements BaseColumns {

        public static final String TABLE_NAME ="sintomas";

        public static final String ID_SERVIDOR = "id_servidor";
        public static final String PREGUNTA = "pregunta";

    }
}
