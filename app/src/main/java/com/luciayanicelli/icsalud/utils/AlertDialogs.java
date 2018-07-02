package com.luciayanicelli.icsalud.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.luciayanicelli.icsalud.R;

/*ALERT DIALOG

EN LA ACTIVIDAD DONDE SE UTILIZARÁ-- implements AlertDialogs.NoticeDialogListener
*
* CREA UN ALERT DIALOG EN DONDE SE PASAN LOS SIGUIENTES PARÁMETROS:
*
* @param msj : String mensaje de la alerta
* @param name : String nombre que identifique a la alerta, para poder instanciar distintas alertas
* @param positiveButton : String texto del botón para aceptar. Si no se pasa un texto, el valor por defecto es ACEPTAR
* @param negativeButton : String texto del botón para cancelar. Si no se pasa un texto, el valor por defecto es CANCELAR
*
*
* AlertDialogs alertDialogDelete = new AlertDialogs();
        alertDialogDelete.setMsj(getResources().getString(R.string.sos_llamar));
        alertDialogDelete.setName("msj");

        //15/03
        alertDialogDelete.setPositiveButton("LLAMAR");
        alertDialogDelete.setNegativeButton("CANCELAR");

        DialogFragment alertSOS = alertDialogDelete;
        alertSOS.show(getSupportFragmentManager(), "alertSOS");



      // EN LA ACTIVIDAD QUE UTILIZA EL ALERTDIALOG RESPONDER A LAS ELECCIONES DE LOS BOTONES CON:

      @Override
    public void onDialogPositiveClick(DialogFragment dialog, String name) {
        Toast.makeText(getApplicationContext(), "positivo", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, String name) {
        Toast.makeText(getApplicationContext(), "negativo", Toast.LENGTH_SHORT).show();

    }
* */

public class AlertDialogs extends DialogFragment {

    private DialogFragment dialogFragment;

    private String name, msj, title;

    private String positiveButton, negativeButton;



    public String getName() {
        return name;
    }

    public String getMsj() { return msj; }

    public void setMsj(@NonNull String msj) {
        this.msj = msj;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {   return title;    }

    public void setTitle(String title) {     this.title = title;    }



    public void setPositiveButton(String positiveButton) {
        this.positiveButton = positiveButton;
    }

    public void setNegativeButton(String negativeButton) {
        this.negativeButton = negativeButton;
    }




    // Interfaz que debe implementar la actividad que instancia al AlertDialog
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String name);
        public void onDialogNegativeClick(DialogFragment dialog, String name);
    }

    // Instancia de la interfaz para ofrecer eventos de acción
    NoticeDialogListener mListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }



        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Construcción del AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                 //15/03
            if (positiveButton == null) {
                positiveButton = getResources().getString(R.string.aceptar);
            }

            if (negativeButton == null){
                negativeButton = getResources().getString(R.string.cancelar);
            }

            builder.setMessage(msj)
                    .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDialogPositiveClick(dialogFragment, name);
                        }
                    })
                    .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDialogNegativeClick(dialogFragment, name);
                        }
                    });

            if(title != null){
                builder.setTitle(title);
            }

            return builder.create();
        }

}
