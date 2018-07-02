package com.luciayanicelli.icsalud.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.luciayanicelli.icsalud.Services.Constants;
import com.luciayanicelli.icsalud.utils.ConsultarCrearNotificacionRecordatorio;

/**
 * Created by LuciaYanicelli on 5/10/2017.
 */

public class MyReceiverMEDICAMENTOS extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent)
    {
		/*Intent service1 = new Intent(context, MyAlarmService.class);
	     context.startService(service1);*/
        Log.i("App", "called receiver method");
        try{

            ConsultarCrearNotificacionRecordatorio mConsult = new ConsultarCrearNotificacionRecordatorio(context, Constants.MEDICAMENTOS);
            mConsult.execute();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
