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

import at.technikum_wien.detectorgridclient.communication.Listener;

/**
 *
 * @author wkoller
 */
public class TagInformation {
    /**
     * Tag for message to be sent to server
     */
    public static final String MESSAGE_TAG = "tagFound";
    
    /**
     * Id of reader who discovered this tag
     */
    public String readerId;
    
    /**
     * Id / Code of tag
     */
    public String tagCode;
    
    /**
     * Distance to tag from this reader
     */
    public int distance;

    /**
     * Convert this tag information to a message string which can be sent to the server
     * @return 
     */
    public String toMessage() {
        // begin message with message_tag
        String messageContent = MESSAGE_TAG + Listener.MESSAGE_SEPARATOR;
        // add the reader id
        messageContent += readerId + Listener.MESSAGE_SEPARATOR;
        // add the tag code
        messageContent += tagCode + Listener.MESSAGE_SEPARATOR;
        // add the distance
        messageContent += distance + Listener.MESSAGE_SEPARATOR;
        
        return messageContent;
    }
}
