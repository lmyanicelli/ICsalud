package com.luciayanicelli.icsalud.Activity_Configuracion;

import android.content.Context;
import android.content.SharedPreferences;

import com.luciayanicelli.icsalud.Login.Encriptador;
import com.luciayanicelli.icsalud.Services.Constants;

/**
 * Created by LuciaYanicelli on 5/10/2017.
 *
 * ver esto
 * https://stackoverflow.com/questions/31142325/shared-preferences-on-a-null-object-reference-android
 *
 * https://www.nosinmiubuntu.com/como-guardar-datos-en-android/
 *
 *
 * USO
 * conf = new Configuraciones(context);
 contador = conf.getContadorConsejoSaludable();
 */

public class Configuraciones {

    //KEYS ESTADO LOGUEADO
    public static final String KEY_ACCESS_TOKEN_PASSWORD= "key_access_token_password";

    public static final String KEY_ESTADO_LOGIN = "pref_estado_login";

    public static final String KEY_PATIENT_ID = "key_patient_id";


    private final String SHARED_PREFS_FILE = "HMPrefs";

    private final String KEY_CONTADOR_CONSEJO_SALUDABLE = "contador_consejo_saludable";

    private final String KEY_BIRTHDAY = "key_fecha_nacimiento";
    private final String KEY_PASSWORD = "key_password";

    //DATOS PERSONAL DE SALUD
    private final String KEY_PREF_MAIL_PERSONAL_SALUD = "pref_mail_personal_salud";
    private static final String KEY_PREF_CELULAR_PERSONAL_SALUD = "pref_celular_personal_salud";

    //KEYS DATOS PERSONALES
    public static final String KEY_PREF_NAME = "pref_name";
    public static final String KEY_PREF_APELLIDO = "pref_apellido";
    public static final String KEY_PREF_MAIL_USUARIO = "pref_mail_usuario";
    public static final String KEY_PREF_CELULAR_USUARIO = "pref_celular_usuario";

    //KEYS DATOS CONTACTO AMBULANCIA
    public static final String KEY_PREF_CELULAR_CONTACTO_AMBULANCIA = "pref_celular_contacto_ambulancia";
    private static final String telefono_ambulancia = "*107";

    //PESO
    //VALORES DE REFERENCIA POSIBLES
    private double min_peso = 45.0; //Obtener de configuración
    private double max_peso = 300.0; //Obtener de configuración

    //CLAVES VALORES DE REFERENCIA POSIBLES
    public static final String KEY_PESO_INGRESAR_MIN = "key_peso_min";
    public static final String KEY_PESO_INGRESAR_MAX = "key_peso_max";


    //PRESION ARTERIAL Y FRECUENCIA CARDIACA
    //VALORES DE REFERENCIA POSIBLES
    private int minPS = 90; //obtener estos valores de configuración
    private int maxPS = 250;

    private int minPD = 20;
    private int maxPD = 120;

    private int minFC = 20;
    private int maxFC = 150;

    //VALORES DE REFERENCIA ALERTAS
    private int ps_max = 140; // Obtener de la Configuracion
    private int ps_min = 90;// Obtener de la Configuracion
    private int pd_min = 60;// Obtener de la Configuracion
    private int fc_max = 84; // Obtener de la Configuracion
    private int fc_min = 50;// Obtener de la Configuracion

    //CLAVES VALORES DE REFERENCIA POSIBLES
    public static final String KEY_PA_INGRESAR_MIN_PS = "key_pa_min_ps";
    public static final String KEY_PA_INGRESAR_MAX_PS = "key_pa_max_ps";

    public static final String KEY_PA_INGRESAR_MIN_PD = "key_pa_min_pd";
    public static final String KEY_PA_INGRESAR_MAX_PD = "key_pa_max_pd";

    public static final String KEY_PA_INGRESAR_MIN_FC = "key_pa_min_fc";
    public static final String KEY_PA_INGRESAR_MAX_FC = "key_pa_max_fc";

    //CLAVES VALORES DE REFERENCIA ALERTAS
    public static final String KEY_PA_ALERTA_PS_MIN = "key_alerta_pa_min_ps";
    public static final String KEY_PA_ALERTA_PS_MAX = "key_alerta_pa_max_ps";

    public static final String KEY_PA_ALERTA_PD_MIN = "key_alerta_pd_min_ps";

