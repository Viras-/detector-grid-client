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

import at.technikum_wien.detectorgrid.TagInformation;
import at.technikum_wien.detectorgridclient.communication.CommunicationListener;
import at.technikum_wien.detectorgridclient.reader.Reader;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wkoller
 */
public class USBReader implements CommunicationListener, Reader, SerialPortEventListener {
    /**
     * Define the serial port to listen on
     */
    public static final String COMM_PORT = "/dev/ttyACM0";
    
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
     * internal lookup table for previously found tags
     */
    protected HashMap<String,TagInformation> tagTable = new HashMap<>();
    
    /**
     * reader UUID, randomly generated for now
     */
    protected String readerUUID = UUID.randomUUID().toString();
    
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
        return readerUUID;
    }

    @Override
    public TagInformation findTag(String tagCode) {
        return tagTable.get(tagCode);
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
                    case "TAG":
                        // split TAG message into its components
                        String tagMsgComponents[] = serialBufferComponents[1].split(",");
                        if( tagMsgComponents.length < 3 ) {
                            throw new Exception("Invalid TAG Message received: " + serialBufferComponents[1]);
                        }
                        
                        // logging of DIST msg
                        Logger.getLogger(USBReader.class.getName()).log(Level.INFO, "TAG msg received: TX=" + tagMsgComponents[1] + " / TagID=" + tagMsgComponents[0] + " / seenTick=" + tagMsgComponents[2]);

                        // create a tag information for the found tag
                        TagInformation tagInformation = new TagInformation();
                        tagInformation.distance = Integer.parseInt(tagMsgComponents[1].trim());
                        tagInformation.readerId = this.getReaderUUID();
                        tagInformation.tagCode = tagMsgComponents[0].trim();
                        tagInformation.seenTick = Integer.parseInt(tagMsgComponents[2].trim());
                        
                        // add the tag to the internal storage table for later lookup
                        tagTable.put(tagInformation.tagCode, tagInformation);
                        break;
                }
            }
            
        } catch (Exception ex) {
            Logger.getLogger(USBReader.class.getName()).log(Level.SEVERE, "Unable to read from SerialPort of USB Reader", ex);
        }
    }
}
