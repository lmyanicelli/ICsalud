package com.luciayanicelli.icsalud.Activity_Autodiagnostico;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.Alertas_DBHelper;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_DBHelper;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_SintomasContract;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_SintomasDBHelper;
import com.luciayanicelli.icsalud.R;
import com.luciayanicelli.icsalud.Services.Constants;
import com.luciayanicelli.icsalud.utils.FechaActual;
import com.luciayanicelli.icsalud.utils.Recordatorio;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
 * Created by LuciaYanicelli on 15/2/2017.
 *
 * Fragment para contestar las preguntas sobre los síntomas
 * Carga los datos ingresados en la BD Autodiagnostico, Tabla: Mediciones Síntomas - Estado. pendiente (se modifica una vez q se envía el mail)
 * Analiza si la respuesta es Mucho o Muchísimo y en base a eso genera una alerta amarilla que la guarda en la BD Alertas - Estado: pendiente (se modifica cdo se envía el mail)
 *
 */

public class Autodiagnostico_Sintomas extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener{

    //Vista
    private TextView textView_header, textView_pregunta;
    private RadioGroup radioGroup;
    private RadioButton radioButton1, radioButton2, radioButton3, radioButton4, radioButton5, radioButton6;
    private Button btn_cancel, btn_save;
    private ScrollView scrollView;

    private boolean datosCargados = false;

    // Pregunta y Respuesta
    private String respuesta;
    private String pregunta, p;
    private  int idpregunta;


    //ELIMINAR UNA VEZ QUE FUNCIONE BIEN CONFIGURACIONES
    private int cantidadPregDiarias = 3; //Cantidad de preguntas que deben contestarse por día //OBTENER DE CONFIGURACION

    //Texto que se enviará luego de que el paciente responda sobre sus síntomas
    private String textoEnviar;

