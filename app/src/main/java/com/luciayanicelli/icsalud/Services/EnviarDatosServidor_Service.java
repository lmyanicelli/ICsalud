package com.luciayanicelli.icsalud.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;
import com.luciayanicelli.icsalud.Api_Json.JSON_functions;
import com.luciayanicelli.icsalud.Api_Json.Post_Alert;
import com.luciayanicelli.icsalud.Api_Json.Post_Answer;
import com.luciayanicelli.icsalud.Api_Json.Post_BloodPressure;
import com.luciayanicelli.icsalud.Api_Json.Post_HeartRate;
import com.luciayanicelli.icsalud.Api_Json.Post_Weight;
import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.Alertas_DBHelper;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_DBHelper;
import com.luciayanicelli.icsalud.Notifications.MyReceiverEnviarDatosServidor;
import com.luciayanicelli.icsalud.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */

/*
PARA UTILIZAR EL SERVICE QUE ENVIA LAS MEDICIONES AL SERVIDOR - CORROBORA CONEXIÓN A INTERNET Y ENVIA LOS DATOS CORRESPONDIENTES

//Sevice Enviar Datos Servidor - PESO
        Intent intentPESO = new Intent(getApplicationContext(), EnviarDatosServidor_Service.class);
        intentPESO.setAction(JSON_CONSTANTS.WEIGHTS);
        startService(intentPESO);

//Sevice Generar Email - ENVIAR MEDICIONES
        Intent intentMEDICIONES = new Intent(getApplicationContext(), ServiceGenerarEmail2.class);
        intentMEDICIONES.setAction(Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE_MEDICIONES);
        startService(intentMEDICIONES);

 */
public class EnviarDatosServidor_Service extends IntentService {
       ///VARIABLES
    private AlarmManager alarmManagerSinConexion, alarmManagerSinConexion30;
    private PendingIntent pendingIntentSinConexion, pendingIntentSinConexion30;

    private int contador = 0;

