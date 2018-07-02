package com.luciayanicelli.icsalud.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.Notifications.MyReceiver;
import com.luciayanicelli.icsalud.Notifications.MyReceiverAlertaVerde;
import com.luciayanicelli.icsalud.Notifications.MyReceiverCONSEJO_SALUDABLE;
import com.luciayanicelli.icsalud.Notifications.MyReceiverENCUESTAS;
import com.luciayanicelli.icsalud.Notifications.MyReceiverMEDICAMENTOS;
import com.luciayanicelli.icsalud.Notifications.MyReceiverPA;
import com.luciayanicelli.icsalud.Notifications.MyReceiverSINTOMAS;
import com.luciayanicelli.icsalud.Services.AlertaVerdeJobService;
import com.luciayanicelli.icsalud.Services.Constants;
import com.luciayanicelli.icsalud.Services.EnviarDatosServidorJobService;
import com.luciayanicelli.icsalud.Services.GenerarEmailJobService;
import com.luciayanicelli.icsalud.Services.GetContactsJobService;
import com.luciayanicelli.icsalud.Services.ServiceEnviarDatosServidor;
import com.luciayanicelli.icsalud.Services.ServiceGenerarEmail;
import com.luciayanicelli.icsalud.Services.ServiceGetContacts;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by LuciaYanicelli on 6/9/2017.
 *
 * SetearAlarma setearAlarma = new SetearAlarma(context, parametro);
 setearAlarma.execute();
 */

public class SetearAlarma extends AsyncTask<Void, Void, Void> {

    private String horarioPAFC, horarioPESO, horarioSINTOMAS, horarioCONSEJO_SALUDABLE, horarioMEDICAMENTOS;
    private String horarioEnviarMailAlertas, horarioEnviarMailMediciones, horarioEnviarDatosServidor, horarioEnviarMailJugadas;
    private String horarioAlertaVerde, horariogetContacts, horarioEncuestas;

    private Configuraciones configuraciones;

    private Context context;
    private String parametro;


    private static int JOB_ID_GENERAR_EMAIL_ALERTAS = 74123;
    private static int JOB_ID_GENERAR_EMAIL_MEDICIONES = 12369;
    private static int JOB_ID_GENERAR_EMAIL_JUGADAS = 98741;
    private static int JOB_ID_AV = 741238569;
    private static int JOB_ID_CONTACTS = 98741236;
    private static int JOB_ID_SERVIDOR = 159357;



    public SetearAlarma(Context context, String parametro){

        this.context = context;
        this.parametro = parametro;

    }




    @Override
    protected void onProgressUpdate(Void... values) {

    }

