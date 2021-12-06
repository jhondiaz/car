package com.webnet.car_meteorologia.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.webnet.car_meteorologia.R;
import com.webnet.car_meteorologia.Utils.DialogConfirmar;
import com.webnet.car_meteorologia.Utils.Utils;
import com.webnet.car_meteorologia.services.ApiController;
import com.webnet.car_meteorologia.services.HttpResultReceiver;
import com.webnet.car_meteorologia.sqLite.MySqLite;
import com.webnet.car_meteorologia.sqLite.models.FormatoModel;
import com.webnet.car_meteorologia.sqLite.models.UsuarioModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements  HttpResultReceiver.Receiver{

    Button btnIngresar;
    EditText txtIdentificacion;
    ArrayList<String> listaPermisos;
    TextView lblVersion;

    HttpResultReceiver mHttpResultReceiver;

    Utils util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponent();
        initEvents();

        util = new Utils();
        mHttpResultReceiver = new HttpResultReceiver(new Handler());
        mHttpResultReceiver.setReceiver(this);

        listaPermisos = new ArrayList<>();
        if(!verificarPermisos()){
            pedirPermisos();
        }else{
            //VALIDAR SESSION
            /*MySqLite sql = new MySqLite(LoginActivity.this);
            UsuarioModel usuario = new UsuarioModel().getUsuario(sql.getDBR());
            if(usuario != null){
                Intent intent = new Intent(this,MainActivity.class);
                this.startActivity(intent);
                this.finish();
            }*/
        }

        MySqLite sql = new MySqLite(LoginActivity.this);
        UsuarioModel usuario = new UsuarioModel().getUsuario(sql.getDBR());
        if(usuario != null){
            txtIdentificacion.setText(usuario.Nit);
        }
        setVersion();
    }

    private void setVersion(){
        String infoVersion = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int codeVersion = 0;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                codeVersion = (int) pInfo.getLongVersionCode();
            } else {
                codeVersion = pInfo.versionCode;
            }
            infoVersion = version + "." + String.valueOf(codeVersion);
        } catch (Exception e) {}
        lblVersion.setText(infoVersion);
    }

    private void initComponent(){
        btnIngresar = findViewById(R.id.btnIngresar);
        txtIdentificacion = findViewById(R.id.txtIdentificacion);
        lblVersion = findViewById(R.id.lblVersion);
    }

    private void initEvents(){
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingresarApp();
            }
        });
    }

    private void ingresarApp(){
        if(txtIdentificacion.getText().toString().trim().compareTo("") == 0){
            Toast.makeText(this,"Debes ingresar una identificaciòn vàlida",Toast.LENGTH_LONG).show();
            return;
        }
        try{
            util.initCargando(this,"Validando...",false);
            JSONObject params = new JSONObject();
            params.put("nit",txtIdentificacion.getText().toString().trim());
            ApiController.getLogin(LoginActivity.this,mHttpResultReceiver,params);
        }catch (Exception ex){
            System.out.println("ERROR LOGIN: " + ex.getMessage());
        }
    }

    public void pedirPermisos(){
        String[] strLista = arrayToStringArray(listaPermisos);
        ActivityCompat.requestPermissions(LoginActivity.this,strLista,1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        boolean ban = true;
        for(int i = 0; i < grantResults.length; i++){
            if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                ban = false;
            }
        }
    }

    private boolean verificarPermisos(){
        boolean ban = true;
        listaPermisos = new ArrayList<>();
        /*if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)) {
            listaPermisos.add(Manifest.permission.READ_PHONE_STATE);
            ban = false;
        }*/
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            listaPermisos.add(Manifest.permission.ACCESS_FINE_LOCATION);
            ban = false;
        }
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            listaPermisos.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            ban = false;
        }
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            listaPermisos.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            ban = false;
        }
        return ban;
    }

    private String[] arrayToStringArray(ArrayList<String> listaPermisos){
        String[] listaNueva = new String[listaPermisos.size()];
        for(int i = 0; i < listaPermisos.size(); i ++){
            listaNueva[i] = listaPermisos.get(i);
        }
        return listaNueva;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        util.finishCargando();
        if (resultCode == ApiController.STATUS_ERROR) {
            Toast.makeText(LoginActivity.this, resultData.getString(Intent.EXTRA_TEXT), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (resultCode) {
            case ApiController.Result.Login:
                try{
                    String Result = resultData.getString(ApiController.EXTRA_DATARESULT);
                    JSONObject response = new JSONObject(Result);

                    if(response == null || !response.has("Observer")){
                        DialogConfirmar dg = new DialogConfirmar(LoginActivity.this,
                                "Advertencia","Usuario no registrado en el sistema");
                        dg.showSimple(null);
                        return;
                    }

                    if(response.getJSONObject("Observer").getBoolean("LockoutEnabled")){
                        DialogConfirmar dg = new DialogConfirmar(LoginActivity.this,
                                "Advertencia","Usuario bloqueado");
                        dg.showSimple(null);
                        return;
                    }

                    MySqLite sql = new MySqLite(LoginActivity.this);
                    UsuarioModel usuario = new UsuarioModel();

                    usuario.borrar(sql.getDBW());

                    usuario.mapearJson(response.getJSONObject("Observer"));
                    usuario.mapearData(response.getJSONArray("ListStations"));
                    usuario.Insertar(sql.getDBW());

                    Intent intent = new Intent(this,MainActivity.class);
                    this.startActivity(intent);
                    this.finish();
                }catch (Exception ex){
                    System.out.println("ERROR: " +ex.getMessage());
                }
                break;
        }
    }
}
