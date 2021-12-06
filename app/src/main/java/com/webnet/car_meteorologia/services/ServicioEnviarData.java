package com.webnet.car_meteorologia.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.webnet.car_meteorologia.Utils.UpLoadDownloadToFile;
import com.webnet.car_meteorologia.Utils.eventUploadImg;
import com.webnet.car_meteorologia.activitys.LoginActivity;
import com.webnet.car_meteorologia.config.ConfigApp;
import com.webnet.car_meteorologia.sqLite.MySqLite;
import com.webnet.car_meteorologia.sqLite.models.FormatoModel;
import com.webnet.car_meteorologia.sqLite.models.UsuarioModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ServicioEnviarData extends Service implements  HttpResultReceiver.Receiver{

    private int TIME_SEND = 10000;
    private boolean banInit = true;
    private boolean banEnviando = false;
    private boolean banEnviandoImg = false;
    private String idDocumento = "";

    private int contadorImgSend = 0;

    HttpResultReceiver mHttpResultReceiver;

    public ServicioEnviarData() {
        mHttpResultReceiver = new HttpResultReceiver(new Handler());
        mHttpResultReceiver.setReceiver(this);
    }

    private void revisarHoraAlerta(){
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            MySqLite sql = new MySqLite(this);
            UsuarioModel usuario = new UsuarioModel().getUsuario(sql.getDBR());


            JSONArray vecAlarmas = new JSONArray(usuario.Alarmas);
            for(int i = 0; i < vecAlarmas.length(); i++){

                try{
                    NotificationScheduler.cancelReminder(getApplicationContext(),
                            AlarmReceiver.class,i + 1);
                }catch (Exception ex){}

                JSONObject obj = vecAlarmas.getJSONObject(i);

                Date date = sdf.parse(obj.getString("AlertDate"));
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                String msg = obj.getString("Name");
                msg += " ";
                msg += obj.getString("AlertDate");

                Calendar calendar = Calendar.getInstance();
                if(cal.after(calendar)){
                    NotificationScheduler.setReminder(getApplicationContext(),
                            AlarmReceiver.class,cal,i + 1,msg);
                }

            }

            System.out.println("ALARMAS AGREGADAS ");

        }catch (Exception ex){
            System.out.println("ERROR ALARMA: " + ex.getMessage());
        }
    }

    private void initService(){
        revisarHoraAlerta();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (banInit) {
                        System.out.println("VERIFICANDO CONEXION...");
                        if(isInternetAvailable()){
                            System.out.println("SI HAY CONEXION...");
                            if(!banEnviando){
                                enviarData();
                            }
                            if(!banEnviandoImg){
                                enviarDataImg();
                            }
                        }else{
                            System.out.println("NO HAY CONEXION...");
                        }
                        Thread.sleep(TIME_SEND);
                    }
                }catch (Exception ex){
                    System.out.println("ERROR SERVICIO: " + ex.getMessage());
                }
            }
        }).start();
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }

    private void enviarData(){
        try {
            MySqLite sql = new MySqLite(getApplicationContext());
            ArrayList<FormatoModel> lista = new FormatoModel().getFormatosPorEnviar(sql.getDBR());

            if (lista != null) {
                //for (FormatoModel item : lista) {
                if(lista.size() > 0){
                    FormatoModel item = lista.get(0);
                    JSONObject objEnviar = new JSONObject();

                    idDocumento = item.Id;

                    objEnviar.put("IdDocumento", item.Id);
                    objEnviar.put("IdObservador", item.IdObservador);
                    objEnviar.put("IdEstacion", item.IdEstacion);
                    objEnviar.put("IdFormato", item.IdFormato);

                    JSONArray vecData = new JSONArray(item.Data);
                    JSONArray arrayData = new JSONArray();
                    for(int i = 0; i < vecData.length(); i++){
                        JSONObject tmepData = vecData.getJSONObject(i);

                        JSONObject temp = new JSONObject();
                        temp.put("Id",tmepData.getString("Id"));
                        temp.put("Value",tmepData.getString("Data"));
                        arrayData.put(temp);
                    }

                    objEnviar.put("Fields",arrayData);
                    System.out.println("OBJ: " + objEnviar.toString());

                    banEnviando = true;
                    ApiController.postSendData(getApplicationContext(),mHttpResultReceiver,objEnviar);

                }
            }
        }catch (Exception ex){
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    private void enviarDataImg(){
        try {
            final MySqLite sql = new MySqLite(getApplicationContext());
            ArrayList<FormatoModel> lista = new FormatoModel().getFormatosPorEnviarImg(sql.getDBR());

            if (lista != null) {
                if(lista.size() > 0){
                    System.out.println("PREPARANDO ENVIAR FOTO...");
                    banEnviandoImg = true;
                    final FormatoModel item = lista.get(0);
                    String[] vecFotos = null;
                    JSONArray dataObj = new JSONArray(item.Data);
                    for(int i = 0; i < dataObj.length(); i++){
                        JSONObject temp = dataObj.getJSONObject(i);
                        if(temp.getString("TypeInput").compareTo("2") == 0){
                            vecFotos = temp.getString("Data").split(",");
                            break;
                        }
                    }
                    if(vecFotos != null){
                        System.out.println("SI HAY FOTOS POR ENVIAR...");
                        final int tam = vecFotos.length;
                        contadorImgSend = 0;
                        eventUploadImg callback = new eventUploadImg() {
                            @Override
                            public void resultLoad(boolean ban) {
                                if(ban){
                                    contadorImgSend++;
                                    if(contadorImgSend >= tam){
                                        //new FormatoModel().actualizarEstadoImg(sql.getDBW(),item.Id,"ENVIADO");
                                        banEnviandoImg = false;
                                    }
                                }
                            }
                        };


                        for(String itemImg : vecFotos){
                            System.out.println("FOTO: " + itemImg);
                            //new UpLoadDownloadToFile().UploadingAsync(ConfigApp.pathEvidencias,"test.jpg",callback);
                            new UpLoadDownloadToFile().UploadingAsync(ConfigApp.pathEvidencias,itemImg,callback);
                        }
                    }
                }else{
                    System.out.println("NO HAY IMAGENES POR ENVIAR");
                }
            }
        }catch (Exception ex){
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initService();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == ApiController.STATUS_ERROR) {
            System.out.println("ERROR SERVER: " + resultData.getString(Intent.EXTRA_TEXT));
            return;
        }
        switch (resultCode) {
            case ApiController.Result.SendData:
                try{

                    String Result = resultData.getString(ApiController.EXTRA_DATARESULT);
                    System.out.println("RESULT ENVIADO: " + Result);
                    JSONObject response = new JSONObject(Result);
                    if(response.getString("result").toLowerCase().compareTo("ok") == 0){
                        //ACTUALIZAR ESTADO
                        MySqLite sql = new MySqLite(getApplicationContext());
                        new FormatoModel().actualizarEstado(sql.getDBW(),idDocumento,"ENVIADO");

                        sendBroadcast(new Intent("_itent_update_list_"));
                    }

                }catch (Exception ex){
                    System.out.println("ERROR SEND DATA: " +ex.getMessage());
                }
                banEnviando = false;
                break;
        }
    }
}
