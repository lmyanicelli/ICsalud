package com.luciayanicelli.icsalud.Activity_Configuracion;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.luciayanicelli.icsalud.R;
import com.luciayanicelli.icsalud.Services.Constants;
import com.luciayanicelli.icsalud.utils.SetearAlarma;

/**
 * Created by LuciaYanicelli on 16/3/2017.
 * ESTE FRAGMENT CONTIENE LOS VALORES A CONFIGURAR DE LOS HORARIOS DE LAS MEDICIONES
 * UTILIZA UN TIMEPICKER PARA SELECCIONAR EL HORARIO
 * ACTUALIZA EL VALOR SELECCIONADO EN EL SUMMARY
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public class SettingsFragment extends PreferenceFragment  implements SharedPreferences.OnSharedPreferenceChangeListener {

    //KEYS Horarios Recordatorios
    public static final String KEY_PREF_HORARIO_PESO = "horario_peso";
    public static final String KEY_PREF_HORARIO_PA_FC = "horario_pafc";
    public static final String KEY_PREF_HORARIO_SINTOMAS = "horario_sintomas";
    public static final String KEY_PREF_HORARIO_CONSEJO_SALUDABLE = "horario_consejo_saludable";
    private Context context;


    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        // Inicializamos nuestra variable de referencia del tipo
        // onJuegoSalirListener junto con el valor del objeto
        // activity que debe ser una Activity que implemente esta interface
        try {

        } catch (ClassCastException e) {
            Log.d("ClassCastException",
                    "La Activity debe implementar esta Interface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
           addPreferencesFromResource(R.xml.preferences);

        //ESTO ACTUALIZA EL VALOR EN EL SUMMARY
        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        bindPreferenceSummaryToValue(findPreference(Constants.KEY_PREF_HORARIO_PA_FC));
        bindPreferenceSummaryToValue(findPreference(Constants.KEY_PREF_HORARIO_PESO));
        bindPreferenceSummaryToValue(findPreference(Constants.KEY_PREF_HORARIO_SINTOMAS));
        bindPreferenceSummaryToValue(findPreference(Constants.KEY_PREF_HORARIO_CONSEJO_SALUDABLE));

        bindPreferenceSummaryToValue(findPreference(Constants.KEY_PREF_FRECUENCIA_CONSEJO_SALUDABLE));

        this.context = getActivity().getApplicationContext();

    }


    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.

                final ListPreference listPreference2 = (ListPreference) preference;
                int index = listPreference2.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
             preference.setSummary(
                        index >= 0
                                ? listPreference2.getEntries()[index]
                                : null);

                listPreference2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        return false;

                    }
                });

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else if (preference instanceof DialogPreferenceSelectTime){

                preference.setSummary(stringValue);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };


    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


    @Override
    public void onResume() {
        super.onResume();
        // Registrar escucha
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Eliminar registro de la escucha
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.KEY_PREF_HORARIO_PA_FC)
                | key.equals(Constants.KEY_PREF_HORARIO_PESO)
                | key.equals(Constants.KEY_PREF_HORARIO_SINTOMAS)
                | key.equals(Constants.KEY_PREF_HORARIO_CONSEJO_SALUDABLE)) {
            Preference pref = findPreference(key);
            // Set summary to be the user-description for the selected value
            pref.setSummary(sharedPreferences.getString(key, ""));

            String parametro = getParametro(key);

            //04/06/18
            SetearAlarma setearAlarma = new SetearAlarma(context, parametro);
            setearAlarma.execute();

        }else if(key.equalsIgnoreCase(Constants.KEY_PREF_FRECUENCIA_CONSEJO_SALUDABLE)){
            Preference pref = findPreference(key);
            // Set summary to be the user-description for the selected value
            pref.setSummary(sharedPreferences.getString(key, ""));

            SetearAlarma setearAlarma = new SetearAlarma(context, Constants.CONSEJO_SALUDABLE);
            setearAlarma.execute();

        }
    }

    private String getParametro(String key) {

        switch (key){
            case Constants.KEY_PREF_HORARIO_PESO:
                return Constants.PARAMETRO_PESO;

            case Constants.KEY_PREF_HORARIO_PA_FC:
                return Constants.PARAMETRO_PAFC;

            case Constants.KEY_PREF_HORARIO_SINTOMAS:
                return Constants.PARAMETRO_SINTOMAS;

            case Constants.KEY_PREF_HORARIO_CONSEJO_SALUDABLE:
                return Constants.CONSEJO_SALUDABLE;

            default:
                return  null;

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}