package com.luciayanicelli.icsalud.Activity_Juego;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.luciayanicelli.icsalud.DataBase.JuegoContract;
import com.luciayanicelli.icsalud.DataBase.Juego_DBHelper;
import com.luciayanicelli.icsalud.DataBase.Jugada_DBHelper;
import com.luciayanicelli.icsalud.R;
import com.luciayanicelli.icsalud.Services.Constants;

import java.util.ArrayList;

/*LIST FRAGMENT
*
* https://sekthdroid.wordpress.com/2013/02/05/fragments-listfragment-en-android/
*
* */

public class JuegoFragmentListOpciones extends ListFragment{

        onJuegoSalirListener mCallback;



    private ArrayList items = new ArrayList();

    private ArrayAdapter listAdapter;




    //Activity juego
    private SQLiteDatabase dbJugada;
    private String[] columnasJugada;
    private String selectionJugada;
    private String[] selectionArgsJugada;
    private Cursor cursorJugada;


    private String[] columnasJugadaIncorrecta;
    private String selectionJugadaIncorrecta;
    private String[] selectionArgsJugadaIncorrecta;
    private Cursor cursorJugadaIncorrecta;

    private Juego_DBHelper mJuego;
    private SQLiteDatabase dbJuego;
    private String[] columnasPreguntas;
    private String selectionPreguntas;
    private String[] selectionArgsPreguntas;
    private Cursor cursorPreguntas;

    private String[] columnasOpciones;
    private String selectionOpciones;
    private String[] selectionArgsOpciones;
    private Cursor cursorOpciones;

    private int puntaje = 0;

    //JUGADA
    private int id_nivel;
    private int id_pregunta;
    private int id_opcion;
    private int opcion_puntaje;
    private int puntaje_acumulado;

    private boolean boolean_preguntas_incorrectas;


    //vista
    private TextView textView_pregunta, textView_puntaje, textView_puntos;
    private JuegoFragmentListOpciones fragmentListOpciones;
    private Button btn_siguiente, btn_salir;

    //Vista
    private String pregunta;
    private ArrayList opciones = new ArrayList();
    private String respuestaCorrecta;
    private int idRespuestaCorrecta = -1;

