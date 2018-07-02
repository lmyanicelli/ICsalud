package com.luciayanicelli.icsalud.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.luciayanicelli.icsalud.utils.SetearAlarma;

/**
 * Created by LuciaYanicelli on 6/9/2017.
 *
 * CUANDO EL DISPOSITIVO SE APAGA Y SE VUELVE A ENCENDER, ESTE BROADCASTRECEIVER ACTIVA NUEVAMENTE LAS ALARMAS QUE SE PIERDEN AL APAGAR EL DISPOSITIVO
 *
 * https://developer.android.com/training/scheduling/alarms.html
 *
 *
 */


public class SampleBootReceiver extends BroadcastReceiver {

    private SetearAlarma saPeso, saPA, saSintomas, saConsejoSaludable, saMedicamentos,
            setearAlarmaEnviarMailAlertas, setearAlarmaEnviarMailMediciones, setearAlertaVerde,
            saEnviarDatosServidor, saGetContacts, setearAlarmaEnviarMailJugada, setearAlarmaEncuestas;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            saPeso = new SetearAlarma(context, Constants.PARAMETRO_PESO);
            saPeso.execute();

            saPA = new SetearAlarma(context, Constants.PARAMETRO_PAFC);
            saPA.execute();

            saSintomas = new SetearAlarma(context, Constants.PARAMETRO_SINTOMAS);
            saSintomas.execute();

            saMedicamentos = new SetearAlarma(context, Constants.MEDICAMENTOS);
            saMedicamentos.execute();

            saConsejoSaludable = new SetearAlarma(context, Constants.CONSEJO_SALUDABLE);
            saConsejoSaludable.execute();

            setearAlarmaEnviarMailAlertas = new SetearAlarma(context, Constants.PARAMETRO_GENERAR_EMAIL_ALERTAS);
            setearAlarmaEnviarMailAlertas.execute();

            setearAlarmaEnviarMailMediciones = new SetearAlarma(context, Constants.PARAMETRO_GENERAR_EMAIL_MEDICIONES);
            setearAlarmaEnviarMailMediciones.execute();

            setearAlertaVerde = new SetearAlarma(context, Constants.PARAMETRO_ALERTA_VERDE);
            setearAlertaVerde.execute();

            saEnviarDatosServidor = new SetearAlarma(context, Constants.PARAMETRO_ENVIAR_DATOS_SERVIDOR);
            saEnviarDatosServidor.execute();

        //    saGetContacts = new SetearAlarma(context, Constants.PARAMETRO_GET_CONTACTS);
        //    saGetContacts.execute();

            setearAlarmaEnviarMailJugada = new SetearAlarma(context, Constants.PARAMETRO_GENERAR_EMAIL_JUGADAS);
            setearAlarmaEnviarMailJugada.execute();

            setearAlarmaEncuestas = new SetearAlarma(context, Constants.PARAMETRO_ENCUESTAS);
            setearAlarmaEncuestas.execute();


        }
    }

}