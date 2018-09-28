package com.luciayanicelli.icsalud.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.luciayanicelli.icsalud.Services.AlertaVerde;

public class MyReceiverAlertaVerde extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i("App", "MyReceiverAlertaVerde");
        try{

            AlertaVerde mAlertaVerde = new AlertaVerde(context);
            mAlertaVerde.execute();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
