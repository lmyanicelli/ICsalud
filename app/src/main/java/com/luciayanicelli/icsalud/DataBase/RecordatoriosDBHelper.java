package com.luciayanicelli.icsalud.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LuciaYanicelli on 29/6/2017.
 *
 * Clase que permite comunicar la App con la Base de Datos
 */

public class RecordatoriosDBHelper extends SQLiteOpenHelper {

    //Constructor
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Recordatorios.db";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + RecordatoriosContract.RecordatoriosEntry.TABLE_NAME + " ("
            + RecordatoriosContract.RecordatoriosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + RecordatoriosContract.RecordatoriosEntry.RECORDATORIO + " TEXT NOT NULL,"
            + RecordatoriosContract.RecordatoriosEntry.TIPO + " TEXT NOT NULL,"
            + RecordatoriosContract.RecordatoriosEntry.FECHA + " TEXT NOT NULL,"
            + RecordatoriosContract.RecordatoriosEntry.PARAMETRO + " TEXT,"
            + RecordatoriosContract.RecordatoriosEntry.ID_NOTIFICACION + " TEXT,"
            + RecordatoriosContract.RecordatoriosEntry.FECHA_HORA + " TEXT NOT NULL"
            + ")";


    private Context context;
    private String linea;

    public RecordatoriosDBHelper(Context context) {
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

        db.execSQL(SQL_CREATE_ENTRIES);

    }

    //Este es ejecutado si se identificó que el usuario tiene una versión antigua de la base de datos.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
