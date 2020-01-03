package android.com.br.conexaobluetooth.dao;

import android.com.br.conexaobluetooth.PersistenceHelper;
import android.com.br.conexaobluetooth.model.TroubleCodes;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Claudio on 04/11/2015.
 */
public class TroubleCodesDao {

    private static final String TABELA_COD_FALHAS = "cod_falhas";
    private static final String ID = "id";
    private static final String PID = "pid";
    private static final String DESCRICAO = "descricao";
    private static final String[] COLUNAS = {ID, PID, DESCRICAO};

    private SQLiteDatabase dataBase = null;

    private static TroubleCodesDao instance;

    public static TroubleCodesDao getInstance(Context context) {
        if(instance == null)
            instance = new TroubleCodesDao(context);
        return instance;
    }

    private TroubleCodesDao(Context context) {
        PersistenceHelper persistenceHelper = PersistenceHelper.getInstance(context);
        dataBase = persistenceHelper.getWritableDatabase();
    }

    public void addCodFalha(TroubleCodes c) {

        ContentValues valores = new ContentValues();

        valores.put(PID, c.getPid());
        valores.put(DESCRICAO, c.getDescricao());

        dataBase.insert(TABELA_COD_FALHAS, null, valores);
    }

    public TroubleCodes getCodFalha(String pid) {

        Cursor registros =
                dataBase.query(TABELA_COD_FALHAS,
                        COLUNAS, 
                        " pid = ?", 
                        new String[] { String.valueOf(pid)},
                        null,
                        null,
                        null,
                        null
                );

        TroubleCodes c = null;

        if (registros != null && registros.moveToFirst()){
            c = new TroubleCodes();
            c.setId(registros.getInt(0));
            c.setPid(registros.getString(1));
            c.setDescricao(registros.getString(2));
        }


        return c;
    }


}
