package com.luciayanicelli.icsalud;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.utils.Alertas;

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
        Alertas mAlertas = new Alertas(getApplicationContext());
        mAlertas.guardar(AlertasContract.AlertasEntry.ALERTA_TIPO_ROJA,
                AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_PESO,
                descripcion,
                AlertasContract.AlertasEntry.ALERTA_VISIBILIDAD_PRIVADA);

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
