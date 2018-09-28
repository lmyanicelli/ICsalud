package com.luciayanicelli.icsalud;

import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.RecordatoriosContract;
import com.luciayanicelli.icsalud.DataBase.RecordatoriosDBHelper;
import com.luciayanicelli.icsalud.utils.Alertas;



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

        eliminarNotificacion();

        //Elimina el recordatorio de la BD recordatorios

            RecordatoriosDBHelper dbHelper = new RecordatoriosDBHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String whereClause = RecordatoriosContract.RecordatoriosEntry.TIPO + "= ?";

            String[] args = {RecordatoriosContract.RecordatoriosEntry.TIPO_MEDICAMENTOS};

            db.delete(RecordatoriosContract.RecordatoriosEntry.TABLE_NAME, whereClause, args);


    }

    private void eliminarNotificacion() {
            RecordatoriosDBHelper dbHelper = new RecordatoriosDBHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String whereClause = RecordatoriosContract.RecordatoriosEntry.TIPO + "= ?";

            String[] args = {RecordatoriosContract.RecordatoriosEntry.TIPO_MEDICAMENTOS};

            //ELIMINAR NOTIFICACION SI AUN ESTA ACTIVA
            String[] columns = {RecordatoriosContract.RecordatoriosEntry.FECHA,
                    RecordatoriosContract.RecordatoriosEntry.ID_NOTIFICACION};

            Cursor mCursor = db.query(true, RecordatoriosContract.RecordatoriosEntry.TABLE_NAME, columns, whereClause, args, null, null, null, null);

            if(mCursor != null & mCursor.moveToFirst()){

                NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

                for(int i=0; i<mCursor.getCount(); i++){

                    String text_idnotificacion= mCursor.getString(1);

                    if(text_idnotificacion != null){

                        int idNotificacion = Integer.parseInt(text_idnotificacion);

                        // Cancelamos la Notificacion que hemos comenzado
                        nm.cancel(idNotificacion);

                        mCursor.moveToNext();
                    }
                }

            }
            mCursor.close();
            db.close();
            dbHelper.close();
    }

    @Override
    public void onClickOk(String text_medicamento_abandono, String fecha) {

        String descripcion = getResources().getString(R.string.alerta_medicamento) + " " + text_medicamento_abandono;

        //guardar Alarma en BD
        Alertas mAlertas = new Alertas(getApplicationContext());
        mAlertas.guardar(AlertasContract.AlertasEntry.ALERTA_TIPO_ROJA,
                AlertasContract.AlertasEntry.ALERTA_PARAMETRO_MEDICINE,
                descripcion,
                AlertasContract.AlertasEntry.ALERTA_VISIBILIDAD_PUBLICA);

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
