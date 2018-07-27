package com.luciayanicelli.icsalud.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.Alertas_DBHelper;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_DBHelper;
import com.luciayanicelli.icsalud.DataBase.JuegoContract;
import com.luciayanicelli.icsalud.DataBase.Jugada_DBHelper;
import com.luciayanicelli.icsalud.Notifications.MyReceiverGenerarEmail;
import com.luciayanicelli.icsalud.utils.EnviarMailSegundoPlano;
import com.luciayanicelli.icsalud.utils.PAFC;
import com.luciayanicelli.icsalud.utils.Peso;
import com.luciayanicelli.icsalud.utils.Sintomas;

import java.util.Calendar;
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
public class ServiceGenerarEmail extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
/*    private static final String ACTION_FOO = "com.luciayanicelli.ic_layout.Services.action.FOO";
    private static final String ACTION_BAZ = "com.luciayanicelli.ic_layout.Services.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.luciayanicelli.ic_layout.Services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.luciayanicelli.ic_layout.Services.extra.PARAM2";

*/

    ///VARIABLES

    private String asunto;
    private String textoEnviar = "";

    private AlarmManager alarmManagerSinConexion, alarmManagerSinConexion30;
    private PendingIntent pendingIntentSinConexion, pendingIntentSinConexion30;

    private int contador = 0;
    private String contactosMediciones, contactoAdministrador;

    private Configuraciones configuraciones;


    public ServiceGenerarEmail() {

        super("ServiceGenerarEmail");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
              if (intent != null) {

            final String action = intent.getAction();

            configuraciones = new Configuraciones(getApplicationContext());

            contactosMediciones = configuraciones.getUserEmailRemitente();
            contactoAdministrador = configuraciones.getEmailAdministrator();


                  ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());

                  try {
                      if(conexionInternet.execute().get()){

                          if (Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE.equals(action)) {
                              handleAlertas();
                          }else if (Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE_MEDICIONES.equals(action)){
                              try {
                                  handleActionMediciones();
                              } catch (ExecutionException e) {
                                  e.printStackTrace();
                              } catch (InterruptedException e) {
                                  e.printStackTrace();
                              }
                          }else if(Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE_JUGADAS.equals(action)){
                              handleActionJugadas();
                          }

                      }else{

                          contador = contador ++;
                          if(contador <= 1){
                              //Volver a intentar la conexión a los 30 segundos
                              Intent myIntent30 = new Intent(getApplicationContext(), ServiceGenerarEmail.class);
                              myIntent30.setAction(Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE);
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
                          myIntentEAlertas.setAction(action);

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

                  // }else if (Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE_MEDICIONES.equals(action)){
             /*   if(conectadointernet()){
                    handleActionMediciones();
                }
                */
           // }
        }
    }
    //LUEGO INVERTIR.. PRIMERO CONECTADO A INTERNET Y DESPUES VER LA CONSTANTS

    /**
     * Maneja la acción de ejecución del servicio
     */
    //ENVIA LAS ALERTAS PENDIENTES
    private void handleAlertas() {
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

              /*  do {
                    asunto = AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA;
                    textoEnviar = cursorPA.getString(2) + " - PS: " + cursorPA.getInt(3) + "mmHg. - PD: "
                            + cursorPA.getInt(4) + "mmHg. - FC: " + cursorPA.getInt(5) + "lat/min."; // Fecha + valor

                    EnviarMailSegundoPlano enviarMailSegundoPlanoPA =
                            new EnviarMailSegundoPlano(getApplicationContext(), asunto, textoEnviar);
                    Boolean mailEnviadoPA = enviarMailSegundoPlanoPA.execute().get();
*/
                asunto = AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA;
                do {

                    textoEnviar = textoEnviar + "<br/><br/>" + cursorPA.getString(2) + " - PS: " + cursorPA.getInt(3) + "mmHg. - PD: "
                            + cursorPA.getInt(4) + "mmHg. - FC: " + cursorPA.getInt(5) + "lat/min."; // Fecha + valor

                }while(cursorPA.moveToNext());

                    EnviarMailSegundoPlano enviarMailSegundoPlanoPA =
                            new EnviarMailSegundoPlano(getApplicationContext(), asunto, textoEnviar, contactosMediciones);
                    Boolean mailEnviadoPA = enviarMailSegundoPlanoPA.execute().get();

//
                    if (mailEnviadoPA.booleanValue()) {
                        ContentValues values = new ContentValues();
                        values.put(AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO,
                                AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO_ENVIADA);

                        String where = BaseColumns._ID + "= ?";
                    //    String[] whereArgs = new String[]{String.valueOf(cursorPA.getInt(0))};

                        //String[] whereArgs = new String[count];

                        //11/1/18
                        cursorPA.moveToFirst();

                        for(int i=0; i<count; i++){
                        //    whereArgs[0]= String.valueOf(cursorPA.getInt(0));
                            String[] whereArgs = new String[]{String.valueOf(cursorPA.getInt(0))};
                            long controlUpdate = dbMediciones.update(AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA,
                                    values, where, whereArgs);

                            if (controlUpdate == 1) {
                                //Se modificó correctamente el estado de PENDIENTE A ENVIADA
                                // cursor.moveToNext();

                            } else {
                                //si no se modificó, salgo del bucle
                                //   break; //Salgo del bucle
                            }

                            cursorPA.moveToNext();
                        }






                    }

              //  } while (cursorPA.moveToNext());

            }

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

        // String selectionPESO = AutodiagnosticoContract.AutodiagnosticoEntry.PESO_ESTADO + "= ?";

                /*query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
Query the given URL, returning a Cursor over the result set.*/
        Cursor cursorPESO = dbMediciones.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO, camposDBPESO,
                selection, args, null, null, orderBy, null);

        //Existen MEDICIONES DE LA TABLA PESO CON ESTADO PENDIENTE
        if (cursorPESO != null & cursorPESO.moveToFirst()) {
            int countPESO;
            countPESO = cursorPESO.getCount();
            //     Toast.makeText(getApplicationContext(), "Cursor count = " + String.valueOf(countPESO), Toast.LENGTH_SHORT).show();

            asunto = AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO;
            do {
                textoEnviar = textoEnviar + "<br/><br/>" + cursorPESO.getString(2) + " - PESO: " + cursorPESO.getDouble(3) + "Kgs."; // Fecha + valor
            }while(cursorPESO.moveToNext());

            EnviarMailSegundoPlano enviarMailSegundoPlanoPESO =
                    new EnviarMailSegundoPlano(getApplicationContext(), asunto, textoEnviar, contactosMediciones);
            Boolean mailEnviadoPESO = null;
            try {
                mailEnviadoPESO = enviarMailSegundoPlanoPESO.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (mailEnviadoPESO.booleanValue()) {
                ContentValues values = new ContentValues();
                values.put(AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO,
                        AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO_ENVIADA);

                String where = BaseColumns._ID + "= ?";
                //   String[] whereArgs = new String[]{String.valueOf(cursorPESO.getInt(0))};
              //  String[] whereArgs = new String[countPESO];

                cursorPESO.moveToFirst();

                for(int i=0; i<countPESO; i++){
                    String[] whereArgs = new String[]{String.valueOf(cursorPESO.getInt(0))};
                    long controlUpdate = dbMediciones.update(AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO,
                            values, where, whereArgs);
                    if (controlUpdate == 1) {
                        //Se modificó correctamente el estado de PENDIENTE A ENVIADA
                        // cursor.moveToNext();

                    } else {
                        //si no se modificó, salgo del bucle
                        // break; //Salgo del bucle
                    }

                    cursorPESO.moveToNext();
                }
                //  int controlUpdate = 1;


                //       Toast.makeText(getApplicationContext(), "Control update = " + String.valueOf(controlUpdate), Toast.LENGTH_SHORT).show();


            }

            // } while (cursorPESO.moveToNext());
        }

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
                    new EnviarMailSegundoPlano(getApplicationContext(), asunto, textoEnviar, contactosMediciones);
            Boolean mailEnviadoSINTOMAS = null;
            try {
                mailEnviadoSINTOMAS = enviarMailSegundoPlanoSINTOMAS.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (mailEnviadoSINTOMAS.booleanValue()) {
                ContentValues values = new ContentValues();
                values.put(AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO,
                        AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO_ENVIADA);

                String where = BaseColumns._ID + "= ?";

                cursorSINTOMAS.moveToFirst();

                for(int i=0; i<countSINTOMAS; i++){
                    String[] whereArgs = new String[]{String.valueOf(cursorSINTOMAS.getInt(0))};
                    int controlUpdate = dbMediciones.update(AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS,
                            values, where, whereArgs);

                    if (controlUpdate == 1) {
                        //Se modificó correctamente el estado de PENDIENTE A ENVIADA
                        // cursor.moveToNext();

                    } else {
                        //si no se modificó, salgo del bucle
                        //  break; //Salgo del bucle
                    }
                    cursorSINTOMAS.moveToNext();
                }

            }

        }

        textoEnviar = "";
        dbMediciones.close();
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

            EnviarMailSegundoPlano enviarMailSegundoPlano2 = new EnviarMailSegundoPlano(getApplicationContext(), asunto, textoEnviar, contactosMediciones);
            Boolean mailEnviado2 = enviarMailSegundoPlano2.execute().get();


            if (mailEnviado2.booleanValue()) {
                ContentValues values = new ContentValues();
                values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_ENVIADA);

                String where = AlertasContract.AlertasEntry._ID + "= ?";

                cursor.moveToFirst();
                for(int i=0; i<count; i++){

                    String[] whereArgs = new String[]{String.valueOf(cursor.getInt(0))};
                    int controlUpdate = db.update(AlertasContract.AlertasEntry.TABLE_NAME, values, where, whereArgs);

                    if (controlUpdate == 1) {
                        //Se modificó correctamente el estado de PENDIENTE A ENVIADA
                        // cursor.moveToNext();

                    } else {
                        //si no se modificó, salgo del bucle
                        // break; //Salgo del bucle
                    }
                    cursor.moveToNext();

                }

            }

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

                    textoEnviar = textoEnviar + "<br/><br/>" + textoAuxiliar;

                } while (cursorJugada.moveToNext());

            }

                EnviarMailSegundoPlano enviarMailSegundoPlanoJugada =
                        new EnviarMailSegundoPlano(getApplicationContext(),
                                asunto,
                                textoCampos + textoEnviar,
                                contactoAdministrador);
                Boolean mailEnviadoJugada = enviarMailSegundoPlanoJugada.execute().get();

