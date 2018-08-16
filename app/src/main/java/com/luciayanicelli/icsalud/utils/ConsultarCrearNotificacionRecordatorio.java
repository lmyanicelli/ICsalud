package com.luciayanicelli.icsalud.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;

import com.luciayanicelli.icsalud.Activity_Autodiagnostico.Activity_Menu_Autodiagnostico;
import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.Activity_ViewConsejoSaludable;
import com.luciayanicelli.icsalud.Activity_ViewConsultaMedicamentos;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_DBHelper;
import com.luciayanicelli.icsalud.DataBase.RecordatoriosContract;
import com.luciayanicelli.icsalud.DataBase.RecordatoriosDBHelper;
import com.luciayanicelli.icsalud.R;
import com.luciayanicelli.icsalud.Services.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.IllegalFormatException;

import static android.app.Notification.PRIORITY_MAX;

/**
 * Created by LuciaYanicelli on 6/9/2017.
 *
 * SetearAlarma setearAlarma = new SetearAlarma(context, parametro);
 setearAlarma.execute();
 */

public class ConsultarCrearNotificacionRecordatorio extends AsyncTask<Void, Void, Void> {

    private static int mId = 3;
    private static String descripcionRecordatorio;
    private String horarioPAFC, horarioPESO, horarioSINTOMAS, horarioCONSEJO_SALUDABLE, horarioMEDICAMENTOS, horarioENCUESTAS;

    private Context context;
    private String parametro;
    private int contador; // controla el orden para los consejos saludables
    private Configuraciones conf;
    private int day_of_week_medicamentos;

    public ConsultarCrearNotificacionRecordatorio(Context context, String parametro){

        this.context = context;
        this.parametro = parametro;

        initChannels(context);

    }




    @Override
    protected void onProgressUpdate(Void... values) {

    }

