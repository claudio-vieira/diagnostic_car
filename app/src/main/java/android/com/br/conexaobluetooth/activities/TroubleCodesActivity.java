package android.com.br.conexaobluetooth.activities;

import android.com.br.conexaobluetooth.ConnectBluetooth;
import android.com.br.conexaobluetooth.Constants;
import android.com.br.conexaobluetooth.R;
import android.com.br.conexaobluetooth.Util;
import android.com.br.conexaobluetooth.adapter.CodAdapter;
import android.com.br.conexaobluetooth.dao.TroubleCodesDao;
import android.com.br.conexaobluetooth.model.TroubleCodes;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TroubleCodesActivity extends ActionBarActivity {

    private ConnectBluetooth connect = null;

    private static final String COD_FALHAS = "03";

    private static final String QTD_COD_FALHAS = "0101";

    private TextView mostrar_cod;

    public static final int STATE_CONNECTED = 3;

    private HashMap hm;
    private CodAdapter codAdap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trouble_codes);

        mostrar_cod = (TextView) findViewById(R.id.mostrar_cod);

        connect = Util.connect;

        connect.setmHandler(mHandler);

        if (connect.getState() == STATE_CONNECTED) {

            connect.write(QTD_COD_FALHAS);
        }else{
            Toast.makeText(TroubleCodesActivity.this, "Você não está conectado", Toast.LENGTH_SHORT).show();
            this.onBackPressed();
        }

        hm = new HashMap();
        hm.put("0","P0");
        hm.put("1","P1");
        hm.put("2","P2");
        hm.put("3","P3");
        hm.put("4","C0");
        hm.put("5","C1");
        hm.put("6","C2");
        hm.put("7","C3");
        hm.put("8","B0");
        hm.put("9","B1");
        hm.put("A","B2");
        hm.put("B","B3");
        hm.put("C","U0");
        hm.put("D","U1");
        hm.put("E","U2");
        hm.put("F","U3");

    }

    public void buscarCodFalha(String cod_falha) {

        TroubleCodesDao dao = TroubleCodesDao.getInstance(this);
        TroubleCodes codFalhas = dao.getCodFalha(cod_falha);


        if(codFalhas != null) {

            List<TroubleCodes> lista = new ArrayList<>();
            lista.add(codFalhas);
            codAdap = new CodAdapter(this, lista);

            ListView lista_adap = (ListView) findViewById(R.id.lista_cod);
            lista_adap.setAdapter(codAdap);
        }else{
            Toast.makeText(TroubleCodesActivity.this, "Código de falha não encontrado no banco de dados", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //Update the Graphic
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //FragmentActivity activity = getActivity();
            switch (msg.what) {
                //Get the retun message of ELM327
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //GEt the rpm in hexadecimal
                    String rpm = readMessage;
                    
                    String[] div_cod = rpm.split(" ");

                    int qtd_cod_falha = 0;
                    String cod_falha = "";

                    int mostrar = 0;
                    String temp;
                    String temp2;

                    //validate the resul
                    if(div_cod[0] == "41" && div_cod[1] == "01") {
                        qtd_cod_falha = Integer.parseInt(div_cod[2]);
                        qtd_cod_falha = qtd_cod_falha - 80;
                        if(qtd_cod_falha > 0){
                            mostrar = 1;
                        }
                    }

                    //retrieve the trouble code
                    if(div_cod[0] == "43") {
                        temp = (String) hm.get(div_cod[1].substring(0,1));
                        temp2 = div_cod[1].substring(1);

                        cod_falha = temp + temp2 + div_cod[2];
                        buscarCodFalha(cod_falha);
                    }

                    if(mostrar == 1) {
                        mostrar_cod.setText(qtd_cod_falha+" encontrados");
                        mostrar_cod.setTextColor(Color.RED);
                        connect.write(COD_FALHAS);
                    }

                    break;
            }
        }
    };

}
