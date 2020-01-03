package android.com.br.conexaobluetooth.model;

/**
 * Created by Claudio on 23/08/2015.
 */
public class Cars {

    private int id;
    private String nome;
    private String marca;
    private String modelo;
    private int selecionado;

    public Cars() {

    }

    public Cars(String nome, String marca, String modelo, int selecionado) {
        this.nome = nome;
        this.marca = marca;
        this.modelo = modelo;
        this.selecionado = selecionado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(int selecionado) {
        this.selecionado = selecionado;
    }

}
