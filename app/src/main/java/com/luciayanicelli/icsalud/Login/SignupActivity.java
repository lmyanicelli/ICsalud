package com.luciayanicelli.icsalud.Login;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.Api_Json.Buscar_Patient;
import com.luciayanicelli.icsalud.Api_Json.Get_Professionals_index;
import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;
import com.luciayanicelli.icsalud.Api_Json.Json_Request_Access_Token_Credential;
import com.luciayanicelli.icsalud.Api_Json.Patients;
import com.luciayanicelli.icsalud.Api_Json.Post_Patient;
import com.luciayanicelli.icsalud.Api_Json.Post_Practitioner;
import com.luciayanicelli.icsalud.R;
import com.luciayanicelli.icsalud.Services.ConexionInternet;
import com.luciayanicelli.icsalud.Services.Constants;
import com.luciayanicelli.icsalud.utils.AlertDialogs;
import com.luciayanicelli.icsalud.utils.SetearAlarma;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static com.luciayanicelli.icsalud.R.id.input_radioGroupCoexistence;
import static com.luciayanicelli.icsalud.R.id.input_radioGroupOS;
import static com.luciayanicelli.icsalud.R.id.input_radioGroupSex;


public class SignupActivity extends AppCompatActivity implements AlertDialogs.NoticeDialogListener{

    private static final String TAG = "SignupActivity";


    private EditText _nameText;
    private EditText _surnameText;
    private EditText _birthdayText, _lastHospitalizacion, _diagnosedAt;
    private EditText _emailText;
    private EditText _passwordText;
    private EditText _passwordRepeatText;
    private Button _signupButton;
    private TextView _loginLink;
    private EditText _emailRemitente;
    private EditText _celRemitente;
    private TextView _sex, _education_level, _occupation, _coexistence, _healthInsurance;
    private RadioGroup _radioGroupSex, _radioGroupOS, _radioGroupCoexistence;
    private RadioButton _radioButtonF, _radioButtonM, _radioButtonNINGUNA, _radioButtonPAMI, _radioButtonOTRA, _radioButtonSOLO, _radioButtonFLIA;
    private Spinner _spinner_educationLevel, _spinner_occupation;


    //Configuraciones
    private Configuraciones config;


    private String gender;
    private String educationLevel;
    private String occupation;
    private String healthInsurance;
    private int coexistence = -1;

    //Calcular edad
    private long birthdayTime;
    private Context context;
    private int JOB_ID_GENERAR_EMAIL_NEW_USER = 1001;

    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        config = new Configuraciones(getApplicationContext());

            setContentView(R.layout.activity_signup);

          //  ButterKnife.bind(this);

            _nameText = (EditText) findViewById(R.id.input_name);
            _surnameText = (EditText) findViewById(R.id.input_surname);
            _birthdayText = (EditText) findViewById(R.id.input_birthdayText);
            _emailText = (EditText) findViewById(R.id.input_email);
            _passwordText = (EditText) findViewById(R.id.input_password);
            _passwordRepeatText = (EditText) findViewById(R.id.input_password_repeat);
            _signupButton = (Button) findViewById(R.id.btn_signup);
            _loginLink = (TextView) findViewById(R.id.link_login);


        //Sexo
            _sex = (TextView) findViewById(R.id.input_sex);
            _radioGroupSex = (RadioGroup) findViewById(input_radioGroupSex);
            _radioButtonF = (RadioButton) findViewById(R.id.radioButtonF);
            _radioButtonM = (RadioButton) findViewById(R.id.radioButtonM);


