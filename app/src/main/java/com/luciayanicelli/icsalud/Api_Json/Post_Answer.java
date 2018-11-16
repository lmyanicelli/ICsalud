package com.luciayanicelli.icsalud.Api_Json;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by LuciaYanicelli on 16/11/2017.
 *
 * Crea un nuevo registro ANSWER
 */

public class Post_Answer extends AsyncTask<Integer, ArrayList, Boolean> {


 private static String _PATIENT_ID;
    private Configuraciones configuraciones;
    private Context context;
    private String ACCESS_TOKEN;

    // get {{url}}/patients/{{patient}}/weights
    private final String BASE_URL;

    private final String QUESTIONID, RATE, DATE_TIME;




    public Post_Answer(Context context, String date_time, String questionId, String rate) throws ExecutionException, InterruptedException {
        this.context = context;
        this.configuraciones = new Configuraciones(context);
        this._PATIENT_ID = configuraciones.getID();
        this.BASE_URL = JSON_CONSTANTS.BASE_URL +
                "/" + JSON_CONSTANTS.PATIENTS +
                "/" + _PATIENT_ID +
                "/" + JSON_CONSTANTS.ANSWERS;

        JSON_functions jsonFunctions = new JSON_functions(context);
        this.ACCESS_TOKEN = jsonFunctions.getAccessTokenPassword();
        this.QUESTIONID = questionId;
        this.RATE = rate;
        this.DATE_TIME = date_time;
    }


    @Override
    protected Boolean doInBackground(Integer... params) {

        final StringBuilder result = new StringBuilder();

        // 1. Obtener la conexión
        URL url = null;
        try {
            url = new URL(BASE_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpsURLConnection con = null;

        try {
            //Conectando
            con = (HttpsURLConnection) url.openConnection();
            //metodo post
            con.setRequestMethod(JSON_CONSTANTS.REQUEST_POST);
            //Añadiendo request headers
            con.setRequestProperty(JSON_CONSTANTS.HEADER_ACCEPT, JSON_CONSTANTS.HEADER_ACCEPT_VALUE_1);
            con.setRequestProperty(JSON_CONSTANTS.HEADER_CONTENT_TYPE, JSON_CONSTANTS.HEADER_CONTENT_TYPE_VALUE);
            con.setRequestProperty(JSON_CONSTANTS.HEADER_AUTHORIZATION, JSON_CONSTANTS.HEADER_AUTHORIZATION_VALUE + ACCESS_TOKEN);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_CONSTANTS.QUESTION_ID, QUESTIONID);
            jsonObject.put(JSON_CONSTANTS.ANSWERS_RATE, RATE);
            jsonObject.put(JSON_CONSTANTS.DATE_TIME, DATE_TIME);
            // 4. convert JSONObject to JSON to String
            String jsonString = jsonObject.toString();

            byte[] bytes = jsonString.getBytes();

            // Enable writing
            con.setDoOutput(true);

            // Write the data
            con.getOutputStream().write(jsonString.getBytes());

            int statusCode = con.getResponseCode();

            if (statusCode == JSON_CONSTANTS.STATUS_CODE_CREATED) {
                InputStream contentStream = null;
                try {
                    contentStream = con.getInputStream();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(contentStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return true;

            } else {

                return false;
            }

        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return false;
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        } catch (JSONException e1) {
            e1.printStackTrace();
            return false;

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            return false;
        }

    }

}
