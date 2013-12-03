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

import at.technikum_wien.detectorgridclient.TagInformation;
import at.technikum_wien.detectorgridclient.communication.Client;
import at.technikum_wien.detectorgridclient.communication.Listener;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

/**
 *
 * @author wkoller
 */
public class SpreadClient extends Client {
    /**
     * Group name for incoming messages
     */
    public static final String LISTEN_GROUP_NAME = "detectorGridClient";
    /**
     * Group name for outgoing messages
     */
    public static final String PUBLISH_GROUP_NAME = "detectorGridServer";
    
    /**
     * Reference to spread connection
     */
    protected SpreadConnection spreadConnection = null;
    
    /**
     * Reference to listening group
     */
    protected SpreadGroup listenSpreadGroup = null;
    
    /**
     * List of all listeners in conjunction with their spread listener
     */
    protected HashMap<Listener, SpreadMessageListener> spreadListeners = new HashMap<>();

    /**
     * Init the client and open connection to spread daemon
     *
     * @param address
     * @return
     */
    @Override
    public boolean init(String address) {
        try {
            // open the connection to the spread daemon
            spreadConnection = new SpreadConnection();
            spreadConnection.connect(InetAddress.getByName(address), 0, "private", true, false);

            // join the default group for the detector grid application
            listenSpreadGroup = new SpreadGroup();
            listenSpreadGroup.join(spreadConnection, LISTEN_GROUP_NAME);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Listen to messages from spread
     *
     * @param listener
     * @return
     */
    @Override
    public boolean addListener(Listener listener) {
        super.addListener(listener);
        
        // add a new spreadlistener wrapper
        SpreadMessageListener spreadListener = new SpreadMessageListener(listener, this);
        spreadConnection.add(spreadListener);

        // keep reference to added listener
        spreadListeners.put(listener, spreadListener);

        return true;
    }

    /**
     * Remove a listener
     *
     * @param listener
     * @return
     */
    @Override
    public boolean removeListener(Listener listener) {
        super.removeListener(listener);
        
        // remove listener from spread and all references to the object
        SpreadMessageListener spreadListener = spreadListeners.remove(listener);
        if (spreadListener != null) {
            spreadConnection.remove(spreadListener);
            return true;
        }

        return false;
    }

    /**
     * Report a found tag to the server
     *
     * @param tagInformation
     * @return
     */
    @Override
    public boolean foundTag(TagInformation tagInformation) {
        // create message for reporting the tag to the server
        SpreadMessage spreadMessage = new SpreadMessage();
        spreadMessage.addGroup(PUBLISH_GROUP_NAME);
        try {
            spreadMessage.setData(tagInformation.toMessage().getBytes("UTF-8"));

            // try to send the message
            try {
                spreadConnection.multicast(spreadMessage);
            } catch (SpreadException ex) {
                Logger.getLogger(SpreadClient.class.getName()).log(Level.SEVERE, "Unable to send message to server", ex);
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SpreadClient.class.getName()).log(Level.SEVERE, "Can't encode message", ex);
        }

        return true;
    }
}
