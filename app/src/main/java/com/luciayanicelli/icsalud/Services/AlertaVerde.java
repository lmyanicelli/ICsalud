package com.luciayanicelli.icsalud.Services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;
import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.Alertas_DBHelper;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_DBHelper;
import com.luciayanicelli.icsalud.utils.PAFC;
import com.luciayanicelli.icsalud.utils.Peso;
import com.luciayanicelli.icsalud.utils.Sintomas;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.util.Calendar.DAY_OF_YEAR;

/**
 * Created by LuciaYanicelli on 11/8/2017.
 *
 * Esta clase corrobora que se encuentren ingresados los datos de la fecha correspondiente, en caso contrario genera una alerta verde
 * Envía un mail al médico indicando que el paciente no cargó los datos correspondientes a la fecha indicada.
 * Guarda estas alertas en una BD
 *
 *
 * PARA UTILIZAR
 * AlertaVerde alertaVerde = new AlertaVerde(String fecha, String nombreDateTabla, String nombreTabla, int cantidadDatos, Context context);
 * alertaVerde.execute();
 */

public class AlertaVerde extends AsyncTask<Void, Void, Void> {

    private String fecha_sin_hora;
    private String fechaHs;
    private String nombreDateTabla;
    private String nombreTabla;
    private int cantidadDatos;
    private Context context;


    private String descripcion;
    private String fecha;


    /*
    @param fecha : String fecha en la que se desea analizar si existen datos cargados
    @param nombreTabla : String Nombre de la tabla donde se quieren analizar si están cargados los datos
    @param cantidadDatos: int cantidad de registros que deben haber en esa fecha
     */

    public AlertaVerde(@NonNull String fechaHora, String nombreDateTabla, String nombreTabla, int cantidadDatos, Context context) {

        this.fechaHs = fechaHora; //corresponde a la fecha de ayer en la que se quiere ver si cargó los datos
        String[] fechaArray = fechaHora.split(" ");
        this.fecha_sin_hora = fechaArray[0];
        this.nombreDateTabla = nombreDateTabla;
        this.nombreTabla = nombreTabla;
        this.cantidadDatos = cantidadDatos;
        this.context = context;
    }


    @Override
    protected void onProgressUpdate(Void... values) {

    }

