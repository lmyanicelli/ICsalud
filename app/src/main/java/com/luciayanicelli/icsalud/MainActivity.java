package com.luciayanicelli.icsalud;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.luciayanicelli.icsalud.Activity_Autodiagnostico.Activity_Menu_Autodiagnostico;
import com.luciayanicelli.icsalud.Activity_Configuracion.Activity_configuracion;
import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.Activity_Educacion.Activity_educacion;
import com.luciayanicelli.icsalud.Activity_Juego.Activity_juego;
import com.luciayanicelli.icsalud.Activity_profesionales.Activity_profesionales;
import com.luciayanicelli.icsalud.DataBase.RecordatoriosContract;
import com.luciayanicelli.icsalud.DataBase.RecordatoriosDBHelper;
import com.luciayanicelli.icsalud.Services.Constants;
import com.luciayanicelli.icsalud.utils.Activity_Encuestas;
import com.luciayanicelli.icsalud.utils.FragmentList;



/*
* Main Activity del proyecto iC_Layout
*
*/

//version 2 public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AlertDialogs.NoticeDialogListener, FragmentList.onRecordatorioSelectedListener {
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentList.onRecordatorioSelectedListener
        , View.OnClickListener{

    private static final int REQUEST_SEND_MAIL = 1 ;


    Configuraciones config;
    private String[] emailRemitentes;

    public static final String TAG = "ExampleFragment";


    private FragmentList fragment = new FragmentList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CONFIGURACIONES
        config = new Configuraciones(getApplicationContext());
        emailRemitentes = new String[]{config.getUserEmailRemitente()};

        //ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Botones flotantes
    //version2
        FloatingActionButton fab_mail = findViewById(R.id.fab);
        fab_mail.setOnClickListener(this);

      /*  FloatingActionButton fab_sos =  findViewById(R.id.fab_sos);
        fab_sos.setOnClickListener(this);
        */

        //Menú de la izquierda
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Fragment para mostrar el listado de recordatorios
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment, TAG).commit();

    }



