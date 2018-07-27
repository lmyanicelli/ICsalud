package com.luciayanicelli.icsalud.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AlertaVerdeJobService extends JobService {

    private boolean isWorking=false;
    private JobParameters jobParameters;
    private String fecha;

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(this.getClass().getSimpleName(),"onStartJobAV");
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                jobParameters = params;
                isWorking = true;
                //obtener fecha de ayer

                Calendar calendarAyer = Calendar.getInstance();
                calendarAyer.add(Calendar.DAY_OF_YEAR, -1);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(JSON_CONSTANTS.DATE_TIME_FORMAT);
                //   fecha = simpleDateFormat.format(calendarAyer.getTime()).split(" ")[0];
                //30/05/18
                fecha = simpleDateFormat.format(calendarAyer.getTime());

                AlertaVerde alertaVerde = new AlertaVerde(fecha,
                        AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE,
                        AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO,
                        1,
                        getApplicationContext());
                alertaVerde.execute();

         /*       AlertaVerde2 alertaVerde1 = new AlertaVerde2(fecha,
                        AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE,
                        AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO,
                        1,
                        getApplicationContext()){
                    @Override
                    protected void onPostExecute(Boolean success) {
                      //  jobFinished(params, !success);
                        alerta2();
                    }
                };
                alertaVerde1.execute();
*/




///
//                jobFinished(params, false);
            }
        });

  //      SetearAlarma.scheduleJobEnviarDatosServidor(getApplicationContext());
   //     return true;
        return isWorking;
    }

    private void alerta2() {
        AlertaVerde2 alertaVerde2 = new AlertaVerde2(fecha,
                AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE,
                AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA,
                1,
                getApplicationContext()){
            @Override
            protected void onPostExecute(Boolean success) {
                alerta3();
            }
        };
        alertaVerde2.execute();

    }

    private void alerta3() {

        AlertaVerde2 alertaVerde3 = new AlertaVerde2(fecha,
                AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE,
                AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS,
                3,
                getApplicationContext()){
            @Override
            protected void onPostExecute(Boolean success) {
                isWorking = false;
                jobFinished(jobParameters, !success);
            }
        };
        alertaVerde3.execute();
    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {

      //  return true;

        boolean needsReschedule = isWorking;
        jobFinished(jobParameters, needsReschedule);
        return needsReschedule;
    }

}
