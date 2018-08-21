package com.luciayanicelli.icsalud;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.Alertas_DBHelper;
import com.luciayanicelli.icsalud.DataBase.RecordatoriosContract;
import com.luciayanicelli.icsalud.DataBase.RecordatoriosDBHelper;



/*
SE GENERA DIARIAMENTE - VER DE PASAR A SEMANALMENTE

* CONSULTA AL USUARIO SI ABANDONÓ ALGÚN MEDICAMENTO ULTIMAMENTE
* EN CASO AFIRMATIVO SOLICITA QUE INDIQUE QUE MEDICAMENTO
* EN CASO NEGATIVO REGRESA A LA PANTALLA PRINCIPAL
*
*/

public class Activity_ViewConsultaMedicamentos extends AppCompatActivity implements FragmentConsultaMedicamentos.onClickListener {

 /*   private TextView titulo, consejo_saludable;
    private Button btn_cancel, btn_leido;

    private String text_consejo_saludable;
    private int id;
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_consulta_medicamentos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        // Fragment que muestra el contenido
        FragmentConsultaMedicamentos contFragment = new FragmentConsultaMedicamentos();

        getFragmentManager().beginTransaction()
                .add(R.id.content_frame_consulta_medicamentos, contFragment)
                .commit();


    }

    private void volverMainActivity() {
        Intent mIntent = new Intent();
        mIntent.setClass(getApplicationContext(), MainActivity.class);
        startActivity(mIntent);

        this.finish();
    }

    @Override
    public void onClickNo() {
        eliminarRecordatorioBD();

        volverMainActivity();
    }

    private void eliminarRecordatorioBD() {

        //Elimina el recordatorio de la BD recordatorios

            RecordatoriosDBHelper dbHelper = new RecordatoriosDBHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String whereClause = RecordatoriosContract.RecordatoriosEntry.TIPO + "= ?";

            String[] args = {RecordatoriosContract.RecordatoriosEntry.TIPO_MEDICAMENTOS};

            db.delete(RecordatoriosContract.RecordatoriosEntry.TABLE_NAME, whereClause, args);


    }

    @Override
    public void onClickOk(String text_medicamento_abandono, String fecha) {

        String descripcion = getResources().getString(R.string.alerta_medicamento) + " " + text_medicamento_abandono;

        //guardar Alarma en BD
        Alertas_DBHelper mDBHelper = new Alertas_DBHelper(getApplicationContext());
        SQLiteDatabase dbAlerta = mDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(AlertasContract.AlertasEntry.FECHA, fecha);
     //14/08/18   values.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_VERDE);
        values.put(AlertasContract.AlertasEntry.TIPO, AlertasContract.AlertasEntry.ALERTA_TIPO_ROJA);
        values.put(AlertasContract.AlertasEntry.PARAMETRO, AlertasContract.AlertasEntry.ALERTA_PARAMETRO_SOS);
        values.put(AlertasContract.AlertasEntry.DESCRIPCION, descripcion);
        values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);

        try{
            long controlInsert = dbAlerta.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);

        }catch(Exception e){
            e.printStackTrace();
        }

        eliminarRecordatorioBD();

        volverMainActivity();

    }

 /*   @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();

    }
    */

}
