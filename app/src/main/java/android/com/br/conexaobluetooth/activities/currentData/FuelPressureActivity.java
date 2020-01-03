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

public class FuelPressureActivity extends ActionBarActivity {

    private LineGraphSeries<DataPoint> series;
    private int ultimoX = 0;

    private ConnectBluetooth connect;

    private static final String PRESAO_COMBUSTIVEL = Util.COD_PRESAO_COMBUSTIVEL;

    private Thread thread;

    public static final int STATE_CONNECTED = 3;  //Device connected state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_pressure);

        GraphView graph = (GraphView) findViewById(R.id.graph_pressao_comb);
        graph.setTitle("Pressão do Combustível");
        graph.setTitleColor(32);
        graph.setBackgroundColor(22);

        series = new LineGraphSeries<>();

        graph.addSeries(series);
        series.setTitle("Pressão do combustível");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(765);

        //connect = new ConnectBluetooth();
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

            //Loop to get the data while the screen is open
            while(!Thread.currentThread().isInterrupted()){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connect.write(PRESAO_COMBUSTIVEL);
                    }
                });

                /*
                    Defining time interval for each entry
                    (This time will be replaced by ELM327 time answer)
                 */
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
        Toast.makeText(FuelPressureActivity.this, "Você não está conectado", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    int pressao = 0;
    int primeiro = 0;
    int pegar = 0;
    int mostrar = 0;

    //Update graphic with Handler variable
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //FragmentActivity activity = getActivity();
            switch (msg.what) {
                //Get the return messagem of EML327
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    //Build a dtring of validated bytes with the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //Get the rpm in hexadecimal
                    String pres = readMessage;
                    //Dividing the string values 
                    String[] div_rpm = pres.split(" ");

                    //Valida o resultado
                    for(String elemento: div_rpm ) {
                        Log.d("Pressao Combustivel", elemento);
                        //Case 1 where return comes from "410AXX"
                        if(elemento.length() == 6){
                            String str = elemento.substring(0,3);
                            if(str.equalsIgnoreCase("1201")){
                                pressao = Integer.parseInt(elemento.substring(4,5), 16);
                                mostrar = 1;
                            }
                        }
                        if (elemento.length() == 2) {
                            //Variable "pegar" actives when two comparisons "41" and "0A" appear in loop
                            if(pegar == 1) {
                                Log.d("Pressao Combustivel", "pegar");
                                pressao = Integer.parseInt(elemento, 16);
                                mostrar = 1;
                            }

                            //Clear the "pegar"
                            if(pegar > 0){
                                pegar = 0;
                            }

                            //Check with the next parameter after "41" is the "0A"
                            if(primeiro == 10){
                                Log.d("Pressao Combustivel", "segundo: "+elemento);
                                if(elemento.equalsIgnoreCase("01")){
                                    pegar = 1;
                                }
                                //Restart to verifying of the first
                                primeiro = 0;
                            }

                            //Check if the 1° parameter is the "41"
                            if(elemento.equalsIgnoreCase("12") && pegar == 0 ){
                                Log.d("Pressao Combustivel", "primeiro: "+elemento);
                                primeiro = 10;
                            }
                        }
                    }

                    //Check if is a valid result
                    if(mostrar == 1) {
                        //There's not formula for fuel pressure
                        int resultado = pressao;

                        //Adding on the graphic
                        series.appendData(new DataPoint(ultimoX++, resultado), true, 10);
                    }

                    break;
            }
        }
    };

}
