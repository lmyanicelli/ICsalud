package com.luciayanicelli.icsalud.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.luciayanicelli.icsalud.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LuciaYanicelli on 29/6/2017.
 *
 * Clase que permite comunicar la App con la Base de Datos
 */

public class Autodiagnostico_SintomasDBHelper extends SQLiteOpenHelper {

    //Constructor
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Autodiagnostico_Sintomas.db";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + Autodiagnostico_SintomasContract.SintomasEntry.TABLE_NAME + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY,"
            + Autodiagnostico_SintomasContract.SintomasEntry.PREGUNTA + " TEXT NOT NULL,"
            + Autodiagnostico_SintomasContract.SintomasEntry.ID_SERVIDOR + " TEXT NOT NULL"
            + ")";

  /*  private static final String SQL_CREATE_ENTRIES_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS " + Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry.TABLE_NAME + " ("
            + Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry.PREGUNTA + " TEXT NOT NULL,"
            + Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry.RESPUESTA + " TEXT NOT NULL"
            + ")";
*/

    private Context context;
    private String linea;

    public Autodiagnostico_SintomasDBHelper(Context context) {
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
        //16/07/17
        db.execSQL(SQL_CREATE_ENTRIES);
      //  db.execSQL(SQL_CREATE_ENTRIES_IF_NOT_EXISTS);


        //insertar datos
    //    insertarDatos(db);


        //20/07
     //   cargarDatos(db);

        //20/12
        cargarPreguntas(db);

    }

    //20/12/17

    private void cargarPreguntas(SQLiteDatabase db) {

        String[] texto = leerArchivo(R.raw.preguntassintomas);

        for(int i=0; i<texto.length; i++){
            String[] linea = texto[i].split(";"); //cada registro por separado

            ContentValues values = new ContentValues();
            values.put(BaseColumns._ID, linea[0]);
            values.put(Autodiagnostico_SintomasContract.SintomasEntry.PREGUNTA, linea[1]);
            values.put(Autodiagnostico_SintomasContract.SintomasEntry.ID_SERVIDOR, linea[2]);

           long correct;
           correct = db.insert(Autodiagnostico_SintomasContract.SintomasEntry.TABLE_NAME, null, values);
        }

    }

    private String[] leerArchivo(int id){
        InputStream inputStream = context.getResources().openRawResource(id);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();


        try {
            int i = inputStream.read();

            while(i!=-1){
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toString().split("\r\n"); //para que este separada por espacio

    }






    //20/07
    //20/07

  //    public void cargarDatos(SQLiteDatabase db) {

   /*     ContentValues values = new ContentValues();
        values.put(DBProvider_preguntasfrecuentes.PREGUNTA, "Pregunta 1");
        values.put(DBProvider_preguntasfrecuentes.RESPUESTA, "Respuesta 1");
        db.insert(DBProvider_preguntasfrecuentes.TABLE_NAME, null, values);

        values.put(DBProvider_preguntasfrecuentes.PREGUNTA, "Pregunta 2");
        values.put(DBProvider_preguntasfrecuentes.RESPUESTA, "Respuesta 2");
        db.insert(DBProvider_preguntasfrecuentes.TABLE_NAME, null, values);


        values.put(DBProvider_preguntasfrecuentes.PREGUNTA, "Pregunta 3");
        values.put(DBProvider_preguntasfrecuentes.RESPUESTA, "Respuesta 3");
        db.insert(DBProvider_preguntasfrecuentes.TABLE_NAME, null, values);

        values.put(DBProvider_preguntasfrecuentes.PREGUNTA, "Pregunta 4");
        values.put(DBProvider_preguntasfrecuentes.RESPUESTA, "Respuesta 4");
        db.insert(DBProvider_preguntasfrecuentes.TABLE_NAME, null, values);

        values.put(DBProvider_preguntasfrecuentes.PREGUNTA, "Pregunta 5");
        values.put(DBProvider_preguntasfrecuentes.RESPUESTA, "Respuesta 5");
       long mLong = db.insert(DBProvider_preguntasfrecuentes.TABLE_NAME, null, values);
*/

        //insertarDatos(db);

  //  }






    //INSERTA DATOS DESDE ARCHIVOS
 /*   private void insertarDatos(SQLiteDatabase sqliteDataBase) {


        try {

                InputStream fraw = context.getResources().openRawResource(R.raw.preguntassintomas);
                BufferedReader brin = new BufferedReader(new InputStreamReader(fraw));

                // reinicio el array

                 int i = 0;

                 String pregunta;
                 String respuesta;

                 ContentValues values = new ContentValues();


                while ((linea = brin.readLine()) != null) {
                    i = i++; //incremento el contador de líneas

                    pregunta = linea;
                    values.put(DBProvider_sintomas.PREGUNTA, pregunta);


           /*         linea = brin.readLine();
                    respuesta = linea;
                    values.put(DBProvider_preguntasfrecuentes.RESPUESTA, respuesta);
*/
                    //Si la pregunta y respuesta tienen un valor, crea una fila en la tabla de la BD
          /*          if(pregunta!= null){
                        sqliteDataBase.insert(DBProvider_sintomas.TABLE_NAME, null, values);

                        //crearPreguntasRespuestas(sqliteDataBase, new Educacion_PreguntasFrecuentes(i,pregunta,respuesta));
                    }

                }

                i = 0; //reinicio el contador

            } catch(Exception ex){
                Log.e("Ficheros", "Error al leer fichero desde recurso raw. Línea = " + linea);

            }

    }


    //no lo uso
  /*  public long crearPreguntasRespuestas (SQLiteDatabase db, Educacion_PreguntasFrecuentes objeto) {

        return db.insert(
                Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry.TABLE_NAME,
                null,
                objeto.toContentValues());
    }
*/

    //Este es ejecutado si se identificó que el usuario tiene una versión antigua de la base de datos.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