    public static final String KEY_PA_ALERTA_FC_MIN = "key_alerta_fc_min_ps";
    public static final String KEY_PA_ALERTA_FC_MAX = "key_alerta_fc_max_ps";

    //SINTOMAS
    private int cantidadPregDiarias = 3; //Cantidad de preguntas que deben contestarse por día //OBTENER DE CONFIGURACION

    //CLAVE
    public static final String KEY_SINTOMAS_CANTIDAD_PREGUNTAS_DIARIAS = "key_sintomas_cantidad_preguntas_diarias";


    //ENVIAR MAIL EN SEGUNDO PLANO
    //Mail habilitado para utilizar javamail - opciones de seguridad
    private String javamail_mail_autorizado = "mhealth.ic@gmail.com";

    public static final String KEY_JAVAMAIL_MAIL_AUTORIZADO = "key_javamail_mail_autorizado";
    public static final String KEY_JAVAMAIL_CONTRASEÑA_MAIL_AUTORIZADO = "key_javamail_contraseña_mail_autorizado";

    //KEYS Horarios Enviar Mail
    public static final String KEY_PREF_HORARIO_GENERAR_EMAIL_MEDICIONES = "horario_generar_email_mediciones";
    public static final String KEY_PREF_HORARIO_GENERAR_EMAIL_ALERTAS = "horario_generar_email_alertas";
    public static final String KEY_PREF_HORARIO_GENERAR_EMAIL_JUGADAS = "horario_generar_email_jugadas";

    public static final String KEY_PREF_HORARIO_ALERTA_VERDE = "horario_alerta_verde";
    public static final String KEY_PREF_HORARIO_MEDICAMENTOS = "horario_medicamentos";

    //KEY horario enviar datos servidor
    private String KEY_PREF_HORARIO_ENVIAR_DATOS_SERVIDOR = "horario_enviar_datos_servidor";

    private Context mContext;
    private String KEY_PREF_GENDER = "key_gender";
    private String KEY_PREF_EDUCATION_LEVEL = "educationLevel";
    private String KEY_PREF_OCCUPATION = "occupation";
    private String KEY_LAST_HOSPITALIZATION = "lastHospitalizacion";
    private String KEY_HEALTH_INSURANCE = "healthInsurance";
    private String KEY_COEXISTENCE = "coexistence";
    private String KEY_DIAGNOSED_AT = "diagnosedAt";
    private String KEY_PREF_HORARIO_GET_CONTACTS = "key_pref_horario_get_contacts";

    public static final String DEFAULT_USER_EMAIL_REMITENTE = "icsalud.adm@gmail.com";

    public static final String KEY_PREF_MAIL_CONTACTS = "key_pref_mail_contacts";

    public static final String DEFAULT_USER_EMAIL_ADMINISTRATOR = "icsalud.adm@gmail.com";
    public static final String KEY_PREF_MAIL_ADMINISTRATOR = "key_pref_mail_administrator";

    public static final String KEY_CONTADOR_CONSEJOS_SALUDABLES_LEIDOS = "key_contador_consejos_saludables_leidos";
    public static final String KEY_CONTADOR_PREGUNTAS_FRECUENTES_LEIDOS = "key_contador_preguntas_frecuentes_leidas";
    private String KEY_ENCUESTAS_CONTESTADAS = "key_encuestas_contestadas";


    public Configuraciones(Context context){
        this.mContext = context;
    }

    private SharedPreferences getSettings(){
        return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
    }

    //ACCESS_TOKEN_PASSWORD
    public String getAccessTokenPassword(){
        return getSettings().getString(KEY_ACCESS_TOKEN_PASSWORD, null);
    }

