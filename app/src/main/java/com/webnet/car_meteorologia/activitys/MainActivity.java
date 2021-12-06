package com.webnet.car_meteorologia.activitys;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.webnet.car_meteorologia.R;
import com.webnet.car_meteorologia.Utils.DialogConfirmar;
import com.webnet.car_meteorologia.Utils.Utils;
import com.webnet.car_meteorologia.services.ApiController;
import com.webnet.car_meteorologia.services.HttpResultReceiver;
import com.webnet.car_meteorologia.services.ServicioEnviarData;
import com.webnet.car_meteorologia.sqLite.MySqLite;
import com.webnet.car_meteorologia.sqLite.models.UsuarioModel;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements  HttpResultReceiver.Receiver{

    GridLayout gridEstaciones;
    HttpResultReceiver mHttpResultReceiver;
    Utils util;

    public static boolean banSend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.banSend = false;

        mHttpResultReceiver = new HttpResultReceiver(new Handler());
        mHttpResultReceiver.setReceiver(this);

        util = new Utils();

        initConponent();
        initEvents();

        getData();
        getAlarmas();


    }

    private void initServiceCheck(){
        Intent serv = new Intent(this, ServicioEnviarData.class);
        getBaseContext().startService(serv);
    }

    private void getAlarmas(){
        final MySqLite sql = new MySqLite(this);
        final UsuarioModel usuario = new UsuarioModel().getUsuario(sql.getDBR());
        usuario.Alarmas = "[]";
        usuario.updateAlarmas(sql.getDBW(),usuario.Id);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray listaEstaciones = new JSONArray(usuario.Data);
                    for (int i = 0; i < listaEstaciones.length(); i++) {
                        final JSONObject obj = listaEstaciones.getJSONObject(i);
                        String idTypeStation = obj.getString("IdTypeStation");

                        JSONObject params = new JSONObject();
                        params.put("id",idTypeStation);
                        ApiController.getAlarmar(MainActivity.this,mHttpResultReceiver,params);
                        MainActivity.banSend = true;

                        while(MainActivity.banSend){
                            Thread.sleep(1000);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initServiceCheck();
                        }
                    });
                }catch (Exception ex){
                    System.out.println("ERROR GET ALARMAS: " + ex.getMessage());
                }
            }
        }).start();

    }

    private void getData(){
        MySqLite sql = new MySqLite(this);
        UsuarioModel usuario = new UsuarioModel().getUsuario(sql.getDBR());
        try{
            JSONArray listaEstaciones = new JSONArray(usuario.Data);
            for(int i = 0; i < listaEstaciones.length(); i++){
                final JSONObject obj = listaEstaciones.getJSONObject(i);

                int ancho = util.dipPixel(this,150);
                int alto = util.dipPixel(this,100);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ancho,RelativeLayout.LayoutParams.WRAP_CONTENT);

                LinearLayout divPadre = new LinearLayout(this);
                divPadre.setOrientation(LinearLayout.VERTICAL);
                divPadre.setGravity(Gravity.CENTER_HORIZONTAL);
                divPadre.setClickable(true);
                params.setMargins(10,10,10,10);
                divPadre.setLayoutParams(params);

                ImageView logo = new ImageView(this);
                ancho = util.dipPixel(this,80);
                alto = util.dipPixel(this,80);
                params = new RelativeLayout.LayoutParams(ancho,alto);
                params.setMargins(0,0,0,0);
                logo.setLayoutParams(params);

                util.setBackgroundView(this,logo,R.drawable.ic_estaciones);

                //NOMBRE DE LA ESTACION
                String nameStation = obj.getString("Name") + "\n" + obj.getString("TypeName");
                UsuarioModel usu = new UsuarioModel().getUsuario(sql.getDBR());
                if(usu != null){
                    if(obj.has("Codigo") && usu.TypeObserver.compareTo("2") == 0){
                        if(obj.getString("Codigo") != null &&
                                obj.getString("Codigo").toLowerCase().compareTo("null") != 0){
                            nameStation += " - " + obj.getString("Codigo");
                        }
                    }
                }


                TextView lblTexto = new TextView(this);
                lblTexto.setGravity(Gravity.CENTER);
                lblTexto.setTextSize(12f);
                lblTexto.setTextColor(util.getColor(this,R.color.colorBlanco));
                lblTexto.setText(nameStation);
                lblTexto.setTypeface(util.getFont(this));

                divPadre.addView(logo);
                divPadre.addView(lblTexto);

                /*Button btn = new Button(this);
                btn.setTextSize(12f);
                btn.setTextColor(util.getColor(this,R.color.colorBlanco));
                btn.setText(obj.getString("Name") + "\n" + obj.getString("TypeName"));
                util.setBackgroundView(this,btn,R.drawable.botons);

                btn.setTypeface(util.getFont(this));

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ancho,alto);
                params.setMargins(10,10,10,10);
                btn.setLayoutParams(params);
                */

                divPadre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            String id = obj.getString("Id");
                            String nombre = obj.getString("Name") + "\n" + obj.getString("TypeName");
                            irFormatos(id,nombre);
                        }catch (Exception ex){}
                    }
                });

                gridEstaciones.addView(divPadre);
            }
        }catch (Exception ex){
            System.out.println("ERROR BTN: " + ex.getMessage());
        }
    }

    private void irFormatos(String idEstacion,String nombre){

        //VALIDAR TIPO DE OBSERVADOR
        MySqLite sql = new MySqLite(this);
        UsuarioModel usu = new UsuarioModel().getUsuario(sql.getDBR());
        if(usu == null){
            DialogConfirmar dg = new DialogConfirmar(
                    this,
                    "Advertencia",
                    "Usuario no válido"
            );
            dg.showSimple(null);
            return;
        }

        if(usu.TypeObserver.compareTo("2") == 0){
            DialogConfirmar dg = new DialogConfirmar(
                    this,
                    "Advertencia",
                    "Usted es un usuario te tipo LAPICERO y no tiene acceso a la captura de datos por la APP"
            );
            dg.showSimple(null);
            return;
        }

        Intent intent = new Intent(this,FormatosActivity.class);
        intent.putExtra("idEstacion",idEstacion);
        intent.putExtra("nombreEstacion",nombre);
        startActivity(intent);
    }

    private void initConponent(){
        gridEstaciones = findViewById(R.id.gridEstaciones);
    }

    private void initEvents(){
        /*btnPluviometrica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PluviometricaActivity.class);
                intent.putExtra("conEvidencia",false);
                intent.putExtra("titulo","Pluviometrica");
                startActivity(intent);
            }
        });*/

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == ApiController.STATUS_ERROR) {
            Toast.makeText(MainActivity.this, resultData.getString(Intent.EXTRA_TEXT), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (resultCode) {
            case ApiController.Result.GetAlarmas:
                try {
                    String Result = resultData.getString(ApiController.EXTRA_DATARESULT);
                    if(Result == null){
                        MainActivity.banSend = false;
                        return;
                    }
                    JSONArray response = new JSONArray(Result);

                    MySqLite sql = new MySqLite(this);
                    UsuarioModel usuario = new UsuarioModel().getUsuario(sql.getDBR());
                    JSONArray vecAlarmas = new JSONArray(usuario.Alarmas);
                    for(int i = 0; i < response.length(); i++){
                        JSONObject obj = response.getJSONObject(i);

                        JSONObject temp = new JSONObject();
                        temp.put("Id",obj.getString("Id"));
                        temp.put("Name",obj.getString("Name"));
                        temp.put("AlertDate",obj.getString("AlertDate").replace("T"," "));

                        vecAlarmas.put(temp);
                    }

                    usuario.Alarmas = vecAlarmas.toString();
                    usuario.updateAlarmas(sql.getDBW(),usuario.Id);

                    MainActivity.banSend = false;
                    System.out.println("GUARDADO CORRECTAMENTE");

                } catch (Exception ex) {
                }
        }
    }

    public void onBtnAjustes(View v){

    }

    public void onBtnManual(View v){
        Intent intent = new Intent(this,ViewPdfActivity.class);
        startActivity(intent);
    }

    public void onBtnSoporte(View v){

    }
}
