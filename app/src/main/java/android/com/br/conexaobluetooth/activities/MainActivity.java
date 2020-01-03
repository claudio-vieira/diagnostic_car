package android.com.br.conexaobluetooth.activities;

import android.com.br.conexaobluetooth.ConnectBluetooth;
import android.com.br.conexaobluetooth.R;
import android.com.br.conexaobluetooth.Util;
import android.com.br.conexaobluetooth.dao.TroubleCodesDao;
import android.com.br.conexaobluetooth.model.TroubleCodes;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends ActionBarActivity {

    private ConnectBluetooth connect;
    private static String DB_PATH = "/data/data/android.com.br.conexaobluetooth/databases/";
    private static final String DATABASE_NAME = Util.DataBaseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connect = new ConnectBluetooth();
        Util.connect = connect;

        if(!checkDataBase()){
            //Importar os códigos de falhas do veículo
            //Import trouble codes of vehicle
            importarCDT();
        }
    }

    public boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            System.err.printf("Erro ao encontrar banco de dados",e);
        }

        if(checkDB != null){
            checkDB.close();
        }

        if(checkDB != null){
            System.out.println("EXISTE");
        }
        else{
            System.out.println("não EXISTE");
        }

        return checkDB != null ? true : false;

    }

    /* Import the trouble codes */
    public void importarCDT(){

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("PID2.txt"), "UTF-8"));

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                System.out.println("SAlva no banco");
                String[] dados = mLine.split(";");
                String pid = dados[0];
                String descricao = dados[1];

                inserirDadosBanco(pid, descricao);
            }
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.printf("Erro no fechamento do arquivo: %s.\n", e.getMessage());
                }
            }
        }
    }

    public void inserirDadosBanco(String pid, String descricao){
        TroubleCodes cod_falhas = new TroubleCodes();
        cod_falhas.setPid(pid);
        cod_falhas.setDescricao(descricao);

        TroubleCodesDao dao = TroubleCodesDao.getInstance(this);
        dao.addCodFalha(cod_falhas);
    }

    public void listaCarsActivity(View view) {
        Intent intent = new Intent(this, CarsActivity.class);
        startActivity(intent);
    }

    public void conexaoActivity(View view) {
        Intent intent = new Intent(this, BluetoothMain.class);
        startActivity(intent);
    }

    public void OBDListActivity(View view) {
        Intent intent = new Intent(this, OBDListActivity.class);
        startActivity(intent);
    }

    public void statusApdapterActivity(View view) {
        Intent intent = new Intent(this, StatusAdapter.class);
        startActivity(intent);
    }

    public void codFalhaActivity(View view) {
        Intent intent = new Intent(this, TroubleCodesActivity.class);
        startActivity(intent);
    }

    public void ReportListActivity(View view) {
        Intent intent = new Intent(this, ReportListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
