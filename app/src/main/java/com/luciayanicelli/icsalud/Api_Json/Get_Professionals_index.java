package com.luciayanicelli.icsalud.Api_Json;

import android.os.AsyncTask;

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
 * Created by LuciaYanicelli on 16/11/2017.
 * Muestra el listado de todos los profesionales
 *
 */

public class Get_Professionals_index extends AsyncTask<Integer, Void, HashMap<String, String>> {

    private String ACCESS_TOKEN;

    // get {{url}}/professional/
    private final String BASE_URL;


    public Get_Professionals_index() throws ExecutionException, InterruptedException {

        this.BASE_URL = JSON_CONSTANTS.BASE_URL +
                "/" + JSON_CONSTANTS.PROFESSIONALS +
                "/";

        Json_Request_Access_Token_Credential jsonRequestAccessTokenCredentials = new Json_Request_Access_Token_Credential();
        String access_token_credentials = null;
        try {
            access_token_credentials = jsonRequestAccessTokenCredentials.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        this.ACCESS_TOKEN = access_token_credentials;
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
        //metodo post
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

        // Recibe:
        // {"data":[],"meta":{}}
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

            if (total != 0) {
                //hay profesionales
                JSONArray jsonArray = null;

                if (total_pages > 1) {


                    String professionals = "";
                    String id_professionals = "";
                    String email_professionals = "";

                    //page=1
                    jsonArray = jsonObject_response.getJSONArray(JSON_CONSTANTS.RESPONSE_DATA);

                    data = obtenerData(jsonArray);
                    professionals = data.get(JSON_CONSTANTS.PROFESSIONALS);
                    id_professionals = data.get(JSON_CONSTANTS.ID);
                    email_professionals = data.get(JSON_CONSTANTS.EMAIL);

                    for (int i = 2; i <= total_pages; i++) {
                        jsonArray = getPage(i);

                        //data from page=i
                        data = obtenerData(jsonArray);
                        professionals = professionals + data.get(JSON_CONSTANTS.PROFESSIONALS) + ";";
                        id_professionals = id_professionals + data.get(JSON_CONSTANTS.ID) + ";";
                        email_professionals = email_professionals + data.get(JSON_CONSTANTS.EMAIL) + ";";


                    }

                    data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.TRUE));
                    data.put(JSON_CONSTANTS.PROFESSIONALS, professionals);
                    data.put(JSON_CONSTANTS.ID, id_professionals);
                    data.put(JSON_CONSTANTS.EMAIL, email_professionals);

                    return data;


                } else {

                    //page=1
                    jsonArray = jsonObject_response.getJSONArray(JSON_CONSTANTS.RESPONSE_DATA);

                    data = obtenerData(jsonArray);

                    return data;

                }


            } else {
                data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.FALSE));
                return data;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.FALSE));
            return data;
        }

    }


    //GET PAGE (I)
    public JSONArray getPage(int page) {

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
        try {
            jsonObject_response = new JSONObject(result.toString());

            // Getting JSON Array node
            jsonArr = jsonObject_response.getJSONArray(JSON_CONSTANTS.RESPONSE_DATA);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArr;
    }

    public HashMap<String, String> obtenerData(JSONArray jsonArr) throws JSONException {

        final int numRows = jsonArr.length();

        HashMap<String, String> data = new HashMap<String, String>();

        String professionals = "";
        String id_professionals = "";
        String email_professionals = "";

        for (int x = 0; x < numRows; x++) {
            final JSONObject row = jsonArr.getJSONObject(x); //última fila numRows-1

            if (row.getString(JSON_CONSTANTS.FIRST_NAME) != "null") {

                professionals = professionals + row.getString(JSON_CONSTANTS.LAST_NAME) + ", " + row.getString(JSON_CONSTANTS.FIRST_NAME) + ";";
                id_professionals = id_professionals + String.valueOf(row.get(JSON_CONSTANTS.ID)) + ";";
                email_professionals = email_professionals + row.getString(JSON_CONSTANTS.EMAIL) + ";";
            }

        }

        data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.TRUE));
        data.put(JSON_CONSTANTS.PROFESSIONALS, professionals);
        data.put(JSON_CONSTANTS.ID, id_professionals);
        data.put(JSON_CONSTANTS.EMAIL, email_professionals);

        return data;

    }
}