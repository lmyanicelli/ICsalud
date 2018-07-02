package com.luciayanicelli.icsalud.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by LuciaYanicelli on 29/6/2017.
 *
 * Clase que permite comunicar la App con la Base de Datos
 */

public class Jugada_DBHelper extends SQLiteOpenHelper {

    //Constructor
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Jugada.db";

    private static final String SQL_CREATE_ENTRIES_JUGADA = "CREATE TABLE " + JuegoContract.JuegoEntry.TABLE_NAME_JUGADA + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL + " INTEGER NOT NULL,"
            + JuegoContract.JuegoEntry.PREGUNTA_ID + " INTEGER NOT NULL,"
            + JuegoContract.JuegoEntry.OPCIONES_ID + " INTEGER NOT NULL,"
            + JuegoContract.JuegoEntry.OPCIONES_PUNTAJE + " INTEGER NOT NULL,"
            + JuegoContract.JuegoEntry.JUGADA_PUNTAJE_ACUMULADO + " INTEGER NOT NULL"
            + ")";



    private Context context;
 //   private String linea;

    public Jugada_DBHelper(Context context) {
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
        db.execSQL(SQL_CREATE_ENTRIES_JUGADA);
    }

    //Este es ejecutado si se identificó que el usuario tiene una versión antigua de la base de datos.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}