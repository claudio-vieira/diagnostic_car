package android.com.br.conexaobluetooth.dao;

import android.com.br.conexaobluetooth.PersistenceHelper;
import android.com.br.conexaobluetooth.model.Cars;
import android.com.br.conexaobluetooth.model.Rpm;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Claudio on 15/11/2015.
 */
public class RpmDao {

    private static final String TABELA_RPM = "rpm";
    private static final String ID = "id";
    private static final String MIN = "min";
    private static final String MAX = "max";
    private static final String ID_CARRO = "id_carro";
    private static final String[] COLUNAS = {ID, MAX, MIN, ID_CARRO};

    private SQLiteDatabase dataBase = null;

    private static RpmDao instance;

    public static RpmDao getInstance(Context context) {
        if(instance == null)
            instance = new RpmDao(context);
        return instance;
    }

    private RpmDao(Context context) {
        PersistenceHelper persistenceHelper = PersistenceHelper.getInstance(context);
        dataBase = persistenceHelper.getWritableDatabase();
    }

    public void addRpm(Rpm c) {

        ContentValues valores = new ContentValues();

        valores.put(MIN, c.getMin());
        valores.put(MAX, c.getMax());
        valores.put(ID_CARRO, c.getId_carro());

        dataBase.insert(TABELA_RPM, null, valores);
    }

    public List<Rpm> getTodosRpm() {
        List<Rpm> rpms = new LinkedList<>();
        String query = "SELECT * FROM " + TABELA_RPM;
        Cursor cursor = dataBase.rawQuery(query, null);
        Rpm c = null;
        if (cursor.moveToFirst()) {
            do {
                c = new Rpm();
                c.setId(cursor.getInt(0));
                c.setMin(cursor.getInt(1));
                c.setMax(cursor.getInt(2));
                c.setId_carro(cursor.getInt(3));
                rpms.add(c);
            } while (cursor.moveToNext());
        }
        return rpms;
    }

    public Rpm getRpmByCarroId(int id) {

        Cursor registros =
                dataBase.query(TABELA_RPM,
                        COLUNAS, 
                        " id_carro = ?", 
                        new String[] { String.valueOf(id)},
                        null,
                        null,
                        null,
                        null
                );
        Rpm rpm = null;

        if (registros != null && registros.moveToFirst()) {
            rpm = new Rpm();
            rpm.setId(registros.getInt(0));
            rpm.setMax(registros.getInt(1));
            rpm.setMin(registros.getInt(2));
            rpm.setId_carro(registros.getInt(3));
        }

        return rpm;
    }

    public void deleteTodosById(Cars c) {
        dataBase.delete(TABELA_RPM,ID_CARRO + " = ?",new String[]{String.valueOf(c.getId())});
    }

}
