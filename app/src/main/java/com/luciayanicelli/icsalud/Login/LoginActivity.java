package com.luciayanicelli.icsalud.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.Api_Json.Buscar_Patient;
import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;
import com.luciayanicelli.icsalud.Api_Json.JSON_functions;
import com.luciayanicelli.icsalud.Api_Json.Json_Request_Access_Token_Password;
import com.luciayanicelli.icsalud.Api_Json.Patients;
import com.luciayanicelli.icsalud.MainActivity;
import com.luciayanicelli.icsalud.R;
import com.luciayanicelli.icsalud.Services.ConexionInternet;
import com.luciayanicelli.icsalud.Services.Constants;
import com.luciayanicelli.icsalud.utils.SetearAlarma;

import org.json.JSONException;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by LuciaYanicelli on 25/10/2017.
 *
 * https://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;


    private EditText _emailText;
    private EditText _passwordText;
    private Button _loginButton;
    private TextView _signupLink;
    private Configuraciones config;
    private TextView _signupLinkContrasena;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Controla si el usuario ya está logueado
        config = new Configuraciones(getApplicationContext());

        if(!config.getEstadoLogin()) {


            setContentView(R.layout.activity_login);
            //ButterKnife.bind(this); //change .inject por .bind

            _emailText = (EditText) findViewById(R.id.input_email);
            _passwordText = (EditText) findViewById(R.id.input_password);
            _loginButton = (Button) findViewById(R.id.btn_login);
            _signupLink = (TextView) findViewById(R.id.link_signup);

            _loginButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //primero corroborar conexión a internet
                    ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());
                    try {
                        if(conexionInternet.execute().get()){
                            login();
                        }else{
                            Toast.makeText(getApplicationContext(), "Corrobore su conexión a internet e intente nuevamente",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }


                }
            });

            _signupLink.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Start the Signup activity
                    Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                    startActivityForResult(intent, REQUEST_SIGNUP);
                }
            });

     /*       _signupLinkContrasena.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Start the Olvido Contraseña activity
                    Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                    startActivityForResult(intent, REQUEST_SIGNUP);
                }
            });
            */

        }else{
        //Si el usuario ya esta logueado lo dirije a MainActivity
            controlLogin(config);

            //Llamo a la actividad principal de la app
         /*   Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
            this.startActivity(mainActivityIntent);
*/
    }




}

    private void controlLogin(Configuraciones config) {

        if(config.getEstadoLogin()){
            //Llamo a la actividad principal de la app
            Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
            this.startActivity(mainActivityIntent);
        }
    }


    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.ThemeOverlay_AppCompat_Dialog); //change R.style.AppTheme_Dark_Dialog por R.style.ThemeOverlay_AppCompat_Dialog
        //final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Corroborando datos...");
        progressDialog.show();

        final String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        config.setUserEmail(email);
        config.setUserPassword(password);


        //poner delay de 1 segundo
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 1s = 1000ms

                continuar(email, progressDialog);

            }
        }, 1000);



    }

    private void continuar(String email, final ProgressDialog progressDialog) {

       // Configuraciones configuraciones = new Configuraciones(getApplicationContext());
      //  final String access_token = configuraciones.getAccessTokenPassword();
      //  boolean b = false;

        context = getApplicationContext();

        Buscar_Patient buscar_patient = null;
        try {
            buscar_patient = new Buscar_Patient(context, email);

        try {
            HashMap<String, String> data = buscar_patient.execute().get();

            if(data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(String.valueOf(Boolean.FALSE))){

                //     b = false;
                //Hubo algún error
                onLoginFailed();

            }else{
                String jsonString = data.get(JSON_CONSTANTS.PATIENTS);

                Patients patients = new Patients();
                patients.cargarPatient(getApplicationContext(), jsonString);


                try {
                    recuperarDatosMediciones();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                completarLogin();


                config.setEstadoLogin(true);

                new Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                onLoginSuccess();
                                //29/05/18
                                if (this != null && progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }

                               // progressDialog.dismiss();
                            }
                        }, 1000);

                this.finish();
            }



        } catch (InterruptedException e) {
            e.printStackTrace();
            //  b = false;
            //Hubo algún error
            onLoginFailed();
        } catch (ExecutionException e) {
            e.printStackTrace();
            //  b = false;
            //Hubo algún error
            onLoginFailed();
        }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }

    private void completarLogin() {

        //cambio 20/02/18
        //INICIALIZO LOS VALORES POR DEFECTO AL INICIAR LA APP SIN CONFIGURACION PREVIA
        //   Luego, desde el método onCreate() en la actividad principal de tu aplicación ,y en cualquier otra actividad a través de la cual el usuario pueda ingresar en tu aplicación por primera vez, llama a setDefaultValues():
        /*
        Siempre que configures el tercer argumento como false, puedes llamar de forma segura a este método cada vez que
         se inicia tu actividad sin reemplazar las preferencias guardadas del usuario restableciéndolas con los
         valores predeterminados. Sin embargo, si lo configuras como true, reemplazarás los valores anteriores con
         los valores predeterminados.
         */

        if(config.setJAVAMAILcontraseñaMailAutorizado()){

            PreferenceManager.setDefaultValues(this, R.xml.advanced_preferences, false);


            configurarAlarmas();

            //Llamo a la actividad principal de la app
            Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
            this.startActivity(mainActivityIntent);

        }else{
            //error contraseña = null
        }



    }

    private void recuperarDatosMediciones() throws ExecutionException, InterruptedException, JSONException {

        JSON_functions jsonFunctions = new JSON_functions(getApplicationContext());
        jsonFunctions.recuperarDatosMediciones();

    }

