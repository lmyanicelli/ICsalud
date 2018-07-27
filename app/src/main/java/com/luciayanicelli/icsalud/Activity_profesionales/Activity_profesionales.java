package com.luciayanicelli.icsalud.Activity_profesionales;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.luciayanicelli.icsalud.Activity_Autodiagnostico.Activity_Menu_Autodiagnostico;
import com.luciayanicelli.icsalud.Activity_Configuracion.Activity_configuracion;
import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.Activity_Educacion.Activity_educacion;
import com.luciayanicelli.icsalud.Activity_Juego.Activity_juego;
import com.luciayanicelli.icsalud.Activity_SOS;
import com.luciayanicelli.icsalud.Api_Json.Del_Contact;
import com.luciayanicelli.icsalud.Api_Json.Del_Practitioner;
import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;
import com.luciayanicelli.icsalud.Api_Json.Post_Contact;
import com.luciayanicelli.icsalud.Api_Json.Post_Practitioner;
import com.luciayanicelli.icsalud.MainActivity;
import com.luciayanicelli.icsalud.R;
import com.luciayanicelli.icsalud.Services.ConexionInternet;
import com.luciayanicelli.icsalud.Services.Constants;
import com.luciayanicelli.icsalud.utils.AlertDialogs;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;



/*
* Activity_profesionales
* Gestiona las vinculaciones con el personal de salud
*/

