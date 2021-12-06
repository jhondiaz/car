package com.webnet.car_meteorologia.views.diligenciarFormato;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.webnet.car_meteorologia.R;
import com.webnet.car_meteorologia.Utils.DialogConfirmar;
import com.webnet.car_meteorologia.Utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class FrgCaptura extends Fragment {
    Context mContext;
    Activity mActivity;
    View rootView;
    Utils util;
    Button btnSiguiente;
    LinearLayout divPadre;
    List<DataModel> listaData;

    LinearLayout divImg;

    private String codigo = "";
    private int contadorImg = 0;
    private List<String> listImg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_captura, container, false);
        util = new Utils();

        divImg = new LinearLayout(mActivity);
        divImg.setOrientation(LinearLayout.HORIZONTAL);
        this.listImg = new ArrayList<>();
        this.contadorImg = 0;
        this.codigo = ((DiligenciarFormatoActivity) mActivity).codigo;

        initComponent();
        initEvents();
        crearFormulario();
        cargarData();
        return rootView;
    }

    private void crearFormulario(){
        JSONArray campos = ((DiligenciarFormatoActivity) mActivity).campos;
        listaData = new ArrayList<>();
        for(int i = 0; i < campos.length(); i++){
            try{
                JSONObject camp = campos.getJSONObject(i);
                int TypeDato = camp.getInt("TypeDato");
                int TypeInput = camp.getInt("TypeInput");
                JSONArray values = null;
                if(camp.get("IsValues") != null && camp.getString("IsValues").compareTo("null") != 0) {
                    if (camp.getString("IsValues").compareTo("") != 0) {
                        values = new JSONArray(camp.getString("IsValues"));
                    }
                }


                View lblNombreCampo = createLabel(camp.getString("Name"),camp.getString("Label"));
                View campo = createInput(TypeInput,TypeDato,values);


                divPadre.addView(lblNombreCampo);
                divPadre.addView(campo);
            }catch (Exception ex){
                System.out.println("ERROR AL CREAR VIEWS: " +ex.getMessage());
            }
        }
    }

    private void capturarEvidencia(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            try{
                this.contadorImg++;
                String extension = ".jpg";
                String nameImg = "CAR_" + this.codigo + "_" + String.valueOf(this.contadorImg) + extension;

                File f = util.getFileimg(nameImg);
                String provider = mActivity.getApplicationContext().getPackageName() + ".fileprovider";
                Uri uri = FileProvider.getUriForFile(mActivity, provider, f);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                startActivityForResult(takePictureIntent, 1);
            }catch (Exception ex){
                System.out.println("ERROR CAPTURA IMG: " + ex.getMessage());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String extension = ".jpg";
        String nameImg = "CAR_" + this.codigo + "_" + String.valueOf(this.contadorImg) + extension;
        if (requestCode == 1 && resultCode == RESULT_OK) {

            //Bundle extras = data.getExtras();
            //Bitmap fotoTemp = (Bitmap) extras.get("data");

            try {
                //util.BitmapToFile(fotoTemp, nameImg);
                this.listImg.add(nameImg);
            }catch (Exception ex){
                System.out.println("ERROR SAVE IMG: " + ex.getMessage());
            }

            renderListImg();
        }else{
            util.deleteFile(nameImg);
        }
    }

    private void previewImg(String file){
        util.viewImg(file,mActivity);
    }

    private void renderListImg(){
        divImg.removeAllViews();
        int contador = 0;
        for(final String item : this.listImg){
            Bitmap foto = util.FileToBitmap(item);
            ImageView imgEvi = new ImageView(mActivity);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200,200);
            params.setMargins(10,10,10,10);
            imgEvi.setLayoutParams(params);
            imgEvi.setImageBitmap(foto);

            final int contadorRmv = contador;
            final DialogConfirmar.eventDialog callback = new DialogConfirmar.eventDialog() {
                @Override
                public void confirmar() {
                    listImg.remove(contadorRmv);
                    util.deleteFile(item);
                    renderListImg();
                }
            };

            imgEvi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    previewImg(item);
                }
            });


            imgEvi.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DialogConfirmar dg = new DialogConfirmar(mActivity,"Confirmar","Desea eliminar la evidencia?");
                    dg.show(callback);
                    return true;
                }
            });

            divImg.addView(imgEvi);
            contador++;
        }
    }

    private View createLabel(String name,String label){
        TextView lblNombreCampo = new TextView(mActivity);

        String strData = name;
        if(label != null && label != "null"){
            if(label.compareTo("") != 0)
                strData += " (" + label + ")";
        }

        lblNombreCampo.setText(strData);
        lblNombreCampo.setTextSize(18f);
        lblNombreCampo.setGravity(Gravity.CENTER);

        lblNombreCampo.setTypeface(util.getFont(mActivity));

        return lblNombreCampo;
    }

    Calendar date;
    public void showDateTimePicker(final TextView lblCampo) {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        lblCampo.setText(util.formatFecha(date.getTime()));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private View createInput(int TypeInput, int TypeDato,JSONArray values){
        /**
         * TypeInput
         * 0 - input
         * 1 - automatico
         *
         * TypeDato
         * 0 - String
         * 1 - Numero
         * 2 - DateTime
         * 3 - lista unica
         * 4 - lista multiple
         * */

        //DATA
        DataModel dataModel = new DataModel();

        //MARGENES GENERALES
        int alto = util.dipPixel(mActivity,50);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,alto);
        params.setMargins(0,10,0,45);

        if(TypeInput == 2){
            //EVIDENCIAS
            LinearLayout divP = new LinearLayout(mActivity);
            divP.setOrientation(LinearLayout.VERTICAL);
            divP.setGravity(Gravity.CENTER_HORIZONTAL);

            RelativeLayout.LayoutParams paramsEvi = new RelativeLayout.LayoutParams(300,300);
            paramsEvi.setMargins(0,5,0,5);
            ImageView btnEvi = new ImageView(mActivity);
            btnEvi.setClickable(true);
            btnEvi.setLayoutParams(paramsEvi);
            util.setBackgroundView(mActivity,btnEvi,R.drawable.ic_btn_evidencias);
            btnEvi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    capturarEvidencia();
                }
            });

            HorizontalScrollView scroll = new HorizontalScrollView(mActivity);
            scroll.addView(divImg);

            divP.addView(btnEvi);
            divP.addView(scroll);

            dataModel.tipo = "EVIDENCIA";
            listaData.add(dataModel);

            return divP;
        }

        if(TypeInput == 1){
            if(TypeDato == 2){
                LinearLayout divPadre = new LinearLayout(mActivity);
                divPadre.setOrientation(LinearLayout.HORIZONTAL);

                ImageView iconEdit = new ImageView(mActivity);
                LinearLayout.LayoutParams paramsPadre = new LinearLayout.LayoutParams(100,100);
                paramsPadre.setMargins(0,0,10,0);
                iconEdit.setLayoutParams(paramsPadre);
                util.setBackgroundView(mActivity,iconEdit,R.mipmap.ic_edit);

                String fechaActual = util.fechaActual();
                final TextView lblCampo = new TextView(mActivity);
                lblCampo.setText(fechaActual);
                lblCampo.setTextSize(16f);
                lblCampo.setGravity(Gravity.CENTER);

                dataModel.tipo = "LBL";
                dataModel.lbl = lblCampo;
                listaData.add(dataModel);

                lblCampo.setTypeface(util.getFont(mActivity));

                divPadre.setClickable(true);
                divPadre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateTimePicker(lblCampo);
                    }
                });

                params.setMargins(0,0,0,45);
                divPadre.setGravity(Gravity.CENTER);
                divPadre.setLayoutParams(params);
                divPadre.setPadding(0,0,100,0);
                divPadre.addView(iconEdit);
                divPadre.addView(lblCampo);

                return divPadre;
            }
        }

        int inputType = InputType.TYPE_CLASS_TEXT;
        if(TypeDato == 1) inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;

        if(TypeDato == 0 || TypeDato == 1){
            //EDIT TEXT
            EditText txtData = new EditText(mActivity);
            txtData.setInputType(inputType);
            txtData.setLayoutParams(params);
            txtData.setGravity(Gravity.CENTER);

            dataModel.tipo = "TXT";
            dataModel.txt = txtData;
            listaData.add(dataModel);

            txtData.setTypeface(util.getFont(mActivity));

            return txtData;
        }else if(TypeDato == 2){
            //DATE TIME
        }else if(TypeDato == 3){
            //SPINNER
            String[] temp = new String[]{};
            if(values != null){
                try{
                    temp = new String[values.length()];
                    for(int i = 0; i < values.length(); i++){
                        temp[i] = values.getString(i);
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item, temp);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner cbox = new Spinner(mActivity);
                    cbox.setAdapter(dataAdapter);
                    cbox.setLayoutParams(params);

                    dataModel.tipo = "CBOX";
                    dataModel.cbox = cbox;
                    listaData.add(dataModel);

                    //cbox.setTypeface(util.getFont(mActivity));

                    return cbox;
                }catch (Exception ex){
                    System.out.println("EROR AL CREAR SPINNER " + ex.getMessage());
                }
            }
        }else if(TypeDato == 4){
            //CHECKBOX
            GridLayout grid = new GridLayout(mActivity);
            grid.setColumnCount(2);
            grid.setColumnOrderPreserved(true);
            GridLayout.LayoutParams layoutParams = null;
            /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                layoutParams = new GridLayout.LayoutParams();
                //layoutParams.width = 0;
                layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED,GridLayout.FILL,1f);
                layoutParams.setMargins(0,10,0,45);
                grid.setLayoutParams(layoutParams);
            }else{
                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,10,0,45);
                grid.setLayoutParams(params);
            }*/
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,10,0,45);
            grid.setLayoutParams(params);
            List<CheckBox> listaCheck = new ArrayList<>();
            if(values != null){
                try{
                    for(int i = 0; i < values.length(); i++){
                        String strData = values.getString(i);
                        CheckBox check = new CheckBox(mActivity);
                        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(40,0,10,0);
                        check.setLayoutParams(params);
                        check.setText(strData);
                        grid.addView(check);

                        check.setTypeface(util.getFont(mActivity));

                        listaCheck.add(check);
                    }
                }catch (Exception ex){
                    System.out.println("EROR AL CREAR CHECKBOX " + ex.getMessage());
                }
            }
            dataModel.tipo = "LISTA";
            dataModel.listaCheck = listaCheck;
            listaData.add(dataModel);
            return grid;
        }
        return new View(mActivity);
    }

    private void cargarData(){
        JSONArray campos = ((DiligenciarFormatoActivity) mActivity).campos;
        for(int i = 0; i < campos.length(); i++) {
            try {
                JSONObject obj = campos.getJSONObject(i);
                if(obj.has("Data")){
                    int TypeDato = obj.getInt("TypeDato");
                    int TypeInput = obj.getInt("TypeInput");
                    DataModel dataModel = listaData.get(i);
                    switch (util.getTipoCampo(TypeInput,TypeDato)){
                        case "EVIDENCIA":
                            System.out.println("LISTA STRING: " + obj.getString("Data"));
                            String[] temp = obj.getString("Data").split(",");
                            for(String item : temp){
                                listImg.add(item);
                            }
                            renderListImg();
                            break;
                        case "LBL":
                            TextView lbl = dataModel.lbl;
                            lbl.setText(obj.getString("Data"));
                            break;
                        case "TXT":
                            EditText txt = dataModel.txt;
                            txt.setText(obj.getString("Data"));
                            break;
                        case "CBOX":
                            try{
                                Spinner cbox = dataModel.cbox;
                                JSONArray vec = new JSONArray(obj.getString("IsValues"));
                                for(int indice = 0; indice < vec.length(); indice++){
                                    if(vec.getString(indice).compareTo(obj.getString("Data")) == 0){
                                        cbox.setSelection(indice);
                                        break;
                                    }
                                }
                            }catch (Exception ex){
                                System.out.println("ERROR AL CARGAR DATA CBOX: " + ex.getMessage());
                            }
                            break;
                        case "LISTA":
                            if(dataModel.listaCheck != null){
                                for(CheckBox item : dataModel.listaCheck){
                                    /*JSONArray vec = new JSONArray(obj.getString("Data"));
                                    for(int indice = 0; indice < vec.length(); indice++){
                                        String data = vec.getString(indice);
                                        if(data.compareTo(item.getText().toString()) == 0){
                                            item.setChecked(true);
                                        }
                                    }*/
                                    String[] vec = obj.getString("Data").split(",");
                                    for(String strItem : vec){
                                        if(strItem.compareTo(item.getText().toString()) == 0){
                                            item.setChecked(true);
                                        }
                                    }
                                }
                            }
                            break;
                    }
                }
            } catch (Exception ex) {}
        }
    }

    private boolean validarFormulario(){
        boolean banValidar = false;
        JSONArray campos = ((DiligenciarFormatoActivity) mActivity).campos;
        for(int i = 0; i < campos.length(); i++) {
            try{
                JSONObject obj = campos.getJSONObject(i);
                int TypeDato = obj.getInt("TypeDato");
                int TypeInput = obj.getInt("TypeInput");
                DataModel dataModel = listaData.get(i);
                switch (util.getTipoCampo(TypeInput,TypeDato)){
                    case "LBL":
                        /*TextView lbl = dataModel.lbl;
                        if(lbl.getText().toString().compareTo("") != 0){
                            banValidar = true;
                            break;
                        }*/
                        break;
                    case "TXT":
                        EditText txt = dataModel.txt;
                        if(txt.getText().toString().compareTo("") != 0){
                            banValidar = true;
                        }
                        break;
                    case "CBOX":
                        try{
                            Spinner cbox = dataModel.cbox;
                            if(cbox.getSelectedItem().toString().compareTo("") != 0){
                                banValidar = true;
                            }
                        }catch (Exception ex){
                            System.out.println("ERROR AL CARGAR DATA CBOX: " + ex.getMessage());
                        }
                        break;
                    case "LISTA":
                        for(int ind = 0; ind < dataModel.listaCheck.size(); ind++){
                            CheckBox item  = dataModel.listaCheck.get(ind);
                            if(item.isChecked()){
                                banValidar = true;
                            }
                        }
                        break;
                }
            }catch (Exception ex){
                System.out.println("ERROR VALIDAR FORMULARIO: " + ex.getMessage());
            }
        }
        return banValidar;
    }

    private void irSiguienteFrg(){
        if(validarFormulario()){
            //LLENAR DATA
            JSONArray campos = ((DiligenciarFormatoActivity) mActivity).campos;
            for(int i = 0; i < campos.length(); i++) {
                try {
                    JSONObject obj = campos.getJSONObject(i);
                    int TypeDato = obj.getInt("TypeDato");
                    int TypeInput = obj.getInt("TypeInput");
                    DataModel dataModel = listaData.get(i);
                    switch (util.getTipoCampo(TypeInput,TypeDato)){
                        case "EVIDENCIA":
                            String dataEvi = "";
                            for(int indice = 0; indice < listImg.size(); indice++){
                                dataEvi += listImg.get(indice);
                                if(indice + 1 < listImg.size())
                                    dataEvi += ",";
                            }
                            obj.put("Data",dataEvi);
                            break;
                        case "LBL":
                            TextView lbl = dataModel.lbl;
                            obj.put("Data",lbl.getText().toString());
                            break;
                        case "TXT":
                            EditText txt = dataModel.txt;
                            obj.put("Data",txt.getText().toString());
                            break;
                        case "CBOX":
                            try{
                                Spinner cbox = dataModel.cbox;
                                obj.put("Data",cbox.getSelectedItem().toString());
                            }catch (Exception ex){
                                System.out.println("ERROR AL CARGAR DATA CBOX: " + ex.getMessage());
                            }
                            break;
                        case "LISTA":
                        /*JSONArray vec = new JSONArray();
                        for(CheckBox item : dataModel.listaCheck){
                            if(item.isChecked()){
                                vec.put(item.getText().toString());
                            }
                        }
                        obj.put("Data",vec.toString());*/
                            String data = "";
                            for(int ind = 0; ind < dataModel.listaCheck.size(); ind++){
                                CheckBox item  = dataModel.listaCheck.get(ind);
                                if(item.isChecked()){
                                    data += item.getText().toString();
                                    if(ind + 1 < dataModel.listaCheck.size()){
                                        data += ",";
                                    }
                                }
                            }
                            obj.put("Data",data);
                            break;
                    }
                } catch (Exception ex) {}
            }


            ((DiligenciarFormatoActivity) mActivity).irDetalle();
        }else{
            AlertDialog.Builder b = new AlertDialog.Builder(mActivity);
            b.setCancelable(true);
            b.setTitle("Advertencia");
            b.setMessage("Debe llenar al menos un dato.");
            b.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog al = b.create();
            al.show();
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
