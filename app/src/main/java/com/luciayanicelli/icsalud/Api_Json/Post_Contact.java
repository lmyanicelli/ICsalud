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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by LuciaYanicelli on 16/11/2017.
 *
 * Crear un nuevo contacto para el paciente
 */

public class Post_Contact extends AsyncTask<Integer, ArrayList, Boolean> {

    private static String ID_PATIENT = "";
    private final Configuraciones configuraciones;

    private String CONTACT, FIRST_NAME, LAST_NAME, EMAIL, MOBILE_NUMBER;
    private String ACCESS_TOKEN;
    private final String BASE_URL;
    private Context context;


 public Post_Contact(@NonNull Context context, @NonNull String firstName, @NonNull String lastName, @NonNull String email, @NonNull String mobileNumber) throws ExecutionException, InterruptedException {

        this.context = context;
        this.FIRST_NAME = firstName;
        this.LAST_NAME = lastName;
        this.EMAIL = email;
        this.MOBILE_NUMBER = mobileNumber;


        this.configuraciones = new Configuraciones(context);
        this.ID_PATIENT = configuraciones.getID();

        JSON_functions jsonFunctions = new JSON_functions(context);
        this.ACCESS_TOKEN = jsonFunctions.getAccessTokenPassword();

        this.BASE_URL = JSON_CONSTANTS.BASE_URL +
                "/" + JSON_CONSTANTS.PATIENTS +
                "/" + ID_PATIENT +
                "/" + JSON_CONSTANTS.CONTACTS;

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

        HttpURLConnection con = null;

        try {
            //Conectando
            con = (HttpURLConnection) url.openConnection();
            //metodo post
            con.setRequestMethod(JSON_CONSTANTS.REQUEST_POST);
            //Añadiendo request headers
            con.setRequestProperty(JSON_CONSTANTS.HEADER_ACCEPT, JSON_CONSTANTS.HEADER_ACCEPT_VALUE_1);
            con.setRequestProperty(JSON_CONSTANTS.HEADER_CONTENT_TYPE, JSON_CONSTANTS.HEADER_CONTENT_TYPE_VALUE);
            con.setRequestProperty(JSON_CONSTANTS.HEADER_AUTHORIZATION, JSON_CONSTANTS.HEADER_AUTHORIZATION_VALUE + ACCESS_TOKEN);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_CONSTANTS.FIRST_NAME, FIRST_NAME);
            jsonObject.put(JSON_CONSTANTS.LAST_NAME, LAST_NAME);
            jsonObject.put(JSON_CONSTANTS.EMAIL, EMAIL);
            jsonObject.put(JSON_CONSTANTS.CONTACTS_PRIORITY, 1);
            jsonObject.put(JSON_CONSTANTS.CONTACTS_MOBILE_NUMBER, MOBILE_NUMBER);
            jsonObject.put(JSON_CONSTANTS.CONTACTS_LAND_LINE, "");
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

            } else {
                String error = con.getResponseMessage();
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

        return true;

    }


}