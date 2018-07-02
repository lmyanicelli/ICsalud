package com.luciayanicelli.icsalud.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.luciayanicelli.icsalud.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by LuciaYanicelli on 29/6/2017.
 *
 * Clase que permite comunicar la App con la Base de Datos
 */

public class Educacion_PreguntasFrecuentesDBHelper extends SQLiteOpenHelper {

    //Constructor
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Educacion_PreguntasFrecuentes.db";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry.TABLE_NAME + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry.PREGUNTA + " TEXT NOT NULL,"
            + Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry.RESPUESTA + " TEXT NOT NULL"
            + ")";


    private Context context;
    private String linea;

    public Educacion_PreguntasFrecuentesDBHelper(Context context) {
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
         insertarDatos(db);

    }

    //INSERTA DATOS DESDE ARCHIVOS
    private void insertarDatos(SQLiteDatabase sqliteDataBase) {


        try {

                InputStream fraw = context.getResources().openRawResource(R.raw.educacionpreguntasfrecuentes);
                BufferedReader brin = new BufferedReader(new InputStreamReader(fraw));

                 String pregunta;
                 String respuesta ="";
                 String aux;

                 ContentValues values = new ContentValues();


                while ((linea = brin.readLine()) != null) {

                    pregunta = linea;
                    values.put(Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry.PREGUNTA, pregunta);


                    linea = brin.readLine();
                    respuesta = linea;

                    values.put(Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry.RESPUESTA, respuesta);

                    //Si la pregunta y respuesta tienen un valor, crea una fila en la tabla de la BD
                    if(pregunta!= null & respuesta!=null){
                        sqliteDataBase.insert(Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry.TABLE_NAME, null, values);

                    }

                }

            } catch(Exception ex){
                Log.e("Ficheros", "Error al leer fichero desde recurso raw. Línea = " + linea);

            }

    }


    //Este es ejecutado si se identificó que el usuario tiene una versión antigua de la base de datos.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
