package android.com.br.conexaobluetooth.activities;

import android.com.br.conexaobluetooth.R;
import android.com.br.conexaobluetooth.activities.currentData.IntakeCollectorPressureActivity;
import android.com.br.conexaobluetooth.activities.currentData.FuelPressureActivity;
import android.com.br.conexaobluetooth.activities.currentData.RPMActivity;
import android.com.br.conexaobluetooth.activities.currentData.IntakeAirPressureActivity;
import android.com.br.conexaobluetooth.activities.currentData.LiquidTemperatureActivity;
import android.com.br.conexaobluetooth.activities.currentData.BatteryVoltageActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class OBDListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obd_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_obd_list, menu);
        return true;
    }

    public void rpmActivity(View view) {
        Intent intent = new Intent(this, RPMActivity.class);
        startActivity(intent);
    }

    public void LiquidTemperatureActivity(View view) {
        Intent intent = new Intent(this, LiquidTemperatureActivity.class);
        startActivity(intent);
    }

    public void tempArAdmissao(View view) {
        Intent intent = new Intent(this, IntakeAirPressureActivity.class);
        startActivity(intent);
    }

    public void pressaoCombustivel(View view) {
        Intent intent = new Intent(this, FuelPressureActivity.class);
        startActivity(intent);
    }

    public void pressaoColetorAdmissao(View view) {
        Intent intent = new Intent(this, IntakeCollectorPressureActivity.class);
        startActivity(intent);
    }

    public void VoltagemBateria(View view) {
        Intent intent = new Intent(this, BatteryVoltageActivity.class);
        startActivity(intent);
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