    @Override
    protected Void doInBackground(Void... voids) {

        try{


        ///LEO PREFERENCIAS
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        horarioPAFC = sharedPref.getString(Constants.KEY_PREF_HORARIO_PA_FC, Constants.DEFAULT_HOUR_MEDICIONES);
        horarioPESO = sharedPref.getString(Constants.KEY_PREF_HORARIO_PESO, Constants.DEFAULT_HOUR_MEDICIONES);
        horarioSINTOMAS = sharedPref.getString(Constants.KEY_PREF_HORARIO_SINTOMAS, Constants.DEFAULT_HOUR_MEDICIONES);
        horarioCONSEJO_SALUDABLE = sharedPref.getString(Constants.KEY_PREF_HORARIO_CONSEJO_SALUDABLE, Constants.DEFAULT_HOUR_CONSEJO_SALUDABLE);

            configuraciones = new Configuraciones(context);

        horarioEnviarMailAlertas = configuraciones.getHorarioEnviarEmailAlertas();
        horarioEnviarMailMediciones = configuraciones.getHorarioEnviarEmailMediciones();
        horarioEnviarMailJugadas = configuraciones.getHorarioEnviarEmailJugadas();

        horarioAlertaVerde = configuraciones.getHorarioAlertaVerde();

        horarioMEDICAMENTOS = configuraciones.getHorarioMedicamentos();

        horarioEnviarDatosServidor = configuraciones.getHorarioEnviarDatosServidor();

        horariogetContacts = configuraciones.getHorarioGetContacts();

        horarioEncuestas = "8:30";




            switch (parametro){
            case Constants.PARAMETRO_PESO:

                //NOTIFICACIONES
                ///http://karanbalkar.com/2013/07/tutorial-41-using-alarmmanager-and-broadcastreceiver-in-android/
                Calendar calendarPESO = Calendar.getInstance();

                //Obtengo la hora y minutos del recordatorio para el peso
                int horaPeso = getHour(horarioPESO);
                int minPeso = getMinute(horarioPESO);

                //   Toast.makeText(context, "Time: " + String.valueOf(horaPeso) + ":" + String.valueOf(minPeso),Toast.LENGTH_LONG).show();

                calendarPESO.set(Calendar.HOUR_OF_DAY, horaPeso);
                calendarPESO.set(Calendar.MINUTE, minPeso);
                calendarPESO.set(Calendar.SECOND, 0);

                // Check we aren't setting it in the past which would trigger it to fire instantly
                if(calendarPESO.getTimeInMillis() < System.currentTimeMillis()) {
                    calendarPESO.add(Calendar.DAY_OF_YEAR, 1);
                }

                //Crea el Intent para las alarmas del PESO
                Intent myIntent = new Intent(context, MyReceiver.class);
                //   myIntent.putExtra("parametro", "peso");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent,0);

                AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        /*
        void setRepeating (int type,
                long triggerAtMillis,
                long intervalMillis,
                PendingIntent operation)
        Parameters
        type	int: type of alarm. Value is RTC_WAKEUP, RTC, ELAPSED_REALTIME_WAKEUP or ELAPSED_REALTIME.
        triggerAtMillis	long: time in milliseconds that the alarm should first go off, using the appropriate clock (depending on the alarm type).
        intervalMillis	long: interval in milliseconds between subsequent repeats of the alarm.
        operation	PendingIntent: Action to perform when the alarm goes off; typically comes from IntentSender.getBroadcast().
         */
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendarPESO.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


                break;

            case Constants.PARAMETRO_PAFC:

                //PARA LA PRESION ARTERIAL Y FRECUENCIA CARDIACA
                //NOTIFICACIONES
                ///http://karanbalkar.com/2013/07/tutorial-41-using-alarmmanager-and-broadcastreceiver-in-android/
                Calendar calendarPA = Calendar.getInstance();

                //Obtengo la hora y minutos del recordatorio para el peso
                int horaPA = getHour(horarioPAFC);
                int minPA = getMinute(horarioPAFC);

                //   Toast.makeText(context, "Time: " + String.valueOf(horaPeso) + ":" + String.valueOf(minPeso),Toast.LENGTH_LONG).show();

                calendarPA.set(Calendar.HOUR_OF_DAY, horaPA);
                calendarPA.set(Calendar.MINUTE, minPA);
                calendarPA.set(Calendar.SECOND, 0);

                // Check we aren't setting it in the past which would trigger it to fire instantly
                if(calendarPA.getTimeInMillis() < System.currentTimeMillis()) {
                    calendarPA.add(Calendar.DAY_OF_YEAR, 1);
                }

                //Crea el Intent para las alarmas del PESO
                Intent myIntentPA = new Intent(context, MyReceiverPA.class);
                PendingIntent pendingIntentPA = PendingIntent.getBroadcast(context, 0, myIntentPA,0);

                AlarmManager alarmManagerPA = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        /*
        void setRepeating (int type,
                long triggerAtMillis,
                long intervalMillis,
                PendingIntent operation)
        Parameters
        type	int: type of alarm. Value is RTC_WAKEUP, RTC, ELAPSED_REALTIME_WAKEUP or ELAPSED_REALTIME.
        triggerAtMillis	long: time in milliseconds that the alarm should first go off, using the appropriate clock (depending on the alarm type).
        intervalMillis	long: interval in milliseconds between subsequent repeats of the alarm.
        operation	PendingIntent: Action to perform when the alarm goes off; typically comes from IntentSender.getBroadcast().
         */
                alarmManagerPA.setRepeating(AlarmManager.RTC_WAKEUP, calendarPA.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentPA);


                break;

            case Constants.PARAMETRO_SINTOMAS:

                //NOTIFICACIONES
                ///http://karanbalkar.com/2013/07/tutorial-41-using-alarmmanager-and-broadcastreceiver-in-android/
                Calendar calendarSINTOMAS = Calendar.getInstance();

                //Obtengo la hora y minutos del recordatorio para el peso
                int horaSINTOMAS = getHour(horarioSINTOMAS);
                int minSINTOMAS = getMinute(horarioSINTOMAS);

                calendarSINTOMAS.set(Calendar.HOUR_OF_DAY, horaSINTOMAS);
                calendarSINTOMAS.set(Calendar.MINUTE, minSINTOMAS);
                calendarSINTOMAS.set(Calendar.SECOND, 0);

                // Check we aren't setting it in the past which would trigger it to fire instantly
                if(calendarSINTOMAS.getTimeInMillis() < System.currentTimeMillis()) {
                    calendarSINTOMAS.add(Calendar.DAY_OF_YEAR, 1);
                }

                //Crea el Intent para las alarmas del PESO
                Intent myIntentSINTOMAS = new Intent(context, MyReceiverSINTOMAS.class);
                PendingIntent pendingIntentSINTOMAS = PendingIntent.getBroadcast(context, 0, myIntentSINTOMAS,0);
                AlarmManager alarmManagerSINTOMAS = (AlarmManager)context.getSystemService(ALARM_SERVICE);

                alarmManagerSINTOMAS.setRepeating(AlarmManager.RTC_WAKEUP, calendarSINTOMAS.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentSINTOMAS);

                  /*
        void setRepeating (int type,
                long triggerAtMillis,
                long intervalMillis,
                PendingIntent operation)
        Parameters
        type	int: type of alarm. Value is RTC_WAKEUP, RTC, ELAPSED_REALTIME_WAKEUP or ELAPSED_REALTIME.
        triggerAtMillis	long: time in milliseconds that the alarm should first go off, using the appropriate clock (depending on the alarm type).
        intervalMillis	long: interval in milliseconds between subsequent repeats of the alarm.
        operation	PendingIntent: Action to perform when the alarm goes off; typically comes from IntentSender.getBroadcast().
         */

                break;

            case Constants.CONSEJO_SALUDABLE:

                //NOTIFICACIONES
                ///http://karanbalkar.com/2013/07/tutorial-41-using-alarmmanager-and-broadcastreceiver-in-android/
                Calendar calendarCONSEJO_SALUDABLE = Calendar.getInstance();

                //Obtengo la hora y minutos del recordatorio
                int horaCS = getHour(horarioCONSEJO_SALUDABLE);
                int minCS = getMinute(horarioCONSEJO_SALUDABLE);

                //   Toast.makeText(context, "Time: " + String.valueOf(horaPeso) + ":" + String.valueOf(minPeso),Toast.LENGTH_LONG).show();

                calendarCONSEJO_SALUDABLE.set(Calendar.HOUR_OF_DAY, horaCS);
                calendarCONSEJO_SALUDABLE.set(Calendar.MINUTE, minCS);
                calendarCONSEJO_SALUDABLE.set(Calendar.SECOND, 0);

                //Crea el Intent para las alarmas del PESO
                Intent myIntentCS = new Intent(context, MyReceiverCONSEJO_SALUDABLE.class);
                //   myIntent.putExtra("parametro", "peso");
                PendingIntent pendingIntentCS = PendingIntent.getBroadcast(context, 0, myIntentCS,0);

                AlarmManager alarmManagerCS = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        /*
        void setRepeating (int type,
                long triggerAtMillis,
                long intervalMillis,
                PendingIntent operation)
        Parameters
        type	int: type of alarm. Value is RTC_WAKEUP, RTC, ELAPSED_REALTIME_WAKEUP or ELAPSED_REALTIME.
        triggerAtMillis	long: time in milliseconds that the alarm should first go off, using the appropriate clock (depending on the alarm type).
        intervalMillis	long: interval in milliseconds between subsequent repeats of the alarm.
        operation	PendingIntent: Action to perform when the alarm goes off; typically comes from IntentSender.getBroadcast().
         */
                alarmManagerCS.setRepeating(AlarmManager.RTC_WAKEUP,
                        calendarCONSEJO_SALUDABLE.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntentCS);


                break;

            case Constants.PARAMETRO_GENERAR_EMAIL_ALERTAS:

                //NOTIFICACIONES
                ///http://karanbalkar.com/2013/07/tutorial-41-using-alarmmanager-and-broadcastreceiver-in-android/
                Calendar calendarEAlertas = Calendar.getInstance();

                //Obtengo la hora y minutos del recordatorio para el peso
                int horaEAlertas = getHour(horarioEnviarMailAlertas);
                int minEAlertas = getMinute(horarioEnviarMailAlertas);

                //   Toast.makeText(context, "Time: " + String.valueOf(horaPeso) + ":" + String.valueOf(minPeso),Toast.LENGTH_LONG).show();

                calendarEAlertas.set(Calendar.HOUR_OF_DAY, horaEAlertas);
                calendarEAlertas.set(Calendar.MINUTE, minEAlertas);
                calendarEAlertas.set(Calendar.SECOND, 0);

                // Check we aren't setting it in the past which would trigger it to fire instantly
                if(calendarEAlertas.getTimeInMillis() < System.currentTimeMillis()) {
                    calendarEAlertas.add(Calendar.DAY_OF_YEAR, 1);
                }


                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    //Crea el Intent para las alarmas del PESO
                    //   Intent myIntentEAlertas = new Intent(context, MyReceiverGenerarEmail.class);
                    Intent myIntentEAlertas = new Intent(context, ServiceGenerarEmail.class);
                    myIntentEAlertas.setAction(Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE);
                    //   myIntent.putExtra("parametro", "peso");
                    //  PendingIntent pendingIntentEAlertas = PendingIntent.getBroadcast(context, 0, myIntentEAlertas,0);
                    PendingIntent pendingIntentEAlertas = PendingIntent.getService(context, 0, myIntentEAlertas, 0);

                    AlarmManager alarmManagerEAlertas = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        /*
        void setRepeating (int type,
                long triggerAtMillis,
                long intervalMillis,
                PendingIntent operation)
        Parameters
        type	int: type of alarm. Value is RTC_WAKEUP, RTC, ELAPSED_REALTIME_WAKEUP or ELAPSED_REALTIME.
        triggerAtMillis	long: time in milliseconds that the alarm should first go off, using the appropriate clock (depending on the alarm type).
        intervalMillis	long: interval in milliseconds between subsequent repeats of the alarm.
        operation	PendingIntent: Action to perform when the alarm goes off; typically comes from IntentSender.getBroadcast().
         */
                    alarmManagerEAlertas.setRepeating(AlarmManager.RTC_WAKEUP, calendarEAlertas.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentEAlertas);

                }else{

                    scheduleJobGenerarEmail(context, Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE, AlarmManager.INTERVAL_DAY, JOB_ID_GENERAR_EMAIL_ALERTAS);
                }

                break;

            case Constants.PARAMETRO_GENERAR_EMAIL_MEDICIONES:

                    //NOTIFICACIONES
                    ///http://karanbalkar.com/2013/07/tutorial-41-using-alarmmanager-and-broadcastreceiver-in-android/
                    Calendar calendarEMediciones = Calendar.getInstance();

                    //Obtengo la hora y minutos del recordatorio para el peso
                    int horaEMediciones = getHour(horarioEnviarMailMediciones);
                    int minEMediciones = getMinute(horarioEnviarMailMediciones);

                    //   Toast.makeText(context, "Time: " + String.valueOf(horaPeso) + ":" + String.valueOf(minPeso),Toast.LENGTH_LONG).show();

                    calendarEMediciones.set(Calendar.HOUR_OF_DAY, horaEMediciones);
                    calendarEMediciones.set(Calendar.MINUTE, minEMediciones);
                    calendarEMediciones.set(Calendar.SECOND, 0);

                    // Check we aren't setting it in the past which would trigger it to fire instantly
                    if(calendarEMediciones.getTimeInMillis() < System.currentTimeMillis()) {
                        calendarEMediciones.add(Calendar.DAY_OF_YEAR, 3);
                    }

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    //Crea el Intent para las alarmas del PESO
                    //    Intent myIntentEMediciones = new Intent(context, MyReceiverGenerarEmail.class);
                    Intent myIntentEMediciones = new Intent(context, ServiceGenerarEmail.class);
                    myIntentEMediciones.setAction(Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE_MEDICIONES);
                    PendingIntent pendingIntentEMediciones = PendingIntent.getService(context, 0, myIntentEMediciones, 0);
                    //        PendingIntent pendingIntentEMediciones = PendingIntent.getBroadcast(context, 0, myIntentEMediciones,0);

                    AlarmManager alarmManagerEMediciones = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        /*
        void setRepeating (int type,
                long triggerAtMillis,
                long intervalMillis,
                PendingIntent operation)
        Parameters
        type	int: type of alarm. Value is RTC_WAKEUP, RTC, ELAPSED_REALTIME_WAKEUP or ELAPSED_REALTIME.
        triggerAtMillis	long: time in milliseconds that the alarm should first go off, using the appropriate clock (depending on the alarm type).
        intervalMillis	long: interval in milliseconds between subsequent repeats of the alarm.
        operation	PendingIntent: Action to perform when the alarm goes off; typically comes from IntentSender.getBroadcast().
         */
                    alarmManagerEMediciones.setRepeating(AlarmManager.RTC_WAKEUP, calendarEMediciones.getTimeInMillis(), 7 * AlarmManager.INTERVAL_DAY, pendingIntentEMediciones);

                }else{

                        scheduleJobGenerarEmail(context, Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE_MEDICIONES, 7 * AlarmManager.INTERVAL_DAY, JOB_ID_GENERAR_EMAIL_MEDICIONES);
                }

                    break;

                case Constants.PARAMETRO_GENERAR_EMAIL_JUGADAS:

                    //NOTIFICACIONES
                    ///http://karanbalkar.com/2013/07/tutorial-41-using-alarmmanager-and-broadcastreceiver-in-android/
                    Calendar calendarEJugadas = Calendar.getInstance();

                    //Obtengo la hora y minutos del recordatorio para el peso
                    int horaEJugadas = getHour(horarioEnviarMailJugadas);
                    int minEJugadas = getMinute(horarioEnviarMailJugadas);

                    //   Toast.makeText(context, "Time: " + String.valueOf(horaPeso) + ":" + String.valueOf(minPeso),Toast.LENGTH_LONG).show();

                    calendarEJugadas.set(Calendar.HOUR_OF_DAY, horaEJugadas);
                    calendarEJugadas.set(Calendar.MINUTE, minEJugadas);
                    calendarEJugadas.set(Calendar.SECOND, 0);

                    // Check we aren't setting it in the past which would trigger it to fire instantly
                    if(calendarEJugadas.getTimeInMillis() < System.currentTimeMillis()) {
                        calendarEJugadas.add(Calendar.DAY_OF_YEAR, 3);
                    }

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        //Crea el Intent para las alarmas del PESO
                        //    Intent myIntentEMediciones = new Intent(context, MyReceiverGenerarEmail.class);
                        Intent myIntentEJugadas = new Intent(context, ServiceGenerarEmail.class);
                        myIntentEJugadas.setAction(Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE_JUGADAS);
                        PendingIntent pendingIntentEJugadas = PendingIntent.getService(context, 0, myIntentEJugadas, 0);
                        //        PendingIntent pendingIntentEMediciones = PendingIntent.getBroadcast(context, 0, myIntentEMediciones,0);

                        AlarmManager alarmManagerEJugadas = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        /*
        void setRepeating (int type,
                long triggerAtMillis,
                long intervalMillis,
                PendingIntent operation)
        Parameters
        type	int: type of alarm. Value is RTC_WAKEUP, RTC, ELAPSED_REALTIME_WAKEUP or ELAPSED_REALTIME.
        triggerAtMillis	long: time in milliseconds that the alarm should first go off, using the appropriate clock (depending on the alarm type).
        intervalMillis	long: interval in milliseconds between subsequent repeats of the alarm.
        operation	PendingIntent: Action to perform when the alarm goes off; typically comes from IntentSender.getBroadcast().
         */
                        alarmManagerEJugadas.setRepeating(AlarmManager.RTC_WAKEUP, calendarEJugadas.getTimeInMillis(), 30 * AlarmManager.INTERVAL_DAY, pendingIntentEJugadas);

                    }else{

                        scheduleJobGenerarEmail(context, Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE_JUGADAS, 30 * AlarmManager.INTERVAL_DAY, JOB_ID_GENERAR_EMAIL_JUGADAS);
                    }

