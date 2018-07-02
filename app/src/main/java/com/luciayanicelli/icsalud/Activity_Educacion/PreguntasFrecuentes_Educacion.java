package com.luciayanicelli.icsalud.Activity_Educacion;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.DataBase.Educacion_PreguntasFrecuentesContract;
import com.luciayanicelli.icsalud.DataBase.Educacion_PreguntasFrecuentesDBHelper;
import com.luciayanicelli.icsalud.R;
import com.luciayanicelli.icsalud.utils.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


//quizás deba sacar implments updateable
public class PreguntasFrecuentes_Educacion extends Fragment {


    //Base de datos
    private Cursor cursor;


    final String[] columnas = new String[] {Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry.PREGUNTA,
            Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry.RESPUESTA};

    private SQLiteDatabase db;

    private View view;

    private String[] camposDB;


    //26/07 ExpandableListView
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private int position;
    private String name;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
           view = inflater.inflate(R.layout.preguntas_frecuentes_educacion, container, false);
           return view;
    }

    //FUNCIONA 22/07
    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        //26/07 - ExpandableListView

        // get the listview
        expListView = (ExpandableListView) getView().findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {

                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                Configuraciones configuraciones = new Configuraciones(getContext());
                int contador = configuraciones.getContadorPreguntasFrecuentesLeidas();
                configuraciones.setContadorPreguntasFrecuentesLeidas(contador + 1);

            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub

                return false;
            }
        });

    }

    /*
      * Preparing the list data
      */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        //Accedo a la BD
        Context mContext = getContext();

        Educacion_PreguntasFrecuentesDBHelper dbHelper = new Educacion_PreguntasFrecuentesDBHelper(mContext);
        db = dbHelper.getWritableDatabase();

        camposDB = new String[]{Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry._ID,
                Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry.PREGUNTA,
                Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry.RESPUESTA};

        cursor = db.query(true, Educacion_PreguntasFrecuentesContract.PreguntasFrecuentesEntry.TABLE_NAME, camposDB, null, null,null,null,null,null); //carga en el cursor toda la BD


        //26/07

        if (cursor != null){
            cursor.moveToFirst();

            //mientras el cursor esté en una posición distinta a la siguiente a la última fila
            while (!cursor.isAfterLast()){
                // Adding child data
                listDataHeader.add(cursor.getString(1)); //cargo la pregunta

                // Adding child data
                position = cursor.getInt(0) - 1;
                name = String.valueOf(position); //el nombre del array será el Id de la fila de la BD menos 1 ya que la BD empieza en 1 y los array en 0
                List<String> name = new ArrayList<String>();
                name.add(cursor.getString(2)); //cargo la respuesta correspondiente

                listDataChild.put(listDataHeader.get(position), name); // asocia la pregunta con la respuesta

                //avanza el cursor
                cursor.moveToNext();

            }
        }

    }

}
