/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mastermind.events.request;

import java.util.EventListener;

/**
 *
 * @author Bram
 */
public interface RequestEventListener extends EventListener {
    public void Requested(RequestEvent evt);
}
