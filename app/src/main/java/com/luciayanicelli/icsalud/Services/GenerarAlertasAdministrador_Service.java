package com.luciayanicelli.icsalud.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;
import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.DataBase.JuegoContract;
import com.luciayanicelli.icsalud.DataBase.Jugada_DBHelper;
import com.luciayanicelli.icsalud.Notifications.MyReceiverGenerarAlertasAdministrador;
import com.luciayanicelli.icsalud.utils.Alertas;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class GenerarAlertasAdministrador_Service extends IntentService {

    ///VARIABLES

    private String asunto;
    private String textoEnviar = "";

    private AlarmManager alarmManagerSinConexion, alarmManagerSinConexion30;
    private PendingIntent pendingIntentSinConexion, pendingIntentSinConexion30;

    private int contador = 0;
    private String contactosAlertas, contactoAdministrador;

    private Configuraciones configuraciones;


    public GenerarAlertasAdministrador_Service() {

        super("GenerarAlertasAdministrador_Service");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
              if (intent != null) {

            final String action = intent.getAction();

            configuraciones = new Configuraciones(getApplicationContext());

            contactosAlertas = configuraciones.getUserEmailRemitente();

            contactoAdministrador = configuraciones.getEmailAdministrator();


                  ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());

                  try {
                      if(conexionInternet.execute().get()){

                          if(Constants.SERVICE_GENERAR_ALERTAS_ADMINISTRADOR.equals(action)){
                              handleActionGenerarAlertasAdministrador();
                          }

                         /* if (Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE.equals(action)) {
                              actualizarContactosMediciones();
                              handleAlertas();
                          }else if (Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE_MEDICIONES.equals(action)){
                              try {
                                  handleActionMediciones();
                              } catch (ExecutionException e) {
                                  e.printStackTrace();
                              } catch (InterruptedException e) {
                                  e.printStackTrace();
                              }
                          }else if(Constants.SERVICE_GENERAR_ALERTAS_ADMINISTRADOR.equals(action)){
                              handleActionGenerarAlertasAdministrador();
                          }
                          */

                      }else{

                          contador = contador ++;
                          if(contador <= 1){
                              //Volver a intentar la conexión a los 30 segundos
                              Intent myIntent30 = new Intent(getApplicationContext(), GenerarAlertasAdministrador_Service.class);
                              myIntent30.setAction(Constants.SERVICE_GENERAR_ALERTAS_ADMINISTRADOR);
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

                          Intent myIntentEAlertas = new Intent(getApplicationContext(), MyReceiverGenerarAlertasAdministrador.class);
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


        }
    }

    //GENERA ALERTAS XA QUE EL ADMINISTRADOR PUEDA ACCEDER A ESTA INFO
    private void handleActionGenerarAlertasAdministrador() throws ExecutionException, InterruptedException {

        String textoCampos = "";

        //  asunto = JuegoContract.JuegoEntry.TABLE_NAME_JUGADA;

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
        Alertas mAlertas = new Alertas(getApplicationContext());



   /*     FechaActual fechaActual = new FechaActual();
        String fecha = fechaActual.execute().get();

        Alertas_DBHelper alertasDbHelper = new Alertas_DBHelper(getApplicationContext());
        SQLiteDatabase dbAlerta = alertasDbHelper.getWritableDatabase();
*/
        if(descripcion.length()<=256) {

            mAlertas.guardar(AlertasContract.AlertasEntry.ALERTA_TIPO_VERDE,
                    AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA,
                    descripcion,
                    AlertasContract.AlertasEntry.ALERTA_VISIBILIDAD_PRIVADA);

         /*   ContentValues values = new ContentValues();

            values.put(AlertasContract.AlertasEntry.FECHA, fecha);
            values.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_VERDE);
            values.put(AlertasContract.AlertasEntry.PARAMETRO, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA);
            values.put(AlertasContract.AlertasEntry.DESCRIPCION, descripcion);
            values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);
            values.put(AlertasContract.AlertasEntry.VISIBILIDAD, AlertasContract.AlertasEntry.ALERTA_VISIBILIDAD_PRIVADA);

            try {
                long controlInsert = dbAlerta.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);

            } catch (Exception e) {
                e.printStackTrace();
            }
            */

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

              /*  ContentValues values = new ContentValues();

                values.put(AlertasContract.AlertasEntry.FECHA, fecha);
                values.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_VERDE);
                values.put(AlertasContract.AlertasEntry.PARAMETRO, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA);
                values.put(AlertasContract.AlertasEntry.DESCRIPCION, textoAuxiliar);
                values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);
                values.put(AlertasContract.AlertasEntry.VISIBILIDAD, AlertasContract.AlertasEntry.ALERTA_VISIBILIDAD_PRIVADA);

                try {
                    long controlInsert = dbAlerta.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);

                } catch (Exception e) {
                    e.printStackTrace();
                }
*/
            }



        }



        ///

        String textoMail;
        textoMail = "Consejos Saludables leídos: " + String.valueOf(configuraciones.getContadorConsejosSaludablesLeidos()) +
                "<br/><br/>"+ "Preguntas Frecuentes leídas: " + String.valueOf(configuraciones.getContadorPreguntasFrecuentesLeidas()) ;

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
        mAlertas.guardar(AlertasContract.AlertasEntry.ALERTA_TIPO_VERDE,
                JSON_CONSTANTS.HEART_RATES,
                textoMail,
                AlertasContract.AlertasEntry.ALERTA_VISIBILIDAD_PRIVADA);

     /*   ContentValues values2 = new ContentValues();

        values2.put(AlertasContract.AlertasEntry.FECHA, fecha);
        values2.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_VERDE);
        values2.put(AlertasContract.AlertasEntry.PARAMETRO, JSON_CONSTANTS.HEART_RATES);
        values2.put(AlertasContract.AlertasEntry.DESCRIPCION, textoMail);
        values2.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);
        values2.put(AlertasContract.AlertasEntry.VISIBILIDAD, AlertasContract.AlertasEntry.ALERTA_VISIBILIDAD_PRIVADA);

        try{
            long controlInsert = dbAlerta.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values2);

        }catch(Exception e){
            e.printStackTrace();
        }
*/




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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
