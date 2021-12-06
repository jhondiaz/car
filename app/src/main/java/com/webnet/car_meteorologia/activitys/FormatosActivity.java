package com.webnet.car_meteorologia.activitys;

import android.content.Intent;
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

import com.webnet.car_meteorologia.R;
import com.webnet.car_meteorologia.Utils.Utils;
import com.webnet.car_meteorologia.sqLite.MySqLite;
import com.webnet.car_meteorologia.sqLite.models.UsuarioModel;
import com.webnet.car_meteorologia.views.diligenciarFormato.DiligenciarFormatoActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class FormatosActivity extends AppCompatActivity {

    private String idEstacion;
    private String nombreEstacion;
    private Utils util;

    TextView lblEstacion;
    GridLayout gridFormatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formatos);
        initComponent();

        util = new Utils();

        Bundle extra = getIntent().getExtras();
        idEstacion = extra.getString("idEstacion");
        nombreEstacion = extra.getString("nombreEstacion");

        lblEstacion.setText(nombreEstacion);

        getData();
    }

    private void getData(){
        MySqLite sql = new MySqLite(this);
        UsuarioModel usuario = new UsuarioModel().getUsuario(sql.getDBR());
        try {
            JSONArray listaEstaciones = new JSONArray(usuario.Data);
            JSONObject obj =  null;
            for (int i = 0; i < listaEstaciones.length(); i++) {
                obj = listaEstaciones.getJSONObject(i);
                if(obj.getString("Id").compareTo(idEstacion) == 0){
                    break;
                }
            }
            if(obj != null){
                JSONArray listaFormatos = obj.getJSONArray("Formats");
                for (int i = 0; i < listaFormatos.length(); i++) {
                    final JSONObject formato = listaFormatos.getJSONObject(i);

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

                    TextView lblTexto = new TextView(this);
                    lblTexto.setGravity(Gravity.CENTER);
                    lblTexto.setTextSize(12f);
                    lblTexto.setTextColor(util.getColor(this,R.color.colorBlanco));
                    lblTexto.setText(formato.getString("Name"));
                    lblTexto.setTypeface(util.getFont(this));

                    divPadre.addView(logo);
                    divPadre.addView(lblTexto);

                    divPadre.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try{
                                String id = formato.getString("Id");
                                String nombre = formato.getString("Name");
                                String campos = formato.getJSONArray("Fields").toString();
                                irDiligenciarFormato(id,nombre,campos);
                            }catch (Exception ex){}
                        }
                    });

                    gridFormatos.addView(divPadre);

                    /*Button btn = new Button(this);
                    int ancho = util.dipPixel(this,150);
                    int alto = util.dipPixel(this,100);
                    btn.setTextSize(12f);
                    btn.setText(formato.getString("Name"));
                    btn.setTextColor(util.getColor(this,R.color.colorBlanco));
                    util.setBackgroundView(this,btn,R.drawable.botons);

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ancho,alto);
                    params.setMargins(10,10,10,10);
                    btn.setLayoutParams(params);

                    btn.setTypeface(util.getFont(this));

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try{
                                String id = formato.getString("Id");
                                String nombre = formato.getString("Name");
                                String campos = formato.getJSONArray("Fields").toString();
                                irDiligenciarFormato(id,nombre,campos);
                            }catch (Exception ex){}
                        }
                    });

                    gridFormatos.addView(btn);*/
                }
            }
        }catch (Exception ex){
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    private void irDiligenciarFormato(String idFormato,String nombre,String campos){
        Intent intent = new Intent(this, DiligenciarFormatoActivity.class);
        intent.putExtra("idEstacion",idEstacion);
        intent.putExtra("idFormato",idFormato);
        intent.putExtra("nombreFormato",nombre);
        intent.putExtra("campos",campos);
        startActivity(intent);
    }

    private void initComponent(){
        lblEstacion = findViewById(R.id.lblEstacion);
        gridFormatos = findViewById(R.id.gridFormatos);
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