    @Override
    protected Void doInBackground(Void... voids) {

        try{

            //Obtener fecha actual
            Calendar calendarNow = Calendar.getInstance();

            int monthDay =calendarNow.get(Calendar.DAY_OF_MONTH);
            int month = calendarNow.get(Calendar.MONTH) + 1; //Calendar.MONTH entrega del 0 al 11 - por eso sumo 1
            int year = calendarNow.get(Calendar.YEAR);

            String fecha = String.valueOf(monthDay) + "-" + String.valueOf(month) + "-" + String.valueOf(year); //fecha de hoy en formato DD-MM-AAAA


            //Acceder BD autodiagnostico
            Autodiagnostico_DBHelper dbHelperAuto = new Autodiagnostico_DBHelper(context);
            SQLiteDatabase db = dbHelperAuto.getWritableDatabase();

            String[] columns;
            String whereClause;
            String[] args;
            Cursor mCursor;

            RecordatoriosDBHelper dbHelper = new RecordatoriosDBHelper(context);
            SQLiteDatabase dbR = dbHelper.getWritableDatabase();

            String whereClauseR = RecordatoriosContract.RecordatoriosEntry.PARAMETRO + "= ?" + " and " + RecordatoriosContract.RecordatoriosEntry.FECHA + "= ?";

            String[] argsR;

            String[] columnsR;

            Cursor mCursorR;




        ///LEO PREFERENCIAS
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        horarioPAFC = sharedPref.getString(Constants.KEY_PREF_HORARIO_PA_FC, Constants.DEFAULT_HOUR_MEDICIONES);
        horarioPESO = sharedPref.getString(Constants.KEY_PREF_HORARIO_PESO, Constants.DEFAULT_HOUR_MEDICIONES);
        horarioSINTOMAS = sharedPref.getString(Constants.KEY_PREF_HORARIO_SINTOMAS, Constants.DEFAULT_HOUR_MEDICIONES);
        horarioCONSEJO_SALUDABLE = sharedPref.getString(Constants.KEY_PREF_HORARIO_CONSEJO_SALUDABLE, Constants.DEFAULT_HOUR_CONSEJO_SALUDABLE);

        horarioENCUESTAS = "8:30";
       Configuraciones configuraciones = new Configuraciones(context);

        horarioMEDICAMENTOS = configuraciones.getHorarioMedicamentos();

        day_of_week_medicamentos = configuraciones.getDayOfWeekMedicamentos();


        switch (parametro){

            case Constants.PARAMETRO_PESO:

               columns = new String[]{AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE};

               whereClause = AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE + "= ?";

               args = new String[]{fecha};

                mCursor = db.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO,
                        columns, whereClause, args, null, null, null, null );

                if(mCursor!= null & mCursor.moveToFirst()){
                    //Existen datos cargados por lo tanto no debo crear ningún recordatorio ni notificación

                }else{
                    //No existen datos cargados en el día de hoy
                    // Consultar si existe el recordatorio en la BD recordatorios


                    argsR = new String[]{Constants.PARAMETRO_PESO, fecha};

                    columnsR = new String[]{RecordatoriosContract.RecordatoriosEntry._ID};

                    mCursorR = dbR.query(true, RecordatoriosContract.RecordatoriosEntry.TABLE_NAME,
                            columnsR, whereClauseR, argsR, null, null, null, null );

                    if(mCursorR!= null & mCursorR.moveToFirst()){
                       // int id = mCursor.getInt(0);
                        //no hacer nada, porque ya existe un recordatorio con la fecha de hoy
                    }else{
                        //no existe recordatorio con la fecha de hoy

                        //CREAR NOTIFICACIÓN Y RECORDATORIO
                        notificationBuilder(context, parametro, horarioPESO);

                    }
                    mCursorR.close();
                    db.close();
                }
                mCursor.close();


                break;



            case Constants.PARAMETRO_PAFC:

                columns = new String[]{AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE};

                whereClause = AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE + "= ?";

                args = new String[]{fecha};

                mCursor = db.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA,
                        columns, whereClause, args, null, null, null, null );

                if(mCursor!= null & mCursor.moveToFirst()){
                    //Existen datos cargados por lo tanto no debo crear ningún recordatorio ni notificación

                }else{
                    //No existen datos cargados en el día de hoy
                    // Consultar si existe el recordatorio en la BD recordatorios


                    argsR = new String[]{parametro, fecha};

                    columnsR = new String[]{RecordatoriosContract.RecordatoriosEntry._ID};

                    mCursorR = dbR.query(true, RecordatoriosContract.RecordatoriosEntry.TABLE_NAME,
                            columnsR, whereClauseR, argsR, null, null, null, null );

                    if(mCursorR!= null & mCursorR.moveToFirst()){
                        // int id = mCursor.getInt(0);
                        //no hacer nada, porque ya existe un recordatorio con la fecha de hoy
                    }else{
                        //no existe recordatorio con la fecha de hoy

                        //CREAR NOTIFICACIÓN Y RECORDATORIO
                        notificationBuilderPA(context, parametro, horarioPAFC);

                    }
                    mCursorR.close();
                    db.close();
                }
                mCursor.close();

                break;

            case Constants.PARAMETRO_SINTOMAS:

                columns = new String[]{AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE,
                        AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_IDPREGUNTA};

                whereClause = AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE + "= ?";

                args = new String[]{fecha};

                mCursor = db.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS,
                        columns, whereClause, args, null, null, null, null );

                if(mCursor!= null & mCursor.getCount()== 3){
                    //Existen todos los datos cargados (3) por lo tanto no debo crear ningún recordatorio ni notificación


                }else{
                    //No existen datos cargados en el día de hoy
                    // Consultar si existe el recordatorio en la BD recordatorios


                    argsR = new String[]{parametro, fecha};

                    columnsR = new String[]{RecordatoriosContract.RecordatoriosEntry._ID};

                    mCursorR = dbR.query(true, RecordatoriosContract.RecordatoriosEntry.TABLE_NAME,
                            columnsR, whereClauseR, argsR, null, null, null, null );

                    if(mCursorR!= null & mCursorR.moveToFirst()){
                        // int id = mCursor.getInt(0);
                        //no hacer nada, porque ya existe un recordatorio con la fecha de hoy
                    }else{
                        //no existe recordatorio con la fecha de hoy

                        //CREAR NOTIFICACIÓN Y RECORDATORIO
                        notificationBuilderSINTOMAS(context, parametro, horarioSINTOMAS);

                    }
                    mCursorR.close();
                    db.close();
                }
                mCursor.close();


