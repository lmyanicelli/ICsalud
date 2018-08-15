package com.luciayanicelli.icsalud.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.BaseColumns;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.Api_Json.Get_Practitioner_index;
import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;
import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.Alertas_DBHelper;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_DBHelper;
import com.luciayanicelli.icsalud.DataBase.JuegoContract;
import com.luciayanicelli.icsalud.DataBase.Jugada_DBHelper;
import com.luciayanicelli.icsalud.utils.EnviarMailSegundoPlano;
import com.luciayanicelli.icsalud.utils.FechaActual;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by LuciaYanicelli on 14/6/2018.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GenerarEmailJobService extends JobService {

    private Configuraciones configuraciones;
    private String contactosAlertas;
    private String contactoAdministrador;
    private String action;
    private String textoEnviar;
    private String asunto;
    private long periodo;
    private int jobId;
    private boolean needsReschedule = false;
    private boolean isWorking = false;
    private JobParameters jobParameters;

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(this.getClass().getSimpleName(),"onStartJobGE");
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                jobParameters = params;

                PersistableBundle extra = params.getExtras();
            //    periodo = extra.getLong("periodo");
                action = extra.getString("extra");
                jobId = extra.getInt("jobId");

                if(action!= null) {

                    isWorking = true;

                    configuraciones = new Configuraciones(getApplicationContext());

                    contactosAlertas = configuraciones.getUserEmailRemitente();
                    contactoAdministrador = configuraciones.getEmailAdministrator();


                    ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());

                    try {
                        if (conexionInternet.execute().get()) {

                            if (Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE.equals(action)) {
                                actualizarContactosMediciones();
                                handleActionRun();
                            } else if (Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE_MEDICIONES.equals(action)) {
                                handleActionMediciones();
                            } else if (Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE_JUGADAS.equals(action)) {
                                handleActionJugadas();
                            } else if (Constants.SERVICE_GENERAR_EMAIL_NEW_USER.equals(action)) {
                                handleActionNewUser();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

///
             //   jobFinished(params, false);
            }
        });

      //  SetearAlarma.scheduleJobGenerarEmail(getApplicationContext(), action, periodo, jobId);
      //  return true;
        return isWorking;
    }

        private void actualizarContactosMediciones() {

            try {
                Get_Practitioner_index get_practitioner_index = new Get_Practitioner_index(getApplicationContext(), JSON_CONSTANTS.PRACTITIONER_STATUS_FRIEND);
                HashMap<String, String> data = get_practitioner_index.execute().get();
                if (data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(String.valueOf(Boolean.TRUE))) {
                    if (data.get(JSON_CONSTANTS.RESPONSE_TOTAL).equalsIgnoreCase("0")) {

                        configuraciones.setUserEmailRemitentes(Configuraciones.DEFAULT_USER_EMAIL_REMITENTE);
                        contactosAlertas = configuraciones.getUserEmailRemitente();

                    }else{
                        String email_professionals = data.get(JSON_CONSTANTS.EMAIL);

                        configuraciones.setUserEmailRemitentes(email_professionals);

                        contactosAlertas=configuraciones.getUserEmailRemitente();
                    }

                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

    }

    private void handleActionNewUser() {
        String textoEnviar = crearTextoFormulario();
        String contactos = configuraciones.getEmailAdministrator();

        EnviarMailSegundoPlano enviarMailSegundoPlano = new EnviarMailSegundoPlano(getApplicationContext(),
                "Formulario Nueva Cuenta",
                textoEnviar,
                contactos);
        enviarMailSegundoPlano.execute();
    }

    private String crearTextoFormulario() {

        String textoEnviar;
        Configuraciones configuraciones = new Configuraciones(getApplicationContext());

        textoEnviar = configuraciones.getUserSurname() + ", " + configuraciones.getUserName() + ";" +
                configuraciones.getGender() + ";" + configuraciones.getUserBirthday() + ";" +
                configuraciones.getOccupation() + ";" + configuraciones.getEducationLevel()+ ";" +
                configuraciones.getCoexistence() + ";" + configuraciones.getHealthInsurance() + ";" +
                configuraciones.getDiagnosedAt() + ";" + configuraciones.getLastHospitalization() + ";" +
                "grupo intervención" + ";" +
                configuraciones.getUserEmail();

        return textoEnviar;
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

    //ENVIA LAS ALERTAS PENDIENTES
    private void handleActionRun() {
        try {

            //ANALIZA ALERTAS
            Alertas_DBHelper mDBHelper = new Alertas_DBHelper(getApplicationContext());
            SQLiteDatabase db = mDBHelper.getWritableDatabase();

            //BUSCO LOS ESTADOS PENDIENTES
            String[] camposDB = new String[]{BaseColumns._ID,
                    AlertasContract.AlertasEntry.ESTADO,
                    AlertasContract.AlertasEntry.TIPO,
                    AlertasContract.AlertasEntry.FECHA,
                    AlertasContract.AlertasEntry.DESCRIPCION};

            String selection = AlertasContract.AlertasEntry.ESTADO + "= ?";
            String[] args = new String[]{AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE};


            String orderBy = AlertasContract.AlertasEntry.TIPO + " DESC";

                /*query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
Query the given URL, returning a Cursor over the result set.*/
            Cursor cursor = db.query(true, AlertasContract.AlertasEntry.TABLE_NAME, camposDB, selection, args, null, null, orderBy, null);

            //Existen alertas con estado PENDIENTE
            if (cursor != null & cursor.moveToFirst()) {
                enviarAlertas(db, cursor);
            }else {
                isWorking = false;
                jobFinished(jobParameters, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //ENVIA LAS MEDICIONES PENDIENTES
    private void handleActionMediciones() throws ExecutionException, InterruptedException {

        //ANALIZA LAS MEDICIONES PENDIENTES PARA SER ENVIADAS
        try {
            Autodiagnostico_DBHelper mDBHelper = new Autodiagnostico_DBHelper(getApplicationContext());
            SQLiteDatabase dbMediciones = mDBHelper.getWritableDatabase();

            String selection = AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO + "= ?";
            String[] args = new String[]{AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO_PENDIENTE};

            String orderBy = BaseColumns._ID + " DESC";


            //BUSCO LOS ESTADOS PENDIENTES EN LA TABLA PA
            String[] camposDBPA = new String[]{BaseColumns._ID,
                    AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO,
                    AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE,
                    AutodiagnosticoContract.AutodiagnosticoEntry.PA_PS,
                    AutodiagnosticoContract.AutodiagnosticoEntry.PA_PD,
                    AutodiagnosticoContract.AutodiagnosticoEntry.PA_FC,
            };

                /*query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
Query the given URL, returning a Cursor over the result set.*/
            Cursor cursorPA = dbMediciones.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA, camposDBPA,
                    selection, args, null, null, orderBy, null);

            //Existen MEDICIONES DE LA TABLA PA CON ESTADO PENDIENTE
            if (cursorPA != null & cursorPA.moveToFirst()) {
                int count;
                count = cursorPA.getCount();

                asunto = AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA;
                do {

                    textoEnviar = textoEnviar + "<br/><br/>" + cursorPA.getString(2) + " - PS: " + cursorPA.getInt(3) + "mmHg. - PD: "
                            + cursorPA.getInt(4) + "mmHg. - FC: " + cursorPA.getInt(5) + "lat/min."; // Fecha + valor

                }while(cursorPA.moveToNext());

                EnviarMailSegundoPlano enviarMailSegundoPlanoPA =
                        new EnviarMailSegundoPlano(getApplicationContext(), asunto, textoEnviar, contactoAdministrador);
                Boolean mailEnviadoPA = enviarMailSegundoPlanoPA.execute().get();

//
                if (mailEnviadoPA) {
                    ContentValues values = new ContentValues();
                    values.put(AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO,
                            AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO_ENVIADA);

                    String where = BaseColumns._ID + "= ?";

                    cursorPA.moveToFirst();

                    for(int i=0; i<count; i++){
                        String[] whereArgs = new String[]{String.valueOf(cursorPA.getInt(0))};
                        long controlUpdate = dbMediciones.update(AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA,
                                values, where, whereArgs);

                        cursorPA.moveToNext();
                    }

                }

            }

            cursorPA.close();
            //modificacion 11/1/18
            textoEnviar = "";
            medicionesPeso(dbMediciones, selection, args, orderBy);


        } catch(InterruptedException e){
            e.printStackTrace();
        } catch(ExecutionException e){
            e.printStackTrace();
        }

    }

    private void medicionesPeso(SQLiteDatabase dbMediciones, String selection, String[] args, String orderBy) {

        //BUSCO LOS ESTADOS PENDIENTES EN LA TABLA PESO
        String[] camposDBPESO = new String[]{BaseColumns._ID,
                AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO,
                AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE,
                AutodiagnosticoContract.AutodiagnosticoEntry.PESO_VALOR};

        /*query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
Query the given URL, returning a Cursor over the result set.*/
        Cursor cursorPESO = dbMediciones.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO, camposDBPESO,
                selection, args, null, null, orderBy, null);

        //Existen MEDICIONES DE LA TABLA PESO CON ESTADO PENDIENTE
        if (cursorPESO != null & cursorPESO.moveToFirst()) {
            int countPESO;
            countPESO = cursorPESO.getCount();

            asunto = AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO;
            do {
                textoEnviar = textoEnviar + "<br/><br/>" + cursorPESO.getString(2) + " - PESO: " + cursorPESO.getDouble(3) + "Kgs."; // Fecha + valor
            }while(cursorPESO.moveToNext());

            EnviarMailSegundoPlano enviarMailSegundoPlanoPESO =
                    new EnviarMailSegundoPlano(getApplicationContext(), asunto, textoEnviar, contactoAdministrador);
            Boolean mailEnviadoPESO = null;
            try {
                mailEnviadoPESO = enviarMailSegundoPlanoPESO.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (mailEnviadoPESO) {
                ContentValues values = new ContentValues();
                values.put(AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO,
                        AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO_ENVIADA);

                String where = BaseColumns._ID + "= ?";
                cursorPESO.moveToFirst();

                for(int i=0; i<countPESO; i++){
                    String[] whereArgs = new String[]{String.valueOf(cursorPESO.getInt(0))};
                    long controlUpdate = dbMediciones.update(AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO,
                            values, where, whereArgs);
                    cursorPESO.moveToNext();
                }

            }

        }

        cursorPESO.close();
        textoEnviar = "";
        medicionesSintomas(dbMediciones, selection, args, orderBy);
    }

    private void medicionesSintomas(SQLiteDatabase dbMediciones, String selection, String[] args, String orderBy) {

        //BUSCO LOS ESTADOS PENDIENTES EN LA TABLA SINTOMAS
        String[] camposDBSINTOMAS = new String[]{BaseColumns._ID,
                AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO,
                AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE,
                AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_PREGUNTA,
                AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_RESPUESTA,
        };

                /*query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
Query the given URL, returning a Cursor over the result set.*/
        Cursor cursorSINTOMAS = dbMediciones.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS,
                camposDBSINTOMAS, selection, args, null, null, orderBy, null);

        //Existen MEDICIONES DE LA TABLA PESO CON ESTADO PENDIENTE
        if (cursorSINTOMAS != null & cursorSINTOMAS.moveToFirst()) {
            int countSINTOMAS;
            countSINTOMAS = cursorSINTOMAS.getCount();
            //   Toast.makeText(getApplicationContext(), "Cursor count = " + String.valueOf(countSINTOMAS), Toast.LENGTH_SHORT).show();

            asunto = AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS;
            do {
                textoEnviar = textoEnviar + "<br/><br/>" + cursorSINTOMAS.getString(2) + " - PREGUNTA: " + cursorSINTOMAS.getString(3) + " - RESPUESTA: "
                        + cursorSINTOMAS.getString(4); // Fecha + valor
            }while(cursorSINTOMAS.moveToNext());

            EnviarMailSegundoPlano enviarMailSegundoPlanoSINTOMAS =
                    new EnviarMailSegundoPlano(getApplicationContext(), asunto, textoEnviar, contactoAdministrador);
            Boolean mailEnviadoSINTOMAS = null;
            try {
                mailEnviadoSINTOMAS = enviarMailSegundoPlanoSINTOMAS.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (mailEnviadoSINTOMAS) {
                ContentValues values = new ContentValues();
                values.put(AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO,
                        AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO_ENVIADA);

                String where = BaseColumns._ID + "= ?";

                cursorSINTOMAS.moveToFirst();

                for(int i=0; i<countSINTOMAS; i++){
                    String[] whereArgs = new String[]{String.valueOf(cursorSINTOMAS.getInt(0))};
                    int controlUpdate = dbMediciones.update(AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS,
                            values, where, whereArgs);
                    cursorSINTOMAS.moveToNext();
                }

            }

        }

        cursorSINTOMAS.close();
        textoEnviar = "";
        dbMediciones.close();
        isWorking = false;
        jobFinished(jobParameters, false);

    }


    //ENVIA LAS ALERTAS POR MAIL
    private void enviarAlertas(SQLiteDatabase db, Cursor cursor) throws ExecutionException, InterruptedException {
        int count;
        count = cursor.getCount();
        //   Toast.makeText(getApplicationContext(), "Cursor count = " + String.valueOf(count), Toast.LENGTH_SHORT).show();

        cursor.moveToLast(); //para que muestre primero las últimas alertas generadas

        asunto = "ALERTAS";
        do {
            textoEnviar = textoEnviar + "<br/><br/>" + cursor.getString(2) + " - " + cursor.getString(3) + " - " + cursor.getString(4); // ALERTA + Fecha + descripción
            //    }while (cursor.moveToNext());
        }while(cursor.moveToPrevious()); //para que muestre primero las últimas alertas generadas

        EnviarMailSegundoPlano enviarMailSegundoPlano2 = new EnviarMailSegundoPlano(getApplicationContext(), asunto, textoEnviar, contactosAlertas);
        Boolean mailEnviado2 = enviarMailSegundoPlano2.execute().get();


        if (mailEnviado2) {
            ContentValues values = new ContentValues();
            values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_ENVIADA);

            String where = AlertasContract.AlertasEntry._ID + "= ?";

            cursor.moveToFirst();
            for(int i=0; i<count; i++){

                String[] whereArgs = new String[]{String.valueOf(cursor.getInt(0))};
                int controlUpdate = db.update(AlertasContract.AlertasEntry.TABLE_NAME, values, where, whereArgs);
                cursor.moveToNext();

            }

        }

        isWorking = false;
        jobFinished(jobParameters, false);


    }

    //ENVIA LOS DATOS DE LAS JUGADAS
    private void handleActionJugadas() throws ExecutionException, InterruptedException {

        String textoCampos = "";

        asunto = JuegoContract.JuegoEntry.TABLE_NAME_JUGADA;

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

        //14/08/18 --> cambio enviar mail por generar alerta para subir al servidor --> level_red=90, type_heartRate=30

        //Guardar como alerta en BD xa luego subir al servidor
        String descripcion = textoCampos + textoEnviar;

        //guardar Alarma en BD
        FechaActual fechaActual = new FechaActual();
        String fecha = fechaActual.execute().get();

        Alertas_DBHelper alertasDbHelper = new Alertas_DBHelper(getApplicationContext());
        SQLiteDatabase dbAlerta = alertasDbHelper.getWritableDatabase();

        if(descripcion.length()<=256) {

            ContentValues values = new ContentValues();

            values.put(AlertasContract.AlertasEntry.FECHA, fecha);
            values.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_ROJA);
            values.put(AlertasContract.AlertasEntry.PARAMETRO, JSON_CONSTANTS.HEART_RATES);
            values.put(AlertasContract.AlertasEntry.DESCRIPCION, descripcion);
            values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);

            try {
                long controlInsert = dbAlerta.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);

            } catch (Exception e) {
                e.printStackTrace();
            }

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

                ContentValues values = new ContentValues();

                values.put(AlertasContract.AlertasEntry.FECHA, fecha);
                values.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_ROJA);
                values.put(AlertasContract.AlertasEntry.PARAMETRO, JSON_CONSTANTS.HEART_RATES);
                values.put(AlertasContract.AlertasEntry.DESCRIPCION, textoAuxiliar);
                values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);

                try {
                    long controlInsert = dbAlerta.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }



        }



        ///

        String textoMail;
        textoMail = "Consejos Saludables leídos: " + configuraciones.getContadorConsejosSaludablesLeidos() +
                "<br/><br/>"+ "Preguntas Frecuentes leídas: " + configuraciones.getContadorPreguntasFrecuentesLeidas() ;

        //14/08/18 --> cambio enviar mail por generar alerta para subir al servidor --> level_red=90, type_heartRate=30
             /*   EnviarMailSegundoPlano enviarMailSegundoPlanoCS =
                        new EnviarMailSegundoPlano(getApplicationContext(),
                                "Consejos Saludables - Preguntas Frecuentes",
                                textoMail,
                                contactoAdministrador);
                Boolean mailSegundoPlanoCS = enviarMailSegundoPlanoCS.execute().get();
                */

        //Guardar como alerta en BD xa luego subir al servidor

        //guardar Alarma en BD

        ContentValues values2 = new ContentValues();

        values2.put(AlertasContract.AlertasEntry.FECHA, fecha);
        values2.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_ROJA);
        values2.put(AlertasContract.AlertasEntry.PARAMETRO, JSON_CONSTANTS.HEART_RATES);
        values2.put(AlertasContract.AlertasEntry.DESCRIPCION, textoMail);
        values2.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);

        try{
            long controlInsert = dbAlerta.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values2);

        }catch(Exception e){
            e.printStackTrace();
        }





        //14/08/18 --> GENERAR NUEVA APP DONDE DESCARGAR LOS DATOS EN FORMATO TABLA CUANDO NECESITE HACER LA ESTADÍSTICA
        // enviarTablaDatosPeso();



    /*    EnviarMailSegundoPlano enviarMailSegundoPlanoJugada =
                new EnviarMailSegundoPlano(getApplicationContext(),
                        asunto,
                        textoCampos + textoEnviar,
                        contactoAdministrador);
        Boolean mailEnviadoJugada = enviarMailSegundoPlanoJugada.execute().get();

        String textoMail;
        textoMail = "Consejos Saludables leídos: " + configuraciones.getContadorConsejosSaludablesLeidos() +
                "<br/><br/>"+ "Preguntas Frecuentes leídas: " + configuraciones.getContadorPreguntasFrecuentesLeidas() ;
        EnviarMailSegundoPlano enviarMailSegundoPlanoCS =
                new EnviarMailSegundoPlano(getApplicationContext(),
                        "Consejos Saludables - Preguntas Frecuentes",
                        textoMail,
                        contactoAdministrador);
        enviarMailSegundoPlanoCS.execute().get();

        */


        //14/08/18 --> generar app que me devuelva los datos de esa manera cuando necesite hacer la estadística
        //Enviar mediciones formato separado por comas

  /*      Peso mPeso = new Peso(getApplicationContext());
        textoEnviar = mPeso.getMedicionesCSV();

        asunto = AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO;

        EnviarMailSegundoPlano enviarMailSegundoPlanoPeso =
                new EnviarMailSegundoPlano(getApplicationContext(),
                        asunto,
                        textoEnviar,
                        contactoAdministrador);
        enviarMailSegundoPlanoPeso.execute().get();


        PAFC mPAFC = new PAFC(getApplicationContext());
        textoEnviar = mPAFC.getMedicionesCSV();

        asunto = AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA;

        EnviarMailSegundoPlano enviarMailSegundoPlanoPA =
                new EnviarMailSegundoPlano(getApplicationContext(),
                        asunto,
                        textoEnviar,
                        contactoAdministrador);
        enviarMailSegundoPlanoPA.execute().get();


        Sintomas mSintomas = new Sintomas(getApplicationContext());
        textoEnviar = mSintomas.getMedicionesCSV();

        asunto = AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS;

        EnviarMailSegundoPlano enviarMailSegundoPlanoSintomas =
                new EnviarMailSegundoPlano(getApplicationContext(),
                        asunto,
                        textoEnviar,
                        contactoAdministrador);
        enviarMailSegundoPlanoSintomas.execute().get();
*/

        isWorking = false;
        jobFinished(jobParameters, false);


    }


}