//
                if (mailEnviadoJugada.booleanValue()) {
                   //envío correcto
                }else{
                    //envío incorrecto
                }

                String textoMail;
                textoMail = "Consejos Saludables leídos: " + configuraciones.getContadorConsejosSaludablesLeidos() +
                        "<br/><br/>"+ "Preguntas Frecuentes leídas: " + configuraciones.getContadorPreguntasFrecuentesLeidas() ;
                EnviarMailSegundoPlano enviarMailSegundoPlanoCS =
                        new EnviarMailSegundoPlano(getApplicationContext(),
                                "Consejos Saludables - Preguntas Frecuentes",
                                textoMail,
                                contactoAdministrador);
                Boolean mailSegundoPlanoCS = enviarMailSegundoPlanoCS.execute().get();

                enviarTablaDatosPeso();


    }

    private void enviarTablaDatosPeso() throws ExecutionException, InterruptedException {

       Peso mPeso = new Peso(getApplicationContext());
       textoEnviar = mPeso.getMedicionesCSV();

       asunto = AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO;

        EnviarMailSegundoPlano enviarMailSegundoPlanoPeso =
                new EnviarMailSegundoPlano(getApplicationContext(),
                        asunto,
                        textoEnviar,
                        contactoAdministrador);
        enviarMailSegundoPlanoPeso.execute().get();

       enviarDataTablaPA();


    }

    private void enviarDataTablaPA() throws ExecutionException, InterruptedException {

        PAFC mPAFC = new PAFC(getApplicationContext());
        textoEnviar = mPAFC.getMedicionesCSV();

        asunto = AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA;

        EnviarMailSegundoPlano enviarMailSegundoPlanoPA =
                new EnviarMailSegundoPlano(getApplicationContext(),
                        asunto,
                        textoEnviar,
                        contactoAdministrador);
        enviarMailSegundoPlanoPA.execute().get();

        enviarDataTablaSintomas();
    }

    private void enviarDataTablaSintomas() throws ExecutionException, InterruptedException {

        Sintomas mSintomas = new Sintomas(getApplicationContext());
        textoEnviar = mSintomas.getMedicionesCSV();

        asunto = AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS;

        EnviarMailSegundoPlano enviarMailSegundoPlanoSintomas =
                new EnviarMailSegundoPlano(getApplicationContext(),
                        asunto,
                        textoEnviar,
                        contactoAdministrador);
        enviarMailSegundoPlanoSintomas.execute().get();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
