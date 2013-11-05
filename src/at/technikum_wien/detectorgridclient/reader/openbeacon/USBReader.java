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

    /**
     * Serial port handler variable
     */
    private SerialPort serialPort = null;
    
    /**
     * input stream for reading fomr serial port
     */
    private InputStream serialPortReader = null;
    
    /**
     * buffer for reading from serial port (since it is using byte based reading)
     */
    private byte[] serialBuffer = new byte[1024];
    
    /**
     * Initialize the OpenBeacon USB reader and start reading from it
     * @throws Exception 
     */
    public USBReader() throws Exception {
        // get the identifier for the specified comm port
        CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(COMM_PORT);
        if( commPortIdentifier.isCurrentlyOwned() ) {
            throw new IllegalAccessException("Comm port is currently in use");
        }
        
        // open the port and check if we have a serial port
        CommPort commPort = commPortIdentifier.open(this.getClass().getName(), 2000);
        if( !(commPort instanceof SerialPort) ) {
            throw new UnsupportedCommOperationException("Only Serial Ports are supported");
        }
        serialPort = (SerialPort) commPort;
        
        // configure serial port to use the openbeacon specific settings
        serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        
        // fetch a reference to the input stream for later reading
        serialPortReader = serialPort.getInputStream();
        
        // use event based reading by subscribing
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

    /**
     * called whenever there is data available from the serial port
     * @param spe 
     */
    @Override
    public void serialEvent(SerialPortEvent spe) {
        try {
            // helper values for reading byte-whise from the serial port
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
                        
                        // logging of DIST msg
                        Logger.getLogger(USBReader.class.getName()).log(Level.FINE, "DIST msg received: TX=" + distMsgComponents[0] + " / TagID=" + distMsgComponents[1]);
                        break;
                }
            }
            
        } catch (Exception ex) {
            Logger.getLogger(USBReader.class.getName()).log(Level.SEVERE, "Unable to read from SerialPort of USB Reader", ex);
        }
    }
}
