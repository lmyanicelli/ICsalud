package com.luciayanicelli.icsalud.utils;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.luciayanicelli.icsalud.DataBase.RecordatoriosContract;
import com.luciayanicelli.icsalud.DataBase.RecordatoriosDBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;


/*OBJETO RECORDATORIO*/

public class Recordatorio implements Parcelable {

    private int id;
    private String titulo;
    private String descripcion;
    private int imagen;

    private String fecha;
    private String fechayhora; //"dateTime": "2017-09-10 13:11:15"

    private String parametro;

    private String idNotificacion; //ID de la notificacion para luego poder eliminarla

    public Recordatorio() {

        Calendar calendarNow = Calendar.getInstance();

        int monthDay =calendarNow.get(Calendar.DAY_OF_MONTH);
        int month = calendarNow.get(Calendar.MONTH) + 1; //Calendar.MONTH entrega del 0 al 11 - por eso sumo 1
        int year = calendarNow.get(Calendar.YEAR);

        fecha = String.valueOf(monthDay) + "-" + String.valueOf(month) + "-" + String.valueOf(year); //fecha de hoy en formato DD-MM-AAAA

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();

        fechayhora = dateFormat.format(date);

    }

    public Recordatorio(int id, String titulo, String descripcion, int imagen, String parametro, String idNotificacion) {
        this.setId(id);
        this.setTitulo(titulo);
        this.setDescripcion(descripcion);
        this.setImagen(imagen);
        this.setParametro(parametro);
        this.setIdNotificacion(idNotificacion);
    }


    public void setId(int id) { this.id = id; }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getImagen() {
        return imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getId() {
        return id;
    }

    public void setParametro(String parametro) { this.parametro = parametro; }

    public String getParametro (){ return parametro; }

    public void setIdNotificacion(String idNotificacion) { this.idNotificacion = idNotificacion; }

    public String getIdNotificacion () { return idNotificacion; }


    public boolean isEmpty() {
        if (titulo != null | descripcion != null) {
            return false;
        } else {
            return true;
        }
    }


    protected Recordatorio(Parcel in) {
        id = Integer.parseInt(in.readString());
        titulo = in.readString();
        descripcion = in.readString();
        imagen = Integer.parseInt(in.readString());
        parametro = in.readString();
        idNotificacion = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(titulo);
        dest.writeString(descripcion);
        dest.writeInt(imagen);
        dest.writeString(parametro);
        dest.writeString(idNotificacion);

    }

    @SuppressWarnings("unused")
    public static final Creator<Recordatorio> CREATOR = new Creator<Recordatorio>() {
        @Override
        public Recordatorio createFromParcel(Parcel in) {
            return new Recordatorio(in);
        }

        @Override
        public Recordatorio[] newArray(int size) {
            return new Recordatorio[size];
        }
    };

    //ESTE ES EL QUE ESTA EN USO
    //Crea un nuevo registro en la base de datos
    public boolean crearRecordatorioBD(Context context){

        RecordatoriosDBHelper mDbHelper = new RecordatoriosDBHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RecordatoriosContract.RecordatoriosEntry.TIPO, getTitulo());
        values.put(RecordatoriosContract.RecordatoriosEntry.RECORDATORIO, getDescripcion());
        values.put(RecordatoriosContract.RecordatoriosEntry.FECHA_HORA, fechayhora);
        values.put(RecordatoriosContract.RecordatoriosEntry.FECHA, fecha);

        if(parametro!= null){
            values.put(RecordatoriosContract.RecordatoriosEntry.PARAMETRO, getParametro());
        }

        if(idNotificacion != null){
            values.put(RecordatoriosContract.RecordatoriosEntry.ID_NOTIFICACION, getIdNotificacion());
        }

        //Devuelve -1 si hay un error al insertar los datos en la base de datos
       // long controlInsert =
       long controlInsert = db.insert(RecordatoriosContract.RecordatoriosEntry.TABLE_NAME, null, values);
        db.close();
        mDbHelper.close();
        //En caso de que los datos no se hayan insertado correctamente devuelve false
       if (controlInsert != -1){
            return true;
        }else{
            return false;
        }

     }


    public void eliminarRecordatorioBD(Context context, String parametro) {

        RecordatoriosDBHelper dbHelper = new RecordatoriosDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = RecordatoriosContract.RecordatoriosEntry.PARAMETRO + "= ?";
        //con esta segunda opcion elimino todos los recordatorios sobre el peso

        String[] args = {parametro};

        //ELIMINAR NOTIFICACION SI AUN ESTA ACTIVA

        String[] columns = {RecordatoriosContract.RecordatoriosEntry.FECHA,
                RecordatoriosContract.RecordatoriosEntry.ID_NOTIFICACION};

        Cursor mCursor = db.query(true, RecordatoriosContract.RecordatoriosEntry.TABLE_NAME, columns, whereClause, args, null, null, null, null);

        if(mCursor != null & mCursor.moveToFirst()){

            NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

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

        db.delete(RecordatoriosContract.RecordatoriosEntry.TABLE_NAME, whereClause, args);
        mCursor.close();
        db.close();
        dbHelper.close();

    }

    public void eliminarRecordatorioBD_CS(Context context, String parametro) {

        RecordatoriosDBHelper dbHelper = new RecordatoriosDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = RecordatoriosContract.RecordatoriosEntry.PARAMETRO + "= ?";
        //con esta segunda opcion elimino todos los recordatorios sobre el peso

        String[] args = {parametro};

        //ELIMINAR NOTIFICACION SI AUN ESTA ACTIVA

        String[] columns = {RecordatoriosContract.RecordatoriosEntry.FECHA,
                RecordatoriosContract.RecordatoriosEntry.ID_NOTIFICACION};

        Cursor mCursor = db.query(true, RecordatoriosContract.RecordatoriosEntry.TABLE_NAME, columns, whereClause, args, null, null, null, null);

        if(mCursor != null & mCursor.moveToFirst()){

            NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

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

        db.delete(RecordatoriosContract.RecordatoriosEntry.TABLE_NAME, whereClause, args);
        mCursor.close();
        db.close();
        dbHelper.close();

    }

}
