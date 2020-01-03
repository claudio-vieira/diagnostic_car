package android.com.br.conexaobluetooth.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.com.br.conexaobluetooth.ConnectBluetooth;
import android.com.br.conexaobluetooth.Constants;
import android.com.br.conexaobluetooth.R;
import android.com.br.conexaobluetooth.Util;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class BluetoothMain extends ActionBarActivity {

    private Button bluetooth_adapter;
    private Button dispositivos;
    private Button conectarOBD2;
    private Button bt_send;
    private EditText bt_edit;

    private ConnectBluetooth connect;

    private AlertDialog alerta;

    //Variable to turn on the bluetooth (default > 0)
    static int REQUEST_ENABLE_BT = 1;

    private static final String TAG = "BluetoothMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_main);

        connect = Util.connect;

        bluetooth_adapter = (Button) findViewById(R.id.bluetooth_adapter);
        bluetooth_adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                connect.setmHandler(mHandler);

                if(connect.isOnBluetooth()){
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        });

        //Button to check available devices
        dispositivos = (Button) findViewById(R.id.dispositivos);
        dispositivos.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> pairedDevices = null;

                if(connect.isOnAdapter()){
                    //Get paired devices list
                    pairedDevices = connect.getDispositivos();
                }else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(BluetoothMain.this);
                    dialog.setTitle("Conexões");
                    dialog.setMessage("É necessario ligar o bluetooth");
                    dialog.setNeutralButton("Ok", null);
                    dialog.show();
                }

                if(pairedDevices != null){
                    if (pairedDevices.size() > 0) {

                        for (BluetoothDevice device : pairedDevices) {
                            //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                            AlertDialog.Builder dialog = new AlertDialog.Builder(BluetoothMain.this);
                            dialog.setTitle("Conexões");
                            dialog.setMessage(device.getName() + "\n" + device.getAddress());
                            dialog.setNeutralButton("Ok", null);
                            dialog.show();
                        }
                    }
                }
            }
        });

        conectarOBD2 = (Button) findViewById(R.id.conectar_obd2);
        conectarOBD2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> pairedDevices = null;
                final ArrayList<BluetoothDevice> itens = new ArrayList<>();

                if(connect.isOnAdapter()){
                    pairedDevices = connect.getDispositivos();
                }else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(BluetoothMain.this);
                    dialog.setTitle("Conexões");
                    dialog.setMessage("É necessario ligar o bluetooth");
                    dialog.setNeutralButton("Ok", null);
                    dialog.show();
                }
                if(pairedDevices != null){
                    
                    if (pairedDevices.size() > 0) {

                        itens.addAll(pairedDevices);

                        ArrayAdapter<BluetoothDevice> itensAdapter = new ArrayAdapter<>(BluetoothMain.this, R.layout.item_device, itens);

                        AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothMain.this);
                        builder.setTitle("Escolha um circuito");
                        
                        builder.setSingleChoiceItems(itensAdapter, 0, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                //Toast.makeText(BluetoothMain.this, "posição selecionada=" + arg1, Toast.LENGTH_SHORT).show();
                                connect.connect(itens.get(arg1));
                                
                                Util.device = itens.get(arg1);
                                Util.connect = connect;
                                alerta.dismiss();
                            }
                        });
                        alerta = builder.create();
                        alerta.show();
                    }
                }
            }
        });

        bt_send = (Button) findViewById(R.id.button_send);
        bt_send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                bt_edit = (EditText) findViewById(R.id.edit_text_out);
                Toast.makeText(getApplicationContext(),"Enviando dados: "+bt_edit.getText(),Toast.LENGTH_SHORT).show();

                connect.write(bt_edit.getText());
            }
        });

    }

    /**
     * get information of the return of ConnectBluetooth
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    Log.d(TAG, "Mensagem: "+readMessage);

                    AlertDialog.Builder dialog = new AlertDialog.Builder(BluetoothMain.this);
                    dialog.setTitle("Resultado retornado");
                    dialog.setMessage(readMessage);
                    dialog.setNeutralButton("Ok", null);
                    dialog.show();
                    break;
            }
        }
    };

}
