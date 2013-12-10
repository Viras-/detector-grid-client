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
package at.technikum_wien.detectorgrid;

import at.technikum_wien.detectorgridclient.communication.Client;

/**
 *
 * @author wkoller
 */
public class TagInformation {
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
     *  Tick of reader when the tag was seen
     */
    public int seenTick;

    /**
     * Convert this tag information to a message string which can be sent to the server
     * @return 
     */
    public String toMessage() {
        // begin message with message_tag
        String messageContent = Client.MESSAGE_FOUND + Client.MESSAGE_SEPARATOR;
        // add the reader id
        messageContent += readerId + Client.MESSAGE_SEPARATOR;
        // add the tag code
        messageContent += tagCode + Client.MESSAGE_SEPARATOR;
        // add the distance
        messageContent += distance + Client.MESSAGE_SEPARATOR;
        // add the seen tick
        messageContent += seenTick;
        
        return messageContent;
    }
    
    public static TagInformation fromMessage(String[] messageComponents) {
        TagInformation tagInformation = new TagInformation();
        tagInformation.readerId = messageComponents[1];
        tagInformation.tagCode = messageComponents[2];
        tagInformation.distance = Integer.parseInt(messageComponents[3]);
        tagInformation.seenTick = Integer.parseInt(messageComponents[4]);
        
        return tagInformation;
    }
}
