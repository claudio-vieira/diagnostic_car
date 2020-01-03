package android.com.br.conexaobluetooth.activities.currentData;

import android.com.br.conexaobluetooth.ConnectBluetooth;
import android.com.br.conexaobluetooth.Constants;
import android.com.br.conexaobluetooth.R;
import android.com.br.conexaobluetooth.Util;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class RPMActivity extends ActionBarActivity {

    private LineGraphSeries<DataPoint> series;
    private static final Random RANDOM = new Random();
    private int ultimoX = 0;

    private ConnectBluetooth connect;

    private static final String RPM = Util.COD_RPM;
    public static final int STATE_CONNECTED = 3;  //Estado do dispositivo conectado
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpm);

        GraphView graph = (GraphView) findViewById(R.id.graph_rpm);
        graph.setTitle("Rotações por minuto");
        graph.setTitleColor(32);
        graph.setBackgroundColor(22);


        series = new LineGraphSeries<>();

        graph.addSeries(series);
        series.setTitle("Rotações por minuto");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(6000);

        connect = Util.connect;

        connect.setmHandler(mHandler);

        if(connect != null) {
            if (connect.getState() != STATE_CONNECTED) {
                desconectado();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {

            while(!Thread.currentThread().isInterrupted()){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connect.write(RPM);
                    }
                });

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            }
        });
        System.out.println("iniciando thread");
        thread.start();
    }

    @Override
    public void onBackPressed() {
        thread.interrupt();
        super.onBackPressed();
    }

    public void desconectado() {
        Toast.makeText(RPMActivity.this,"Você não está conectado",Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;

                    String writeMessage = new String(writeBuf);

                    break;

                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = new String(readBuf, 0, msg.arg1);

                    String rpm = readMessage;
                    if(readMessage.contains("UNABLE TO CONNECT")){
                        desconectado();
                        break;
                    }
                    Log.d("Resposta: ", readMessage);

                    String[] div_rpm = rpm.split(" ");

                    int a = -1;
                    int b = -1;

                    int primeiro = 0;
                    int pegar = 0;

                    for(String elemento: div_rpm ) {
                        Log.d("RPM: ", elemento);

                        //Case 1 where return comes from "410CXXXX"
                        if(elemento.length() == 8){
                            String str = elemento.substring(0,3);
                            if(str.equalsIgnoreCase("410C")){
                                a = Integer.parseInt(elemento.substring(4,5), 16);
                                b = Integer.parseInt(elemento.substring(6,7), 16);
                            }
                        }
                        //Case 2 where return comes from "41 0C XX XX"
                        else if (elemento.length() == 2) {
                            //Variable "pegar" actives when two comparisons "41" and "0C" appear in loop
                            if(pegar == 2) {
                                a = Integer.parseInt(elemento, 16);
                            }else if(pegar == 1){
                                b = Integer.parseInt(elemento, 16);
                            }

                            if(pegar > 0){
                                pegar--;
                            }

                            if(primeiro == 41){
                                if(elemento.equalsIgnoreCase("0C")){
                                    pegar = 2;
                                }

                                primeiro = 0;
                            }

                            if(elemento.equalsIgnoreCase("41") && pegar == 0 ){
                                primeiro = 41;
                            }
                        }
                    }

                    if(a >= 0 && b >= 0) {
                        //Formula to conversion of RPM ((a * 256) + B) / 4
                        int resultado = ((a * 256) + b) / 4;

                        series.appendData(new DataPoint(ultimoX++, resultado), true, 10);

                        a = -1;
                        b = -1;
                    }

                    break;
            }
        }
    };

}
