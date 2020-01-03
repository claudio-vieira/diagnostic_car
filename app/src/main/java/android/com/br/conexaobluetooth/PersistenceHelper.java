package android.com.br.conexaobluetooth;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Claudio on 06/11/2015.
 */
public class PersistenceHelper extends SQLiteOpenHelper {

    public static final String TAG = "PersistenceHelper";

    public static final String SCRIPT_CRIACAO_TABELA_CARROS =
            "CREATE TABLE carros ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nome TEXT, " +
            "marca TEXT, " +
            "modelo TEXT, " +
            "selecionado INTEGER DEFAULT 0 )";

    public static final String SCRIPT_CRIACAO_TABELA_COD_FALHAS =
            "CREATE TABLE cod_falhas ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "pid TEXT, " +
                    "descricao TEXT); ";

    public static final String SCRIPT_CRIACAO_TABELA_RPM =
            "CREATE TABLE rpm ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "min INTEGER, " +
                    "max INTEGER, " +
                    "id_carro INTEGER); ";

    public static final String SCRIPT_CRIACAO_TABELA_COLETOR_ADMISSAO =
            "CREATE TABLE pressao_coletor ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "min INTEGER, " +
                    "max INTEGER, " +
                    "id_carro INTEGER); ";

    public static final String SCRIPT_CRIACAO_TABELA_PRESS_COMB =
            "CREATE TABLE pressao_comb ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "min INTEGER, " +
                    "max INTEGER, " +
                    "id_carro INTEGER); ";

    public static final String SCRIPT_CRIACAO_TABELA_TEMP_AR =
            "CREATE TABLE temp_ar ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "min INTEGER, " +
                    "max INTEGER, " +
                    "id_carro INTEGER); ";

    public static final String SCRIPT_CRIACAO_TABELA_TEMP_LIQ =
            "CREATE TABLE temp_liq ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "min INTEGER, " +
                    "max INTEGER, " +
                    "id_carro INTEGER); ";

    public static final String SCRIPT_DELECAO_TABELA_CARROS =  "DROP TABLE IF EXISTS carros";

    public static final String SCRIPT_DELECAO_TABELA_RPM = "DROP TABLE IF EXISTS rpm";
    public static final String SCRIPT_DELECAO_TABELA_COLETOR_ADMISSAO = "DROP TABLE IF EXISTS pressao_coletor";
    public static final String SCRIPT_DELECAO_TABELA_PRESS_COMB =  "DROP TABLE IF EXISTS pressao_comb";
    public static final String SCRIPT_DELECAO_TABELA_TEMP_AR =  "DROP TABLE IF EXISTS temp_ar";
    public static final String SCRIPT_DELECAO_TABELA_TEMP_LIQ =  "DROP TABLE IF EXISTS temp_liq";


    private static PersistenceHelper instance;

    private static final String DATABASE_NAME = Util.DataBaseName;
    public static final int VERSAO = 1;

    private PersistenceHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        System.out.println("Criando Tabelas");

        db.beginTransaction();
        try {
            db.execSQL(SCRIPT_CRIACAO_TABELA_CARROS);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.beginTransaction();
        try {
            db.execSQL(SCRIPT_CRIACAO_TABELA_COD_FALHAS);
            db.setTransactionSuccessful();
            System.out.println("script: " + SCRIPT_CRIACAO_TABELA_CARROS);
        } finally {
            db.endTransaction();
        }

        db.beginTransaction();
        try {
            db.execSQL(SCRIPT_CRIACAO_TABELA_RPM);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.beginTransaction();
        try {
            db.execSQL(SCRIPT_CRIACAO_TABELA_COLETOR_ADMISSAO);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.beginTransaction();
        try {
            db.execSQL(SCRIPT_CRIACAO_TABELA_PRESS_COMB);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.beginTransaction();
        try {
            db.execSQL(SCRIPT_CRIACAO_TABELA_TEMP_AR);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.beginTransaction();
        try {
            db.execSQL(SCRIPT_CRIACAO_TABELA_TEMP_LIQ);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SCRIPT_DELECAO_TABELA_CARROS);
        db.execSQL(SCRIPT_DELECAO_TABELA_RPM);
        db.execSQL(SCRIPT_DELECAO_TABELA_COLETOR_ADMISSAO);
        db.execSQL(SCRIPT_DELECAO_TABELA_PRESS_COMB);
        db.execSQL(SCRIPT_DELECAO_TABELA_TEMP_AR);
        db.execSQL(SCRIPT_DELECAO_TABELA_TEMP_LIQ);

        this.onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.d(TAG, "Opening the database... " + db.getPath() + " version " + db.getVersion());
    }

    public static PersistenceHelper getInstance(Context context) {
        if(instance == null)
            instance = new PersistenceHelper(context);

        return instance;
    }
}