        // Manipulando los RadioButton
        View.OnClickListener radioListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()){

                    case R.id.radioButtonF:
                        gender = JSON_CONSTANTS.GENDER_FEMALE;
                        break;

                    case R.id.radioButtonM:
                        gender = JSON_CONSTANTS.GENDER_MALE;
                        break;

                    default:
                        gender = null;
                        break;
                }

            }
        };

        _radioButtonF.setOnClickListener(radioListener);
        _radioButtonM.setOnClickListener(radioListener);


        //Nivel Educativo
            _spinner_educationLevel = (Spinner) findViewById(R.id.spinner_education_level);

        String[] education_levels = JSON_CONSTANTS.ARRAY_EDUCATION_LEVELS_SPANISH;
        _spinner_educationLevel.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, education_levels));

        _spinner_educationLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                if(pos == 0){
                    educationLevel = null;
                }else{
                    educationLevel = JSON_CONSTANTS.ARRAY_EDUCATION_LEVELS_ENGLISH[pos];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {                }
        });

        //Ocupacion
        _spinner_occupation = (Spinner) findViewById(R.id.spinner_occupation);

        String[] arrayOccupation = JSON_CONSTANTS.ARRAY_OCCUPATION_SPANISH;
        _spinner_occupation.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayOccupation));

        _spinner_occupation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                if(pos == 0){
                    occupation = null;
                }else{
                    occupation = JSON_CONSTANTS.ARRAY_OCCUPATION_ENGLISH[pos];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {              }
        });


        //OBRA SOCIAL
        _healthInsurance = (TextView) findViewById(R.id.input_healthInsurance);
        _radioGroupOS = (RadioGroup) findViewById(input_radioGroupOS);
        _radioButtonNINGUNA = (RadioButton) findViewById(R.id.radioButtonNinguna);
        _radioButtonPAMI = (RadioButton) findViewById(R.id.radioButtonPAMI);
        _radioButtonOTRA = (RadioButton) findViewById(R.id.radioButtonOtra);

        // Manipulando los RadioButton
        View.OnClickListener radioListenerOS = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()){

                    case R.id.radioButtonNinguna:
                        healthInsurance = "Ninguna";
                        break;

                    case R.id.radioButtonPAMI:
                        healthInsurance = "PAMI";
                        break;

                    case R.id.radioButtonOtra:
                        healthInsurance = "Otra";
                        break;

                    default:
                        healthInsurance = null;
                        break;
                }
            }
        };

        _radioButtonNINGUNA.setOnClickListener(radioListenerOS);
        _radioButtonPAMI.setOnClickListener(radioListenerOS);
        _radioButtonOTRA.setOnClickListener(radioListenerOS);


        //CONVIVENCIA
        _coexistence = (TextView) findViewById(R.id.input_coexistence);
        _radioGroupCoexistence = (RadioGroup) findViewById(input_radioGroupCoexistence);
        _radioButtonSOLO = (RadioButton) findViewById(R.id.radioButtonSOLO);
        _radioButtonFLIA = (RadioButton) findViewById(R.id.radioButtonFAMILIA);

        // Manipulando los RadioButton
        View.OnClickListener radioListenerCOEXISTENCE = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()){

                    case R.id.radioButtonSOLO:
                         coexistence = 0;
                        break;

                    case R.id.radioButtonFAMILIA:
                        coexistence = 2;
                        break;

                    default:
                        coexistence = -1;
                        break;
                }

            }
        };

        _radioButtonSOLO.setOnClickListener(radioListenerCOEXISTENCE);
        _radioButtonFLIA.setOnClickListener(radioListenerCOEXISTENCE);


        //DATE DIAGNOSIS
        _diagnosedAt = (EditText) findViewById(R.id.input_dateDiagnosis);

        _diagnosedAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog("dateDiagnosis");
            }
        });


        //LAST HOSPITALIZATION
        _lastHospitalizacion = (EditText) findViewById(R.id.input_lastHospitalization);

        _lastHospitalizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog("lastHospitalization");
            }
        });


        _birthdayText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog("birthdayText");
                }
            });

        _signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signup();
                }
            });

            _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

    }


    //CARGAR FECHAS
    private void showDatePickerDialog(final String text) {


        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                        0, 0, 0);

                // +1 because january is zero
                String sMonth = String.valueOf(month+1);
                if(sMonth.length()<2){
                    sMonth = "0" + String.valueOf(month+1);
                }

                String sDay = String.valueOf(day);
                if(sDay.length()<2){
                    sDay = "0" + String.valueOf(day);
                }

                String selectedDate = year + "-" + (sMonth) + "-" + sDay; //format YYYY-MM-DD

                //Corroborar que las fechas sean anteriores a la fecha actual
                long fechaSeleccionada = calendar.getTimeInMillis();
                long fechaActual = Calendar.getInstance().getTimeInMillis();

                if(fechaSeleccionada > fechaActual){
                     //fecha incorrecta
                    //Mensaje de aviso
                    Toast.makeText(getApplicationContext(), "Fecha Incorrecta. Por favor seleccione otra fecha", Toast.LENGTH_LONG).show();
                }else{
                    //fecha correcta
                    if(text.equalsIgnoreCase("lastHospitalization") ){
                        SignupActivity.this._lastHospitalizacion.setText(selectedDate);
                    }else if(text.equalsIgnoreCase("dateDiagnosis")){
                        SignupActivity.this._diagnosedAt.setText(selectedDate);
                    }else if(text.equalsIgnoreCase("birthdayText")){
                        SignupActivity.this._birthdayText.setText(selectedDate);
                        birthdayTime = calendar.getTimeInMillis();
                    }
                }


            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }



