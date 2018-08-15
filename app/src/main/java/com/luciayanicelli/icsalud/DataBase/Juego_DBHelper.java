package com.luciayanicelli.icsalud.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.luciayanicelli.icsalud.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LuciaYanicelli on 29/6/2017.
 *
 * Clase que permite comunicar la App con la Base de Datos
 */

public class Juego_DBHelper extends SQLiteOpenHelper {

    //Constructor
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Juego.db";

    private static final String SQL_CREATE_ENTRIES_PREGUNTAS = "CREATE TABLE " + JuegoContract.JuegoEntry.TABLE_NAME_PREGUNTAS + " ("
         //   + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + JuegoContract.JuegoEntry.PREGUNTA_ID + " INTEGER PRIMARY KEY,"
            + JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL + " INTEGER NOT NULL,"
            + JuegoContract.JuegoEntry.PREGUNTA_TEXTO + " TEXT NOT NULL"
            + ")";




    private static final String SQL_CREATE_ENTRIES_OPCIONES = "CREATE TABLE " + JuegoContract.JuegoEntry.TABLE_NAME_OPCIONES + " ("
       //     + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + JuegoContract.JuegoEntry.PREGUNTA_ID + " INTEGER NOT NULL,"
            + JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL + " INTEGER NOT NULL,"
            + JuegoContract.JuegoEntry.OPCIONES_ID + " INTEGER NOT NULL,"
            + JuegoContract.JuegoEntry.OPCIONES_TEXTO + " TEXT NOT NULL,"
            + JuegoContract.JuegoEntry.OPCIONES_PUNTAJE + " INTEGER NOT NULL,"
            + "PRIMARY KEY " + "(" + JuegoContract.JuegoEntry.PREGUNTA_ID + "," + JuegoContract.JuegoEntry.OPCIONES_ID + ")"
            + ")";


    private Context context;
 //   private String linea;

    public Juego_DBHelper(Context context) {
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
        db.execSQL(SQL_CREATE_ENTRIES_PREGUNTAS);
        db.execSQL(SQL_CREATE_ENTRIES_OPCIONES);

        //        cargarDatosPreguntas(db);
        cargarPreguntas(db);

      //  cargarDatosOpciones(db);
        cargarOpciones(db);

    }


    private void cargarPreguntas(SQLiteDatabase db) {
        String[] texto = leerArchivo(R.raw.juegotablapreguntas);

        for(int i=0; i<texto.length; i++){
            String[] linea = texto[i].split(";"); //cada registro por separado

            ContentValues values = new ContentValues();
            values.put(JuegoContract.JuegoEntry.PREGUNTA_ID, linea[0]);
            values.put(JuegoContract.JuegoEntry.PREGUNTA_TEXTO, linea[1]);
            values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, linea[2]);

            db.insert(JuegoContract.JuegoEntry.TABLE_NAME_PREGUNTAS, null, values);
        }

    }

    private void cargarOpciones(SQLiteDatabase db) {

        String[] texto = leerArchivo(R.raw.juegotablaopciones);

        for(int i=0; i<texto.length; i++){
            String[] linea = texto[i].split(";"); //cada registro por separado

            ContentValues values = new ContentValues();
            values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, linea[0]);
            values.put(JuegoContract.JuegoEntry.PREGUNTA_ID, linea[1]);
            values.put(JuegoContract.JuegoEntry.OPCIONES_ID, linea[2]);
            values.put(JuegoContract.JuegoEntry.OPCIONES_TEXTO, linea[3]);
            values.put(JuegoContract.JuegoEntry.OPCIONES_PUNTAJE, linea[4]);

            db.insert(JuegoContract.JuegoEntry.TABLE_NAME_OPCIONES, null, values);
        }

    }

    private String[] leerArchivo(int id){
        InputStream inputStream = context.getResources().openRawResource(id);
        ///prueba
        // context.getResources().openRawResource(id);
     //   BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        //

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

        return byteArrayOutputStream.toString().split("\n"); //para que este separada por espacio

    }

