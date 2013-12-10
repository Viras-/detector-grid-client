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

/**
 *
 * @author wkoller
 */
public interface CommunicationProtocol {
    /**
     * Message protocol separator
     */
    //public static final String MESSAGE_SEPARATOR = "\u001E";
    public static final String MESSAGE_SEPARATOR = ":";

    /**
     * Found a tag message
     */
    public static final String MESSAGE_FOUND = "foundTag";
    
    /**
     * Find a tag message
     */
    public static final String MESSAGE_FIND = "findTag";
}
