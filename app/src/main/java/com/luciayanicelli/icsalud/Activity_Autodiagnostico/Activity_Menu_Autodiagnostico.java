package com.luciayanicelli.icsalud.Activity_Autodiagnostico;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.luciayanicelli.icsalud.Activity_Configuracion.Activity_configuracion;
import com.luciayanicelli.icsalud.Activity_Educacion.Activity_educacion;
import com.luciayanicelli.icsalud.Activity_Juego.Activity_juego;
import com.luciayanicelli.icsalud.Activity_SOS;
import com.luciayanicelli.icsalud.Activity_profesionales.Activity_profesionales;
import com.luciayanicelli.icsalud.MainActivity;
import com.luciayanicelli.icsalud.R;
import com.luciayanicelli.icsalud.Services.Constants;
import com.luciayanicelli.icsalud.utils.AlertDialogs;
import com.luciayanicelli.icsalud.utils.SetearAlarma;

public class Activity_Menu_Autodiagnostico extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AlertDialogs.NoticeDialogListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__menu__autodiagnostico);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        //Navigation View
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // http://www.hermosaprogramacion.com/2015/06/tablayout-como-anadir-pestanas-en-android/
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);

        tabs.addTab(tabs.newTab().setText(R.string.tab_peso));
        tabs.addTab(tabs.newTab().setText(R.string.tab_fc));
        tabs.addTab(tabs.newTab().setText(R.string.tab_sintomas));


        //http://www.truiton.com/2015/06/android-tabs-example-fragments-viewpager/
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabs.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        //PARA ABRIR UN TAB ESPECÍFICO CUANDO SE SOLICITA ESTA ACTIVITY DESDE UNA NOTIFICACIÓN RECORDATORIO
        String parametro;
        parametro = getIntent().getExtras().getString(Constants.PARAMETRO);

        switch (parametro){
            case Constants.PARAMETRO_PESO:
                viewPager.setCurrentItem(0);
                break;

            case Constants.PARAMETRO_PAFC:
                viewPager.setCurrentItem(1);
                break;

            case Constants.PARAMETRO_SINTOMAS:
                viewPager.setCurrentItem(2);
                break;

            default:
                break;
        }


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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        switch (id){

            case R.id.nav_autodiagnostico:

                //Llama a la actividad de Autodiagnóstico
                intent = new Intent();
                intent.setClass(Activity_Menu_Autodiagnostico.this, Activity_Menu_Autodiagnostico.class);
                intent.putExtra(Constants.PARAMETRO, Constants.PARAMETRO_PESO);
                startActivity(intent);

                break;

            case R.id.nav_educacion:

                //Llama a la actividad educación
                intent = new Intent();
                intent.setClass(Activity_Menu_Autodiagnostico.this, Activity_educacion.class);
                startActivity(intent);

                break;

            case R.id.nav_configuracion:

                //Llama a la actividad de configuración
                intent = new Intent();
                intent.setClass(Activity_Menu_Autodiagnostico.this, Activity_configuracion.class);
                startActivity(intent);

                break;

            case R.id.nav_recordatorios:

                //Llama a la actividad recordatorios
                intent = new Intent();
                intent.setClass(Activity_Menu_Autodiagnostico.this, MainActivity.class);
                startActivity(intent);

                break;

            case R.id.nav_juego:

                intent = new Intent();
                intent.setClass(Activity_Menu_Autodiagnostico.this, Activity_juego.class);
                startActivity(intent);

                break;

            case  R.id.nav_profesionales:

                //Llama a la actividad profesionales
                intent = new Intent();
                intent.setClass(Activity_Menu_Autodiagnostico.this, Activity_profesionales.class);
                startActivity(intent);

                break;

            case R.id.nav_sos:

                //Muestra un alertDialog consultando al paciente si desea solicitar auxilio a su médico, ambulancia y cuidadores
                //En caso afirmativo obtiene ubicación y la envía por msj de texto y mail
                //Luego consulta al usuario si desea llamar a su ambulancia
                crearAlertDialogSOS();

                break;

            default:
                break;

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
        //Llama a la actividad sos
        Intent intent = new Intent();
        intent.setClass(Activity_Menu_Autodiagnostico.this, Activity_SOS.class);
        startActivity(intent);

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, String name) {
    }

    @Override
    protected void onStop() {
        super.onStop();
        SetearAlarma setearAlarma = new SetearAlarma(getApplicationContext(), Constants.PARAMETRO_ENVIAR_DATOS_SERVIDOR);
        setearAlarma.execute();
    }
}
