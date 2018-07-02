package com.luciayanicelli.icsalud.Activity_profesionales;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.luciayanicelli.icsalud.R;

public class FragmentNewContact extends android.support.v4.app.Fragment {

    private TextView textView_titulo;
    private EditText editText_firstName, editText_lastName, editText_email, editText_mobileNumber;

    private String firstName, lastName, email, mobileNumber;

    private Button btn_cancel, btn_crear;


    onClickListener mCallback;

    // Interface que la Activity contenedora debe implementar
    // para poder tener comunicación
    public interface onClickListener {
        void onClickCrear(String firstName, String lastName, String email, String mobileNumber);

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
       // return inflater.inflate(R.layout.content_view_consejosaludable, container, false);
        return inflater.inflate(R.layout.dialog_new_contact, container, false);
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        armarVista();

        //actualizarContenido();

    }

    private void armarVista() {

        //Armo la vista
        textView_titulo = (TextView) getActivity().findViewById(R.id.textView_titulo);
        textView_titulo.setText(getResources().getText(R.string.ingrese_contacto));

        editText_firstName = (EditText) getActivity().findViewById(R.id.editText_firstName);
        editText_lastName = (EditText) getActivity().findViewById(R.id.editText_lastName);
        editText_email = (EditText) getActivity().findViewById(R.id.editText_email);
        editText_mobileNumber = (EditText) getActivity().findViewById(R.id.editText_mobileNumber);

        firstName = editText_firstName.getText().toString();
        lastName = editText_lastName.getText().toString();
        email = editText_email.getText().toString();
        mobileNumber = editText_mobileNumber.getText().toString();

        btn_cancel = (Button) getActivity().findViewById(R.id.btn_cancel);
        btn_crear = (Button) getActivity().findViewById(R.id.btn_crear);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //borrar datos
                editText_firstName.setText("");
                editText_lastName.setText("");
                editText_email.setText("");
                editText_mobileNumber.setText("");

                firstName="";
                lastName="";
                email="";
                mobileNumber = "";
            }
        });

        btn_crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validate()){
                    mCallback.onClickCrear(firstName, lastName, email, mobileNumber);
                }


            }
        });

    }

    public boolean validate() {
        boolean valid = true;

        firstName = editText_firstName.getText().toString();
        lastName = editText_lastName.getText().toString();
        email = editText_email.getText().toString();
        mobileNumber = editText_mobileNumber.getText().toString();


        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editText_email.setError("ingrese una dirección de correo válida");
            editText_email.requestFocus();
            valid = false;
        } else {
            editText_email.setError(null);
        }


        if (lastName.isEmpty() || lastName.length() < 3) {
            editText_lastName.setError("al menos 3 caracteres");
            editText_lastName.requestFocus();
            valid = false;
        } else {
            editText_lastName.setError(null);
        }

        if (firstName.isEmpty() || firstName.length() < 3) {
            editText_firstName.setError("al menos 3 caracteres");
            editText_firstName.requestFocus();
            valid = false;
        } else {
            editText_firstName.setError(null);
        }

        if (mobileNumber.isEmpty() || mobileNumber.length() != 10) {
            editText_mobileNumber.setError("debe ingresar el número de celular con la característica sin '0' ni '15'. Ej: 3816123456");
            editText_mobileNumber.requestFocus();
            valid = false;
        } else {
            editText_mobileNumber.setError(null);
        }

        return valid;
    }

  /*  public void actualizarContenido() {

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
    */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

    }



    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        // Inicializamos nuestra variable de referencia
        try {
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
            mCallback = (onClickListener) context;

        } catch (ClassCastException e) {
            Log.d("ClassCastException",
                    "La Activity debe implementar esta Interface");
        }
    }




}