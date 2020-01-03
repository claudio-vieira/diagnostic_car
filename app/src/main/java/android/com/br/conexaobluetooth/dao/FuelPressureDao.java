package android.com.br.conexaobluetooth.dao;

import android.com.br.conexaobluetooth.PersistenceHelper;
import android.com.br.conexaobluetooth.model.Cars;
import android.com.br.conexaobluetooth.model.FuelPressure;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Claudio on 15/11/2015.
 */
public class FuelPressureDao {
    private static final String TABELA_PRESSAO_COMB = "pressao_comb";
    private static final String ID = "id";
    private static final String MIN = "min";
    private static final String MAX = "max";
    private static final String ID_CARRO = "id_carro";
    private static final String[] COLUNAS = {ID, MAX, MIN, ID_CARRO};

    private SQLiteDatabase dataBase = null;

    private static FuelPressureDao instance;

    public static FuelPressureDao getInstance(Context context) {
        if(instance == null)
            instance = new FuelPressureDao(context);
        return instance;
    }

    private FuelPressureDao(Context context) {
        PersistenceHelper persistenceHelper = PersistenceHelper.getInstance(context);
        dataBase = persistenceHelper.getWritableDatabase();
    }

    public void addPressaoComb(FuelPressure c) {
        ContentValues valores = new ContentValues();

        valores.put(MIN, c.getMin());
        valores.put(MAX, c.getMax());
        valores.put(ID_CARRO, c.getId_carro());

        dataBase.insert(TABELA_PRESSAO_COMB, null, valores);
    }

    public List<FuelPressure> getTodasPressoesComb() {
        List<FuelPressure> pressoes_ombustiveis = new LinkedList<>();
        String query = "SELECT * FROM " + TABELA_PRESSAO_COMB;
        Cursor cursor = dataBase.rawQuery(query, null);
        FuelPressure c = null;
        if (cursor.moveToFirst()) {
            do {
                c = new FuelPressure();
                c.setId(cursor.getInt(0));
                c.setMin(cursor.getInt(1));
                c.setMax(cursor.getInt(2));
                c.setId_carro(cursor.getInt(3));
                pressoes_ombustiveis.add(c);
            } while (cursor.moveToNext());
        }
        return pressoes_ombustiveis;
    }

    public FuelPressure getPressaoByCarro(int id) {

        Cursor registros =
                dataBase.query(TABELA_PRESSAO_COMB,
                        COLUNAS, 
                        " id_carro = ?", 
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        null,
                        null
                );

        FuelPressure press_comb = null;

        if (registros != null && registros.moveToFirst()) {
            press_comb = new FuelPressure();
            press_comb.setId(registros.getInt(0));
            press_comb.setMax(registros.getInt(1));
            press_comb.setMin(registros.getInt(2));
            press_comb.setId_carro(registros.getInt(3));
        }

        return press_comb;
    }

    public void deleteTodosById(Cars c) {
        dataBase.delete(TABELA_PRESSAO_COMB,ID_CARRO + " = ?",new String[]{String.valueOf(c.getId())});
    }

}
