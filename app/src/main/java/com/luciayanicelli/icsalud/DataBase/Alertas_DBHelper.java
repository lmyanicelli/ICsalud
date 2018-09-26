package com.luciayanicelli.icsalud.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Clase que permite comunicar la App con la Base de Datos
 */

public class Alertas_DBHelper extends SQLiteOpenHelper {

    //Constructor
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Alertas.db";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + AlertasContract.AlertasEntry.TABLE_NAME + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + AlertasContract.AlertasEntry.FECHA + " TEXT NOT NULL,"
            + AlertasContract.AlertasEntry.TIPO + " TEXT NOT NULL,"
            + AlertasContract.AlertasEntry.PARAMETRO + " TEXT NOT NULL,"
            + AlertasContract.AlertasEntry.DESCRIPCION + " TEXT NOT NULL,"
            + AlertasContract.AlertasEntry.VISIBILIDAD + " INTEGER,"
            + AlertasContract.AlertasEntry.ESTADO + " TEXT NOT NULL"
            + ")";


    private Context context;

    public Alertas_DBHelper(Context context) {
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
        db.execSQL(SQL_CREATE_ENTRIES);

    }

    //Este es ejecutado si se identificó que el usuario tiene una versión antigua de la base de datos.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
