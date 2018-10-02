package com.luciayanicelli.icsalud.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.Alertas_DBHelper;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_DBHelper;
import com.luciayanicelli.icsalud.Services.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by LuciaYanicelli on 23/7/2018.
 */

public class PAFC implements Mediciones {

    private final Context mContext;
    private String nameTabla = AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA;
    private String nameDateTabla = AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE;
    private String fechaHsHoy;

    private int cantidadDias;

    public PAFC(Context context) {
        this.mContext = context;
        Configuraciones configuraciones = new Configuraciones(mContext);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        int frecuenciaRECORDATORIO = Integer.parseInt(sharedPref.getString(Constants.KEY_PREF_FRECUENCIA_RECORDATORIO_PAFC, Constants.DEFAULT_FRECUENCIA_RECORDATORIO_PAFC));
        this.cantidadDias = configuraciones.getCantidadDiasAlertaAmarilla()*frecuenciaRECORDATORIO;

    }

  /*  @Override
    public String getMedicionesCSV() {
        String textoCampos = "";
        String textoEnviar = "";


        Autodiagnostico_DBHelper mDBHelper = new Autodiagnostico_DBHelper(mContext);
        SQLiteDatabase dbPeso = mDBHelper.getWritableDatabase();

        String[] camposDBPeso = new String[]{
                AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE,
                AutodiagnosticoContract.AutodiagnosticoEntry.PA_PS,
                AutodiagnosticoContract.AutodiagnosticoEntry.PA_PD,
                AutodiagnosticoContract.AutodiagnosticoEntry.PA_FC

        };

        for(int i=0; i<camposDBPeso.length; i++) {
            textoCampos = textoCampos + camposDBPeso[i] + ";";
        }

        Cursor cursorPeso = dbPeso.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA,
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
                        cursorPeso.getInt(2)+";"+
                        cursorPeso.getInt(3);
                textoEnviar = textoEnviar + "<br/><br/>" + textoAuxiliar;

            } while (cursorPeso.moveToNext());

        }

        return textoCampos + textoEnviar;
    }
*/

 /*   @Override
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

        //  boolean amarilla = comprobarAlertaAmarilla(nameTabla);

        boolean amarilla = alertaVerde();

        if(!amarilla){
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
            values.put(AlertasContract.AlertasEntry.VISIBILIDAD, AlertasContract.AlertasEntry.ALERTA_VISIBILIDAD_PUBLICA);

            db.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);

        }

    }
*/

    public boolean alertaVerde() {

        this.cantidadDias = cantidadDias;

        boolean alerta = false;

        Calendar calendarXdiasAntes = Calendar.getInstance();
        calendarXdiasAntes.add(Calendar.DAY_OF_YEAR, -cantidadDias);

        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat(JSON_CONSTANTS.DATE_TIME_FORMAT);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fechaXdiasAntes = simpleDateFormat.format(calendarXdiasAntes.getTime()).split(" ")[0];

        Calendar calendarAyer = Calendar.getInstance();
        calendarAyer.add(Calendar.DAY_OF_YEAR, -1);

        String fechaAyer = simpleDateFormat.format(calendarAyer.getTime()).split(" ")[0];


        //CORROBORAR QUE NO EXISTAN ALERTAS YA GENERADAS DESDE LA FECHA HACE X DIAS EN ADELANTE
        Alertas_DBHelper mDBHelper = new Alertas_DBHelper(mContext);
        SQLiteDatabase dbAlertas = mDBHelper.getWritableDatabase();

        String[] camposDBAV = new String[]{AlertasContract.AlertasEntry.DESCRIPCION};
        String selectionAV = AlertasContract.AlertasEntry.TIPO + "= ? "
                //  + "and " + AlertasContract.AlertasEntry.FECHA +"= ? "
                + "and " + AlertasContract.AlertasEntry.FECHA + ">= ?" //+ " and " +  AlertasContract.AlertasEntry.FECHA + "<= ?"
                + "and " + AlertasContract.AlertasEntry.PARAMETRO +"= ? ";

        String[] argsAV = new String[] {AlertasContract.AlertasEntry.ALERTA_TIPO_VERDE,
                //   String.valueOf(fecha),
                String.valueOf(fechaXdiasAntes + " 00:00:00"), //String.valueOf(fechaXdiasAntes + " 23:59:59"),
                nameTabla};

        Cursor busquedaAV = dbAlertas.query(true, AlertasContract.AlertasEntry.TABLE_NAME,
                camposDBAV, selectionAV, argsAV,null,null,null,null);

        //Si existen alertas del tipo verde con ese parámetro y fecha sale, en caso contrario ingresa a buscar alertas
        if (busquedaAV != null & busquedaAV.moveToFirst()) {
            //existen alertas amarillas con esa fecha y parámetro
            busquedaAV.getCount();

        }else {

            //No existen alertas amarillas con esa fecha y parámetro por lo que busca alertas amarillas

            //Conectar con la BD Autodiagnóstico
            Autodiagnostico_DBHelper dbHelper = new Autodiagnostico_DBHelper(mContext);
            SQLiteDatabase db = dbHelper.getWritableDatabase();


            String[] camposDB2 = new String[]{nameDateTabla, BaseColumns._ID};
            String selection = nameDateTabla + ">= ?"+
                    " and " +  nameDateTabla + "<= ?";

            String[] args = new String[] {String.valueOf(fechaXdiasAntes + " 00:00:00"), String.valueOf(fechaAyer + " 23:59:59")}; //busco en los dìas completos

            Cursor busqueda = db.query(true, nameTabla,
                    camposDB2, selection, args, null, null, null, null);

            //Si existen datos guardados con la fecha indicada devuelve true
            if (busqueda != null & busqueda.moveToFirst()) {

                //Analiza si existe la cantidad de registros establecida en cantidadDatos x la cantidad de dìas
              /*  if (busqueda.getCount() < countDatos*cantidadDias) {
                    //No existe la cantidad de datos indicados guardados con la fecha

                    ///ESTE QUIZÀS ELIMINAR
                    crearAlertaVerde(nameTabla);
                    alerta = true;
                } //en caso contrario si estarían guardados los datos y no se debería generar ninguna alerta
*/
              busqueda.getInt(1);
              int count;
              count = busqueda.getCount();

            } else {
                //No existen datos guardados entre las fechas indicadas
                crearAlertaVerde(nameTabla);
                alerta = true;
            }
            busqueda.close();


        }

        busquedaAV.close();
        return alerta;

    }

    private void crearAlertaVerde(String nameTabla) {
        String descripcion = "No se han cargado todos los datos correspondientes del parámetro: " + nameTabla + " en al menos los últimos " +  cantidadDias  + " días. Quizás podría averiguar que sucede con su paciente.";
        //Guardar el registro de alerta en la BD Alertas
        guardarAlerta(descripcion, AlertasContract.AlertasEntry.ALERTA_TIPO_VERDE);

    }

    public void guardarAlerta(String descripcion, String tipo) {
        Alertas mAlertas = new Alertas(mContext);
        mAlertas.guardar(tipo, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA, descripcion, AlertasContract.AlertasEntry.ALERTA_VISIBILIDAD_PUBLICA);
    }

}
