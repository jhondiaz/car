package com.webnet.car_meteorologia.views.diligenciarFormato;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.webnet.car_meteorologia.R;
import com.webnet.car_meteorologia.activitys.ViewPdfActivity;
import com.webnet.car_meteorologia.config.ConfigApp;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

public class DiligenciarFormatoActivity extends AppCompatActivity {

    public String idEstacion;
    public String idFormato;
    public String nombreFormato;
    public JSONArray campos;
    public JSONArray camposBD;
    public boolean banDetalleBd = false;

    TextView lblFormato;
    private ConfigApp.currentFrg frgState;
    private String currentTagFrg = "";

    private ImageView imgAdd;
    private FrgLista frgLista;

    public String codigo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diligenciar_formato);
        initComponent();
        initEvents();
        Bundle extra = getIntent().getExtras();

        try{
            idEstacion = extra.getString("idEstacion");
            idFormato = extra.getString("idFormato");
            nombreFormato = extra.getString("nombreFormato");
            campos = new JSONArray(extra.getString("campos"));
        }catch (Exception ex){

        }

        lblFormato.setText(nombreFormato);

        IntentFilter filter = new IntentFilter();
        filter.addAction("_itent_update_list_");
        registerReceiver(mBroadcastReceiver, filter);

        //INIT DATA
        irLista();
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "_itent_update_list_":
                    if(frgState == ConfigApp.currentFrg.LISTA){
                        if(frgLista != null)
                            frgLista.cargarData();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null)
            unregisterReceiver(mBroadcastReceiver);
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.frgRoot, fragment, tag);
        ft.commitAllowingStateLoss();

        this.currentTagFrg = tag;

        if(frgState == ConfigApp.currentFrg.LISTA){
            //MOSTRAR BTN PARA AGREGAR NUEVO
            imgAdd.setVisibility(View.VISIBLE);
        }else{
            imgAdd.setVisibility(View.GONE);
        }
    }

    private void setdataCampos(){
        try{
            if(campos != null){
                for(int i = 0 ; i < campos.length(); i++){
                    JSONObject obj = campos.getJSONObject(i);
                    if(obj.has("Data")){
                        obj.put("Data","");
                    }
                }
            }
        }catch (Exception ex){}
    }

    public void irLista(){
        setdataCampos();
        frgState = ConfigApp.currentFrg.LISTA;
        frgLista = new FrgLista();
        addFragment(frgLista,false,"frgLista");
    }

    public void irCapturaDatos(){
        this.codigo = UUID.randomUUID().toString();
        frgState = ConfigApp.currentFrg.CAPTURA;
        addFragment(new FrgCaptura(),false,"frgCaptura");
    }

    public void irDetalle(){
        banDetalleBd = false;
        frgState = ConfigApp.currentFrg.DETALLE;
        addFragment(new FrgDetalle(),false,"frgDetalle");
    }

    public void irDetalleBD(){
        banDetalleBd = true;
        frgState = ConfigApp.currentFrg.DETALLE_BD;
        addFragment(new FrgDetalle(),false,"frgDetalle");
    }

    @Override
    public void onBackPressed() {

        switch (frgState){
            case LISTA:
                finish();
                break;
            case CAPTURA:
                irLista();
                break;
            case DETALLE:
                irCapturaDatos();
                break;
            case DETALLE_BD:
                irLista();
                break;
        }
    }

    private void initComponent(){
        lblFormato = findViewById(R.id.lblFormato);
        imgAdd = findViewById(R.id.imgAdd);

        imgAdd.setVisibility(View.GONE);
    }

    private void initEvents(){
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irCapturaDatos();
            }
        });
    }

    public void onBtnAjustes(View v){

    }

    public void onBtnManual(View v){
        Intent intent = new Intent(this, ViewPdfActivity.class);
        startActivity(intent);
    }

    public void onBtnSoporte(View v){

    }
}