    public void setAccessTokenPassword(String accessTokenPassword){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_ACCESS_TOKEN_PASSWORD, accessTokenPassword );
        editor.commit();
    }

    //ESTADO LOGIN
    public boolean getEstadoLogin(){
        return getSettings().getBoolean(KEY_ESTADO_LOGIN, false);
    }

    public void setEstadoLogin(boolean estadoLogin){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putBoolean(KEY_ESTADO_LOGIN, estadoLogin );
        editor.commit();
    }

    //LOGIN PATIENT ID
    public String getID(){
        return getSettings().getString(KEY_PATIENT_ID,  null);
    }

    public void setID(String patient_id){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PATIENT_ID, patient_id );
        editor.commit();
    }

    //CONTADORES
    public int getContadorConsejosSaludablesLeidos(){
        return getSettings().getInt(KEY_CONTADOR_CONSEJOS_SALUDABLES_LEIDOS, 0);
    }

    public void setContadorConsejosSaludablesLeidos(int contadorConsejosSaludablesLeidos){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putInt(KEY_CONTADOR_CONSEJOS_SALUDABLES_LEIDOS, contadorConsejosSaludablesLeidos );
        editor.commit();
    }

    public int getContadorPreguntasFrecuentesLeidas(){
        return getSettings().getInt(KEY_CONTADOR_PREGUNTAS_FRECUENTES_LEIDOS, 0);
    }

    public void setContadorPreguntasFrecuentesLeidas(int contadorPreguntasFrecuentesLeidas){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putInt(KEY_CONTADOR_PREGUNTAS_FRECUENTES_LEIDOS, contadorPreguntasFrecuentesLeidas );
        editor.commit();
    }


    //HORARIOS ENVIAR MAIL
    public String getHorarioEnviarEmailAlertas(){
        return getSettings().getString(KEY_PREF_HORARIO_GENERAR_EMAIL_ALERTAS, Constants.DEFAULT_HOUR_GENERAR_EMAIL_ALERTAS);
    }

    public void setHorarioEnviarEmailAlertas(String horarioEnviarEmailAlertas){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_HORARIO_GENERAR_EMAIL_ALERTAS, horarioEnviarEmailAlertas );
        editor.commit();
    }


    public String getHorarioEnviarEmailMediciones(){
        return getSettings().getString(KEY_PREF_HORARIO_GENERAR_EMAIL_MEDICIONES, Constants.DEFAULT_HOUR_GENERAR_EMAIL_MEDICIONES);
    }

    public void setHorarioEnviarEmailMediciones(String horarioEnviarEmailMediciones){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_HORARIO_GENERAR_EMAIL_MEDICIONES, horarioEnviarEmailMediciones );
        editor.commit();
    }

    public String getHorarioEnviarEmailJugadas(){
        return getSettings().getString(KEY_PREF_HORARIO_GENERAR_EMAIL_JUGADAS, Constants.DEFAULT_HOUR_GENERAR_EMAIL_JUGADAS);
    }

    public void setHorarioEnviarEmailJugadas(String horarioEnviarEmailJugadas){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_HORARIO_GENERAR_EMAIL_JUGADAS, horarioEnviarEmailJugadas );
        editor.commit();
    }


    //HORARIOS ENVIAR DATOS SERVIDOR
    public String getHorarioEnviarDatosServidor(){
        return getSettings().getString(KEY_PREF_HORARIO_ENVIAR_DATOS_SERVIDOR, Constants.DEFAULT_HOUR_ENVIAR_DATOS_SERVIDOR);
    }

    public void setHorarioEnviarDatosServidor(String horarioEnviarDatosServidor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_HORARIO_ENVIAR_DATOS_SERVIDOR, horarioEnviarDatosServidor );
        editor.commit();
    }


    //ALERTA VERDE
    public String getHorarioAlertaVerde(){
        return getSettings().getString(KEY_PREF_HORARIO_ALERTA_VERDE, Constants.DEFAULT_HOUR_GENERAR_ALERTA_VERDE);
    }

    public void setHorarioAlertaVerde(String horarioAlertaVerde){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_HORARIO_ALERTA_VERDE, horarioAlertaVerde );
        editor.commit();
    }


    //MEDICAMENTOS
    public String getHorarioMedicamentos(){
        return getSettings().getString(KEY_PREF_HORARIO_MEDICAMENTOS, Constants.DEFAULT_HOUR_MEDICAMENTOS);
    }

    public void setHorarioMedicamentos(String horarioMedicamentos){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_HORARIO_MEDICAMENTOS, horarioMedicamentos);
        editor.commit();
    }

    public int getDayOfWeekMedicamentos(){
        return getSettings().getInt(KEY_PREF_HORARIO_MEDICAMENTOS, Constants.DEFAULT_DAY_OF_WEEK_MEDICAMENTOS);
    }

    public void setDayOfWeekMedicamentos(int day_of_week_medicamentos){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putInt(KEY_PREF_HORARIO_MEDICAMENTOS, day_of_week_medicamentos);
        editor.commit();
    }

    //GET CONTACTS
    public String getHorarioGetContacts(){
        return getSettings().getString(KEY_PREF_HORARIO_GET_CONTACTS, Constants.DEFAULT_HOUR_GET_CONTACTS);
    }

    public void setHorarioGetContacts(String horarioGetContacts){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_HORARIO_GET_CONTACTS, horarioGetContacts);
        editor.commit();
    }


    //DATOS PERSONALES
    public String getUserEmail(){
        return getSettings().getString(KEY_PREF_MAIL_USUARIO, null);
    }

    public void setUserEmail(String email){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_MAIL_USUARIO, email );
        editor.commit();
    }

    public int getContadorConsejoSaludable(){
        return getSettings().getInt(KEY_CONTADOR_CONSEJO_SALUDABLE, 0);
        //0 valor por defecto
    }

    public void setContadorConsejoSaludable(int contadorConsejoSaludable){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putInt(KEY_CONTADOR_CONSEJO_SALUDABLE, contadorConsejoSaludable);
        editor.commit();
    }

    public String getUserName(){
        return getSettings().getString(KEY_PREF_NAME, null);
    }

    public void setUserName(String name){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_NAME, name );
        editor.commit();

        //PRUEBA 25/10/17 no cambia nada
     /*   editor.putString(KEY_PREF_NAME, name);
        editor.commit();
        */
    }


    public String getUserSurname(){
        return getSettings().getString(KEY_PREF_APELLIDO, null);
    }

    public void setUserSurname(String surname){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_APELLIDO, surname );
        editor.commit();
    }

    public String getEducationLevel(){
        return getSettings().getString(KEY_PREF_EDUCATION_LEVEL, null);
    }

    public void setEducationLevel(String educationLevel){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_EDUCATION_LEVEL, educationLevel );
        editor.commit();
    }

    public String getOccupation(){
        return getSettings().getString(KEY_PREF_OCCUPATION, null);
    }

    public void setOccupation(String occupation){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_OCCUPATION, occupation );
        editor.commit();
    }

    public String getGender(){
        return getSettings().getString(KEY_PREF_GENDER, null);
    }

    public void setGender(String gender){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_GENDER, gender);
        editor.commit();
    }

    public String getUserBirthday(){
        return getSettings().getString(KEY_BIRTHDAY, null);
    }

    public void setUserBirthday(String birthday){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_BIRTHDAY, birthday );
        editor.commit();
    }

    public String getLastHospitalization(){
        return getSettings().getString(KEY_LAST_HOSPITALIZATION, null);
    }

    public void setLastHospitalization(String lastHospitalization){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_LAST_HOSPITALIZATION, lastHospitalization );
        editor.commit();
    }

    public String getDiagnosedAt(){
        return getSettings().getString(KEY_DIAGNOSED_AT, null);
    }

    public void setDiagnosedAt(String dateDiagnosis){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_DIAGNOSED_AT, dateDiagnosis );
        editor.commit();
    }

    public String getHealthInsurance(){
        return getSettings().getString(KEY_HEALTH_INSURANCE, null);
    }

    public void setHealthInsurance(String healthInsurance){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_HEALTH_INSURANCE, healthInsurance );
        editor.commit();
    }

    public int getCoexistence(){
        return getSettings().getInt(KEY_COEXISTENCE, -1);
    }

    public void setCoexistence(int coexistence){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putInt(KEY_COEXISTENCE, coexistence );
        editor.commit();
    }

    public String getUserPassword(){
         String password = getSettings().getString(KEY_PASSWORD, null);
         String passworDesencriptada;
        //ENCRIPTAR DATOS
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Encriptador encriptador = new Encriptador(mContext, getUserEmail()); //utilizo el email como palabra clave
            passworDesencriptada = encriptador.decryptString(password);
        }else {
            passworDesencriptada=password;
        }

        return passworDesencriptada;
    }

    public void setUserPassword(String password){
        String passwordEncriptada;
        //ENCRIPTAR DATOS
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Encriptador encriptador = new Encriptador(mContext, getUserEmail()); //utilizo el email como palabra clave
            passwordEncriptada = encriptador.encryptString(password);
        }else {
            passwordEncriptada=password;
        }
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PASSWORD, passwordEncriptada);
        editor.commit();
    }

    public String getUserEmailRemitente(){
       // return getSettings().getString(KEY_EMAIL_REMITENTES, null);
        return getSettings().getString(KEY_PREF_MAIL_PERSONAL_SALUD, DEFAULT_USER_EMAIL_REMITENTE);
    }

    public void setUserEmailRemitentes(String emailRemitentes){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_MAIL_PERSONAL_SALUD, emailRemitentes );
        editor.commit();
    }

    public String getEmailAdministrator(){
        return getSettings().getString(KEY_PREF_MAIL_ADMINISTRATOR, DEFAULT_USER_EMAIL_ADMINISTRATOR);
    }

    public void setEmailAdministrator(String emailAdministrator){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_MAIL_ADMINISTRATOR, emailAdministrator );
        editor.commit();
    }

    public String getUserEmailContacts(){
        return getSettings().getString(KEY_PREF_MAIL_CONTACTS, DEFAULT_USER_EMAIL_ADMINISTRATOR);
    }

    public void setUserEmailContacts(String emailContacts){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_MAIL_CONTACTS, DEFAULT_USER_EMAIL_ADMINISTRATOR + "," + emailContacts);
        editor.commit();
    }

    public String getUserCelContacts(){
        // return getSettings().getString(KEY_EMAIL_REMITENTES, null);
        return getSettings().getString(KEY_PREF_CELULAR_PERSONAL_SALUD, null);
    }

    public void setUserCelContacts(String celRemitentes){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_CELULAR_PERSONAL_SALUD, celRemitentes);
        editor.commit();

    }

    public String getUserTelefonoAmbulancia(){
        // return getSettings().getString(KEY_EMAIL_REMITENTES, null);
        return getSettings().getString(KEY_PREF_CELULAR_CONTACTO_AMBULANCIA, String.valueOf(telefono_ambulancia));
    }

    public void setUserTelefonoAmbulancia(String telefono_ambulancia){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREF_CELULAR_CONTACTO_AMBULANCIA, telefono_ambulancia);
        editor.commit();

    }


    public String getSummary(String key){

         return getSettings().getString(key, null);

    }

    //PESO
    //VALORES A INGRESAR
    public String getPESOIngresarMin(){
        return getSettings().getString(KEY_PESO_INGRESAR_MIN, String.valueOf(min_peso));
    }

    public void setPESOIngresarMin(String valor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PESO_INGRESAR_MIN, valor);
        editor.commit();
    }

    public String getPESOIngresarMax(){
        return getSettings().getString(KEY_PESO_INGRESAR_MAX, String.valueOf(max_peso));
    }

    public void setPESOIngresarMax(String valor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PESO_INGRESAR_MAX, valor);
        editor.commit();
    }




    //PRESION ARTERIAL Y FRECUENCIA CARDIACA
    //VALORES A INGRESAR
    public String getPAIngresarMinPS(){
        return getSettings().getString(KEY_PA_INGRESAR_MIN_PS, String.valueOf(minPS));
    }

    public void setPAIngresarMinPS(String valor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PA_INGRESAR_MIN_PS, valor);
        editor.commit();
    }

    public String getPAIngresarMinPD(){
        return getSettings().getString(KEY_PA_INGRESAR_MIN_PD, String.valueOf(minPD));
    }

    public void setPAIngresarMinPD(String valor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PA_INGRESAR_MIN_PD, valor);
        editor.commit();
    }

    public String getPAIngresarMinFC(){
        return getSettings().getString(KEY_PA_INGRESAR_MIN_FC, String.valueOf(minFC));
    }

    public void setPAIngresarMinFC(String valor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PA_INGRESAR_MIN_FC, valor);
        editor.commit();
    }



    public String getPAIngresarMaxPS(){
        return getSettings().getString(KEY_PA_INGRESAR_MAX_PS, String.valueOf(maxPS));
    }

    public void setPAIngresarMaxPS(String valor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PA_INGRESAR_MAX_PS, valor);
        editor.commit();
    }

    public String getPAIngresarMaxPD(){
        return getSettings().getString(KEY_PA_INGRESAR_MAX_PD, String.valueOf(maxPD));
    }

    public void setPAIngresarMaxPD(String valor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PA_INGRESAR_MAX_PD, valor);
        editor.commit();
    }

    public String getPAIngresarMaxFC(){
        return getSettings().getString(KEY_PA_INGRESAR_MAX_FC, String.valueOf(maxFC));
    }

    public void setPAIngresarMaxFC(String valor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PA_INGRESAR_MAX_FC, valor);
        editor.commit();
    }

    //ALERTAS
    public String getPAAlertaMinPS(){
        return getSettings().getString(KEY_PA_ALERTA_PS_MIN, String.valueOf(ps_min));
    }

    public void setPAAlertaMinPS(String valor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PA_ALERTA_PS_MIN, valor);
        editor.commit();
    }

    public String getPAAlertaMinPD(){
        return getSettings().getString(KEY_PA_ALERTA_PD_MIN, String.valueOf(pd_min));
    }

    public void setPAAlertaMinPD(String valor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PA_ALERTA_PD_MIN, valor);
        editor.commit();
    }

    public String getPAAlertaMinFC(){
        return getSettings().getString(KEY_PA_ALERTA_FC_MIN, String.valueOf(fc_min));
    }

    public void setPAAlertaMinFC(String valor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PA_ALERTA_FC_MIN, valor);
        editor.commit();
    }

    public String getPAAlertaMaxPS(){
        return getSettings().getString(KEY_PA_ALERTA_PS_MAX, String.valueOf(ps_max));
    }

    public void setPAAlertaMaxPS(String valor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PA_ALERTA_PS_MAX, valor);
        editor.commit();
    }

    public String getPAAlertaMaxFC(){
        return getSettings().getString(KEY_PA_ALERTA_FC_MAX, String.valueOf(fc_max));
    }

    public void setPAAlertaMaxFC(String valor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PA_ALERTA_FC_MAX, valor);
        editor.commit();
    }

    //SINTOMAS
    public String getSINTOMAScantidadPreguntasDiarias(){
        return getSettings().getString(KEY_SINTOMAS_CANTIDAD_PREGUNTAS_DIARIAS, String.valueOf(cantidadPregDiarias));
    }

    public void setSINTOMAScantidadPreguntasDiarias(String valor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_SINTOMAS_CANTIDAD_PREGUNTAS_DIARIAS, valor);
        editor.commit();
    }


    //ENVIAR MAIL EN SEGUNDO PLANO
    public String getJAVAMAILmailAutorizado(){
        return getSettings().getString(KEY_JAVAMAIL_MAIL_AUTORIZADO, String.valueOf(javamail_mail_autorizado));
    }

    public void setJAVAMAILmailAutorizado(String valor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_JAVAMAIL_MAIL_AUTORIZADO, valor);
        editor.commit();
    }

    public String getJAVAMAILcontraseñaMailAutorizado(){
        return getSettings().getString(KEY_JAVAMAIL_CONTRASEÑA_MAIL_AUTORIZADO, null);
    }

    public boolean setJAVAMAILcontraseñaMailAutorizado( ){
        String valor;

        String[] email = javamail_mail_autorizado.split("@");
        String email_primera_parte = email[0];
        String[] email_primera_parte_array = email_primera_parte.split("\\.");
        String email_segunda_parte = email[1];
        String[] email_segunda_parte_array = email_segunda_parte.split("\\.");
        String email_1 = email_primera_parte_array[0];
        String email_2 = email_primera_parte_array[1];
        String email_3 = email_segunda_parte_array[0];
        String email_4 = email_segunda_parte_array[1];

        valor = email_1 + email_3 + String.valueOf(email_1.length()) + String.valueOf(email_2.length())+
                String.valueOf(email_3.length()) + String.valueOf(email_4.length());


        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_JAVAMAIL_CONTRASEÑA_MAIL_AUTORIZADO, valor);
        editor.commit();

        if(valor!= null){
            return true;
        }else{
            return false;
        }
    }


 /*   public void setEncuestaContestada(String encuestaContestada) {

        Set<String> setEncuestas = getEncuestasContestadas();
        setEncuestas.add(encuestaContestada);

        SharedPreferences.Editor editor = getSettings().edit();
        editor.putStringSet(KEY_ENCUESTAS_CONTESTADAS, setEncuestas);
        editor.commit();

      //  this.encuestaContestada = encuestaContestada;
    }

    public Set<String> getEncuestasContestadas() {

        Set<String> setEncuestas = new Set<String>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<String> iterator() {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(@NonNull T[] a) {
                return null;
            }

            @Override
            public boolean add(String s) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends String> c) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }
        };
        return getSettings().getStringSet(KEY_ENCUESTAS_CONTESTADAS, setEncuestas);
    }

    public void borrarEncuestasContestadas(){

        SharedPreferences.Editor editor = getSettings().edit();
      //  editor.putStringSet(KEY_ENCUESTAS_CONTESTADAS, null);
        editor.remove(KEY_ENCUESTAS_CONTESTADAS);
        editor.commit();

    }
    */


}