public class Activity_profesionales extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        AlertDialogs.NoticeDialogListener, FragmentProfesionalesVinculados.onProfesionalesVinculadosSelectedListener,
        FragmentListadoProfesionales.onListadoProfesionalesSelectedListener,
        FragmentListadoContactos.onListadoContactosSelectedListener,
        FragmentNewContact.onClickListener{

    public static final String TAG = "ExampleFragment";

    private Spinner spinner;
    private String nameProfesionalesVinculados = "nameProfesionalesVinculados";
    private String mProfesional; //profesional seleccionado
    private String nameDesvincular = "nameDesvincular";
    private Bundle args;
    private String nameSolicitudesRecibidas = "nameSolicitudesRecibidas";
    private String nameSolicitudesEnviadas = "nameSolicitudesEnviadas";
    private String mIdProfesional;
    private int mSelection; //Opción seleccionada
    private String msjError = "No se pudo realizar la operación. Por favor intente más tarde.";
    private String msjExito = "La operación fue realizada con éxito";
    private String nameEnviarSolicitud = "nameEnviarSolicitud";
    private String nameContacto = "nameContacto";
    private int pos;
    private String mCelProfesional;
    private String mEmailProfesional;
    private static final int REQUEST_CODE_ASK_PERMISSIONS_CALL = 94;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesionales);

        //ActionBar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Menú de la izquierda
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //Seleccionar listado
        spinner = findViewById(R.id.spinner);

        String[] seleccion_profesionales = Constants.ARRAY_SELECTION_VINCULACIONES_PROFESIONALES;
        spinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_items, seleccion_profesionales));

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            pos = extras.getInt("pos");
            spinner.setSelection(pos);
        }


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                mSelection = pos;
                actualizar(mSelection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {                }
        });

    }


    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_autodiagnostico) {

            //Llama a la actividad de Autodiagnóstico
            Intent intent = new Intent();
            intent.setClass(Activity_profesionales.this, Activity_Menu_Autodiagnostico.class);
            intent.putExtra(Constants.PARAMETRO, Constants.PARAMETRO_PESO);
            startActivity(intent);

        } else if (id == R.id.nav_educacion) {

            //Llama a la actividad educación
            Intent intent = new Intent();
            intent.setClass(Activity_profesionales.this, Activity_educacion.class);
            startActivity(intent);

        } else if (id == R.id.nav_configuracion) {

            //Llama a la actividad de configuración
            Intent intent = new Intent();
            intent.setClass(Activity_profesionales.this, Activity_configuracion.class);
            startActivity(intent);


        } else if (id == R.id.nav_juego) {

            //Llama a la actividad juego
            Intent intent = new Intent();
            intent.setClass(Activity_profesionales.this, Activity_juego.class);
            startActivity(intent);

        } else if (id == R.id.nav_recordatorios) {

            //Llama a la actividad principal
            Intent intent = new Intent();
            intent.setClass(Activity_profesionales.this, MainActivity.class);
            startActivity(intent);


        }  else if (id == R.id.nav_sos) {

            //Muestra un alertDialog consultando al paciente si desea solicitar auxilio a su médico, ambulancia y cuidadores
            //En caso afirmativo obtiene ubicación y la envía por msj de texto y mail
            //Luego consulta al usuario si desea llamar a su ambulancia
            crearAlertDialogSOS();

        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
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


    private void crearAlertDialog(String name, String title, String datos, String[] opciones) {

            AlertDialogs alertDialog = new AlertDialogs();
            alertDialog.setMsj(datos);
            alertDialog.setName(name);
            alertDialog.setTitle(title);

            alertDialog.setPositiveButton(opciones[0]);
            alertDialog.setNegativeButton(opciones[1]);

            DialogFragment alertCS = alertDialog;
            alertCS.show(getSupportFragmentManager(), "alertCS");

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String name) {
        if(name.equalsIgnoreCase(nameProfesionalesVinculados)){
            //CANCELAR
        }else if(name.equalsIgnoreCase(nameDesvincular)){
          // "DESVINCULAR" - "SI"
           // eliminarPractitioner();
            Toast.makeText(getApplicationContext(), "Para desvincularse envíe un correo electrónico al profesional.", Toast.LENGTH_LONG).show();
        }else if(name.equalsIgnoreCase(nameSolicitudesRecibidas)){
         //   "ACEPTAR SOLICITUD"
            enviarSolicitud(); // al enviar ambas partes la solicitud se establece la vinculación
        }else if(name.equalsIgnoreCase(nameSolicitudesEnviadas)){
         //   "ACEPTAR"
        }else if(name.equalsIgnoreCase(nameEnviarSolicitud)){
          //  "ACEPTAR"

        }else if(name.equalsIgnoreCase(Constants.SOS)){
            //Llama a la actividad sos
            Intent intent = new Intent();
            intent.setClass(Activity_profesionales.this, Activity_SOS.class);
            startActivity(intent);
        }else if(name.equalsIgnoreCase(nameContacto)) {
            //LLAMAR
          //  llamarTelefono(mCelProfesional);
            checkPermissionCALL();
        }

    }

    private void checkPermissionCALL() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            llamarTelefono(mCelProfesional);

        } else {

            int hasCALLPermission = checkSelfPermission(Manifest.permission.CALL_PHONE);

            if (hasCALLPermission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[] {Manifest.permission.CALL_PHONE},
                        REQUEST_CODE_ASK_PERMISSIONS_CALL);


            }else{

                llamarTelefono(mCelProfesional);

            }

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE_ASK_PERMISSIONS_CALL:

                if (grantResults.length == 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Una vez q corrobora que los permisos fueron concedidos

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    llamarTelefono(mCelProfesional);
                }

                break;

            default:
                break;
        }

    }

    private void llamarTelefono(String tlf) {

        Intent llamada = new Intent(Intent.ACTION_CALL);
        Uri uriTlf = Uri.parse("tel:" + tlf);
        llamada.setData(uriTlf);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }


        startActivity(llamada);
        //cierro la actividad actual
        this.finish();


    }

    //Enviar Solicitud
    private void enviarSolicitud(){

        ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());
        try {
            if(conexionInternet.execute().get()){

                try {
                    Post_Practitioner post_practitioner = new Post_Practitioner(getApplicationContext(), mIdProfesional);
                    HashMap<String, String> response = post_practitioner.execute().get();

                    if(response.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(Boolean.TRUE.toString())){
                        Toast.makeText(getApplicationContext(), msjExito, Toast.LENGTH_LONG).show();
                        if(mSelection!=3){ //No actualiza el listado de profesionales, sólo si es una solicitud recibida
                            actualizar(mSelection);
                        }

                    }else if(response.get(JSON_CONSTANTS.PRACTITIONER).equalsIgnoreCase(String.valueOf(0))){
                        Toast.makeText(getApplicationContext(), "Ya envió la solicitud a este profesional anteriormente" , Toast.LENGTH_LONG).show();

                    }else {
                        Toast.makeText(getApplicationContext(), msjError, Toast.LENGTH_LONG).show();
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }else {
                actualizar(mSelection);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onDialogNegativeClick(DialogFragment dialog, String name) {
        if(name.equalsIgnoreCase(nameProfesionalesVinculados)){
            //DESVINCULAR
            crearAlertDialog(nameDesvincular, "Desvincular", "¿Desea desvincularse de " + mProfesional + "?", new String[]{"SI", "NO"});
        }else if(name.equalsIgnoreCase(nameDesvincular)){
         //  "DESVINCULAR" - "NO"
        }else if(name.equalsIgnoreCase(nameSolicitudesRecibidas)){
        //  "RECHAZAR SOLICITUD"
            eliminarPractitioner();
        }else if(name.equalsIgnoreCase(nameSolicitudesEnviadas)){
        // "CANCELAR SOLICITUD"
            eliminarPractitioner();
        }else if(name.equalsIgnoreCase(nameEnviarSolicitud)){
            //   "ENVIAR SOLICITUD"
            enviarSolicitud();
        }else if(name.equalsIgnoreCase(nameContacto)){
            //   "ELIMINAR"
            eliminarContacto();
        }

    }

    private void eliminarPractitioner() {

        ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());

        try {
            if(conexionInternet.execute().get()){
                // delete {{url}}/patients/{{patient}}/practitioners/{{practitioner}}
                try {
                    Del_Practitioner del_practitioner = new Del_Practitioner(getApplicationContext(), mIdProfesional);
                    HashMap<String, String> data = del_practitioner.execute().get();
                    if (data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(String.valueOf(Boolean.FALSE))) {
                        //FALSE
                        Toast.makeText(getApplicationContext(), msjError, Toast.LENGTH_LONG).show();

                    }else{
                        //TRUE
                        Toast.makeText(getApplicationContext(), msjExito, Toast.LENGTH_LONG).show();
                        actualizar(mSelection);
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }else {
                actualizar(mSelection);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    private void eliminarContacto() {

        ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());

        try {
            if(conexionInternet.execute().get()){
                // delete {{url}}/patients/{{patient}}/practitioners/{{practitioner}}
                try {
                    Del_Contact del_contact = new Del_Contact(getApplicationContext(), mIdProfesional);
                    HashMap<String, String> data = del_contact.execute().get();
                    if (data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(String.valueOf(Boolean.FALSE))) {
                        //FALSE
                        Toast.makeText(getApplicationContext(), msjError, Toast.LENGTH_LONG).show();

                    }else{
                        //TRUE
                        Toast.makeText(getApplicationContext(), msjExito, Toast.LENGTH_LONG).show();
                        actualizar(mSelection);
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }else {
                actualizar(mSelection);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    //Actualizar la página para ver que se haya eliminado o agregado una solicitud
    private void actualizar(int mSelection) {

        if (mSelection==0) { //PROFESIONALES VINCULADOS
            // Creamos un nuevo Bundle
            args = new Bundle();
            args.putInt(String.valueOf(JSON_CONSTANTS.PRACTITIONER_STATUS), JSON_CONSTANTS.PRACTITIONER_STATUS_FRIEND);

            FragmentProfesionalesVinculados fragmentProfesionalesVinculados = new FragmentProfesionalesVinculados();
            fragmentProfesionalesVinculados.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_listado, fragmentProfesionalesVinculados, TAG)
                    .addToBackStack(null)
                    .commit();

        } else if (mSelection==1){ //SOLICITUDES RECIBIDAS

            // Creamos un nuevo Bundle
            args = new Bundle();
            args.putInt(String.valueOf(JSON_CONSTANTS.PRACTITIONER_STATUS), JSON_CONSTANTS.PRACTITIONER_STATUS_PENDING_PATIENT_APPROVAL);

            FragmentProfesionalesVinculados fSolicitudesRecibidas = new FragmentProfesionalesVinculados();
            fSolicitudesRecibidas.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_listado, fSolicitudesRecibidas, TAG)
                    .addToBackStack(null)
                    .commit();

        } else if (mSelection==2){ //SOLICITUDES ENVIADAS
            // Creamos un nuevo Bundle
            args = new Bundle();
            args.putInt(String.valueOf(JSON_CONSTANTS.PRACTITIONER_STATUS), JSON_CONSTANTS.PRACTITIONER_STATUS_PENDING_PROFESSIONAL_APPROVAL);

            FragmentProfesionalesVinculados fSolicitudesEnviadas = new FragmentProfesionalesVinculados();
            fSolicitudesEnviadas.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_listado, fSolicitudesEnviadas, TAG)
                    .addToBackStack(null)
                    .commit();

        } else if (mSelection==3){ //ENVIAR NUEVA SOLICITUD

            FragmentListadoProfesionales fragmentListadoProfesionales = new FragmentListadoProfesionales();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_listado, fragmentListadoProfesionales)
                    .addToBackStack(null)
                    .commit();

        }else if (mSelection==4){ //CONTACTOS EXISTENTES

            FragmentListadoContactos fragmentListadoContactos = new FragmentListadoContactos();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_listado, fragmentListadoContactos)
                    .addToBackStack(null)
                    .commit();

        }else if (mSelection==5){ //NUEVO CONTACTO

            FragmentNewContact fragmentNewContact = new FragmentNewContact();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_listado, fragmentNewContact)
                    .addToBackStack(null)
                    .commit();
        }

    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onProfesionalesVinculadosSelected(int arguments, String profesional, String datos, String id_profesional) {

        mProfesional = profesional;
        mIdProfesional = id_profesional;
        String[]opciones;

        switch (arguments){

            case JSON_CONSTANTS.PRACTITIONER_STATUS_FRIEND:
                //PROFESIONALES VINCULADOS
                opciones = new String[]{"ACEPTAR", "DESVINCULAR"};
                crearAlertDialog(nameProfesionalesVinculados, profesional, datos, opciones);
                break;

            case JSON_CONSTANTS.PRACTITIONER_STATUS_PENDING_PATIENT_APPROVAL:
               //SOLICITUDES RECIBIDAS
                opciones = new String[]{"ACEPTAR SOLICITUD", "RECHAZAR SOLICITUD"};
                crearAlertDialog(nameSolicitudesRecibidas, profesional, datos, opciones);
                break;

            case JSON_CONSTANTS.PRACTITIONER_STATUS_PENDING_PROFESSIONAL_APPROVAL:
                //SOLICITUDES ENVIADAS
                opciones = new String[]{"ACEPTAR", "CANCELAR SOLICITUD"};
                crearAlertDialog(nameSolicitudesEnviadas, profesional, datos, opciones);
                break;

            default:
                break;

        }



    }

    @Override
    public void onListadoProfesionalesSelected(String profesional, String datos, String id_profesional) {
        mProfesional = profesional;
        mIdProfesional = id_profesional;
        String[]opciones;
        opciones = new String[]{"ACEPTAR", "ENVIAR SOLICITUD"};
        crearAlertDialog(nameEnviarSolicitud, profesional, datos, opciones);

    }

    @Override
    public void onClickCrear(String firstName, String lastName, String email, String mobileNumber) {

        ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());

        try {
            if(conexionInternet.execute().get()){
                Post_Contact post_contact = new Post_Contact(getApplicationContext(), firstName, lastName, email, mobileNumber);
                if(post_contact.execute().get()){
                    //Guardar en configuraciones
                    Configuraciones configuraciones = new Configuraciones(getApplicationContext());
                    configuraciones.setUserEmailContacts(email);
                    configuraciones.setUserCelContacts(mobileNumber);
                    configuraciones.setUserNameContacts(lastName + ", " + firstName);

                    Toast.makeText(getApplicationContext(),msjExito, Toast.LENGTH_LONG).show();
                    mSelection = mSelection -1;
                    actualizar(mSelection); //Contactos Existentes
                }else {
                    Toast.makeText(getApplicationContext(),msjError, Toast.LENGTH_LONG).show();
                }
            }else{
                String mensaje = "Corrobore su conexión a internet e intente nuevamente";
                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onListadoContactosSelected(String profesional, String datos, String id_profesional, String cel, String email) {

        mProfesional = profesional;
        mIdProfesional = id_profesional;
        mCelProfesional = cel;
        mEmailProfesional = email;
        String[]opciones;
        opciones = new String[]{"LLAMAR", "ELIMINAR"};
        crearAlertDialog(nameContacto, profesional, datos, opciones);
    }


}
