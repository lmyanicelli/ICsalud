package com.luciayanicelli.icsalud.Activity_Configuracion;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.luciayanicelli.icsalud.R;



/*
* CONFIGURACIÓN:
*
* Permite acceder y editar los datos correspondientes a:
*
* HORARIOS DE MEDICIONES Y RECORDATORIOS
*
* https://developer.android.com/guide/topics/ui/settings.html
*/

public class Activity_configuracion extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        //Agrego el fragment de las preferencias/configuración
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SettingsFragment())
                .commit();

    }

  /*  @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();

    }
    */

}
