package com.luciayanicelli.icsalud.Api_Json;

import android.content.Context;
import android.os.AsyncTask;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
  */

public class Get_Alerts_index extends AsyncTask<Integer, Void, HashMap<String, String>>{

    private static String _PATIENT_ID;
    private Configuraciones configuraciones;
    private Context context;
    private String ACCESS_TOKEN;
    private int STATUS;

    // get {{url}}/patients/{{patient}}/alerts
    private final String BASE_URL;

    public Get_Alerts_index(Context context) throws ExecutionException, InterruptedException {
        this.context = context;
        this.configuraciones = new Configuraciones(context);
        this._PATIENT_ID = configuraciones.getID();

        this.BASE_URL = JSON_CONSTANTS.BASE_URL +
                "/" + JSON_CONSTANTS.PATIENTS +
                "/" + _PATIENT_ID +
                "/" + JSON_CONSTANTS.ALERTS;

        JSON_functions jsonFunctions = new JSON_functions(context);
        this.ACCESS_TOKEN = jsonFunctions.getAccessTokenPassword();

    }



    @Override
    protected HashMap<String, String> doInBackground(Integer... params) {

        HashMap<String, String> data = new HashMap<String, String>();

        // 1. Obtener la conexión
        URL url = null;
        try {
            url = new URL(BASE_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection con = null;

        //Conectando
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            con.setRequestMethod(JSON_CONSTANTS.REQUEST_GET);
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        }
        //Añadiendo request headers
        con.setRequestProperty(JSON_CONSTANTS.HEADER_ACCEPT, JSON_CONSTANTS.HEADER_ACCEPT_VALUE_1);
        con.setRequestProperty(JSON_CONSTANTS.HEADER_CONTENT_TYPE, JSON_CONSTANTS.HEADER_CONTENT_TYPE_VALUE);
        con.setRequestProperty(JSON_CONSTANTS.HEADER_AUTHORIZATION, JSON_CONSTANTS.HEADER_AUTHORIZATION_VALUE + ACCESS_TOKEN);


        InputStream contentStream = null;
        try {
            contentStream = con.getInputStream();

        } catch (IOException e1) {
            e1.printStackTrace();
            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.FALSE));
            return data;
        }


        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(contentStream));

        final StringBuilder result = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.FALSE));
            return data;
        }


        // Recibe esto:
        //{"data":[],
        // "meta":{"pagination":{"total":0,
        //                         "count":0,
        //                          "per_page":15,
        //                          "current_page":1,
        //                           "total_pages":0,
        //                             "links":{"first":"http:\/\/api.icsalud.com.ar\/\/patients\/d6c63410-0dd4-11e8-8c3d-c7476782b79a\/practitioners?filter=status+eq+0&page=1","previous":null,"next":null,"last":"http:\/\/api.icsalud.com.ar\/\/patients\/d6c63410-0dd4-11e8-8c3d-c7476782b79a\/practitioners?filter=status+eq+0&page=1"}}}}

        JSONObject jsonObject_response = null;
        try {
            jsonObject_response = new JSONObject(result.toString());

            //OBTENER TOTAL
            JSONObject jsonObjectMETA = jsonObject_response.getJSONObject(JSON_CONSTANTS.RESPONSE_META);
            JSONObject jsonObjectPAGINATION = jsonObjectMETA.getJSONObject(JSON_CONSTANTS.RESPONSE_PAGINATION);
            int total = jsonObjectPAGINATION.getInt(JSON_CONSTANTS.RESPONSE_TOTAL);
            int total_pages = jsonObjectPAGINATION.getInt(JSON_CONSTANTS.RESPONSE_TOTAL_PAGES);
            int per_page = jsonObjectPAGINATION.getInt(JSON_CONSTANTS.RESPONSE_PER_PAGE);
            int current_page = jsonObjectPAGINATION.getInt(JSON_CONSTANTS.RESPONSE_CURRENT_PAGE);

            if(total!=0){

                if(total_pages>1){
                    //hay varias páginas - buscar la última
                    return nuevaConsulta(total_pages);


                }else{
                    //sólo hay una página
                    return cargarDatos(jsonObject_response, result);
                }


            }else{
                data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.FALSE));
                return  data;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.FALSE));
            return data;
        }

    }


    private HashMap<String,String> nuevaConsulta(int page) {
        URL url_page = null;
        JSONArray jsonArr = null;

        try {
            url_page = new URL(BASE_URL + "?page=" + String.valueOf(page));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection con = null;

        //Conectando
        try {
            con = (HttpURLConnection) url_page.openConnection();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            con.setRequestMethod(JSON_CONSTANTS.REQUEST_GET);
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        }
        //Añadiendo request headers
        con.setRequestProperty(JSON_CONSTANTS.HEADER_ACCEPT, JSON_CONSTANTS.HEADER_ACCEPT_VALUE_1);
        con.setRequestProperty(JSON_CONSTANTS.HEADER_CONTENT_TYPE, JSON_CONSTANTS.HEADER_CONTENT_TYPE_VALUE);
        con.setRequestProperty(JSON_CONSTANTS.HEADER_AUTHORIZATION, JSON_CONSTANTS.HEADER_AUTHORIZATION_VALUE + ACCESS_TOKEN);


        InputStream contentStream = null;
        try {
            contentStream = con.getInputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(contentStream));

        final StringBuilder result = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        JSONObject jsonObject_response = null;
        HashMap<String, String> data = new HashMap<String, String>();
        try {
            jsonObject_response = new JSONObject(result.toString());

            // Getting JSON Array node
            jsonArr = jsonObject_response.getJSONArray(JSON_CONSTANTS.RESPONSE_DATA);

            data=cargarDatos(jsonObject_response, result);

        } catch (JSONException e) {
            e.printStackTrace();
            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.FALSE));
        }
        return data;
    }

    private HashMap<String,String> cargarDatos(JSONObject jsonObject_response, StringBuilder result) throws JSONException {
        // Getting JSON Array node
        JSONArray jsonArr = jsonObject_response.getJSONArray(JSON_CONSTANTS.RESPONSE_DATA);

        final int numRows = jsonArr.length();

        HashMap<String, String> data = new HashMap<String, String>();
        if (numRows != 0) {
            final JSONObject row = jsonArr.getJSONObject(numRows - 1); //última fila numRows-1

            String date_time = row.getString(JSON_CONSTANTS.DATE_TIME);

            //Obtener última fecha
            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.TRUE));
            data.put(JSON_CONSTANTS.DATE_TIME, date_time);

            data.put(JSON_CONSTANTS.JSON_STRING, result.toString());

            return data;


        } else {
            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.FALSE));
            return data;
        }
    }


}