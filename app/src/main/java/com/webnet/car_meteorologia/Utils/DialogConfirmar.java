package com.webnet.car_meteorologia.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogConfirmar {

    private Context mContext;
    private String titulo;
    private String msg;

    private AlertDialog al;

    public DialogConfirmar(Context mContext,String titulo,String msg){
        this.mContext = mContext;
        this.titulo = titulo;
        this.msg = msg;
    }

    public void show(final eventDialog callback){
        AlertDialog.Builder bl = new AlertDialog.Builder(mContext);
        bl.setTitle(this.titulo);
        bl.setMessage(this.msg);
        bl.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(callback != null)
                    callback.confirmar();
                dialog.dismiss();
            }
        });
        bl.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        al = bl.create();
        al.show();
    }

    public void showSimple(final eventDialog callback){
        AlertDialog.Builder bl = new AlertDialog.Builder(mContext);
        bl.setTitle(this.titulo);
        bl.setMessage(this.msg);
        bl.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(callback != null)
                    callback.confirmar();
                dialog.dismiss();
            }
        });

        al = bl.create();
        al.show();
    }

    public void close(){
        al.dismiss();
    }

    public interface eventDialog{

        void confirmar();

    }

}


