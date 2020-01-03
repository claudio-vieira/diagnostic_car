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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class LiquidTemperatureActivity extends ActionBarActivity {

    private LineGraphSeries<DataPoint> series;
    private int ultimoX = 0;


    private ConnectBluetooth connect;

    private static final String TEMP_lIQUIDO = Util.COD_TEMP_lIQUIDO;

    private Thread thread;

    public static final int STATE_CONNECTED = 3;  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liquid_temperature);

        GraphView graph = (GraphView) findViewById(R.id.graph_temp_liquido);
        graph.setTitle("Temperatura Líquido Refrigerador");
        graph.setTitleColor(32);
        graph.setBackgroundColor(22);

        series = new LineGraphSeries<>();

        graph.addSeries(series);
        series.setTitle("Temperatura do liq refrigerador");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(-10);
        viewport.setMaxY(130);

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
                        connect.write(TEMP_lIQUIDO);
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

        thread.start();
    }

    @Override
    public void onBackPressed() {
        thread.interrupt();
        super.onBackPressed();
    }

    public void desconectado() {
        Toast.makeText(LiquidTemperatureActivity.this, "Você não está conectado", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = new String(readBuf, 0, msg.arg1);

                    String rpm = readMessage;

                    String[] div_rpm = rpm.split(" ");

                    int temperatura = 0;

                    int primeiro = 0;
                    int pegar = 0;
                    int mostrar = 0;

                    for(String elemento: div_rpm ) {
                        Log.d("Gráficos", elemento);

                        if(elemento.length() == 6){
                            String str = elemento.substring(0,3);
                            if(str.equalsIgnoreCase("4105")){
                                temperatura = Integer.parseInt(elemento.substring(4,5), 16);
                                mostrar = 1;
                            }
                        }
                        if (elemento.length() == 2) {

                            if(pegar == 1) {
                                temperatura = Integer.parseInt(elemento, 16);
                                mostrar = 1;
                            }

                            if(pegar > 0){
                                pegar = 0;
                            }

                            if(primeiro == 41){
                                if(elemento.equalsIgnoreCase("05")){
                                    pegar = 1;
                                }

                                primeiro = 0;
                            }

                            if(elemento.equalsIgnoreCase("41") && pegar == 0 ){
                                primeiro = 41;
                            }
                        }
                    }

                    if(mostrar == 1) {
                        //Formula to conversion of intake air temperature (a - 40)
                        int resultado = temperatura - 40;

                        series.appendData(new DataPoint(ultimoX++, resultado), true, 10);
                    }

                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_liquid_temperature, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
