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

/**
 *
 * @author wkoller
 */
public class DetectorGridClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpreadClient spreadClient = new SpreadClient();
        spreadClient.init("localhost");
        
        USBReader uSBReader = new USBReader();
        spreadClient.addListener(uSBReader);

        try {
            spreadClient.wait();
        }
        catch(Exception e) {
            
        }
    }
}
