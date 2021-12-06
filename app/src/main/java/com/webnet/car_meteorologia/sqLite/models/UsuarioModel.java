package com.webnet.car_meteorologia.sqLite.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UsuarioModel {
    public static String tableName = "Usuario";
    public static String sqlTable = "CREATE TABLE IF NOT EXISTS Usuario " +
            "(Id TEXT PRIMARY KEY, " +
            "Name TEXT, " +
            "LastName TEXT, " +
            "Phone TEXT, " +
            "CreateDate TEXT, " +
            "Nit TEXT, " +
            "TypeObserver TEXT, " +
            "Alarmas TEXT, " +
            "Data TEXT)";

    public String Id;
    public String Name;
    public String LastName;
    public String Phone;
    public String CreateDate;
    public String Nit;
    public String TypeObserver;
    public String Alarmas;

    public String Data;

    public void mapearJson(JSONObject obj) throws JSONException {
        if(obj.has("Id"))
            this.Id = obj.getString("Id");

        if(obj.has("Name"))
            this.Name = obj.getString("Name");

        if(obj.has("LastName"))
            this.LastName = obj.getString("LastName");

        if(obj.has("Phone"))
            this.Phone = obj.getString("Phone");

        if(obj.has("CreateDate"))
            this.CreateDate = obj.getString("CreateDate");

        if(obj.has("Nit"))
            this.Nit = obj.getString("Nit");

        if(obj.has("TypeObserver"))
            this.TypeObserver = obj.getString("TypeObserver");

        this.Alarmas = "[]";
    }

    public void mapearData(JSONArray data){
        this.Data = data.toString();
    }

    public void mapearAlarmas(JSONArray data){
        this.Alarmas = data.toString();
    }

    public void Insertar(SQLiteDatabase db){
        if(db != null){
            ContentValues valores = new ContentValues();
            valores.put("Id", this.Id);
            valores.put("Name", this.Name);
            valores.put("LastName", this.LastName);
            valores.put("Phone", this.Phone);
            valores.put("CreateDate", this.CreateDate);
            valores.put("Nit", this.Nit);
            valores.put("Data", this.Data);
            valores.put("Alarmas", this.Alarmas);
            valores.put("TypeObserver", this.TypeObserver);
            db.insert("Usuario", null, valores);
            db.close();
        }
    }

    public void borrar(SQLiteDatabase db){
        db.execSQL("DELETE FROM Usuario");
        db.close();
    }

    public void updateAlarmas(SQLiteDatabase db,String Id){
        db.execSQL("UPDATE Usuario SET Alarmas = '"+Alarmas+"' WHERE Id = '" + Id + "'");
        db.close();
    }

    public void borrarRegistro(SQLiteDatabase db,String Id){
        db.execSQL("DELETE FROM Usuario WHERE Id = '" + Id + "'");
        db.close();
    }

    public UsuarioModel getUsuario(SQLiteDatabase db){
        UsuarioModel result = null;
        String str_sql = "SELECT Id,Name,LastName,Phone,CreateDate,Nit,Data,Alarmas,TypeObserver FROM Usuario";
        Cursor c = db.rawQuery(str_sql,null);
        c.moveToFirst();
        if(c.getCount() == 0 )
            return result;

        result = new UsuarioModel();
        result.Id = c.getString(0);
        result.Name = c.getString(1);
        result.LastName = c.getString(2);
        result.Phone = c.getString(3);
        result.CreateDate = c.getString(4);
        result.Nit = c.getString(5);
        result.Data = c.getString(6);
        result.Alarmas = c.getString(7);
        result.TypeObserver = c.getString(8);
        //result.TypeObserver = "2";
        db.close();
        c.close();
        return result;
    }

}
