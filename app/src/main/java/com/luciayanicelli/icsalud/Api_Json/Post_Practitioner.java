package com.luciayanicelli.icsalud.Api_Json;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by LuciaYanicelli on 16/11/2017.
 *
 * Enviar solicitud de vinculacion a un practitioner
 */

public class Post_Practitioner extends AsyncTask<Integer, ArrayList, HashMap<String, String>> {

    private static String ID_PATIENT = "";
    private final Configuraciones configuraciones;

    private String ID_PRACTITIONER;
    private String ACCESS_TOKEN;
    private final String BASE_URL;
    private Context context;

    private HashMap<String,String> data = new HashMap<>();

    public Post_Practitioner(@NonNull Context context, @NonNull String ID_PRACTITIONER) throws ExecutionException, InterruptedException {

        this.context = context;
        this.ID_PRACTITIONER = ID_PRACTITIONER;

        this.configuraciones = new Configuraciones(context);
        this.ID_PATIENT = configuraciones.getID();

        JSON_functions jsonFunctions = new JSON_functions(context);
        this.ACCESS_TOKEN = jsonFunctions.getAccessTokenPassword();

        this.BASE_URL = JSON_CONSTANTS.BASE_URL +
                "/" + JSON_CONSTANTS.PATIENTS +
                "/" + ID_PATIENT +
                "/" + JSON_CONSTANTS.PRACTITIONER;

    }

    @Override
    protected HashMap<String, String> doInBackground(Integer... params) {

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
            jsonObject.put(JSON_CONSTANTS.ID, ID_PRACTITIONER);
            jsonObject.put(JSON_CONSTANTS.PRACTITIONER_STATUS, 0);
            // 4. convert JSONObject to JSON to String
            String jsonString = jsonObject.toString();

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
                data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, Boolean.TRUE.toString());

            } else {
                data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, Boolean.FALSE.toString());
                data.put(JSON_CONSTANTS.PRACTITIONER, String.valueOf(0));
            }

        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, Boolean.FALSE.toString());
        } catch (IOException e1) {
            e1.printStackTrace();
            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, Boolean.FALSE.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, Boolean.FALSE.toString());
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, Boolean.FALSE.toString());
        }

        return data;

    }


}
