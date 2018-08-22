package com.luciayanicelli.icsalud.utils;

/**
 * Created by LuciaYanicelli on 23/7/2018.
 */

public interface Mediciones {

     String getMedicionesCSV();

     void alertaVerde(String fecha_sin_hora);

     boolean alertaAmarilla();


}

