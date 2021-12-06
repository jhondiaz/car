package com.webnet.car_meteorologia.views.diligenciarFormato;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.webnet.car_meteorologia.R;
import com.webnet.car_meteorologia.Utils.DialogConfirmar;
import com.webnet.car_meteorologia.Utils.Utils;
import com.webnet.car_meteorologia.sqLite.MySqLite;
import com.webnet.car_meteorologia.sqLite.models.FormatoModel;
import com.webnet.car_meteorologia.sqLite.models.UsuarioModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class FrgDetalle extends Fragment {
    Context mContext;
    Activity mActivity;
    View rootView;
    Utils util;

    private String codigo = "";

    Button btnSiguiente;
    LinearLayout divPadre;
    LinearLayout divImg;
    private List<String> listImg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_detalle, container, false);

        divImg = new LinearLayout(mActivity);
        divImg.setOrientation(LinearLayout.HORIZONTAL);
        this.listImg = new ArrayList<>();

        util = new Utils();
        this.codigo = ((DiligenciarFormatoActivity) mActivity).codigo;

        initComponent();
        initEvents();
        cargarData();

        if(((DiligenciarFormatoActivity) mActivity).banDetalleBd){
            btnSiguiente.setVisibility(View.GONE);
        }else{
            btnSiguiente.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    private void cargarData(){
        JSONArray campos;
        if(((DiligenciarFormatoActivity) mActivity).banDetalleBd){
            campos = ((DiligenciarFormatoActivity) mActivity).camposBD;
        }else{
            campos = ((DiligenciarFormatoActivity) mActivity).campos;
        }
        for(int i = 0; i < campos.length(); i++){
            try{
                JSONObject camp = campos.getJSONObject(i);

                String unidad = "";
                if(((DiligenciarFormatoActivity) mActivity).banDetalleBd){
                    unidad = camp.getString("Unidad");
                }else{
                    unidad = camp.getString("Label");
                }

                String label = "";
                if(unidad.compareTo("") != 0 && unidad != null && unidad.compareTo("null") != 0 && unidad.compareTo(".") != 0){
                    label = " (" + unidad + ")";
                }

                TextView lblNombreCampo = new TextView(mActivity);
                lblNombreCampo.setText(camp.getString("Name")+ label);
                lblNombreCampo.setTextSize(18f);
                lblNombreCampo.setGravity(Gravity.CENTER);
                lblNombreCampo.setTypeface(util.getFont(mActivity));


                TextView lblData = new TextView(mActivity);
                lblData.setText("");

                LinearLayout divP = new LinearLayout(mActivity);

                int TypeDato = camp.getInt("TypeDato");
                int TypeInput = camp.getInt("TypeInput");
                String tipo = util.getTipoCampo(TypeInput,TypeDato);
                if(camp.has("Data")){
                    String data = "";

                    if(tipo.compareTo("LISTA") == 0){
                        /*JSONArray vec = new JSONArray(camp.getString("Data"));
                        for(int ind = 0; ind < vec.length(); ind ++){
                            data += vec.getString(ind);
                            if(ind + 1 < vec.length())
                                data += " - ";
                        }*/
                        String[] vec = camp.getString("Data").split(",");
                        for(int ind = 0; ind < vec.length; ind++){
                            data += vec[ind];
                            if(ind + 1 < vec.length)
                                data += " - ";
                        }
                    }else if(tipo.compareTo("EVIDENCIA") == 0){
                        divP.setOrientation(LinearLayout.VERTICAL);
                        HorizontalScrollView scroll = new HorizontalScrollView(mActivity);
                        scroll.addView(divImg);
                        divP.addView(scroll);

                        String[] temp = camp.getString("Data").split(",");
                        for(String item : temp){
                            listImg.add(item);
                        }
                        renderListImg();
                    }else{
                        data = camp.getString("Data");
                    }

                    lblData.setText(data);
                }
                lblData.setTextSize(18f);
                lblData.setGravity(Gravity.CENTER);
                lblData.setTypeface(util.getFont(mActivity));
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,0,0,60);
                lblData.setLayoutParams(params);


                divPadre.addView(lblNombreCampo);
                if(tipo.compareTo("EVIDENCIA") != 0){
                    divPadre.addView(lblData);
                }else{
                    divPadre.addView(divP);
                }

            }catch (Exception ex){
                System.out.println("ERRO AL CARAR DATA: " +ex.getMessage());
            }
        }
    }

    private void previewImg(String file){
        util.viewImg(file,mActivity);
    }

    private void renderListImg(){
        divImg.removeAllViews();
        for(final String item : this.listImg){
            Bitmap foto = util.FileToBitmap(item);
            ImageView imgEvi = new ImageView(mActivity);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200,200);
            params.setMargins(10,10,10,10);
            imgEvi.setLayoutParams(params);
            imgEvi.setImageBitmap(foto);

            imgEvi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    previewImg(item);
                }
            });

            divImg.addView(imgEvi);
        }
    }

    private void irSiguienteFrg(){
        //GUARDAR DATA
        try {
            JSONArray dataSave = new JSONArray();
            String idEstacion = ((DiligenciarFormatoActivity) mActivity).idEstacion;
            String idFormato = ((DiligenciarFormatoActivity) mActivity).idFormato;
            JSONArray campos = ((DiligenciarFormatoActivity) mActivity).campos;
            for (int i = 0; i < campos.length(); i++) {
                JSONObject obj = campos.getJSONObject(i);
                JSONObject objSave = new JSONObject();

                objSave.put("Id", obj.getString("Id"));
                objSave.put("Name", obj.getString("Name"));
                objSave.put("Unidad", obj.getString("Label"));
                objSave.put("TypeDato", obj.getString("TypeDato"));
                objSave.put("TypeInput", obj.getString("TypeInput"));
                objSave.put("Data", obj.getString("Data"));
                dataSave.put(objSave);
            }
            MySqLite sql = new MySqLite(mActivity);
            FormatoModel formato = new FormatoModel();

            UsuarioModel usu = new UsuarioModel().getUsuario(sql.getDBR());

            formato.Id = codigo;
            formato.IdEstacion = idEstacion;
            formato.IdFormato = idFormato;
            formato.IdObservador = usu.Id;
            formato.Data = dataSave.toString();
            formato.Fecha = util.fechaActual();
            formato.Estado = "PENDIENTE";
            formato.EstadoImg = "";

            if(listImg.size() > 0){
                formato.EstadoImg = "PENDIENTE";
            }

            formato.Insertar(sql.getDBW());

            ((DiligenciarFormatoActivity) mActivity).irLista();
        }catch (Exception ex){
            System.out.println("ERROR SAVE: " +ex.getMessage());
        }
    }

    private void initComponent(){
        btnSiguiente = rootView.findViewById(R.id.btnSiguiente);
        divPadre = rootView.findViewById(R.id.divPadre);
    }

    private void initEvents(){
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irSiguienteFrg();
            }
        });

        /*btnCapturarEvidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturarEvidencia();
            }
        });*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
