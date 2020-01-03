package android.com.br.conexaobluetooth.model;

/**
 * Created by Claudio on 15/11/2015.
 */
public class FuelPressure {
    private int id;
    private int min;
    private int max;
    private int id_carro;

    public FuelPressure() {}

    public FuelPressure(int min, int max, int id_carro) {
        this.min = min;
        this.max = max;
        this.id_carro = id_carro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getId_carro() {
        return id_carro;
    }

    public void setId_carro(int id_carro) {
        this.id_carro = id_carro;
    }
}
