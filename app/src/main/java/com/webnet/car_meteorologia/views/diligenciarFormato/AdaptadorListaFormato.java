package com.webnet.car_meteorologia.views.diligenciarFormato;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.webnet.car_meteorologia.R;
import com.webnet.car_meteorologia.Utils.Utils;
import com.webnet.car_meteorologia.sqLite.models.FormatoModel;

import java.util.ArrayList;

/**
 * Created by Gilmar Ocampo Nieves on 10/07/2016.
 */
public class AdaptadorListaFormato extends ArrayAdapter {

    Utils util;
    Activity context;
    ArrayList<FormatoModel> listaDatos;

    public AdaptadorListaFormato(Activity context, ArrayList<FormatoModel> l) {
        super(context, R.layout.layout_item_lista_general,l);
        this.context = context;
        this.listaDatos = l;
        this.util = new Utils();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public View getView(final int position, View convertView, ViewGroup parent){
        View item = convertView;
        item = context.getLayoutInflater().inflate(R.layout.layout_item_lista_general, null);

        TextView lblCodigo = item.findViewById(R.id.lblCodigo);
        TextView lblFecha =  item.findViewById(R.id.lblFecha);
        TextView lblEstado =  item.findViewById(R.id.lblEstado);
        RelativeLayout divCirculo =  item.findViewById(R.id.divCirculo);
        LinearLayout divPadre = item.findViewById(R.id.divPadre);

        FormatoModel doc = listaDatos.get(position);

        if(!doc.banVisible){
            divPadre.setVisibility(View.INVISIBLE);
            return item;
        }

        String[] temp = doc.Id.split("-");

        lblCodigo.setText(temp[temp.length - 1]);
        lblFecha.setText(doc.Fecha);
        lblEstado.setText(doc.Estado);

        if(doc.Estado.compareTo("PENDIENTE") == 0){
            util.setBackgroundView(context,divCirculo,R.drawable.circulo_negativo);
            lblEstado.setTextColor(util.getColor(context,R.color.color_C_negativo));
        }else{
            util.setBackgroundView(context,divCirculo,R.drawable.circulo_positivo);
            lblEstado.setTextColor(util.getColor(context,R.color.color_C_positivo));
        }

        return item;
    }
}
