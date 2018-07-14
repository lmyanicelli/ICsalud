package com.luciayanicelli.icsalud;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.DataBase.AutodiagnosticoContract;
import com.luciayanicelli.icsalud.DataBase.Autodiagnostico_DBHelper;
import com.luciayanicelli.icsalud.DataBase.RecordatoriosContract;
import com.luciayanicelli.icsalud.DataBase.RecordatoriosDBHelper;



/*
*PERMITE VISUALIZAR EL CONSEJO SALUDABLE
* SELECCIONAR LEIDO - LO ELIMINA DEL LISTADO DE RECORDATORIOS PENDIENTES EN MAIN ACTIVITY
* SELECCIONAR CANCELAR - SÓLO REGRESA AL MAIN ACTIVITY
*/

public class Activity_ViewConsejoSaludable extends AppCompatActivity implements FragmentConsejoSaludable.onClickListener {

 /*   private TextView titulo, consejo_saludable;
    private Button btn_cancel, btn_leido;

    private String text_consejo_saludable;
    private int id;
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_consejosaludable);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //23/03 intentando back press button in action bar - en Manifest
        /* <activity android:name=".Activity_Configuracion.Activity_configuracion"
            android:parentActivityName=".MainActivity">
        <!-- Parent activity meta-data to support 4.0 and lower -->
        <meta-data
            android:name="android.support.PARENT_ACTIVITY"
            android:value=".MainActivity" />
            </activity>
            */
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        // Fragment que muestra el contenido de la nota
        FragmentConsejoSaludable contFragment = new FragmentConsejoSaludable();
      //  Bundle args = new Bundle();

        // Establecemos el id de la nota seleccionada
      //  args.putInt(FragmentConsejoSaludable.ID, id);
      //  contFragment.setArguments(args);

        getFragmentManager().beginTransaction()
                .add(R.id.content_frame_consejo_saludable, contFragment)
                .commit();


    }



    @Override
    public void onClickLeido(String fecha) {

        Configuraciones configuraciones = new Configuraciones(getApplicationContext());
        int contador = configuraciones.getContadorConsejosSaludablesLeidos();
        configuraciones.setContadorConsejosSaludablesLeidos(contador + 1);

        eliminarRecordatorioSaludable(fecha);

        volverMainActivity();
    }

    @Override
    public void onClickCancel() {

        volverMainActivity();
    }


    private void volverMainActivity() {
        Intent mIntent = new Intent();
        mIntent.setClass(getApplicationContext(), MainActivity.class);
        startActivity(mIntent);

        this.finish();
    }

    //Elimina el recordatorio saludable de la BD recordatorios y modifica el estado en AutodiagnosticoDbHelper
    private void eliminarRecordatorioSaludable(String fecha) {
        RecordatoriosDBHelper dbHelper = new RecordatoriosDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = RecordatoriosContract.RecordatoriosEntry.FECHA + "= ?"
                + " and " + RecordatoriosContract.RecordatoriosEntry.TIPO + "= ?";

        String[] args = {fecha, RecordatoriosContract.RecordatoriosEntry.TIPO_RECORDATORIO};

        db.delete(RecordatoriosContract.RecordatoriosEntry.TABLE_NAME, whereClause, args);

        //modificar estado en Autodiagnostico
        Autodiagnostico_DBHelper dbAutoHelper = new Autodiagnostico_DBHelper(getApplicationContext());
        SQLiteDatabase dbAuto = dbAutoHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO, AutodiagnosticoContract.AutodiagnosticoEntry.ESTADO_ENVIADA);

        String whereClauseAuto = AutodiagnosticoContract.AutodiagnosticoEntry.CONSEJO_SALUDABLE_DATE + "= ?";

        String[] whereArgs = new String[] {fecha};

        dbAuto.update(AutodiagnosticoContract.AutodiagnosticoEntry.TABLE_NAME_CONSEJO_SALUDABLE, values, whereClauseAuto, whereArgs);

    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();

    }


}
