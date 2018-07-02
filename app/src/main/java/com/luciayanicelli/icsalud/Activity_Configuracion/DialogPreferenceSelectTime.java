package com.luciayanicelli.icsalud.Activity_Configuracion;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.luciayanicelli.icsalud.R;


/*
* Preferencia personalizada que muestra un TimePicker para obtener un horario seleccionado por el usuario
* */

public class DialogPreferenceSelectTime extends DialogPreference {

    private TimePicker timePicker1;
    private TextView textView;

    private int hour;
    private int minute;

    static final int TIME_DIALOG_ID = 999;

    private int lastHour;
    private int lastMinute;

    private String time;
    private String minutes;


    //Obtiene la hora del String time HH:MM
    public static int getHour(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    //Obtiene los minutos del String time HH:MM
    public static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }


    //Constructor
    public DialogPreferenceSelectTime(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(true); //Importante!! para que los datos se guarden!
        setDialogLayoutResource(R.layout.dialog_select_time); //Carga el layout correspondiente

    }

    //Muestra la hora actual
    // display current time -- CAMBIAR POR PONER LA HORA ESTABLECIDA
  /*  public void setCurrentTimeOnView() {

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        // set current time into timepicker
        timePicker1.setCurrentHour(hour);
        timePicker1.setCurrentMinute(minute);

    }
*/


     protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                // set time picker as current time
                return new TimePickerDialog(getContext(), timePickerListener, hour, minute,false);

        }
        return null;
    }

    //carga la hora seleccionada
    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    hour = selectedHour;
                    minute = selectedMinute;

                    // set current time into textview
         /*           tvDisplayTime.setText(new StringBuilder().append(pad(hour))
                            .append(":").append(pad(minute)));
*/
                    // set current time into timepicker
                    timePicker1.setCurrentHour(hour);
                    timePicker1.setCurrentMinute(minute);


                }
            };

            //solo para setCurrentTime
    /*
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
    */


    //El sistema llama esta función cuando el diálogo se cierra, ya se con una aprobación positiva del usuario o negativa
    @Override
    protected void onDialogClosed(boolean positiveResult) {

        super.onDialogClosed(positiveResult);

        //Hace persistir el valor seleccionado por el usuario
        if (positiveResult) {
            lastHour=timePicker1.getCurrentHour();
            lastMinute=timePicker1.getCurrentMinute();

            if (lastMinute >= 10) {
                minutes = String.valueOf(lastMinute);

            } else {
                minutes=  "0" + String.valueOf(lastMinute);
            }

            String time=String.valueOf(lastHour)+":"+ minutes;

            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    //Creo q no hace falta
    @Override
    public void onClick(DialogInterface dialog, int which) {

     /*   if (which == DialogInterface.BUTTON_POSITIVE) {
            String value = mEditTextPassword.getText().toString();
            callChangeListener(value);
        }
        */
        super.onClick(dialog, which);
    }


    //Arma la vista en base a los elementos del layout
    @Override
    protected void onBindDialogView(View view) {

        //Armo la vista
        textView = (TextView) view.findViewById(R.id.textView);
        textView.setText("Seleccione el horario en el que sonará el recordatorio para la medición:");

        timePicker1 = (TimePicker) view.findViewById(R.id.timePicker1);

        timePicker1.setCurrentHour(lastHour);
        timePicker1.setCurrentMinute(lastMinute);

    //    setCurrentTimeOnView();


        super.onBindDialogView(view);
    }


    //Para inicializar el valor por defecto
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time=null;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString("08:00");

            }
            else {
                time=this.getPersistedString((String) defaultValue);
            }
        }
        else {
            time=defaultValue.toString();
            persistString(time);
        }

        lastHour=getHour(time);
        lastMinute=getMinute(time);
    }


    //Para obtener el valor por defecto
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        //return a.getInteger(index, DEFAULT_VALUE);
        return a.getString(index);
    }


    //22/03 Cómo guardar y restaurar el estado de Preferencias
    private static class SavedState extends BaseSavedState {
        // Member that holds the setting's value
        // Change this data type to match the type saved by your Preference
        //int value;
        String value;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            // Get the current preference's value
            //value = source.readInt();  // Change this to read the appropriate data type
            value = source.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's value
            //dest.writeInt(value);  // Change this to write the appropriate data type
            dest.writeString(value);
        }

        // Standard creator object using an instance of this class
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            // No need to save instance state since it's persistent,
            // use superclass state
            return superState;
        }

        // Create instance of custom BaseSavedState
        final SavedState myState = new SavedState(superState);
        // Set the state's value with the class member that holds current
        // setting value
        //22/03
    //    String time=String.valueOf(lastHour)+":"+String.valueOf(lastMinute);
        String time=String.valueOf(lastHour)+":"+ minutes;

        myState.value = time;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Set this Preference's widget to reflect the restored state
      //  mNumberPicker.setValue(myState.value);
    }



}