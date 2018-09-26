package com.luciayanicelli.icsalud;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.Alertas_DBHelper;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.utils.FechaActual;

import java.util.concurrent.ExecutionException;

/**
 * Created by LuciaYanicelli on 15/8/2018.
 */

public class Activity_Servicio_Tecnico extends AppCompatActivity
        implements FragmentServicioTecnico.onClickListener {


    private String descripcion = "";
    private Configuraciones configuraciones;
    private String user_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_servicio_tecnico);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        // Fragment que muestra el contenido
        FragmentServicioTecnico contFragment = new FragmentServicioTecnico();

        getFragmentManager().beginTransaction()
                .add(R.id.content_frame_servicio_tecnico, contFragment)
                .commit();

        //crear alerta roja en BD con mail y luego con celular en caso de que lo ingrese
        configuraciones = new Configuraciones(getApplicationContext());


    }

    private void crearAlertaRoja(String descripcion) {

        //guardar Alarma en BD
        Alertas_DBHelper mDBHelper = new Alertas_DBHelper(getApplicationContext());
        SQLiteDatabase dbAlerta = mDBHelper.getWritableDatabase();

        FechaActual fechaActual = new FechaActual();
        String fecha = null;
        try {
            fecha = fechaActual.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();

        values.put(AlertasContract.AlertasEntry.FECHA, fecha);
        values.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_ROJA);
        values.put(AlertasContract.AlertasEntry.PARAMETRO, AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO);
        values.put(AlertasContract.AlertasEntry.DESCRIPCION, descripcion);
        values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);
        values.put(AlertasContract.AlertasEntry.VISIBILIDAD, AlertasContract.AlertasEntry.ALERTA_VISIBILIDAD_PRIVADA);

        try{
            long controlInsert = dbAlerta.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onClickCancel() {
        descripcion = getResources().getString(R.string.titulo_servicio_tecnico)+ "-" +
                configuraciones.getUserSurname() +
                "," + configuraciones.getUserName() +
                " - " + configuraciones.getUserEmail();
        crearAlertaRoja(descripcion);

        this.finish();
    }


    @Override
    public void onClickHelp(String phone) {

        user_phone = phone;

        descripcion = getResources().getString(R.string.titulo_servicio_tecnico)+ "-" +
                configuraciones.getUserSurname() +
                "," + configuraciones.getUserName() +
                " - " + configuraciones.getUserEmail() + " - " + phone;

        crearAlertaRoja(descripcion);

       Toast.makeText(getApplicationContext(),
               "Su solicitud de ayuda se envió correctamente. El servicio técnico se contactará con Ud. Muchas gracias",
               Toast.LENGTH_LONG).show();


       // volverMainActivity();
        this.finish();

    }


 /*   @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();

    }
    */

}
