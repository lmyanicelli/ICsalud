package com.luciayanicelli.icsalud.Services;


/**
 * Contiene las constantes de las acciones de los servicios y sus parámetros
 */
public class Constants {


    public static final String SOS = "sos";


    public static final String SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE = "ACTION_RUN_SERVICE_ALERTAS";
    public static final String SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE_MEDICIONES = "SERVICE_GENERAR_EMAIL_ACTION_RUN_SERVICE_MEDICIONES";

    public static final String SERVICE_GENERAR_ALERTAS_ADMINISTRADOR = "SERVICE_GENERAR_ALERTAS_ADMINISTRADOR";

    public static final String SERVICE_GENERAR_EMAIL_NEW_USER = "SERVICE_GENERAR_EMAIL_NEW_USER";


    /**
     * Constantes para {@link com.luciayanicelli.icsalud.Activity_Autodiagnostico.Activity_Menu_Autodiagnostico}
     */
    public static final String PARAMETRO = "PARAMETRO";
    public static final String PARAMETRO_PESO = "PESO";
    public static final String PARAMETRO_PAFC = "PRESION_ARTERIAL_FRECUENCIA_CARDIACA";
    public static final String PARAMETRO_SINTOMAS = "SINTOMAS";

    public static final String PARAMETRO_ENCUESTAS = "ENCUESTAS";


    /**
     * Constantes para {@link com.luciayanicelli.icsalud.Activity_ViewConsejoSaludable}
     */
    public static final String CONSEJO_SALUDABLE = "CONSEJO SALUDABLE";
    public static final String FECHA_CONSEJO_SALUDABLE = "fecha_consejo_saludable";


    /**
     * Constantes para {@link com.luciayanicelli.icsalud.Activity_ViewConsultaMedicamentos}
     */
    public static final String MEDICAMENTOS = "MEDICAMENTOS";




    public static final String KEY_PREF_HORARIO_PESO = "horario_peso";
    public static final String KEY_PREF_HORARIO_PA_FC = "horario_pafc";
    public static final String KEY_PREF_HORARIO_SINTOMAS = "horario_sintomas";
    public static final String KEY_PREF_HORARIO_CONSEJO_SALUDABLE = "horario_consejo_saludable";

    public static final String KEY_PREF_FRECUENCIA_CONSEJO_SALUDABLE = "frecuencia_consejo_saludable";
    public static final String DEFAULT_FRECUENCIA_CONSEJO_SALUDABLE = "3";
    public static final String KEY_PREF_FRECUENCIA_RECORDATORIO_PESO = "frecuencia_recordatorio_peso";
    public static final String DEFAULT_FRECUENCIA_RECORDATORIO_PESO = "1";
    public static final String KEY_PREF_FRECUENCIA_RECORDATORIO_PAFC = "frecuencia_recordatorio_pafc";
    public static final String DEFAULT_FRECUENCIA_RECORDATORIO_PAFC = "1";
    public static final String KEY_PREF_FRECUENCIA_RECORDATORIO_SINTOMAS = "frecuencia_recordatorio_sintomas";
    public static final String DEFAULT_FRECUENCIA_RECORDATORIO_SINTOMAS = "1";


    public static final String DEFAULT_HOUR_MEDICIONES = "8:00";
    public static final String DEFAULT_HOUR_CONSEJO_SALUDABLE = "10:00";
    public static final String DEFAULT_HOUR_MEDICAMENTOS = "10:01";

    public static final int DEFAULT_DAY_OF_WEEK_MEDICAMENTOS = 1;

    public static final String  NOTIFICACION_CHANNEL_ID = "notificationIC";
    public static final String  NOTIFICACION_CHANNEL_NAME = "Notificaciones iC";
    public static final String  NOTIFICACION_CHANNEL_DESCRIPTION = "Notificaciones de la aplicación iC";




    public static final String PARAMETRO_GENERAR_EMAIL_MEDICIONES = "parametro_generar_email_mediciones";
    public static final String PARAMETRO_GENERAR_EMAIL_ALERTAS = "parametro_generar_email_alertas";

    public static final String PARAMETRO_GENERAR_ALERTAS_ADMINISTRADOR = "parametro_generar_alertas_administrador";

    public static final String PARAMETRO_ENVIAR_DATOS_SERVIDOR = "parametro_enviar_datos_servidor";
    public static final String PARAMETRO_GENERAR_EMAIL_TABLA_DATOS = "parametro_generar_email_tabla_datos";

    public static final String DEFAULT_HOUR_GENERAR_EMAIL_MEDICIONES = "02:15";
    public static final String DEFAULT_HOUR_GENERAR_EMAIL_ALERTAS = "03:30";
    public static final String DEFAULT_HOUR_ENVIAR_DATOS_SERVIDOR = "04:00";
    public static final String DEFAULT_HOUR_GENERAR_ALERTAS_ADMINISTRADOR = "4:30";


    public static final String PARAMETRO_ALERTA_VERDE = "parametro_alerta_verde";
    public static final String DEFAULT_HOUR_GENERAR_ALERTA_VERDE = "2:30";

    public static final String PARAMETRO_GET_CONTACTS = "parametro_get_contacts";
    public static final String DEFAULT_HOUR_GET_CONTACTS = "00:30";

    public static final int PUNTAJE_JUEGO_CORRECTO = 100;



    //PROFESIONALES
 /*version 2   public static final String[] ARRAY_SELECTION_VINCULACIONES_PROFESIONALES = new String[]{
            "Profesionales Vinculados",
            "Solicitudes Recibidas",
            "Solicitudes Enviadas",
            "Enviar Nueva Solicitud",
            "Contactos Existentes",
            "Nuevo Contacto"};
*/
    public static final String[] ARRAY_SELECTION_VINCULACIONES_PROFESIONALES = new String[]{
            "Profesionales Vinculados",
            "Solicitudes Recibidas",
            "Solicitudes Enviadas",
            "Enviar Nueva Solicitud"};


}

