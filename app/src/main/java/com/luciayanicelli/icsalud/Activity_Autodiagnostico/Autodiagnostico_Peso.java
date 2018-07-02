package com.luciayanicelli.icsalud.Activity_Autodiagnostico;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;
import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.Alertas_DBHelper;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_DBHelper;
import com.luciayanicelli.icsalud.R;
import com.luciayanicelli.icsalud.Services.Constants;
import com.luciayanicelli.icsalud.utils.FechaActual;
import com.luciayanicelli.icsalud.utils.Recordatorio;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;


//dudo si tiene q ser un fragment o un activity
public class Autodiagnostico_Peso extends Fragment implements View.OnClickListener {

    //Vista
    TextView textView_header, textView_kg;
    EditText editText_peso;
    Button btn_cancel, btn_save;

    private boolean pesoCargado;

    //Texto que se enviará una vez que el usuario cargue su peso
    private String fecha, fechaHora;
    private FechaActual fechaActual;
    private ScrollView scrollView;

    //BD
    private SQLiteDatabase dbAuto;

    //MODIFICAR LUEGO CUANDO FUNCIONE CONFIGURACIONES CORRECTAMENTE
   // private String nombrePaciente; //Obtener de CONFIGURACION


    private double min_peso; //Obtener de configuración
    private double max_peso; //Obtener de configuración


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.autodiagnostico_peso, container, false);

        //Armado de la vista
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        textView_header = (TextView) view.findViewById(R.id.textView_header);
        textView_kg = (TextView) view.findViewById(R.id.textView_kg);
        editText_peso = (EditText) view.findViewById(R.id.editText_peso);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_save = (Button) view.findViewById(R.id.btn_save);

        btn_cancel.setOnClickListener((View.OnClickListener) this);
        btn_save.setOnClickListener((View.OnClickListener) this);

        //Acceder BD
        //Buscar si existe un peso cargado con la fecha de hoy y determinar el booleano pesoCargado en true o false
        pesoCargado = buscarDatosHoy();

        //Actualizar vista
        refreshView();

        //INICIALIZO VALORES DE CONFIGURACION
        Configuraciones configuraciones = new Configuraciones(getContext());

        min_peso = Double.valueOf(configuraciones.getPESOIngresarMin()); //Obtener de configuración
        max_peso = Double.valueOf(configuraciones.getPESOIngresarMax()); //Obtener de configuración

        //nombrePaciente = configuraciones.getUserName() + " " + configuraciones.getUserSurname();

        return view;
    }

    ////CORROBORAR QUE EXISTA EL PESO CARGADO CON LA FECHA ACTUAL PARA IMPEDIR QUE SIGA CARGANDO DATOS
    private boolean buscarDatosHoy() {
        //Acceder a la BD y ver si existen datos cargados con la fecha de hoy

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
        dbAuto = mdbHelper.getWritableDatabase();


        String[] camposDB2 = new String[]{AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE,
                AutodiagnosticoContract.AutodiagnosticoEntry.PESO_VALOR};
    //    String selection = AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE + "= ?";
    //    String[] args = new String[] {String.valueOf(fecha)};

        String selection = AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE + ">= ?"+
                " and " + AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE + "<= ?";
        String[] args = new String[] {String.valueOf(fecha + " 00:00:00"), String.valueOf(fecha + " 23:59:59")}; //busco en el dia completo

        Cursor busqueda = dbAuto.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO,
                camposDB2, selection, args,null,null,null,null);

        //Si existen datos guardados con la fecha de hoy devuelve true
        if (busqueda != null & busqueda.moveToFirst()){
            //eliminar recordatorio de BD si es que existe
            //eliminarRecordatorioBD(fecha);
            Recordatorio mRecordatorio = new Recordatorio();
            mRecordatorio.eliminarRecordatorioBD(getContext(), Constants.PARAMETRO_PESO);
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

    private void refreshView() {

        //Vuelve el scroll al inicio
        scrollView.fullScroll(scrollView.FOCUS_UP);

        //Setear texto en el header de acuerdo a si se cargó o no la medición del peso en el día de hoy
        if (pesoCargado) {
            //Hay peso cargado
            textView_header.setText(getResources().getText(R.string.msj_peso_cargado));
            setVisible(false);



        } else {
            //No existe un peso cargado
            textView_header.setText(getResources().getText(R.string.msj_peso_cargar));
            setVisible(true);
        }
    }


    //Hace visible o invisible el editText, textView kg y los botones
    public void setVisible(boolean bool){

        int visible;

        if (bool) {
             visible = View.VISIBLE;
        } else {
            visible = View.INVISIBLE;
        }

        editText_peso.setVisibility(visible);
        textView_kg.setVisibility(visible);
        btn_cancel.setVisibility(visible);
        btn_save.setVisibility(visible);


    }


    //Respuesta de los botones
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View view) {

       switch (view.getId()){

            case R.id.btn_cancel:
                //Acción Cancelar
                editText_peso.setText("");

                break;

           case R.id.btn_save:
               //Acción Guardar

               //Controlar que el dato ingresado esté dentro de valores normales (no son las alarmas sino el control de ingreso de datos)
               double peso_ingresado = Double.parseDouble(editText_peso.getText().toString());

               if (min_peso < peso_ingresado & peso_ingresado < max_peso){
                   //Peso correcto

                   //CONTROL ALERTA AMARILLA
                   Autodiagnostico_DBHelper dbAuto = new Autodiagnostico_DBHelper(getContext());
                   SQLiteDatabase db = dbAuto.getWritableDatabase();

                   String[] camposDBControl = new String[]{AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE,
                           AutodiagnosticoContract.AutodiagnosticoEntry.PESO_VALOR};
                   String selection = AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE + ">= ?"+
                           " and " + AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE + "<= ?";


                   //Obtengo la fecha de hace 3 días
                   String fecha3diasAntes;
                   Calendar calendar3dias = Calendar.getInstance();
                   calendar3dias.add(Calendar.DAY_OF_YEAR, -3);

                   SimpleDateFormat simpleDateFormat = new SimpleDateFormat(JSON_CONSTANTS.DATE_TIME_FORMAT);
                   fecha3diasAntes = simpleDateFormat.format(calendar3dias.getTime()).split(" ")[0];

                   String[] args3dias = new String[] {String.valueOf(fecha3diasAntes + " 00:00:00"), String.valueOf(fecha3diasAntes + " 23:59:59")};
                   Cursor busqueda3dias = db.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO,
                           camposDBControl, selection, args3dias, null, null, null, null);

                   double peso3diasAntes;
                   double pesoHoy = peso_ingresado;

                   //Si existen datos guardados con la fecha3diasAntes
                   if (busqueda3dias != null & busqueda3dias.moveToFirst()) {
                           peso3diasAntes = busqueda3dias.getDouble(1);

                           if ((pesoHoy - peso3diasAntes) >= 2.00) {
                               //Se debe generar la alarmaAmarilla

                               String descripcion = "El paciente aumentó " + String.valueOf(pesoHoy - peso3diasAntes) +"Kg en los últimos 3 días";
                               //guardar Alarma en BD
                               guardarAlerta(descripcion);

                           }
                           busqueda3dias.close();

                       }else{

                       //01/11/17

                       //Comparar con fecha hace 2 días, 1 día o 4 días - para evitar aquellos casos en los que el paciente no cargue siempre el peso
                       if(compararFechaHaceXdias(db, camposDBControl, selection, peso_ingresado, -2)){
                           //aumentó más de 2 kilos en 2 días
                       }else if(compararFechaHaceXdias(db, camposDBControl, selection, peso_ingresado, -1)){
                           //aumentó más de 2 kilos en 1 día
                       } else if (compararFechaHaceXdias(db, camposDBControl, selection, peso_ingresado, -4)){
                           //aumentó más de 2 kilos en 4 días
                       }

                       }




                   //Guardar dato en BD
                   Boolean pesoGuardado = guardarBD(peso_ingresado);

                   if (pesoGuardado){
                       //Mensaje provisorio luego cambiar por un ALERT DIALOG
                       Toast.makeText(getContext(),R.string.alert_dialog_peso_correcto, Toast.LENGTH_LONG).show();

                       Recordatorio mRecordatorio = new Recordatorio();
                       mRecordatorio.eliminarRecordatorioBD(getContext(), Constants.PARAMETRO_PESO);
                   }else{
                       Toast.makeText(getContext(),R.string.alert_dialog_problemasBD, Toast.LENGTH_LONG).show();
                   }


               }else{
                   //Peso incorrecto

                   //Mensaje provisorio luego cambiar por un ALERT DIALOG
                   Toast.makeText(getContext(),R.string.alert_dialog_peso_incorrecto, Toast.LENGTH_LONG).show();
                   editText_peso.setText("");

               }

               break;

           default:
               break;
       }

       //Consulto si existen datos guardados con la fecha de hoy antes de refrescar la vista
        pesoCargado = buscarDatosHoy();

        refreshView();


    }

    //01/11/17
    private boolean compararFechaHaceXdias(SQLiteDatabase db, String[] camposDBControl, String selection, double peso_ingresado, int cantidadDias) {

        boolean b = false;

        //Obtengo la fecha de hace 2 días
        String fecha3diasAntes;
        Calendar calendar3dias = Calendar.getInstance();
        calendar3dias.add(Calendar.DAY_OF_YEAR, cantidadDias);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(JSON_CONSTANTS.DATE_TIME_FORMAT);
        fecha3diasAntes = simpleDateFormat.format(calendar3dias.getTime()).split(" ")[0];

        String[] args3dias = new String[] {String.valueOf(fecha3diasAntes + " 00:00:00"), String.valueOf(fecha3diasAntes + " 23:59:59")};

        Cursor busqueda3dias = db.query(true, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO,
                camposDBControl, selection, args3dias, null, null, null, null);

        double peso3diasAntes;
        double pesoHoy = peso_ingresado;

        //Si existen datos guardados con la fecha3diasAntes
        if (busqueda3dias != null & busqueda3dias.moveToFirst()) {
            peso3diasAntes = busqueda3dias.getDouble(1);

            if ((pesoHoy - peso3diasAntes) >= 2.00) {
                //Se debe generar la alarmaAmarilla

                String descripcion = "El paciente aumentó "
                        + String.format("%.2f", pesoHoy - peso3diasAntes) //restringe los decimales a 2
                        + " Kgs en los últimos " + String.valueOf(-cantidadDias) +" días";
                //guardar Alarma en BD
                guardarAlerta(descripcion);

            }else{
                b =  false;
            }
        }else{
             b = false;
        }

        busqueda3dias.close();
        return b;
    }

    private void guardarAlerta(String descripcion) {
        Alertas_DBHelper mDBHelper = new Alertas_DBHelper(getContext());
        SQLiteDatabase dbAlerta = mDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(AlertasContract.AlertasEntry.FECHA, fechaHora);
        values.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_AMARILLA);
        values.put(AlertasContract.AlertasEntry.PARAMETRO, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO);
        values.put(AlertasContract.AlertasEntry.DESCRIPCION, descripcion);
        values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);

        try{
            long controlInsert = dbAlerta.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private boolean guardarBD(double peso_ingresado) {

            Autodiagnostico_DBHelper dbHelper = new Autodiagnostico_DBHelper(getContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
//cambiar fecha por fechaHora
            values.put(AutodiagnosticoContract.AutodiagnosticoEntry.PESO_DATE, fechaHora);
            values.put(AutodiagnosticoContract.AutodiagnosticoEntry.PESO_VALOR, peso_ingresado);
            values.put(AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO, AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO_PENDIENTE);

        //Devuelve -1 si hay un error al insertar los datos en la base de datos
            long controlInsert = db.insert(AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO, null, values);
            db.close();
        //En caso de que los datos no se hayan insertado correctamente devuelve false
        if (controlInsert != -1){

            return true;
        }else{
            return false;
        }

    }

}