/*
    public void cargarDatosPreguntas(SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, 1);
        values.put(JuegoContract.JuegoEntry.PREGUNTA_TEXTO, "¿QUÉ ES LA INSUFICIENCIA CARDÍACA?");
        db.insert(JuegoContract.JuegoEntry.TABLE_NAME_PREGUNTAS, null, values);

         values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, 1);
         values.put(JuegoContract.JuegoEntry.PREGUNTA_TEXTO, "¿CUÁLES SON LOS DISTINTOS TIPOS DE INSUFICIENCIA CARDÍACA?");
         long mLong = db.insert(JuegoContract.JuegoEntry.TABLE_NAME_PREGUNTAS, null, values);

         values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, 1);
         values.put(JuegoContract.JuegoEntry.PREGUNTA_TEXTO, "¿CUÁL DE LOS SIGUIENTES SÍNTOMAS CORRESPONDEN A LA INSUFICIENCIA CARDÍACA?");
         db.insert(JuegoContract.JuegoEntry.TABLE_NAME_PREGUNTAS, null, values);


         //  insertarDatos(db);

    }

    public void cargarDatosOpciones(SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID, 1);
        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, 1);
        values.put(JuegoContract.JuegoEntry.OPCIONES_ID, 1);
        values.put(JuegoContract.JuegoEntry.OPCIONES_TEXTO, "Una enfermedad en la que el corazón no bombea la sangre al organismo como debería.");
        values.put(JuegoContract.JuegoEntry.OPCIONES_PUNTAJE, 1);
        db.insert(JuegoContract.JuegoEntry.TABLE_NAME_OPCIONES, null, values);

        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID, 1);
        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, 1);
        values.put(JuegoContract.JuegoEntry.OPCIONES_ID, 2);
        values.put(JuegoContract.JuegoEntry.OPCIONES_TEXTO, "Una enfermedad en la que el corazón late muy deprisa.");
        values.put(JuegoContract.JuegoEntry.OPCIONES_PUNTAJE, 0);
        db.insert(JuegoContract.JuegoEntry.TABLE_NAME_OPCIONES, null, values);

        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID, 1);
        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, 1);
        values.put(JuegoContract.JuegoEntry.OPCIONES_ID, 3);
        values.put(JuegoContract.JuegoEntry.OPCIONES_TEXTO, "Una enfermedad en la que el corazón podría dejar de latir en algún momento.");
        values.put(JuegoContract.JuegoEntry.OPCIONES_PUNTAJE, 0);
        db.insert(JuegoContract.JuegoEntry.TABLE_NAME_OPCIONES, null, values);

        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID, 1);
        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, 1);
        values.put(JuegoContract.JuegoEntry.OPCIONES_ID, 4);
        values.put(JuegoContract.JuegoEntry.OPCIONES_TEXTO, "Una enfermedad en la que el paciente está teniendo un ataque al corazón.");
        values.put(JuegoContract.JuegoEntry.OPCIONES_PUNTAJE, 0);
        long long1 = db.insert(JuegoContract.JuegoEntry.TABLE_NAME_OPCIONES, null, values);


        //PREGUNTA 2
        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID, 2);
        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, 1);
        values.put(JuegoContract.JuegoEntry.OPCIONES_ID, 1);
        values.put(JuegoContract.JuegoEntry.OPCIONES_TEXTO, "Severa y Moderada");
        values.put(JuegoContract.JuegoEntry.OPCIONES_PUNTAJE, 0);
        db.insert(JuegoContract.JuegoEntry.TABLE_NAME_OPCIONES, null, values);

        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID, 2);
        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, 1);
        values.put(JuegoContract.JuegoEntry.OPCIONES_ID, 2);
        values.put(JuegoContract.JuegoEntry.OPCIONES_TEXTO, "Crónica y Aguda");
        values.put(JuegoContract.JuegoEntry.OPCIONES_PUNTAJE, 1);
        db.insert(JuegoContract.JuegoEntry.TABLE_NAME_OPCIONES, null, values);

        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID, 2);
        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, 1);
        values.put(JuegoContract.JuegoEntry.OPCIONES_ID, 3);
        values.put(JuegoContract.JuegoEntry.OPCIONES_TEXTO, "Leve, Moderada y Grave");
        values.put(JuegoContract.JuegoEntry.OPCIONES_PUNTAJE, 0);
        long long2 = db.insert(JuegoContract.JuegoEntry.TABLE_NAME_OPCIONES, null, values);


        //PREGUNTA 3
        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID, 3);
        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, 1);
        values.put(JuegoContract.JuegoEntry.OPCIONES_ID, 1);
        values.put(JuegoContract.JuegoEntry.OPCIONES_TEXTO, "Fiebre");
        values.put(JuegoContract.JuegoEntry.OPCIONES_PUNTAJE, 0);
        db.insert(JuegoContract.JuegoEntry.TABLE_NAME_OPCIONES, null, values);

        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID, 3);
        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, 1);
        values.put(JuegoContract.JuegoEntry.OPCIONES_ID, 2);
        values.put(JuegoContract.JuegoEntry.OPCIONES_TEXTO, "Coloración amarillenta de la piel");
        values.put(JuegoContract.JuegoEntry.OPCIONES_PUNTAJE, 0);
        db.insert(JuegoContract.JuegoEntry.TABLE_NAME_OPCIONES, null, values);

        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID, 3);
        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, 1);
        values.put(JuegoContract.JuegoEntry.OPCIONES_ID, 3);
        values.put(JuegoContract.JuegoEntry.OPCIONES_TEXTO, "Dificultad para respirar mientras se encuentra acostado");
        values.put(JuegoContract.JuegoEntry.OPCIONES_PUNTAJE, 1);
        db.insert(JuegoContract.JuegoEntry.TABLE_NAME_OPCIONES, null, values);

        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID, 3);
        values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, 1);
        values.put(JuegoContract.JuegoEntry.OPCIONES_ID, 4);
        values.put(JuegoContract.JuegoEntry.OPCIONES_TEXTO, "Vómitos con sangre");
        values.put(JuegoContract.JuegoEntry.OPCIONES_PUNTAJE, 0);
        db.insert(JuegoContract.JuegoEntry.TABLE_NAME_OPCIONES, null, values);


        //  insertarDatos(db);

    }

*/




    //INSERTA DATOS DESDE ARCHIVOS
 /*   private void insertarDatos(SQLiteDatabase sqliteDataBase) {


        try {

                InputStream fraw = context.getResources().openRawResource(R.raw.educacionpreguntasfrecuentes);
                BufferedReader brin = new BufferedReader(new InputStreamReader(fraw));

                // reinicio el array

                 int i = 0;

                 String pregunta;
                 String respuesta;

                 ContentValues values = new ContentValues();


                while ((linea = brin.readLine()) != null) {
                    i = i++; //incremento el contador de líneas

                    pregunta = linea;
                    values.put(DBProvider_preguntasfrecuentes.PREGUNTA, pregunta);


                    linea = brin.readLine();
                    respuesta = linea;
                    values.put(DBProvider_preguntasfrecuentes.RESPUESTA, respuesta);

                    //Si la pregunta y respuesta tienen un valor, crea una fila en la tabla de la BD
                    if(pregunta!= null & respuesta!=null){
                        sqliteDataBase.insert(DBProvider_preguntasfrecuentes.TABLE_NAME, null, values);

                        //crearPreguntasRespuestas(sqliteDataBase, new Educacion_PreguntasFrecuentes(i,pregunta,respuesta));
                    }

                }

                i = 0; //reinicio el contador

            } catch(Exception ex){
                Log.e("Ficheros", "Error al leer fichero desde recurso raw. Línea = " + linea);

            }

    }
*/

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
