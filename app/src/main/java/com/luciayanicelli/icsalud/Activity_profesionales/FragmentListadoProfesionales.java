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

import com.luciayanicelli.icsalud.Api_Json.Get_Professionals_index;
import com.luciayanicelli.icsalud.Api_Json.JSON_CONSTANTS;
import com.luciayanicelli.icsalud.R;
import com.luciayanicelli.icsalud.Services.ConexionInternet;
import com.luciayanicelli.icsalud.utils.Lista_adaptador;
import com.luciayanicelli.icsalud.utils.Lista_entrada_profesionales;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/*LIST FRAGMENT*/

public class FragmentListadoProfesionales extends ListFragment{

   onListadoProfesionalesSelectedListener mCallbackPV;


    private ListAdapter listAdapter;
    private boolean clickable = Boolean.FALSE;


    // Interfaz que la Activity contenedora debe implementar
        // para poder tener comunicación

        public interface onListadoProfesionalesSelectedListener {
            void onListadoProfesionalesSelected(String profesional, String datos, String id_profesional);
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
            Get_Professionals_index get_professionals_index = new Get_Professionals_index();

            HashMap<String, String> data = get_professionals_index.execute().get();

            if (data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(String.valueOf(Boolean.FALSE))) {

                if (data.get(JSON_CONSTANTS.ERROR_MESSAGE).equalsIgnoreCase("0")) {
                    //No hay profesionales en el listado
                    String mensaje = "No hay profesionales disponibles";
                    datos.add(new Lista_entrada_profesionales("-1", mensaje, mensaje));

                } else {
                    //Error de red
                    datos.add(new Lista_entrada_profesionales("-1", "Hubo un problema. Por favor corrobore su conexión a internet o intente más tarde.",
                            "Hubo un problema. Por favor corrobore su conexión a internet o intente más tarde."));
                }

                clickable = Boolean.FALSE;

            } else {

                String professionals = data.get(JSON_CONSTANTS.PROFESSIONALS);
                String id_professionals = data.get(JSON_CONSTANTS.ID);
                String email_professionals = data.get(JSON_CONSTANTS.EMAIL);

                String[] profesionales = professionals.split(";");
                String[] id_profesionales = id_professionals.split(";");
                String[] email_profesionales = email_professionals.split(";");

                //Recupera los datos en un HashMap para luego poder ordenarlos alfabéticamente
                HashMap<String, Lista_entrada_profesionales> listado = new HashMap<>();

                for (int i = 0; i < profesionales.length; i++) {
                    listado.put(profesionales[i], new Lista_entrada_profesionales(id_profesionales[i], profesionales[i], email_profesionales[i]));
                }

                ArrayList<String> list = new ArrayList<>((Collection<? extends String>) Arrays.asList(profesionales));
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
                 mCallbackPV = (onListadoProfesionalesSelectedListener) activity;

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
            mCallbackPV = (onListadoProfesionalesSelectedListener) context;

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

            mCallbackPV.onListadoProfesionalesSelected(profesional, datos, id_profesional);

            }

        }

}
