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
    protected String host = "";

    public DetectorGridClient(String hst, String uuid) throws Exception {
        host = hst;
        
        SpreadClient spreadClient = new SpreadClient();
        spreadClient.init(host);
        
        USBReader uSBReader = new USBReader(uuid);
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
                
                // fetch the host from the options
                String host = cmd.getOptionValue("h", "localhost");
                try {
                    // create class instance to start the logic
                    new DetectorGridClient(host, uuid);
                } catch (Exception ex) {
                    Logger.getLogger(DetectorGridClient.class.getName()).log(Level.SEVERE, "Unable to start main detector grid", ex);
                }

                // let the listening threads do their work...
                while(true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DetectorGridClient.class.getName()).log(Level.SEVERE, "Error while executing main loop", ex);
                    }
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(DetectorGridClient.class.getName()).log(Level.SEVERE, "Error while parsing command line parameters", ex);
        }
    }
}
