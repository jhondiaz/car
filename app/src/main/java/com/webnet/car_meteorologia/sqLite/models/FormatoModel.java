package com.webnet.car_meteorologia.sqLite.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FormatoModel {
    public static String tableName = "Formatos";
    public static String sqlTable = "CREATE TABLE IF NOT EXISTS Formatos " +
            "(Id TEXT PRIMARY KEY, " +
            "IdEstacion TEXT, " +
            "IdObservador TEXT, " +
            "IdFormato TEXT, " +
            "Estado TEXT, " +
            "EstadoImg TEXT, " +
            "Fecha TEXT, " +
            "Data TEXT)";

    public String Id;
    public String IdEstacion;
    public String IdObservador;
    public String IdFormato;
    public String Estado;
    public String EstadoImg;
    public String Fecha;
    public String Data;

    public boolean banVisible = true;

    public void Insertar(SQLiteDatabase db){
        if(db != null){
            ContentValues valores = new ContentValues();
            valores.put("Id", this.Id);
            valores.put("IdEstacion", this.IdEstacion);
            valores.put("IdObservador", this.IdObservador);
            valores.put("IdFormato", this.IdFormato);
            valores.put("Estado", this.Estado);
            valores.put("EstadoImg", this.EstadoImg);
            valores.put("Fecha", this.Fecha);
            valores.put("Data", this.Data);
            db.insert("Formatos", null, valores);
            db.close();
        }
    }

    public void borrar(SQLiteDatabase db){
        db.execSQL("DELETE FROM Formatos");
        db.close();
    }

    public void borrarRegistro(SQLiteDatabase db,String Id){
        db.execSQL("DELETE FROM Formatos WHERE Id = '" + Id + "'");
        db.close();
    }

    public void actualizarEstado(SQLiteDatabase db,String Id,String estado){
        db.execSQL("UPDATE Formatos SET Estado = '"+estado+"' WHERE Id = '" + Id + "'");
        db.close();
    }

    public void actualizarEstadoImg(SQLiteDatabase db,String Id,String estado){
        db.execSQL("UPDATE Formatos SET EstadoImg = '"+estado+"' WHERE Id = '" + Id + "'");
        db.close();
    }

    public ArrayList<FormatoModel> getFormatos(SQLiteDatabase db,String idFormato,String idEstacion){
        ArrayList<FormatoModel> result = new ArrayList<>();
        String str_sql = "SELECT Id,IdEstacion,IdFormato,Estado,Fecha,Data,IdObservador " +
                "FROM Formatos " +
                "WHERE IdFormato = '" + idFormato + "' " +
                "AND IdEstacion = '" + idEstacion + "'";
        Cursor c = db.rawQuery(str_sql,null);
        c.moveToFirst();
        if(c.getCount() == 0 )
            return result;

        FormatoModel temp = new FormatoModel();
        temp.banVisible = false;
        result.add(temp);

        do {
            FormatoModel b = new FormatoModel();
            b.Id = c.getString(0);
            b.IdEstacion = c.getString(1);
            b.IdFormato = c.getString(2);
            b.Estado = c.getString(3);
            b.Fecha = c.getString(4);
            b.Data = c.getString(5);
            b.IdObservador = c.getString(6);
            result.add(b);
        } while (c.moveToNext());
        db.close();
        c.close();
        return result;
    }

    public ArrayList<FormatoModel> getFormatosPorEnviar(SQLiteDatabase db){
        ArrayList<FormatoModel> result = new ArrayList<>();
        String str_sql = "SELECT Id,IdEstacion,IdFormato,Estado,Fecha,Data,IdObservador " +
                "FROM Formatos " +
                "WHERE Estado = 'PENDIENTE'";
        Cursor c = db.rawQuery(str_sql,null);
        c.moveToFirst();
        if(c.getCount() == 0 )
            return result;

        do {
            FormatoModel b = new FormatoModel();
            b.Id = c.getString(0);
            b.IdEstacion = c.getString(1);
            b.IdFormato = c.getString(2);
            b.Estado = c.getString(3);
            b.Fecha = c.getString(4);
            b.Data = c.getString(5);
            b.IdObservador = c.getString(6);
            result.add(b);
        } while (c.moveToNext());
        db.close();
        c.close();
        return result;
    }

    public ArrayList<FormatoModel> getFormatosPorEnviarImg(SQLiteDatabase db){
        ArrayList<FormatoModel> result = new ArrayList<>();
        String str_sql = "SELECT Id,IdEstacion,IdFormato,Estado,Fecha,Data,IdObservador,EstadoImg " +
                "FROM Formatos " +
                "WHERE EstadoImg = 'PENDIENTE'";
        Cursor c = db.rawQuery(str_sql,null);
        c.moveToFirst();
        if(c.getCount() == 0 )
            return result;

        do {
            FormatoModel b = new FormatoModel();
            b.Id = c.getString(0);
            b.IdEstacion = c.getString(1);
            b.IdFormato = c.getString(2);
            b.Estado = c.getString(3);
            b.Fecha = c.getString(4);
            b.Data = c.getString(5);
            b.IdObservador = c.getString(6);
            b.EstadoImg = c.getString(7);
            result.add(b);
        } while (c.moveToNext());
        db.close();
        c.close();
        return result;
    }

    public ArrayList<FormatoModel> getImgAll(SQLiteDatabase db){
        ArrayList<FormatoModel> result = new ArrayList<>();
        String str_sql = "SELECT Id,IdEstacion,IdFormato,Estado,Fecha,Data,IdObservador,EstadoImg " +
                "FROM Formatos ";
        Cursor c = db.rawQuery(str_sql,null);
        c.moveToFirst();
        if(c.getCount() == 0 )
            return result;

        do {
            FormatoModel b = new FormatoModel();
            b.Id = c.getString(0);
            b.IdEstacion = c.getString(1);
            b.IdFormato = c.getString(2);
            b.Estado = c.getString(3);
            b.Fecha = c.getString(4);
            b.Data = c.getString(5);
            b.IdObservador = c.getString(6);
            b.EstadoImg = c.getString(7);
            result.add(b);
        } while (c.moveToNext());
        db.close();
        c.close();
        return result;
    }
}
