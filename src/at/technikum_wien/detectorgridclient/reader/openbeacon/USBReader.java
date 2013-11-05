/*
 * This file is part of DetectorGridClient.
 * 
 * DetectorGridClient is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DetectorGridClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with DetectorGridClient.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.technikum_wien.detectorgridclient.reader.openbeacon;

import at.technikum_wien.detectorgridclient.TagInformation;
import at.technikum_wien.detectorgridclient.communication.Listener;
import at.technikum_wien.detectorgridclient.reader.Reader;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wkoller
 */
public class USBReader extends Listener implements Reader, SerialPortEventListener {
    /**
     * Define the serial port to listen on
     */
    public static final String COMM_PORT = "/dev/ttyS80";
    
    /**
     * Separator for message coming in from serial port
     */
    public static final String SERIAL_SEPARATOR = ": ";
    
    private SerialPort serialPort = null;
    private InputStream serialPortReader = null;
    private byte[] serialBuffer = new byte[1024];

    public USBReader() throws Exception {
        CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(COMM_PORT);
        if( commPortIdentifier.isCurrentlyOwned() ) {
            throw new IllegalAccessException("Comm port is currently in use");
        }
        
        CommPort commPort = commPortIdentifier.open(this.getClass().getName(), 2000);
        if( !(commPort instanceof SerialPort) ) {
            throw new UnsupportedCommOperationException("Only Serial Ports are supported");
        }
        serialPort = (SerialPort) commPort;
        serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        
        serialPortReader = serialPort.getInputStream();
        
        serialPort.addEventListener(this);
        serialPort.notifyOnDataAvailable(true);
    }

    @Override
    public String getReaderUUID() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TagInformation findTag(String tagCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void serialEvent(SerialPortEvent spe) {
        try {
            int data = 0;
            int len = 0;

            // read each byte from the serial buffer
            while( (data = serialPortReader.read()) > -1 ) {
                if( data == '\n' ) {
                    break;
                }
                
                serialBuffer[len++] = (byte) data;
            }
            // convert byte array to a string
            String serialBufferString = new String(serialBuffer,0,len).trim();

            // debug output of serial buffer content
            Logger.getLogger(USBReader.class.getName()).log(Level.FINEST, serialBufferString);
            
            // Analyze message and check for distance reading
            String serialBufferComponents[] = serialBufferString.split(SERIAL_SEPARATOR);
            if( serialBufferComponents.length > 1 ) {
                switch(serialBufferComponents[0]) {
                    case "DIST":
                        // split DIST message into its components
                        String distMsgComponents[] = serialBufferComponents[1].split(",");
                        if( distMsgComponents.length < 2 ) {
                            throw new Exception("Invalid DIST Message received: " + serialBufferComponents[1]);
                        }
                        
                        System.err.println("DIST msg received: TX=" + distMsgComponents[0] + " / TagID=" + distMsgComponents[1]);
                        
                        break;
                }
            }
            
        } catch (Exception ex) {
            Logger.getLogger(USBReader.class.getName()).log(Level.SEVERE, "Unable to read from SerialPort of USB Reader", ex);
        }
    }
}
