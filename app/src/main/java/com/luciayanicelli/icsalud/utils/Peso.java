package com.luciayanicelli.icsalud.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.Alertas_DBHelper;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_DBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
 * Created by LuciaYanicelli on 23/7/2018.
 */

public class Peso implements Mediciones {

    private Context mContext;
    private String nameTabla = AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO;
    private String nameDateTabla = AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE;
    private String fechaHsHoy;

    private int cantidadDias;

    public Peso(Context context) {
        this.mContext=context;
        Configuraciones configuraciones = new Configuraciones(mContext);
        this.cantidadDias = configuraciones.getCantidadDiasAlertaAmarilla();
    }

//utilizo esta funcion xa ver si cargó los datos
    public boolean alertaVerde() {

        this.cantidadDias = cantidadDias;

        boolean alerta = false;

        FechaActual fechaActual = new FechaActual();
        try {
            this.fechaHsHoy = fechaActual.execute().get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
        mAlertas.guardar(tipo, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO, descripcion, AlertasContract.AlertasEntry.ALERTA_VISIBILIDAD_PUBLICA);
    }

}