/*    private void guardarDatosPatient(final String email) {

        Configuraciones configuraciones = new Configuraciones(getApplicationContext());
        final String access_token = configuraciones.getAccessTokenPassword();
        boolean b = false;

        //poner delay de 1 segundo
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 1s = 1000ms
                Buscar_Patient buscar_patient = new Buscar_Patient(access_token, email);
                try {
                    HashMap<String, String> data = buscar_patient.execute().get();

                    if(data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(String.valueOf(Boolean.FALSE))){

                        //     b = false;
                        //Hubo algún error
                        onLoginFailed();

                    }else{
                        String jsonString = data.get(JSON_CONSTANTS.PATIENTS);

                        Patients patients = new Patients();
                        patients.cargarPatient(getApplicationContext(), jsonString);


                        //    b = true;
                    }



                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //  b = false;
                    //Hubo algún error
                    onLoginFailed();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    //  b = false;
                    //Hubo algún error
                    onLoginFailed();
                }
            }
        }, 1000);




      //  return b;
    }

*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
               // Toast.makeText(getApplicationContext(), "onActivityResult: RESULT_OK", Toast.LENGTH_LONG).show();

                completarLogin();

                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Falló el Login", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("ingrese una dirección de correo válida");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 20) {
            _passwordText.setError("entre 8 y 20 caracteres alfanuméricos");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        ///ACCEDER AL SERVIDOR Y COMPROBAR QUE EXISTA EL USUARIO Y CONTRASEÑA
        Json_Request_Access_Token_Password jsonRequestAccessTokenPassword = new Json_Request_Access_Token_Password(email,password);
        try {
            HashMap<String, String> data = jsonRequestAccessTokenPassword.execute().get();
            if(data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(JSON_CONSTANTS.HEADER_UNAUTHORIZED)){
                //Email o password incorrectos
                _emailText.setError("El email y la contraseña no coinciden. Intente nuevamente");
                _passwordText.setError("El email y la contraseña no coinciden. Intente nuevamente");
                valid = false;

            }else if(data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(String.valueOf(Boolean.TRUE))){
                //Autorizado
                String access_token_password = data.get(JSON_CONSTANTS.RESPONSE_ACCESS_TOKEN);

                Configuraciones configuraciones = new Configuraciones(getApplicationContext());
                configuraciones.setAccessTokenPassword(access_token_password);

                valid = true;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return valid;
    }



    //11/12/17 configura las alarmas inicialmente
    private void configurarAlarmas() {

        SetearAlarma setearAlarmaSERVIDOR = new SetearAlarma(getApplicationContext(), Constants.PARAMETRO_ENVIAR_DATOS_SERVIDOR);
        setearAlarmaSERVIDOR.execute();

        SetearAlarma setearAlarmaMEDICAMENTOS = new SetearAlarma(getApplicationContext(), Constants.MEDICAMENTOS);
        setearAlarmaMEDICAMENTOS.execute();

        SetearAlarma setearAlarmaCONSEJO_SALUDABLE = new SetearAlarma(getApplicationContext(), Constants.CONSEJO_SALUDABLE);
        setearAlarmaCONSEJO_SALUDABLE.execute();

        SetearAlarma setearAlarmaPESO = new SetearAlarma(getApplicationContext(), Constants.PARAMETRO_PESO);
        setearAlarmaPESO.execute();

        SetearAlarma setearAlarmaPA = new SetearAlarma(getApplicationContext(), Constants.PARAMETRO_PAFC);
        setearAlarmaPA.execute();

        SetearAlarma setearAlarmaSINTOMAS = new SetearAlarma(getApplicationContext(), Constants.PARAMETRO_SINTOMAS);
        setearAlarmaSINTOMAS.execute();

        SetearAlarma setearAlarmaEnviarMailAlertas = new SetearAlarma(getApplicationContext(), Constants.PARAMETRO_GENERAR_EMAIL_ALERTAS);
        setearAlarmaEnviarMailAlertas.execute();

        SetearAlarma setearAlarmaEnviarMailMediciones = new SetearAlarma(getApplicationContext(), Constants.PARAMETRO_GENERAR_EMAIL_MEDICIONES);
        setearAlarmaEnviarMailMediciones.execute();

        SetearAlarma setearAlarmaAlertaVerde = new SetearAlarma(getApplicationContext(), Constants.PARAMETRO_ALERTA_VERDE);
        setearAlarmaAlertaVerde.execute();

        SetearAlarma setearAlarmaGetContacts = new SetearAlarma(getApplicationContext(), Constants.PARAMETRO_GET_CONTACTS);
        setearAlarmaGetContacts.execute();

        SetearAlarma setearAlarmaEnviarMailJugada = new SetearAlarma(getApplicationContext(), Constants.PARAMETRO_GENERAR_EMAIL_JUGADAS);
        setearAlarmaEnviarMailJugada.execute();

        SetearAlarma setearAlarmaEnviarMailTablaDatos = new SetearAlarma(getApplicationContext(), Constants.PARAMETRO_GENERAR_EMAIL_TABLA_DATOS);
        setearAlarmaEnviarMailTablaDatos.execute();

    }


}
