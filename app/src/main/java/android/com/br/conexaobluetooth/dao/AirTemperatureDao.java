package android.com.br.conexaobluetooth.dao;

import android.com.br.conexaobluetooth.PersistenceHelper;
import android.com.br.conexaobluetooth.model.Cars;
import android.com.br.conexaobluetooth.model.AirTemperature;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Claudio on 15/11/2015.
 */
public class AirTemperatureDao {
    private static final String TABELA_TEMP_AR = "temp_ar";
    private static final String ID = "id";
    private static final String MIN = "min";
    private static final String MAX = "max";
    private static final String ID_CARRO = "id_carro";
    private static final String[] COLUNAS = {ID, MAX, MIN, ID_CARRO};

    private SQLiteDatabase dataBase = null;

    private static AirTemperatureDao instance;

    public static AirTemperatureDao getInstance(Context context) {
        if(instance == null)
            instance = new AirTemperatureDao(context);
        return instance;
    }

    private AirTemperatureDao(Context context) {
        PersistenceHelper persistenceHelper = PersistenceHelper.getInstance(context);
        dataBase = persistenceHelper.getWritableDatabase();
    }

    public void addTempAr(AirTemperature c) {
        ContentValues valores = new ContentValues();

        valores.put(MIN, c.getMin());
        valores.put(MAX, c.getMax());
        valores.put(ID_CARRO, c.getId_carro());

        dataBase.insert(TABELA_TEMP_AR, null, valores);
    }

    public List<AirTemperature> getTodasTempAr() {
        List<AirTemperature> temperaturas_ar = new LinkedList<>();
        String query = "SELECT * FROM " + TABELA_TEMP_AR;
        Cursor cursor = dataBase.rawQuery(query, null);
        AirTemperature c = null;
        if (cursor.moveToFirst()) {
            do {
                c = new AirTemperature();
                c.setId(cursor.getInt(0));
                c.setMin(cursor.getInt(1));
                c.setMax(cursor.getInt(2));
                c.setId_carro(cursor.getInt(3));
                temperaturas_ar.add(c);
            } while (cursor.moveToNext());
        }
        return temperaturas_ar;
    }

    public AirTemperature getPressaoByCarro(int id) {
        Cursor registros =
                dataBase.query(TABELA_TEMP_AR,
                        COLUNAS, 
                        " id_carro = ?", 
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        null,
                        null
                );

        AirTemperature temp_ar = null;

        if (registros != null && registros.moveToFirst()) {
            temp_ar = new AirTemperature();
            temp_ar.setId(registros.getInt(0));
            temp_ar.setMax(registros.getInt(1));
            temp_ar.setMin(registros.getInt(2));
            temp_ar.setId_carro(registros.getInt(3));
        }

        return temp_ar;
    }

    public void deleteTodosById(Cars c) {
        dataBase.delete(TABELA_TEMP_AR,ID_CARRO + " = ?",new String[]{String.valueOf(c.getId())});
    }

}
