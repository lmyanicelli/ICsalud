package com.luciayanicelli.icsalud.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.Api_Json.Get_Contact;
import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;
import com.luciayanicelli.icsalud.Notifications.MyReceiverGenerarEmail;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */

/*
PARA UTILIZAR EL SERVICE QUE CONTROLA LOS ESTADOS PENDIENTES DE LAS ALERTAS Y MEDICIONES - CORROBORA CONEXIÓN A INTERNET Y ENVIA LOS MAILS CORRESPONDIENTES

//Sevice Generar Email - ENVIAR ALERTAS
        Intent intentMAIL = new Intent(getApplicationContext(), ServiceGenerarEmail.class);
        intentMAIL.setAction(Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE);
        startService(intentMAIL);

//Sevice Generar Email - ENVIAR MEDICIONES
        Intent intentMEDICIONES = new Intent(getApplicationContext(), ServiceGenerarEmail.class);
        intentMEDICIONES.setAction(Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE_MEDICIONES);
        startService(intentMEDICIONES);

 */
public class ServiceGetContacts extends IntentService {


    ///VARIABLES

    private String asunto;
    private String textoEnviar = "";

    private AlarmManager alarmManagerSinConexion, alarmManagerSinConexion30;
    private PendingIntent pendingIntentSinConexion, pendingIntentSinConexion30;

    private int contador = 0;
    private String contactosMediciones, contactoAdministrador;

    private Configuraciones configuraciones;


    public ServiceGetContacts() {

        super("ServiceGetContacts");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
              if (intent != null) {

            configuraciones = new Configuraciones(getApplicationContext());

                  ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());

                  try {
                      if(conexionInternet.execute().get()){
                          handleActionCONTACTS();
                      }else{

                          contador = contador ++;
                          if(contador <= 1){
                              //Volver a intentar la conexión a los 30 segundos
                              Intent myIntent30 = new Intent(getApplicationContext(), ServiceGetContacts.class);
                            //  myIntent30.setAction(Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE);
                              pendingIntentSinConexion30 = PendingIntent.getService(getApplicationContext(), 0, myIntent30,0);

                              alarmManagerSinConexion30 = (AlarmManager)getApplicationContext().getSystemService(ALARM_SERVICE);
                              //Calendar calendar = Calendar.getInstance();

                              //alarma en 30 segundos
                              alarmManagerSinConexion30.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 30 * 1000, pendingIntentSinConexion30);
                            //  alarmManagerSinConexion.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, pendingIntentSinConexion);

                          } else {
                              contador = 0;
                          }


                          //VER DE PONER UN BUCLE PARA QUE VUELVA A INTENTAR CADA UNA HORA

                          Intent myIntentEAlertas = new Intent(getApplicationContext(), MyReceiverGenerarEmail.class);


                          pendingIntentSinConexion = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntentEAlertas,0);

                          alarmManagerSinConexion = (AlarmManager)getApplicationContext().getSystemService(ALARM_SERVICE);

                          Calendar calendar = Calendar.getInstance();

                          alarmManagerSinConexion.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, pendingIntentSinConexion);

                      }
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  } catch (ExecutionException e) {
                      e.printStackTrace();
                  }


        }
    }

    //OBTIENE LOS CONTACTOS Y LOS GUARDA EN CONFIGURACIONES
    private void handleActionCONTACTS() throws ExecutionException, InterruptedException {

        Get_Contact get_contacts = new Get_Contact(getApplicationContext());
        HashMap<String, String> data = get_contacts.execute().get();

        String emailsContacts = data.get(JSON_CONSTANTS.EMAIL);
        String telefonosContacts = data.get(JSON_CONSTANTS.CONTACTS_MOBILE_NUMBER);
        String nombreContacts = data.get(JSON_CONSTANTS.CONTACTS);
        String idContacts = data.get(JSON_CONSTANTS.ID);
        //Guardar en configuraciones
        Configuraciones configuraciones = new Configuraciones(getApplicationContext());
        configuraciones.setUserEmailContacts(emailsContacts);

        configuraciones.setUserCelContacts(telefonosContacts);
        configuraciones.setUserNameContacts(nombreContacts);
        configuraciones.setUserIdContacts(idContacts);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
