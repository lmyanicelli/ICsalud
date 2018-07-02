package com.luciayanicelli.icsalud.utils;

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

import com.luciayanicelli.icsalud.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/*LIST FRAGMENT*/

public class FragmentEncuestas extends ListFragment{

   onListadoEncuestasSelectedListener mCallback;


    private ListAdapter listAdapter;

    // Interfaz que la Activity contenedora debe implementar
        // para poder tener comunicación

        public interface onListadoEncuestasSelectedListener {
            void onListadoEncuestaSelected(int position) throws ExecutionException, InterruptedException;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
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

        ArrayList<Lista_entrada> listado = new ArrayList<>();

        listado.add(new Lista_entrada("0", getResources().getString(R.string.encuesta1), " "));
        listado.add(new Lista_entrada("1", getResources().getString(R.string.encuesta2), " "));
        listado.add(new Lista_entrada("2", getResources().getString(R.string.encuesta3), " "));
        listado.add(new Lista_entrada("3", getResources().getString(R.string.encuesta4), " "));


       listAdapter = new Lista_adaptador(getContext().getApplicationContext(), R.layout.entrada_profesionales, listado) {
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {

                    TextView textView = view.findViewById(R.id.textView_superior);
                    textView.setText(((Lista_entrada) entrada).get_texto());

                }

            }
        };

       setListAdapter(listAdapter);

    }

    @Override
        public void onAttach(Activity activity) {

            super.onAttach(activity);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
            // Inicializamos nuestra variable de referencia del tipo
            // onListadoContactosSelectedListener junto con el valor del objeto
            // activity que debe ser una Activity que implemente esta interface
            try {
                 mCallback = (onListadoEncuestasSelectedListener) activity;

            } catch (ClassCastException e) {
                Log.d("ClassCastException",
                        "La Activity debe implementar esta Interface");
            }
        }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Inicializamos nuestra variable de referencia del tipo
        // onListadoContactosSelectedListener junto con el valor del objeto
        // activity que debe ser una Activity que implemente esta interface
        try {
            mCallback = (onListadoEncuestasSelectedListener) context;

        } catch (ClassCastException e) {
            Log.d("ClassCastException",
                    "La Activity debe implementar esta Interface");
        }
    }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);

            // Llamamos al método que implementa la Activity pasandole
            // la posicion del elemento que hemos pulsado
            try {
                mCallback.onListadoEncuestaSelected(position);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

}
