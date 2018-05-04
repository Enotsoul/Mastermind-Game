/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mastermind.controls;

import mastermind.events.request.RequestEvent;
import mastermind.events.request.RequestEventListener;

/**
 *
 * @author Bram
 */
public class BPanel extends javax.swing.JPanel {
    public BPanel()
    {
        super();
    }

    // Create the listener list
    protected javax.swing.event.EventListenerList listE =
        new javax.swing.event.EventListenerList();

    public void addRequestListener(RequestEventListener listener) {
        listE.add(RequestEventListener.class, listener);
    }

    public void removeRequestListener(RequestEventListener listener) {
        listE.remove(RequestEventListener.class, listener);
    }

    protected void fireRequestEvent(RequestEvent evt) {
        Object[] listeners = listE.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==RequestEventListener.class) {
                ((RequestEventListener)listeners[i+1]).Requested(evt);
            }
        }
    }

}
