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
package at.technikum_wien.detectorgridclient.communication;

import at.technikum_wien.detectorgridclient.TagInformation;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author wkoller
 */
public abstract class Client {
    public abstract boolean init(String address);
    
    protected ArrayList<Listener> listeners = new ArrayList<>();
    
    public boolean addListener(Listener listener) {
        listeners.add(listener);
        
        return true;
    }
    
    public boolean removeListener(Listener listener) {
        listeners.remove(listener);
        
        return true;
    }

    /**
     * Handle an incoming message and trigger the action for it
     *
     * @param messageComponents
     * @return
     * @throws Exception
     */
    public void handleMessage(String[] messageComponents) throws Exception {
        // check if we have at least one message component
        if(messageComponents.length <= 0) {
            throw new Exception("Invalid message components passed to 'handleMessage'");
        }
        
        // determine action and trigger function for it
        switch (messageComponents[0]) {
            case "findTag":
                // check for tag-code to find in message
                if (messageComponents.length >= 2) {
                    Iterator<Listener> l_it = listeners.iterator();
                    while( l_it.hasNext() ) {
                        Listener currListener = l_it.next();
                        
                        // ask listener for tag information
                        TagInformation tagInformation = currListener.findTag(messageComponents[1]);
                        if( tagInformation != null ) {
                            this.foundTag(tagInformation);
                        }
                    }
                } else {
                    throw new Exception("Invalid Message: '" + messageComponents[0] + "' - missing payload!");
                }
            // all other cases are invalid messages
            default:
                throw new Exception("Unknown Message: '" + messageComponents[0] + "'");
        }
    }

    /**
     * communicate a found tag to the server over this client
     * @param tagInformation
     * @return 
     */
    public abstract boolean foundTag(TagInformation tagInformation);
}
