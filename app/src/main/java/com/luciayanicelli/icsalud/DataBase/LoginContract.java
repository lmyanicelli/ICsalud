package com.luciayanicelli.icsalud.DataBase;

import android.provider.BaseColumns;

/**
 * Created by LuciaYanicelli on 29/6/2017.
 *
 * Determina el "contrato" con la base de datos.
 * La estructura que tendrá la misma. Tabla, columnas etc.
 */

public class LoginContract {


    //Creamos esta clase abstracta para guardar el nombre de las columnas y de la tabla
    public static abstract class LoginEntry implements BaseColumns {

        public static final String TABLE_NAME_LOGIN ="login_tabla";

        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String EMAIL = "email";
        public static final String ID_USER_WEB_SERVICE = "id_user";
        public static final String PASSWORD = "password";



    }
}
