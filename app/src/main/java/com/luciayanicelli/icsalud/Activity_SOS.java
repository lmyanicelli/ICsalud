package com.luciayanicelli.icsalud;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.Activity_profesionales.Activity_profesionales;
import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.Alertas_DBHelper;
import com.luciayanicelli.icsalud.Services.Constants;
import com.luciayanicelli.icsalud.utils.AlertDialogs;
import com.luciayanicelli.icsalud.utils.EnviarMailSegundoPlano;
import com.luciayanicelli.icsalud.utils.EnviarMsjTexto;
import com.luciayanicelli.icsalud.utils.FechaActual;
import com.luciayanicelli.icsalud.utils.SetearAlarma;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/*
Esta actividad se encarga de solicitar los permisos al usuario para obtener su ubicación actual y enviarla a sus contactos en busca
de auxilio por msj de texto y correo electrónico.
Además le da la opción al paciente de llamar una ambulancia (*107) directamente luego de pedir auxilio

-- OBTENER LOS DATOS DESDE CONFIGURACIÓN

 */

public class Activity_SOS extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, AlertDialogs.NoticeDialogListener {

    //private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 102;
  //  private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Geocoder mGeocoder;

    //Vista
    private TextView edittext;
    private ProgressBar progressBar;

 //   private LatLng mlatlng = new LatLng(-34, 151);
    private Double lat, lng;

  //  private MarkerOptions marcador = new MarkerOptions();


    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final int PETICION_CONFIG_UBICACION = 201;

    private LocationRequest locRequest;
 //   private Marker marker;

    private Location lastLocation = null;


    private String nombrePaciente = ""; //Esto hay q obtenerlo de la Configuración BD
    private String textoEnviar;
    //   private String[] telefonos = new String[]{"0381156674907", "3816674907", "0381156674907", "543816674907"};
    private String[] telefonos = new String[]{""};
 //   private boolean msjEnviado = false;
    private  String direccion;
    private String telefono_ambulancia;


 //   Session session;


    //Para solucionar el problema de que no se puede crear el alert dialog
    //cuando el usuario cancela la autorizacion para utilizar el gps
    private boolean mReturningWithResult = false;

    //Fecha actual
    private String fecha;

    private Configuraciones configuraciones;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 93;
    private static final int REQUEST_CODE_ASK_PERMISSIONS_CALL = 94;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);


        edittext = findViewById(R.id.edittext);
        edittext.setText("Procesando solicitud");
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0); //inicializo en 0 el progreso
        progressBar.setVisibility(View.VISIBLE); //Hago visible la barra de progreso circular

        //Instancia de GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //Geocoder para localización
        mGeocoder = new Geocoder(getApplicationContext());


        //INICIALIZO VALORES SEGÚN CONFIGURACION

        configuraciones = new Configuraciones(getApplicationContext());

        nombrePaciente = configuraciones.getUserSurname() + ", " + configuraciones.getUserName();

        telefonos = new String[]{configuraciones.getUserCelContacts()};

        telefono_ambulancia = configuraciones.getUserTelefonoAmbulancia();


        //Solicitar permisos en línea
        //checkPermission();

    }

