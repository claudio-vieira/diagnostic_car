package android.com.br.conexaobluetooth.adapter;

import android.app.Activity;
import android.com.br.conexaobluetooth.R;
import android.com.br.conexaobluetooth.model.TroubleCodes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Claudio on 11/11/2015.
 */
public class CodAdapter  extends ArrayAdapter<TroubleCodes> {

    private final List<TroubleCodes> dados;
    private final Activity context;
    private int posicao;

    public CodAdapter(Activity context, List<TroubleCodes> dados) {
        super(context, R.layout.activity_trouble_codes, dados);
        this.context = context;
        this.dados = dados;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        this.posicao = position;
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.trouble_code_list, null);

        //Load the widget on screen
        TextView descricao = (TextView) view.findViewById(R.id.conteudo_cod);
        descricao.setText(dados.get(position).getDescricao());

        return view;
    }

}
