package com.webnet.car_meteorologia.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.TypedValue;
import android.view.View;

import com.webnet.car_meteorologia.config.ConfigApp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    private ProgressDialog dialogCargando;

    public String getTipoCampo(int TypeInput, int TypeDato){
        /**
         * TypeInput
         * 0 - input
         * 1 - automatico
         * 2 - evidencia
         *
         * TypeDato
         * 0 - String
         * 1 - Numero
         * 2 - DateTime
         * 3 - lista unica
         * 4 - lista multiple
         * */

        if(TypeInput == 1){
            return "LBL";
        }

        if(TypeInput == 2){
            return "EVIDENCIA";
        }

        if(TypeDato == 0 || TypeDato == 1){
            return "TXT";
        }else if(TypeDato == 2){
            return "";
        }else if(TypeDato == 3){
            return "CBOX";
        }else if(TypeDato == 4){
            return "LISTA";
        }

        return "";
    }

    public Typeface getFont(Activity mActivity){
        Typeface type = Typeface.createFromAsset(mActivity.getAssets(),"fonts/font_car.otf");
        return type;
    }

    public void initCargando(Activity mActivity, String mensaje, boolean cancelable){
        dialogCargando = new ProgressDialog(mActivity);
        dialogCargando.setMessage(mensaje);
        dialogCargando.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogCargando.setCancelable(cancelable);
        dialogCargando.show();
    }

    public int dipPixel(Activity mActivity,float dip){
        Resources r = mActivity.getResources();
        return (int)TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
    }

    public void finishCargando(){
        if(dialogCargando != null)
            if(dialogCargando.isShowing())
                dialogCargando.dismiss();
    }

    public void setBackgroundView(Activity mActivity,View v, int id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackground(getDrawable(mActivity,id));
        }else{
            v.setBackgroundDrawable(getDrawable(mActivity,id));
        }
    }

    public int getColor(Activity mActivity,int id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return mActivity.getResources().getColor(id,mActivity.getTheme());
        }else{
            return mActivity.getResources().getColor(id);
        }
    }

    public Drawable getDrawable(Activity mActivity,int id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mActivity.getResources().getDrawable(id,mActivity.getTheme());
        }else{
            return mActivity.getResources().getDrawable(id);
        }
    }

    public String fechaActual(){
        try {
            Calendar c = Calendar.getInstance();
            Date date = c.getTime();
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        }catch(Exception ex){}
        return null;
    }

    public String formatFecha(Date date){
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        }catch(Exception ex){}
        return null;
    }

    public void viewImg(String item, Activity mActivity){
        try {
            String strUrl = ConfigApp.pathEvidencias + "/" + item;
            String provider = mActivity.getApplicationContext().getPackageName() + ".fileprovider";
            Uri photoURI = FileProvider.getUriForFile(mActivity, provider, new File(strUrl));
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(photoURI, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mActivity.startActivity(intent);
        }catch (Exception ex){}
    }

    public File getFileimg(String fileName)throws IOException {
        File dir = new File(ConfigApp.pathEvidencias);
        if(!dir.exists()){
            if(!dir.mkdirs()){
                System.out.println("NOO SE PUDO CREAR CARPETA FOTO");
            }
        }
        File f = new File(ConfigApp.pathEvidencias, fileName);
        f.createNewFile();

        return f;
    }

    public void BitmapToFile(Bitmap bitmap, String fileName) throws IOException {
        File dir = new File(ConfigApp.pathEvidencias);
        if(!dir.exists()){
            if(!dir.mkdirs()){
                System.out.println("NOO SE PUDO CREAR CARPETA FOTO");
            }
        }

        File f = new File(ConfigApp.pathEvidencias, fileName);
        f.createNewFile();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , bos);
        byte[] bitmapdata = bos.toByteArray();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
    }

    public Bitmap FileToBitmap(String name){
        try {
            File file = new File(ConfigApp.pathEvidencias, name);
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        }catch (Exception ex){
            return null;
        }
    }

    public void deleteFile(String name){
        try {
            File file = new File(ConfigApp.pathEvidencias, name);
            file.delete();
        }catch (Exception ex){}
    }
}
