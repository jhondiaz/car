package com.webnet.car_meteorologia.config;

import android.os.Environment;

public class ConfigApp {

    public static boolean debug = false;
    public static String pathEvidencias = Environment.getExternalStorageDirectory().getPath() +"/EvidenciasCAR";
    public static int interbaloAlerta = 10;
    public static String[] horasNotificacion = new String[]{
            "07:00:00",
            "13:00:00",
            "19:00:00"
    };

    public static String getUrlServer(){
        if(debug)
            return "http://evaluadorescar.skgtecnologia.com/";
        else
            return "http://evaluadorescar.skgtecnologia.com/";
    }

    public static enum currentFrg {
        LISTA,
        CAPTURA,
        OBSERVACIONES,
        DETALLE,
        DETALLE_BD
    }

}