                    break;

            case Constants.PARAMETRO_ALERTA_VERDE:

                //NOTIFICACIONES
                ///http://karanbalkar.com/2013/07/tutorial-41-using-alarmmanager-and-broadcastreceiver-in-android/
                Calendar calendarAlertaVerde = Calendar.getInstance();

                //Obtengo la hora y minutos del recordatorio para el peso
                int horaAlertaVerde = getHour(horarioAlertaVerde);
                int minAlertaVerde = getMinute(horarioAlertaVerde);

                //   Toast.makeText(context, "Time: " + String.valueOf(horaPeso) + ":" + String.valueOf(minPeso),Toast.LENGTH_LONG).show();

                calendarAlertaVerde.set(Calendar.HOUR_OF_DAY, horaAlertaVerde);
                calendarAlertaVerde.set(Calendar.MINUTE, minAlertaVerde);
                calendarAlertaVerde.set(Calendar.SECOND, 0);

                // Check we aren't setting it in the past which would trigger it to fire instantly
                if(calendarAlertaVerde.getTimeInMillis() < System.currentTimeMillis()) {
                    calendarAlertaVerde.add(Calendar.DAY_OF_YEAR, 2);
                }

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    //Crea el Intent para las alarmas del PESO
                    Intent myIntentAlertaVerde = new Intent(context, MyReceiverAlertaVerde.class);
                    //  myIntentAlertaVerde.setAction(Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE);
                    //   myIntent.putExtra("parametro", "peso");
                    PendingIntent pendingIntentAlertaVerde = PendingIntent.getBroadcast(context, 0, myIntentAlertaVerde, 0);

