package com.luciayanicelli.icsalud.DataBase;

import android.provider.BaseColumns;

/**
 * Created by LuciaYanicelli on 29/6/2017.
 *
 * Determina el "contrato" con la base de datos.
 * La estructura que tendrá la misma. Tabla, columnas etc.
 */

public class AutodiagnosticoContract {


    //Creamos esta clase abstracta para guardar el nombre de las columnas y de la tabla
    public static abstract class AutodiagnosticoEntry implements BaseColumns {

        public static final String TABLE_NAME_PESO ="PESO";

        public static final String PESO_DATE = "datePeso";
        public static final String PESO_VALOR = "peso";
   //     public static final String PESO_ESTADO = "estadoPeso";



        public static final String TABLE_NAME_PA ="PRESION_ARTERIAL_Y_FRECUENCIA_CARDIACA";

        public static final String PA_DATE = "datePA";
        public static final String PA_PS = "presionSistolica";
        public static final String PA_PD = "presionDiastolica";
        public static final String PA_FC = "frecuenciaCardiaca";


        public static final String TABLE_NAME_SINTOMAS ="SINTOMAS";

        public static final String SINTOMAS_IDPREGUNTA = "idPregunta";
        public static final String SINTOMAS_IDPREGUNTA_SERVIDOR = "idPreguntaServidor";
        public static final String SINTOMAS_DATE = "dateSintoma";
        public static final String SINTOMAS_PREGUNTA = "preguntaSintoma";
        public static final String SINTOMAS_RESPUESTA = "respuestaSintoma";


        public static final String TABLE_NAME_CONSEJO_SALUDABLE = "CONSEJOS_SALUDABLES_ENVIADOS";

        public static final String CONSEJO_SALUDABLE_IDPREGUNTA = "idConsejoSaludable";
        public static final String CONSEJO_SALUDABLE_DATE = "dateConsejoSaludable";
        public static final String CONSEJO_SALUDABLE_DESCRIPCION = "descripcionConsejoSaludable";


        public static final String TABLE_NAME_CONTADORES = "CONTADORES";
        public static final String CONTADOR_CONSEJOS_SALUDABLE_LEIDOS = "cs_cantidad_leidos";
        public static final String CONTADOR_FECHA_CONSEJOS_SALUDABLE_LEIDOS = "cs_fecha_cantidad_leidos";
        public static final String CONTADOR_PREGUNTAS_FRECUENTES_LEIDAS = "pf_cantidad_leidas";
        public static final String CONTADOR_FECHA_PREGUNTAS_FRECUENTES_LEIDAS = "pf_fecha_cantidad_leidas";


        //PARA TODAS ESTADO PARA VER SI LA INFORMACIÓN FUE ENVIADA POR MAIL O NO
        public static final String ESTADO = "estado";

        public static final String ESTADO_PENDIENTE = "PENDIENTE";
        public static final String ESTADO_ENVIADA = "ENVIADA";

    }
}
