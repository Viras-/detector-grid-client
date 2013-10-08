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
package at.technikum_wien.detectorgridclient.communication.spread;

import at.technikum_wien.detectorgridclient.communication.Client;
import at.technikum_wien.detectorgridclient.communication.Listener;
import java.net.InetAddress;
import java.util.HashMap;
import spread.SpreadConnection;

/**
 *
 * @author wkoller
 */
public class SpreadClient implements Client {
    protected SpreadConnection spreadConnection = null;
    protected HashMap<Listener,SpreadListener> listeners = new HashMap<>();

    @Override
    public boolean init(String address) {
        try {
            spreadConnection = new SpreadConnection();
            spreadConnection.connect(InetAddress.getByName(address), 0, "private", true, false);
            
            return true;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    public boolean addListener(Listener listener) {
        SpreadListener spreadListener = new SpreadListener(listener);
        spreadConnection.add(spreadListener);
        
        listeners.put(listener, spreadListener);
        
        return true;
    }

    @Override
    public boolean removeListener(Listener listener) {
        SpreadListener spreadListener = listeners.remove(listener);
        if( spreadListener != null ) {
            spreadConnection.remove(spreadListener);
            return true;
        }
        
        return false;
    }
}
