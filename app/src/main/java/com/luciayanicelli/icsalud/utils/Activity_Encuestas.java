package com.luciayanicelli.icsalud.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.R;
import com.luciayanicelli.icsalud.Services.ConexionInternet;
import com.luciayanicelli.icsalud.Services.Constants;

import java.util.concurrent.ExecutionException;


public class Activity_Encuestas extends AppCompatActivity implements
        FragmentEncuestas.onListadoEncuestasSelectedListener{

    private FragmentEncuestas fragmentEncuestas;
    private Button btn_cancelar, btn_encuestasContestadas;
    private TextView textView_principal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuestas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        textView_principal = findViewById(R.id.textView_principal);
        btn_cancelar = findViewById(R.id.btn_cancel);
        btn_encuestasContestadas = findViewById(R.id.btn_encuestasContestadas);

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizar();
            }
        });

        btn_encuestasContestadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Configuraciones configuraciones = new Configuraciones(getApplicationContext());
                String contactos = configuraciones.getEmailAdministrator();
                EnviarMailSegundoPlano enviarMailSegundoPlano = new EnviarMailSegundoPlano(
                        getApplicationContext(),
                        "Encuestas contestadas",
                        "El paciente indicó que contestó todas las encuestas",
                        contactos);
                enviarMailSegundoPlano.execute();
                borrarRecordatorio();
                finalizar();
            }
        });

        fragmentEncuestas = new FragmentEncuestas();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_encuestas, fragmentEncuestas).commit();

    }

    private void borrarRecordatorio() {
        Recordatorio mRecordatorio = new Recordatorio();
        mRecordatorio.eliminarRecordatorioBD(getApplicationContext(), Constants.PARAMETRO_ENCUESTAS);
    }

    private void finalizar() {
        this.finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private String getUrl(String name) {

        switch (Integer.parseInt(name)){
            case 0:
                return getResources().getString(R.string.url_encuesta1);

            case 1:
                return getResources().getString(R.string.url_encuesta2);

            case 2:
                return getResources().getString(R.string.url_encuesta3);

            case 3:
                return getResources().getString(R.string.url_encuesta4);

            default:
                return null;
        }

    }


    @Override
    public void onListadoEncuestaSelected(int position) throws ExecutionException, InterruptedException {

        ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());

        if(conexionInternet.execute().get()){
            String url;

            url = getUrl(String.valueOf(position));

            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }else {
            Toast.makeText(getApplicationContext(),
                    "Para poder contestar la encuesta debe estar conectado a Internet. Una vez conectado intente nuevamente.",
                    Toast.LENGTH_LONG).show();
        }


    }
}