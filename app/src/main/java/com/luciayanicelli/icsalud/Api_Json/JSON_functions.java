package com.luciayanicelli.icsalud.Api_Json;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.provider.BaseColumns;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_DBHelper;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_SintomasContract;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_SintomasDBHelper;
import com.luciayanicelli.icsalud.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by LuciaYanicelli on 13/12/2017.
 */

public class JSON_functions {

    private Context context;

    private Configuraciones configuraciones;

    private String USERNAME, PASSWORD;
    private String access_token_password;

    public JSON_functions(Context context) throws ExecutionException, InterruptedException {
        this.context = context;
        this.configuraciones = new Configuraciones(context);
        this.USERNAME = configuraciones.getUserEmail();
        this.PASSWORD = configuraciones.getUserPassword();

    }

    public String getAccessTokenPassword(){

       HashMap<String, String> request;

       Json_Request_Access_Token_Password jsonRequestAccessTokenPassword =
               new Json_Request_Access_Token_Password(USERNAME, PASSWORD);
        try {
            request = jsonRequestAccessTokenPassword.execute().get();

            //Si se obtiene la autorización correcta, devuelve el access_token_password
            if(request.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(String.valueOf(Boolean.TRUE))){
                access_token_password = request.get(JSON_CONSTANTS.RESPONSE_ACCESS_TOKEN);
                return access_token_password;
            }else if(request.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(JSON_CONSTANTS.HEADER_UNAUTHORIZED)){
                configuraciones.setEstadoLogin(Boolean.FALSE);
                return null;
            }else{
                return null;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }

   }


   //Buscar fecha de último parametro ingresado
    public String getLastDate(String parameter) throws ExecutionException, InterruptedException {

//get {{url}}/patients/{{patient}}/weights

        Get_Parameters get_LastDateParameter = new Get_Parameters(context, parameter);
        HashMap<String, String> response;
        String date_time;

        try {
            response = get_LastDateParameter.execute().get();
            String authorization = response.get(JSON_CONSTANTS.HEADER_AUTHORIZATION);
            date_time = response.get(JSON_CONSTANTS.DATE_TIME);

            if(authorization.equalsIgnoreCase(String.valueOf(Boolean.TRUE))){
               return date_time;

           }else{
               //devuelve una fecha muy antigua para que envíe todos los datos de la tabla
               return "2000-01-01 00:00:00";
           }


        } catch (InterruptedException e) {
            e.printStackTrace();
            return "2000-01-01 00:00:00";
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "2000-01-01 00:00:00";
        }
    }

    //Buscar fecha de último parametro ingresado
    public String getAlertsLastDate() throws ExecutionException, InterruptedException {

//get {{url}}/patients/{{patient}}/weights

        Get_Alerts_index get_alerts_index = new Get_Alerts_index(context);
        HashMap<String, String> response;
        String date_time;

        try {
            response = get_alerts_index.execute().get();
            String authorization = response.get(JSON_CONSTANTS.HEADER_AUTHORIZATION);
            date_time = response.get(JSON_CONSTANTS.DATE_TIME);

            if(authorization.equalsIgnoreCase(String.valueOf(Boolean.TRUE))){
                return date_time;

            }else{
                //devuelve una fecha muy antigua para que envíe todos los datos de la tabla
                return "2000-01-01 00:00:00";
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
            return "2000-01-01 00:00:00";
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "2000-01-01 00:00:00";
        }
    }


    public void recuperarDatosMediciones() throws ExecutionException, InterruptedException, JSONException {

        String fecha = "2000-01-01 00:00:00";
        Autodiagnostico_DBHelper dbHelper = new Autodiagnostico_DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        recuperarDatosPeso(fecha, db);
    }

    private void recuperarDatosPeso(final String fecha, final SQLiteDatabase db) throws JSONException, ExecutionException, InterruptedException {

        Get_Parameters getParameterW = new Get_Parameters(context,JSON_CONSTANTS.WEIGHTS);
        HashMap<String, String> data = getParameterW.execute().get();

        if(data.size()>1){

            JSONObject jsonObject_response = new JSONObject(data.get(JSON_CONSTANTS.JSON_STRING));
            // Getting JSON Array node
            JSONArray jsonArray = jsonObject_response.getJSONArray(JSON_CONSTANTS.RESPONSE_DATA);

            int numRows = jsonArray.length();
            int fin;
            if(numRows >= 3){
                fin = 3;
            }else{
                if(numRows==2){
                    fin = 2;
                }else{
                    fin = 1;
                }
            }

            for (int x = numRows-1; x >= numRows - fin; x--) {
                JSONObject row = null;
                row = jsonArray.getJSONObject(x); //última fila numRows-1

                ContentValues values = new ContentValues();
                values.put(AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE, row.getString(JSON_CONSTANTS.DATE_TIME));
                values.put(AutodiagnosticoContract.AutodiagnosticoEntry.PESO_VALOR, row.getString(JSON_CONSTANTS.WEIGHTS_KG));
                values.put(AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO, AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO_ENVIADA);

                //Devuelve -1 si hay un error al insertar los datos en la base de datos
                long controlInsert = db.insert(AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO, null, values);

            }
        }

        //poner delay de 1 segundo
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 1s = 1000ms
                try {
                    recuperarDatosPA(fecha, db);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 1000);




    }

    private void recuperarDatosPA(final String fecha, final SQLiteDatabase db) throws ExecutionException, InterruptedException, JSONException {

        Get_Parameters getParameterB = new Get_Parameters(context,JSON_CONSTANTS.BLOOD_PRESSURES);
        HashMap<String, String> dataB = getParameterB.execute().get();
        int ps = 0;
        int pd = 0;
        int fc;
        String fechaHora = null;

        if(dataB.size()>1){

            JSONObject jsonObject_response = new JSONObject(dataB.get(JSON_CONSTANTS.JSON_STRING));
            // Getting JSON Array node
            JSONArray jsonArrayB = jsonObject_response.getJSONArray(JSON_CONSTANTS.RESPONSE_DATA);

            int numRows = jsonArrayB.length();

            if (numRows > 1) {
                //Existen al menos 2 filas (ps y pd)
                for (int x = numRows-1; x >= numRows - 2; x--) {
                    JSONObject row = null;
                    row = jsonArrayB.getJSONObject(x); //última fila numRows-1

                    //Obtener última fecha
                    fechaHora = row.getString(JSON_CONSTANTS.DATE_TIME);
                    if(row.getString(JSON_CONSTANTS.BLOOD_PRESSURES_TYPE).equalsIgnoreCase(String.valueOf(JSON_CONSTANTS.TYPE_DIASTOLIC))){
                        //PRESION DIASTOLICA
                        pd = Integer.parseInt(row.getString(JSON_CONSTANTS.BLOOD_PRESSURES_MMHG));
                    }else{
                        ps = Integer.parseInt(row.getString(JSON_CONSTANTS.BLOOD_PRESSURES_MMHG));
                    }
                }


                Get_Parameters getParameterFC = new Get_Parameters(context,JSON_CONSTANTS.HEART_RATES);
                HashMap<String, String> heartRate = getParameterFC.execute().get();
                String fechaBP = fechaHora;

                if(fechaBP.equalsIgnoreCase(getLastDate(JSON_CONSTANTS.HEART_RATES))){
                    //las fechas son las mismas
                    fc = Integer.parseInt(heartRate.get(JSON_CONSTANTS.HEART_RATES_PPM));

                    ContentValues values = new ContentValues();

                    values.put(AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE, fechaBP);
                    values.put(AutodiagnosticoContract.AutodiagnosticoEntry.PA_PS, ps);
                    values.put(AutodiagnosticoContract.AutodiagnosticoEntry.PA_PD, pd);
                    values.put(AutodiagnosticoContract.AutodiagnosticoEntry.PA_FC, fc);
                    values.put(AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO, AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO_ENVIADA);

                    //Devuelve -1 si hay un error al insertar los datos en la base de datos
                    long controlInsert = db.insert(AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA, null, values);

                }

                //CONTINUAR AQUI --- RECUPERAR ULTIMOS 2 DATOS AL MENOS XQ SISTOLICA Y DIASTOLICA VAN EN DISTINTOS REGISTROS

            }
        }

        //poner delay de 1 segundo
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 1s = 1000ms
                try {
                    recuperarDatosSintomas(fecha, db);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 1000);


    }

    private void recuperarDatosSintomas(String fecha, SQLiteDatabase db) throws JSONException, ExecutionException, InterruptedException {

        //     case JSON_CONSTANTS.ANSWERS:
        Get_Parameters getParameterA = new Get_Parameters(context,JSON_CONSTANTS.ANSWERS);
        HashMap<String, String> dataA = getParameterA.execute().get();

        if(dataA.size() > 1){

            JSONObject jsonObject_response = new JSONObject(dataA.get(JSON_CONSTANTS.JSON_STRING));
            // Getting JSON Array node
            JSONArray jsonArrayA = jsonObject_response.getJSONArray(JSON_CONSTANTS.RESPONSE_DATA);


            int numRows = jsonArrayA.length();
            int fin;
            if(numRows >= 3){
                fin = 3;
            }else{
                if(numRows==2){
                    fin = 2;
                }else{
                    fin = 1;
                }
            }

            //CAMBIAR PARA CARGAR DE ATRAS HACIA ADELANTE
            for (int x = numRows-fin; x <= numRows - 1; x++) {
                JSONObject row = null;
                row = jsonArrayA.getJSONObject(x); //última fila numRows-1

                String idPreguntaServidor = row.getString(JSON_CONSTANTS.QUESTION_ID);

                HashMap<String, String> dataPregunta = obtenerPregunta(idPreguntaServidor);
                if(dataPregunta.size()>0){
                    ContentValues values = new ContentValues();
                    values.put(AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE, row.getString(JSON_CONSTANTS.DATE_TIME));

                    values.put(AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_IDPREGUNTA_SERVIDOR, idPreguntaServidor);

                    String pregunta = dataPregunta.get(Autodiagnostico_SintomasContract.SintomasEntry.PREGUNTA);
                    String idPregunta = dataPregunta.get(Autodiagnostico_SintomasContract.SintomasEntry._ID);

                    values.put(AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_PREGUNTA, pregunta);
                    values.put(AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_IDPREGUNTA, Integer.parseInt(idPregunta));

                    String respuesta = obtenerRespuesta(row.getString(JSON_CONSTANTS.ANSWERS_RATE));
                    values.put(AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_RESPUESTA, respuesta);

                    values.put(AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO, AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO_ENVIADA);

                    //Devuelve -1 si hay un error al insertar los datos en la base de datos
                    long controlInsert = db.insert(AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS, null, values);

                }

            }
        }
        db.close();

    }



    private HashMap<String, String> obtenerPregunta(String idPreguntaServidor) {

        HashMap<String, String> dataSintomas = new HashMap<String, String>();

        Autodiagnostico_SintomasDBHelper dbHelper = new Autodiagnostico_SintomasDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //BUSCO LA PREGUNTA
        String[] camposDB2 = new String[]{Autodiagnostico_SintomasContract.SintomasEntry.PREGUNTA, BaseColumns._ID, Autodiagnostico_SintomasContract.SintomasEntry.ID_SERVIDOR};
        String selection = Autodiagnostico_SintomasContract.SintomasEntry.ID_SERVIDOR + "= ?";

        String[] args = new String[] {idPreguntaServidor};

                /*query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
Query the given URL, returning a Cursor over the result set.*/
        Cursor cursor2 = db.query(true, Autodiagnostico_SintomasContract.SintomasEntry.TABLE_NAME, camposDB2, selection, args,null,null,null,null);

        String pregunta;

        if(cursor2 != null && cursor2.moveToFirst()){
            int count;
            count = cursor2.getCount();
            pregunta = cursor2.getString(0);
            int idPregunta = cursor2.getInt(1);

            dataSintomas.put(Autodiagnostico_SintomasContract.SintomasEntry.PREGUNTA, pregunta);
            dataSintomas.put(Autodiagnostico_SintomasContract.SintomasEntry._ID, String.valueOf(idPregunta));
        }

        db.close();
        return dataSintomas;

    }

    private String obtenerRespuesta(String string) {

        String respuesta;

        switch (string){

            case "0":
                respuesta = context.getResources().getString(R.string.sintomas_respuesta_no);
            break;

            case "1":
                respuesta = context.getResources().getString(R.string.sintomas_respuesta_muy_poco);
            break;

            case "2":
                respuesta = context.getResources().getString(R.string.sintomas_respuesta_poco);
            break;

            case "3":
                respuesta = context.getResources().getString(R.string.sintomas_respuesta_si);
            break;

            case "4":
                respuesta = context.getResources().getString(R.string.sintomas_respuesta_mucho);
            break;

            case "5":
                respuesta = context.getResources().getString(R.string.sintomas_respuesta_muchisimo);
            break;

            default:
                respuesta = null;
                break;
        }

        return respuesta;
    }


}