package com.luciayanicelli.icsalud.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LuciaYanicelli on 29/6/2017.
 *
 * Clase que permite comunicar la App con la Base de Datos
 */

public class Login_DBHelper extends SQLiteOpenHelper {

    //Constructor
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Login.db";

    private static final String SQL_CREATE_ENTRIES_LOGIN = "CREATE TABLE " + LoginContract.LoginEntry.TABLE_NAME_LOGIN + " ("
         //   + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + LoginContract.LoginEntry.EMAIL + " TEXT PRIMARY KEY,"
            + LoginContract.LoginEntry.FIRST_NAME + " TEXT NOT NULL,"
            + LoginContract.LoginEntry.LAST_NAME + " TEXT NOT NULL,"
            + LoginContract.LoginEntry.PASSWORD + " TEXT NOT NULL,"
            + LoginContract.LoginEntry.ID_USER_WEB_SERVICE + " TEXT NOT NULL"
            + ")";


    private Context context;

    public Login_DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    //Métodos obligatorios del Helper

   /* Por defecto el archivo de la base de datos será almacenado en:

            /data/data/<paquete>/databases/<nombre-de-la-bd>.db
    */

    @Override
    public void onCreate(SQLiteDatabase db) {

        if(db.isReadOnly()){
            db = getWritableDatabase();
        }

        //Comandos SQL
        db.execSQL(SQL_CREATE_ENTRIES_LOGIN);

    }

    //Este es ejecutado si se identificó que el usuario tiene una versión antigua de la base de datos.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
