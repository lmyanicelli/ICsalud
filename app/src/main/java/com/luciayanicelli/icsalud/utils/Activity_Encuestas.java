package com.luciayanicelli.icsalud.utils;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;
import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.Alertas_DBHelper;
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

                /*14/08/18 Configuraciones configuraciones = new Configuraciones(getApplicationContext());
                String contactos = configuraciones.getEmailAdministrator();
                EnviarMailSegundoPlano enviarMailSegundoPlano = new EnviarMailSegundoPlano(
                        getApplicationContext(),
                        "Encuestas contestadas",
                        "El paciente indicó que contestó todas las encuestas",
                        contactos);
                enviarMailSegundoPlano.execute();
                borrarRecordatorio();
                */

                //Guardar alerta roja level_roja - type_heartRate
                String descripcion = "Encuestas contestadas";

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
                values.put(AlertasContract.AlertasEntry.PARAMETRO, JSON_CONSTANTS.HEART_RATES);
                values.put(AlertasContract.AlertasEntry.DESCRIPCION, descripcion);
                values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);

                try{
                    long controlInsert = dbAlerta.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);

                    dbAlerta.close();

                }catch(Exception e){
                    e.printStackTrace();
                }

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

 /*   @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
*/

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