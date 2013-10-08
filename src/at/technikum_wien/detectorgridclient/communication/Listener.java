/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum_wien.detectorgridclient.communication;

import at.technikum_wien.detectorgridclient.TagInformation;

/**
 *
 * @author wkoller
 */
public interface Listener {
    public abstract TagInformation findTag(String tagCode);
    public abstract int getReaderId();
}
