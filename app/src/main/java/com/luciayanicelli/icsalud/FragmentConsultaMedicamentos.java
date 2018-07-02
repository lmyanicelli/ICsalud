package com.luciayanicelli.icsalud;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.luciayanicelli.icsalud.utils.FechaActual;

import java.util.concurrent.ExecutionException;

/**
 * Created by LuciaYanicelli on 28/9/2017.
 *
 *
 * //CONTINUAR AQUI
 */


public class FragmentConsultaMedicamentos extends Fragment {

    private TextView textView_pregunta, textView_medicamento;
    private EditText editText_medicamento;
    private Button btn_si, btn_no, btn_ok;

    private String text_medicamento_abandono;
    private String fecha;

    onClickListener mCallback;

    // Interface que la Activity contenedora debe implementar
    // para poder tener comunicación
    public interface onClickListener {


            void onClickNo(); //volver a la pantalla principal
            void onClickOk(String text_medicamento_abandono, String fecha);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        // Comprobamos si se recupera de un estado anterior
    /*    if (savedInstanceState != null) {
            _id = savedInstanceState.getInt(ID);
        }
*/
        return inflater.inflate(R.layout.content_view_consulta_medicamentos, container, false);
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        actualizarContenido();

    }

    public void actualizarContenido() {

        textView_pregunta = (TextView) getActivity().findViewById(R.id.textView_pregunta);
        textView_medicamento = (TextView) getActivity().findViewById(R.id.textView_medicamento);
        editText_medicamento = (EditText) getActivity().findViewById(R.id.editText_medicamento);

        btn_si = (Button) getActivity().findViewById(R.id.btn_si);
        btn_no = (Button) getActivity().findViewById(R.id.btn_no);
        btn_ok = (Button) getActivity().findViewById(R.id.btn_ok);


        FechaActual fechaActual = new FechaActual();
        fechaActual.execute();

        try {
            fecha = fechaActual.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallback.onClickNo();
            }
        });

        btn_si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //deshabilito el boton no
                btn_no.setEnabled(false);

                textView_medicamento.setVisibility(View.VISIBLE);
                editText_medicamento.setVisibility(View.VISIBLE);
                btn_ok.setVisibility(View.VISIBLE);




                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            text_medicamento_abandono = editText_medicamento.getText().toString();

                            if(text_medicamento_abandono.isEmpty() || text_medicamento_abandono.length() < 2) {
                                editText_medicamento.setError("Por favor indique qué medicamento dejó de tomar últimamente");
                                editText_medicamento.requestFocus();

                            } else {
                                mCallback.onClickOk(text_medicamento_abandono, fecha);
                            }
                        }
                    });






            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        // Guardamos el estado de la posicion del elemento
        // que estábamos consultando
       // outState.putInt(ID, _id);
    }



    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        // Inicializamos nuestra variable de referencia
        try {
            //  mCallback = (onFabClickListener) activity;
            mCallback = (onClickListener) activity;

        } catch (ClassCastException e) {
            Log.d("ClassCastException",
                    "La Activity debe implementar esta Interface");
        }
    }

    @Override
    public void onAttach(Context context) {
        // TODO Auto-generated method stub
        super.onAttach(context);
        // Inicializamos nuestra variable de referencia
        try {
            //  mCallback = (onFabClickListener) activity;
            mCallback = (onClickListener) context;

        } catch (ClassCastException e) {
            Log.d("ClassCastException",
                    "La Activity debe implementar esta Interface");
        }
    }

}
