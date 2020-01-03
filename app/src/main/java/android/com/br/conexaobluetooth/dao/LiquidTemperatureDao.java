package android.com.br.conexaobluetooth.dao;

import android.com.br.conexaobluetooth.PersistenceHelper;
import android.com.br.conexaobluetooth.model.Cars;
import android.com.br.conexaobluetooth.model.LiquidTemperature;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Claudio on 15/11/2015.
 */
public class LiquidTemperatureDao {
    private static final String TABELA_TEMP_LIQ = "temp_liq";
    private static final String ID = "id";
    private static final String MIN = "min";
    private static final String MAX = "max";
    private static final String ID_CARRO = "id_carro";
    private static final String[] COLUNAS = {ID, MAX, MIN, ID_CARRO};

    private SQLiteDatabase dataBase = null;

    private static LiquidTemperatureDao instance;

    public static LiquidTemperatureDao getInstance(Context context) {
        if(instance == null)
            instance = new LiquidTemperatureDao(context);
        return instance;
    }

    private LiquidTemperatureDao(Context context) {
        PersistenceHelper persistenceHelper = PersistenceHelper.getInstance(context);
        dataBase = persistenceHelper.getWritableDatabase();
    }

    public void addTempLiq(LiquidTemperature c) {

        ContentValues valores = new ContentValues();

        valores.put(MIN, c.getMin());
        valores.put(MAX, c.getMax());
        valores.put(ID_CARRO, c.getId_carro());

        dataBase.insert(TABELA_TEMP_LIQ, null, valores);
    }

    public List<LiquidTemperature> getTodasTempLiq() {
        List<LiquidTemperature> temp_liquidos = new LinkedList<>();
        String query = "SELECT * FROM " + TABELA_TEMP_LIQ;
        Cursor cursor = dataBase.rawQuery(query, null);
        LiquidTemperature c = null;
        if (cursor.moveToFirst()) {
            do {
                c = new LiquidTemperature();
                c.setId(cursor.getInt(0));
                c.setMin(cursor.getInt(1));
                c.setMax(cursor.getInt(2));
                c.setId_carro(cursor.getInt(3));
                temp_liquidos.add(c);
            } while (cursor.moveToNext());
        }
        return temp_liquidos;
    }

    public LiquidTemperature getPressaoByCarro(int id) {

        Cursor registros =
                dataBase.query(TABELA_TEMP_LIQ,
                        COLUNAS, 
                        " id_carro = ?", 
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        null,
                        null
                );

        LiquidTemperature temp_liq = null;

        if (registros != null && registros.moveToFirst()) {
            temp_liq = new LiquidTemperature();
            temp_liq.setId(registros.getInt(0));
            temp_liq.setMax(registros.getInt(1));
            temp_liq.setMin(registros.getInt(2));
            temp_liq.setId_carro(registros.getInt(3));
        }

        return temp_liq;
    }

    public void deleteTodosById(Cars c) {
        dataBase.delete(TABELA_TEMP_LIQ,ID_CARRO + " = ?",new String[]{String.valueOf(c.getId())});
    }

}
