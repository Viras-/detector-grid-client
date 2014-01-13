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
package at.technikum_wien.detectorgridclient;

import at.technikum_wien.detectorgridclient.communication.spread.SpreadClient;
import at.technikum_wien.detectorgridclient.reader.openbeacon.USBReader;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author wkoller
 */
public class DetectorGridClient {
    public DetectorGridClient(String host, String uuid, String device) throws Exception {
        SpreadClient spreadClient = new SpreadClient(uuid);
        if( !spreadClient.init(host) ) {
            throw new Exception("Unable to start SpreadClient - exiting!");
        }
        
        USBReader uSBReader = new USBReader(uuid, device);
        spreadClient.addListener(uSBReader);
    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // setup available command line options
        Options options = new Options();
        options.addOption("h", "host", true, "Name of host to connect to (defaults to localhost)");
        options.addOption("?", "help", false, "Display help information");
        options.addOption("u", "uuid", true, "UUID to use for this reader");
        options.addOption("d", "device", true, "Device to read from (e.g. /dev/ttyACM0)");
        
        CommandLineParser clp = new BasicParser();
        try {
            CommandLine cmd = clp.parse(options, args);
            
            // check if we need to display the help
            if(cmd.hasOption("?")) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp(DetectorGridClient.class.getSimpleName(), options);
            }
            else {
                // fetch the UUID from the options
                String uuid = cmd.getOptionValue("uuid", UUID.randomUUID().toString());
                
                // fech the device from the options
                String device = cmd.getOptionValue("device", "/dev/ttyACM0");
                
                // fetch the host from the options
                String host = cmd.getOptionValue("h", "localhost");
                // create class instance to start the logic
                new DetectorGridClient(host, uuid, device);

                // let the listening threads do their work...
                while(true) {
                    Thread.sleep(1000);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DetectorGridClient.class.getName()).log(Level.SEVERE, "Error while running DetectorGridClient", ex);
        }
    }
}
