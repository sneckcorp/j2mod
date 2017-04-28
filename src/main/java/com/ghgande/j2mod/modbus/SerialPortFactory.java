package com.ghgande.j2mod.modbus;

import java.util.Iterator;
import java.util.ServiceLoader;

public class SerialPortFactory {
    public static SerialPort create(String devicePath) throws Exception {
        SerialPort serialPort = null;
        ServiceLoader<SerialPort> serviceLoader = ServiceLoader.load(SerialPort.class);
        Iterator<SerialPort> iterator = serviceLoader.iterator();

        if (iterator.hasNext()) {
            serialPort = serviceLoader.iterator().next();
            serialPort.init(devicePath);
            return serialPort;
        } else {
            throw new Exception("Not found SerialPort service.");
        }
    }
}
