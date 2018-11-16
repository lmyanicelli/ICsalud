package com.luciayanicelli.icsalud.Api_Json;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by LuciaYanicelli on 16/11/2017.
 */

public class Buscar_Patient extends AsyncTask<String, ArrayList, HashMap<String, String>> {

    private static String BASE_URL = JSON_CONSTANTS.BASE_URL + JSON_CONSTANTS.PATIENTS;

    private static String BUSQUEDA = "";


    private String EMAIL;
    private String ACCESS_TOKEN;
    private Context CONTEXT;

    private StringBuilder result = null;

    public Buscar_Patient(Context context, String EMAIL) throws ExecutionException, InterruptedException {
        this.CONTEXT = context;
        this.EMAIL = EMAIL;

        JSON_functions jsonFunctions = new JSON_functions(CONTEXT);
        this.ACCESS_TOKEN = jsonFunctions.getAccessTokenPassword();
    }


    @Override
    protected HashMap<String, String> doInBackground(String... params) {

        final HashMap<String, String> data = new HashMap<String, String>();

        BUSQUEDA = buscarPatientForEmail(EMAIL);

        String base_url = BASE_URL + JSON_CONSTANTS.FILTER + BUSQUEDA;

        // 1. Obtener la conexión
        URL url = null;
        try {
            url = new URL(base_url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpsURLConnection con = null;

        //Conectando
        try {
            con = (HttpsURLConnection)url.openConnection();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        //metodo post
        try {
            con.setRequestMethod(JSON_CONSTANTS.REQUEST_GET);
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        }
        //Añadiendo request headers
        con.setRequestProperty(JSON_CONSTANTS.HEADER_ACCEPT, JSON_CONSTANTS.HEADER_ACCEPT_VALUE_1);
        con.setRequestProperty(JSON_CONSTANTS.HEADER_CONTENT_TYPE, JSON_CONSTANTS.HEADER_CONTENT_TYPE_VALUE);
        con.setRequestProperty(JSON_CONSTANTS.HEADER_AUTHORIZATION, JSON_CONSTANTS.HEADER_AUTHORIZATION_VALUE +  ACCESS_TOKEN);

        int statusCode = 0;
        try {
            statusCode = con.getResponseCode();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if(statusCode == 401){
            //Unauthorized
            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.FALSE));

            return data;

        }else{
            //Autorizado
            InputStream contentStream = null;
            try {
                contentStream = con.getInputStream();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(contentStream));

            result = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.TRUE));
            data.put(JSON_CONSTANTS.PATIENTS, result.toString());

            return data;

        }
}

    public String buscarPatientForEmail (String email){

        String param= JSON_CONSTANTS.EMAIL + JSON_CONSTANTS.IGUAL + email;

        return param;
    }

}
