package android.com.br.conexaobluetooth.activities;

import android.com.br.conexaobluetooth.R;
import android.com.br.conexaobluetooth.dao.CarsDao;
import android.com.br.conexaobluetooth.model.Cars;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CarDetailActivity extends ActionBarActivity {
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);

        Intent it = getIntent();

        id = it.getIntExtra("id", 0);
        if (id != 0) {
            CarsDao db = CarsDao.getInstance(getApplicationContext());
            Cars c = db.getCars(id);

            EditText nome = (EditText) findViewById(R.id.edtNome);
            EditText marca = (EditText) findViewById(R.id.edtMarca);
            EditText modelo = (EditText) findViewById(R.id.edtModelo);

            nome.setText(c.getNome());
            marca.setText(c.getMarca());
            modelo.setText(c.getModelo());
        }

        Button btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(it);
            }
        });

        Button btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edtNome = (EditText) findViewById(R.id.edtNome);
                EditText edtMarca = (EditText) findViewById(R.id.edtMarca);
                EditText edtModelo = (EditText) findViewById(R.id.edtModelo);

                Cars c = new Cars(
                        edtNome.getText().toString(),
                        edtMarca.getText().toString(),
                        edtModelo.getText().toString(),
                        0);

                CarsDao db = CarsDao.getInstance(getApplicationContext());
                if (id == 0)
                    db.addCarro(c);
                else {
                    c.setId(id);
                    db.updateCarrros(c);
                }
                Intent it = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(it);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_car_detail, menu);
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
