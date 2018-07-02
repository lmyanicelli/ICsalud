package com.luciayanicelli.icsalud.utils;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.luciayanicelli.icsalud.DataBase.RecordatoriosContract;
import com.luciayanicelli.icsalud.DataBase.RecordatoriosDBHelper;
import com.luciayanicelli.icsalud.R;

import java.util.ArrayList;

/*LIST FRAGMENT*/

public class FragmentList extends ListFragment {

        onRecordatorioSelectedListener mCallback;

    private ListAdapter listAdapter;


    // Interfaz que la Activity contenedora debe implementar
        // para poder tener comunicación
        public interface onRecordatorioSelectedListener {
            void onRecordatorioSelected(int id);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);

            armarVista();


        }

    private void armarVista() {
        // Establecemos el Adapter cuando se crea el Fragment
        final String[] columnas = new String[]{
                RecordatoriosContract.RecordatoriosEntry._ID,//0
                RecordatoriosContract.RecordatoriosEntry.RECORDATORIO,//1  //descripción del recordatorio
                RecordatoriosContract.RecordatoriosEntry.TIPO,//2  //tipo "consejo saludable" o "recuerde monitorearse" o "medicamentos"
                RecordatoriosContract.RecordatoriosEntry.FECHA_HORA //3

        };

        Cursor mCursor;

        //Busco en la BD si existen registros
        RecordatoriosDBHelper mdbHelper = new RecordatoriosDBHelper(getContext());
        SQLiteDatabase dbAuto = mdbHelper.getWritableDatabase();

        String orderBy = RecordatoriosContract.RecordatoriosEntry._ID + " DESC";

        mCursor = dbAuto.query(true, RecordatoriosContract.RecordatoriosEntry.TABLE_NAME,
                columnas, null,null,null,null, orderBy,null);

        ArrayList<Lista_entrada> datos = new ArrayList<Lista_entrada>();

        if(mCursor == null | !mCursor.moveToFirst()){
            datos.add(new Lista_entrada("-1", getResources().getString(R.string.sin_recordatorios), getResources().getString(R.string.sin_recordatorios)));

        } else {

            do {
                    datos.add(new Lista_entrada(mCursor.getString(0), mCursor.getString(1), mCursor.getString(2)));
                } while (mCursor.moveToNext());
        }

        listAdapter = new Lista_adaptador(getContext().getApplicationContext(), R.layout.entrada, datos) {
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {

                    TextView texto_inferior_entrada = view.findViewById(R.id.textView_inferior);
                    if (texto_inferior_entrada != null)
                        texto_inferior_entrada.setText(((Lista_entrada) entrada).get_texto()); //acortar a x cantidad de palabras

                    TextView texto_superior_entrada = view.findViewById(R.id.textView_superior);
                    ImageView imagen_entrada = view.findViewById(R.id.imageView_imagen);

                    //Analizo el tipo de recordatorio y en base a eso coloco título e imagen
                    if(((Lista_entrada) entrada).get_tipo().equalsIgnoreCase(RecordatoriosContract.RecordatoriosEntry.TIPO_RECORDATORIO)){

                        if (texto_superior_entrada != null)
                            texto_superior_entrada.setText(RecordatoriosContract.RecordatoriosEntry.TIPO_RECORDATORIO);

                        if (imagen_entrada != null) {
                            imagen_entrada.setImageResource(R.drawable.ic_recordatorio_saludable);

                        }


                    }else if(((Lista_entrada) entrada).get_tipo().equalsIgnoreCase(RecordatoriosContract.RecordatoriosEntry.TIPO_ACCION)){
                        if (texto_superior_entrada != null)
                            texto_superior_entrada.setText(RecordatoriosContract.RecordatoriosEntry.TIPO_ACCION);

                        if (imagen_entrada != null) {
                            imagen_entrada.setImageResource(R.drawable.ic_recordatorio_accion);

                        }

                    }else if(((Lista_entrada) entrada).get_tipo().equalsIgnoreCase(RecordatoriosContract.RecordatoriosEntry.TIPO_MEDICAMENTOS)){

                        if (texto_superior_entrada != null)
                            texto_superior_entrada.setText(RecordatoriosContract.RecordatoriosEntry.TIPO_MEDICAMENTOS);

                        if (imagen_entrada != null) {
                            imagen_entrada.setImageResource(R.drawable.ic_medicamentos);
                        }

                    }else if(((Lista_entrada) entrada).get_tipo().equalsIgnoreCase(RecordatoriosContract.RecordatoriosEntry.TIPO_ENCUESTAS)){

                        if (texto_superior_entrada != null)
                            texto_superior_entrada.setText(RecordatoriosContract.RecordatoriosEntry.TIPO_ENCUESTAS);

                        if (imagen_entrada != null) {
                            imagen_entrada.setImageResource(R.drawable.ic_encuestas);
                        }

                    }else{
                        //NO HAY RECORDATORIOS
                        if (texto_superior_entrada != null)
                            texto_superior_entrada.setText("BIENVENIDO");

                        if (imagen_entrada != null) {
                          //  imagen_entrada.setImageResource(R.drawable.ic_logo);
                           // imagen_entrada.setImageResource(R.mipmap.ic_logo_sin_fondo);
                            imagen_entrada.setImageResource(R.drawable.ic_ic);
                        }

                    }

                }

            }
        };

        setListAdapter(listAdapter);

        //06/03/18
        dbAuto.close();

    }

    @Override
        public void onAttach(Activity activity) {
            // TODO Auto-generated method stub
            super.onAttach(activity);
            // Inicializamos nuestra variable de referencia del tipo
            // onRecordatorioSelectedListener junto con el valor del objeto
            // activity que debe ser una Activity que implemente esta interface
            try {
                mCallback = (onRecordatorioSelectedListener) activity;

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

            Lista_entrada elegido = (Lista_entrada) l.getItemAtPosition(position);
            int _id = Integer.valueOf(elegido.get_id());


            mCallback.onRecordatorioSelected(_id);
        }

    @Override
    public void onResume() {
        super.onResume();
        armarVista();
    }
}
