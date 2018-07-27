package com.luciayanicelli.icsalud.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.Services.AlertaVerde;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyReceiverAlertaVerde extends BroadcastReceiver {


    private String fecha;

    @Override
    public void onReceive(Context context, Intent intent)
    {
		/*Intent service1 = new Intent(context, MyAlarmService.class);
	     context.startService(service1);*/
        Log.i("App", "called receiver method");
        try{



            //Obtener fecha actual
          /*  Calendar calendarNow = Calendar.getInstance(); //fecha actual
            calendarNow.add(Calendar.DAY_OF_YEAR, -1); //fecha de ayer

            int monthDay =calendarNow.get(Calendar.DAY_OF_MONTH);
            int month = calendarNow.get(Calendar.MONTH) + 1; //Calendar.MONTH entrega del 0 al 11 - por eso sumo 1
            int year = calendarNow.get(Calendar.YEAR);

            String fecha = String.valueOf(monthDay) + "-" + String.valueOf(month) + "-" + String.valueOf(year); //fecha de hoy en formato DD-MM-AAAA
*/

            //Obtener fecha de hoy
      /*      FechaActual fechaActual = new FechaActual(); //formato AAAA-MM-DD HH:MM:SS
            try {
                String fechaHora = fechaActual.execute().get();
                String[] splitFecha = fechaHora.split(" ");
                fecha = splitFecha[0]; //obtengo sólo la fecha sin hora

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
           */

            //obtener fecha de ayer
            String fecha;
            Calendar calendarAyer = Calendar.getInstance();
            calendarAyer.add(Calendar.DAY_OF_YEAR, -1);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(JSON_CONSTANTS.DATE_TIME_FORMAT);
         //   fecha = simpleDateFormat.format(calendarAyer.getTime()).split(" ")[0];
            //30/05/18
            fecha = simpleDateFormat.format(calendarAyer.getTime());

            AlertaVerde alertaVerde1 = new AlertaVerde(fecha,
                    AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE,
                    AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO,
                    1,
                     context);
            alertaVerde1.execute();

       /*     AlertaVerde alertaVerde2 = new AlertaVerde(fecha,
                    AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE,
                    AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA,
                    1,
                    context);
            alertaVerde2.execute();

            AlertaVerde alertaVerde3 = new AlertaVerde(fecha,
                    AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE,
                    AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS,
                    3,
                    context);
            alertaVerde3.execute();
*/
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
