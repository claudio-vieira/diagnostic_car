package android.com.br.conexaobluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Claudio on 06/09/2015.
 */
public class Util {

    public static BluetoothDevice device = null;
    public static ConnectBluetooth connect = null;
    public static String DataBaseName = "Diagnostic";
    public static String COD_RPM = "010C";
    public static String COD_PRESAO_COLETOR_ADMISSAO = "010B";
    public static String COD_PRESAO_COMBUSTIVEL = "010A";
    public static String COD_TEMP_AR_ADMiSSAO = "010F";
    public static String COD_TEMP_lIQUIDO = "0105";
    public static String COD_VOLTAGEM_BATERIA = "AT RV";
    public static String[] CODIGOS_LEITURA = {
            COD_RPM,
            COD_PRESAO_COLETOR_ADMISSAO,
            COD_PRESAO_COMBUSTIVEL,
            COD_TEMP_AR_ADMiSSAO,
            COD_TEMP_lIQUIDO
    };
}
