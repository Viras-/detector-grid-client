/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum_wien.detectorgridclient.communication;

/**
 *
 * @author wkoller
 */
public interface Client {
    public abstract boolean init();
    
    public abstract boolean addListener(Listener listener);
    public abstract boolean removeListener(Listener listener);
}