    @Override
    protected Void doInBackground(Void... strings) {

        Calendar calendarAyer = Calendar.getInstance();
        calendarAyer.add(Calendar.DAY_OF_YEAR, -1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(JSON_CONSTANTS.DATE_TIME_FORMAT);
        //   fecha = simpleDateFormat.format(calendarAyer.getTime()).split(" ")[0];
        //30/05/18
        fecha = simpleDateFormat.format(calendarAyer.getTime());

        Peso mPeso = new Peso(context);
        mPeso.alertaVerde(fecha);

        PAFC mPAFC = new PAFC(context);
        mPAFC.alertaVerde(fecha);

        Sintomas mSintomas = new Sintomas(context);
        mSintomas.alertaVerde(fecha);

        //PRUEBA CON FOR
   /*     boolean isFinish = false;
        for(int i=0; i<3; i++){
            switch (i){

                case 0:
                    if(iniciarAlertaVerde(fecha_sin_hora,
                            AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE,
                            AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO,
                            1)){
                        break;
                    }
                    break;

                case 1:
                    if(iniciarAlertaVerde(fecha_sin_hora,
                            AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE,
                            AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA,
                            1)){
                        break;
                    }
                    break;

                case 2:
                    if(iniciarAlertaVerde(fecha_sin_hora,
                            AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE,
                            AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS,
                            3)){
                        break;
                    }
                    break;

                default:
                        break;

            }

        }

*/


        //TAREA PRINCIPAL
   /*    iniciarAlertaVerde(fecha_sin_hora, nombreDateTabla, nombreTabla, cantidadDatos);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        iniciarAlertaVerde(fecha_sin_hora,
                                AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE,
                                AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA,
                                1);
                    }
                }, 3000);


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        iniciarAlertaVerde(fecha_sin_hora,
                                AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE,
                                AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS,
                                3);
                    }
                }, 5000);


*/

        return null;

    }

    private boolean iniciarAlertaVerde(String fecha_sin_hora, String nameDateTabla, String nameTabla, int countDatos) {

        try {
            //CORROBORAR QUE NO EXISTAN ALERTAS YA GENERADAS CON ESA FECHA Y TABLA
            Alertas_DBHelper mDBHelper = new Alertas_DBHelper(context);
            SQLiteDatabase dbAlertas = mDBHelper.getWritableDatabase();

            String[] camposDBAV = new String[]{AlertasContract.AlertasEntry.DESCRIPCION};
            String selectionAV = AlertasContract.AlertasEntry.TIPO + "= ? "
                    //  + "and " + AlertasContract.AlertasEntry.FECHA +"= ? "
                    + "and " + AlertasContract.AlertasEntry.FECHA + ">= ?"+ " and " +  AlertasContract.AlertasEntry.FECHA + "<= ?"
                    + "and " + AlertasContract.AlertasEntry.PARAMETRO +"= ? ";

            String[] argsAV = new String[] {AlertasContract.AlertasEntry.ALERTA_TIPO_VERDE,
                    //   String.valueOf(fecha),
                    String.valueOf(fecha_sin_hora + " 00:00:00"), String.valueOf(fecha_sin_hora + " 23:59:59"),
                    nameTabla};

            Cursor busquedaAV = dbAlertas.query(true, AlertasContract.AlertasEntry.TABLE_NAME,
                    camposDBAV, selectionAV, argsAV,null,null,null,null);

            //Si existen alertas del tipo verde con ese parámetro y fecha sale, en caso contrario ingresa a buscar alertas
            if (busquedaAV != null & busquedaAV.moveToFirst()) {
                //existen alertas verdes con esa fecha y parámetro
                busquedaAV.getCount();
                return true;

            }else {

                //No existen alertas verdes con esa fecha y parámetro por lo que busca alertas verdes


                //Conectar con la BD Autodiagnóstico
                Autodiagnostico_DBHelper dbHelper = new Autodiagnostico_DBHelper(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();


                String[] camposDB2 = new String[]{nameDateTabla, BaseColumns._ID};
                String selection = nameDateTabla + ">= ?"+
                        " and " +  nameDateTabla + "<= ?";

                String[] args = new String[] {String.valueOf(fecha_sin_hora + " 00:00:00"), String.valueOf(fecha_sin_hora + " 23:59:59")}; //busco en el dia completo

                Cursor busqueda = db.query(true, nameTabla,
                        camposDB2, selection, args, null, null, null, null);

                //Si existen datos guardados con la fecha indicada devuelve true
                if (busqueda != null & busqueda.moveToFirst()) {

                    //Analiza si existe la cantidad de registros establecida en cantidadDatos con la fecha actual
                    if (busqueda.getCount() < countDatos) {
                        //No existe la cantidad de datos indicados guardados con la fecha
                        return crearAlertaVerde(nameTabla);
                    } //en caso contrario si estarían guardados los datos y no se debería generar ninguna alerta



                } else {
                    //No existen datos guardados con la fecha indicada
                    return crearAlertaVerde(nameTabla);
                }
                busqueda.close();



            }

            busquedaAV.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return true;

        }


    }


    private boolean crearAlertaVerde(String nameTabla) {

        boolean amarilla = comprobarAlertaAmarilla(nameTabla);

        if(amarilla){
            //SE DEBE GENERAR UNA ALERTA AMARILLA PORQUE YA PASARON MÁS DE 3 DÍAS SIN CARGAR DATOS
            return crearAlertaAmarilla(nameTabla);
        }else{
            //CREAR ALERTA VERDE
            descripcion = "El día " + fecha_sin_hora + " no cargó todos los datos correspondientes del parámetro: " + nameTabla;
            //Guardar el registro de alerta en la BD Alertas
            Alertas_DBHelper mDBHelper = new Alertas_DBHelper(context);
            SQLiteDatabase db = mDBHelper.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(AlertasContract.AlertasEntry.FECHA, fechaHs);
            values.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_VERDE);
            values.put(AlertasContract.AlertasEntry.PARAMETRO, nameTabla);
            values.put(AlertasContract.AlertasEntry.DESCRIPCION, descripcion);
            values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);

            db.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);

            return true;

        }

    }

    private boolean comprobarAlertaAmarilla(String nameTabla) {

        Alertas_DBHelper mDBHelper = new Alertas_DBHelper(context);
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


    private boolean crearAlertaAmarilla(String nameTabla) {
        descripcion = "No se han cargado todos los datos correspondientes del parámetro: " + nameTabla + " en al menos los últimos 3 días. Quizás podría averiguar que sucede con su paciente.";
        //Guardar el registro de alerta en la BD Alertas
        Alertas_DBHelper mDBHelper = new Alertas_DBHelper(context);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(AlertasContract.AlertasEntry.FECHA, fechaHs);
        values.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_AMARILLA);
        values.put(AlertasContract.AlertasEntry.PARAMETRO, nameTabla);
        values.put(AlertasContract.AlertasEntry.DESCRIPCION, descripcion);
        values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);

        try {
            db.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;

        }

    @Override
    protected void onPreExecute() {
    }


    @Override
    protected void onCancelled() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

}