/*
    private void checkPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            Toast.makeText(this, "This version is not Android 6 or later " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();

        } else {

            int hasAccessFineLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasAccessFineLocationPermission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);

                Toast.makeText(this, "Requesting permissions", Toast.LENGTH_LONG).show();

            }else if (hasAccessFineLocationPermission == PackageManager.PERMISSION_GRANTED){

                Toast.makeText(this, "The permissions are already granted ", Toast.LENGTH_LONG).show();
           //     openCamera();
                //dosomething

            }

        }

        return;
    }
    */

 /*   @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(REQUEST_CODE_ASK_PERMISSIONS == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "OK Permissions granted ! " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
                openCamera();
            } else {
                Toast.makeText(this, "Permissions are not granted ! " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    */

    //Corrobora que se encuentren habilitadas las configuraciones necesarias (activado el GPS) para obtener la ubicacion
    //en caso de no poseerlas pide permiso al usuario para configurarlas correctamente
    private void enableLocationUpdates() {



        locRequest = LocationRequest.create();
        locRequest.setInterval(2 * 1000 * 60); //2*1000*60 = 2minutos //Periodicidad de actualizaciones.
        // Se establece mediante el método setInterval() y define cada cuanto tiempo (en milisegundos)
        // nos gustaría recibir datos actualizados de la posición. De esta forma,
        // si queremos recibir la nueva posición cada 2 segundos utilizaremos setInterval(2000).
        locRequest.setFastestInterval(1000);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest locSettingsRequest =
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(locRequest)
                        .build();

        //Hace el pedido de comprobación
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient, locSettingsRequest);


        //Aquí se observan los resultados de la comprobación de GPS activado o no
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        //El GPS se encuentra activado
                        Log.i(LOGTAG, "Configuración correcta");
                        startLocationUpdates();

                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {

                            //El GPS no se encuentra activado
                            Log.i(LOGTAG, "Se requiere actuación del usuario");

                            //CON ESTA LLAMADA PIDE AUTORIZACION AL USUARIO XA USAR GPS
                            status.startResolutionForResult(Activity_SOS.this, PETICION_CONFIG_UBICACION);
                            //LOS RESULTADOS SE VEN EN onActivityResult

                        } catch (IntentSender.SendIntentException e) {
                            Log.i(LOGTAG, "Error al intentar solucionar configuración de ubicación");
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(LOGTAG, "No se puede cumplir la configuración de ubicación necesaria");
                        Toast.makeText(getApplicationContext()
                                , "No se puede cumplir la configuración de ubicación necesaria"
                                , Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    //Deshabilita las actualizaciones de ubicación
    private void disableLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);

    }


    //Inicia las actualizaciones de ubicación
    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(Activity_SOS.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Activity_SOS.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);


            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");

            //Solicita que se empiece a actualizar la ubicación
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locRequest, Activity_SOS.this);


        }
    }


    //Una vez que está conectado a Google Play Services
    @Override
    public void onConnected(Bundle connectionHint) {

        //COMPRUEBA QUE TENGA LAS AUTORIZACIONES EN EL MANIFEST PARA ACCEDER A LAS UBICACIONES... PERO NO ES LO DEL GPS... ESO ESTÁ EN enableLocationUpdates()
        if (ActivityCompat.checkSelfPermission(Activity_SOS.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

           //SI NO TIENE LOS PERMISOS EN EL MANIFEST - LOS PIDE
            ActivityCompat.requestPermissions(Activity_SOS.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
            //LOS RESULTADOS DE ESTA PETICION SE VEN EN onRequestPermissionResult


        } else {
            //SI SI TIENE LOS PERMISOS EN EL MANIFEST

            //Controlo q no tenga ninguna ubicación guardada
            enableLocationUpdates();
        }


    }


    @Override
    public void onConnectionSuspended(int i) {
//Se ha interrumpido la conexión con Google Play Services
        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
        Toast.makeText(getApplicationContext(), "Se ha interrumpido la conexión con Google Play Services", Toast.LENGTH_LONG).show();
    }


    //ACTUALIZA la Interfaz de Usuario - en realidad no hace nada - podría reemplazar directamente por setLocation
    private void updateUI(Location loc) {
        if (loc != null) {
            //Para ver las coordenadas en lenguaje humano
            setLocation(loc);

            //Cargo las coordenadas actuales en la variables correspondientes
            lat = loc.getLatitude();
            lng = loc.getLongitude();
        }
    }


    //Se ven los resultados de los permisos del manifest
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case PETICION_PERMISO_LOCALIZACION:

                if (grantResults.length == 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Una vez q corrobora que los permisos fueron concedidos

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }

                    lastLocation =
                            LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                    if (lastLocation == null) {

                        startLocationUpdates();

                        //Esto solucionó el bucle
                        updateUI(lastLocation);

                    } else {

                        updateUI(lastLocation);

                    }

                } else {
                    //Permiso denegado:
                    //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.

                    Log.e(LOGTAG, "Permiso denegado");

                    //08/03/17
                    disableLocationUpdates();

                    pedirAuxilio("Ubicación no disponible - Por Favor contacte al paciente");

                    //Una vez q el usuario cancela la autorización de usar el gps, se crea el alert dialogo para que llame una ambulancia
                    edittext.setText(getResources().getString(R.string.msj_gps_cancelado_llamar));
                    progressBar.setVisibility(View.INVISIBLE);
                    crearAlertDialog();
                }
                break;


            case REQUEST_CODE_ASK_PERMISSIONS:

                if (grantResults.length == 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Una vez q corrobora que los permisos fueron concedidos

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED | ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.



                        return;
                    }

                    EnviarMsjTexto enviarMsjTexto = new EnviarMsjTexto(getApplicationContext(), textoEnviar, telefonos);
                    enviarMsjTexto.execute();
                }

                break;


            case REQUEST_CODE_ASK_PERMISSIONS_CALL:

                if (grantResults.length == 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Una vez q corrobora que los permisos fueron concedidos

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED | ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PRIVILEGED) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    llamarTelefono(telefono_ambulancia);
                }

                break;

            default:
                break;

        }


    }

    //Respuestas luego de solicitar al usuario que active el GPS
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PETICION_CONFIG_UBICACION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //luego de que el usuario autoriza a utilizar el gps
                        startLocationUpdates();
                        break;

                    case Activity.RESULT_CANCELED:
                        Log.i(LOGTAG, "El usuario no ha realizado los cambios de configuración necesarios");

                        Toast.makeText(getApplicationContext(), R.string.msj_gps_cancelado, Toast.LENGTH_LONG).show();

                        //Quizás podría aparecer algo en pantalla que muestre un msj más grande y que le de la posibilidad de reintentarlo
                        //o enviar un msj y mail de auxilio sin la ubicación correspondiente

                        //ENVIAR MSJ Y MAIL SOLICITANDO AUXILIO SIN UBICACIÓN
                        pedirAuxilio("Ubicación no disponible - Por Favor contacte al paciente");

                        //Consulta si desea llamar una ambulancia
                  //15/03 NO PERMITE REALIZAR ESTA ACCIÓN AQUÍ -- SOLUCION: CREAR UN BOOLEANO Y PONER LA ACCION EN onPostResume() para que la app ya haya restablecido sus valores
                             mReturningWithResult = true;

                        break;
                }
                break;
        }
    }



    private void pedirAuxilio(String direccion) {

        if (textoEnviar == null){
            textoEnviar = nombrePaciente + " -SOS- " + direccion;
        }

        checkPermissionSMS();

        EnviarMailSegundoPlano enviarMailSegundoPlano = new EnviarMailSegundoPlano(getApplicationContext(),
                "ALERTA SOS - ",
                textoEnviar,
                configuraciones.getUserEmailContacts());
        enviarMailSegundoPlano.execute();



        //Cargar AlertaRoja en BD Alertas
        FechaActual fechaActual = new FechaActual();
        try {
            fecha = fechaActual.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        Alertas_DBHelper mDBHelper = new Alertas_DBHelper(getApplicationContext());
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AlertasContract.AlertasEntry.FECHA, fecha);
        values.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_ROJA);
        values.put(AlertasContract.AlertasEntry.PARAMETRO, AlertasContract.AlertasEntry.ALERTA_PARAMETRO_SOS);
        values.put(AlertasContract.AlertasEntry.DESCRIPCION, textoEnviar);
        values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);

        long controlInsert = db.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);
    //    Toast.makeText(getApplicationContext(), "ControlInsert= " + String.valueOf(controlInsert), Toast.LENGTH_LONG).show();

        //Sevice Generar Email - ENVIAR ALERTAS
        /*Intent intentMAIL = new Intent(getApplicationContext(), ServiceGenerarEmail.class);
        intentMAIL.setAction(Constants.SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE);
        startService(intentMAIL);
        */

        //SERVICE GENERAR EMAIL ALERTAS
        SetearAlarma setearAlarma = new SetearAlarma(getApplicationContext(), Constants.PARAMETRO_GENERAR_EMAIL_ALERTAS);
        setearAlarma.execute();

    }

    private void checkPermissionSMS() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            EnviarMsjTexto enviarMsjTexto = new EnviarMsjTexto(getApplicationContext(), textoEnviar, telefonos);
            enviarMsjTexto.execute();
        //    Toast.makeText(getApplicationContext(), "This version is not Android 6 or later " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();

        } else {

            int hasSMSPermission = checkSelfPermission(Manifest.permission.SEND_SMS);

            int hasSMS2Permission = checkSelfPermission(Manifest.permission.GET_ACCOUNTS);

            if (hasSMSPermission != PackageManager.PERMISSION_GRANTED | hasSMS2Permission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[] {Manifest.permission.SEND_SMS},
                        REQUEST_CODE_ASK_PERMISSIONS);

                requestPermissions(new String[] {Manifest.permission.GET_ACCOUNTS},
                        REQUEST_CODE_ASK_PERMISSIONS);

              //  Toast.makeText(this, "Requesting permissions", Toast.LENGTH_LONG).show();


            }else{
              //  Toast.makeText(this, "The permissions are already granted ", Toast.LENGTH_LONG).show();
                EnviarMsjTexto enviarMsjTexto = new EnviarMsjTexto(getApplicationContext(), textoEnviar, telefonos);
                enviarMsjTexto.execute();

            }

        }


    }


    /*
    * Esta función actúa luego del onActivityResult una vez que se estabilizan nuevamente los parámetros
    * */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mReturningWithResult) {
            // Commit your transactions here.

            //Una vez q el usuario cancela la autorización de usar el gps, se crea el alert dialogo para que llame una ambulancia
            edittext.setText(getResources().getString(R.string.msj_gps_cancelado_llamar));
            progressBar.setVisibility(View.INVISIBLE);
            crearAlertDialog();
        }
        // Reset the boolean flag back to false for next time.
        mReturningWithResult = false;
    }


    //Obtiene la dirección en lenguaje humano en función de las coordenadas obtenidas
    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {

            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                lat = loc.getLatitude();
                lng = loc.getLongitude();

                if (!list.isEmpty()) {
                    Address address = list.get(0);
                    String ubicacion = getResources().getString(R.string.ubicacion);

                    if (address.getAddressLine(0) != null & address.getLocality() != null) {
                        direccion = address.getAddressLine(0) + ", " + address.getLocality();
                    } else if (address.getLocality() != null & address.getCountryName() != null) {
                        direccion = address.getLocality() + ", " + address.getCountryName();
                    } else if (address.getLocality() == null & address.getCountryName() != null) {
                        direccion = address.getCountryName();
                    } else {
                        //Error
                        direccion = "Ubicación no disponible";
                    }

                    //En base a la respuesta, cargo el valor en el textView para mostrar por pantalla al usuario
                    if (direccion == "Ubicación no disponible"){
                        edittext.setText(direccion);
                        progressBar.setVisibility(View.INVISIBLE);
                    }else{
                        edittext.setText(ubicacion + direccion);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    //ESTA DIRECCIÓN DEBERÍA SER ENVIADA AL AUXILIO: AMBULANCIA, FAMILIAR, MEDICO (por mail y por msj de texto)
                    textoEnviar = "SOS " + nombrePaciente + " - DIRECCION: " + direccion + "\n Latitud: " + String.valueOf(lat) + " \n Longitud: " + String.valueOf(lng);

                    //Envía mail y msjs de texto con los datos proporcionados
                   pedirAuxilio(direccion);

                    //ALERT DIALOG propone llamar a su ambulancia
                    crearAlertDialog();

                }
            } catch (IOException e) {
                e.printStackTrace();
                reintentar(loc);
            }
        }else{
            //Error
            direccion = "Ubicación no disponible";

            //En base a la respuesta, cargo el valor en el textView para mostrar por pantalla al usuario

                edittext.setText(direccion);
                progressBar.setVisibility(View.INVISIBLE);


            //ESTA DIRECCIÓN DEBERÍA SER ENVIADA AL AUXILIO: AMBULANCIA, FAMILIAR, MEDICO (por mail y por msj de texto)
            textoEnviar = "SOS " + nombrePaciente + " - DIRECCION: " + direccion + "\n Latitud: " + String.valueOf(lat) + " \n Longitud: " + String.valueOf(lng);

            //Envía mail y msjs de texto con los datos proporcionados
            pedirAuxilio(direccion);

            //ALERT DIALOG propone llamar a su ambulancia
            crearAlertDialog();
        }
    }

    private void reintentar(Location loc) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        lat = loc.getLatitude();
        lng = loc.getLongitude();

        if (!list.isEmpty()) {
            Address address = list.get(0);
            String ubicacion = getResources().getString(R.string.ubicacion);

            if (address.getAddressLine(0) != null & address.getLocality() != null) {
                direccion = address.getAddressLine(0) + ", " + address.getLocality();
            } else if (address.getLocality() != null & address.getCountryName() != null) {
                direccion = address.getLocality() + ", " + address.getCountryName();
            } else if (address.getLocality() == null & address.getCountryName() != null) {
                direccion = address.getCountryName();
            } else {
                //Error
                direccion = "Ubicación no disponible";
            }

            //En base a la respuesta, cargo el valor en el textView para mostrar por pantalla al usuario
            if (direccion == "Ubicación no disponible") {
                edittext.setText(direccion);
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                edittext.setText(ubicacion + direccion);
                progressBar.setVisibility(View.INVISIBLE);
            }

            //ESTA DIRECCIÓN DEBERÍA SER ENVIADA AL AUXILIO: AMBULANCIA, FAMILIAR, MEDICO (por mail y por msj de texto)
            textoEnviar = "SOS " + nombrePaciente + " - DIRECCION: " + direccion + "\n Latitud: " + String.valueOf(lat) + " \n Longitud: " + String.valueOf(lng);

            //Envía mail y msjs de texto con los datos proporcionados
            pedirAuxilio(direccion);

            //ALERT DIALOG propone llamar a su ambulancia
            crearAlertDialog();
        }
    }


    private void crearAlertDialog() {

        AlertDialogs alertDialogs = new AlertDialogs();
        String msj;
        if(configuraciones.getUserCelContacts()==null){
            msj = "No tiene ningún contacto agendado. Llame al *107 para solicitar una ambulancia.";
            alertDialogs.setName("107");
            alertDialogs.setMsj(msj);

            alertDialogs.setPositiveButton(getResources().getString(R.string.btn_llamar));
            alertDialogs.setNegativeButton(getResources().getString(R.string.cancelar));

            DialogFragment alertSOS = alertDialogs;
            alertSOS.show(getSupportFragmentManager(), "alertSOS");
        }else{
            msj = "¿Desea llamar a alguno de sus contactos para pedirle ayuda o llamar una ambulancia?";
            alertDialogs.setName("contacto");
            alertDialogs.setMsj(msj);

            alertDialogs.setPositiveButton(getResources().getString(R.string.llamar_contacto));
            alertDialogs.setNegativeButton(getResources().getString(R.string.llamar_ambulancia));

            DialogFragment alertSOS = alertDialogs;
            alertSOS.show(getSupportFragmentManager(), "alertSOS");
        }


    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOGTAG, "Recibida nueva ubicación!");

        //QUIZÁS ESTO ME CONVENGA ELIMINAR PARA ESTA APP EN PARTICULAR
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.

        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");

        Toast.makeText(getApplicationContext(), "Error al conectar con Google Play Services", Toast.LENGTH_LONG).show();
    }



    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String name) {

        switch (name){

            case "107":

                //llamarTelefono(telefono_ambulancia);
                checkPermissionCALL();

                break;

            case "contacto":

                Intent mIntent = new Intent();
                mIntent.putExtra("pos", 4);
                mIntent.setClass(getApplicationContext(), Activity_profesionales.class);
                this.startActivity(mIntent);

                break;

            default:
                break;
        }


    }

    private void checkPermissionCALL() {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

                llamarTelefono(telefono_ambulancia);

            } else {

                int hasCALLPermission = checkSelfPermission(Manifest.permission.CALL_PHONE);

                int hasCALL2Permission = checkSelfPermission(Manifest.permission.CALL_PRIVILEGED);

                if (hasCALLPermission != PackageManager.PERMISSION_GRANTED | hasCALL2Permission != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[] {Manifest.permission.CALL_PHONE},
                            REQUEST_CODE_ASK_PERMISSIONS_CALL);

                    requestPermissions(new String[] {Manifest.permission.CALL_PRIVILEGED},
                            REQUEST_CODE_ASK_PERMISSIONS_CALL);

                }else{

                    llamarTelefono(telefono_ambulancia);

                }

            }

    }

    private void llamarTelefono(String tlf) {

        Intent llamada = new Intent(Intent.ACTION_DIAL);
        Uri uriTlf = Uri.parse("tel:" + tlf);
        llamada.setData(uriTlf);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        startActivity(llamada);
        //cierro la actividad actual
        this.finish();

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, String name) {
        //finalizo la actividad para regresar a la actividad en la que estaba

        switch (name){

            case "contacto":
                checkPermissionCALL();

                break;

                default:
                    this.finish();
                    break;
        }

    }
}
