package com.luciayanicelli.icsalud.Services;

import android.content.Context;
import android.os.AsyncTask;

import com.luciayanicelli.icsalud.utils.PAFC;
import com.luciayanicelli.icsalud.utils.Peso;
import com.luciayanicelli.icsalud.utils.Sintomas;

/**
 * Created by LuciaYanicelli on 11/8/2017.
 *
 * Esta clase corrobora que se encuentren ingresados los datos de la fecha correspondiente, en caso contrario genera una alerta verde
 * Envía un mail al médico indicando que el paciente no cargó los datos correspondientes a la fecha indicada.
 * Guarda estas alertas en una BD
 *
 *
 * PARA UTILIZAR
 * AlertaVerde alertaVerde = new AlertaVerde(String fecha, String nombreDateTabla, String nombreTabla, int cantidadDatos, Context context);
 * alertaVerde.execute();
 */

public class AlertaVerde extends AsyncTask<Void, Void, Void> {

    private Context context;

    public AlertaVerde(Context context) {
        this.context = context;
    }

    @Override
    protected void onProgressUpdate(Void... values) {

    }

    @Override
    protected Void doInBackground(Void... strings) {

        Peso mPeso = new Peso(context);
        mPeso.alertaVerde();

        PAFC mPAFC = new PAFC(context);
        mPAFC.alertaVerde();

        Sintomas mSintomas = new Sintomas(context);
        mSintomas.alertaVerde();

        return null;

    }

    @Override
    protected void onPreExecute() {
    }


    @Override
    protected void onCancelled() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

}
