package android.com.br.conexaobluetooth.activities;

import android.com.br.conexaobluetooth.ConnectBluetooth;
import android.com.br.conexaobluetooth.Constants;
import android.com.br.conexaobluetooth.R;
import android.com.br.conexaobluetooth.Util;
import android.com.br.conexaobluetooth.dao.CarsDao;
import android.com.br.conexaobluetooth.dao.CollectorPressureDao;
import android.com.br.conexaobluetooth.dao.FuelPressureDao;
import android.com.br.conexaobluetooth.dao.RpmDao;
import android.com.br.conexaobluetooth.dao.AirTemperatureDao;
import android.com.br.conexaobluetooth.dao.LiquidTemperatureDao;
import android.com.br.conexaobluetooth.model.Cars;
import android.com.br.conexaobluetooth.model.CollectorPressure;
import android.com.br.conexaobluetooth.model.FuelPressure;
import android.com.br.conexaobluetooth.model.Rpm;
import android.com.br.conexaobluetooth.model.AirTemperature;
import android.com.br.conexaobluetooth.model.LiquidTemperature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class ReportActivity extends ActionBarActivity {

    private LineGraphSeries<DataPoint> series_rpm;
    private LineGraphSeries<DataPoint> series_pres_coletor_ad;
    private LineGraphSeries<DataPoint> series_pres_comb;
    private LineGraphSeries<DataPoint> series_temp_ar_ad;
    private LineGraphSeries<DataPoint> series_temp_liq;

    private int ultimoX = 0;

    private static final String[] CODIGOS_LEITURA = Util.CODIGOS_LEITURA;   
    public static final int STATE_CONNECTED = 3;                            
    private ConnectBluetooth connect;                                       
    private Thread thread;                                                  

    private int rpm_min = 0;
    private int rpm_max = 0;
    private int col_ad_min = 0;
    private int col_ad_max = 0;
    private int comb_min = 0;
    private int comb_max = 0;
    private int temp_ar_min = 0;
    private int temp_ar_max = 0;
    private int temp_liq_min = 0;
    private int temp_liq_max = 0;

    private TextView text_rpm_min;
    private TextView text_rpm_max;
    private TextView text_col_min;
    private TextView text_col_max;
    private TextView text_comb_min;
    private TextView text_comb_max;
    private TextView text_temp_ar_min;
    private TextView text_temp_ar_max;
    private TextView text_temp_liq_min;
    private TextView text_temp_liq_max;

    private Cars carro_atual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        connect = Util.connect;

        connect.setmHandler(mHandler);

        if(connect != null) {
            if (connect.getState() != STATE_CONNECTED) {
                Toast.makeText(ReportActivity.this, "Você não está conectado", Toast.LENGTH_SHORT).show();
                super.onBackPressed();
            }
        }

        CarsDao carro_dao = CarsDao.getInstance(this);

        carro_atual = carro_dao.buscarSelecionado();

        if(carro_atual == null){
            Toast.makeText(ReportActivity.this, "Favor selecionar um veículo", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }

        GraphView graph_rpm = (GraphView) findViewById(R.id.graph_rel_rpm);
        graph_rpm.setTitle("Relatório RPM");
        graph_rpm.setTitleColor(32);
        graph_rpm.setBackgroundColor(22);

        series_rpm = new LineGraphSeries<>();

        graph_rpm.addSeries(series_rpm);

        Viewport viewport_rpm = graph_rpm.getViewport();
        viewport_rpm.setYAxisBoundsManual(true);
        viewport_rpm.setMinY(0);
        viewport_rpm.setMaxY(4000);

        GraphView graph_col = (GraphView) findViewById(R.id.graph_rel_press_col);
        graph_col.setTitle("Relatório Coletor de Admissão");
        graph_col.setTitleColor(32);
        graph_col.setBackgroundColor(22);

        series_pres_coletor_ad = new LineGraphSeries<>();

        graph_col.addSeries(series_pres_coletor_ad);

        Viewport viewport_col = graph_col.getViewport();
        viewport_col.setYAxisBoundsManual(true);
        viewport_col.setMinY(0);
        viewport_col.setMaxY(255);

        GraphView graph_comb = (GraphView) findViewById(R.id.graph_rel_press_comb);
        graph_comb.setTitle("Relatório Pressão do Combustível");
        graph_comb.setTitleColor(32);
        graph_comb.setBackgroundColor(22);

        series_pres_comb = new LineGraphSeries<>();

        graph_comb.addSeries(series_pres_comb);

        Viewport viewport_comb = graph_comb.getViewport();
        viewport_comb.setYAxisBoundsManual(true);
        viewport_comb.setMinY(0);
        viewport_comb.setMaxY(765);

        GraphView graph_temp_ar = (GraphView) findViewById(R.id.graph_rel_temp_ar);
        graph_temp_ar.setTitle("Relatório Temperatura do Ar de Admissão");
        graph_temp_ar.setTitleColor(32);
        graph_temp_ar.setBackgroundColor(22);

        series_temp_ar_ad = new LineGraphSeries<>();

        graph_temp_ar.addSeries(series_temp_ar_ad);

        Viewport viewport_temp_ar = graph_temp_ar.getViewport();
        viewport_temp_ar.setYAxisBoundsManual(true);
        viewport_temp_ar.setMinY(-30);
        viewport_temp_ar.setMaxY(50);

        GraphView graph_temp_liq = (GraphView) findViewById(R.id.graph_rel_temp_liq);
        graph_temp_liq.setTitle("Relatório Temperatura Líquido Refrigerador");
        graph_temp_liq.setTitleColor(32);
        graph_temp_liq.setBackgroundColor(22);

        series_temp_liq = new LineGraphSeries<>();

        graph_temp_liq.addSeries(series_temp_liq);
        Viewport viewport_temp_liq = graph_temp_liq.getViewport();
        viewport_temp_liq.setYAxisBoundsManual(true);
        viewport_temp_liq.setMinY(-10);
        viewport_temp_liq.setMaxY(130);

        text_rpm_max = (TextView) findViewById(R.id.max_rpm);
        text_rpm_min = (TextView) findViewById(R.id.min_rpm);
        text_col_max = (TextView) findViewById(R.id.max_press_col);
        text_col_min = (TextView) findViewById(R.id.min_press_col);
        text_comb_max = (TextView) findViewById(R.id.max_press_comb);
        text_comb_min = (TextView) findViewById(R.id.min_press_comb);
        text_temp_ar_max = (TextView) findViewById(R.id.max_temp_ar);
        text_temp_ar_min = (TextView) findViewById(R.id.min_temp_ar);
        text_temp_liq_max = (TextView) findViewById(R.id.max_temp_liq);
        text_temp_liq_min = (TextView) findViewById(R.id.min_temp_liq);

    }

    @Override
    protected void onResume() {
        super.onResume();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int tempo_exec = 0;
                final int[] i = {0};
                while(!Thread.currentThread().isInterrupted()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            connect.write(CODIGOS_LEITURA[i[0]]);
                            System.out.println("segundos: " + CODIGOS_LEITURA[i[0]]);

                            if (i[0] < CODIGOS_LEITURA.length - 1) i[0]++;
                            else i[0] = 0;
                        }
                    });
                    
                    try {
                        Thread.sleep(100);
                        tempo_exec += 500;
                        System.out.println("tempo: "+tempo_exec);
                        if(tempo_exec == 60000){
                            pararthread();
                        }
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

    public void pararthread() {
        thread.interrupt();

        deletarRegistrosDoCarro();
        
        salvarDados();
    }

    public void deletarRegistrosDoCarro() {
        RpmDao rpm_dao = RpmDao.getInstance(this);
        rpm_dao.deleteTodosById(carro_atual);
        CollectorPressureDao dao1 = CollectorPressureDao.getInstance(this);
        dao1.deleteTodosById(carro_atual);
        FuelPressureDao dao2 = FuelPressureDao.getInstance(this);
        dao2.deleteTodosById(carro_atual);
        AirTemperatureDao dao3 = AirTemperatureDao.getInstance(this);
        dao3.deleteTodosById(carro_atual);
        LiquidTemperatureDao dao4 = LiquidTemperatureDao.getInstance(this);
        dao4.deleteTodosById(carro_atual);
    }

    public void salvarDados() {
        Rpm rpm = new Rpm();
        rpm.setMin(rpm_min);
        rpm.setMax(rpm_max);
        rpm.setId_carro(carro_atual.getId());

        if(rpm_max >= 0) {
            RpmDao rpm_dao = RpmDao.getInstance(this);
            rpm_dao.addRpm(rpm);
        }

        CollectorPressure pressao_coletor = new CollectorPressure();
        pressao_coletor.setMin(col_ad_min);
        pressao_coletor.setMax(col_ad_max);
        pressao_coletor.setId_carro(carro_atual.getId());
        CollectorPressureDao press_col_dao = CollectorPressureDao.getInstance(this);
        press_col_dao.addCollectorPressure(pressao_coletor);

        FuelPressure pressao_combustivel = new FuelPressure();
        pressao_combustivel.setMin(comb_min);
        pressao_combustivel.setMax(comb_max);
        pressao_combustivel.setId_carro(carro_atual.getId());
        FuelPressureDao press_comb_dao = FuelPressureDao.getInstance(this);
        press_comb_dao.addPressaoComb(pressao_combustivel);

        AirTemperature temp_ar = new AirTemperature();
        temp_ar.setMin(temp_ar_min);
        temp_ar.setMax(temp_ar_max);
        temp_ar.setId_carro(carro_atual.getId());
        AirTemperatureDao temp_ar_dao = AirTemperatureDao.getInstance(this);
        temp_ar_dao.addTempAr(temp_ar);

        LiquidTemperature temp_liq = new LiquidTemperature();
        temp_liq.setMin(temp_liq_min);
        temp_liq.setMax(temp_liq_max);
        temp_liq.setId_carro(carro_atual.getId());
        LiquidTemperatureDao temp_liq_dao = LiquidTemperatureDao.getInstance(this);
        temp_liq_dao.addTempLiq(temp_liq);

    }

    public void processarRPM(String rpm) {

        String[] div_rpm = rpm.split(" ");

        int a = -1;
        int b = -1;

        int primeiro = 0;
        int pegar = 0;

        //System.out.println("rpm new: "+rpm);

        for(String elemento: div_rpm ) {

            if(elemento.length() == 8){
                System.out.println("processar1: "+elemento);
                String str = elemento.substring(0,3);
                if(str.equalsIgnoreCase("410C")){
                    a = Integer.parseInt(elemento.substring(4,5), 16);
                    b = Integer.parseInt(elemento.substring(6,7), 16);
                }
            }
            else if (elemento.length() == 2) {
                //System.out.println("processar2: "+elemento);

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

            int resultado = ((a * 256) + b) / 4;

            series_rpm.appendData(new DataPoint(ultimoX++, resultado), true, 10);

            if(resultado < rpm_min) rpm_min = resultado;
            if(resultado > rpm_max) rpm_max = resultado;
            text_rpm_min.setText(String.valueOf(rpm_min));
            text_rpm_max.setText(String.valueOf(rpm_max));
        }
    }

    public void processarPressColetorAd (String press_col) {

        String[] div_press_col = press_col.split(" ");

        int pressao = 0;

        int primeiro = 0;
        int pegar = 0;
        int mostrar = 0;

        for(String elemento: div_press_col ) {

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
        System.out.println("valor: ");

        if(mostrar == 1) {

            int resultado = pressao;

            series_pres_coletor_ad.appendData(new DataPoint(ultimoX++, resultado), true, 10);

            if(resultado < col_ad_min || col_ad_min == 0) col_ad_min = resultado;
            if(resultado > col_ad_max) col_ad_max = resultado;

            text_col_min.setText(String.valueOf(col_ad_min));
            text_col_max.setText(String.valueOf(col_ad_max));
        }
    }

    public void processarPressCombustivel (String press_comb) {
        int pressao = 0;
        int primeiro = 0;
        int pegar = 0;
        int mostrar = 0;

        String[] div_press_comb = press_comb.split(" ");

        System.out.println("BUG: "+press_comb);

        for(String elemento: div_press_comb ) {

            if(elemento.length() == 6){
                String str = elemento.substring(0,3);
                if(str.equalsIgnoreCase("1201")){
                    pressao = Integer.parseInt(elemento.substring(4,5), 16);
                    mostrar = 1;
                }
            }
            if (elemento.length() == 2) {

                if(pegar == 1) {
                    Log.d("Pressao Combustivel", "pegar");
                    pressao = Integer.parseInt(elemento, 16);
                    mostrar = 1;
                }

                if(pegar > 0){
                    pegar = 0;
                }

                if(primeiro == 10){
                    Log.d("Pressao Combustivel", "segundo: "+elemento);
                    if(elemento.equalsIgnoreCase("01")){
                        pegar = 1;
                    }

                    primeiro = 0;
                }

                if(elemento.equalsIgnoreCase("12") && pegar == 0 ){
                    Log.d("Pressao Combustivel", "primeiro: "+elemento);
                    primeiro = 10;
                }
            }
        }

        if(mostrar == 1) {

            int resultado = pressao;

            series_pres_comb.appendData(new DataPoint(ultimoX++, resultado), true, 10);

            if(resultado < comb_min || comb_min == 0) comb_min = resultado;
            if(resultado > comb_max) comb_max = resultado;
            text_comb_min.setText(String.valueOf(comb_min));
            text_comb_max.setText(String.valueOf(comb_max));
        }
    }

    public void processarTempAr (String temp_ar) {

        String[] div_temp_ar = temp_ar.split(" ");

        int temperatura = 0;

        int primeiro = 0;
        int pegar = 0;
        int mostrar = 0;

        for(String elemento: div_temp_ar ) {

            if(elemento.length() == 6){
                String str = elemento.substring(0,3);
                if(str.equalsIgnoreCase("410F")){
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
                    if(elemento.equalsIgnoreCase("0F")){
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

            int resultado = temperatura - 40;

            series_temp_ar_ad.appendData(new DataPoint(ultimoX++, resultado), true, 10);

            if(resultado < temp_ar_min || temp_ar_min == 0) temp_ar_min = resultado;
            if(resultado > temp_ar_max) temp_ar_max = resultado;
            text_temp_ar_min.setText(String.valueOf(temp_ar_min));
            text_temp_ar_max.setText(String.valueOf(temp_ar_max));
        }
    }

    public void processarTempLiq (String temp_liq) {

        String[] div_temp_liq = temp_liq.split(" ");

        int temperatura = 0;

        int primeiro = 0;
        int pegar = 0;
        int mostrar = 0;

        for(String elemento: div_temp_liq ) {

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

            int resultado = temperatura - 40;

            series_temp_liq.appendData(new DataPoint(ultimoX++, resultado), true, 10);

            if(resultado < temp_liq_min || temp_liq_min == 0) temp_liq_min = resultado;
            if(resultado > temp_liq_max) temp_liq_max = resultado;
            text_temp_liq_min.setText(String.valueOf(temp_liq_min));
            text_temp_liq_max.setText(String.valueOf(temp_liq_max));
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = new String(readBuf, 0, msg.arg1);

                    //System.out.println("COD_: "+readMessage);
                    if(readMessage.indexOf("410C") >= 0 || readMessage.indexOf("41 0C") >= 0){
                        processarRPM(readMessage);
                    }else if(readMessage.indexOf("410B") >= 0 || readMessage.indexOf("41 0B") >= 0) {
                        processarPressColetorAd(readMessage);
                    }else if(readMessage.indexOf("1201") >= 0 || readMessage.indexOf("12 01") >= 0) {
                        processarPressCombustivel(readMessage);
                    }else if(readMessage.indexOf("410F") >= 0 || readMessage.indexOf("41 0F") >= 0) {
                        processarTempAr(readMessage);
                    }else if(readMessage.indexOf("4105") >= 0 || readMessage.indexOf("41 05") >= 0) {
                        processarTempLiq(readMessage);
                    }
                    break;
            }
        }
    };
}
