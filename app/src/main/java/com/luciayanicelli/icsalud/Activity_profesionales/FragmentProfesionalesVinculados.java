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

import com.luciayanicelli.icsalud.Api_Json.Get_Practitioner_index;
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

public class FragmentProfesionalesVinculados extends ListFragment {

   onProfesionalesVinculadosSelectedListener mCallbackPV;


    private ListAdapter listAdapter;
    private boolean clickable = Boolean.FALSE;
    private int arguments;


    // Interfaz que la Activity contenedora debe implementar
        // para poder tener comunicación

        public interface onProfesionalesVinculadosSelectedListener {
            void onProfesionalesVinculadosSelected(int arguments, String profesional, String datos, String id_profesional);
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

            if(!conexionInternet.execute().get()){
                //No hay conexión a internet
                String mensaje = "Corrobore su conexión a internet e intente nuevamente";
                datos.add(new Lista_entrada_profesionales("-1", mensaje, mensaje));

            }else {
                //Hay conexión a internet
                arguments = getArguments().getInt(JSON_CONSTANTS.PRACTITIONER_STATUS);

                Get_Practitioner_index get_practitioner_index = new Get_Practitioner_index(getContext(), arguments);

                HashMap<String, String> data = get_practitioner_index.execute().get();

                if (data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(String.valueOf(Boolean.TRUE))) {

                    if (data.get(JSON_CONSTANTS.RESPONSE_TOTAL).equalsIgnoreCase("0")) {
                        //No hay profesionales en el listado
                        String mensaje;
                        if (arguments == JSON_CONSTANTS.PRACTITIONER_STATUS_FRIEND) {
                            mensaje = "No se encuentra vinculado a ningún profesional. Envíe una solicitud desde la opción ENVIAR NUEVA SOLICITUD";
                        } else {
                            mensaje = "No tiene solicitudes";
                        }
                        datos.add(new Lista_entrada_profesionales("-1", mensaje, mensaje));

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
                        Collections.sort(list);

                        for (int j = 0; j < list.size(); j++) {
                            String profesional = list.get(j);

                            datos.add(listado.get(list.get(j)));

                        }

                        clickable = Boolean.TRUE;


                    }
                } else {
                    //Error de red
                    datos.add(new Lista_entrada_profesionales("-1", "Hubo un problema. Por favor corrobore su conexión a internet o intente más tarde.",
                            "Hubo un problema. Por favor corrobore su conexión a internet o intente más tarde."));
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
                 mCallbackPV = (onProfesionalesVinculadosSelectedListener) activity;

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
            mCallbackPV = (onProfesionalesVinculadosSelectedListener) context;

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

            mCallbackPV.onProfesionalesVinculadosSelected(arguments, profesional, datos, id_profesional);

            }

        }

}
