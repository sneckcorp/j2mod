package com.ghgande.j2mod.modbus;

import java.util.Iterator;
import java.util.ServiceLoader;

public class SerialPortFactory {
    public static SerialPort create() throws Exception {
        ServiceLoader<SerialPort> serviceLoader = ServiceLoader.load(SerialPort.class);
        Iterator<SerialPort> iterator = serviceLoader.iterator();
        if (iterator.hasNext()) {
            return serviceLoader.iterator().next();
        } else {
            throw new Exception("Not found SerialPort service.");
        }
    }
}
