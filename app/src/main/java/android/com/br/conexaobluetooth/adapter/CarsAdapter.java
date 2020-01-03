package android.com.br.conexaobluetooth.adapter;

import android.app.Activity;
import android.com.br.conexaobluetooth.R;
import android.com.br.conexaobluetooth.model.Cars;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Claudio on 02/09/2015.
 */
public class CarsAdapter extends ArrayAdapter<Cars> {
    private final List<Cars> dados;
    private final Activity context;
    private int posicao;

    public CarsAdapter(Activity context, List<Cars> dados) {
        super(context, R.layout.activity_cars, dados);
        this.context = context;
        this.dados = dados;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        this.posicao = position;
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.cars_list, null);

        //Load the widget on screen
        TextView descricao = (TextView) view.findViewById(R.id.descricao);

        //Pass product id
        ImageButton imageButtonDelete = (ImageButton) view.findViewById(R.id.imageButtonDelete);
        imageButtonDelete.setTag(dados.get(position).getId());
        ImageButton imageButtonEdit = (ImageButton) view.findViewById(R.id.imageButtonEdit);
        imageButtonEdit.setTag(dados.get(position).getId());
        ImageButton imageButtomSelecionado = (ImageButton) view.findViewById(R.id.imageButtonSelect);
        imageButtomSelecionado.setTag(dados.get(position).getId());

        descricao.setText(dados.get(position).getNome());

        return view;
    }
}
