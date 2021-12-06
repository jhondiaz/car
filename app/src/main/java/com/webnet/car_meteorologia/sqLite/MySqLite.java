package com.webnet.car_meteorologia.sqLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.webnet.car_meteorologia.sqLite.models.FormatoModel;
import com.webnet.car_meteorologia.sqLite.models.TablaModel;
import com.webnet.car_meteorologia.sqLite.models.UsuarioModel;

import java.util.ArrayList;

/**
 * Created by Gilmar Ocampo Nieves on 22/11/2017.
 */

public class MySqLite extends SQLiteOpenHelper {

    Context mContext;
    ArrayList<TablaModel> listTablas;
    TablaModel tablaModel;

    private static final int bd_version = 4;
    private static final String bd_nombre = "NewAppCar.db";

    public MySqLite(Context context) {
        super(context, bd_nombre, null, bd_version);
        mContext = context;
        listTablas = new ArrayList<>();
        initTables();
    }

    private void initTables(){
        tablaModel = new TablaModel();
        tablaModel.Name = UsuarioModel.tableName;
        tablaModel.TableSql = UsuarioModel.sqlTable;
        listTablas.add(tablaModel);

        tablaModel = new TablaModel();
        tablaModel.Name = FormatoModel.tableName;
        tablaModel.TableSql = FormatoModel.sqlTable;
        listTablas.add(tablaModel);

        onCreate(getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(TablaModel item : listTablas) {
            db.execSQL(item.TableSql);

            //UPDATE COLUMN
            try {
                if (item.Name.compareTo("Usuario") == 0)
                    db.execSQL("ALTER TABLE " + item.Name + " ADD COLUMN TypeObserver TEXT default ''");
            }catch (Exception ex){}
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        for(TablaModel item : listTablas){
            db.execSQL("DROP TABLE IF EXISTS " + item.Name);
        }
        onCreate(db);
    }

    public SQLiteDatabase getDBW(){
        return getWritableDatabase();
    }

    public SQLiteDatabase getDBR(){
        return getReadableDatabase();
    }

    //PROCESOS
    public void VaciarTablas(){
        SQLiteDatabase db = getWritableDatabase();
        for(TablaModel item : listTablas){
            db.execSQL("DELETE FROM " + item.Name);
        }
        db.close();
    }
}
