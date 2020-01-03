package android.com.br.conexaobluetooth.activities;

import android.com.br.conexaobluetooth.ConnectBluetooth;
import android.com.br.conexaobluetooth.Constants;
import android.com.br.conexaobluetooth.R;
import android.com.br.conexaobluetooth.Util;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class StatusAdapter extends ActionBarActivity {

    private static final String TAG = "StatusAdapter";

    private ConnectBluetooth connect = null;
    private Thread thread;
    private TextView status_bluetooth;
    private TextView version_adapter;
    private TextView connect_adapter;
    private TextView protocol_obd;
    private TextView connect_ecu;
    private static final String VERSION_CIRCUIT = "AT I";   
    private static final String CURRENT_PROTOCOL = "AT DP"; 
    private static final String RPM = "010C";
    public static final int STATE_CONNECTED = 3;            

    public Button btn_teste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_adapter);

        connect = Util.connect;
        connect.setmHandler(mHandler);

        status_bluetooth = (TextView) findViewById(R.id.state_bluetooth);
        connect_adapter = (TextView) findViewById(R.id.connect_adapter);
        version_adapter = (TextView) findViewById(R.id.version_adapter);
        protocol_obd = (TextView) findViewById(R.id.protocol_obd);
        connect_ecu = (TextView) findViewById(R.id.connect_ecu);

        if(connect.isOnBluetooth()) {
            status_bluetooth.setText("Desligado");
            status_bluetooth.setTextColor(Color.RED);
        }else{
            status_bluetooth.setText("Ligado");
            status_bluetooth.setTextColor(Color.GREEN);
        }

        if (connect.getState() == STATE_CONNECTED) {
            connect_adapter.setText("Connectado");
            connect_adapter.setTextColor(Color.GREEN);
        } else {
            connect_adapter.setText("Não connectado");
            connect_adapter.setTextColor(Color.RED);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                int troca = 0;

                while(!Thread.currentThread().isInterrupted()){
                    final int finalTroca = troca;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(finalTroca == 1){
                                Log.d(TAG, "enviando: "+VERSION_CIRCUIT);
                                connect.write(VERSION_CIRCUIT);
                            }
                            else if(finalTroca == 2){
                                Log.d(TAG, "enviando: "+RPM);
                                connect.write(RPM);
                            }

                            else {
                                Log.d(TAG, "enviando: "+CURRENT_PROTOCOL);
                                connect.write(CURRENT_PROTOCOL);
                            }
                        }

                    });

                    if(troca == 0) troca = 1;
                    else if(troca == 1) troca = 2;
                    else troca = 0;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });

        if(connect != null) {
            if (connect.getState() == STATE_CONNECTED) {
                thread.start();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(connect != null) {
            thread.interrupt();
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = new String(readBuf, 0, msg.arg1);

                    Log.d(TAG, "messagess: " + readMessage);

                    /*
                        Os valores padrão de retorno são "at i ELM327 vX.X >"
                     */
                    
                    String[] resposta = readMessage.split(" ");

                    System.out.println("resposta status: "+readMessage);

                    if(readMessage.contains("TO, ISO") && readMessage.length() > 10) {
                        protocol_obd.setText(readMessage.substring(8));
                        protocol_obd.setTextColor(Color.GREEN);
                    }

                    if(resposta != null && resposta.length > 2){
                        if(resposta[0].equalsIgnoreCase("at") && resposta[1].substring(0,1).equalsIgnoreCase("i")){
                            if(resposta[2].length() > 4) {
                                version_adapter.setText(resposta[2].substring(0, 4));
                                version_adapter.setTextColor(Color.GREEN);
                            }
                        }
                        else if(resposta[0].equalsIgnoreCase("41") && resposta[1].equalsIgnoreCase("0C")){
                            connect_ecu.setText("Conectado");
                            connect_ecu.setTextColor(Color.GREEN);
                        }
                    }

                    break;
            }
        }
    };

}
