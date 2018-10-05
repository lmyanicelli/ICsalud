package com.luciayanicelli.icsalud.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.luciayanicelli.icsalud.Services.EnviarDatosServidor_Service;


/**
 * Created by LuciaYanicelli on 23/3/2017.
 *
 * BROADCASTRECEIVER
 *
 * RECIBE LOS INTENTS
 *
 * Tareas a realizar cuando la alarma sea lanzada por Android
 *
 * NOTIFICACIONES  * https://developer.android.com/guide/topics/ui/notifiers/notifications.html
 */



public class MyReceiverEnviarDatosServidor extends BroadcastReceiver
{

    private static int mId;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i("App", "called receiver method");
        try{
            Intent intentSERVIDOR = new Intent(context, EnviarDatosServidor_Service.class);
            context.startService(intentSERVIDOR);

        }catch(Exception e){
            e.printStackTrace();
        }
    }



}
