package com.luciayanicelli.icsalud.Activity_Autodiagnostico;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.Alertas_DBHelper;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_DBHelper;
import com.luciayanicelli.icsalud.R;
import com.luciayanicelli.icsalud.Services.Constants;
import com.luciayanicelli.icsalud.utils.FechaActual;
import com.luciayanicelli.icsalud.utils.Recordatorio;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;


public class Autodiagnostico_PAFC extends Fragment implements View.OnClickListener {


    //Vista
    private TextView textView_header, textView_PS, textView_PD, textView_FC, textView_mmHg1, textView_mmHg2, textView_lat_min;
    private EditText editText_PS, editText_PD, editText_FC;
    private Button btn_cancel, btn_save;
    private ScrollView scrollView;

    private boolean datosCargados = false;

    //Texto que se enviará luego de que el usuario cargue sus mediciones
    private String textoEnviar;
    private Calendar calendarNow;
    private String fecha;

    //VALORES DE REFERENCIA POSIBLES
    private int minPS; //obtener estos valores de configuración
    private int maxPS;

    private int minPD;
    private int maxPD;

    private int minFC;
    private int maxFC;

    //VALORES DE REFERENCIA ALERTAS
    private int ps_max; // Obtener de la Configuracion
    private int ps_min;// Obtener de la Configuracion
    private int pd_min;// Obtener de la Configuracion
    private int fc_max; // Obtener de la Configuracion
    private int fc_min;// Obtener de la Configuracion
    private FechaActual fechaActual;
    private String fechaHora;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.autodiagnostico_fc, container, false);


        //Armado de la vista
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        textView_header = (TextView) view.findViewById(R.id.textView_header);
        textView_PS = (TextView) view.findViewById(R.id.textView_PS);
        textView_PD = (TextView) view.findViewById(R.id.textView_PD);
        textView_FC = (TextView) view.findViewById(R.id.textView_FC);

        textView_mmHg1 = (TextView) view.findViewById(R.id.textView_mmHg1);
        textView_mmHg2 = (TextView) view.findViewById(R.id.textView_mmHg2);
        textView_lat_min = (TextView) view.findViewById(R.id.textView_lat_min);

        editText_PS = (EditText) view.findViewById(R.id.editText_PS);
        editText_PD = (EditText) view.findViewById(R.id.editText_PD);
        editText_FC = (EditText) view.findViewById(R.id.editText_FC);

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


        //CARGAR VALORES DE REFERENCIA
        Configuraciones configuraciones = new Configuraciones(getContext());

        //Valores de referencia para ingresar
        minPS = Integer.valueOf(configuraciones.getPAIngresarMinPS()); //obtener estos valores de configuración
        maxPS = Integer.valueOf(configuraciones.getPAIngresarMaxPS());

        minPD = Integer.valueOf(configuraciones.getPAIngresarMinPD());
        maxPD = Integer.valueOf(configuraciones.getPAIngresarMaxPD());

        minFC = Integer.valueOf(configuraciones.getPAIngresarMinFC());
        maxFC = Integer.valueOf(configuraciones.getPAIngresarMaxFC());

        //VALORES DE REFERENCIA ALERTAS
        ps_max = Integer.valueOf(configuraciones.getPAAlertaMaxPS()); // Obtener de la Configuracion
        ps_min = Integer.valueOf(configuraciones.getPAAlertaMinPS());// Obtener de la Configuracion
        pd_min = Integer.valueOf(configuraciones.getPAAlertaMinPD());// Obtener de la Configuracion
        fc_max = Integer.valueOf(configuraciones.getPAAlertaMaxFC()); // Obtener de la Configuracion
        fc_min = Integer.valueOf(configuraciones.getPAAlertaMinFC());// Obtener de la Configuracion


        return view;
    }

          ////CORROBORAR QUE EXISTA EL PESO CARGADO CON LA FECHA ACTUAL PARA IMPEDIR QUE SIGA CARGANDO DATOS
        private boolean buscarDatosHoy() {
            //Acceder a la BD y ver si existen datos cargados con la fecha de hoy


            //CAMBIAR POR FECHA ACTUAL
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
            //BD
            Autodiagnostico_DBHelper mdbHelper = new Autodiagnostico_DBHelper(getContext());
            SQLiteDatabase dbAuto = mdbHelper.getWritableDatabase();


            String[] camposDB2 = new String[]{AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE,
                    AutodiagnosticoContract.AutodiagnosticoEntry.PA_PD,
                    AutodiagnosticoContract.AutodiagnosticoEntry.PA_PS,
                    AutodiagnosticoContract.AutodiagnosticoEntry.PA_FC};

            String selection = AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE +  ">= ?"+
                    " and " + AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE +  "<= ?";

            String[] args = new String[] {String.valueOf(fecha + " 00:00:00"), String.valueOf(fecha + " 23:59:59")}; //busco en el dia completo

            Cursor busqueda = dbAuto.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA,
                    camposDB2, selection, args,null,null,null,null);

            //Si existen datos guardados con la fecha de hoy devuelve true
            if (busqueda != null & busqueda.moveToFirst()){
                //eliminar recordatorio de BD si es que existe
             //   eliminarRecordatorioBD(fecha);
                Recordatorio mRecordatorio = new Recordatorio();
                mRecordatorio.eliminarRecordatorioBD(getContext(), Constants.PARAMETRO_PAFC);

                busqueda.close();
                dbAuto.close();
                return true;
            } else {
                //Si no existe ningún registro con la fecha actual devuelve false
                busqueda.close();
                dbAuto.close();
                return false;
            }



        }

  /*  private void eliminarRecordatorioBD(String fecha) {

        RecordatoriosDBHelper dbHelper = new RecordatoriosDBHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = RecordatoriosContract.RecordatoriosEntry.PARAMETRO + "= ?" + " and " + RecordatoriosContract.RecordatoriosEntry.FECHA + "= ?";

        String[] args = {Constants.PARAMETRO_PAFC, fecha};

        db.delete(RecordatoriosContract.RecordatoriosEntry.TABLE_NAME, whereClause, args);

        Toast.makeText(getContext(), "eliminarRecordatorioBD" + fecha, Toast.LENGTH_LONG).show();

    }
    */


    //EN LUGAR DE HACER ESTO, CONVENDRÍA TENER UN FRAGMENT XA CARGAR LOS DATOS Y OTRO  QUE SÓLO CONTENGA EL TEXT VIEW INDICANDO QUE YA CARGÓ LOS DATOS
    //eL FRAGMENT CON EL TEXTVIEW SERVIRÍA XA TODOS LOS TABS
    private void refreshView() {
        //Vuelve el scroll al inicio
        scrollView.fullScroll(scrollView.FOCUS_UP);

        //Analizo si los datos ya estan cargados
        if (datosCargados) {
            //Ya estan cargados los datos entonces sólo muestra una msj
            setVisible(false);
            textView_header.setText(R.string.fc_cargada);

        }else {
            //Pantalla para cargar datos
            setVisible(true);
            textView_header.setText(R.string.ingrese_fc);
        }

    }

    private void setVisible(boolean b) {
        int visible;

        if (b){
            visible = View.VISIBLE;
        }else {
            visible = View.INVISIBLE;
        }

        textView_PS.setVisibility(visible);
        textView_PD.setVisibility(visible);
        textView_FC.setVisibility(visible);

        textView_mmHg1.setVisibility(visible);
        textView_mmHg2.setVisibility(visible);
        textView_lat_min.setVisibility(visible);

        editText_PS.setVisibility(visible);
        editText_PD.setVisibility(visible);
        editText_FC.setVisibility(visible);

        btn_cancel.setVisibility(visible);
        btn_save.setVisibility(visible);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_cancel:
                //Borra todos los editText
                editText_PS.setText("");
                editText_PD.setText("");
                editText_FC.setText("");

                break;

            case R.id.btn_save:
                //Control de que haya datos en todos los campos
                if (editText_PS.getText().toString().isEmpty() | editText_PD.getText().toString().isEmpty() | editText_FC.getText().toString().isEmpty()){
                    //Alguno de los campos está vacío
                    Toast.makeText(getContext(), "Por Favor complete todos los campos. Muchas gracias", Toast.LENGTH_SHORT).show();

                }else{
                    //Todos los campos contienen datos

                    //Control de que los datos ingresados se encuentren dentro de valores normales (no son las alarmas, sino el control de ingreso de datos)

                    //Obtengo los valores ingresados
                    double PS_ingresada = Double.parseDouble(editText_PS.getText().toString());
                    double PD_ingresada = Double.parseDouble(editText_PD.getText().toString());
                    double FC_ingresada = Double.parseDouble(editText_FC.getText().toString());

                    //Control de que cada valor sea válido
                    if (PS_ingresada < minPS | PS_ingresada > maxPS){
                        Toast.makeText(getContext(), "Por Favor revise el valor de PS", Toast.LENGTH_SHORT).show();
                        editText_PS.setError("Por Favor revise el valor de PS. Ej: 120");
                        editText_PS.requestFocus();
                        return;

                    } else if (PD_ingresada < minPD | PD_ingresada > maxPD){
                        Toast.makeText(getContext(), "Por Favor revise el valor de PD. Ej: 80", Toast.LENGTH_SHORT).show();
                        editText_PD.requestFocus();
                        return;

                    } else if (FC_ingresada < minFC | FC_ingresada > maxFC){
                        Toast.makeText(getContext(), "Por Favor revise el valor de la FC. Ej: 70", Toast.LENGTH_SHORT).show();
                        editText_FC.requestFocus();
                        return;

                    } else {
                        //Todos los datos ingresados són válidos

                        //CONTROL ALERTA AMARILLA
                        if(PS_ingresada > ps_max | PS_ingresada < ps_min){

                            String descripcion = "El paciente tiene su PS fuera de los valores normales: " + String.valueOf(PS_ingresada) +"mmHg.";
                            //guardar Alarma en BD
                            guardarAlerta(descripcion);
                        }

                        if(PD_ingresada < pd_min){

                            String descripcion = "El paciente tiene su PD fuera de los valores normales: " + String.valueOf(PD_ingresada) +"mmHg.";
                            //guardar Alarma en BD
                            guardarAlerta(descripcion);
                        }

                        if(FC_ingresada > fc_max | FC_ingresada < fc_min){

                            String descripcion = "El paciente tiene su FC fuera de los valores normales: " + String.valueOf(FC_ingresada) +"lat/min.";
                            //guardar Alarma en BD
                            guardarAlerta(descripcion);
                        }

                        //Guardar los datos
                        boolean datosGuardados = guardarBD(PS_ingresada, PD_ingresada, FC_ingresada);

                        if (datosGuardados){
                            //Mensaje provisorio luego cambiar por un ALERT DIALOG
                            Toast.makeText(getContext(),R.string.alert_dialog_datos_correctos, Toast.LENGTH_LONG).show();

                            //Enviar email - SE ENVÍA A TRAVÉS DEL SERVICE GENERAR EMAIL UNA VEZ X DÍA CON TODAS LAS MEDICIONES
                     /*       //  enviarEmail();
                            textoEnviar = "Mediciones de hoy: " + "\n PS: " + editText_PS.getText().toString() + "mmHg" +
                                    "\n PD: " + editText_PD.getText().toString() + "mmHg" +
                                    "\n FC: " + editText_FC.getText().toString() + "lat/min";

                         //PRUEBA SERVICE 15/08
                            Intent intent = new Intent(getContext(), ServiceEnviarEmail.class);
                            intent.setAction(Constants.SERVICE_ENVIAR_EMAIL_ACTION_RUN_SERVICE);
                            intent.putExtra(Constants.SERVICE_ENVIAR_EMAIL_ASUNTO,"Medición PA-FC");
                            intent.putExtra(Constants.SERVICE_ENVIAR_EMAIL_TEXTO_ENVIAR, textoEnviar);
                            getActivity().startService(intent);

                          //  EnviarMailSegundoPlano enviarMailSegundoPlano = new EnviarMailSegundoPlano("Medición PA-FC", textoEnviar);
                          //  enviarMailSegundoPlano.execute();

*/
                     //elimina recordatorio de la BD
                            //eliminarRecordatorioBD(fecha);
                            Recordatorio mRecordatorio = new Recordatorio();
                            mRecordatorio.eliminarRecordatorioBD(getContext(), Constants.PARAMETRO_PAFC);

                        }else{
                            Toast.makeText(getContext(),R.string.alert_dialog_problemasBD, Toast.LENGTH_LONG).show();
                        }


                    }

                }

                break;

            default:
                break;

        }

        //Control de datos existentes con la fecha actual

        datosCargados = buscarDatosHoy();

        refreshView();

    }

    private void guardarAlerta(String descripcion) {
        Alertas_DBHelper mDBHelper = new Alertas_DBHelper(getContext());
        SQLiteDatabase dbAlerta = mDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(AlertasContract.AlertasEntry.FECHA, fechaHora);
        values.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_AMARILLA);
        values.put(AlertasContract.AlertasEntry.PARAMETRO, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA);
        values.put(AlertasContract.AlertasEntry.DESCRIPCION, descripcion);
        values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);

        try{
            long controlInsert = dbAlerta.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);
            dbAlerta.close();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private boolean guardarBD(double ps_ingresada, double pd_ingresada, double fc_ingresada) {
        Autodiagnostico_DBHelper dbHelper = new Autodiagnostico_DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(AutodiagnosticoContract.AutodiagnosticoEntry.PA_DATE, fechaHora);
        values.put(AutodiagnosticoContract.AutodiagnosticoEntry.PA_PS, ps_ingresada);
        values.put(AutodiagnosticoContract.AutodiagnosticoEntry.PA_PD, pd_ingresada);
        values.put(AutodiagnosticoContract.AutodiagnosticoEntry.PA_FC, fc_ingresada);
        values.put(AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO, AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO_PENDIENTE);

        //Devuelve -1 si hay un error al insertar los datos en la base de datos
        long controlInsert = db.insert(AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PA, null, values);
        db.close();
        //En caso de que los datos no se hayan insertado correctamente devuelve false
        if (controlInsert != -1){
            return true;
        }else{
            return false;
        }
    }

}
