package com.luciayanicelli.icsalud.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;

import java.util.ArrayList;

/**
 * Created by LuciaYanicelli on 14/3/2017.
 *
 * ESTA CLASE ASÍNCRONA ENVÍA UN MSJ DE TEXTO A LOS NÚMEROS INDICADOS EN TELEFONOS
 * EL MENSAJE A ENVIAR SE INDICA EN EL CONSTRUCTOR
 * ---
 * EnviarMsjTexto enviarMsjTexto = new EnviarMsjTexto(direccion);
 enviarMsjTexto.execute();
 ---

 */

public class EnviarMsjTexto extends AsyncTask<Void, Void, Void> {

    //ELIMINAR CUANDO FUNCIONE BIEN CONFIGURACIONES
    private String[] cel_remitentes; //OBTENER DE CONFIGURACION
    private String textoMsj;
    private Context context;

 //   private Boolean result = false;


    /*
    @param mensaje : String Texto que se enviará por mensaje de texto a los teléfonos indicados en teléfonos
    @param telefonos : String[] con los teléfonos a los que se les enviará el mensaje de texto
     */

    public EnviarMsjTexto(@NonNull Context context, @NonNull String mensaje, String[] telefonos) {

        this.context = context;
        this.textoMsj = mensaje;


      //  this.cel_remitentes = telefonos;
        //prueba 26/10
        Configuraciones configuraciones = new Configuraciones(context);
        this.cel_remitentes = new String[] {configuraciones.getUserCelContacts()};
      //  this.cel_remitentes = configuraciones.getUserCelContacts();

    }

    @Override
        protected void onProgressUpdate(Void... values) {

        }

    @Override
    protected Void doInBackground(Void... strings) {

     //   boolean msjEnviado;
        //TAREA PRINCIPAL
        try {

     //       String textoMsj = strings.toString();

            SmsManager smsMgr = SmsManager.getDefault();

            //Recurro a dividir el mensaje si el texto es demasiado largo

       //     Toast.makeText(getApplicationContext(),                    "length SMS: " + textoMsj.length(), Toast.LENGTH_LONG)                    .show();

            if(textoMsj.length() < 100){

                for (int i = 0; i < cel_remitentes.length; i++) {

                    smsMgr.sendTextMessage(cel_remitentes[i], null, textoMsj, null, null);

                }

            }else {


                ArrayList messageParts = smsMgr.divideMessage(textoMsj);

                for (int j = 0; j < cel_remitentes.length; j++) {

                    smsMgr.sendMultipartTextMessage(cel_remitentes[j], null, messageParts, null, null);

                }
            }

         //   msjEnviado = true;

        } catch (Exception e) {
         //   Toast.makeText(getApplicationContext(), "Error al enviar el SMS", Toast.LENGTH_LONG).show();
            e.printStackTrace();
         //   msjEnviado = false;
        }


     //   return null;
       // return msjEnviado;
        return null;
    }

    @Override
        protected void onPreExecute() {
    }



        @Override
        protected void onCancelled() {

        }
/*
    @Override
    protected void onPostExecute(Boolean result) {

    //    this.result = result;

    }
    */

  /*  public Boolean getResult() {
        return result;
    }
    */
}