    public EnviarDatosServidor_Service() {

        super("EnviarDatosServidor_Service");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
              if (intent != null) {

                     //29/05/18
                  ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());
              //29/05  if(conectadointernet()){
                  try {
                      if(conexionInternet.execute().get()){

                          try {
                            handleActionMediciones();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }else{

                        contador = contador ++;
                        if(contador <= 1){
                            //Volver a intentar la conexión a los 30 segundos
                            Intent myIntent30 = new Intent(getApplicationContext(), EnviarDatosServidor_Service.class);

                            pendingIntentSinConexion30 = PendingIntent.getService(getApplicationContext(), 0, myIntent30,0);

                            alarmManagerSinConexion30 = (AlarmManager)getApplicationContext().getSystemService(ALARM_SERVICE);

                          //alarma en 30 segundos
                            alarmManagerSinConexion30.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 30 * 1000, pendingIntentSinConexion30);

                        } else {
                            contador = 0;
                        }


                        //VER DE PONER UN BUCLE PARA QUE VUELVA A INTENTAR CADA UNA HORA

                        Intent myIntentEAlertas = new Intent(getApplicationContext(), MyReceiverEnviarDatosServidor.class);
                     //   myIntentEAlertas.setAction(action);

                        pendingIntentSinConexion = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntentEAlertas,0);

                        alarmManagerSinConexion = (AlarmManager)getApplicationContext().getSystemService(ALARM_SERVICE);

                        Calendar calendar = Calendar.getInstance();

                        alarmManagerSinConexion.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, pendingIntentSinConexion);

                    }
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  } catch (ExecutionException e) {
                      e.printStackTrace();
                  }

        }
    }


    //ENVIA LAS MEDICIONES PENDIENTES AL SERVIDOR
    private void handleActionMediciones() throws ExecutionException, InterruptedException {
        buscarDatosServidorPeso();
    }


    private void buscarDatosServidorPeso() throws ExecutionException, InterruptedException {

        //Buscar fecha de último parámetro ingresado
        JSON_functions jsonFunctions = new JSON_functions(getApplicationContext());
        String lastDate = jsonFunctions.getLastDate(JSON_CONSTANTS.WEIGHTS);

        //BUSCA SI HAY REGISTROS CON FECHA POSTERIOR A LASTDATE EN LA TABLA PESO

        String[] camposDBPESO = new String[]{BaseColumns._ID,
                // AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO,
                AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE,
                AutodiagnosticoContract.AutodiagnosticoEntry.PESO_VALOR};

        String selection = AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE + "> ?";

        String args[] = new String[]{lastDate};

        Autodiagnostico_DBHelper autodiagnosticoDbHelper = new Autodiagnostico_DBHelper(getApplicationContext());
        SQLiteDatabase dbMediciones = autodiagnosticoDbHelper.getWritableDatabase();

                /*query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
Query the given URL, returning a Cursor over the result set.*/
        Cursor cursorPESO = dbMediciones.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO, camposDBPESO,
                selection, args, null, null, null, null);

        //Existen MEDICIONES DE LA TABLA PESO CON FECHA POSTERIOR A LASTDATE
        if (cursorPESO != null & cursorPESO.moveToFirst()) {
            int countPESO;
            countPESO = cursorPESO.getCount();

            String kg, date_time;

            do {

                date_time = cursorPESO.getString(1);
                kg = String.valueOf(cursorPESO.getDouble(2));
                postWeight(kg, date_time);

            } while (cursorPESO.moveToNext());
        }

        buscarDatosServidorPA();
    }

    private void buscarDatosServidorPA() throws ExecutionException, InterruptedException {
        //Buscar fecha de último parámetro ingresado
        JSON_functions jsonFunctions = new JSON_functions(getApplicationContext());
        String lastDate = jsonFunctions.getLastDate(JSON_CONSTANTS.BLOOD_PRESSURES);

        //BUSCA SI HAY REGISTROS CON FECHA POSTERIOR A LASTDATE EN LA TABLA PA
        String[] camposDBPA = new String[]{BaseColumns._ID, //0
                //    AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO,
                AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE, //1
                AutodiagnosticoContract.AutodiagnosticoEntry.PA_PS, //2
                AutodiagnosticoContract.AutodiagnosticoEntry.PA_PD, //3
                //  AutodiagnosticoContract.AutodiagnosticoEntry.PA_FC,
        };

        String selection = AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE + "> ?";

        String args[] = new String[]{lastDate};

        Autodiagnostico_DBHelper autodiagnosticoDbHelper = new Autodiagnostico_DBHelper(getApplicationContext());
        SQLiteDatabase dbMediciones = autodiagnosticoDbHelper.getWritableDatabase();

                /*query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
Query the given URL, returning a Cursor over the result set.*/
        Cursor cursorPESO = dbMediciones.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA, camposDBPA,
                selection, args, null, null, null, null);

        //Existen MEDICIONES DE LA TABLA PA CON FECHA POSTERIOR A LASTDATE
        if (cursorPESO != null & cursorPESO.moveToFirst()) {
            int countPESO;
            countPESO = cursorPESO.getCount();

            String date_time, mmHg, type, shift;
                /*
                const TYPE_SYSTOLIC  = 0;
                const TYPE_DIASTOLIC = 1;
                const SHIFT_MORNING  = 0;
                const SHIFT_EVENING  = 1;
                 */

            do {

                date_time = cursorPESO.getString(1);
                mmHg = String.valueOf(cursorPESO.getInt(2)); //PS
                type = String.valueOf(JSON_CONSTANTS.TYPE_SYSTOLIC);
                shift = String.valueOf(JSON_CONSTANTS.SHIFT_MORNING);

                postBloodPressure(date_time, mmHg, type, shift);

                //delay de un 1 segundo y cargo PD

                date_time = cursorPESO.getString(1);
                mmHg = String.valueOf(cursorPESO.getInt(3)); //PD
                type = String.valueOf(JSON_CONSTANTS.TYPE_DIASTOLIC);
                shift = String.valueOf(JSON_CONSTANTS.SHIFT_MORNING);

                postBloodPressure(date_time, mmHg, type, shift);



            } while (cursorPESO.moveToNext());
        }

        buscarDatosServidorHR();
    }

    private void buscarDatosServidorHR() throws ExecutionException, InterruptedException {
        //Buscar fecha de último parámetro ingresado
        JSON_functions jsonFunctions = new JSON_functions(getApplicationContext());
        String lastDate = jsonFunctions.getLastDate(JSON_CONSTANTS.HEART_RATES);

        //BUSCA SI HAY REGISTROS CON FECHA POSTERIOR A LASTDATE EN LA TABLA PA
        String[] camposDBPA = new String[]{BaseColumns._ID, //0
                //    AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO,
                AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE, //1
                //  AutodiagnosticoContract.AutodiagnosticoEntry.PA_PS,
                //  AutodiagnosticoContract.AutodiagnosticoEntry.PA_PD,
                AutodiagnosticoContract.AutodiagnosticoEntry.PA_FC,//2
        };

        String selection = AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE + "> ?";

        String args[] = new String[]{lastDate};

        Autodiagnostico_DBHelper autodiagnosticoDbHelper = new Autodiagnostico_DBHelper(getApplicationContext());
        SQLiteDatabase dbMediciones = autodiagnosticoDbHelper.getWritableDatabase();

                /*query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
Query the given URL, returning a Cursor over the result set.*/
        Cursor cursorPESO = dbMediciones.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA, camposDBPA,
                selection, args, null, null, null, null);

        //Existen MEDICIONES DE LA TABLA PA CON FECHA POSTERIOR A LASTDATE
        if (cursorPESO != null & cursorPESO.moveToFirst()) {
            int countPESO;
            countPESO = cursorPESO.getCount();

            String date_time, ppm;

            do {

                date_time = cursorPESO.getString(1);
                ppm = String.valueOf(cursorPESO.getInt(2)); //FC

                postHeartRate(date_time, ppm);

            } while (cursorPESO.moveToNext());
        }

        buscarDatosServidorSINTOMAS();
    }

    private void buscarDatosServidorSINTOMAS() throws ExecutionException, InterruptedException {
        //Buscar fecha de último parámetro ingresado
        JSON_functions jsonFunctions = new JSON_functions(getApplicationContext());
        String lastDate = jsonFunctions.getLastDate(JSON_CONSTANTS.ANSWERS);

        //BUSCA SI HAY REGISTROS CON FECHA POSTERIOR A LASTDATE EN LA TABLA ANSWERS
        String[] camposDBSINTOMAS = new String[]{BaseColumns._ID,
                //  AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO,
                AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE,
                AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_IDPREGUNTA_SERVIDOR,
                AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_PREGUNTA,
                AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_RESPUESTA
        };

        String selection = AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE + "> ?";

        String args[] = new String[]{lastDate};

        Autodiagnostico_DBHelper autodiagnosticoDbHelper = new Autodiagnostico_DBHelper(getApplicationContext());
        SQLiteDatabase dbMediciones = autodiagnosticoDbHelper.getWritableDatabase();

                /*query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
Query the given URL, returning a Cursor over the result set.*/
        Cursor cursorPESO = dbMediciones.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS, camposDBSINTOMAS,
                selection, args, null, null, null, null);

        //Existen MEDICIONES DE LA TABLA ANSWERS CON FECHA POSTERIOR A LASTDATE
        if (cursorPESO != null & cursorPESO.moveToFirst()) {
            int countPESO;
            countPESO = cursorPESO.getCount();

            String date_time, questionId, rate;

            do {

                date_time = cursorPESO.getString(1);
                questionId = String.valueOf(cursorPESO.getString(2)); //id pregunta servidor
                String respuesta = cursorPESO.getString(4); //respuesta

                rate = obtenerRateRespuesta(respuesta);


                postAnswer(date_time, questionId, rate);

            } while (cursorPESO.moveToNext());
        }

        buscarDatosServidorAlertas();

    }

    private void buscarDatosServidorAlertas() throws ExecutionException, InterruptedException {
            //Buscar fecha de último registro ingresado
            JSON_functions jsonFunctions = new JSON_functions(getApplicationContext());
            String lastDate = jsonFunctions.getAlertsLastDate();

            //BUSCA SI HAY REGISTROS CON FECHA POSTERIOR A LASTDATE EN LA TABLA ALERTAS
            String[] camposDBALERTAS = new String[]{BaseColumns._ID,
                    AlertasContract.AlertasEntry.FECHA,
                    AlertasContract.AlertasEntry.DESCRIPCION,
                    AlertasContract.AlertasEntry.TIPO, //level
                    AlertasContract.AlertasEntry.PARAMETRO, //type
                    AlertasContract.AlertasEntry.VISIBILIDAD, //visibility
            };

            String selection = AlertasContract.AlertasEntry.FECHA + "> ?";

            String args[] = new String[]{lastDate};

            Alertas_DBHelper alertasDbHelper = new Alertas_DBHelper(getApplicationContext());
            SQLiteDatabase dbAlertas = alertasDbHelper.getWritableDatabase();

                /*query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
Query the given URL, returning a Cursor over the result set.*/
            Cursor cursorAlertas = dbAlertas.query(true, AlertasContract.AlertasEntry.TABLE_NAME, camposDBALERTAS,
                    selection, args, null, null, null, null);

            //Existen ALERTAS DE LA TABLA ALERTAS CON FECHA POSTERIOR A LASTDATE
            if (cursorAlertas != null & cursorAlertas.moveToFirst()) {
                int count;
                count = cursorAlertas.getCount();

                String date_time, description, level, type;
                int visibility;

                do {

                    date_time = cursorAlertas.getString(1);
                    description = cursorAlertas.getString(2);
                    level = cursorAlertas.getString(3); //tipo verde amarilla roja
                    type = cursorAlertas.getString(4); //parametro peso, pa, sintomas, sos
                    visibility = cursorAlertas.getInt(5); //visibilidad pública o privada

                    postAlert(date_time, description, level,type, visibility);

                } while (cursorAlertas.moveToNext());
            }

        cursorAlertas.close();

        //Eliminar registros anteriores a lastDate que ya fueron subidos al servidor
        String[] camposALERTAS = new String[]{BaseColumns._ID,
                AlertasContract.AlertasEntry.FECHA
        };

        String selectionALERTAS = AlertasContract.AlertasEntry.FECHA + "< ?";

        //   String argsALERTAS[] = new String[]{lastDate}; //debereìa ser lastDate - cantidadDiasAlertaAmarilla

        String fechaEliminar = lastDate;

        String dtStart = lastDate;
        SimpleDateFormat format = new SimpleDateFormat(JSON_CONSTANTS.DATE_TIME_FORMAT);
        try {
            Date date = format.parse(dtStart);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            Configuraciones configuraciones = new Configuraciones(getApplicationContext());
            int cantidadDias = configuraciones.getCantidadDiasAlertaAmarilla();
            //le resto los dìas
            cal.add(Calendar.DAY_OF_YEAR, -cantidadDias);


            fechaEliminar = format.format(cal.getTime());



            // System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String argsALERTAS[] = new String[]{fechaEliminar}; //debereìa ser lastDate - cantidadDiasAlertaAmarilla

        Cursor cursorAlertasEliminar = dbAlertas.query(true, AlertasContract.AlertasEntry.TABLE_NAME, camposALERTAS,
                selectionALERTAS, argsALERTAS, null, null, null, null);

        if (cursorAlertasEliminar != null & cursorAlertasEliminar.moveToFirst()) {
            cursorAlertasEliminar.getCount();

            long result = dbAlertas.delete(AlertasContract.AlertasEntry.TABLE_NAME,
                    selectionALERTAS,
                    argsALERTAS);

            if(result!=-1){
                boolean ok = true;
            }

        }


        cursorAlertasEliminar.close();
        dbAlertas.close();

    }



    private String obtenerRateRespuesta(String respuesta) {
//API ACEPTA RATE DE 0 EN ADELANTE
        if(respuesta.equalsIgnoreCase(getApplicationContext().getResources().getString(R.string.sintomas_respuesta_no))){
            return "0";
        }else if(respuesta.equalsIgnoreCase(getApplicationContext().getResources().getString(R.string.sintomas_respuesta_muy_poco))){
            return "1";
        }else if(respuesta.equalsIgnoreCase(getApplicationContext().getResources().getString(R.string.sintomas_respuesta_poco))){
            return "2";
        }else if(respuesta.equalsIgnoreCase(getApplicationContext().getResources().getString(R.string.sintomas_respuesta_si))){
            return "3";
        }else if(respuesta.equalsIgnoreCase(getApplicationContext().getResources().getString(R.string.sintomas_respuesta_mucho))){
            return "4";
        }else if(respuesta.equalsIgnoreCase(getApplicationContext().getResources().getString(R.string.sintomas_respuesta_muchisimo))) {
            return "5";
        }else {
            return null;
        }

    }

    private void postWeight(String kg, String date_time) throws ExecutionException, InterruptedException {

        Post_Weight postWeight = new Post_Weight(getApplicationContext(), kg, date_time);
        try {
            boolean response = postWeight.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    private void postBloodPressure(String date_time, String mmHg, String type, String shift) throws ExecutionException, InterruptedException {

        Post_BloodPressure postBloodPressure = new Post_BloodPressure(getApplicationContext(), date_time, mmHg, type, shift);
        try {
            boolean response = postBloodPressure.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    private void postHeartRate(String date_time, String ppm) throws ExecutionException, InterruptedException {
        Post_HeartRate postHeartRate = new Post_HeartRate(getApplicationContext(), date_time, ppm);
        try {
            boolean response = postHeartRate.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void postAnswer(String date_time, String questionId, String rate) throws ExecutionException, InterruptedException {

        Post_Answer postAnswers = new Post_Answer(getApplicationContext(), date_time, questionId, rate);
        try {
            boolean response = postAnswers.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    private void postAlert(String date_time, String description, String level, String type, int visibility) throws ExecutionException, InterruptedException {

        int intLevel = convertirLevel(level);
        int intType = convertirType(type);

        Post_Alert post_alert = new Post_Alert(getApplicationContext(), date_time, description, intLevel, intType, visibility);

        boolean response = post_alert.execute().get();
    }

    private int convertirType(String type) {
        int intType;

        switch (type){
            case AlertasContract.AlertasEntry.ALERTA_PARAMETRO_SOS:
                intType= JSON_CONSTANTS.ALERTA_TYPE_SOS;
                break;

            case AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO:
                intType= JSON_CONSTANTS.ALERTA_TYPE_WEIGHT;
                break;

            case AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA:
                intType= JSON_CONSTANTS.ALERTA_TYPE_BLOOD_PRESSURE;
                break;

            case AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS:
                intType= JSON_CONSTANTS.ALERTA_TYPE_SYMPTOMS;
                break;

                //14/08/18
            case JSON_CONSTANTS.HEART_RATES:
                intType= JSON_CONSTANTS.ALERTA_TYPE_HEART_RATE;
                break;

            case AlertasContract.AlertasEntry.ALERTA_PARAMETRO_MEDICINE:
                intType= JSON_CONSTANTS.ALERTA_TYPE_MEDICINE;
                break;

            default:
                intType= 0;
                break;
        }
        return intType;
    }

    private int convertirLevel(String level) {

        int intLevel;

        switch (level){
            case AlertasContract.AlertasEntry.ALERTA_TIPO_VERDE:
                intLevel= JSON_CONSTANTS.ALERTA_LEVEL_GREEN;
            break;

            case AlertasContract.AlertasEntry.ALERTA_TIPO_AMARILLA:
                intLevel= JSON_CONSTANTS.ALERTA_LEVEL_YELLOW;
            break;

            case AlertasContract.AlertasEntry.ALERTA_TIPO_ROJA:
                intLevel= JSON_CONSTANTS.ALERTA_LEVEL_RED;
            break;

            default:
                intLevel= 0;
                break;
        }
            return intLevel;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      }
}
