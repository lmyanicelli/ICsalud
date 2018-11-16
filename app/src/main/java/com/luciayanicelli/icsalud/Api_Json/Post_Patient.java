package com.luciayanicelli.icsalud.Api_Json;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

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

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by LuciaYanicelli on 16/11/2017.
 *
 * Crea un nuevo paciente
 */

public class Post_Patient extends AsyncTask<Integer, ArrayList, Boolean> {



    private static final String BASE_URL = JSON_CONSTANTS.BASE_URL + JSON_CONSTANTS.PATIENTS;

    private String FIRST_NAME;
    private String LAST_NAME;
    private String EMAIL;
    private String PASSWORD;
    private String BIRTHDAY;
    private String ACCESS_TOKEN;
    private String GENDER;
    private String OCCUPATION;
    private String EDUCATION_LEVEL;
    private String HEALTH_INSURANCE;
    private int COEXISTENCE;
    private String LAST_HOSPITALIZED_AT;
    private String DIAGNOSED_AT;

    public Post_Patient(@NonNull String ACCESS_TOKEN, @NonNull String FIRST_NAME, @NonNull String LAST_NAME, @NonNull String EMAIL, @NonNull String PASSWORD, @NonNull String BIRTHDAY, @NonNull String OCCUPATION, @NonNull String HEALTH_INSURANCE, @NonNull String GENDER, @NonNull int COEXISTENCE, @NonNull String EDUCATION_LEVEL, @NonNull String LAST_HOSPITALIZED_AT, @NonNull String DIAGNOSED_AT) {
        this.ACCESS_TOKEN = ACCESS_TOKEN;
        this.FIRST_NAME = FIRST_NAME;
        this.LAST_NAME = LAST_NAME;
        this.EMAIL = EMAIL;
        this.PASSWORD = PASSWORD;
        this.BIRTHDAY = BIRTHDAY;
        this.GENDER = GENDER;
        this.OCCUPATION = OCCUPATION;
        this.EDUCATION_LEVEL = EDUCATION_LEVEL;
        this.HEALTH_INSURANCE = HEALTH_INSURANCE;
        this.COEXISTENCE = COEXISTENCE;
        this.LAST_HOSPITALIZED_AT = LAST_HOSPITALIZED_AT;
        this.DIAGNOSED_AT = DIAGNOSED_AT;
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
            jsonObject.put(JSON_CONSTANTS.FIRST_NAME, FIRST_NAME);
            jsonObject.put(JSON_CONSTANTS.LAST_NAME, LAST_NAME);
            jsonObject.put(JSON_CONSTANTS.EMAIL, EMAIL);
            jsonObject.put(JSON_CONSTANTS.BIRTHDAY, BIRTHDAY);
            jsonObject.put(JSON_CONSTANTS.PASSWORD, PASSWORD);
            jsonObject.put(JSON_CONSTANTS.GENDER, GENDER);
            jsonObject.put(JSON_CONSTANTS.OCCUPATION, OCCUPATION);
            jsonObject.put(JSON_CONSTANTS.EDUCATION_LEVEL, EDUCATION_LEVEL);
            jsonObject.put(JSON_CONSTANTS.COEXISTENCE, COEXISTENCE);
            jsonObject.put(JSON_CONSTANTS.LAST_HOSPITALIZED_AT, LAST_HOSPITALIZED_AT);
            jsonObject.put(JSON_CONSTANTS.HEALTH_INSURANCE, HEALTH_INSURANCE);
            jsonObject.put(JSON_CONSTANTS.DIAGNOSED_AT, DIAGNOSED_AT);
            // 4. convert JSONObject to JSON to String
            String jsonString = jsonObject.toString();

            // Enable writing
            con.setDoOutput(true);

            // Write the data
            con.getOutputStream().write(jsonString.getBytes());

            int statusCode = con.getResponseCode();
            String message = con.getResponseMessage();

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