    //Fecha
    private Calendar calendarNow;
    private String fecha;
    private FechaActual fechaActual;
    private String fechaHora;
    private String idpregunta_servidor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.autodiagnostico_sintomas, container, false);

        //Armado de la vista
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        textView_header = (TextView) view.findViewById(R.id.textView_header);
        textView_pregunta = (TextView) view.findViewById(R.id.textView_pregunta);

        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);

        radioButton1 = (RadioButton) view.findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) view.findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) view.findViewById(R.id.radioButton3);
        radioButton4 = (RadioButton) view.findViewById(R.id.radioButton4);
        radioButton5 = (RadioButton) view.findViewById(R.id.radioButton5);
        radioButton6 = (RadioButton) view.findViewById(R.id.radioButton6);

        //Funcionalidad del radio group
        radioGroup.setOnCheckedChangeListener(this);

        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_save = (Button) view.findViewById(R.id.btn_save);

        //Funcionalidad a los botones
        btn_cancel.setOnClickListener(this);
        btn_save.setOnClickListener(this);


        //Acceder BD
        //Buscar si existen datos cargados con la fecha de hoy y determinar el booleano datosCargados en true o false
        datosCargados = buscarDatosHoy();

        //Actualizar vista
        refreshView();

        //INICIALIZO VALORES DE CONFIGURACION
        Configuraciones configuraciones = new Configuraciones(getContext());
        cantidadPregDiarias = Integer.valueOf(configuraciones.getSINTOMAScantidadPreguntasDiarias());


        return view;
    }



    ////CORROBORAR QUE EXISTA LA CANTIDAD DIARIA RESPUESTAS GUARDADAS CON LA FECHA ACTUAL PARA IMPEDIR QUE SIGA CONTESTANDO
    private boolean buscarDatosHoy() {
        //Acceder a la BD de las mediciones de los síntomas y ver si existen datos cargados con la fecha de hoy

        //Obtengo la fecha actual
        //Obtener fecha de hoy
        fechaActual = new FechaActual(); //formato AAAA-MM-DD HH:MM:SS
        try {
            fechaHora = fechaActual.execute().get();
            String[] splitFecha = fechaHora.split(" ");
            fecha = splitFecha[0]; //obtengo sólo la fecha sin hora

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //Busco en la BD si existen registros con la fecha de hoy
        Autodiagnostico_DBHelper mdbHelper = new Autodiagnostico_DBHelper(getContext());
        SQLiteDatabase dbAuto = mdbHelper.getWritableDatabase();

        String[] camposDB2 = new String[]{AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE,
                AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_IDPREGUNTA};

        String selection = AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE + ">= ?"+
                " and " +  AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE + "<= ?";

        String[] args = new String[] {String.valueOf(fecha + " 00:00:00"), String.valueOf(fecha + " 23:59:59")}; //busco en el dia completo


        Cursor busqueda = dbAuto.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS,
                camposDB2, selection, args,null,null,null,null);

        //Si existen datos guardados con la fecha de hoy devuelve true
        if (busqueda != null & busqueda.moveToFirst()){

            int count = busqueda.getCount();

         //   Toast.makeText(getContext(), "Count preguntas" + String.valueOf(count), Toast.LENGTH_LONG).show();

            //Analiza si existe la cantidad de registros establecida eb cantidadPregDiarias con la fecha actual
            if (busqueda.getCount()< cantidadPregDiarias){
                return false;
            }else{
                //eliminar recordatorio de BD si es que existe
                //eliminarRecordatorioBD(fecha);
                Recordatorio mRecordatorio = new Recordatorio();
                mRecordatorio.eliminarRecordatorioBD(getContext(), Constants.PARAMETRO_SINTOMAS);

                busqueda.close();
                dbAuto.close();
                return true;
            }

        } else {
            //Si no existe ningún registro con la fecha actual devuelve false
            busqueda.close();
            dbAuto.close();
            return false;
        }

    }

    //EN LUGAR DE HACER ESTO, CONVENDRÍA TENER UN FRAGMENT XA CARGAR LOS DATOS Y OTRO QUE SÓLO CONTENGA EL TEXT VIEW INDICANDO QUE YA CARGÓ LOS DATOS
        //eL FRAGMENT CON EL TEXTVIEW SERVIRÍA XA TODOS LOS TABS
        private void refreshView() {

            //Vuelve el scroll al inicio
            scrollView.fullScroll(scrollView.FOCUS_UP);

            //Analizo si los datos ya estan cargados
            if (datosCargados) {
                //Ya estan cargados todos los datos de hoy entonces sólo muestra un msj
                setVisible(false);
              //  textView_header.setText(R.string.sintomas_cargados);
                textView_pregunta.setText(R.string.sintomas_cargados);
                textView_pregunta.setTypeface(null, Typeface.BOLD);

            }else {
                //Pantalla para cargar datos
                setVisible(true);
                textView_header.setText(R.string.ingrese_sintomas);

                radioGroup.clearCheck(); //Limpia la selección anterior en caso de existir
                respuesta = null;

                pregunta = preguntaDiaria();

                p = "¿Le ha impedido la Insuficiencia Cardíaca vivir como Ud. hubiera deseado porque: ";

                textView_pregunta.setText(p + pregunta);
                textView_pregunta.setTypeface(null, Typeface.NORMAL);
            }

        }

    private String preguntaDiaria() {

        //BUSCA EL ULTIMO ID DE LA PREGUNTA GUARDADA
        Context mContext = getContext();

        Autodiagnostico_DBHelper dbHelperAuto = new Autodiagnostico_DBHelper(mContext);
        SQLiteDatabase dbAutodiagnostico = dbHelperAuto.getWritableDatabase();

        String [] camposDBAuto = new String[]{AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_IDPREGUNTA, BaseColumns._ID};

        Cursor mcursor = dbAutodiagnostico.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS, camposDBAuto, null, null, null, null, null, null);

        //Si existen preguntas guardadas, busca el id de la última pregunta para luego establecer la pregunta siguiente
        if(mcursor != null & mcursor.moveToLast()){
          idpregunta = mcursor.getInt(0);
        } else {
            idpregunta = 0;
        }


        //Accede a la BD con las preguntas de los síntomas
        Autodiagnostico_SintomasDBHelper dbHelper = new Autodiagnostico_SintomasDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String [] camposDB = new String[]{Autodiagnostico_SintomasContract.SintomasEntry.PREGUNTA, BaseColumns._ID};

        Cursor cursor = db.query(true, Autodiagnostico_SintomasContract.SintomasEntry.TABLE_NAME, camposDB, null, null, null, null, null, null);

           //si la ultima pregunta es menor a la cantidad total de preguntas puedo seguir sumando 1 sino volver a empezar
        if (idpregunta < cursor.getCount()){
          //  Toast.makeText(getContext(), "GetCount = " + String.valueOf(cursor.getCount()), Toast.LENGTH_LONG).show();
            idpregunta = idpregunta + 1;
        }else{
            idpregunta = 1;
        }

        //BUSCO LA PREGUNTA
        String[] camposDB2 = new String[]{Autodiagnostico_SintomasContract.SintomasEntry.PREGUNTA, BaseColumns._ID, Autodiagnostico_SintomasContract.SintomasEntry.ID_SERVIDOR};
        String selection = BaseColumns._ID + "= ?";
        String[] args = new String[] {String.valueOf(idpregunta)};

                /*query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
Query the given URL, returning a Cursor over the result set.*/
        Cursor cursor2 = db.query(true, Autodiagnostico_SintomasContract.SintomasEntry.TABLE_NAME, camposDB2, selection, args,null,null,null,null);

        String pregunta;

        if  (cursor2.moveToFirst()) {
            pregunta = cursor2.getString(0);
            idpregunta_servidor = cursor2.getString(2);
        }else {
            pregunta = "No hay pregunta disponible";
        }

        cursor.close();
        cursor2.close();
        dbAutodiagnostico.close();
        db.close();
        return pregunta;

    }



    private void setVisible(boolean b) {
        int visible;

        if (b){
            visible = View.VISIBLE;
        }else {
            visible = View.INVISIBLE;
        }

      //  textView_pregunta.setVisibility(visible);
        textView_header.setVisibility(visible);
        radioGroup.setVisibility(visible);

        btn_cancel.setVisibility(visible);
        btn_save.setVisibility(visible);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_cancel:
                //Deseleccionar
                radioGroup.clearCheck();
                //06/11/17
                getActivity().finish();


                break;

            case R.id.btn_save:
                //Control de que haya seleccionado una opción
                if (respuesta == null){
                    //respuesta vacía
                    Toast.makeText(getContext(), "Por favor seleccione una respuesta", Toast.LENGTH_SHORT).show();

                }else if(respuesta.isEmpty()){
                    //respuesta vacía
                    Toast.makeText(getContext(), "Por favor seleccione una respuesta", Toast.LENGTH_SHORT).show();

                }else {
                    //respuesta contestada

                    //Control alerta amarilla
                    if(respuesta.equalsIgnoreCase(getString(R.string.sintomas_respuesta_mucho)) | respuesta.equalsIgnoreCase(getString(R.string.sintomas_respuesta_muchisimo)) ){
                        String descripcion = "El paciente presenta síntomas acentuados por su IC: \n" +
                                pregunta + ": " + respuesta;

                        //guardar Alarma en BD
                        guardarAlerta(descripcion);
                    /*    Alertas_DBHelper mDBHelper = new Alertas_DBHelper(getContext());
                        SQLiteDatabase db = mDBHelper.getWritableDatabase();

                        ContentValues values = new ContentValues();

                        values.put(AlertasContract.AlertasEntry.FECHA, fechaHora);
                        values.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_AMARILLA);
                        values.put(AlertasContract.AlertasEntry.PARAMETRO, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS);
                        values.put(AlertasContract.AlertasEntry.DESCRIPCION, descripcion);
                        values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);

                        try{
                            long controlInsert = db.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);

                        }catch(Exception e){
                            e.printStackTrace();
                        }
*/
                    }

                    boolean datosGuardados;
                    //Guardar los datos en la BD //DEBERIA SER EN SEGUNDO PLANO????
                    datosGuardados = guardarBD(textView_pregunta.getText().toString(), respuesta);

                    if (datosGuardados){
                        //Enviar email - SE ENVÍAN TODOS JUNTOS UNA VEZ X DÍA DESDE EL SERVICE GENERAR EMAIL
                      /*  textoEnviar = "Síntoma de hoy: " + textView_pregunta.getText().toString() + "\n" +
                                "Respuesta: " + respuesta;


                        Intent intent = new Intent(getContext(), ServiceEnviarEmail.class);
                        intent.setAction(Constants.SERVICE_ENVIAR_EMAIL_ACTION_RUN_SERVICE);
                        intent.putExtra(Constants.SERVICE_ENVIAR_EMAIL_ASUNTO,"Medición SINTOMAS");
                        intent.putExtra(Constants.SERVICE_ENVIAR_EMAIL_TEXTO_ENVIAR, textoEnviar);
                        getActivity().startService(intent);
*/
                        //EnviarMailSegundoPlano enviarMailSegundoPlano = new EnviarMailSegundoPlano("Medición SINTOMAS", textoEnviar);
                        //enviarMailSegundoPlano.execute();

                    }else{
                        Toast.makeText(getContext(), R.string.errorBD, Toast.LENGTH_LONG).show();
                    }


                    //Buscar si existen datos cargados con la fecha de hoy y determinar el booleano datosCargados en true o false
                    datosCargados = buscarDatosHoy();

                    if (!datosCargados){
                        //Si aún quedan preguntas por contestar mostrar un alertDialog indicando que debe seguir contestando
                        AlertDialog alertDialog = createSimpleDialog();
                        alertDialog.show();


                    }else{
                        //elimina recordatorio de la BD
                        //eliminarRecordatorioBD(fecha);
                        Recordatorio mRecordatorio = new Recordatorio();
                        mRecordatorio.eliminarRecordatorioBD(getContext(), Constants.PARAMETRO_SINTOMAS);

                        //Actualizar vista
                        refreshView();
                    }

                }


                break;

            default:
                break;

        }




    }

    private void guardarAlerta(String descripcion) {
        Alertas_DBHelper mDBHelper = new Alertas_DBHelper(getContext());
        SQLiteDatabase dbAlerta = mDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(AlertasContract.AlertasEntry.FECHA, fechaHora);
        values.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_AMARILLA);
        values.put(AlertasContract.AlertasEntry.PARAMETRO, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS);
        values.put(AlertasContract.AlertasEntry.DESCRIPCION, descripcion);
        values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);
        values.put(AlertasContract.AlertasEntry.VISIBILIDAD, AlertasContract.AlertasEntry.ALERTA_VISIBILIDAD_PUBLICA);

        try{
            long controlInsert = dbAlerta.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);
            dbAlerta.close();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Crea un diálogo de alerta sencillo
     * @return Nuevo diálogo
     */
    public AlertDialog createSimpleDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("")
                .setMessage(getResources().getString(R.string.sintomas_continuar))
                .setPositiveButton("CONTINUAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Actualizar vista
                                refreshView();
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        });

        return builder.create();
    }

    private boolean guardarBD(String pregunta, String respuesta) {

        Autodiagnostico_DBHelper dbHelper = new Autodiagnostico_DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_DATE, fechaHora);
        values.put(AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_IDPREGUNTA, idpregunta);
        values.put(AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_IDPREGUNTA_SERVIDOR, idpregunta_servidor);
        values.put(AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_PREGUNTA, pregunta);
        values.put(AutodiagnosticoContract.AutodiagnosticoEntry.SINTOMAS_RESPUESTA, respuesta);
        values.put(AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO,
                AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO_PENDIENTE);

        long controlInsert = db.insert(AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_SINTOMAS, null, values);
        db.close();
        //Control de que los datos se hayan guardado correctamente en la BD
        if(controlInsert != -1){

            return true;
        }else{
            return false;
        }
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton1:
                if (checked)
                    respuesta = radioButton1.getText().toString();
                    break;
            case R.id.radioButton2:
                if (checked)
                    respuesta = radioButton2.getText().toString();
                    break;
            case  R.id.radioButton3:
                if (checked)
                    respuesta = radioButton3.getText().toString();
                    break;
            case R.id.radioButton4:
                if (checked)
                    respuesta = radioButton4.getText().toString();
                    break;
            case R.id.radioButton5:
                if (checked)
                    respuesta = radioButton5.getText().toString();
                    break;
            case  R.id.radioButton6:
                if (checked)
                    respuesta = radioButton6.getText().toString();
                    break;

            default:

                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

        // Check which radio button was clicked
        switch(checkedId) {
            case R.id.radioButton1:
                    respuesta = radioButton1.getText().toString();
                break;

            case R.id.radioButton2:
                    respuesta = radioButton2.getText().toString();
                break;

            case  R.id.radioButton3:
                    respuesta = radioButton3.getText().toString();
                break;

            case R.id.radioButton4:
                    respuesta = radioButton4.getText().toString();
                break;

            case R.id.radioButton5:
                    respuesta = radioButton5.getText().toString();
                break;

            case  R.id.radioButton6:
                    respuesta = radioButton6.getText().toString();
                break;

            default:

                break;
        }
    }

}
