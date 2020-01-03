package android.com.br.conexaobluetooth.activities;

import android.annotation.TargetApi;
import android.com.br.conexaobluetooth.R;
import android.com.br.conexaobluetooth.adapter.CarsAdapter;
import android.com.br.conexaobluetooth.dao.CarsDao;
import android.com.br.conexaobluetooth.model.Cars;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CarsActivity extends ActionBarActivity {

    private CarsAdapter carrosAdap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        ListView lista = (ListView) findViewById(R.id.cars_list);
        carrosAdap = new CarsAdapter(this, getCars());
        lista.setAdapter(carrosAdap);

        ImageButton imageButtonAdd = (ImageButton)
                findViewById(R.id.imageButtonAdd);
        imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view) {
                //Open detail screen of car
                Intent it = new Intent(getApplicationContext(),
                        CarDetailActivity.class);
                startActivity(it);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onClickDeleteButton(View view) {
        int id = (int) view.getTag();
        CarsDao db = CarsDao.getInstance(this);
        Cars c = db.getCars(id);
        db.deleteCars(c);
        carrosAdap.remove(c);
        carrosAdap.clear();
        carrosAdap.addAll(getCars());

        carrosAdap.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "" + id, Toast.LENGTH_SHORT).show();
    }

    public void onClickEditButton(View view) {
        int id = (int) view.getTag();
        Intent it = new Intent(getApplicationContext(), CarDetailActivity.class);
        it.putExtra("id", id);
        Toast.makeText(getApplicationContext(), "" + id, Toast.LENGTH_SHORT).show();
        startActivity(it);
    }

    public void onClickSelectButton(View view) {
        int id = (int) view.getTag();
        CarsDao db = CarsDao.getInstance(this);
        Cars c = db.getCars(id);
        int i = db.updateCarrros(c,1);
        Toast.makeText(getApplicationContext(), "Selecionado: " + i, Toast.LENGTH_SHORT).show();
    }

    private List<Cars> getCars() {
        List<Cars> lista = new ArrayList<>();
        CarsDao db = CarsDao.getInstance(this);
        List<Cars> listTemp = db.getTodosCars();
        for (Cars temp : listTemp) {
            lista.add(temp);
        }
        return lista;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cars, menu);
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
