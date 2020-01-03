package android.com.br.conexaobluetooth.activities;

import android.com.br.conexaobluetooth.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class ReportListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_carro
activity_detalhe_carro
activity_detalhe_carro
activity_report_list);
    }

    public void gerarReport(View view) {
        Intent intent = new Intent(this, ReportActivity.class);
        startActivity(intent);
    }

    public void mostrarReport(View view) {
        Intent intent = new Intent(this, ShowReportActivity.class);
        startActivity(intent);
    }

}
