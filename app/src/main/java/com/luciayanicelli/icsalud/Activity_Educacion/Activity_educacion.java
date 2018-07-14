package com.luciayanicelli.icsalud.Activity_Educacion;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.luciayanicelli.icsalud.Activity_Autodiagnostico.Activity_Menu_Autodiagnostico;
import com.luciayanicelli.icsalud.Activity_Configuracion.Activity_configuracion;
import com.luciayanicelli.icsalud.Activity_Juego.Activity_juego;
import com.luciayanicelli.icsalud.Activity_SOS;
import com.luciayanicelli.icsalud.Activity_profesionales.Activity_profesionales;
import com.luciayanicelli.icsalud.MainActivity;
import com.luciayanicelli.icsalud.R;
import com.luciayanicelli.icsalud.Services.Constants;
import com.luciayanicelli.icsalud.utils.AlertDialogs;



/*
*
*/

public class Activity_educacion extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AlertDialogs.NoticeDialogListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educacion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //TABS
        TabLayout tabs = findViewById(R.id.tabs_educacion);

        tabs.addTab(tabs.newTab().setText(R.string.tab_pregfrec));
      //  tabs.addTab(tabs.newTab().setText(R.string.tab_wikiCardio));

        //http://www.truiton.com/2015/06/android-tabs-example-fragments-viewpager/
        final ViewPager viewPager = findViewById(R.id.pager_educacion);
        final PagerAdapterEducacion adapter = new PagerAdapterEducacion
                (getSupportFragmentManager(), tabs.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.getAdapter().notifyDataSetChanged();

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_autodiagnostico) {

            //Llama a la actividad de Autodiagnóstico
            Intent intent = new Intent();
            intent.setClass(Activity_educacion.this, Activity_Menu_Autodiagnostico.class);
            intent.putExtra(Constants.PARAMETRO, Constants.PARAMETRO_PESO);
            startActivity(intent);

        } else if (id == R.id.nav_educacion) {

            Intent intent = new Intent();
            intent.setClass(Activity_educacion.this, Activity_educacion.class);
            startActivity(intent);

        } else if (id == R.id.nav_configuracion) {

            Intent intent = new Intent();
            intent.setClass(Activity_educacion.this, Activity_configuracion.class);
            startActivity(intent);

        } else if (id == R.id.nav_recordatorios) {

            //Llama a la actividad principal
            Intent intent = new Intent();
            intent.setClass(Activity_educacion.this, MainActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_juego) {

            Intent intent = new Intent();
            intent.setClass(Activity_educacion.this, Activity_juego.class);
            startActivity(intent);


        }  else if (id == R.id.nav_profesionales) {

            //Llama a la actividad profesionales
            Intent intent = new Intent();
            intent.setClass(Activity_educacion.this, Activity_profesionales.class);
            startActivity(intent);


        } else if (id == R.id.nav_sos) {

            //Muestra un alertDialog consultando al paciente si desea solicitar auxilio a su médico, ambulancia y cuidadores
            //En caso afirmativo obtiene ubicación y la envía por msj de texto y mail
            //Luego consulta al usuario si desea llamar a su ambulancia
            crearAlertDialogSOS();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void crearAlertDialogSOS() {

        AlertDialogs alertDialogs = new AlertDialogs();
        alertDialogs.setMsj(getResources().getString(R.string.sos_msj));
        alertDialogs.setName(Constants.SOS);

        alertDialogs.setPositiveButton(getResources().getString(R.string.sos_auxilio));
        alertDialogs.setNegativeButton(getResources().getString(R.string.cancelar));

        DialogFragment alertSOS = alertDialogs;
        alertSOS.show(getSupportFragmentManager(), "alertSOS");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String name) {
        if(name.equalsIgnoreCase(Constants.SOS)){
            //Llama a la actividad sos
            Intent intent = new Intent();
            intent.setClass(Activity_educacion.this, Activity_SOS.class);
            startActivity(intent);
        }

    }


    @Override
    public void onDialogNegativeClick(DialogFragment dialog, String name) {

    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();

    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }
}
