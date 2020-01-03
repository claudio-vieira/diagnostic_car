package android.com.br.conexaobluetooth.dao;

import android.com.br.conexaobluetooth.PersistenceHelper;
import android.com.br.conexaobluetooth.model.Cars;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Claudio on 23/08/2015.
 */
public class CarsDao {

    private static final String TABELA_CARROS = "carros";
    private static final String ID = "id";
    private static final String NOME = "nome";
    private static final String MARCA = "marca";
    private static final String MODELO = "modelo";
    private static final String SELECIONADO = "selecionado";
    private static final String[] COLUNAS = {ID, NOME, MARCA, MODELO, SELECIONADO};

    private SQLiteDatabase dataBase = null;

    private static CarsDao instance;

    public static CarsDao getInstance(Context context) {
        if(instance == null)
            instance = new CarsDao(context);
        return instance;
    }

    private CarsDao(Context context) {
        PersistenceHelper persistenceHelper = PersistenceHelper.getInstance(context);
        dataBase = persistenceHelper.getWritableDatabase();
    }

    public void addCarro(Cars c) {

        ContentValues valores = new ContentValues();

        valores.put(NOME, c.getNome());
        valores.put(MARCA, c.getMarca());
        valores.put(MODELO, c.getModelo());
        valores.put(SELECIONADO, c.getSelecionado());

        dataBase.insert(TABELA_CARROS, null, valores);
    }

    public Cars getCars(int id) {

        Cursor registros =
                dataBase.query(TABELA_CARROS,
                        COLUNAS, 
                        " id = ?", 
                        new String[] { String.valueOf(id)},
                        null,
                        null,
                        null,
                        null
                );

        if (registros != null)
            registros.moveToFirst();

        Cars c = new Cars();
        c.setId(registros.getInt(0));
        c.setNome(registros.getString(1));
        c.setMarca(registros.getString(2));
        c.setModelo(registros.getString(3));
        c.setSelecionado(registros.getInt(4));

        return c;
    }

    public List<Cars> getTodosCars() {
        List<Cars> carros = new LinkedList<>();
        String query = "SELECT * FROM " + TABELA_CARROS;
        Cursor cursor = dataBase.rawQuery(query, null);
        Cars c = null;
        if (cursor.moveToFirst()) {
            do {
                c = new Cars();
                c.setId(cursor.getInt(0));
                c.setNome(cursor.getString(1));
                c.setMarca(cursor.getString(2));
                c.setModelo(cursor.getString(3));
                c.setSelecionado(cursor.getInt(4));
                carros.add(c);
            } while (cursor.moveToNext());
        }
        return carros;
    }

    public int updateCarrros(Cars c) {
        ContentValues values = new ContentValues();
        values.put(NOME, c.getNome());
        values.put(MARCA, c.getMarca());
        values.put(MODELO, c.getModelo());
        values.put(SELECIONADO, c.getSelecionado());

        int i = dataBase.update(TABELA_CARROS,
                values,
                ID+" = ?",
                new String[] { String.valueOf(c.getId()) });

        return i;
    }

    public int updateCarrros(Cars c, int selecionado) {
        todosSemSelecionar();
        ContentValues values = new ContentValues();
        values.put(NOME, c.getNome());
        values.put(MARCA, c.getMarca());
        values.put(MODELO, c.getModelo());
        values.put(SELECIONADO, selecionado);

        int i = dataBase.update(TABELA_CARROS,
                values,
                ID+" = ?",
                new String[] { String.valueOf(c.getId()) });

        return i;
    }

    public void todosSemSelecionar() {
        dataBase.execSQL("UPDATE " + TABELA_CARROS + " SET selecionado = 0 WHERE selecionado = 1");
    }

    public void deleteCars(Cars c) {
        dataBase.delete(TABELA_CARROS,
                ID + " = ?",
                new String[]{String.valueOf(c.getId())});
    }

    public Cars buscarSelecionado() {

        Cursor registros =
                dataBase.query(TABELA_CARROS,
                        COLUNAS, 
                        " selecionado = 1", 
                        null,
                        null,
                        null,
                        null,
                        null
                );

        Cars c = null;

        if (registros != null && registros.moveToFirst()) {
            c = new Cars();
            c.setId(registros.getInt(0));
            c.setNome(registros.getString(1));
            c.setMarca(registros.getString(2));
            c.setModelo(registros.getString(3));
            c.setSelecionado(registros.getInt(4));
        }

        return c;
    }

}
