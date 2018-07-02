package com.luciayanicelli.icsalud.utils;

import android.os.AsyncTask;

import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * ---
 * FechaActual fechaActual = new FechaActual();
 * String fecha = fechaActual.execute();
 ---

 */

public class FechaActual extends AsyncTask<Void, Void, String> {

    private Calendar calendarNow;
    private String fecha;


    public FechaActual() {

    }

    @Override
        protected void onProgressUpdate(Void... values) {

        }

    @Override
    protected String doInBackground(Void... strings) {
        //TAREA PRINCIPAL
        try {
            //Obtengo la fecha actual
         /*   calendarNow = Calendar.getInstance();

            int monthDay =calendarNow.get(Calendar.DAY_OF_MONTH);
            int month = calendarNow.get(Calendar.MONTH) + 1; //Calendar.MONTH entrega del 0 al 11 - por eso sumo 1
            int year = calendarNow.get(Calendar.YEAR);

            fecha = String.valueOf(monthDay) + "-" + String.valueOf(month) + "-" + String.valueOf(year); //fecha de hoy en formato DD-MM-AAAA
*/
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(JSON_CONSTANTS.DATE_TIME_FORMAT);
            return simpleDateFormat.format(Calendar.getInstance().getTime());


        } catch (Exception e) {
            e.printStackTrace();
        }

        return fecha;

    }

    @Override
        protected void onPreExecute() {
    }

    @Override
        protected void onCancelled() {
    }

}