//Signup
    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        //agregar consentimiento informado
        crearAlertDialogConsentimientoInformado();

    }

    private void crearAlertDialogConsentimientoInformado() {

        AlertDialogs alertDialogConsentimientoInformado = new AlertDialogs();
        alertDialogConsentimientoInformado.setMsj(getResources().getString(R.string.consentimiento_informado));
        alertDialogConsentimientoInformado.setName(getResources().getString(R.string.alert_consentimiento_informado));

        alertDialogConsentimientoInformado.setPositiveButton(getResources().getString(R.string.de_acuerdo));
        alertDialogConsentimientoInformado.setNegativeButton(getResources().getString(R.string.en_desacuerdo));

        DialogFragment alertConsentimientoInformado = alertDialogConsentimientoInformado;
        alertConsentimientoInformado.show(getSupportFragmentManager(), "alertConsentimientoInformado");
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String name) {

        switch (name){

            case "loginIncorrecto":
                return;

            case "consentimientoInformado":
                continuarSignup();
                // onSignupSuccess();
                break;

            default:
                break;
        }

        return;
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, String name) {
        //volver a LoginActivity
        setResult(RESULT_CANCELED, null);
        finish();

    }


    //CONSENTIMIENTO APROBADO
    private void continuarSignup(){

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.ThemeOverlay_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creando Cuenta...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String surname = _surnameText.getText().toString();
        String birthday = _birthdayText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        //    String passwordRepeat = _passwordRepeatText.getText().toString();
        String lastHospitalization = _lastHospitalizacion.getText().toString();
        String diagnosedAt = _diagnosedAt.getText().toString();

        if(lastHospitalization.isEmpty()){
            lastHospitalization = "1900-01-01"; //carga esta fecha por defecto para indicar que nunca hubo hospitalizacion
        }

        // TODO: Implement your own signup logic here.

        //GUARDAR DATOS EN CONFIGURACIONES
        Configuraciones config = new Configuraciones(getApplicationContext());

        config.setUserName(name);
        config.setUserSurname(surname);
        config.setUserBirthday(birthday);
        config.setUserEmail(email);
        config.setUserPassword(password);
        config.setGender(gender);
        config.setEducationLevel(educationLevel);
        config.setOccupation(occupation);
        config.setLastHospitalization(lastHospitalization);
        config.setHealthInsurance(healthInsurance);
        config.setCoexistence(coexistence);
        config.setDiagnosedAt(diagnosedAt);

        //corrobora la conexión a internet, crea la cuenta en el servidor y devuelve el Id del patient creado
        boolean loginCorrecto = corroborarConexionInternet();


        if(loginCorrecto){
            config.setEstadoLogin(true);

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onSignupSuccess or onSignupFailed
                            // depending on success
                            onSignupSuccess();
                            progressDialog.dismiss();
                        }
                    }, 3000);

        }else{
            onSignupFailed();
            progressDialog.dismiss();
            return;
        }

    }


    public void onSignupSuccess() {

        //VER DE PONER ACCESO SIEMPRE
        SetearAlarma setearAlarmaEncuestas = new SetearAlarma(getApplicationContext(), Constants.PARAMETRO_ENCUESTAS);
        setearAlarmaEncuestas.execute();

       //14/08/18 enviarFormularioMail();
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
        this.finish();
    }

    private void crearAlertDialog() {

        AlertDialogs alertDialogLoginIncorrecto = new AlertDialogs();
        alertDialogLoginIncorrecto.setMsj(getResources().getString(R.string.login_incorrecto));
        alertDialogLoginIncorrecto.setName(getResources().getString(R.string.alert_login_incorrecto));

        alertDialogLoginIncorrecto.setPositiveButton(getResources().getString(R.string.aceptar));
        alertDialogLoginIncorrecto.setNegativeButton(getResources().getString(R.string.salir));

        DialogFragment alertLoginIncorrecto = alertDialogLoginIncorrecto;
        alertLoginIncorrecto.show(getSupportFragmentManager(), "alertLoginIncorrecto");
    }

  /*  private void enviarFormularioMail() {

        String textoEnviar = crearTextoFormulario();
        String contactos = config.getEmailAdministrator();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            EnviarMailSegundoPlano enviarMailSegundoPlano = new EnviarMailSegundoPlano(getApplicationContext(),
                    "Formulario Nueva Cuenta",
                    textoEnviar,
                    contactos);
            enviarMailSegundoPlano.execute();

        }else{
            scheduleJobGenerarEmail(getApplicationContext(),
                    Constants.SERVICE_GENERAR_EMAIL_NEW_USER,
                    JOB_ID_GENERAR_EMAIL_NEW_USER);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJobGenerarEmail(Context context, String extra, int jobId) {

        PersistableBundle pb = new PersistableBundle();
        pb.putString("extra", extra);
     //   pb.putLong("periodo", periodo);
        pb.putInt("jobId", jobId);

        ComponentName serviceComponentGE = new ComponentName(context, GenerarEmailJobService.class);
        JobInfo.Builder builderGE = new JobInfo.Builder(jobId, serviceComponentGE);
      //  builderGE.setPeriodic(periodo); //UN DÍA ? 86400000
        //   builder.setMinimumLatency(1000); //1segundo
        //   builder.setOverrideDeadline(1*60*60*1000); //1hora * 60 minutos * 60 segundos * 1000ms

        //new
        //Network type (metered/unmetered)
        //If your job requires network access, you must include this condition. You can specify a metered or unmetered network, or any type of network. But not calling this when building your JobInfo means the system will assume you do not need any network access and you will not be able to contact your server.
        builderGE.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

        //Persistent
        ///Any work that needs to be persisted across a reboot can be marked as such here. Once the device reboots, the job will be rescheduled according to the conditions. (Note that your app needs the RECEIVE_BOOT_COMPLETED permission for this to work, though.)
        builderGE.setPersisted(Boolean.TRUE);
        //new

        builderGE.setExtras(pb);

        JobScheduler jobSchedulerGE = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobSchedulerGE.schedule(builderGE.build());

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
*/

    private boolean corroborarConexionInternet() {

        ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());
        boolean conectado = false;
        try {
            conectado = conexionInternet.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(conectado) {
            //Crear Usuario en el Servidor y obtener Token e ID
            Json_Request_Access_Token_Credential jsonRequestAccessTokenCredentials = null;
            try {
                jsonRequestAccessTokenCredentials = new Json_Request_Access_Token_Credential();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String access_token_credentials = null;
            try {
                access_token_credentials = jsonRequestAccessTokenCredentials.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            final Configuraciones configuraciones = new Configuraciones(getApplicationContext());
            Post_Patient post_patient = new Post_Patient(access_token_credentials,
                    configuraciones.getUserName(),
                    configuraciones.getUserSurname(),
                    configuraciones.getUserEmail(),
                    configuraciones.getUserPassword(),
                    configuraciones.getUserBirthday(),
                    configuraciones.getOccupation(),
                    configuraciones.getHealthInsurance(),
                    configuraciones.getGender(),
                    configuraciones.getCoexistence(),
                    configuraciones.getEducationLevel(),
                    configuraciones.getLastHospitalization(),
                    configuraciones.getDiagnosedAt());

            boolean patientCreated = false;
            try {
                patientCreated = post_patient.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (patientCreated) {
                //Obtener ID
                context = getApplicationContext();
                Buscar_Patient buscar_patient = null;
                try {
                    buscar_patient = new Buscar_Patient(context, configuraciones.getUserEmail());

                try {
                    HashMap<String, String> data = buscar_patient.execute().get();

                    if(data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(String.valueOf(Boolean.FALSE))){

                        return false;

                    }else{
                        String jsonString = data.get(JSON_CONSTANTS.PATIENTS);

                        Patients patients = new Patients();
                        patients.cargarPatient(getApplicationContext(), jsonString);

                        //Enviar solicitud de vinculación al administrador icsalud.adm@gmail.com
                        enviarSolicitudAdministrador();


                        return true;
                    }



                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    return false;
                }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        } else {
            return false;
        }

        return false;
    }

    private void enviarSolicitudAdministrador() throws ExecutionException, InterruptedException {

        //obtener id -- icsalud.adm@gmail.com

        Get_Professionals_index get_professionals_index = new Get_Professionals_index();
        HashMap<String, String> data = get_professionals_index.execute().get();

        String professionals = data.get(JSON_CONSTANTS.PROFESSIONALS);
        String id_professionals = data.get(JSON_CONSTANTS.ID);
        String email_professionals = data.get(JSON_CONSTANTS.EMAIL);

        String[] profesionales = professionals.split(";");
        String[] id_profesionales = id_professionals.split(";");
        String[] email_profesionales = email_professionals.split(";");

        //Recupera los datos en un HashMap para luego poder ordenarlos alfabéticamente
        HashMap<String, String> email_id = new HashMap<>();

        for (int i = 0; i < profesionales.length; i++) {
            email_id.put(email_profesionales[i], id_profesionales[i]);
        }

        String id_administrador;

        if(email_id.get(config.getEmailAdministrator())!= null){
            id_administrador = email_id.get(Configuraciones.DEFAULT_USER_EMAIL_ADMINISTRATOR);

            //Envía solicitud de vinculación al administrador
            Post_Practitioner post_practitioner = new Post_Practitioner(context, id_administrador);

            HashMap<String, String> response = post_practitioner.execute().get();

            if(response.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equals(Boolean.TRUE)){
                //correcto

            }else {
                //incorrecto

            }


        }

    }


    public void onSignupFailed() {
          crearAlertDialog();
        _signupButton.setEnabled(true);
    }

    @SuppressLint("ResourceAsColor")
    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String surname = _surnameText.getText().toString();
        String birthday = _birthdayText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String passwordRepeat = _passwordRepeatText.getText().toString();
        String dateDiagnosis = _diagnosedAt.getText().toString();

        if (password.isEmpty() || password.length() < 8 || password.length() > 20) {
            _passwordText.setError("entre 8 y 20 caracteres alfanuméricos");
            _passwordText.requestFocus();
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (passwordRepeat.isEmpty() || passwordRepeat.length() < 8 || passwordRepeat.length() > 20 || !passwordRepeat.equalsIgnoreCase(password)) {
            _passwordRepeatText.setError("no coinciden las contraseñas");
            _passwordRepeatText.requestFocus();
            valid = false;
        } else {
            _passwordRepeatText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("ingrese una dirección de correo válida");
            _emailText.requestFocus();
            valid = false;
        } else {
            _emailText.setError(null);
        }

        //DATE DIAGNOSIS
        if (dateDiagnosis.isEmpty()) {
            _diagnosedAt.setError("debe seleccionar una fecha aproximada de cuándo le diagnosticaron Insuficiencia Cardíaca por primera vez");
            _diagnosedAt.requestFocus();
            _diagnosedAt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.alert_dialog_icon, 0);
            _diagnosedAt.setDrawingCacheBackgroundColor(R.color.cast_expanded_controller_background_color);
            _diagnosedAt.setLinkTextColor(Color.RED);
            _diagnosedAt.setError("debe seleccionar una fecha aproximada de cuándo le diagnosticaron Insuficiencia Cardíaca por primera vez");
            _diagnosedAt.setHighlightColor(Color.RED);
            valid = false;
        } else {
            _diagnosedAt.setError(null);
        }

        if (birthday.isEmpty() || calcularEdad(birthdayTime) < 18) {
            _birthdayText.setError("debe seleccionar su fecha de nacimiento");
            _birthdayText.requestFocus();
            _birthdayText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.alert_dialog_icon, 0);
            _birthdayText.setDrawingCacheBackgroundColor(R.color.cast_expanded_controller_background_color);
            _birthdayText.setLinkTextColor(Color.RED);
            valid = false;
        } else {
            _birthdayText.setError(null);
        }


        if (surname.isEmpty() || surname.length() < 3) {
            _surnameText.setError("al menos 3 caracteres");
            _surnameText.requestFocus();
            valid = false;
        } else {
            _surnameText.setError(null);
        }

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("al menos 3 caracteres");
            _nameText.requestFocus();
            valid = false;
        } else {
            _nameText.setError(null);
        }

        //sexo
        if (gender == null) {
            _sex.setError("Por favor seleccione una opción");
            _sex.requestFocus();
            valid = false;
        } else {
            _sex.setError(null);
        }

       //nivel educativo
        if (educationLevel ==null) {
            TextView errorText = (TextView)_spinner_educationLevel.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Por favor seleccione su nivel educativo");
            valid = false;
        }

        //ocupación
        if (occupation==null) {
            TextView errorText = (TextView)_spinner_occupation.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Por favor seleccione su ocupación");
            valid = false;
        }

        //OBRA SOCIAL
        if (healthInsurance == null) {
            _healthInsurance.setError("Por favor seleccione una opción");
            _healthInsurance.requestFocus();
            valid = false;
        } else {
            _healthInsurance.setError(null);
        }

        //CONVIVENCIA
        if (coexistence == -1) {
            _coexistence.setError("Por favor seleccione una opción");
            _coexistence.requestFocus();
            valid = false;
        } else {
            _coexistence.setError(null);
        }

        return valid;
    }

    private int calcularEdad(long birthdayTime) {

        int edad;
        Date fechaActual = new Date(System.currentTimeMillis());

        long diferencia = fechaActual.getTime() - birthdayTime;

        long segsMilli = 1000;
        long minsMilli = segsMilli * 60;
        long horasMilli = minsMilli * 60;
        long diasMilli = horasMilli * 24;
        long añosMilli = diasMilli * 365;

        long añosTranscurridos = diferencia / añosMilli;
     //   diferencia = diferencia % añosMilli;

 /*       long diasTranscurridos = diferencia / diasMilli;
        diferencia = diferencia % diasMilli;

        long horasTranscurridos = diferencia / horasMilli;
        diferencia = diferencia % horasMilli;

        long minutosTranscurridos = diferencia / minsMilli;
        diferencia = diferencia % minsMilli;

        long segsTranscurridos = diferencia / segsMilli;
*/
        edad = (int) añosTranscurridos;

        return edad;
    }



}
