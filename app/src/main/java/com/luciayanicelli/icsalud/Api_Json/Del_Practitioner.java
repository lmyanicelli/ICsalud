package com.luciayanicelli.icsalud.Api_Json;

import android.content.Context;
import android.os.AsyncTask;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
  */

public class Del_Practitioner extends AsyncTask<Integer, Void, HashMap<String, String>>{

    private static String _PATIENT_ID;
    private Configuraciones configuraciones;
    private Context context;
    private String ACCESS_TOKEN;
    private String ID_PRACTITIONER;


    // get {{url}}/patients/{{patient}}/practitioners
    private final String BASE_URL;

    public Del_Practitioner(Context context, String id_practitioner) throws ExecutionException, InterruptedException {
        this.context = context;
        this.configuraciones = new Configuraciones(context);
        this._PATIENT_ID = configuraciones.getID();
        this.ID_PRACTITIONER = id_practitioner;

        this.BASE_URL = JSON_CONSTANTS.BASE_URL +
                "/" + JSON_CONSTANTS.PATIENTS +
                "/" + _PATIENT_ID +
                "/" + JSON_CONSTANTS.PRACTITIONER +
                "/" + ID_PRACTITIONER ;

        JSON_functions jsonFunctions = new JSON_functions(context);
        this.ACCESS_TOKEN = jsonFunctions.getAccessTokenPassword();

    }



    @Override
    protected HashMap<String, String> doInBackground(Integer... params) {

        final HashMap<String, String> data = new HashMap<String, String>();

        // 1. Obtener la conexión
        URL url = null;
        try {
            url = new URL(BASE_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpsURLConnection con = null;

        //Conectando
        try {
            con = (HttpsURLConnection) url.openConnection();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        //metodo post
        try {
            con.setRequestMethod(JSON_CONSTANTS.REQUEST_DELETE);
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
            data.put(JSON_CONSTANTS.ERROR_MESSAGE, String.valueOf(0));
            return data;
        }

        try {
            int statusCode = con.getResponseCode();

            if(statusCode == JSON_CONSTANTS.STATUS_CODE_OK |
                    statusCode == JSON_CONSTANTS.STATUS_CODE_NO_CONTENT){
                //204 No Content
                //La petición se ha completado con éxito pero su respuesta no tiene ningún contenido (la respuesta sí que puede incluir información en sus cabeceras HTTP).2​

                //Practitioner eliminado
                data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.TRUE));

            }else{
                //no se eliminó
                data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.FALSE));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }


}