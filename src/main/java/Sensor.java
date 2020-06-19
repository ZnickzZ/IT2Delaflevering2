/** @author {Mads Voss, Mikkel Bech, Dalia Pireh, Sali Azou, Beant Sandhu}*/
import data.EKGDTO;
import jssc.*;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public class Sensor {
    private SerialPort serialPort = null;
    private String result = null;
    private int value = 0;

    public Sensor(int portnummer) {
        String[] portNames = SerialPortList.getPortNames();
        try {
            serialPort = new SerialPort(portNames[portnummer]);
            serialPort.openPort();
            serialPort.setParams(115200, 8, 1, 0);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            serialPort.setDTR(true);
            serialPort.setRTS(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<EKGDTO> getData() {
        try {
            if (serialPort.getInputBufferBytesCount() > 0) {
                result = serialPort.readString();
                String[] rawValues;
                if (result != null) {
                    result = result.substring(0, result.length() - 1);
                    rawValues = result.split(" ");
                    List<EKGDTO> data = new LinkedList<>();
                    for (int i = 0; i < rawValues.length; i++) {
                        EKGDTO ekgDTO = new EKGDTO();
                        try {
                            ekgDTO.setEkg(Double.parseDouble(rawValues[i]));
                            ekgDTO.setTimestamp(new Timestamp(System.currentTimeMillis()));
                            data.add(ekgDTO);
                            Thread.sleep(1);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return data;
                }
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        return null;
    }

/*
    public static void main(String[] args) throws InterruptedException {
        Sensor sensor = new Sensor(1);
        while(true){
            Thread.sleep(20);
            int data = sensor.getData();
            System.out.println(data);
        }
    }
    */
}