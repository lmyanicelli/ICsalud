package com.luciayanicelli.icsalud.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.BaseColumns;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;
import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.DataBase.JuegoContract;
import com.luciayanicelli.icsalud.DataBase.Jugada_DBHelper;
import com.luciayanicelli.icsalud.utils.Alertas;

import java.util.concurrent.ExecutionException;

/**
 * Created by LuciaYanicelli on 14/6/2018.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GenerarAlertasAdministrador_JobService extends JobService {

    private Configuraciones configuraciones;
    private String action;
    private String textoEnviar;
    private boolean needsReschedule = false;
    private boolean isWorking = false;
    private JobParameters jobParameters;


    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(this.getClass().getSimpleName(),"onStartJobGenerarAlertasAdministrador_JobService");
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                jobParameters = params;

                PersistableBundle extra = params.getExtras();
                action = extra.getString("extra");

                if(action!= null) {

                    isWorking = true;

                   configuraciones = new Configuraciones(getApplicationContext());

                    ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());

                    try {
                        if (conexionInternet.execute().get()) {

                            if (Constants.SERVICE_GENERAR_ALERTAS_ADMINISTRADOR.equals(action)) {
                                handleActionGenerarAlertasAdministrador();
                            }

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        return isWorking;
    }


    //GENERA ALERTAS XA QUE EL ADMINISTRADOR PUEDA ACCEDER A ESTA INFO
    private void handleActionGenerarAlertasAdministrador() throws ExecutionException, InterruptedException {

        String textoCampos = "";

        Jugada_DBHelper mDBHelper = new Jugada_DBHelper(getApplicationContext());
        SQLiteDatabase dbJugadas = mDBHelper.getWritableDatabase();

        String[] camposDBJugada = new String[]{
                BaseColumns._ID,
                JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL,
                JuegoContract.JuegoEntry.PREGUNTA_ID,
                JuegoContract.JuegoEntry.OPCIONES_ID,
                JuegoContract.JuegoEntry.OPCIONES_PUNTAJE,
                JuegoContract.JuegoEntry.JUGADA_PUNTAJE_ACUMULADO
        };

        for(int i=0; i<camposDBJugada.length; i++) {
            textoCampos = textoCampos + camposDBJugada[i] + ";";
        }

                /*query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
Query the given URL, returning a Cursor over the result set.*/
        Cursor cursorJugada = dbJugadas.query(true, JuegoContract.JuegoEntry.TABLE_NAME_JUGADA, camposDBJugada,
                null, null, null, null, null, null);

        //Existen datos cargados en la tabla jugada
        if (cursorJugada != null & cursorJugada.moveToFirst()) {
            int count;
            count = cursorJugada.getCount();

            do {

                String textoAuxiliar = "";
                for (int i = 0; i < cursorJugada.getColumnCount(); i++) {
                    textoAuxiliar = textoAuxiliar + cursorJugada.getInt(i) + ";";
                }

                textoEnviar = textoEnviar + " , " + textoAuxiliar;

            } while (cursorJugada.moveToNext());

        }

        cursorJugada.close();

        //Guardar como alerta en BD xa luego subir al servidor
        String descripcion = textoCampos + textoEnviar;

        //guardar Alarma en BD
        Alertas mAlertas = new Alertas(getApplicationContext());

        if(descripcion.length()<=256) {

            mAlertas.guardar(AlertasContract.AlertasEntry.ALERTA_TIPO_VERDE,
                    AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA,
                    descripcion,
                    AlertasContract.AlertasEntry.ALERTA_VISIBILIDAD_PRIVADA);

        }else{

            int caracteres = descripcion.length();
            int multiplo256 = caracteres/256;
            if(caracteres%256 != 0){
                multiplo256 = multiplo256 + 1;
            }

            for(int j = 0; j < multiplo256; j++){

                String textoAuxiliar= "";
                int inicio = j*256;
                int fin = j*256 +256;
                if(fin>caracteres){
                    fin = caracteres;
                }

                textoAuxiliar = descripcion.substring(inicio, fin);
                mAlertas.guardar(AlertasContract.AlertasEntry.ALERTA_TIPO_VERDE,
                        AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA,
                        textoAuxiliar,
                        AlertasContract.AlertasEntry.ALERTA_VISIBILIDAD_PRIVADA);
            }

        }

        String textoMail;
        textoMail = "Consejos Saludables leídos: " + String.valueOf(configuraciones.getContadorConsejosSaludablesLeidos()) +
                "<br/><br/>"+ "Preguntas Frecuentes leídas: " + String.valueOf(configuraciones.getContadorPreguntasFrecuentesLeidas()) ;

        //Guardar como alerta en BD xa luego subir al servidor

        //guardar Alarma en BD
        mAlertas.guardar(AlertasContract.AlertasEntry.ALERTA_TIPO_VERDE,
                JSON_CONSTANTS.HEART_RATES,
                textoMail,
                AlertasContract.AlertasEntry.ALERTA_VISIBILIDAD_PRIVADA);

        isWorking = false;
        jobFinished(jobParameters, false);


    }

    /*
   onStopJob() is called by the system if the job is cancelled before being finished.
   This generally happens when your job conditions are no longer being met,
   such as when the device has been unplugged or if WiFi is no longer available.
   So use this method for any safety checks and clean up you may need to do in response to a half-finished job.
   Then, return true if you’d like the system to reschedule the job,
   or false if it doesn’t matter and the system will drop this job.
    */
    // // Called if the job was cancelled before being finished
    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        //  return true;

        needsReschedule = isWorking;
        jobFinished(jobParameters, needsReschedule);
        return needsReschedule;

    }

}
