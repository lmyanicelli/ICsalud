package com.luciayanicelli.icsalud.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;
import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.Alertas_DBHelper;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_DBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import static java.util.Calendar.DAY_OF_YEAR;

/**
 * Created by LuciaYanicelli on 23/7/2018.
 */

public class Sintomas implements Mediciones {

    private Context mContext;
    private String nameTabla = AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS;
    private String nameDateTabla = AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE;
    private int countDatos = 3;
    private String fechaHsAyer, fechaHsHoy;
    private String fecha_sin_hora_Ayer, fecha_sin_hora_Hoy;

    public Sintomas(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public String getMedicionesCSV() {
        String textoCampos = "";
        String textoEnviar = "";


        Autodiagnostico_DBHelper mDBHelper = new Autodiagnostico_DBHelper(mContext);
        SQLiteDatabase dbPeso = mDBHelper.getWritableDatabase();

        String[] camposDBPeso = new String[]{
                AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE,
                AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_IDPREGUNTA,
                AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_RESPUESTA
        };

        for(int i=0; i<camposDBPeso.length; i++) {
            textoCampos = textoCampos + camposDBPeso[i] + ";";
        }

                /*query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
Query the given URL, returning a Cursor over the result set.*/
        Cursor cursorPeso = dbPeso.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS,
                camposDBPeso,
                null, null, null, null, null, null);

        //Existen datos cargados en la tabla
        if (cursorPeso != null & cursorPeso.moveToFirst()) {
            int count;
            count = cursorPeso.getCount();

            do {

                String textoAuxiliar = "";
                String[] fecha = cursorPeso.getString(0).split(" ");
                String fecha_sin_hora = fecha[0];

                textoAuxiliar = fecha_sin_hora + ";" +
                        cursorPeso.getInt(1)+";"+
                        cursorPeso.getString(2);
                textoEnviar = textoEnviar + "<br/><br/>" + textoAuxiliar;

            } while (cursorPeso.moveToNext());

        }

        return textoCampos + textoEnviar;
    }



