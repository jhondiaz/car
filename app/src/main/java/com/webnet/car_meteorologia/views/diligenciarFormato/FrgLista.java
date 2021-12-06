package com.webnet.car_meteorologia.views.diligenciarFormato;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.webnet.car_meteorologia.R;
import com.webnet.car_meteorologia.Utils.DialogConfirmar;
import com.webnet.car_meteorologia.sqLite.MySqLite;
import com.webnet.car_meteorologia.sqLite.models.FormatoModel;

import org.json.JSONArray;

import java.util.ArrayList;

public class FrgLista extends Fragment {
    View rootView;

    Context mContext;
    Activity mActivity;

    ListView listView;
    TextView lblInfo;
    ArrayList<FormatoModel> listaData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_lista, container, false);
        initComponent();
        cargarData();
        return rootView;
    }

    public void cargarData(){
        System.out.println("CARGAR DATA");
        MySqLite sql = new MySqLite(mActivity);
        String idFormato = ((DiligenciarFormatoActivity) mActivity).idFormato;
        String idEstacion = ((DiligenciarFormatoActivity) mActivity).idEstacion;
        listaData = new FormatoModel().getFormatos(sql.getDBR(),idFormato,idEstacion);

        AdaptadorListaFormato ad = new AdaptadorListaFormato(mActivity,listaData);
        listView.setAdapter(ad);

        lblInfo.setText("");
        if(listaData.size() <= 0){
            lblInfo.setText("No se han registrado datos");
        }
    }

    private void initComponent(){
        listView = rootView.findViewById(R.id.listView);
        lblInfo = rootView.findViewById(R.id.lblInfo);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listaData != null){
                    try{
                        //cargar data
                        FormatoModel formato = listaData.get(position);
                        ((DiligenciarFormatoActivity) mActivity).camposBD = new JSONArray(formato.Data);

                        ((DiligenciarFormatoActivity) mActivity).irDetalleBD();
                    }catch (Exception ex){
                        System.out.println("EROR CARGAR DATA: " + ex.getMessage());
                    }
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                DialogConfirmar.eventDialog event = new DialogConfirmar.eventDialog() {
                    @Override
                    public void confirmar() {
                        FormatoModel select = listaData.get(position);

                        MySqLite sql = new MySqLite(mActivity);
                        new FormatoModel().borrarRegistro(sql.getDBW(),select.Id);

                        cargarData();
                    }
                };

                new DialogConfirmar(mContext,"Advertencia","Desea eliminar el registro?").show(event);

                return true;
            }
        });
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
