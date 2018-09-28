package com.luciayanicelli.icsalud.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.luciayanicelli.icsalud.DataBase.AlertasContract;
import com.luciayanicelli.icsalud.DataBase.Alertas_DBHelper;

import java.util.concurrent.ExecutionException;

/**
 * Created by LuciaYanicelli on 27/9/2018.
 */

public class Alertas {

    private Context mContext;
    private String fechaHsHoy;

    public Alertas(Context context) {
        mContext = context;
        FechaActual fechaActual = new FechaActual();
        try {
            fechaHsHoy = fechaActual.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void guardar(String tipo, String parametro, String descripcion, int visibilidad){

        Alertas_DBHelper mDBHelper = new Alertas_DBHelper(mContext);
        SQLiteDatabase dbAlerta = mDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(AlertasContract.AlertasEntry.FECHA, fechaHsHoy);
        values.put(AlertasContract.AlertasEntry.TIPO, tipo);
        values.put(AlertasContract.AlertasEntry.PARAMETRO, parametro);
        values.put(AlertasContract.AlertasEntry.DESCRIPCION, descripcion);
        values.put(AlertasContract.AlertasEntry.ESTADO, AlertasContract.AlertasEntry.ALERTA_ESTADO_PENDIENTE);
        values.put(AlertasContract.AlertasEntry.VISIBILIDAD, visibilidad);

        try{
            long controlInsert = dbAlerta.insert(AlertasContract.AlertasEntry.TABLE_NAME, null, values);
            dbAlerta.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