/*version 2    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.fab:
                //ENVIAR MAIL
                enviarEmail();

                this.finish();
                break;

            case R.id.fab_sos:
                //SOS
                crearAlertDialogSOS();
                break;
        }



    }

    //Envio de email - permite seleccionar por donde enviarlo - pero no recibiría los del médico
    private void enviarEmail() {

        //Instanciamos un Intent del tipo ACTION_SEND
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        //Definimos la tipologia de datos del contenido del Email en este caso text/html
        emailIntent.setType("text/html");
        // Indicamos con un Array de tipo String las direcciones de correo a las cuales
        //queremos enviar el texto
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailRemitentes);
        // Definimos un titulo para el Email
        emailIntent.putExtra(Intent.EXTRA_TITLE, "Consulta");
        // Definimos un Asunto para el Email
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Consulta Médica");
        // Obtenemos la referencia al texto y lo pasamos al Email Intent
        try {
            //Enviamos el Correo iniciando una nueva Activity con el emailIntent.
            startActivityForResult(Intent.createChooser(emailIntent, "Enviar E-mail..."), REQUEST_SEND_MAIL);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "No hay ningun cliente de correo instalado.", Toast.LENGTH_SHORT).show();
        }
    }
    */

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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_autodiagnostico) {

            //Llama a la actividad de Autodiagnóstico
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Activity_Menu_Autodiagnostico.class);
            intent.putExtra(Constants.PARAMETRO, Constants.PARAMETRO_PESO);
            startActivity(intent);

        } else if (id == R.id.nav_educacion) {

            //Llama a la actividad educación
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Activity_educacion.class);
            startActivity(intent);

        } else if (id == R.id.nav_configuracion) {

            //Llama a la actividad de configuración
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Activity_configuracion.class);
            startActivity(intent);


        } else if (id == R.id.nav_recordatorios) {

            //Llama a la actividad recordatorios
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MainActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_juego) {

            //Llama a la actividad juego
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Activity_juego.class);
            startActivity(intent);

        } else if (id == R.id.nav_profesionales) {

            //Llama a la actividad profesionales
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Activity_profesionales.class);
            startActivity(intent);


        } /*version 2 else if (id == R.id.nav_sos) {

            //Muestra un alertDialog consultando al paciente si desea solicitar auxilio a su médico, ambulancia y cuidadores
            //En caso afirmativo obtiene ubicación y la envía por msj de texto y mail
            //Luego consulta al usuario si desea llamar a su ambulancia
            crearAlertDialogSOS();

        }*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


 /*version 2   private void crearAlertDialogSOS() {

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
            intent.setClass(MainActivity.this, Activity_SOS.class);
            startActivity(intent);
        }

    }


    @Override
    public void onDialogNegativeClick(DialogFragment dialog, String name) {

    }
*/

    // Función para recibir la interacción del usuario al seleccionar un recordatorio del listado
    @Override
    public void onRecordatorioSelected(int id) {

     //ingresar a la BD recordatorios
        RecordatoriosDBHelper dbHelper = new RecordatoriosDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] camposDB = new String[]{RecordatoriosContract.RecordatoriosEntry.TIPO,
                RecordatoriosContract.RecordatoriosEntry.PARAMETRO,
                RecordatoriosContract.RecordatoriosEntry.RECORDATORIO,
                RecordatoriosContract.RecordatoriosEntry.FECHA};

        String selection = RecordatoriosContract.RecordatoriosEntry._ID + "= ?";

        String[] arg = new String[]{String.valueOf(id)};
        Cursor busqueda = db.query(true, RecordatoriosContract.RecordatoriosEntry.TABLE_NAME,
                camposDB, selection, arg, null, null, null, null);

        String tipo;
        String parametro;
        String textoRecordatorio;
        String fecha;

        if(busqueda != null){
          if(busqueda.moveToFirst()) {

              tipo = busqueda.getString(0);
              parametro = busqueda.getString(1);
              textoRecordatorio = busqueda.getString(2);
              fecha = busqueda.getString(3);

              //si es un recordatorio ACCION - buscar parámetro y acceder a la página de autodiagnóstico para cargar el valor
              if (tipo.equalsIgnoreCase(RecordatoriosContract.RecordatoriosEntry.TIPO_ACCION)) {

                  Intent intentAccion = new Intent();
                  intentAccion.setClass(MainActivity.this, Activity_Menu_Autodiagnostico.class);
                  intentAccion.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  intentAccion.putExtra(Constants.PARAMETRO, parametro);
                  startActivity(intentAccion);

              } else if (tipo.equalsIgnoreCase(RecordatoriosContract.RecordatoriosEntry.TIPO_RECORDATORIO)) {
                  //si es consejo saludable - mostrar el consejo saludable en Activity_ViewConsejoSaludable
                  Intent intentRecordatorio = new Intent();
                  intentRecordatorio.setClass(getApplicationContext(), Activity_ViewConsejoSaludable.class);
                  intentRecordatorio.putExtra(Constants.CONSEJO_SALUDABLE, textoRecordatorio);
                  intentRecordatorio.putExtra(Constants.FECHA_CONSEJO_SALUDABLE, fecha);
                  startActivity(intentRecordatorio);


              } else if (tipo.equalsIgnoreCase(RecordatoriosContract.RecordatoriosEntry.TIPO_MEDICAMENTOS)) {
                  //si es consejo saludable - mostrar el consejo saludable en Activity_ViewConsejoSaludable
                  Intent intentMedicamento = new Intent();
                  intentMedicamento.setClass(getApplicationContext(), Activity_ViewConsultaMedicamentos.class);
                  startActivity(intentMedicamento);

              } else if (tipo.equalsIgnoreCase(RecordatoriosContract.RecordatoriosEntry.TIPO_ENCUESTAS)) {
                  //si es consejo saludable - mostrar el consejo saludable en Activity_ViewConsejoSaludable
                  Intent intentEncuestas = new Intent();
                  intentEncuestas.setClass(getApplicationContext(), Activity_Encuestas.class);
                  startActivity(intentEncuestas);

              }else if (tipo.equalsIgnoreCase(RecordatoriosContract.RecordatoriosEntry.TIPO_SERVICIO_TECNICO)) {
                  //15/08/18 agregar activity contactar servicio técnico
                  Intent intentServicioTecnico = new Intent();
                  intentServicioTecnico.setClass(getApplicationContext(), Activity_Servicio_Tecnico.class);
                  startActivity(intentServicioTecnico);

              }


          }else{
              db.close();
          }
        }
        busqueda.close();

    }

    @Override
    public void onClick(View v) {

    Intent intentServicioTecnico = new Intent();
    intentServicioTecnico.setClass(getApplicationContext(), Activity_Servicio_Tecnico.class);
    startActivity(intentServicioTecnico);

    }
}
