package com.luciayanicelli.icsalud.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.luciayanicelli.icsalud.utils.PAFC;
import com.luciayanicelli.icsalud.utils.Peso;
import com.luciayanicelli.icsalud.utils.Sintomas;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AlertaVerdeJobService extends JobService {

    private boolean isWorking=false;

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(this.getClass().getSimpleName(),"onStartJobAV");
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                isWorking = true;

                Peso mPeso = new Peso(getApplicationContext());
                mPeso.alertaVerde();

                PAFC mPAFC = new PAFC(getApplicationContext());
                mPAFC.alertaVerde();

                Sintomas mSintomas = new Sintomas(getApplicationContext());
                mSintomas.alertaVerde();

            }
        });

        return isWorking;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        boolean needsReschedule = isWorking;
        jobFinished(jobParameters, needsReschedule);
        return needsReschedule;
    }

}
