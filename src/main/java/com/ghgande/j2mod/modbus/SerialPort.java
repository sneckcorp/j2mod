package com.ghgande.j2mod.modbus;

import java.io.InputStream;
import java.io.OutputStream;

public interface SerialPort {
    int DATABITS_5 = 5;
    int DATABITS_6 = 6;
    int DATABITS_7 = 7;
    int DATABITS_8 = 8;
    int FLOWCONTROL_NONE = 0;
    int FLOWCONTROL_RTSCTS_IN = 1;
    int FLOWCONTROL_RTSCTS_OUT = 2;
    int FLOWCONTROL_XONXOFF_IN = 4;
    int FLOWCONTROL_XONXOFF_OUT = 8;
    int PARITY_EVEN = 2;
    int PARITY_MARK = 3;
    int PARITY_NONE = 0;
    int PARITY_ODD = 1;
    int PARITY_SPACE = 4;
    int STOPBITS_1 = 1;
    int STOPBITS_1_5 = 3;
    int STOPBITS_2 = 2;

    String getDeviceName();

    void setSerialPortParams(int baudRate, int dataBits, int stopBits, int parity);

    int getBaudRate();

    int getDataBits();

    int getStopBits();

    int getParity();

    void setFlowControlMode(int mode);

    int getFlowControlMode();

    /**
     * Data Terminal Ready
     *
     * @param option
     */
    void setDTR(boolean option);

    boolean isDTR();

    /**
     * Request to Send
     *
     * @param option
     */
    void setRTS(Boolean option);

    boolean isRTS();

    InputStream getInputStream();

    OutputStream getOutputStream();

    void open() throws Exception;

    void close() throws Exception;
}
