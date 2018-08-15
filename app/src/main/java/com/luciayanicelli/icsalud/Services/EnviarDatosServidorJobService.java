package com.luciayanicelli.icsalud.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.RequiresApi;
import android.util.Log;

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
import com.luciayanicelli.icsalud.R;

import java.util.concurrent.ExecutionException;

import static com.luciayanicelli.icsalud.R.string.sintomas_respuesta_muchisimo;
import static com.luciayanicelli.icsalud.R.string.sintomas_respuesta_mucho;
import static com.luciayanicelli.icsalud.R.string.sintomas_respuesta_poco;
import static com.luciayanicelli.icsalud.R.string.sintomas_respuesta_si;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class EnviarDatosServidorJobService extends JobService {

    private boolean isWorking = false;
    private JobParameters jobParameters;

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(this.getClass().getSimpleName(),"onStartJobServidor");
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
               // Toast.makeText(getApplicationContext(), R.string.service_message, Toast.LENGTH_SHORT).show();

                jobParameters = params;
                isWorking = true;
                try {
                    enviarDatosServidor();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


///
//                jobFinished(params, false);
            }
        });

  //      SetearAlarma.scheduleJobEnviarDatosServidor(getApplicationContext());
  //      return true;
        return isWorking;
    }

    private void enviarDatosServidor() throws ExecutionException, InterruptedException {
        ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());

            if(conexionInternet.execute().get()) {
                handleActionMediciones();
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
            cursorPESO.getCount();

            String kg, date_time;

            do {

                date_time = cursorPESO.getString(1);
                kg = String.valueOf(cursorPESO.getDouble(2));
                postWeight(kg, date_time);

            } while (cursorPESO.moveToNext());
        }

        cursorPESO.close();
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
            cursorPESO.getCount();

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
        cursorPESO.close();

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
           cursorPESO.getCount();

            String date_time, ppm;

            do {

                date_time = cursorPESO.getString(1);
                ppm = String.valueOf(cursorPESO.getInt(2)); //FC

                postHeartRate(date_time, ppm);

            } while (cursorPESO.moveToNext());
        }
        cursorPESO.close();

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
            cursorPESO.getCount();

            String date_time, questionId, rate;

            do {

                date_time = cursorPESO.getString(1);
                questionId = String.valueOf(cursorPESO.getString(2)); //id pregunta servidor
                String respuesta = cursorPESO.getString(4); //respuesta

                rate = obtenerRateRespuesta(respuesta);


                postAnswer(date_time, questionId, rate);

            } while (cursorPESO.moveToNext());
        }

        cursorPESO.close();
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
           cursorAlertas.getCount();

            String date_time, description, level, type;

            do {

                date_time = cursorAlertas.getString(1);
                description = cursorAlertas.getString(2);
                level = cursorAlertas.getString(3); //tipo verde amarilla roja
                type = cursorAlertas.getString(4); //parametro peso, pa, sintomas, sos

                postAlert(date_time, description, level,type);

            } while (cursorAlertas.moveToNext());
        }

        cursorAlertas.close();

        isWorking = false;
        jobFinished(jobParameters, false);

    }



    private String obtenerRateRespuesta(String respuesta) {
//API ACEPTA RATE DE 0 EN ADELANTE
        if(respuesta.equalsIgnoreCase(getApplicationContext().getResources().getString(R.string.sintomas_respuesta_no))){
            return "0";
        }else if(respuesta.equalsIgnoreCase(getApplicationContext().getResources().getString(R.string.sintomas_respuesta_muy_poco))){
            return "1";
        }else if(respuesta.equalsIgnoreCase(getApplicationContext().getResources().getString(sintomas_respuesta_poco))){
            return "2";
        }else if(respuesta.equalsIgnoreCase(getApplicationContext().getResources().getString(sintomas_respuesta_si))){
            return "3";
        }else if(respuesta.equalsIgnoreCase(getApplicationContext().getResources().getString(sintomas_respuesta_mucho))){
            return "4";
        }else if(respuesta.equalsIgnoreCase(getApplicationContext().getResources().getString(sintomas_respuesta_muchisimo))) {
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

    private void postAlert(String date_time, String description, String level, String type) throws ExecutionException, InterruptedException {

        int intLevel = convertirLevel(level);
        int intType = convertirType(type);

        Post_Alert post_alert = new Post_Alert(getApplicationContext(), date_time, description, intLevel, intType);

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
    public boolean onStopJob(JobParameters jobParameters) {

    //    return true;
        boolean needsReschedule = isWorking;
        jobFinished(jobParameters, needsReschedule);
        return needsReschedule;
    }

}