                break;


            case Constants.CONSEJO_SALUDABLE:

                columns = new String[]{AutodiagnosticoContract.AutodiagnosticoEntry.CONSEJO_SALUDABLE_DATE};

                whereClause = AutodiagnosticoContract.AutodiagnosticoEntry.CONSEJO_SALUDABLE_DATE+ "= ?";

                args = new String[]{fecha};

                mCursor = db.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_CONSEJO_SALUDABLE,
                        columns, whereClause, args, null, null, null, null );

                if(mCursor!= null & mCursor.moveToFirst()){
                    //Existe un consejo saludable enviado por lo tanto no debo crear ningún recordatorio ni notificación

                }else{
                        //CREAR NOTIFICACIÓN Y RECORDATORIO
                        notificationBuilderCONSEJO_SALUDABLE(context, parametro, horarioCONSEJO_SALUDABLE, fecha);

                        db.close();

                    }
                    mCursor.close();

                break;

            case Constants.MEDICAMENTOS:

                //Primero consultar si hoy es el día de la semana indicado para los recordatorios
          //      Calendar calendar = Calendar.getInstance();
          //      int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);

          //      if(day_of_week == day_of_week_medicamentos){

                    // Consultar si existe el recordatorio en la BD recordatorios
                    argsR = new String[]{Constants.MEDICAMENTOS, fecha};

                    columnsR = new String[]{RecordatoriosContract.RecordatoriosEntry._ID};

                    mCursorR = dbR.query(true, RecordatoriosContract.RecordatoriosEntry.TABLE_NAME,
                            columnsR, whereClauseR, argsR, null, null, null, null );

                    if(mCursorR!= null & mCursorR.moveToFirst()){
                        // int id = mCursor.getInt(0);
                        //no hacer nada, porque ya existe un recordatorio con la fecha de hoy
                    }else{
                        //no existe recordatorio con la fecha de hoy

                        //CREAR NOTIFICACIÓN Y RECORDATORIO
                        notificationBuilderMEDICAMENTO(context, parametro, horarioMEDICAMENTOS, fecha);

                    }
                    mCursorR.close();


                break;


            case Constants.PARAMETRO_ENCUESTAS:

                //CREAR NOTIFICACIÓN Y RECORDATORIO
                notificationBuilderEncuesta(context, parametro, horarioENCUESTAS);



                break;


            default:
                break;
        }
