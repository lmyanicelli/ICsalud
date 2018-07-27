package com.luciayanicelli.icsalud.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.Api_Json.Get_Contact;
import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetContactsJobService extends JobService {


    private boolean isWorking = false;
    private JobParameters jobParameters;

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(this.getClass().getSimpleName(),"onStartJobContacts");
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                jobParameters = params;
                isWorking = true;
                ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());

                try {
                    if(conexionInternet.execute().get()) {

                        handleActionCONTACTS();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
///
//                jobFinished(params, false);
            }
        });

     //   SetearAlarma.scheduleJobContacts(getApplicationContext());
     //   return true;
        return isWorking;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

   //return true;

        boolean needsRechedule = isWorking;
        jobFinished(jobParameters, needsRechedule);
        return needsRechedule;
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

        isWorking = false;
        jobFinished(jobParameters, false);

    }



}
