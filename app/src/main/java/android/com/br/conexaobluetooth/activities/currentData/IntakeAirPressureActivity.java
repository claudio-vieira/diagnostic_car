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

public class IntakeAirPressureActivity extends ActionBarActivity {

    private LineGraphSeries<DataPoint> series;
    private int ultimoX = 0;

    private ConnectBluetooth connect;

    private static final String TEMP_AR_ADMiSSAO = Util.COD_TEMP_AR_ADMiSSAO;

    private Thread thread;

    public static final int STATE_CONNECTED = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intake_air_temperature);

        GraphView graph = (GraphView) findViewById(R.id.graph_temp_admissao);
        graph.setTitle("Temperatura do Ar de Admissão");
        graph.setTitleColor(32);
        graph.setBackgroundColor(22);

        series = new LineGraphSeries<>();

        graph.addSeries(series);
        series.setTitle("Temperatura do ar");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(-40);
        viewport.setMaxY(215);

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
                        connect.write(TEMP_AR_ADMiSSAO);
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
        Toast.makeText(IntakeAirPressureActivity.this, "Você não está conectado", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

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
                    String rpm = readMessage;
                    //Dividing the string values 
                    String[] div_rpm = rpm.split(" ");

                    int temperatura = 0;

                    int primeiro = 0;
                    int pegar = 0;
                    int mostrar = 0;

                    //Validate the result and add the variables 'a' e 'b'
                    for(String elemento: div_rpm ) {
                        Log.d("TempArAdmissao", elemento);
                        //Case 1 where return comes from "410CXXXX"
                        if(elemento.length() == 6){
                            String str = elemento.substring(0,3);
                            if(str.equalsIgnoreCase("410F")){
                                temperatura = Integer.parseInt(elemento.substring(4,5), 16);
                                mostrar = 1;
                            }
                        }
                        if (elemento.length() == 2) {
                            //Variable "pegar" actives when two comparisons "41" and "0F" appear in loop
                            if(pegar == 1) {
                                temperatura = Integer.parseInt(elemento, 16);
                                mostrar = 1;
                            }

                            //Clean the "pegar"
                            if(pegar > 0){
                                pegar = 0;
                            }

                            //Check with the next parameter after "41" is the "0F"
                            if(primeiro == 41){
                                if(elemento.equalsIgnoreCase("0F")){
                                    pegar = 1;
                                }
                                //Restart to verifying of the first
                                primeiro = 0;
                            }

                            //Check if the 1° parameter is the "41"
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

}
