package com.luciayanicelli.icsalud.Api_Json;

import android.os.AsyncTask;
import android.util.Log;

import com.luciayanicelli.icsalud.R;

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
 * Obtiene el ACCESS_TOKEN_CLIENT_CREDENTIALS con el cual se puede crear un nuevo usuario
 */

public class Json_Request_Access_Token_Credential extends AsyncTask<Integer, ArrayList, String> {

    private final String BASE_URL;
    private String access_token_json_object=null;

    public Json_Request_Access_Token_Credential() throws ExecutionException, InterruptedException {
        this.BASE_URL = JSON_CONSTANTS.BASE_URL +
                JSON_CONSTANTS.OAUTH +
                JSON_CONSTANTS.TOKEN;
    }


    @Override
    protected String doInBackground(Integer... params) {

        final StringBuilder result = new StringBuilder();

        // 1. Obtener la conexión
        URL url = null;
        try {
            url = new URL(BASE_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpsURLConnection con;

        try {
            //Conectando
            con = (HttpsURLConnection) url.openConnection();
            //metodo post
            con.setRequestMethod(JSON_CONSTANTS.REQUEST_POST);
            //Añadiendo request headers
            con.setRequestProperty(JSON_CONSTANTS.HEADER_ACCEPT, JSON_CONSTANTS.HEADER_ACCEPT_VALUE_1);
            con.setRequestProperty(JSON_CONSTANTS.HEADER_CONTENT_TYPE, JSON_CONSTANTS.HEADER_CONTENT_TYPE_VALUE);


            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_CONSTANTS.GRANT_TYPE, JSON_CONSTANTS.CLIENT_CREDENTIALS);
            jsonObject.put(JSON_CONSTANTS.CLIENT_ID, JSON_CONSTANTS.CLIENT_ID_VALUE);
            jsonObject.put(JSON_CONSTANTS.CLIENT__SECRET, JSON_CONSTANTS.CLIENT__SECRET_VALUE);
            // 4. convert JSONObject to JSON to String
            String jsonString = jsonObject.toString();

            // Enable writing
            con.setDoOutput(true);

            // Write the data
            con.getOutputStream().write(jsonString.getBytes());

            int statusCode = con.getResponseCode();


            if (statusCode == JSON_CONSTANTS.STATUS_CODE_OK) {
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

                try {
                    // Recibe esto: {"token_type":"Bearer","expires_in":2592000,"access_token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjdmNzRkNWI3ZGM0OGMwMWFhOTNhYTIyYjA2Nzc1ZGE1NDE4MTkwNmQyMTFlNjhjNmI1NzY1MTliNzE5NTUwNWM1MDAxZTNlY2I1OTJhNWFkIn0.eyJhdWQiOiIxIiwianRpIjoiN2Y3NGQ1YjdkYzQ4YzAxYWE5M2FhMjJiMDY3NzVkYTU0MTgxOTA2ZDIxMWU2OGM2YjU3NjUxOWI3MTk1NTA1YzUwMDFlM2VjYjU5MmE1YWQiLCJpYXQiOjE1MTI2NTM0MzAsIm5iZiI6MTUxMjY1MzQzMCwiZXhwIjoxNTE1MjQ1NDMwLCJzdWIiOiIiLCJzY29wZXMiOltdfQ.tnoaAKWw0WoGBRUw65shS7jHVLLYuNT25wHadV-o_hJ-AkEYYc98--5FetP6E9u0F3Z5P51dqBlN1K4Qs1J75SHi_tM4BvDs8R_NoFQWg6uI2n4fMIYY1uXfysOxJgtphnPawJBJC_lC3IfrbzBP8vkYEe6yZ20PAbAuoRQkAUwwNT2NfmdEq-uyEC54J1y0gDcgvbxQ8xkNsg2NBKbeK-s4N2DNovo743yl5yfUSriMJ-841HHvFldlElLAQgUm77X1F_MOXHyf5UD25WqebzKnJkJlGpXHSquAPZw41W9uG6ThZpZqLth84sb3-poe3L4pQpIY5aWu3GSZNDvBi9PewpEanr4Lo6CEn6FHIGKfYGjyF61ZLk9NprLJNp3BMm8g_rqqmeuPIOPmG_37P1xFKwvPsujN_J90oWdA1_vFKVh70951ix5N51On0w-Qgx4wB09mxEIsluqHPNKXRw8OxsdxzsKQZHGl2fP7Edm81c_1Sph3a-UdQPDK1wuHcTZmYaQ89T5QKTly1Iciem-NHVeRX6KW3f6R-uMQTtQfUgBqyha6yB8ehNRveaovrKCAKOXap0EePmgQDP6O9JHi0LsArzESi5VnIuz__v0J3FlKAIdkis7ipV9vZCmE3E-5a4eMJKZWilbKigNUDBXW-836PnwrskUUaXb3-hU"}
                    JSONObject jsonObject_response = null;
                    try {
                        jsonObject_response = new JSONObject(result.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    access_token_json_object = jsonObject_response.getString(JSON_CONSTANTS.RESPONSE_ACCESS_TOKEN);


                } catch (JSONException e) {
                    e.printStackTrace();

                }


            } else {
                String message = String.valueOf(R.string.message_error_conection_server);

            }

        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());

        }
        return access_token_json_object;

    }

}
