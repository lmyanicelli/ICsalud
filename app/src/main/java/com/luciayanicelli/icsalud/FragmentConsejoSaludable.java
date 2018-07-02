package com.luciayanicelli.icsalud;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.luciayanicelli.icsalud.Services.Constants;


public class FragmentConsejoSaludable extends Fragment {


    private TextView titulo, consejo_saludable;
    private Button btn_cancel, btn_leido;

    private String text_consejo_saludable;
    private String fecha;

    onClickListener mCallback;

    // Interface que la Activity contenedora debe implementar
    // para poder tener comunicación
    public interface onClickListener {
         void onClickLeido(String fecha);

         void onClickCancel();

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.content_view_consejosaludable, container, false);
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        actualizarContenido();

    }

    public void actualizarContenido() {

        Intent intent = getActivity().getIntent();
        text_consejo_saludable = intent.getStringExtra(Constants.CONSEJO_SALUDABLE);
        fecha = intent.getStringExtra(Constants.FECHA_CONSEJO_SALUDABLE);

        titulo = (TextView) getActivity().findViewById(R.id.titulo);
        consejo_saludable = (TextView) getActivity().findViewById(R.id.consejo_saludable);

        titulo.setText(Constants.CONSEJO_SALUDABLE);
        consejo_saludable.setText(text_consejo_saludable);


        btn_cancel = (Button) getActivity().findViewById(R.id.btn_cancel);
        btn_leido = (Button) getActivity().findViewById(R.id.btn_leido);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallback.onClickCancel();
            }
        });

        btn_leido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallback.onClickLeido(fecha);
            }
        });

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

    }


//deprecated in API 23
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;

        try {
            // Inicializamos nuestra variable de referencia
            mCallback = (onClickListener) activity;

        } catch (ClassCastException e) {
            Log.d("ClassCastException",
                    "La Activity debe implementar esta Interface");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (onClickListener) context;

        } catch (ClassCastException e) {
            Log.d("ClassCastException",
                    "La Activity debe implementar esta Interface");
        }
    }




}