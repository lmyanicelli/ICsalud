package com.luciayanicelli.icsalud.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.luciayanicelli.icsalud.Services.Constants;
import com.luciayanicelli.icsalud.Services.ServiceEnviarDatosServidor;
import com.luciayanicelli.icsalud.utils.SetearAlarma;


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
            Intent intentSERVIDOR = new Intent(context, ServiceEnviarDatosServidor.class);
            context.startService(intentSERVIDOR);

            SetearAlarma setearAlarmaAlerta = new SetearAlarma(context, Constants.PARAMETRO_GENERAR_EMAIL_ALERTAS);
            setearAlarmaAlerta.execute();

        }catch(Exception e){
            e.printStackTrace();
        }
    }



}
