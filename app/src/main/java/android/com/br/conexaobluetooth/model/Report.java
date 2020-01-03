package android.com.br.conexaobluetooth.model;

/**
 * Created by Claudio on 17/11/2015.
 */
public class Report {

    private int rpm_max;
    private int rpm_min;
    private int press_col_max;
    private int press_col_min;
    private int press_comb_max;
    private int press_comb_min;
    private int temp_ar_max;
    private int temp_ar_min;
    private int temp_liq_max;
    private int temp_liq_min;

    public Report(int rpm_max, int rpm_min, int press_col_max, int press_col_min, int press_comb_max, int press_comb_min, int temp_ar_max, int temp_ar_min, int temp_liq_max, int temp_liq_min) {
        this.rpm_max = rpm_max;
        this.rpm_min = rpm_min;
        this.press_col_max = press_col_max;
        this.press_col_min = press_col_min;
        this.press_comb_max = press_comb_max;
        this.press_comb_min = press_comb_min;
        this.temp_ar_max = temp_ar_max;
        this.temp_ar_min = temp_ar_min;
        this.temp_liq_max = temp_liq_max;
        this.temp_liq_min = temp_liq_min;
    }

    public int getRpm_max() {
        return rpm_max;
    }

    public void setRpm_max(int rpm_max) {
        this.rpm_max = rpm_max;
    }

    public int getRpm_min() {
        return rpm_min;
    }

    public void setRpm_min(int rpm_min) {
        this.rpm_min = rpm_min;
    }

    public int getPress_col_max() {
        return press_col_max;
    }

    public void setPress_col_max(int press_col_max) {
        this.press_col_max = press_col_max;
    }

    public int getPress_col_min() {
        return press_col_min;
    }

    public void setPress_col_min(int press_col_min) {
        this.press_col_min = press_col_min;
    }

    public int getPress_comb_max() {
        return press_comb_max;
    }

    public void setPress_comb_max(int press_comb_max) {
        this.press_comb_max = press_comb_max;
    }

    public int getPress_comb_min() {
        return press_comb_min;
    }

    public void setPress_comb_min(int press_comb_min) {
        this.press_comb_min = press_comb_min;
    }

    public int getTemp_ar_max() {
        return temp_ar_max;
    }

    public void setTemp_ar_max(int temp_ar_max) {
        this.temp_ar_max = temp_ar_max;
    }

    public int getTemp_ar_min() {
        return temp_ar_min;
    }

    public void setTemp_ar_min(int temp_ar_min) {
        this.temp_ar_min = temp_ar_min;
    }

    public int getTemp_liq_max() {
        return temp_liq_max;
    }

    public void setTemp_liq_max(int temp_liq_max) {
        this.temp_liq_max = temp_liq_max;
    }

    public int getTemp_liq_min() {
        return temp_liq_min;
    }

    public void setTemp_liq_min(int temp_liq_min) {
        this.temp_liq_min = temp_liq_min;
    }
}