    private ListView listView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_list, container, false);

    }


    // Interfaz que la Activity contenedora debe implementar
        // para poder tener comunicación
        public interface onJuegoSalirListener {
            public void onClickSalir();
    }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);

        }


    @Override
    public void onStart() {
        super.onStart();

        armarVista();
    }

    /**
     * Attach to list view once the view hierarchy has been created.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void armarVista() {

        textView_pregunta = (TextView) getActivity().findViewById(R.id.textView_pregunta);
        textView_puntaje = (TextView) getActivity().findViewById(R.id.textView_puntaje);
        textView_puntos = (TextView) getActivity().findViewById(R.id.textView_puntos);

        btn_siguiente = (Button) getActivity().findViewById(R.id.btn_siguiente);
        btn_salir = (Button) getActivity().findViewById(R.id.btn_salir);

        btn_siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < listView.getChildCount(); i++) {
                    View listItem = listView.getChildAt(i);
                    listItem.setBackgroundColor(Color.TRANSPARENT);
                }



                opciones.clear();

                cargarSiguientePregunta();

                ArrayAdapter arrayAdapter = (ArrayAdapter) listView.getAdapter();
                arrayAdapter.notifyDataSetChanged();


           //volver a activar el click en el listView

                listView.setEnabled(true);

                btn_siguiente.setEnabled(false);


            }
        });
        btn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //actualizar puntaje acumulado en la última fila
                actualizarPuntajeAcumulado();

                mCallback.onClickSalir();
            }
        });

        btn_siguiente.setEnabled(false);


        //buscar jugada
        //Si la tabla esta vacía crear y arrancar con la pregunta 1
        Jugada_DBHelper mJugada = new Jugada_DBHelper(getContext());
        dbJugada = mJugada.getWritableDatabase();

        columnasJugada = new String[]{JuegoContract.JuegoEntry.PREGUNTA_ID, JuegoContract.JuegoEntry.JUGADA_PUNTAJE_ACUMULADO};

        selectionJugada = JuegoContract.JuegoEntry.PREGUNTA_ID;

        cursorJugada = dbJugada.query(JuegoContract.JuegoEntry.TABLE_NAME_JUGADA, columnasJugada, selectionJugada, null, null, null, null);

        if(cursorJugada!= null & cursorJugada.moveToLast()){
            //jugada iniciada
            puntaje_acumulado = cursorJugada.getInt(1);
            cargarSiguientePregunta();

        }else{
            //Jugada por iniciar
            puntaje_acumulado = 0;
            iniciarJuego();
        }

    }

    private void cargarSiguientePregunta() {

        Jugada_DBHelper mJugada = new Jugada_DBHelper(getContext());
        SQLiteDatabase dbJugada = mJugada.getWritableDatabase();

        //Buscar última pregunta contestada
        columnasJugada = new String[]{JuegoContract.JuegoEntry.PREGUNTA_ID, JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL};

        cursorJugada = dbJugada.query(JuegoContract.JuegoEntry.TABLE_NAME_JUGADA,
                columnasJugada, null, null,
                null, null, null);

        int id_proximaPregunta;

        if(cursorJugada!= null & cursorJugada.moveToLast()){
            id_proximaPregunta = cursorJugada.getInt(0) + 1;
            id_nivel = cursorJugada.getInt(1);
            cargarPregunta_Opciones(id_proximaPregunta, id_nivel);

        }


    }

    private void iniciarJuego() {
        //Cargar primera pregunta
        id_pregunta = 1;
        id_nivel = 1;
        cargarPregunta_Opciones(id_pregunta, id_nivel);

    }


    private void cargarPregunta_Opciones(int i_pregunta, int i_nivel) {

        opciones.clear();

        mJuego = new Juego_DBHelper(getContext());
        dbJuego = mJuego.getReadableDatabase();

        //Buscar pregunta en la ubicacion i_pregunta i_nivel
        columnasPreguntas = new String[]{JuegoContract.JuegoEntry.PREGUNTA_TEXTO, JuegoContract.JuegoEntry.PREGUNTA_ID, JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL};
        selectionPreguntas = JuegoContract.JuegoEntry.PREGUNTA_ID + "= ?" +
                " and " + JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL + "= ?";
        selectionArgsPreguntas = new String[]{String.valueOf(i_pregunta), String.valueOf(i_nivel)};

        cursorPreguntas = dbJuego.query(JuegoContract.JuegoEntry.TABLE_NAME_PREGUNTAS,
                columnasPreguntas, selectionPreguntas, selectionArgsPreguntas,
                null, null, null);

        if(cursorPreguntas!= null & cursorPreguntas.moveToFirst()){

            pregunta = cursorPreguntas.getString(0);

            id_pregunta = cursorPreguntas.getInt(1);
            id_nivel = cursorPreguntas.getInt(2);


            cargarOpciones(id_pregunta, id_nivel);


        }else{
            //buscar si existen preguntas en el nivel siguiente
            //Buscar pregunta en la ubicacion i_pregunta i_nivel
            columnasPreguntas = new String[]{JuegoContract.JuegoEntry.PREGUNTA_TEXTO, JuegoContract.JuegoEntry.PREGUNTA_ID, JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL};
            selectionPreguntas = JuegoContract.JuegoEntry.PREGUNTA_ID + "= ?" +
                    " and " + JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL + "= ?";
            selectionArgsPreguntas = new String[]{String.valueOf(1), String.valueOf(i_nivel + 1)}; //nivel siguiente primera pregunta

            cursorPreguntas = dbJuego.query(JuegoContract.JuegoEntry.TABLE_NAME_PREGUNTAS,
                    columnasPreguntas, selectionPreguntas, selectionArgsPreguntas,
                    null, null, null);

            if(cursorPreguntas!= null & cursorPreguntas.moveToFirst()) {
                //existe preguntas en el nivel siguiente
                pregunta = cursorPreguntas.getString(0);
                id_pregunta = cursorPreguntas.getInt(1);
                id_nivel = cursorPreguntas.getInt(2);

                cargarOpciones(id_pregunta, id_nivel);

            }else{
                //No existen preguntas en el nivel siguiente
                //se acaban las preguntas... volver a cargar las q contestó mal si es que existen
                if(cargarPreguntasIncorrectas()){
                    //existen preguntas incorrectas
                    //id_pregunta e id_nivel con nuevos valores
                    columnasPreguntas = new String[]{JuegoContract.JuegoEntry.PREGUNTA_TEXTO, JuegoContract.JuegoEntry.PREGUNTA_ID, JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL};
                    selectionPreguntas = JuegoContract.JuegoEntry.PREGUNTA_ID + "= ?" +
                            " and " + JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL + "= ?";
                    selectionArgsPreguntas = new String[]{String.valueOf(id_pregunta), String.valueOf(id_nivel)};

                    cursorPreguntas = dbJuego.query(JuegoContract.JuegoEntry.TABLE_NAME_PREGUNTAS,
                            columnasPreguntas, selectionPreguntas, selectionArgsPreguntas,
                            null, null, null);

                    if(cursorPreguntas!= null & cursorPreguntas.moveToFirst()) {

                        pregunta = cursorPreguntas.getString(0);

                        cargarOpciones(id_pregunta, id_nivel);
                        // id_pregunta = cursorPreguntas.getInt(1);
                        // id_nivel = cursorPreguntas.getInt(2);
                    }

                }else{
                    //no existen preguntas incorrectas

                }
            }


        }


    }

    private void cargarOpciones(int id_pregunta, int id_nivel) {

        opciones.clear();

        //Buscar opciones de la pregunta
        columnasOpciones = new String[]{JuegoContract.JuegoEntry.OPCIONES_ID, JuegoContract.JuegoEntry.OPCIONES_TEXTO, JuegoContract.JuegoEntry.OPCIONES_PUNTAJE};
        selectionPreguntas = JuegoContract.JuegoEntry.PREGUNTA_ID + "= ?" +
                " and " + JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL + "= ?";
        selectionArgsPreguntas = new String[]{String.valueOf(id_pregunta), String.valueOf(id_nivel)};

        cursorOpciones = dbJuego.query(JuegoContract.JuegoEntry.TABLE_NAME_OPCIONES,
                columnasOpciones, selectionPreguntas, selectionArgsPreguntas,
                null, null, null);

        if(cursorOpciones!= null & cursorOpciones.moveToFirst()){

            respuestaCorrecta = null;

            int cantidadOpciones = cursorOpciones.getCount();

            for(int j=0; j<cantidadOpciones; j++){

                opciones.add(cursorOpciones.getString(1));
                puntaje = cursorOpciones.getInt(2);

                if(puntaje == 1){
                    respuestaCorrecta = cursorOpciones.getString(1);
                    idRespuestaCorrecta = j;
                }

                cursorOpciones.moveToNext();

            }

            dbJuego.close();
            armarVistaDefinitiva();

        }
    }

    private void armarVistaDefinitiva() {

        listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, opciones);

        listAdapter.notifyDataSetChanged();

        setListAdapter(listAdapter);

        //armo la vista
        textView_pregunta.setText(pregunta);
        textView_puntos.setText(String.valueOf(puntaje_acumulado));

    }

    //Analiza la BD jugada, aquellas con puntaje 0 las vuelve a mostrar y si acerta les cambia el puntaje a 2 como para registrar
    //que fueron acertadas después de la primera vez
    //
    private boolean cargarPreguntasIncorrectas() {



        opciones.clear();

        columnasJugadaIncorrecta = new String[]{JuegoContract.JuegoEntry.PREGUNTA_ID, JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL};
        selectionJugadaIncorrecta = JuegoContract.JuegoEntry.OPCIONES_PUNTAJE + "= ?";
        selectionArgsJugadaIncorrecta = new String[]{String.valueOf(0)};

        cursorJugadaIncorrecta = dbJugada.query(JuegoContract.JuegoEntry.TABLE_NAME_JUGADA,
                columnasJugadaIncorrecta, selectionJugadaIncorrecta, selectionArgsJugadaIncorrecta,
                null, null, null);

        if(cursorJugadaIncorrecta!=null & cursorJugadaIncorrecta.moveToFirst()){
            //existen preguntas incorrectas
            int cantidad = cursorJugadaIncorrecta.getCount();
            //número aleatorio entre 0 y cantidad para seleccionar que pregunta elegir
            int numero = (int) (Math.random() * (cantidad - 1));

            cursorJugadaIncorrecta.moveToPosition(numero);
            id_pregunta = cursorJugadaIncorrecta.getInt(0);
            id_nivel = cursorJugadaIncorrecta.getInt(1);

            boolean_preguntas_incorrectas = true;

        } else {
            //ya están correctas todas las preguntas. Volver a empezar
            repasar();
            boolean_preguntas_incorrectas = false;
        }

        return boolean_preguntas_incorrectas;
    }

    private void repasar() {
        iniciarJuego();
    }




    @Override
        public void onAttach(Activity activity) {
            // TODO Auto-generated method stub
            super.onAttach(activity);
            // Inicializamos nuestra variable de referencia del tipo
            // onJuegoSalirListener junto con el valor del objeto
            // activity que debe ser una Activity que implemente esta interface
            try {
                mCallback = (onJuegoSalirListener) activity;

            } catch (ClassCastException e) {
                Log.d("ClassCastException",
                        "La Activity debe implementar esta Interface");
            }
        }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        if(opciones.get((int) id).equals(respuestaCorrecta)){
            l.getChildAt((int) id).setBackgroundColor(Color.GREEN);
            actualizarPuntaje(true);


        }else{
            l.getChildAt((int) id).setBackgroundColor(Color.RED);
            l.getChildAt(idRespuestaCorrecta).setBackgroundColor(Color.GREEN);
            actualizarPuntaje(false);

        }

        id_opcion = (int) (id + 1); //porque id arranca en 0

        listView = l;

        //Desactiva las opciones para que no puedan ser seleccionadas
        for (int i = 0; i < listView.getChildCount(); i++) {
            View listItem = listView.getChildAt(i);
        }

        //desactivar click en el listView
         listView.setEnabled(false);

        btn_siguiente.setEnabled(true);

    }

    private void actualizarPuntaje(Boolean respuesta) {

        int puntos = Integer.parseInt(textView_puntos.getText().toString());

        if(respuesta){
            puntaje_acumulado = puntos + Constants.PUNTAJE_JUEGO_CORRECTO;
            opcion_puntaje = 1;
        }else{
            opcion_puntaje = 0;
            puntaje_acumulado = puntos;
        }

        textView_puntos.setText(String.valueOf(puntaje_acumulado));

        //guardar puntaje acumulado en BD
        Jugada_DBHelper mJugada = new Jugada_DBHelper(getContext());
        SQLiteDatabase dbJugada = mJugada.getWritableDatabase();

        if(boolean_preguntas_incorrectas){
            //actualizar jugada
            ContentValues values = new ContentValues();
            values.put(JuegoContract.JuegoEntry.OPCIONES_PUNTAJE, opcion_puntaje);
            values.put(JuegoContract.JuegoEntry.OPCIONES_ID, id_opcion);
            values.put(JuegoContract.JuegoEntry.JUGADA_PUNTAJE_ACUMULADO, puntaje_acumulado);

            String whereClause = JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL + "= ?" +
                    " and " + JuegoContract.JuegoEntry.PREGUNTA_ID + "= ?";

            String[] whereArgs = {String.valueOf(id_nivel), String.valueOf(id_pregunta)};

            long mlong = dbJugada.update(JuegoContract.JuegoEntry.TABLE_NAME_JUGADA, values,whereClause, whereArgs);


        }else{

            //guardar jugada
            ContentValues values = new ContentValues();
            values.put(JuegoContract.JuegoEntry.PREGUNTA_ID_NIVEL, id_nivel);
            values.put(JuegoContract.JuegoEntry.PREGUNTA_ID, id_pregunta);
            values.put(JuegoContract.JuegoEntry.OPCIONES_ID, id_opcion);
            values.put(JuegoContract.JuegoEntry.OPCIONES_PUNTAJE, opcion_puntaje);
            values.put(JuegoContract.JuegoEntry.JUGADA_PUNTAJE_ACUMULADO, puntaje_acumulado);

            long mLong = dbJugada.insert(JuegoContract.JuegoEntry.TABLE_NAME_JUGADA, null, values);
        }


    }

    private void actualizarPuntajeAcumulado() {

        //buscar jugada
        Jugada_DBHelper mJugada = new Jugada_DBHelper(getContext());
        dbJugada = mJugada.getWritableDatabase();

        columnasJugada = new String[]{BaseColumns._ID, JuegoContract.JuegoEntry.JUGADA_PUNTAJE_ACUMULADO};

        selectionJugada = JuegoContract.JuegoEntry.PREGUNTA_ID;

        cursorJugada = dbJugada.query(JuegoContract.JuegoEntry.TABLE_NAME_JUGADA, columnasJugada, selectionJugada, null, null, null, null);

        int id_jugada = -1;

        if(cursorJugada!= null & cursorJugada.moveToLast()){
            id_jugada = cursorJugada.getInt(0);
        }

        //actualizar
        ContentValues values = new ContentValues();
        values.put(JuegoContract.JuegoEntry.JUGADA_PUNTAJE_ACUMULADO, puntaje_acumulado);

        String whereClause = JuegoContract.JuegoEntry._ID + "= ?";

        String[] whereArgs = {String.valueOf(id_jugada)};

        long mlong = dbJugada.update(JuegoContract.JuegoEntry.TABLE_NAME_JUGADA, values, whereClause, whereArgs);

    }



}
