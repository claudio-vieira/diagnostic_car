package android.com.br.conexaobluetooth.dao;

import android.com.br.conexaobluetooth.PersistenceHelper;
import android.com.br.conexaobluetooth.model.Cars;
import android.com.br.conexaobluetooth.model.CollectorPressure;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Claudio on 15/11/2015.
 */
public class CollectorPressureDao {
    private static final String TABELA_PRESSAO_COLETOR = "pressao_coletor";
    private static final String ID = "id";
    private static final String MIN = "min";
    private static final String MAX = "max";
    private static final String ID_CARRO = "id_carro";
    private static final String[] COLUNAS = {ID, MAX, MIN, ID_CARRO};

    private SQLiteDatabase dataBase = null;

    private static CollectorPressureDao instance;

    public static CollectorPressureDao getInstance(Context context) {
        if(instance == null)
            instance = new CollectorPressureDao(context);
        return instance;
    }

    private CollectorPressureDao(Context context) {
        PersistenceHelper persistenceHelper = PersistenceHelper.getInstance(context);
        dataBase = persistenceHelper.getWritableDatabase();
    }

    public void addCollectorPressure(CollectorPressure c) {

        ContentValues valores = new ContentValues();

        valores.put(MIN, c.getMin());
        valores.put(MAX, c.getMax());
        valores.put(ID_CARRO, c.getId_carro());

        dataBase.insert(TABELA_PRESSAO_COLETOR, null, valores);
    }

    public List<CollectorPressure> getTodasPressoesColetor() {
        List<CollectorPressure> pressao_coletores = new LinkedList<>();
        String query = "SELECT * FROM " + TABELA_PRESSAO_COLETOR;
        Cursor cursor = dataBase.rawQuery(query, null);
        CollectorPressure c = null;
        if (cursor.moveToFirst()) {
            do {
                c = new CollectorPressure();
                c.setId(cursor.getInt(0));
                c.setMin(cursor.getInt(1));
                c.setMax(cursor.getInt(2));
                c.setId_carro(cursor.getInt(3));
                pressao_coletores.add(c);
            } while (cursor.moveToNext());
        }
        return pressao_coletores;
    }

    public CollectorPressure getPressaoByCarro(int id) {

        Cursor registros =
                dataBase.query(TABELA_PRESSAO_COLETOR,
                        COLUNAS, 
                        " id_carro = ?", 
                        new String[] { String.valueOf(id)},
                        null,
                        null,
                        null,
                        null
                );

        CollectorPressure press_col = null;

        if (registros != null && registros.moveToFirst()) {
            press_col = new CollectorPressure();
            press_col.setId(registros.getInt(0));
            press_col.setMax(registros.getInt(1));
            press_col.setMin(registros.getInt(2));
            press_col.setId_carro(registros.getInt(3));
        }

        return press_col;
    }

    public void deleteTodosById(Cars c) {
        dataBase.delete(TABELA_PRESSAO_COLETOR,ID_CARRO + " = ?",new String[]{String.valueOf(c.getId())});
    }

}
