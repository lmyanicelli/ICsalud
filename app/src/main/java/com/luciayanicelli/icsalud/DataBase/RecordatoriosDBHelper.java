package com.luciayanicelli.icsalud.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.luciayanicelli.icsalud.Services.Constants;

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
        crearRecordatorios(db);

    }

    private void crearRecordatorios(SQLiteDatabase db) {

        //RECORDATORIO ENCUESTAS
        ContentValues values = new ContentValues();
        String descripcionRecordatorio = "Por favor conteste las siguientes encuestas que forman parte del estudio clínico del cual Ud forma parte";
        values.put(RecordatoriosContract.RecordatoriosEntry.TIPO, Constants.PARAMETRO_ENCUESTAS);
        values.put(RecordatoriosContract.RecordatoriosEntry.RECORDATORIO, descripcionRecordatorio);
        values.put(RecordatoriosContract.RecordatoriosEntry.FECHA_HORA, "2018-08-17 17:00:00");
        values.put(RecordatoriosContract.RecordatoriosEntry.FECHA, "2018-08-17");
        values.put(RecordatoriosContract.RecordatoriosEntry.PARAMETRO, Constants.PARAMETRO_ENCUESTAS);

        long controlInsert = db.insert(RecordatoriosContract.RecordatoriosEntry.TABLE_NAME, null, values);


            //RECORDATORIO SERVICIO TECNICO
        ContentValues values2 = new ContentValues();
        String descripcionRecordatorio2 = "¿Tiene dudas sobre el uso de la App? Contacte al servicio técnico";
        values2.put(RecordatoriosContract.RecordatoriosEntry.TIPO, RecordatoriosContract.RecordatoriosEntry.TIPO_SERVICIO_TECNICO);
        values2.put(RecordatoriosContract.RecordatoriosEntry.RECORDATORIO, descripcionRecordatorio2);
        values2.put(RecordatoriosContract.RecordatoriosEntry.FECHA_HORA, "2018-08-17 17:00:10");
        values2.put(RecordatoriosContract.RecordatoriosEntry.FECHA, "2018-08-17");
        values2.put(RecordatoriosContract.RecordatoriosEntry.PARAMETRO, RecordatoriosContract.RecordatoriosEntry.TIPO_SERVICIO_TECNICO);

        long controlInsert2 = db.insert(RecordatoriosContract.RecordatoriosEntry.TABLE_NAME, null, values2);

    }

    //Este es ejecutado si se identificó que el usuario tiene una versión antigua de la base de datos.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