                    AlarmManager alarmManagerAlertaVerde = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        /*
        void setRepeating (int type,
                long triggerAtMillis,
                long intervalMillis,
                PendingIntent operation)
        Parameters
        type	int: type of alarm. Value is RTC_WAKEUP, RTC, ELAPSED_REALTIME_WAKEUP or ELAPSED_REALTIME.
        triggerAtMillis	long: time in milliseconds that the alarm should first go off, using the appropriate clock (depending on the alarm type).
        intervalMillis	long: interval in milliseconds between subsequent repeats of the alarm.
        operation	PendingIntent: Action to perform when the alarm goes off; typically comes from IntentSender.getBroadcast().
         */
                    alarmManagerAlertaVerde.setRepeating(AlarmManager.RTC_WAKEUP, calendarAlertaVerde.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentAlertaVerde);

                }else {

                    scheduleJobAlertaVerde(context);
                }

                break;

            case Constants.MEDICAMENTOS:

                //NOTIFICACIONES
                ///http://karanbalkar.com/2013/07/tutorial-41-using-alarmmanager-and-broadcastreceiver-in-android/
                Calendar calendarMEDICAMENTOS = Calendar.getInstance();

                //Obtengo la hora y minutos del recordatorio
                int horaMEDICAMENTOS = getHour(horarioMEDICAMENTOS);
                int minMEDICAMENTOS = getMinute(horarioMEDICAMENTOS);

                //   Toast.makeText(context, "Time: " + String.valueOf(horaPeso) + ":" + String.valueOf(minPeso),Toast.LENGTH_LONG).show();

                calendarMEDICAMENTOS.set(Calendar.HOUR_OF_DAY, horaMEDICAMENTOS);
                calendarMEDICAMENTOS.set(Calendar.MINUTE, minMEDICAMENTOS);
                calendarMEDICAMENTOS.set(Calendar.SECOND, 0);

                // Check we aren't setting it in the past which would trigger it to fire instantly
                if(calendarMEDICAMENTOS.getTimeInMillis() < System.currentTimeMillis()) {
                    calendarMEDICAMENTOS.add(Calendar.DAY_OF_YEAR, 7);
                }



                //Crea el Intent para las alarmas del PESO
                Intent myIntentMEDICAMENTOS = new Intent(context, MyReceiverMEDICAMENTOS.class);
                //   myIntent.putExtra("parametro", "peso");
                PendingIntent pendingIntentMEDICAMENTOS = PendingIntent.getBroadcast(context, 0, myIntentMEDICAMENTOS,0);

                AlarmManager alarmManagerMEDICAMENTOS = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        /*
        void setRepeating (int type,
                long triggerAtMillis,
                long intervalMillis,
                PendingIntent operation)
        Parameters
        type	int: type of alarm. Value is RTC_WAKEUP, RTC, ELAPSED_REALTIME_WAKEUP or ELAPSED_REALTIME.
        triggerAtMillis	long: time in milliseconds that the alarm should first go off, using the appropriate clock (depending on the alarm type).
        intervalMillis	long: interval in milliseconds between subsequent repeats of the alarm.
        operation	PendingIntent: Action to perform when the alarm goes off; typically comes from IntentSender.getBroadcast().
         */
                alarmManagerMEDICAMENTOS.setRepeating(AlarmManager.RTC_WAKEUP,
                        calendarMEDICAMENTOS.getTimeInMillis(),
                        7*AlarmManager.INTERVAL_DAY, pendingIntentMEDICAMENTOS);



                break;

                case Constants.PARAMETRO_GET_CONTACTS:

                    //NOTIFICACIONES
                    ///http://karanbalkar.com/2013/07/tutorial-41-using-alarmmanager-and-broadcastreceiver-in-android/
                    Calendar calendarCONTACTS = Calendar.getInstance();

                    //Obtengo la hora y minutos del recordatorio
                    int horaCONTACTS = getHour(horariogetContacts);
                    int minCONTACTS = getMinute(horariogetContacts);

                    //   Toast.makeText(context, "Time: " + String.valueOf(horaPeso) + ":" + String.valueOf(minPeso),Toast.LENGTH_LONG).show();

                    calendarCONTACTS.set(Calendar.HOUR_OF_DAY, horaCONTACTS);
                    calendarCONTACTS.set(Calendar.MINUTE, minCONTACTS);
                    calendarCONTACTS.set(Calendar.SECOND, 0);

                    // Check we aren't setting it in the past which would trigger it to fire instantly
                    if(calendarCONTACTS.getTimeInMillis() < System.currentTimeMillis()) {
                        calendarCONTACTS.add(Calendar.DAY_OF_YEAR, 1); //primera búsqueda en 1
                    }


                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                        Intent myIntentCONTACTS = new Intent(context, ServiceGetContacts.class);
                        //  myIntentServidor.setAction(Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE_MEDICIONES);
                        PendingIntent pendingIntentCONTACTS = PendingIntent.getService(context, 0, myIntentCONTACTS, 0);
                        //        PendingIntent pendingIntentEMediciones = PendingIntent.getBroadcast(context, 0, myIntentEMediciones,0);

                        AlarmManager alarmManagerCONTACTS = (AlarmManager) context.getSystemService(ALARM_SERVICE);


                    //  alarmManagerCONTACTS.setRepeating(AlarmManager.RTC_WAKEUP,  calendarCONTACTS.getTimeInMillis(),  , pendingIntentCONTACTS); //repita la búsqueda cada 10 días

                    }else{

                        scheduleJobContacts(context);

                    }


                    break;

                case Constants.PARAMETRO_ENVIAR_DATOS_SERVIDOR:

                    //NOTIFICACIONES
                    ///http://karanbalkar.com/2013/07/tutorial-41-using-alarmmanager-and-broadcastreceiver-in-android/
                    Calendar calendarServidor = Calendar.getInstance();

                    //Obtengo la hora y minutos del recordatorio para el peso
                    int horaServidor = getHour(horarioEnviarDatosServidor);
                    int minServidor = getMinute(horarioEnviarDatosServidor);

                    //   Toast.makeText(context, "Time: " + String.valueOf(horaPeso) + ":" + String.valueOf(minPeso),Toast.LENGTH_LONG).show();

                    calendarServidor.set(Calendar.HOUR_OF_DAY, horaServidor);
                    calendarServidor.set(Calendar.MINUTE, minServidor);
                    calendarServidor.set(Calendar.SECOND, 0);

                    // Check we aren't setting it in the past which would trigger it to fire instantly
                    if(calendarServidor.getTimeInMillis() < System.currentTimeMillis()) {
                        calendarServidor.add(Calendar.DAY_OF_YEAR, 1);
                    }



                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                        //Crea el Intent para las alarmas del PESO
                        //    Intent myIntentEMediciones = new Intent(context, MyReceiverGenerarEmail.class);
                        Intent myIntentServidor = new Intent(context, ServiceEnviarDatosServidor.class);
                        //  myIntentServidor.setAction(Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE_MEDICIONES);
                        PendingIntent pendingIntentServidor = PendingIntent.getService(context, 0, myIntentServidor, 0);
                        //        PendingIntent pendingIntentEMediciones = PendingIntent.getBroadcast(context, 0, myIntentEMediciones,0);

                        AlarmManager alarmManagerServidor = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        /*
        void setRepeating (int type,
                long triggerAtMillis,
                long intervalMillis,
                PendingIntent operation)
        Parameters
        type	int: type of alarm. Value is RTC_WAKEUP, RTC, ELAPSED_REALTIME_WAKEUP or ELAPSED_REALTIME.
        triggerAtMillis	long: time in milliseconds that the alarm should first go off, using the appropriate clock (depending on the alarm type).
        intervalMillis	long: interval in milliseconds between subsequent repeats of the alarm.
        operation	PendingIntent: Action to perform when the alarm goes off; typically comes from IntentSender.getBroadcast().
         */
                        alarmManagerServidor.setRepeating(AlarmManager.RTC_WAKEUP, calendarServidor.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentServidor);

                    }else{
                        scheduleJobEnviarDatosServidor(context);
                    }

                    break;


                case Constants.PARAMETRO_ENCUESTAS:

                    //NOTIFICACIONES
                    ///http://karanbalkar.com/2013/07/tutorial-41-using-alarmmanager-and-broadcastreceiver-in-android/
                    Calendar calendarENCUESTAS = Calendar.getInstance();

                    //Obtengo la hora y minutos del recordatorio para el peso
                    int horaENCUESTAS = getHour(horarioEncuestas);
                    int minENCUESTAS = getMinute(horarioEncuestas);

                    //   Toast.makeText(context, "Time: " + String.valueOf(horaPeso) + ":" + String.valueOf(minPeso),Toast.LENGTH_LONG).show();

                    calendarENCUESTAS.set(Calendar.HOUR_OF_DAY, horaENCUESTAS);
                    calendarENCUESTAS.set(Calendar.MINUTE, minENCUESTAS);
                    calendarENCUESTAS.set(Calendar.SECOND, 0);

                    //Crea el Intent para las alarmas del PESO
                    Intent myIntentENCUESTAS = new Intent(context, MyReceiverENCUESTAS.class);
                    //   myIntent.putExtra("parametro", "peso");
                    PendingIntent pendingIntentENCUESTAS = PendingIntent.getBroadcast(context,
                            0, myIntentENCUESTAS,0);

                    AlarmManager alarmManagerENCUESTAS = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        /*
        void setRepeating (int type,
                long triggerAtMillis,
                long intervalMillis,
                PendingIntent operation)
        Parameters
        type	int: type of alarm. Value is RTC_WAKEUP, RTC, ELAPSED_REALTIME_WAKEUP or ELAPSED_REALTIME.
        triggerAtMillis	long: time in milliseconds that the alarm should first go off, using the appropriate clock (depending on the alarm type).
        intervalMillis	long: interval in milliseconds between subsequent repeats of the alarm.
        operation	PendingIntent: Action to perform when the alarm goes off; typically comes from IntentSender.getBroadcast().
         */
                    alarmManagerENCUESTAS.setRepeating(AlarmManager.RTC_WAKEUP, calendarENCUESTAS.getTimeInMillis(), 90*AlarmManager.INTERVAL_DAY, pendingIntentENCUESTAS);


                    break;


            default:
                break;
        }

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public static void scheduleJobEnviarDatosServidor(Context context) {
            ComponentName serviceComponent = new ComponentName(context, EnviarDatosServidorJobService.class);
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID_SERVIDOR, serviceComponent);
            builder.setPeriodic(AlarmManager.INTERVAL_DAY); //UN DÍA ? 86400000
         //   builder.setMinimumLatency(1000); //1segundo
         //   builder.setOverrideDeadline(1*60*60*1000); //1hora * 60 minutos * 60 segundos * 1000ms

            //new
            //Network type (metered/unmetered)
            //If your job requires network access, you must include this condition. You can specify a metered or unmetered network, or any type of network. But not calling this when building your JobInfo means the system will assume you do not need any network access and you will not be able to contact your server.
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

            //Persistent
            ///Any work that needs to be persisted across a reboot can be marked as such here. Once the device reboots, the job will be rescheduled according to the conditions. (Note that your app needs the RECEIVE_BOOT_COMPLETED permission for this to work, though.)
            builder.setPersisted(Boolean.TRUE);
            //new

            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());

        }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJobGenerarEmail(Context context, String extra, long periodo, int jobId) {

        PersistableBundle pb = new PersistableBundle();
        pb.putString("extra", extra);
        pb.putLong("periodo", periodo);
        pb.putInt("jobId", jobId);

        ComponentName serviceComponentGE = new ComponentName(context, GenerarEmailJobService.class);
        JobInfo.Builder builderGE = new JobInfo.Builder(jobId, serviceComponentGE);
        builderGE.setPeriodic(periodo); //UN DÍA ? 86400000
        //   builder.setMinimumLatency(1000); //1segundo
        //   builder.setOverrideDeadline(1*60*60*1000); //1hora * 60 minutos * 60 segundos * 1000ms

        //new
        //Network type (metered/unmetered)
        //If your job requires network access, you must include this condition. You can specify a metered or unmetered network, or any type of network. But not calling this when building your JobInfo means the system will assume you do not need any network access and you will not be able to contact your server.
        builderGE.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

        //Persistent
        ///Any work that needs to be persisted across a reboot can be marked as such here. Once the device reboots, the job will be rescheduled according to the conditions. (Note that your app needs the RECEIVE_BOOT_COMPLETED permission for this to work, though.)
        builderGE.setPersisted(Boolean.TRUE);
        //new

        builderGE.setExtras(pb);

        JobScheduler jobSchedulerGE = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobSchedulerGE.schedule(builderGE.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJobContacts(Context context) {
        ComponentName serviceComponentC = new ComponentName(context, GetContactsJobService.class);
        JobInfo.Builder builderC = new JobInfo.Builder(JOB_ID_CONTACTS, serviceComponentC);
    //    builderC.setPeriodic(AlarmManager.INTERVAL_FIFTEEN_MINUTES); //UN DÍA ? 86400000
        //new
        //Network type (metered/unmetered)
        //If your job requires network access, you must include this condition. You can specify a metered or unmetered network, or any type of network. But not calling this when building your JobInfo means the system will assume you do not need any network access and you will not be able to contact your server.
        builderC.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

        //Persistent
        ///Any work that needs to be persisted across a reboot can be marked as such here. Once the device reboots, the job will be rescheduled according to the conditions. (Note that your app needs the RECEIVE_BOOT_COMPLETED permission for this to work, though.)
        builderC.setPersisted(Boolean.TRUE);
        //new

        JobScheduler jobSchedulerC= (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobSchedulerC.schedule(builderC.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJobAlertaVerde(Context context) {
        ComponentName serviceComponentAV = new ComponentName(context, AlertaVerdeJobService.class);
        JobInfo.Builder builderAV = new JobInfo.Builder(JOB_ID_AV, serviceComponentAV);
        builderAV.setPeriodic(AlarmManager.INTERVAL_DAY); //UN DÍA ? 86400000

        //new
        //Network type (metered/unmetered)
        //If your job requires network access, you must include this condition. You can specify a metered or unmetered network, or any type of network. But not calling this when building your JobInfo means the system will assume you do not need any network access and you will not be able to contact your server.
        builderAV.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

        //Persistent
        ///Any work that needs to be persisted across a reboot can be marked as such here. Once the device reboots, the job will be rescheduled according to the conditions. (Note that your app needs the RECEIVE_BOOT_COMPLETED permission for this to work, though.)
        builderAV.setPersisted(Boolean.TRUE);
        //new

        JobScheduler jobSchedulerAV = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobSchedulerAV.schedule(builderAV.build());

    }


    //FUNCIONES XA CONVERTIR LA HORA DE STRING A NUMEROS
    //Obtiene la hora del String time HH:MM
    public static int getHour(String time) {

        String[] pieces;

        if (time != null){
            pieces = time.split(":");

        }else {
            //PONGO VALOR POR DEFECTO EN CASO DE NO LEER LAS PREFERENCIAS
            pieces = new String[]{String.valueOf('0'), String.valueOf('0')};
        }

        return(Integer.parseInt(pieces[0]));
    }

    //Obtiene los minutos del String time HH:MM
    public static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onCancelled() {

    }

}
