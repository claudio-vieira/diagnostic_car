package android.com.br.conexaobluetooth.model;

/**
 * Created by Claudio on 04/11/2015.
 */
public class TroubleCodes {

    private int id;
    private String pid;
    private String descricao;

    public TroubleCodes() {}

    public TroubleCodes(String pid, String descricao){
        this.pid = pid;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