//cambios aqui para detectar error
        } catch (IllegalFormatException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



    //FUNCIONES XA CONVERTIR LA HORA DE STRING A NUMEROS
    //Obtiene la hora del String time HH:MM
    private static int getHour(String time) {

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
    private static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onCancelled() {

    }

    ///api 26
    private static void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }else{
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICACION_CHANNEL_ID,
                    Constants.NOTIFICACION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(Constants.NOTIFICACION_CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }

    }


    //RECORDATORIO PESO
    private static void notificationBuilder (Context context, String parametro, String horarioPESO){

        //CREAR RECORDATORIO
        descripcionRecordatorio = "Debe ingresar la medición de su PESO del día de hoy";
        crearRecordatorio(context, parametro, descripcionRecordatorio, RecordatoriosContract.RecordatoriosEntry.TIPO_ACCION);


        int horaPeso = getHour(horarioPESO);
        int minPeso = getMinute(horarioPESO);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, horaPeso);
        calendar.set(Calendar.MINUTE, minPeso);
        calendar.set(Calendar.SECOND, 0);

        long when = calendar.getTimeInMillis();

        NotificationManager notificationManagerPESO = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, Activity_Menu_Autodiagnostico.class); //Abre esta actividad al hacer click en la notificacion
        //prueba tab
        notificationIntent.putExtra(Constants.PARAMETRO, parametro);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntentPESO = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Cada notificación debe tener un int distinto para que puedan ser tratados por separado, PESO 0, PA 1, SINTOMAS 2


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //f you wish, you can set the priority of a notification on Android 7.1 (API level 25) and lower. The priority acts as a hint to the device UI about how the notification should be displayed. To set a notification's priority, call NotificationCompat.Builder.setPriority() and pass in one of the NotificationCompat priority constants. There are five priority levels, ranging from PRIORITY_MIN (-2) to PRIORITY_MAX (2); if not set, the priority defaults to PRIORITY_DEFAULT (0).
try {


    NotificationCompat.Builder mNotifyBuilder = (NotificationCompat.Builder) new
            NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_ic)
            .setContentTitle("¡Hora de Pesarse!")
            .setContentText("Por favor ingrese la medición de su peso de hoy")
            .setSound(alarmSound)
            // .setAutoCancel(true) //Descarta la notificación una vez que el usuario hace click sobre ella - esto deberia suceder una vez q carga los datos?
            .setWhen(when)
            .setContentIntent(pendingIntentPESO)
            .setPriority(PRIORITY_MAX)
            .setChannelId(Constants.NOTIFICACION_CHANNEL_ID)
            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
    mId++;
    notificationManagerPESO.notify(mId, mNotifyBuilder.build());

}catch (SecurityException e){
    e.printStackTrace();
}
        //CONSULTAR SI EXISTEN RECORDATORIOS DEL DÍA YA CREADOS en realidad tendría q buscar si ya se cargaron los datos del día de hoy... en ese caso no crear

        //  consultar(Constants.PARAMETRO_PESO, context);



    }

    //RECORDATORIO PRESION ARTERIAL Y FRECUENCIA CARDIACA
    //CREA LA NOTIFICACION PARA LA PA Y FC
    private static void notificationBuilderPA(Context context, String parametro, String horarioPAFC) {
        //CREAR RECORDATORIO
        descripcionRecordatorio = "Debe ingresar la medición de su PRESIÓN ARTERIAL Y FRECUENCIA CARDÍACA del día de hoy";
        crearRecordatorio(context, parametro, descripcionRecordatorio, RecordatoriosContract.RecordatoriosEntry.TIPO_ACCION);


        int horaPA = getHour(horarioPAFC);
        int minPA = getMinute(horarioPAFC);

        Calendar calendarPA = Calendar.getInstance();

        calendarPA.set(Calendar.HOUR_OF_DAY, horaPA);
        calendarPA.set(Calendar.MINUTE, minPA);
        calendarPA.set(Calendar.SECOND, 0);

        long when = calendarPA.getTimeInMillis();


     //   initChannels(context);

        NotificationManager notificationManagerPA = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntentPA = new Intent(context, Activity_Menu_Autodiagnostico.class); //Abre esta actividad al hacer click en la notificacion

        //prueba tab
        notificationIntentPA.putExtra(Constants.PARAMETRO, parametro);

        notificationIntentPA.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntentPA = PendingIntent.getActivity(context, 1,
                notificationIntentPA, PendingIntent.FLAG_UPDATE_CURRENT);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotifyBuilderPA = (NotificationCompat.Builder) new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_ic)
                .setContentTitle("¡Hora de medir su Presión Arterial!")
                .setContentText("Por favor ingrese la medición de su Presión Arterial y Frecuencia Cardíaca del día de hoy")
                .setSound(alarmSound)
               // .setDefaults(DEFAULT_SOUND)
               // .setAutoCancel(true) //Descarta la notificación una vez que el usuario hace click sobre ella - esto deberia suceder una vez q carga los datos?
                .setWhen(when)
                .setContentIntent(pendingIntentPA)
                .setPriority(PRIORITY_MAX)
                .setChannelId(Constants.NOTIFICACION_CHANNEL_ID)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        mId++;
        notificationManagerPA.notify(mId, mNotifyBuilderPA.build());



    }


    private static void notificationBuilderSINTOMAS(Context context, String parametro, String horarioSINTOMAS) {
        // long when = System.currentTimeMillis();
        //23/08 trato de que solo se genere la notificacion en el horario del peso configurado y no cada vez q se abre mainactivity

 /*       SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String horarioPAFC = sharedPref.getString(SettingsActivity.KEY_PREF_HORARIO_PA_FC, "");
        //  horarioPESO = sharedPref.getString(SettingsActivity.KEY_PREF_HORARIO_PESO, "");
        String horarioPESO = sharedPref.getString(SettingsActivity.KEY_PREF_HORARIO_PESO, "8:00");
        String horarioSINTOMAS = sharedPref.getString(SettingsActivity.KEY_PREF_HORARIO_SINTOMAS, "");
*/

        //CREAR RECORDATORIO
        descripcionRecordatorio = "Tiene preguntas sobre sus síntomas pendientes de contestar";
        crearRecordatorio(context, parametro, descripcionRecordatorio, RecordatoriosContract.RecordatoriosEntry.TIPO_ACCION);


        int horaSINTOMAS = getHour(horarioSINTOMAS);
        int minSINTOMAS = getMinute(horarioSINTOMAS);

        Calendar calendarSINTOMAS = Calendar.getInstance();

        calendarSINTOMAS.set(Calendar.HOUR_OF_DAY, horaSINTOMAS);
        calendarSINTOMAS.set(Calendar.MINUTE, minSINTOMAS);
        calendarSINTOMAS.set(Calendar.SECOND, 0);

        long when = calendarSINTOMAS.getTimeInMillis();

        NotificationManager notificationManagerSINTOMAS = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntentSINTOMAS = new Intent(context, Activity_Menu_Autodiagnostico.class); //Abre esta actividad al hacer click en la notificacion
        //prueba tab
        notificationIntentSINTOMAS.putExtra(Constants.PARAMETRO, parametro);

        notificationIntentSINTOMAS.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        /*
        int	FLAG_CANCEL_CURRENT
Flag indicating that if the described PendingIntent already exists, the current one should be canceled before generating a new one.
int	FLAG_IMMUTABLE
Flag indicating that the created PendingIntent should be immutable.
int	FLAG_NO_CREATE
Flag indicating that if the described PendingIntent does not already exist, then simply return null instead of creating it.
int	FLAG_ONE_SHOT
Flag indicating that this PendingIntent can be used only once.
int	FLAG_UPDATE_CURRENT
Flag indicating that if the described PendingIntent already exists, then keep it but replace its extra data with what is in this new Intent.
         */

       PendingIntent pendingIntentSINTOMAS = PendingIntent.getActivity(context, 2,
               notificationIntentSINTOMAS,PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotifyBuilderSINTOMAS = (NotificationCompat.Builder) new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_ic)
                .setContentTitle("¡Hora de controlar sus síntomas!")
                .setContentText("Por favor conteste las siguientes preguntas")
                .setSound(alarmSound)
                // .setAutoCancel(true) //Descarta la notificación una vez que el usuario hace click sobre ella - esto deberia suceder una vez q carga los datos?
                .setWhen(when)
                .setContentIntent(pendingIntentSINTOMAS)
                .setPriority(PRIORITY_MAX)
                .setChannelId(Constants.NOTIFICACION_CHANNEL_ID)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        mId++;
        notificationManagerSINTOMAS.notify(mId, mNotifyBuilderSINTOMAS.build());


    }

    //RECORDATORIO CONSEJO SALUDABLE
    private void notificationBuilderCONSEJO_SALUDABLE(Context context, String parametro, String horarioCONSEJO_SALUDABLE, String fecha) {

        conf = new Configuraciones(context);
        contador = conf.getContadorConsejoSaludable();
        //CREAR RECORDATORIO
        descripcionRecordatorio = crearConsejoSaludable(context, contador);
        crearRecordatorio(context, parametro, descripcionRecordatorio, RecordatoriosContract.RecordatoriosEntry.TIPO_RECORDATORIO);

       guardarConsejoSaludableBDAutodiagnostico_DBHelper(context, descripcionRecordatorio, fecha, contador);


        int horaCS = getHour(horarioCONSEJO_SALUDABLE);
        int minCS = getMinute(horarioCONSEJO_SALUDABLE);

        Calendar calendarCS = Calendar.getInstance();

        calendarCS.set(Calendar.HOUR_OF_DAY, horaCS);
        calendarCS.set(Calendar.MINUTE, minCS);
        calendarCS.set(Calendar.SECOND, 0);

        long when = calendarCS.getTimeInMillis();


        NotificationManager notificationManagerCS = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntentCS = new Intent(context, Activity_ViewConsejoSaludable.class); //Abre esta actividad al hacer click en la notificacion
        //prueba tab
        notificationIntentCS.putExtra(Constants.CONSEJO_SALUDABLE, descripcionRecordatorio);
        notificationIntentCS.putExtra(Constants.FECHA_CONSEJO_SALUDABLE, fecha);

        notificationIntentCS.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        /*
        int	FLAG_CANCEL_CURRENT
Flag indicating that if the described PendingIntent already exists, the current one should be canceled before generating a new one.
int	FLAG_IMMUTABLE
Flag indicating that the created PendingIntent should be immutable.
int	FLAG_NO_CREATE
Flag indicating that if the described PendingIntent does not already exist, then simply return null instead of creating it.
int	FLAG_ONE_SHOT
Flag indicating that this PendingIntent can be used only once.
int	FLAG_UPDATE_CURRENT
Flag indicating that if the described PendingIntent already exists, then keep it but replace its extra data with what is in this new Intent.
         */

        PendingIntent pendingIntentCS = PendingIntent.getActivity(context, 2,
                notificationIntentCS,PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotifyBuilderCS = (NotificationCompat.Builder) new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_ic)
                .setContentTitle("Consejo Saludable")
                .setContentText("Lea este consejo para mejorar su salud")
                // .setSound(alarmSound)
                .setAutoCancel(true) //Descarta la notificación una vez que el usuario hace click sobre ella - esto deberia suceder una vez q carga los datos?
                .setWhen(when)
                .setContentIntent(pendingIntentCS)
                .setPriority(PRIORITY_MAX)
                .setChannelId(Constants.NOTIFICACION_CHANNEL_ID)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        mId++;
        notificationManagerCS.notify(mId, mNotifyBuilderCS.build());

    }

    private int guardarConsejoSaludableBDAutodiagnostico_DBHelper(Context context, String descripcionRecordatorio, String fecha, int contador) {

        Autodiagnostico_DBHelper dbHelperAuto = new Autodiagnostico_DBHelper(context);
        SQLiteDatabase dbConsejo = dbHelperAuto.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(AutodiagnosticoContract.AutodiagnosticoEntry.CONSEJO_SALUDABLE_DATE, fecha);
        contentValues.put(AutodiagnosticoContract.AutodiagnosticoEntry.CONSEJO_SALUDABLE_DESCRIPCION, descripcionRecordatorio);
        contentValues.put(AutodiagnosticoContract.AutodiagnosticoEntry.CONSEJO_SALUDABLE_IDPREGUNTA, contador);
        contentValues.put(AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO, AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO_PENDIENTE);

        int confirmacion = (int) dbConsejo.insert(AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_CONSEJO_SALUDABLE, null, contentValues);

        dbConsejo.close();
        return confirmacion;
    }

    private String crearConsejoSaludable(Context context, int contador) {
        //Leer el archivo
        String linea = null;
        String lineaInicial;
        String consejoSaludable = null;


        try {

            InputStream fraw = context.getResources().openRawResource(R.raw.consejossaludables);
            BufferedReader brin = new BufferedReader(new InputStreamReader(fraw));
            
            lineaInicial = brin.readLine();
            
            
            if(contador != 0) {
                for (int i = 1; i <= contador; i++) {
                    linea = brin.readLine();
                }
                if (linea != null) {
                    consejoSaludable = linea;

                } else {
                    contador = 0;
                    consejoSaludable = lineaInicial;

                }
            }else {
                consejoSaludable = lineaInicial;

            }

            contador++;
            conf.setContadorConsejoSaludable(contador);
                
            } catch (IOException e1) {
            e1.printStackTrace();
        }

        return consejoSaludable;
        
    }


    //RECORDATORIO MEDICAMENTO
    private void notificationBuilderMEDICAMENTO(Context context, String parametro, String horarioMEDICAMENTOS, String fecha) {

     //   conf = new Configuraciones(context);
     //   contador = conf.getContadorConsejoSaludable();
        //CREAR RECORDATORIO
     //  descripcionRecordatorio = crearConsejoSaludable(context, contador);

        descripcionRecordatorio = "Por favor conteste la siguiente pregunta sobre sus medicamentos.";
       // crearRecordatorio(context, parametro, descripcionRecordatorio, RecordatoriosContract.RecordatoriosEntry.TIPO_RECORDATORIO);
        crearRecordatorio(context, parametro, descripcionRecordatorio, Constants.MEDICAMENTOS);

    //    int confirm;
    //    confirm = guardarConsejoSaludableBDAutodiagnostico_DBHelper(context, descripcionRecordatorio, fecha, contador);


        int horaMEDICAMENTOS = getHour(horarioMEDICAMENTOS);
        int minMEDICAMENTOS = getMinute(horarioMEDICAMENTOS);

        Calendar calendarMEDICAMENTOS = Calendar.getInstance();

        calendarMEDICAMENTOS.set(Calendar.HOUR_OF_DAY, horaMEDICAMENTOS);
        calendarMEDICAMENTOS.set(Calendar.MINUTE, minMEDICAMENTOS);
        calendarMEDICAMENTOS.set(Calendar.SECOND, 0);

        long when = calendarMEDICAMENTOS.getTimeInMillis();


        NotificationManager notificationManagerMEDICAMENTOS = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntentMEDICAMENTOS = new Intent(context, Activity_ViewConsultaMedicamentos.class); //Abre esta actividad al hacer click en la notificacion
        //prueba tab
   //     notificationIntentMEDICAMENTOS.putExtra(Constants.CONSEJO_SALUDABLE, descripcionRecordatorio);
    //    notificationIntentMEDICAMENTOS.putExtra(Constants.FECHA_CONSEJO_SALUDABLE, fecha);

        notificationIntentMEDICAMENTOS.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        /*
        int	FLAG_CANCEL_CURRENT
Flag indicating that if the described PendingIntent already exists, the current one should be canceled before generating a new one.
int	FLAG_IMMUTABLE
Flag indicating that the created PendingIntent should be immutable.
int	FLAG_NO_CREATE
Flag indicating that if the described PendingIntent does not already exist, then simply return null instead of creating it.
int	FLAG_ONE_SHOT
Flag indicating that this PendingIntent can be used only once.
int	FLAG_UPDATE_CURRENT
Flag indicating that if the described PendingIntent already exists, then keep it but replace its extra data with what is in this new Intent.
         */

        PendingIntent pendingIntentMEDICAMENTOS = PendingIntent.getActivity(context, 2,
                notificationIntentMEDICAMENTOS,PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotifyBuilderMEDICAMENTOS = (NotificationCompat.Builder) new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_ic)
                .setContentTitle("Medicamentos")
                .setContentText("Por favor conteste la siguiente pregunta")
                // .setSound(alarmSound)
                .setAutoCancel(true) //Descarta la notificación una vez que el usuario hace click sobre ella - esto deberia suceder una vez q carga los datos?
                .setWhen(when)
                .setContentIntent(pendingIntentMEDICAMENTOS)
                .setPriority(PRIORITY_MAX)
                .setChannelId(Constants.NOTIFICACION_CHANNEL_ID)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        mId++;
        notificationManagerMEDICAMENTOS.notify(mId, mNotifyBuilderMEDICAMENTOS.build());

    }


    private static void crearRecordatorio(Context context, String parametro, String descripcionRecordatorio, String tipo_recordatorio) {

        String idNotificacion = String.valueOf(mId + 1);

        Recordatorio mRecordatorio = new Recordatorio();
        mRecordatorio.setTitulo(tipo_recordatorio);
        mRecordatorio.setParametro(parametro);
        mRecordatorio.setDescripcion(descripcionRecordatorio);        
        mRecordatorio.setIdNotificacion(idNotificacion);

        mRecordatorio.crearRecordatorioBD(context);
    }

    //RECORDATORIO ENCUESTAS
    private void notificationBuilderEncuesta(Context context, String parametro, String horarioENCUESTAS) {

        //CREAR RECORDATORIO
        descripcionRecordatorio = "Por favor conteste las siguientes encuestas que forman parte del estudio clínico del cual Ud forma parte";
        crearRecordatorio(context, parametro, descripcionRecordatorio, Constants.PARAMETRO_ENCUESTAS);

        int horaCS = getHour(horarioENCUESTAS);
        int minCS = getMinute(horarioENCUESTAS);

        Calendar calendarCS = Calendar.getInstance();

        calendarCS.set(Calendar.HOUR_OF_DAY, horaCS);
        calendarCS.set(Calendar.MINUTE, minCS);
        calendarCS.set(Calendar.SECOND, 0);

        long when = calendarCS.getTimeInMillis();


        NotificationManager notificationManagerEncuestas = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntentEncuestas = new Intent(context, Activity_Encuestas.class); //Abre esta actividad al hacer click en la notificacion
        //prueba tab
     //   notificationIntentEncuestas.putExtra(Constants.CONSEJO_SALUDABLE, descripcionRecordatorio);
     //   notificationIntentEncuestas.putExtra(Constants.FECHA_CONSEJO_SALUDABLE, fecha);

        notificationIntentEncuestas.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        /*
        int	FLAG_CANCEL_CURRENT
Flag indicating that if the described PendingIntent already exists, the current one should be canceled before generating a new one.
int	FLAG_IMMUTABLE
Flag indicating that the created PendingIntent should be immutable.
int	FLAG_NO_CREATE
Flag indicating that if the described PendingIntent does not already exist, then simply return null instead of creating it.
int	FLAG_ONE_SHOT
Flag indicating that this PendingIntent can be used only once.
int	FLAG_UPDATE_CURRENT
Flag indicating that if the described PendingIntent already exists, then keep it but replace its extra data with what is in this new Intent.
         */

        PendingIntent pendingIntentEncuestas = PendingIntent.getActivity(context, 2,
                notificationIntentEncuestas,PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotifyBuilderEncuestas = (NotificationCompat.Builder) new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_ic)
                .setContentTitle("Encuestas")
                .setContentText("Encuestas pendientes de contestar")
                // .setSound(alarmSound)
                .setAutoCancel(true) //Descarta la notificación una vez que el usuario hace click sobre ella - esto deberia suceder una vez q carga los datos?
                .setWhen(when)
                .setContentIntent(pendingIntentEncuestas)
                .setPriority(PRIORITY_MAX)
                .setChannelId(Constants.NOTIFICACION_CHANNEL_ID)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        mId++;
        notificationManagerEncuestas.notify(mId, mNotifyBuilderEncuestas.build());

    }


}
