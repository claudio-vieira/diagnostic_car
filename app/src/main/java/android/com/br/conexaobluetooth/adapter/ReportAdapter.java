package android.com.br.conexaobluetooth.adapter;

import android.app.Activity;
import android.com.br.conexaobluetooth.R;
import android.com.br.conexaobluetooth.model.Report;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Claudio on 17/11/2015.
 */
public class ReportAdapter extends ArrayAdapter<Report>{

    private List<Report> dados;
    private final Activity context;
    private int posicao;

    public ReportAdapter(Activity context, List<Report> dados) {
        super(context, R.layout.activity_show_report, dados);
        this.dados = dados;
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        this.posicao = position;
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.report_list, null);

        //Load the widget on screen
        TextView rel_max_rpm = (TextView) view.findViewById(R.id.rel_max_rpm);
        TextView rel_min_rpm = (TextView) view.findViewById(R.id.rel_min_rpm);
        TextView rel_max_press_col = (TextView) view.findViewById(R.id.rel_max_press_col);
        TextView rel_min_press_col = (TextView) view.findViewById(R.id.rel_min_press_col);
        TextView rel_max_press_comb = (TextView) view.findViewById(R.id.rel_max_press_comb);
        TextView rel_min_press_comb = (TextView) view.findViewById(R.id.rel_min_press_comb);
        TextView rel_max_temp_ar = (TextView) view.findViewById(R.id.rel_max_temp_ar);
        TextView rel_min_temp_ar = (TextView) view.findViewById(R.id.rel_min_temp_ar);
        TextView rel_max_temp_liq = (TextView) view.findViewById(R.id.rel_max_temp_liq);
        TextView rel_min_temp_liq = (TextView) view.findViewById(R.id.rel_min_temp_liq);

        rel_max_rpm.setText(String.valueOf(dados.get(position).getRpm_max()));
        rel_min_rpm.setText(String.valueOf(dados.get(position).getRpm_min()));
        rel_max_press_col.setText(String.valueOf(dados.get(position).getPress_col_max()));
        rel_min_press_col.setText(String.valueOf(dados.get(position).getPress_col_min()));
        rel_max_press_comb.setText(String.valueOf(dados.get(position).getPress_comb_max()));
        rel_min_press_comb.setText(String.valueOf(dados.get(position).getPress_comb_min()));
        rel_max_temp_ar.setText(String.valueOf(dados.get(position).getTemp_ar_max()));
        rel_min_temp_ar.setText(String.valueOf(dados.get(position).getTemp_ar_min()));
        rel_max_temp_liq.setText(String.valueOf(dados.get(position).getTemp_liq_max()));
        rel_min_temp_liq.setText(String.valueOf(dados.get(position).getTemp_liq_min()));

        return view;
    }

}
