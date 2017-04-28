/*
 * Copyright 2002-2016 jamod & j2mod development teams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ghgande.j2mod.modbus.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.SerialPort;
import com.ghgande.j2mod.modbus.SerialPortFactory;
import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import com.ghgande.j2mod.modbus.io.ModbusASCIITransport;
import com.ghgande.j2mod.modbus.io.ModbusRTUTransport;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransport;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that implements a serial connection which can be used for master and
 * slave implementations.
 *
 * @author Dieter Wimberger
 * @author John Charlton
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public class SerialConnection extends AbstractSerialConnection {

    private static final Logger logger = LoggerFactory.getLogger(SerialConnection.class);

    private SerialParameters parameters;
    private ModbusSerialTransport transport;
    private SerialPort serialPort;
    private InputStream inputStream;
    private OutputStream outputStream;
    private int timeout = Modbus.DEFAULT_TIMEOUT;

    /**
     * Default constructor
     */
    public SerialConnection() {
        this.parameters = new SerialParameters();
    }

    /**
     * Creates a SerialConnection object and initializes variables passed in as
     * params.
     *
     * @param parameters A SerialParameters object.
     */
    public SerialConnection(SerialParameters parameters) {
        this.parameters = parameters;
    }

    /**
     * Returns a JSerialComm implementation for the given comms port
     *
     * @param commPort Comms port e.g. /dev/ttyAMA0
     * @return JSerialComm implementation
     */
    public static AbstractSerialConnection getCommPort(String commPort) {
        SerialConnection connection = new SerialConnection();
        connection.parameters.setPortName(commPort);
        try {
            connection.serialPort = SerialPortFactory.create(connection.parameters.getPortName());
        } catch (Exception ignored) {
        }
        return connection;
    }

    @Override
    public AbstractModbusTransport getModbusTransport() {
        return transport;
    }

    @Override
    public boolean open() {
        if (serialPort == null) {
            try {
                serialPort = SerialPortFactory.create(parameters.getPortName());
            } catch (Exception e) {
                return false;
            }
        }

        try {
            serialPort.close();
        } catch (Exception ignored) {
        }

        setConnectionParameters();

        if (Modbus.SERIAL_ENCODING_ASCII.equals(parameters.getEncoding())) {
            transport = new ModbusASCIITransport();
        } else if (Modbus.SERIAL_ENCODING_RTU.equals(parameters.getEncoding())) {
            transport = new ModbusRTUTransport();
        } else {
            transport = new ModbusRTUTransport();
            logger.warn("Unknown transport encoding [{}] - reverting to RTU", parameters.getEncoding());
        }
        transport.setEcho(parameters.isEcho());
        transport.setTimeout(timeout);

        // Open the input and output streams for the connection. If they won't
        // open, close the port before throwing an exception.
        try {
            transport.setCommPort(this);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            serialPort.open();
        } catch (Exception e) {
            close();
            return false;
        }

        inputStream = serialPort.getInputStream();
        outputStream = serialPort.getOutputStream();

        return true;
    }

    @Override
    public void setConnectionParameters() {

        // Set connection parameters, if set fails return parameters object
        // to original state

        if (serialPort != null) {
            serialPort.setSerialPortParams(parameters.getBaudRate(), parameters.getDatabits(), parameters.getStopbits(), parameters.getParity());
            serialPort.setFlowControlMode(parameters.getFlowControlIn() | parameters.getFlowControlOut());
        }
    }

    @Override
    public void close() {
        // Check to make sure serial port has reference to avoid a NPE

        if (serialPort != null) {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                logger.debug(e.getMessage());
            } finally {
                // Close the port.
                try {
                    serialPort.close();
                } catch (Exception ignore) {
                }
            }
        }
        serialPort = null;
    }

    @Override
    public boolean isOpen() {
        return serialPort != null;
    }

    @Override
    public synchronized int getTimeout() {
        return timeout;
    }

    @Override
    public synchronized void setTimeout(int timeout) {
        this.timeout = timeout;
        if (transport != null) {
            transport.setTimeout(timeout);
        }
    }

    @Override
    public int readBytes(byte[] buffer, long bytesToRead) {
        try {
            return inputStream.read(buffer, 0, (int) bytesToRead);
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public int writeBytes(byte[] buffer, long bytesToWrite) {
        try {
            outputStream.write(buffer, 0, (int) bytesToWrite);
            return (int) bytesToWrite;
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public int bytesAvailable() {
        try {
            return inputStream.available();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public int getBaudRate() {
        return serialPort.getBaudRate();
    }

    @Override
    public void setBaudRate(int newBaudRate) {
        serialPort.setSerialPortParams(newBaudRate, serialPort.getDataBits(), serialPort.getStopBits(), serialPort.getParity());
    }

    @Override
    public int getNumDataBits() {
        return serialPort.getDataBits();
    }

    @Override
    public int getNumStopBits() {
        return serialPort.getStopBits();
    }

    @Override
    public int getParity() {
        return serialPort.getParity();
    }

    @Override
    public String getDescriptivePortName() {
        return serialPort.getDeviceName();
    }

    @Override
    public void setComPortTimeouts(int newTimeoutMode, int newReadTimeout, int newWriteTimeout) {

    }
}
