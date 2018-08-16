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


public class FragmentServicioTecnico extends Fragment {

    private TextView tv_titulo, tv_msj;
    private EditText et_phone;
    private Button btn_cancel, btn_solicitar_ayuda;

    onClickListener mCallback;
    private String fecha;

    // Interface que la Activity contenedora debe implementar
    // para poder tener comunicación
    public interface onClickListener {


            void onClickCancel(); //volver a la pantalla principal
            void onClickHelp(String phone);
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
        return inflater.inflate(R.layout.activity_servicio_tecnico, container, false);
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        actualizarContenido();

    }

    public void actualizarContenido() {

        tv_titulo = (TextView) getActivity().findViewById(R.id.textView_titulo);
        tv_msj = (TextView) getActivity().findViewById(R.id.textView_msj_st);
        et_phone = (EditText) getActivity().findViewById(R.id.editText_celular);

        btn_cancel = (Button) getActivity().findViewById(R.id.btn_cancel);
        btn_solicitar_ayuda = (Button) getActivity().findViewById(R.id.btn_solicitar_st);

        FechaActual fechaActual = new FechaActual();
        fechaActual.execute();

        try {
            fecha = fechaActual.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallback.onClickCancel();
            }
        });

        btn_solicitar_ayuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //enviar mail?
                if(validate()){
                    mCallback.onClickHelp(et_phone.getText().toString()); //
                    }



            }
        });

    }



    private boolean validate() {

        boolean valid=false;

        String phone = et_phone.getText().toString();

        if (phone.isEmpty() || phone.length() !=10) {
            et_phone.setError("ingrese su número de celular con la característica sin 0 ni 15. Ej: 3816123456");
            et_phone.requestFocus();

        } else {
            et_phone.setError(null);
            valid = true;
        }

        return valid;
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
