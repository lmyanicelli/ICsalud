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

public class Autodiagnostico_DBHelper extends SQLiteOpenHelper {

    //Constructor
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Autodiagnostico.db";

    private static final String SQL_CREATE_ENTRIES_PESO = "CREATE TABLE " + AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.PESO_VALOR + " REAL NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE + " TEXT NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO + " TEXT NOT NULL"
            + ")";

    private static final String SQL_CREATE_ENTRIES_PA = "CREATE TABLE " + AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.PA_PS + " INTEGER NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.PA_PD + " INTEGER NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.PA_FC + " INTEGER NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE + " TEXT NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO + " TEXT NOT NULL"
            + ")";

    private static final String SQL_CREATE_ENTRIES_SINTOMAS = "CREATE TABLE " + AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_IDPREGUNTA + " INTEGER NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_IDPREGUNTA_SERVIDOR + " TEXT NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE + " TEXT NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_PREGUNTA + " TEXT NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_RESPUESTA + " TEXT NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO + " TEXT NOT NULL"
            + ")";

    private static final String SQL_CREATE_ENTRIES_CONSEJO_SALUDABLE = "CREATE TABLE " + AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_CONSEJO_SALUDABLE + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.CONSEJO_SALUDABLE_IDPREGUNTA + " INTEGER NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.CONSEJO_SALUDABLE_DATE + " TEXT NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.CONSEJO_SALUDABLE_DESCRIPCION + " TEXT NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO + " TEXT NOT NULL"
            + ")";

/*    private static final String SQL_CREATE_ENTRIES_CONTADORES = "CREATE TABLE " + AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_CONTADORES + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.CONTADOR_CONSEJOS_SALUDABLE_LEIDOS + " INTEGER NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.CONTADOR_FECHA_CONSEJOS_SALUDABLE_LEIDOS + " TEXT NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.CONTADOR_PREGUNTAS_FRECUENTES_LEIDAS + " INTEGER NOT NULL,"
            + AutodiagnosticoContract.AutodiagnosticoEntry.CONTADOR_FECHA_PREGUNTAS_FRECUENTES_LEIDAS + " TEXT NOT NULL"
            + ")";
*/

    private Context context;

    public Autodiagnostico_DBHelper(Context context) {
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
        db.execSQL(SQL_CREATE_ENTRIES_PESO);
        db.execSQL(SQL_CREATE_ENTRIES_PA);
        db.execSQL(SQL_CREATE_ENTRIES_SINTOMAS);
        db.execSQL(SQL_CREATE_ENTRIES_CONSEJO_SALUDABLE);
       // db.execSQL(SQL_CREATE_ENTRIES_CONTADORES);


    }

    //Este es ejecutado si se identificó que el usuario tiene una versión antigua de la base de datos.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
