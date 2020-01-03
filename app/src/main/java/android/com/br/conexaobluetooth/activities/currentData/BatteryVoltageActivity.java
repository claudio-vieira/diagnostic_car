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

public class BatteryVoltageActivity extends ActionBarActivity {

    private LineGraphSeries<DataPoint> series;
    private int ultimoX = 0;

    private ConnectBluetooth connect;

    private static final String VOLTAGEM_BATERIA = Util.COD_VOLTAGEM_BATERIA;

    public static final int STATE_CONNECTED = 3;  //Device connected state

    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_voltage);

        //Get Graphic
        GraphView graph = (GraphView) findViewById(R.id.graph_voltagem_bateria);
        graph.setTitle("Voltagem da Bateria");
        graph.setTitleColor(32);
        graph.setBackgroundColor(22);

        //Creating the line graphic type
        series = new LineGraphSeries<>();
        //Adding graphic
        graph.addSeries(series);
        series.setTitle("Voltagem da bateria");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        //Defining viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(20);

        //Get existence connection
        connect = Util.connect;

        //Passing the attribute that will get the answer of integrated circle
        connect.setmHandler(mHandler);

        if(connect != null) {
            if (connect.getState() != STATE_CONNECTED) {
                desconectado();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                //Loop to get the data while the screen is open
                while(!Thread.currentThread().isInterrupted()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connect.write(VOLTAGEM_BATERIA);
                            Log.d("Voltagem bateria",": passou");
                        }
                    });

                    try {
                        //Get data on half second each
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    public void onBackPressed() {
        thread.interrupt();
        super.onBackPressed();
    }

    public void desconectado() {
        Toast.makeText(BatteryVoltageActivity.this, "Você não está conectado", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    //Update graphic with Handler variable
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d("Voltagem bateria", ": " + readMessage.replaceAll("[^0-9]", ""));

                    String str = readMessage.replaceAll("[^0-9]", "");
                    int num = 0;
                    if(str.length() > 1){
                        num = Integer.parseInt(str.substring(0,2));
                        Log.d("Voltagem bateria", ": " + num);

                        //Adding in the graphic
                        series.appendData(new DataPoint(ultimoX++, num), true, 10);
                    }

                    break;
            }
        }
    };

}
