package android.com.br.conexaobluetooth.activities;

import android.com.br.conexaobluetooth.R;
import android.com.br.conexaobluetooth.adapter.ReportAdapter;
import android.com.br.conexaobluetooth.dao.CarsDao;
import android.com.br.conexaobluetooth.dao.CollectorPressureDao;
import android.com.br.conexaobluetooth.dao.FuelPressureDao;
import android.com.br.conexaobluetooth.dao.RpmDao;
import android.com.br.conexaobluetooth.dao.AirTemperatureDao;
import android.com.br.conexaobluetooth.dao.LiquidTemperatureDao;
import android.com.br.conexaobluetooth.model.Cars;
import android.com.br.conexaobluetooth.model.CollectorPressure;
import android.com.br.conexaobluetooth.model.FuelPressure;
import android.com.br.conexaobluetooth.model.Report;
import android.com.br.conexaobluetooth.model.Rpm;
import android.com.br.conexaobluetooth.model.AirTemperature;
import android.com.br.conexaobluetooth.model.LiquidTemperature;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShowReportActivity extends ActionBarActivity {

    private ReportAdapter rel_adapter;
    private TextView mostrar_carro_rel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_report);

        CarsDao carro_dao = CarsDao.getInstance(this);
        Cars c = carro_dao.buscarSelecionado();

        if(c != null) {
            mostrar_carro_rel = (TextView) findViewById(R.id.mostrar_carro_rel);
            mostrar_carro_rel.setText("Relatório do carro: "+c.getNome());

            RpmDao rpm_dao = RpmDao.getInstance(this);
            CollectorPressureDao press_col_dao = CollectorPressureDao.getInstance(this);
            FuelPressureDao press_comb_dao = FuelPressureDao.getInstance(this);
            AirTemperatureDao temp_ar_dao = AirTemperatureDao.getInstance(this);
            LiquidTemperatureDao temp_liq_dao = LiquidTemperatureDao.getInstance(this);
            Rpm rpm = rpm_dao.getRpmByCarroId(c.getId());
            CollectorPressure press_col = press_col_dao.getPressaoByCarro(c.getId());
            FuelPressure press_comb = press_comb_dao.getPressaoByCarro(c.getId());
            AirTemperature temp_ar = temp_ar_dao.getPressaoByCarro(c.getId());
            LiquidTemperature temp_liq = temp_liq_dao.getPressaoByCarro(c.getId());

            if(rpm != null) {

                Report rel = new Report(rpm.getMax(), rpm.getMin(), press_col.getMax(), press_col.getMin(),
                        press_comb.getMax(), press_comb.getMin(), temp_ar.getMax(), temp_ar.getMin(),
                        temp_liq.getMax(), temp_liq.getMin());
                List<Report> relatorios = new ArrayList<>();
                relatorios.add(rel);
                rel_adapter = new ReportAdapter(this, relatorios);

                ListView lista_adap_rel_geral = (ListView) findViewById(R.id.lista_rel_geral);
                lista_adap_rel_geral.setAdapter(rel_adapter);
            }else{
                mostrar_carro_rel.setText(c.getNome()+" não possui um relatório.");
            }
        }else{
            Toast.makeText(ShowReportActivity.this, "Favor selecionar um veículo", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }

}
