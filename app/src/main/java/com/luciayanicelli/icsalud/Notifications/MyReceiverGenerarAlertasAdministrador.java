package com.luciayanicelli.icsalud.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.luciayanicelli.icsalud.Services.GenerarAlertasAdministrador_Service;


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



public class MyReceiverGenerarAlertasAdministrador extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i("App", "called receiver method");
        try{
                Intent mintent = new Intent(context, GenerarAlertasAdministrador_Service.class);
                mintent.setAction(intent.getAction());
                context.startService(mintent);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
