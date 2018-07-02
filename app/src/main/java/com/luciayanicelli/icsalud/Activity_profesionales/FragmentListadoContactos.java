package com.luciayanicelli.icsalud.Activity_profesionales;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.Api_Json.Get_Contact;
import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;
import com.luciayanicelli.icsalud.R;
import com.luciayanicelli.icsalud.Services.ConexionInternet;
import com.luciayanicelli.icsalud.utils.Lista_adaptador;
import com.luciayanicelli.icsalud.utils.Lista_entrada_profesionales;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/*LIST FRAGMENT*/

public class FragmentListadoContactos extends ListFragment{

   onListadoContactosSelectedListener mCallbackPV;


    private ListAdapter listAdapter;
    private boolean clickable = Boolean.FALSE;
    private String mensaje;

    // Interfaz que la Activity contenedora debe implementar
        // para poder tener comunicación

        public interface onListadoContactosSelectedListener {
            void onListadoContactosSelected(String profesional, String datos, String id_profesional, String cel, String email);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);


            try {
                armarVista();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    private void armarVista() throws ExecutionException, InterruptedException {

        ArrayList<Lista_entrada_profesionales> datos = new ArrayList<>();

        //primero corroborar conexión a internet
        ConexionInternet conexionInternet = new ConexionInternet(getContext());

        if(!conexionInternet.execute().get()) {
            //No hay conexión a internet
            String mensaje = "Corrobore su conexión a internet e intente nuevamente";
            datos.add(new Lista_entrada_profesionales("-1", mensaje, mensaje));

        }else {
            //Hay conexión a internet

            // GET PATIENT.CONTACTS.INDEX
            Get_Contact get_contacts = new Get_Contact(getContext());
            HashMap<String, String> data = get_contacts.execute().get();

            if (data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(String.valueOf(Boolean.FALSE))) {
                //No hay contactos en el listado
                mensaje = "En estos momentos no se encuentra ningún contacto. Cree un nuevo contacto a través de la opción '"
                        + getResources().getString(R.string.pref_new_contact) +"'. "
                        + "Muchas gracias.";
                datos.add(new Lista_entrada_profesionales("-1", mensaje, mensaje));

                //Guardar en configuraciones
                Configuraciones configuraciones = new Configuraciones(getContext());
                configuraciones.setUserEmailContacts(null);
                configuraciones.setUserCelContacts(null);

                clickable = Boolean.FALSE;

            } else {

                String idContacts = data.get(JSON_CONSTANTS.ID);
                String nombreContacts = data.get(JSON_CONSTANTS.CONTACTS);
                String emailsContacts = data.get(JSON_CONSTANTS.EMAIL);
                String telefonosContacts = data.get(JSON_CONSTANTS.CONTACTS_MOBILE_NUMBER);

                //Guardar en configuraciones
                Configuraciones configuraciones = new Configuraciones(getContext());
                configuraciones.setUserEmailContacts(emailsContacts);
                configuraciones.setUserCelContacts(telefonosContacts);

                String[] id_contact = idContacts.split(";");
                String[] nombres = nombreContacts.split(";");
                String[] emails = emailsContacts.split(",");
                String[] telefonos = telefonosContacts.split(";");
                String[] contactos = new String[nombres.length];


                //Recupera los datos en un HashMap para luego poder ordenarlos alfabéticamente
                HashMap<String, Lista_entrada_profesionales> listado = new HashMap<>();

                for (int i = 0; i < nombres.length; i++) {

                  //  listado.put(nombres[i], new Lista_entrada_profesionales(id_contact[i], nombres[i], "Email: " + emails[i] + "\n" + "Cel: " + telefonos[i]));
                    Lista_entrada_profesionales lista_entrada_profesionales = new Lista_entrada_profesionales(id_contact[i], nombres[i], "Email: " + emails[i] + "\n" + "Cel: " + telefonos[i]);
                    lista_entrada_profesionales.setCel(telefonos[i]);
                    lista_entrada_profesionales.setEmail(emails[i]);
                    listado.put(nombres[i], lista_entrada_profesionales);
                }

              //  ArrayList<String> list = new ArrayList<>((Collection<? extends String>) Arrays.asList(nombres));
                ArrayList<String> list = new ArrayList<>(Arrays.asList(nombres));
                Collections.sort(list); //ordena alfabéticamente

                for (int j = 0; j < list.size(); j++) {
                    datos.add(listado.get(list.get(j)));
                }

                clickable = Boolean.TRUE;


            }

        }

       listAdapter = new Lista_adaptador(getContext().getApplicationContext(), R.layout.entrada_profesionales, datos) {
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {

                    TextView textView = (TextView) view.findViewById(R.id.textView_superior);
                    textView.setText(((Lista_entrada_profesionales) entrada).getProfesional());

                }

            }
        };


       setListAdapter(listAdapter);

    }

    @Override
        public void onAttach(Activity activity) {
            // TODO Auto-generated method stub
            super.onAttach(activity);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
            // Inicializamos nuestra variable de referencia del tipo
            // onListadoContactosSelectedListener junto con el valor del objeto
            // activity que debe ser una Activity que implemente esta interface
            try {
                 mCallbackPV = (onListadoContactosSelectedListener) activity;

            } catch (ClassCastException e) {
                Log.d("ClassCastException",
                        "La Activity debe implementar esta Interface");
            }
        }

    @Override
    public void onAttach(Context context) {
        // TODO Auto-generated method stub
        super.onAttach(context);
        // Inicializamos nuestra variable de referencia del tipo
        // onListadoContactosSelectedListener junto con el valor del objeto
        // activity que debe ser una Activity que implemente esta interface
        try {
            mCallbackPV = (onListadoContactosSelectedListener) context;

        } catch (ClassCastException e) {
            Log.d("ClassCastException",
                    "La Activity debe implementar esta Interface");
        }
    }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            // TODO Auto-generated method stub
            super.onListItemClick(l, v, position, id);

            // Llamamos al método que implementa la Activity pasandole
            // la posicion del elemento que hemos pulsado

            if(clickable){

                Lista_entrada_profesionales elegido = (Lista_entrada_profesionales) l.getItemAtPosition(position);
                  String profesional = elegido.getProfesional();
                    String datos = elegido.getDatos();
                     String id_profesional = elegido.get_id();
                String cel = elegido.getCel();
                String email = elegido.getEmail();


            mCallbackPV.onListadoContactosSelected(profesional, datos, id_profesional, cel, email);

            }

        }

}
