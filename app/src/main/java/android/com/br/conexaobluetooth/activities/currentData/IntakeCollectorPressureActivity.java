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

public class IntakeCollectorPressureActivity extends ActionBarActivity {

    private LineGraphSeries<DataPoint> series;
    private int ultimoX = 0;

    private ConnectBluetooth connect;

    private static final String PRESAO_COLETOR_ADMISSAO = Util.COD_PRESAO_COLETOR_ADMISSAO;

    private Thread thread;

    public static final int STATE_CONNECTED = 3;  //Estado do dispositivo conectado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intake_collector_pressure);

        GraphView graph = (GraphView) findViewById(R.id.graph_pressao_coletor_admissao);
        graph.setTitle("Pressão do Coletor de Admissão");
        graph.setTitleColor(32);
        graph.setBackgroundColor(22);

        series = new LineGraphSeries<>();

        graph.addSeries(series);
        series.setTitle("Pressão do coletor admissão");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(255);

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
    public void onResume(){
        super.onResume();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while(!Thread.currentThread().isInterrupted()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {connect.write(PRESAO_COLETOR_ADMISSAO);
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
        Toast.makeText(IntakeCollectorPressureActivity.this, "Você não está conectado", Toast.LENGTH_SHORT).show();
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

                    int pressao = 0;

                    int primeiro = 0;
                    int pegar = 0;
                    int mostrar = 0;


                    for(String elemento: div_rpm ) {
                        Log.d("Pressão Coletor Adm", elemento);

                        if(elemento.length() == 6){
                            String str = elemento.substring(0,3);
                            if(str.equalsIgnoreCase("410B")){
                                pressao = Integer.parseInt(elemento.substring(4,5), 16);
                                mostrar = 1;
                            }
                        }
                        if (elemento.length() == 2) {

                            if(pegar == 1) {
                                pressao = Integer.parseInt(elemento, 16);
                                mostrar = 1;
                            }


                            if(pegar > 0){
                                pegar = 0;
                            }


                            if(primeiro == 41){
                                if(elemento.equalsIgnoreCase("0B")){
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

                        int resultado = pressao;

                        series.appendData(new DataPoint(ultimoX++, resultado), true, 10);
                    }

                    break;
            }
        }
    };

}