    @Override
    public void alertaVerde(String fechaHora) {

        this.fechaHsAyer = fechaHora; //fecha y hora de cuando se buscan los datos si están cargados (fecha de ayer)
        String[] fechaArray = fechaHora.split(" ");
        this.fecha_sin_hora_Ayer = fechaArray[0];

        FechaActual fechaActual = new FechaActual();
        try {
            fechaHsHoy = fechaActual.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        String[] fechaArrayHoy = fechaHsHoy.split(" ");
        this.fecha_sin_hora_Hoy = fechaArrayHoy[0];


        //CORROBORAR QUE NO EXISTAN ALERTAS YA GENERADAS CON ESA FECHA Y TABLA
        Alertas_DBHelper mDBHelper = new Alertas_DBHelper(mContext);
        SQLiteDatabase dbAlertas = mDBHelper.getWritableDatabase();

        String[] camposDBAV = new String[]{AlertasContract.AlertasEntry.DESCRIPCION};
        String selectionAV = AlertasContract.AlertasEntry.TIPO + "= ? "
                //  + "and " + AlertasContract.AlertasEntry.FECHA +"= ? "
                + "and " + AlertasContract.AlertasEntry.FECHA + ">= ?"+ " and " +  AlertasContract.AlertasEntry.FECHA + "<= ?"
                + "and " + AlertasContract.AlertasEntry.PARAMETRO +"= ? ";

        String[] argsAV = new String[] {AlertasContract.AlertasEntry.ALERTA_TIPO_VERDE,
                //   String.valueOf(fecha),
                String.valueOf(fecha_sin_hora_Hoy + " 00:00:00"), String.valueOf(fecha_sin_hora_Hoy + " 23:59:59"),
                nameTabla};

        Cursor busquedaAV = dbAlertas.query(true, AlertasContract.AlertasEntry.TABLE_NAME,
                camposDBAV, selectionAV, argsAV,null,null,null,null);

        //Si existen alertas del tipo verde con ese parámetro y fecha sale, en caso contrario ingresa a buscar alertas
        if (busquedaAV != null & busquedaAV.moveToFirst()) {
            //existen alertas verdes con esa fecha y parámetro
            busquedaAV.getCount();

        }else {

            //No existen alertas verdes con esa fecha y parámetro por lo que busca alertas verdes

            //Conectar con la BD Autodiagnóstico
            Autodiagnostico_DBHelper dbHelper = new Autodiagnostico_DBHelper(mContext);
            SQLiteDatabase db = dbHelper.getWritableDatabase();


            String[] camposDB2 = new String[]{nameDateTabla, BaseColumns._ID};
            String selection = nameDateTabla + ">= ?"+
                    " and " +  nameDateTabla + "<= ?";

            String[] args = new String[] {String.valueOf(fecha_sin_hora_Ayer + " 00:00:00"), String.valueOf(fecha_sin_hora_Ayer + " 23:59:59")}; //busco en el dia completo

            Cursor busqueda = db.query(true, nameTabla,
                    camposDB2, selection, args, null, null, null, null);

            //Si existen datos guardados con la fecha indicada devuelve true
            if (busqueda != null & busqueda.moveToFirst()) {

                //Analiza si existe la cantidad de registros establecida en cantidadDatos con la fecha actual
                if (busqueda.getCount() < countDatos) {
                    //No existe la cantidad de datos indicados guardados con la fecha
                    crearAlertaVerde(nameTabla);
                } //en caso contrario si estarían guardados los datos y no se debería generar ninguna alerta


            } else {
                //No existen datos guardados con la fecha indicada
                crearAlertaVerde(nameTabla);
            }
            busqueda.close();


        }

        busquedaAV.close();

    }


    private void crearAlertaVerde(String nameTabla) {

        boolean amarilla = comprobarAlertaAmarilla(nameTabla);

        if(amarilla){
            //SE DEBE GENERAR UNA ALERTA AMARILLA PORQUE YA PASARON MÁS DE 3 DÍAS SIN CARGAR DATOS
            crearAlertaAmarilla(nameTabla);
        }else{
            //CREAR ALERTA VERDE
            String descripcion = "El día " + fecha_sin_hora_Ayer + " no cargó todos los datos correspondientes del parámetro: " + nameTabla;
            //Guardar el registro de alerta en la BD Alertas
            Alertas_DBHelper mDBHelper = new Alertas_DBHelper(mContext);
            SQLiteDatabase db = mDBHelper.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(AlertasContract.AlertasEntry.FECHA, fechaHsHoy);
            values.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_VERDE);
            values.put(AlertasContract.AlertasEntry.PARAMETRO, nameTabla);
            values.put(AlertasContract.AlertasEntry.DESCRIPCION, descripcion);
            values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);

            db.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);

        }

    }

    private boolean comprobarAlertaAmarilla(String nameTabla) {

        Alertas_DBHelper mDBHelper = new Alertas_DBHelper(mContext);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        //consultar si existen alertas verdes similares en los últimos 3 días consecutivos
        String[] campos = new String[]{AlertasContract.AlertasEntry.FECHA};
        String selection = AlertasContract.AlertasEntry.PARAMETRO + "= ?" + " and "+
                AlertasContract.AlertasEntry.FECHA + "= ?";

        //fecha corresponde al día anterior a la fecha actual porque controla que el día de ayer no se hayan cargado los parámetros
        String fecha2diasAntes;
        Calendar calendar3dias = Calendar.getInstance();
        calendar3dias.add(Calendar.DAY_OF_YEAR, -2);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(JSON_CONSTANTS.DATE_TIME_FORMAT);
        fecha2diasAntes = simpleDateFormat.format(calendar3dias.getTime()).split(" ")[0];


        String[] args = new String[]{nameTabla, fecha2diasAntes};
        Cursor cursorBusqueda = db.query(true,  AlertasContract.AlertasEntry.TABLE_NAME,
                campos, selection, args, null, null, null, null);

        if(cursorBusqueda!= null & cursorBusqueda.moveToFirst()){
            //significa que existe una alerta verde del dia anterior del mismo parámetro
            //buscar si existe una tercera
            Calendar calendar2AntesAyer = Calendar.getInstance();
            calendar2AntesAyer.add(DAY_OF_YEAR, -3);

            //  SimpleDateFormat simpleDateFormat = new SimpleDateFormat(JSON_CONSTANTS.DATE_TIME_FORMAT);
            String fecha3diasAntes = simpleDateFormat.format(calendar2AntesAyer.getTime()).split(" ")[0];


            String[] args2 = new String[]{nameTabla, fecha3diasAntes};
            Cursor cursorBusqueda2 = db.query(true,  AlertasContract.AlertasEntry.TABLE_NAME,
                    campos, selection, args2, null, null, null, null);

            if(cursorBusqueda2!= null & cursorBusqueda2.moveToFirst()){
                //exiten 3 alertas verdes consecutivas al menos en las q el paciente no cargó los datos del parámetro indicado
                // crearAlertaAmarilla();
                return true;
            }
            cursorBusqueda2.close();

        }
        cursorBusqueda.close();

        return false;

    }


    private void crearAlertaAmarilla(String nameTabla) {
        String descripcion = "No se han cargado todos los datos correspondientes del parámetro: " + nameTabla + " en al menos los últimos 3 días. Quizás podría averiguar que sucede con su paciente.";
        //Guardar el registro de alerta en la BD Alertas
        Alertas_DBHelper mDBHelper = new Alertas_DBHelper(mContext);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(AlertasContract.AlertasEntry.FECHA, fechaHsHoy);
        values.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_AMARILLA);
        values.put(AlertasContract.AlertasEntry.PARAMETRO, nameTabla);
        values.put(AlertasContract.AlertasEntry.DESCRIPCION, descripcion);
        values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);

        try {
            db.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
